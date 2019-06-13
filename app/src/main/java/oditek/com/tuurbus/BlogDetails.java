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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import oditek.com.tuurbus.navigationdrawer.HomeSlidingActivity;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.util.NetworkConnection;
import oditek.com.tuurbus.webservices.MyApplication;
import oditek.com.tuurbus.webservices.VolleySingleton;

public class BlogDetails extends AppCompatActivity {
    ImageView ivImage;
    TextView tvName,tvDate,tvDetails;
    String blogId = "";
    ProgressDialog progressDialog;
    private int i = 0;
    private String apiKey = "";
    private ImageLoader imageLoader;
    NetworkConnection nw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);

        nw = new NetworkConnection(BlogDetails.this);
        progressDialog = new ProgressDialog(BlogDetails.this);
        imageLoader = VolleySingleton.getInstance().getImageLoader();
        apiKey = ApiClient.getDataFromKey(BlogDetails.this,"api_key");
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivImage=findViewById(R.id.ivImage);
        tvName=findViewById(R.id.tvName);
        tvDate=findViewById(R.id.tvDate);
        tvDetails=findViewById(R.id.tvDetails);

        Bundle bundle = getIntent().getExtras();
        blogId = bundle.getString("blogId_");
        if (nw.isConnectingToInternet()) {
            syncCrazyTourDetails();
        }else{
            NoInternetDialog();
        }

    }

    private void syncCrazyTourDetails(){
        progressDialog.show();
        String tag_json_req = "sync_blog_details";
        StringRequest data = new StringRequest(Request.Method.GET,
                getResources().getString(R.string.main_url) + getResources()
                        .getString(R.string.blog_list) + "detail?" + "appKey=" + apiKey + "&id=" + blogId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            Log.d("blog response", response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject responseObj = jsonObject.getJSONObject("response");
                            JSONObject errorObj = jsonObject.getJSONObject("error");

                            if(responseObj.length() != 0){
                                String title = responseObj.getString("title");
                                String date = responseObj.getString("date");
                                String desc = responseObj.getString("description");
                                String image = responseObj.getString("thumbnail");

                                tvName.setText(title);
                                tvDate.setText(date);
                                tvDetails.setText(desc);
                                imageLoader.get(image, new ImageLoader.ImageListener() {
                                    @Override
                                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                        ivImage.setImageBitmap(response.getBitmap());
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
                        Toast.makeText(BlogDetails.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(BlogDetails.this, error.getMessage(),
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
                                ApiClient.saveDataWithKeyAndValue(BlogDetails.this,"api_key", key);
                                startActivity(new Intent(BlogDetails.this,HomeSlidingActivity.class));
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
                        Toast.makeText(BlogDetails.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        //progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(BlogDetails.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
}
