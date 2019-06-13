package oditek.com.tuurbus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import oditek.com.tuurbus.navigationdrawer.HomeSlidingActivity;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.util.NetworkConnection;
import oditek.com.tuurbus.webservices.VolleySingleton;

public class OtpPage extends AppCompatActivity {

    ImageView iv_BackpressOtp;
    private Button confirm;
    private EditText textOtp;
    private int i = 0;
    private ProgressDialog progressDialog;
    private String mobileNo = "",coming_from = "";
    NetworkConnection nw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_page);

        nw = new NetworkConnection(OtpPage.this);
        progressDialog = new ProgressDialog(OtpPage.this);
        iv_BackpressOtp = findViewById(R.id.iv_BackpressOtp);
        confirm = findViewById(R.id.button1);
        textOtp = findViewById(R.id.et1);

        coming_from = getIntent().getStringExtra("comingFrom");
        System.out.println(coming_from);
        if(coming_from.equals("SignUp")){
            mobileNo = ApiClient.getDataFromKey(OtpPage.this, "mobile");
        }else if(coming_from.equals("MainLoginFragment")){
            mobileNo = getIntent().getStringExtra("MobileNo");
        }else{
            mobileNo = "";
        }


        iv_BackpressOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences =getSharedPreferences("turrbus", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                onBackPressed();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(textOtp.getText().toString().trim().length() == 0){
                    textOtp.setError("Otp is required");
                }else{
                    if (nw.isConnectingToInternet()) {
                        syncOtpDetails(textOtp.getText().toString().trim());
                    } else {
                        NoInternetDialog();
                    }

                }
            }
        });
    }

    private void syncOtpDetails(final String otp) {
        progressDialog.show();
        String tag_json_req = "sync_otp";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.main_url) + getResources()
                        .getString(R.string.otp_url) + "confirmOTP/?" + "appKey=tuurbusoditek",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            progressDialog.dismiss();
                            Log.d("otp response is ", response);
                            JSONObject jsonObject = new JSONObject(response);
                            String apiResponse = jsonObject.getString("response");
                            JSONObject errorObj = jsonObject.getJSONObject("error");
                            if(errorObj.getString("status").equalsIgnoreCase("false")){
                                String msg = errorObj.getString("msg");
                                JSONObject dataObj = errorObj.getJSONObject("data");
                                String userId = dataObj.getString("user_id");
                                ApiClient.saveDataWithKeyAndValue(OtpPage.this, "user_id", userId);
                                Toast.makeText(OtpPage.this,msg,Toast.LENGTH_LONG).show();
                                Intent mIntent=new Intent(OtpPage.this, HomeSlidingActivity.class);
                                mIntent.putExtra("fragmentPosition",0);
                                startActivity(mIntent);
                            }else{
                                String msg = errorObj.getString("msg");
                                Toast.makeText(OtpPage.this,msg,Toast.LENGTH_LONG).show();
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
                        syncOtpDetails(otp);
                    } else {
                        Toast.makeText(OtpPage.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(OtpPage.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("phone", mobileNo);
                params.put("otp", otp);


                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance().getRequestQueue().add(data).addMarker(tag_json_req);
    }

    public void NoInternetDialog() {
        new AwesomeErrorDialog(OtpPage.this)
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
