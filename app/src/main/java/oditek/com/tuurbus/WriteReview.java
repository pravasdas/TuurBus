package oditek.com.tuurbus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import oditek.com.tuurbus.webservices.VolleySingleton;

public class WriteReview extends AppCompatActivity {

    private String tourId = "", appModule = "", apiKey = "";
    private EditText name, email, review;
    private Button submit;
    private int i = 0;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        progressDialog = new ProgressDialog(WriteReview.this);
        apiKey = ApiClient.getDataFromKey(WriteReview.this,"api_key");
        name = (EditText) findViewById(R.id.etName);
        email = (EditText) findViewById(R.id.etEmail);
        review = (EditText) findViewById(R.id.etReview);
        submit = (Button) findViewById(R.id.btnSubmit);
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                onBackPressed();
            }
        });
        tourId = getIntent().getStringExtra("TourId_");
        appModule = getIntent().getStringExtra("AppModule_");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().length() == 0) {
                    name.setError("Name is required");
                } else if (email.getText().toString().trim().length() == 0) {
                    email.setError(getResources().getString(R.string.error_email));
                } else if (review.getText().toString().trim().length() == 0) {
                    review.setError("Review is required");
                } else if (!isValidEmail(email.getText().toString().trim())) {
                    email.setError(getResources().getString(R.string.error_email2));
                } else{
                    postReview();
                }
            }
        });
    }

    private void postReview(){
        progressDialog.show();
        String tag_json_req = "sync_review";
        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.main_url) + getResources()
                        .getString(R.string.tours_url) + "tourreview/?" + "appKey=" + apiKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            //progressDialog.dismiss();
                            Log.d("review response is ", response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject errorObj = jsonObject.getJSONObject("error");
                            String status = errorObj.getString("status");
                            if(status.equalsIgnoreCase("false")){
                                new AwesomeErrorDialog(WriteReview.this)
                                        .setTitle("Success")
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
                            }else{
                                new AwesomeErrorDialog(WriteReview.this)
                                        .setTitle("Error")
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
                        postReview();
                    } else {
                        Toast.makeText(WriteReview.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(WriteReview.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String emailData = email.getText().toString().trim();
                String nameData = name.getText().toString().trim();
                String reviewData = review.getText().toString().trim();

                Map<String, String> params = new HashMap<>();
                params.put("email", emailData);
                params.put("fullname",nameData);
                params.put("reviews_comments",reviewData);
                params.put("reviewmodule",appModule);
                params.put("reviewfor",tourId);
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
}
