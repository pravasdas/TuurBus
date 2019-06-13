package oditek.com.tuurbus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import oditek.com.tuurbus.navigationdrawer.HomeSlidingActivity;
import oditek.com.tuurbus.util.AllStaticVariables;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.util.ApiHelper;
import oditek.com.tuurbus.util.NetworkConnection;
import oditek.com.tuurbus.webservices.VolleySingleton;

public class Login extends AppCompatActivity {
    Button loginbutton;
    TextView textView,tv_otherLogin;
    EditText etEmailID,etPassword;
    private TextInputLayout etPasswordLayout;
    private String emailval_,passwordVal_, apiKey = "";
    ProgressDialog progressDialog;
    private int i = 0;
    NetworkConnection nw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nw = new NetworkConnection(Login.this);
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Loading");
        loginbutton = findViewById(R.id.loginButton);
        textView=findViewById(R.id.tv_signup);
        etEmailID = findViewById(R.id.et1);
        etPassword = findViewById(R.id.et2);
        etPasswordLayout = (TextInputLayout) findViewById(R.id.etPasswordLayout);
        apiKey = ApiClient.getDataFromKey(Login.this,"api_key");
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent=new Intent(Login.this,HomeSlidingActivity.class);
//                startActivity(intent);

                emailval_ = etEmailID.getText().toString().trim();
                passwordVal_ = etPassword.getText().toString().trim();

                if (etEmailID.getText().toString().trim().length() == 0) {
                    etEmailID.setError(getResources().getString(R.string.error_email));

                } else if (!isValidEmail(emailval_)) {
                    etEmailID.setError(getResources().getString(R.string.error_email2));
                } else if (etPassword.getText().toString().trim().length() == 0) {
                    etPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                    etPassword.setError(getResources().getString(R.string.error_password));

                } else if (!isValidPassword(etPassword.getText().toString().trim())) {
                    etPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                    etPassword.setError(getResources().getString(R.string.err_msg_password_strength));
                } else {
                    String loginemailID_ = etEmailID.getText().toString();
                    String loginpassword_ = etPassword.getText().toString();
                    if (nw.isConnectingToInternet()) {
                        syncSignInData(loginemailID_,loginpassword_);
                    } else {
                        NoInternetDialog();
                    }

                }
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
    private void syncSignInData(final String email, final String password){
        progressDialog.show();
        String tag_json_req = "sync_signin";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.main_url) + getResources()
                        .getString(R.string.otp_url) + "check/?" + "appKey=" + apiKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            progressDialog.dismiss();
                            Log.d("otp response is ", response);
                            JSONObject jsonObject = new JSONObject(response);
                            String responseText = jsonObject.getString("response");
                            JSONObject errorObj = jsonObject.getJSONObject("error");
                            String msg_ = errorObj.getString("msg");
                            if(responseText.equalsIgnoreCase("true")){
                                JSONObject userInfoObj = jsonObject.getJSONObject("userInfo");
                                String userId = userInfoObj.getString("id");
                                ApiClient.saveDataWithKeyAndValue(Login.this,"user_id", userId);
                                startActivity(new Intent(Login.this,HomeSlidingActivity.class));
                            }else{
                                new AwesomeErrorDialog(Login.this)
                                        .setTitle("Error")
                                        .setMessage(msg_)
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
                        syncSignInData(email,password);
                    } else {
                        Toast.makeText(Login.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);


                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance().getRequestQueue().add(data).addMarker(tag_json_req);
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
                                ApiClient.saveDataWithKeyAndValue(Login.this,"api_key", key);
                                startActivity(new Intent(Login.this,HomeSlidingActivity.class));
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
                        Toast.makeText(Login.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        //progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
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

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=\\S+$).{8,20}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public void NoInternetDialog() {
        new AwesomeErrorDialog(Login.this)
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