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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oditek.com.tuurbus.adapter.InclusionsAdapter;
import oditek.com.tuurbus.adapter.PackageAdapter;
import oditek.com.tuurbus.adapter.dataholder.PackageModel;
import oditek.com.tuurbus.adapter.dataholder.PackageVehicleModel;
import oditek.com.tuurbus.navigationdrawer.HomeSlidingActivity;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.webservices.MyApplication;
import oditek.com.tuurbus.webservices.VolleySingleton;

public class PackageToursFragment extends Fragment {

    CardView cardView, cardView2;
    ImageView ivFilter;
    TextView noData;
    RecyclerView mRecyclerView;
    List<PackageModel> list = new ArrayList<>();
    List<PackageVehicleModel> list2 = new ArrayList<>();
    ProgressDialog progressDialog;
    private int i = 0;
    private String apiKey = "", homeStringArrData = "", fromData = "default";
    private JSONArray homeSearchArr;

    public static PackageToursFragment newInstance(JSONArray jsonArray, String from) {
        PackageToursFragment f = new PackageToursFragment();
        Bundle args = new Bundle();
        args.putString("ArrayString", jsonArray.toString());
        args.putString("ComingFrom", from);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_package_tours, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading");
        apiKey = ApiClient.getDataFromKey(getActivity(),"api_key");
        cardView = (CardView) view.findViewById(R.id.card_view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noData = (TextView) view.findViewById(R.id.tv_noData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);


        Bundle args = getArguments();
        if (args != null) {
            fromData = args.getString("ComingFrom");
            homeStringArrData = args.getString("ArrayString");
            System.out.println(homeStringArrData);
        }

        if(fromData.equalsIgnoreCase("HomeSearch")){
            try {
                homeSearchArr = new JSONArray(homeStringArrData);
                PackageAdapter adapter = new PackageAdapter(getActivity(),getPackageTourData(homeSearchArr));
                mRecyclerView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            syncPackageTourData();
        }
        //This code is for Filter Search
        ivFilter = view.findViewById(R.id.ivFilter);
        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FilterSearch.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void syncPackageTourData(){
        progressDialog.show();
        String tag_json_req = "sync_package_data";
        StringRequest data = new StringRequest(Request.Method.GET,
                getResources().getString(R.string.main_url) + getResources()
                        .getString(R.string.tours_url) + "list?" + "appKey=" + apiKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            Log.d("tour response", response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray responseArr = jsonObject.getJSONArray("response");
                            JSONObject errorObj = jsonObject.getJSONObject("error");

                            if(responseArr.length() > 0){
                                noData.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.VISIBLE);
                                PackageAdapter adapter = new PackageAdapter(getActivity(),getPackageTourData(responseArr));
                                mRecyclerView.setAdapter(adapter);
                            }else{
                                noData.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.GONE);
                            }

                            String statusCode = errorObj.getString("status_code");
                            if(statusCode.equalsIgnoreCase("-1")){
                                //call the appkey API
                                Toast.makeText(getActivity(), "App ApiKey has Changed",Toast.LENGTH_SHORT).show();
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
                        syncPackageTourData();

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

    private List<PackageModel> getPackageTourData(JSONArray jsonArray) {
        List<PackageModel> packageData = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    PackageModel rowData = new PackageModel();
                    JSONObject ltData = jsonArray.getJSONObject(i);

                    rowData.tourId = ltData.getString("id");
                    rowData.name = ltData.getString("title");
                    rowData.location = ltData.getString("location");
                    rowData.imageUrl = ltData.getString("thumbnail");
                    rowData.starsCount = ltData.getString("starsCount");
                    rowData.tourtype = ltData.getString("tourType");
                    rowData.days = ltData.getString("tourDays");
                    rowData.nights = ltData.getString("tourNights");
                    rowData.speciality = ltData.getString("estr");
                    rowData.descp = ltData.getString("desc");
                    rowData.latitude = ltData.getString("latitude");
                    rowData.longitude = ltData.getString("longitude");
                    rowData.currCode = ltData.getString("currCode");
                    rowData.currSymbol = ltData.getString("currSymbol");
                    rowData.appModule = ltData.getString("appModule");
                    rowData.priceArr = ltData.getJSONArray("vprice");

                    packageData.add(rowData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return packageData;
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
}
