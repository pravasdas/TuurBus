package oditek.com.tuurbus;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import oditek.com.tuurbus.adapter.BookingSummaryAdapter;
import oditek.com.tuurbus.adapter.dataholder.BookingSummaryModel;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.util.NetworkConnection;
import oditek.com.tuurbus.webservices.VolleySingleton;

public class BookingSummary extends AppCompatActivity {
    CardView cardView;
    RecyclerView mRecyclerView;
    private String tourId_txt = "", isPromo_txt = "", tAdult_txt = "", tChild_txt = "", tInfant_txt = "",
                   vehicleType_txt = "", vehiclePrice_txt = "", vehicleId_txt = "", depDate_txt = "", apiKey = "",
                    aCount = "", cCount = "", iCount = "";
    ProgressDialog progressDialog;
    private int i = 0;
    ImageLoader imageLoader;
    private ImageView tourImg;
    private SimpleRatingBar ratingBar;
    NetworkConnection nw;
    private TextView tourName, location, days, nights, depDate, vehicleType, perPrice, adultPrice, childPrice,
                     infantPrice, adultCount, childCount, infantCount, subTotal, gstCount, gstPrice, totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_summary);

        nw = new NetworkConnection(BookingSummary.this);
        apiKey = ApiClient.getDataFromKey(BookingSummary.this,"api_key");
        progressDialog = new ProgressDialog(BookingSummary.this);
        imageLoader = VolleySingleton.getInstance().getImageLoader();
        tourImg = findViewById(R.id.ivTop);
        tourName = findViewById(R.id.tvName);
        location = findViewById(R.id.tvLocation);
        days = findViewById(R.id.tvDay);
        nights = findViewById(R.id.tvNight);
        depDate = findViewById(R.id.tvDepartureDate);
        vehicleType = findViewById(R.id.tvVehicleType);
        perPrice = findViewById(R.id.tvPrice);
        adultPrice = findViewById(R.id.tvAdults);
        childPrice = findViewById(R.id.tvChild);
        infantPrice = findViewById(R.id.tvInfants);
        subTotal = findViewById(R.id.tvSubtotal);
        adultCount = findViewById(R.id.Adults);
        childCount = findViewById(R.id.Child);
        infantCount = findViewById(R.id.Infants);
        gstCount = findViewById(R.id.GST);
        gstPrice = findViewById(R.id.tvGST);
        totalPrice = findViewById(R.id.tvTotal);
        ratingBar = findViewById(R.id.ratingbar);
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cardView = findViewById(R.id.card_view);
        mRecyclerView= findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        tourId_txt = getIntent().getStringExtra("TourId_");
        isPromo_txt = getIntent().getStringExtra("IsPromo_");
        tAdult_txt = getIntent().getStringExtra("TotalAdult_");
        tChild_txt = getIntent().getStringExtra("TotalChild_");
        tInfant_txt = getIntent().getStringExtra("TotalInfant_");
        vehicleType_txt = getIntent().getStringExtra("VehicleType_");
        vehiclePrice_txt = getIntent().getStringExtra("vehiclePrice_");
        vehicleId_txt = getIntent().getStringExtra("VehicleId_");
        depDate_txt = getIntent().getStringExtra("DepartureDate_");

        if (nw.isConnectingToInternet()) {
            syncBookingSummeryDetails();
        } else {
            NoInternetDialog();
        }
    }

    private void syncBookingSummeryDetails(){
        progressDialog.show();
        String tag_json_req = "sync_promo";
        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.main_url) + getResources()
                        .getString(R.string.tours_url) + "booking/?" + "appKey=" + apiKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            Log.d("promo response is ", response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject errorObj = jsonObject.getJSONObject("error");
                            String status = errorObj.getString("status");
                            if(status.equalsIgnoreCase("false")){
                                JSONObject responseObj = jsonObject.getJSONObject("response");
                                JSONObject tourObj = responseObj.getJSONObject("tour");
                                imageLoader.get(tourObj.getString("thumbnail"), new ImageLoader.ImageListener() {
                                    @Override
                                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                        tourImg.setImageBitmap(response.getBitmap());
                                    }
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                });
                                tourName.setText(tourObj.getString("title"));
                                location.setText(tourObj.getString("location"));
                                days.setText(tourObj.getString("tourDays"));
                                nights.setText(tourObj.getString("tourNights"));
                                ratingBar.setRating(Float.valueOf(tourObj.getString("starsCount")));
                                depDate.setText(tourObj.getString("date"));
                                vehicleType.setText(tourObj.getString("vType"));
                                String perPricetxt = tourObj.getString("currCode") + " " + tourObj.getString("currSymbol") + " " + tourObj.getString("vprice");
                                perPrice.setText(perPricetxt);
                                String adultPricetxt = tourObj.getString("currCode") + " " + tourObj.getString("currSymbol") + " " + tourObj.getString("adultprice");
                                adultPrice.setText(adultPricetxt);
                                String childPricetxt = tourObj.getString("currCode") + " " + tourObj.getString("currSymbol") + " " + tourObj.getString("childprice");
                                childPrice.setText(childPricetxt);
                                String infantPricetxt = tourObj.getString("currCode") + " " + tourObj.getString("currSymbol") + " " + tourObj.getString("infantprice");
                                infantPrice.setText(infantPricetxt);
                                if(tourObj.getString("adults").equalsIgnoreCase("")){
                                    aCount = "0";
                                }else{
                                    aCount = tourObj.getString("adults");
                                }
                                if(tourObj.getString("children").equalsIgnoreCase("")){
                                    cCount = "0";
                                }else{
                                    cCount = tourObj.getString("children");
                                }
                                if(tourObj.getString("infants").equalsIgnoreCase("")){
                                    iCount = "0";
                                }else{
                                    iCount = tourObj.getString("infants");
                                }
                                adultCount.setText("Adults (" + aCount + ")");
                                childCount.setText("Child (" + cCount + ")");
                                infantCount.setText("Infants (" + iCount + ")");
                                String subTotalPricetxt = tourObj.getString("currCode") + " " + tourObj.getString("currSymbol") + " " + tourObj.getString("subTotal");
                                subTotal.setText(subTotalPricetxt);
                                gstCount.setText("GST (" + tourObj.getString("tax") + ")");
                                String gstPricetxt = tourObj.getString("currCode") + " " + tourObj.getString("currSymbol") + " " + tourObj.getString("taxAmount");
                                gstPrice.setText(gstPricetxt);
                                String totalPricetxt = tourObj.getString("currCode") + " " + tourObj.getString("currSymbol") + " " + tourObj.getString("price");
                                totalPrice.setText(totalPricetxt);
                                getPersonCount(aCount,cCount,iCount);
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
                        syncBookingSummeryDetails();
                    } else {
                        Toast.makeText(BookingSummary.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(BookingSummary.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("tourid", tourId_txt);
                params.put("isPromo",isPromo_txt);
                params.put("t_adult",tAdult_txt);
                params.put("t_child",tChild_txt);
                params.put("t_infant",tInfant_txt);
                params.put("vehicle_type",vehicleType_txt);
                params.put("vehicle_price",vehiclePrice_txt);
                params.put("vehicle_id",vehicleId_txt);
                params.put("dep_date",depDate_txt);
                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance().getRequestQueue().add(data).addMarker(tag_json_req);
    }

    private void getPersonCount(String aCnt, String cCnt, String iCnt){
        int personCount = Integer.parseInt(aCnt) + Integer.parseInt(cCnt) + Integer.parseInt(iCnt);

        ArrayList<BookingSummaryModel> list=new ArrayList<>();

        for (int i = 0; i < personCount; i++){
            BookingSummaryModel s=new BookingSummaryModel();
            s.setName("");
            s.setPassport("");
            s.setAge("");
            list.add(s);
        }
        BookingSummaryAdapter bookingAdapter = new BookingSummaryAdapter(BookingSummary.this, list);
        mRecyclerView.setAdapter(bookingAdapter);
    }

    public void NoInternetDialog() {
        new AwesomeErrorDialog(BookingSummary.this)
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
