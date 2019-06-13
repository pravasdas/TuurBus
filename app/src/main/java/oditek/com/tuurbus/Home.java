package oditek.com.tuurbus;

import android.app.AlertDialog;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import oditek.com.tuurbus.adapter.HomeAdapter;
import oditek.com.tuurbus.adapter.PackageAdapter;
import oditek.com.tuurbus.adapter.dataholder.HomeModel;
import oditek.com.tuurbus.navigationdrawer.HomeSlidingActivity;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.util.NetworkConnection;
import oditek.com.tuurbus.webservices.MyApplication;
import oditek.com.tuurbus.webservices.VolleySingleton;


public class Home extends Fragment implements DatePickerDialog.OnDateSetListener{
    Button btnPack, btnVehicle,btnSearchP;
    TextView noData, type_edittext, tour_location, date, pickLocation, dropLocation;
    DatePickerDialog datePickerDialog;
    LinearLayout llPackage, llVehicle;
    CardView cardView;
    RecyclerView mRecyclerView;
    Boolean isOnePressed = true, isSecondPlace = false;
    List<HomeModel> list = new ArrayList<>();
    ProgressDialog progressDialog;
    String apiKey = "", type_id = "", location_id = "", pickLocation_id, dropLocation_id;
    private int i = 0;
    private JSONArray tourTypeData, tourLocationData, tourDepatureData, carPicLocationData, carDropLocationData;
    private DatePickerDialog dpd;
    NetworkConnection nw;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        nw = new NetworkConnection(getActivity());
        btnPack = view.findViewById(R.id.btnPack);
        btnVehicle = view.findViewById(R.id.btnVehicle);
        date = view.findViewById(R.id.etDate);
        llPackage = view.findViewById(R.id.llPackage);
        llVehicle = view.findViewById(R.id.llVehicle);
        btnSearchP= view.findViewById(R.id.btnSearchP);
        noData= view.findViewById(R.id.tv_noData);
        type_edittext= view.findViewById(R.id.etType);
        tour_location= view.findViewById(R.id.etLocation);
        pickLocation= view.findViewById(R.id.etPickLocation);
        dropLocation= view.findViewById(R.id.etDropLocation);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading");
        apiKey = ApiClient.getDataFromKey(getActivity(),"api_key");

        cardView = (CardView) view.findViewById(R.id.card_view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (nw.isConnectingToInternet()) {
            syncHomeDataLists("package_tour");
        } else {
            NoInternetDialog();
        }


        btnPack.setBackgroundColor(Color.parseColor("#F1334C"));

        btnPack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nw.isConnectingToInternet()) {
                    syncHomeDataLists("package_tour");
                } else {
                    NoInternetDialog();
                }
                isOnePressed = true;
                btnPack.setBackgroundColor(Color.parseColor("#F1334C"));

                llPackage.setVisibility(View.VISIBLE);
                llVehicle.setVisibility(View.GONE);
                if (isSecondPlace) {
                    btnVehicle.setBackgroundResource(R.drawable.diagonal);
                    isSecondPlace = false;
                }
            }
        });

        btnVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nw.isConnectingToInternet()) {
                    syncHomeDataLists("vehicle_rental");
                } else {
                    NoInternetDialog();
                }
                btnVehicle.setBackgroundColor(Color.parseColor("#F1334C"));
                isSecondPlace = true;
                llVehicle.setVisibility(View.VISIBLE);
                llPackage.setVisibility(View.GONE);

                if (isOnePressed) {
                    btnPack.setBackgroundResource(R.drawable.diagonal);
                    isOnePressed = false;
                }
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tourDepatureData.length()>0){
                    showDatePicker();
                }else{
                    Toast.makeText(getActivity(),"No Data Found",Toast.LENGTH_LONG).show();
                }
            }
        });

        type_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tourTypeData.length()>0){
                    final CharSequence[] items = new CharSequence[tourTypeData.length()];
                    System.out.println(items.length);
                    try {
                        for(int i = 0; i<tourTypeData.length(); i++){
                            items[i] = tourTypeData.getJSONObject(i).getString("type");
                            //type_id = tourTypeData.getJSONObject(i).getString("id");
                            //types = tourTypeData.getJSONObject(i).getString("type");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Select Your Type");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            // Do something with the selection
                            type_edittext.setText(items[item]);
                            type_edittext.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                            try {
                                type_id = tourTypeData.getJSONObject(item).getString("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    Toast.makeText(getActivity(),"No Data Found",Toast.LENGTH_LONG).show();
                }
            }
        });

        tour_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tourLocationData.length()>0){
                    final CharSequence[] items = new CharSequence[tourLocationData.length()];
                    try {
                        for(int i = 0; i<tourLocationData.length(); i++){
                            items[i] = tourLocationData.getJSONObject(i).getString("name");
                            //location_id = tourLocationData.getJSONObject(i).getString("id");
                            //types = tourTypeData.getJSONObject(i).getString("type");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Select Your Location");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            // Do something with the selection
                            tour_location.setText(items[item]);
                            tour_location.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                            try {
                                location_id = tourLocationData.getJSONObject(item).getString("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    Toast.makeText(getActivity(),"No Data Found",Toast.LENGTH_LONG).show();
                }
            }
        });

        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carPicLocationData.length()>0){
                    final CharSequence[] items = new CharSequence[carPicLocationData.length()];
                    try {
                        for(int i = 0; i<carPicLocationData.length(); i++){
                            items[i] = carPicLocationData.getJSONObject(i).getString("name");
                            pickLocation_id = carPicLocationData.getJSONObject(i).getString("id");
                            //types = tourTypeData.getJSONObject(i).getString("type");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Select Pick Up Location");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            // Do something with the selection
                            pickLocation.setText(items[item]);
                            pickLocation.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    Toast.makeText(getActivity(),"No Data Found",Toast.LENGTH_LONG).show();
                }
            }
        });

        dropLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carDropLocationData.length()>0){
                    final CharSequence[] items = new CharSequence[carDropLocationData.length()];
                    try {
                        for(int i = 0; i<carDropLocationData.length(); i++){
                            items[i] = carDropLocationData.getJSONObject(i).getString("name");
                            dropLocation_id = carDropLocationData.getJSONObject(i).getString("id");
                            //types = tourTypeData.getJSONObject(i).getString("type");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Select Pick Up Location");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            // Do something with the selection
                            dropLocation.setText(items[item]);
                            dropLocation.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    Toast.makeText(getActivity(),"No Data Found",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnSearchP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tour_location.getText().toString().length() == 0){
                    tour_location.setError("Select Location");
                }else if(date.getText().toString().length() == 0){
                    date.setError("Select Date");
                }else if(type_edittext.getText().toString().length() == 0){
                    type_edittext.setError("Select Type");
                }else{
                    syncSearchTourData();
                }
            }
        });
        return view;
    }
    private void syncHomeDataLists(final String btnClick) {
        progressDialog.show();
        String tag_json_req = "sync_district_data";
        StringRequest data = new StringRequest(Request.Method.GET,
                getResources().getString(R.string.main_url) + getResources()
                        .getString(R.string.home_url) + "main?" + "appKey=" + apiKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            Log.d("home response", response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject responseObj = jsonObject.getJSONObject("response");
                            JSONObject errorObj = jsonObject.getJSONObject("error");
                            JSONObject tourObj = responseObj.getJSONObject("tour");
                            JSONObject vehicleObj = responseObj.getJSONObject("vehicle");
                            JSONArray featuredTourArr = tourObj.getJSONArray("featuredTours");
                            tourTypeData = tourObj.getJSONArray("tourTypeArr");
                            tourLocationData = tourObj.getJSONArray("locationArr");
                            tourDepatureData = tourObj.getJSONArray("depDates");
                            //System.out.println(depDateArr);
                            JSONArray featuredCarArr = vehicleObj.getJSONArray("featurecarArr");
                            carPicLocationData = vehicleObj.getJSONArray("carPickLocationArr");
                            carDropLocationData = vehicleObj.getJSONArray("carDropLocationArr");

                            if(btnClick.equals("package_tour")){
                                if(featuredTourArr.length() > 0){
                                    noData.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    HomeAdapter adapter = new HomeAdapter(getActivity(),getHomeTourData(featuredTourArr),"package_tour");
                                    mRecyclerView.setAdapter(adapter);
                                }else{
                                    noData.setVisibility(View.VISIBLE);
                                    mRecyclerView.setVisibility(View.GONE);
                                }
                            }else{
                                if(featuredCarArr.length() > 0){
                                    noData.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    HomeAdapter adapter = new HomeAdapter(getActivity(),getHomeVehicleData(featuredCarArr),"vehicle_rental");
                                    mRecyclerView.setAdapter(adapter);
                                }else{
                                    noData.setVisibility(View.VISIBLE);
                                    mRecyclerView.setVisibility(View.GONE);
                                }
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
                        syncHomeDataLists(btnClick);

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

    private List<HomeModel> getHomeTourData(JSONArray jsonArray) {
        List<HomeModel> homeData = new ArrayList<>();
        try {
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        HomeModel rowData = new HomeModel();
                        JSONObject ltData = jsonArray.getJSONObject(i);

                        rowData.tour_id = ltData.getString("tour_id");
                        rowData.name = ltData.getString("title");
                        rowData.add = ltData.getString("location");
                        rowData.imageUrl = ltData.getString("thumbnail");

                        homeData.add(rowData);
                    }
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return homeData;
    }

    private List<HomeModel> getHomeVehicleData(JSONArray jsonArray) {
        List<HomeModel> homeData = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    HomeModel rowData = new HomeModel();
                    JSONObject ltData = jsonArray.getJSONObject(i);

                    rowData.car_id = ltData.getString("car_id");
                    rowData.name = ltData.getString("title");
                    rowData.add = ltData.getString("location");
                    rowData.rate = ltData.getString("price");
                    rowData.imageUrl = ltData.getString("thumbnail");

                    homeData.add(rowData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return homeData;
    }

    private void showDatePicker(){
        Calendar calendar = Calendar.getInstance();

        if (dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    Home.this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            dpd.initialize(
                    Home.this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
        }
        dpd.show(getActivity().getFragmentManager(),"DatePickerDialog");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String[] depaturedates = new String[tourDepatureData.length()];
        java.util.Date date = null;

        for (int i = 0;i < depaturedates.length; i++) {

            try {
                depaturedates[i]=tourDepatureData.optString(i);
                date = sdf.parse(depaturedates[i]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            calendar = dateToCalendar(date);
            System.out.println(calendar.getTime());

            List<Calendar> dates = new ArrayList<>();
            dates.add(calendar);
            Calendar[] enabledDays1 = dates.toArray(new Calendar[dates.size()]);
            dpd.setSelectableDays(enabledDays1);
        }
    }

    private Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String dateText = dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
        date.setText(dateText);
        date.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
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
                                    Fragment fragment = PackageToursFragment.newInstance(responseArr,"HomeSearch");
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.container, fragment)
                                            .addToBackStack(null)
                                            .commit();
                                }
                            }else{
                                new AwesomeErrorDialog(getActivity())
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
                        syncSearchTourData();

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
                params.put("location", location_id);
                params.put("tour_type", type_id);
                params.put("dep_date", date.getText().toString().trim());
                params.put("offset", "1");
                params.put("ttype", "");
                params.put("ftype", "");
                params.put("stars", "");
                if(ApiClient.getDataFromKey(getActivity(),"user_id").equalsIgnoreCase("")||ApiClient.getDataFromKey(getActivity(),"user_id").equalsIgnoreCase("0")){
                    params.put("user_id", "");
                }else{
                    params.put("user_id", ApiClient.getDataFromKey(getActivity(),"user_id"));
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
