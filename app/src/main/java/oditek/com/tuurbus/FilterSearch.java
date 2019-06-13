package oditek.com.tuurbus;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import net.igenius.customcheckbox.CustomCheckBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oditek.com.tuurbus.adapter.dataholder.FilterModel;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.util.ApiHelper;
import oditek.com.tuurbus.util.NetworkConnection;
import oditek.com.tuurbus.webservices.MyApplication;

public class FilterSearch extends AppCompatActivity {
    TextView tvApply, tvClear;
    CardView cardView;
    RecyclerView mRecyclerView, mRecyclerView1;
    RadioButton r1, r2, r3, r4, r5;
    NetworkConnection nw;
    ArrayList<FilterModel> radiolist ;
    ArrayList<FilterModel> radiolist1;
    ProgressDialog progressDialog;
    private int i = 0;
    FrameLayout frameLayout;
    private String apiKey = "", starCount = "", ttCheck = "", ftCheck = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_search);

        nw = new NetworkConnection(FilterSearch.this);
        progressDialog = new ProgressDialog(FilterSearch.this);
        apiKey = ApiClient.getDataFromKey(FilterSearch.this,"api_key");
        frameLayout = (FrameLayout) findViewById(R.id.container);
        tvApply = findViewById(R.id.tvApply);
        tvApply.setVisibility(View.VISIBLE);
        tvClear = findViewById(R.id.tvClear);
        tvClear.setVisibility(View.VISIBLE);

        r1 = findViewById(R.id.rbPopularity1);
        r2 = findViewById(R.id.rbPopularity2);
        r3 = findViewById(R.id.rbPopularity3);
        r4 = findViewById(R.id.rbPopularity4);
        r5 = findViewById(R.id.rbPopularity5);

        cardView = findViewById(R.id.card_view);
        mRecyclerView = findViewById(R.id.recyclerViewT);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView1 = findViewById(R.id.recyclerViewF);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        mRecyclerView1.setLayoutManager(linearLayoutManager1);

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r1.setChecked(false);
                r2.setChecked(false);
                r3.setChecked(false);
                r4.setChecked(false);
                r5.setChecked(false);
                new ApiHelper(FilterSearch.this, "filtersearch", getFilterSearchListener).execute();

            }
        });

        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nw.isConnectingToInternet()) {
                    syncSearchTourData();
                } else {
                    NoInternetDialog();
                }
            }
        });

        if (nw.isConnectingToInternet()) {
            new ApiHelper(this, "filtersearch", getFilterSearchListener).execute();
        } else {
            NoInternetDialog();
        }
    }

    private ApiHelper.TaskDelegate getFilterSearchListener = new ApiHelper.TaskDelegate() {
        @Override
        public void onTaskFisnishGettingData(Object result) {
            try {
                if (result != null) {
                    radiolist = new ArrayList<FilterModel>();
                    radiolist1 = new ArrayList<FilterModel>();
                    JSONObject json = (JSONObject) result;
                    System.out.println("json----" + json);

                    JSONObject response_ = json.getJSONObject("response");
                    JSONObject error_ = json.getJSONObject("error");
                    String status_ = error_.getString("status");
                    String msg_ = error_.getString("msg");

                    if ("false".equalsIgnoreCase(status_)) {

                        JSONArray module_type_ = response_.getJSONArray("module_type");
                        for (int i = 0; i < module_type_.length(); i++) {
                            JSONObject responseObj = module_type_.getJSONObject(i);

                            String id = responseObj.getString("id");
                            String name = responseObj.getString("name");

                            FilterModel obj = new FilterModel(name,id);
                            radiolist.add(obj);
                        }

                        FilterTourAdapter filterModel = new FilterTourAdapter(FilterSearch.this, radiolist, "ModuleType");
                        mRecyclerView.setAdapter(filterModel);

                    } else {

                    }

                    if ("false".equalsIgnoreCase(status_)) {

                        JSONArray friendly_type_ = response_.getJSONArray("friendly_type");
                        for (int i = 0; i < friendly_type_.length(); i++) {
                            JSONObject responseObj = friendly_type_.getJSONObject(i);

                            String id = responseObj.getString("id");
                            String name = responseObj.getString("name");

                            FilterModel obj1 = new FilterModel(name,id);
                            radiolist1.add(obj1);

                        }

                        FilterTourAdapter filterModel = new FilterTourAdapter(FilterSearch.this, radiolist1, "FriendlyType");
                        mRecyclerView1.setAdapter(filterModel);

                    } else {

                    }
                }


            } catch (ClassCastException e) {

            } catch (JSONException e) {

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    };

    public void onRadioButtonClicked(View view) {
        boolean checked = true;

        switch (view.getId()) {
            case R.id.rbPopularity1:
                if (checked)
                    starCount = "1";
                    r1.setChecked(true);
                r2.setChecked(false);
                r3.setChecked(false);
                r4.setChecked(false);
                r5.setChecked(false);
                break;
            case R.id.rbPopularity2:
                if (checked)
                    starCount = "2";
                    r1.setChecked(false);
                r2.setChecked(true);
                r3.setChecked(false);
                r4.setChecked(false);
                r5.setChecked(false);
                break;
            case R.id.rbPopularity3:
                if (checked)
                    starCount = "3";
                    r1.setChecked(false);
                r2.setChecked(false);
                r3.setChecked(true);
                r4.setChecked(false);
                r5.setChecked(false);
                break;
            case R.id.rbPopularity4:
                if (checked)
                    starCount = "4";
                    r1.setChecked(false);
                r2.setChecked(false);
                r3.setChecked(false);
                r4.setChecked(true);
                r5.setChecked(false);
                break;
            case R.id.rbPopularity5:
                if (checked)
                    starCount = "5";
                    r1.setChecked(false);
                r2.setChecked(false);
                r3.setChecked(false);
                r4.setChecked(false);
                r5.setChecked(true);
                break;
        }
    }

    public class FilterTourAdapter extends RecyclerView.Adapter<FilterTourAdapter.ViewHolder> {
        private Context mContext;
        List<FilterModel> radiolist;
        private String typeData = "";

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_filter_tourtype_rowlayout, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }


        public FilterTourAdapter(Context mContext, List<FilterModel> radiolist, String type) {
            this.mContext = mContext;
            this.radiolist = radiolist;
            this.typeData = type;

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final FilterModel filterModel = radiolist.get(position);
            final String name = filterModel.getName();
            holder.name.setText(name);
            holder.tourTypeChk.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                    Log.d("CustomCheckBox", filterModel.getId());
                    if(typeData.equalsIgnoreCase("ModuleType")){
                        ttCheck = filterModel.getId();
                    }else{
                        ftCheck = filterModel.getId();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return radiolist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView name;
            public CustomCheckBox tourTypeChk;
            public CardView mCardView;

            public ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.tvTour);
                tourTypeChk = itemView.findViewById(R.id.checkBox);
                mCardView = (CardView) itemView.findViewById(R.id.card_view);
            }
        }
    }

    public void NoInternetDialog() {
        new AwesomeErrorDialog(this)
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

    private void syncSearchTourData(){
        progressDialog.show();
        String tag_json_req = "sync_search_data";
        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.main_url) + getResources()
                        .getString(R.string.tours_url) + "searchtour/?" + "appKey=" + apiKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            Log.d("tour response", response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject errorObj = jsonObject.getJSONObject("error");
                            String status = errorObj.getString("status");
                            if(status.equalsIgnoreCase("false")){
                                JSONArray responseArr = jsonObject.getJSONArray("response");
                                if (responseArr.length()>0){
                                    finish();
                                    frameLayout.setVisibility(View.VISIBLE);
                                    Fragment fragment = PackageToursFragment.newInstance(responseArr,"FilterSearch");
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.container, fragment)
                                            .addToBackStack(null)
                                            .commit();
                                }
                            }else{
                                frameLayout.setVisibility(View.GONE);
                                new AwesomeErrorDialog(FilterSearch.this)
                                        .setTitle("Attention!")
                                        .setMessage(errorObj.getString("msg"))
                                        .setColoredCircle(R.color.white)
                                        .setDialogIconOnly(R.drawable.main_logo)
                                        .setCancelable(false)
                                        .setButtonBackgroundColor(R.color.colorPrimaryDark)
                                        .setButtonText(getString(R.string.dialog_ok_button))
                                        .setErrorButtonClick(new Closure() {
                                            @Override
                                            public void exec() {

                                            }
                                        })
                                        .show();
                            }

                            String statusCode = errorObj.getString("status_code");
                            if(statusCode.equalsIgnoreCase("-1")){
                                //call the appkey API
                                Toast.makeText(FilterSearch.this, "App ApiKey has Changed",Toast.LENGTH_SHORT).show();
                                //getAppApiKey();
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
                        syncSearchTourData();

                    } else {
                        Toast.makeText(FilterSearch.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(FilterSearch.this, error.getMessage(),
                            Toast.LENGTH_LONG).show();
            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("location", "");
                params.put("tour_type", "");
                params.put("dep_date", "");
                params.put("offset", "1");
                params.put("ttype", ttCheck);
                params.put("ftype", ftCheck);
                params.put("stars", starCount);
                if(ApiClient.getDataFromKey(FilterSearch.this,"user_id").equalsIgnoreCase("")||ApiClient.getDataFromKey(FilterSearch.this,"user_id").equalsIgnoreCase("0")){
                    params.put("user_id", "");
                }else{
                    params.put("user_id", ApiClient.getDataFromKey(FilterSearch.this,"user_id"));
                }
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
}
