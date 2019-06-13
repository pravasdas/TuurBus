package oditek.com.tuurbus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import oditek.com.tuurbus.navigationdrawer.HomeSlidingActivity;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.util.NetworkConnection;
import oditek.com.tuurbus.webservices.MyApplication;
import oditek.com.tuurbus.webservices.VolleySingleton;

public class CrazyToursDetails extends AppCompatActivity {
    ImageView BackButton,ivTop;
    TextView tvName,tvPrice,tvDetails,tvCall;
    ProgressDialog progressDialog;
    private int i = 0;
    private String apiKey = "", tourId = "";
    private ImageLoader imageLoader;
    NetworkConnection nw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crazy_tours_details);

        nw = new NetworkConnection(CrazyToursDetails.this);
        imageLoader = VolleySingleton.getInstance().getImageLoader();
        progressDialog = new ProgressDialog(CrazyToursDetails.this);
        progressDialog.setMessage("Loading");
        apiKey = ApiClient.getDataFromKey(CrazyToursDetails.this,"api_key");

        ivTop=findViewById(R.id.ivTop);
        tvName=findViewById(R.id.tvName);
        tvPrice=findViewById(R.id.tvPrice);
        tvDetails=findViewById(R.id.tvDetails);
        tvCall=findViewById(R.id.tvCall);
        BackButton=findViewById(R.id.ivBack);

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Bundle bundle = getIntent().getExtras();
        tourId = bundle.getString("tourID_");

        if (nw.isConnectingToInternet()) {
            syncCrazyTourDetails();
        } else {
            NoInternetDialog();
        }

    }

    private void syncCrazyTourDetails(){
        progressDialog.show();
        String tag_json_req = "sync_tour_details";
        StringRequest data = new StringRequest(Request.Method.GET,
                getResources().getString(R.string.main_url) + getResources()
                        .getString(R.string.crazy_tour_list) + "offerdetails?" + "appKey=" + apiKey + "&id=" + tourId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            Log.d("crazy response", response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject responseObj = jsonObject.getJSONObject("response");
                            JSONObject errorObj = jsonObject.getJSONObject("error");

                            if(responseObj.length() != 0){
                                String title = responseObj.getString("title");
                                String price = responseObj.getString("price");
                                String mobile = responseObj.getString("phone");
                                String desc = responseObj.getString("desc");
                                String image = responseObj.getString("thumbnail");

                                tvName.setText(title);
                                tvPrice.setText(price);
                                tvCall.setText(mobile);
                                tvDetails.setText(desc);
                                imageLoader.get(image, new ImageLoader.ImageListener() {
                                    @Override
                                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                        ivTop.setImageBitmap(response.getBitmap());
                                    }
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                });
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
                        syncCrazyTourDetails();

                    } else {
                        Toast.makeText(CrazyToursDetails.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(CrazyToursDetails.this, error.getMessage(),
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
                                ApiClient.saveDataWithKeyAndValue(CrazyToursDetails.this,"api_key", key);
                                startActivity(new Intent(CrazyToursDetails.this,HomeSlidingActivity.class));
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
                        Toast.makeText(CrazyToursDetails.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        //progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(CrazyToursDetails.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
        new AwesomeErrorDialog(CrazyToursDetails.this)
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
