package oditek.com.tuurbus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oditek.com.tuurbus.adapter.BlogsAdapter;
import oditek.com.tuurbus.adapter.dataholder.BlogModel;
import oditek.com.tuurbus.navigationdrawer.HomeSlidingActivity;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.util.ApiHelper;
import oditek.com.tuurbus.util.NetworkConnection;
import oditek.com.tuurbus.webservices.MyApplication;
import oditek.com.tuurbus.webservices.VolleySingleton;

public class BlogsFragment extends Fragment {

    CardView cardView;
    RecyclerView mRecyclerView;
    List<BlogModel> list = new ArrayList<>();
    private ProgressDialog progressDialog;
    private int i = 0;
    private String apiKey = "";
    TextView noData;
    NetworkConnection nw;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_blog, container, false);

        nw = new NetworkConnection(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading");
        apiKey = ApiClient.getDataFromKey(getActivity(),"api_key");
        cardView = (CardView)view.findViewById(R.id.card_view);
        mRecyclerView= (RecyclerView)view.findViewById(R.id.recyclerView);
        noData= view.findViewById(R.id.tv_noData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (nw.isConnectingToInternet()) {
            syncBlogListData();
        } else {
            NoInternetDialog();
        }
        return view;
    }

    private void syncBlogListData(){
        progressDialog.show();
        String tag_json_req = "sync_tour_data";
        StringRequest data = new StringRequest(Request.Method.GET,
                getResources().getString(R.string.main_url) + getResources()
                        .getString(R.string.blog_list) + "list/?" + "appKey=" + apiKey + "&offset=1&threshold=20",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            Log.d("crazy response", response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject responseObj = jsonObject.getJSONObject("response");
                            JSONObject errorObj = jsonObject.getJSONObject("error");
                            JSONArray postArr = responseObj.getJSONArray("posts");

                            if(postArr.length()>0){
                                noData.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.VISIBLE);
                                BlogsAdapter adapter = new BlogsAdapter(getActivity(),getBlogData(postArr));
                                mRecyclerView.setAdapter(adapter);
                            }

                            String statusCode = errorObj.getString("status_code");
                            if(statusCode.equalsIgnoreCase("-1")){
                                //call the appkey API
                                getAppApiKey();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    if (i < 3) {
                        Log.e("Retry due to error ", "for time : " + i);
                        i++;
                        syncBlogListData();

                    } else {
                        Toast.makeText(getActivity(), "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(getActivity(), error.getMessage(),
                            Toast.LENGTH_LONG).show();
            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                Log.d("params are :", "" + params);
                return params;
            }
        };

        data.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                0,
                0));
        Volley.newRequestQueue(MyApplication.getAppContext()).add(data).addMarker(tag_json_req);
    }

    private List<BlogModel> getBlogData(JSONArray jsonArray) {
        List<BlogModel> blogData = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    BlogModel rowData = new BlogModel();
                    JSONObject ltData = jsonArray.getJSONObject(i);

                    rowData.blogId = ltData.getString("id");
                    rowData.name = ltData.getString("title");
                    rowData.details = ltData.getString("description");
                    rowData.date = ltData.getString("date");
                    rowData.imageUrl = ltData.getString("thumbnail");

                    blogData.add(rowData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return blogData;
    }

    private void getAppApiKey(){
        String tag_json_req = "sync_key";

        StringRequest data = new StringRequest(Request.Method.GET,
                getResources().getString(R.string.app_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //progressDialog.dismiss();
                            Log.d("key response is ", response);
                            JSONObject jsonObject = new JSONObject(response);
                            String key = jsonObject.getString("appkey");
                            if(key.equalsIgnoreCase("")){
                                System.out.println("No key Found");
                            }else{
                                ApiClient.saveDataWithKeyAndValue(getActivity(),"api_key", key);
                                startActivity(new Intent(getActivity(),HomeSlidingActivity.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    if (i < 3) {
                        Log.e("Retry due to error ", "for time : " + i);
                        i++;
                        getAppApiKey();
                    } else {
                        Toast.makeText(getActivity(), "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        //progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance().getRequestQueue().add(data).addMarker(tag_json_req);
    }

    public void NoInternetDialog() {
        new AwesomeErrorDialog(getActivity())
                .setTitle("No Internet!")
                .setMessage("Make sure that WI-FI or Mobile data is turned on, then try again...")
                .setCancelable(false)
                .setColoredCircle(R.color.white)
                .setDialogIconOnly(R.drawable.main_logo)
                .setCancelable(true).setButtonText(getString(R.string.dialog_ok_button))
                .setButtonBackgroundColor(R.color.colorPrimaryDark)
                .setButtonText(getString(R.string.dialog_ok_button))
                .setErrorButtonClick(new Closure() {
                    @Override
                    public void exec() {
                    }
                })
                .show();
    }


}
