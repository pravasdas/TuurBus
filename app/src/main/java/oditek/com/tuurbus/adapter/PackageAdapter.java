package oditek.com.tuurbus.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oditek.com.tuurbus.PackageBookNow;
import oditek.com.tuurbus.adapter.dataholder.PackageVehicleModel;
import oditek.com.tuurbus.PlanWithFriends;
import oditek.com.tuurbus.R;
import oditek.com.tuurbus.adapter.dataholder.PackageModel;
import oditek.com.tuurbus.navigationdrawer.HomeSlidingActivity;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.webservices.MyApplication;
import oditek.com.tuurbus.webservices.VolleySingleton;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {
    private Context mContext;
    private ImageLoader imageLoader;
    List<PackageModel> list;
    ProgressDialog progressDialog;
    private int i = 0;
    private String apiKey = "";
    JSONArray jsonArray;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_tours_rowlayout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public PackageAdapter(Context mContext, List<PackageModel> list) {
        this.mContext = mContext;
        this.list = list;
        imageLoader = VolleySingleton.getInstance().getImageLoader();
        apiKey = ApiClient.getDataFromKey(mContext,"api_key");
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading");

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PackageModel model = list.get(position);

        holder.name.setText(model.name);
        holder.location.setText(model.location);
        holder.tourtype.setText(model.tourtype);
        holder.days.setText(model.days);
        holder.nights.setText(model.nights);
        holder.speciality.setText(model.speciality);
        holder.ratingBar.setRating(Float.valueOf(model.starsCount));
        if(ApiClient.getDataFromKey(mContext,"user_id").equalsIgnoreCase("")||ApiClient.getDataFromKey(mContext,"user_id").equalsIgnoreCase("0")){
            holder.likeButton.setVisibility(View.GONE);
        }else{
            holder.likeButton.setVisibility(View.VISIBLE);
        }
        imageLoader.get(model.imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.image.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonArray = model.priceArr;
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(mContext);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.recyclerView11.setLayoutManager(linearLayoutManager1);
        PackageVehicleAdapter vehicleAdapter = new PackageVehicleAdapter(mContext, getVehiclePriceData(jsonArray,model.currSymbol),model.tourId);
        holder.recyclerView11.setAdapter(vehicleAdapter);
        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                AddtoWishList(model.tourId,model.appModule);
            }

            @Override
            public void unLiked(LikeButton likeButton) {

            }
        });
        holder.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String image_url = "", vehiclerate = "", scrolldate = "", symbol = "", vehicleName = "", vehicleId = "";
                if(model.priceArr.length() > 0){
                        try {
                            image_url = model.priceArr.getJSONObject(0).getString("sett_img");
                            vehiclerate = model.priceArr.getJSONObject(0).getString("price");
                            scrolldate = model.priceArr.getJSONObject(0).getString("dep_dates");
                            vehicleName = model.priceArr.getJSONObject(0).getString("sett_name");
                            vehicleId = model.priceArr.getJSONObject(0).getString("vehicle_id");
                            symbol = model.currSymbol;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
                Intent intent=new Intent(mContext,PackageBookNow.class);
                intent.putExtra("TourID_", model.tourId);
                intent.putExtra("VehicleImage_", image_url);
                intent.putExtra("VehiclePrice_", vehiclerate);
                intent.putExtra("AvlDates_", scrolldate);
                intent.putExtra("CurrSymbol_", symbol);
                intent.putExtra("VehicleName_", vehicleName);
                intent.putExtra("vehicleId_", vehicleId);
                intent.putExtra("DateString", scrolldate);
                intent.putExtra("From_", "PackageAdapter");
                mContext.startActivity(intent);
            }
        });
        holder.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext,PlanWithFriends.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, location,tourtype,days,nights,speciality;
        public ImageView image;
        public Button button2,button1;
        public RecyclerView recyclerView11;
        public CardView mCardView;
        public CardView cardView2;
        public SimpleRatingBar ratingBar;
        public LikeButton likeButton;
        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv1);
            name = (TextView) itemView.findViewById(R.id.tv1);
            location = (TextView) itemView.findViewById(R.id.tv2);
            tourtype = (TextView) itemView.findViewById(R.id.textv1);
            days = (TextView) itemView.findViewById(R.id.textv3);
            nights = (TextView) itemView.findViewById(R.id.textv5);
            speciality = (TextView) itemView.findViewById(R.id.textv9);
            button2 = (Button) itemView.findViewById(R.id.button2);
            button1 = (Button) itemView.findViewById(R.id.button1);
            ratingBar = (SimpleRatingBar) itemView.findViewById(R.id.ratingbar);
            likeButton = (LikeButton) itemView.findViewById(R.id.star_button);
            recyclerView11=(RecyclerView) itemView.findViewById(R.id.recyclerView11);


            mCardView = (CardView) itemView.findViewById(R.id.card_view);

            cardView2 = (CardView) itemView.findViewById(R.id.card_view2);
            recyclerView11 = (RecyclerView) itemView.findViewById(R.id.recyclerView11);
        }
    }

    private List<PackageVehicleModel> getVehiclePriceData(JSONArray jsonArray, String symbol) {
        List<PackageVehicleModel> priceData = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    PackageVehicleModel rowData = new PackageVehicleModel();
                    JSONObject ltData = jsonArray.getJSONObject(i);

                    rowData.tourId = ltData.getString("tour_id");
                    rowData.vehicleId = ltData.getString("vehicle_id");
                    rowData.image_url = ltData.getString("sett_img");
                    rowData.vehiclerate = ltData.getString("price");
                    rowData.scrolldate = ltData.getString("dep_dates");
                    rowData.vehicleName = ltData.getString("sett_name");
                    rowData.currSymbol = symbol;

                    priceData.add(rowData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return priceData;
    }

    private void AddtoWishList(final String tourId, final String module){
        progressDialog.show();
        String tag_json_req = "sync_like_data";
        StringRequest data = new StringRequest(Request.Method.POST,
                mContext.getResources().getString(R.string.main_url) + mContext.getResources()
                        .getString(R.string.tours_url) + "tourwishlistadd/?" + "appKey=" + apiKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            Log.d("wish response", response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject responseObj = jsonObject.getJSONObject("response");
                            JSONObject errorObj = jsonObject.getJSONObject("error");
                            if(errorObj.getString("status").equalsIgnoreCase("false")){
                                String msg_ = errorObj.getString("msg");
                                new AwesomeErrorDialog(mContext)
                                        .setTitle("Success")
                                        .setMessage(msg_)
                                        .setColoredCircle(R.color.white)
                                        .setDialogIconOnly(R.drawable.main_logo)
                                        .setCancelable(false)
                                        .setButtonBackgroundColor(R.color.colorPrimaryDark)
                                        .setButtonText(mContext.getString(R.string.dialog_ok_button))
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
                                Toast.makeText(mContext, "App ApiKey has Changed",Toast.LENGTH_SHORT).show();
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
                        AddtoWishList(tourId,module);

                    } else {
                        Toast.makeText(mContext, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(mContext, error.getMessage(),
                            Toast.LENGTH_LONG).show();
            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String userId = ApiClient.getDataFromKey(mContext,"user_id");

                Map<String, String> params = new HashMap<>();
                params.put("user_id",userId);
                params.put("tour_id",tourId);
                params.put("module",module);
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
                mContext.getString(R.string.app_url),
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
                                ApiClient.saveDataWithKeyAndValue(mContext,"api_key", key);
                                mContext.startActivity(new Intent(mContext,HomeSlidingActivity.class));
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
                        Toast.makeText(mContext, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        //progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
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