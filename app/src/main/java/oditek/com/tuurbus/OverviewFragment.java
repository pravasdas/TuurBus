package oditek.com.tuurbus;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import oditek.com.tuurbus.adapter.BlogsAdapter;
import oditek.com.tuurbus.adapter.InclusionsAdapter;
import oditek.com.tuurbus.adapter.ReviewsAdapter;
import oditek.com.tuurbus.adapter.dataholder.FilterModel;
import oditek.com.tuurbus.adapter.dataholder.OverviewPaymentOptionModel;
import oditek.com.tuurbus.adapter.dataholder.PackageVehicleModel;
import oditek.com.tuurbus.adapter.dataholder.ReviewsModel;
import oditek.com.tuurbus.adapter.dataholder.VehicleDataModel;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.webservices.MyApplication;
import oditek.com.tuurbus.webservices.VolleySingleton;

public class OverviewFragment extends Fragment implements DatePickerDialog.OnDateSetListener{

    private String data = "", vehicleImg_txt = "", vehiclePrice_txt = "", currSym_txt = "", apiKey = "", paymenrData = "",
                   vehicleName_txt = "", dateToSend_txt = "", vehicleId_txt = "", tourID_txt = "", dateString_txt = "";
    private JSONArray vehicleArr, paymentArr;
    private TextView vehiclePrice, seatTotal, seatAvail, seatBooked, depDate, adult, child, infant,
                     adult_txt, child_txt, infant_txt, adultPrice, childPrice, infantPrice, adultSym,
                     childSym, infantSym, tvDesc, tvPolicy;
    private ImageButton adultMinus, adultPlus, childMinus, childPlus, infantMinus, infantPlus;
    private ImageView vehicleImg;
    private EditText promoCode;
    private LinearLayout selectVehicle;
    private ImageLoader imageLoader;
    private Button totalPriceBtn, btnCheck, btnRemove, btnPayNow;
    private int i = 0;
    private int adultounter = 1, childCounter = 0, infantCounter = 0;
    ProgressDialog progressDialog;
    private DatePickerDialog dpd;
    RecyclerView mRecyclerView;
    private String  _stringValAdult = "", _stringValChild = "", _stringValInfant = "",  _totalPrice = "", desc = "",
            policy = "", isPromo = "0";

    public static OverviewFragment newInstance(JSONArray carList, JSONArray paymentList, String vehicleImg, String vehiclePrice, String currSym,
                                               String vehicleName, String dateToSend, String vehicleId, String tourID,
                                               String dateString, String desc, String policy) {
        OverviewFragment f = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString("ArrayString", carList.toString());
        args.putString("PaymentArray", paymentList.toString());
        args.putString("VehicleImage", vehicleImg);
        args.putString("vehiclePrice", vehiclePrice);
        args.putString("currSym", currSym);
        args.putString("vehicleName", vehicleName);
        args.putString("dateToSend", dateToSend);
        args.putString("vehicleId", vehicleId);
        args.putString("tourID", tourID);
        args.putString("DateString_", dateString);
        args.putString("Description", desc);
        args.putString("Policy", policy);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        progressDialog = new ProgressDialog(getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        vehiclePrice = view.findViewById(R.id.tvVehiclePrice);
        seatTotal = view.findViewById(R.id.tv_total);
        seatAvail = view.findViewById(R.id.tv_available);
        seatBooked = view.findViewById(R.id.tv_booked);
        adult = view.findViewById(R.id.tvAdults);
        adult_txt = view.findViewById(R.id.tv_box);
        adultPrice = view.findViewById(R.id.tvAdultPrice);
        child = view.findViewById(R.id.tvChild);
        child_txt = view.findViewById(R.id.tv_box1);
        childPrice = view.findViewById(R.id.tvChildPrice);
        infant = view.findViewById(R.id.tvInfants);
        infant_txt = view.findViewById(R.id.tv_box2);
        infantPrice = view.findViewById(R.id.tvInfantPrice);
        adultSym = view.findViewById(R.id.tvAdultPriceSym);
        childSym = view.findViewById(R.id.tvChildPriceSym);
        infantSym = view.findViewById(R.id.tvInfantPriceSym);
        tvDesc = view.findViewById(R.id.tv_desc);
        tvPolicy = view.findViewById(R.id.tv_policy);
        vehicleImg = view.findViewById(R.id.ivVehicleImg);
        adultMinus = view.findViewById(R.id.ibAdultMinus);
        adultPlus = view.findViewById(R.id.ibAdultPlus);
        childMinus = view.findViewById(R.id.ibChildMinus);
        childPlus = view.findViewById(R.id.ibChildPlus);
        infantMinus = view.findViewById(R.id.ibInfantMinus);
        infantPlus = view.findViewById(R.id.ibInfantPlus);
        totalPriceBtn = view.findViewById(R.id.total_button);
        btnCheck = view.findViewById(R.id.btnCheck);
        btnRemove = view.findViewById(R.id.btnRemove);
        btnPayNow = view.findViewById(R.id.btnPay);
        depDate = view.findViewById(R.id.etDepDate);
        promoCode = view.findViewById(R.id.tvpromo);
        selectVehicle = (LinearLayout) view.findViewById(R.id.vehicleLayout);
        imageLoader = VolleySingleton.getInstance().getImageLoader();
        apiKey = ApiClient.getDataFromKey(getActivity(),"api_key");
        Bundle args = getArguments();
        if (args != null) {
            data = args.getString("ArrayString");
            paymenrData = args.getString("PaymentArray");
            //System.out.println(data);
            try {
                vehicleArr = new JSONArray(data);
                paymentArr = new JSONArray(paymenrData);
                OverviewPaymentAdapter paymentAdapter = new OverviewPaymentAdapter(getActivity(), getPaymentData(paymentArr));
                mRecyclerView.setAdapter(paymentAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            vehicleImg_txt = args.getString("VehicleImage");
            vehiclePrice_txt = args.getString("vehiclePrice");
            currSym_txt = args.getString("currSym");
            vehicleName_txt = args.getString("vehicleName");
            dateToSend_txt = args.getString("dateToSend");
            vehicleId_txt = args.getString("vehicleId");
            tourID_txt = args.getString("tourID");
            dateString_txt = args.getString("DateString_");
            desc = args.getString("Description");
            policy = args.getString("Policy");
            syncVehicleSeatData(tourID_txt,vehicleId_txt);

            imageLoader.get(vehicleImg_txt, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    vehicleImg.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            vehiclePrice.setText(vehicleName_txt + " " + currSym_txt + " " + vehiclePrice_txt);
            depDate.setText(dateToSend_txt);
            adult.setText("Adults " + vehiclePrice_txt);
            child.setText("Child " + vehiclePrice_txt);
            infant.setText("Infants " + vehiclePrice_txt);
            adultSym.setText(currSym_txt);
            childSym.setText(currSym_txt);
            infantSym.setText(currSym_txt);
            adultPrice.setText(vehiclePrice_txt);
            _totalPrice = vehiclePrice_txt;
            totalPriceBtn.setText("Total " + currSym_txt + vehiclePrice_txt);
            tvDesc.setText(desc);
            tvPolicy.setText(policy);
            }

        selectVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vehicleArr.length()>0){
                    final CharSequence[] items = new CharSequence[vehicleArr.length()];
                    System.out.println(items.length);
                    try {
                        for(int i = 0; i<vehicleArr.length(); i++){
                            items[i] = vehicleArr.getJSONObject(i).getString("sett_name");
                            //String vehicle = tourTypeData.getJSONObject(i).getString("id");
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
                            try {
                                vehiclePrice.setText(items[item] + " " + currSym_txt + " " + vehicleArr.getJSONObject(item).getString("price"));
                                vehiclePrice.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                                vehicleId_txt = vehicleArr.getJSONObject(item).getString("vehicle_id");
                                vehiclePrice_txt = vehicleArr.getJSONObject(item).getString("price");
                                vehicleName_txt = vehicleArr.getJSONObject(item).getString("sett_name");
                                seatTotal.setText(vehicleArr.getJSONObject(item).getString("t_seat"));
                                seatAvail.setText(vehicleArr.getJSONObject(item).getString("a_seat"));
                                seatBooked.setText(vehicleArr.getJSONObject(item).getString("b_seat"));
                                imageLoader.get(vehicleArr.getJSONObject(item).getString("sett_img"), new ImageLoader.ImageListener() {
                                    @Override
                                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                        vehicleImg.setImageBitmap(response.getBitmap());
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                                if(vehicleArr.getJSONObject(item).getString("dep_dates").equalsIgnoreCase("")){
                                    Calendar calendar = Calendar.getInstance();
                                    if (dpd == null) {
                                        dpd = DatePickerDialog.newInstance(
                                                OverviewFragment.this,
                                                calendar.get(Calendar.YEAR),
                                                calendar.get(Calendar.MONTH),
                                                calendar.get(Calendar.DAY_OF_MONTH)
                                        );
                                    } else {
                                        dpd.initialize(
                                                OverviewFragment.this,
                                                calendar.get(Calendar.YEAR),
                                                calendar.get(Calendar.MONTH),
                                                calendar.get(Calendar.DAY_OF_MONTH)
                                        );
                                    }
                                    dpd.show(getActivity().getFragmentManager(),"DatePickerDialog");
                                    dpd.setMinDate(calendar);

                                }else{
                                    showDatePicker(vehicleArr.getJSONObject(item).getString("dep_dates"));
                                }
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

        depDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateToSend_txt.equalsIgnoreCase("")){
                    Calendar calendar = Calendar.getInstance();
                    if (dpd == null) {
                        dpd = DatePickerDialog.newInstance(
                                OverviewFragment.this,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                        );
                    } else {
                        dpd.initialize(
                                OverviewFragment.this,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                        );
                    }
                    dpd.show(getActivity().getFragmentManager(),"DatePickerDialog");
                    dpd.setMinDate(calendar);

                }else{
                    showDatePicker(dateString_txt);
                }
            }
        });

        selectVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        adultPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adultounter++;
                _stringValAdult = Integer.toString(adultounter);
                adult_txt.setText(_stringValAdult);
                setAdultPrice(_stringValAdult);
            }
        });
        adultMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adultounter != 0){
                    adultounter--;
                    _stringValAdult = Integer.toString(adultounter);
                    adult_txt.setText(_stringValAdult);
                    setAdultPrice(_stringValAdult);
                }

            }
        });

        childPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childCounter++;
                _stringValChild = Integer.toString(childCounter);
                child_txt.setText(_stringValChild);
                setChildPrice(_stringValChild);
            }
        });
        childMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(childCounter != 0){
                    childCounter--;
                    _stringValChild = Integer.toString(childCounter);
                    child_txt.setText(_stringValChild);
                    setChildPrice(_stringValChild);
                }
            }
        });

        infantPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infantCounter++;
                _stringValInfant = Integer.toString(infantCounter);
                infant_txt.setText(_stringValInfant);
                setInfantPrice(_stringValInfant);
            }
        });
        infantMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(infantCounter != 0){
                    infantCounter--;
                    _stringValInfant = Integer.toString(infantCounter);
                    infant_txt.setText(_stringValInfant);
                    setInfantPrice(_stringValInfant);
                }
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(promoCode.getText().toString().length() == 0){
                    promoCode.setError("Enter Promo Code");
                }else {
                    checkPromoCode();
                }
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPromo = "0";
                btnCheck.setVisibility(View.VISIBLE);
                btnRemove.setVisibility(View.GONE);
                promoCode.setText("");
                setTotalPrice();

            }
        });

        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(depDate.getText().toString().length() == 0){
                    depDate.setError("Departure Date is required");
                }else{
                    Intent intent = new Intent(getActivity(), BookingSummary.class);
                    intent.putExtra("TourId_", tourID_txt);
                    intent.putExtra("IsPromo_", isPromo);
                    intent.putExtra("TotalAdult_", adult_txt.getText().toString());
                    intent.putExtra("TotalChild_", child_txt.getText().toString());
                    intent.putExtra("TotalInfant_", infant_txt.getText().toString());
                    intent.putExtra("VehicleType_", vehicleName_txt);
                    intent.putExtra("vehiclePrice_", vehiclePrice_txt);
                    intent.putExtra("VehicleId_", vehicleId_txt);
                    intent.putExtra("DepartureDate_", depDate.getText().toString());
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    private void syncVehicleSeatData(final String tourId, final String vehicleId){
        progressDialog.show();
        String tag_json_req = "sync_seat_data";
        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.main_url) + getResources()
                        .getString(R.string.tours_url) + "vehicleSeat/?" + "appKey=" + apiKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            Log.d("crazy response", response);
                            JSONArray jsonArray = new JSONArray(response);
                            if(jsonArray.length()>0){
                                JSONObject ltData = jsonArray.getJSONObject(0);
                                String id = ltData.getString("id");
                                String totalSeat = ltData.getString("t_seat");
                                String availSeat = ltData.getString("a_seat");
                                String bookedSeat = ltData.getString("b_seat");
                                seatTotal.setText(totalSeat);
                                seatAvail.setText(availSeat);
                                seatBooked.setText(bookedSeat);
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
                        syncVehicleSeatData(tourId,vehicleId);

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
                params.put("tour_id", tourId);
                params.put("vehicle_id", vehicleId);
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

    private String getDepDate(String dates){
        String displaydate = "";
        List<String> myList = new ArrayList<String>(Arrays.asList(dates.split(",")));
        //System.out.println(myList);
        for(int i = 0; i < myList.size(); i++){
            String depDate = myList.get(i);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date strDate = null;
            Date currentDate = new Date();
            //System.out.println(currentDate);
            try {
                strDate = sdf.parse(depDate);
                //System.out.println(strDate);
                //String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                if (strDate.after(currentDate)) {
                    displaydate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(strDate);
                    break;
                }
                else{
                    displaydate = "";
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return displaydate;
    }

    private void setAdultPrice(String value){
        int priceVal = Integer.parseInt(value) * Integer.parseInt(vehiclePrice_txt);
        String finalVal = Integer.toString(priceVal);
        adultPrice.setText(finalVal);
        setTotalPrice();
    }

    private void setChildPrice(String value){
        int priceVal = Integer.parseInt(value) * Integer.parseInt(vehiclePrice_txt);
        String finalVal = Integer.toString(priceVal);
        childPrice.setText(finalVal);
        setTotalPrice();
    }

    private void setInfantPrice(String value){
        int priceVal = Integer.parseInt(value) * Integer.parseInt(vehiclePrice_txt);
        String finalVal = Integer.toString(priceVal);
        infantPrice.setText(finalVal);
        setTotalPrice();
    }

    private void setTotalPrice(){
        String aVal = adultPrice.getText().toString().trim();
        String cVal = childPrice.getText().toString().trim();
        String iVal = infantPrice.getText().toString().trim();
        int priceVal = Integer.parseInt(aVal) + Integer.parseInt(cVal) + Integer.parseInt(iVal);
        _totalPrice = Integer.toString(priceVal);
        totalPriceBtn.setText("Total " + currSym_txt + " " + _totalPrice);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String dateText = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        depDate.setText(dateText);
        depDate.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
    }

    private void showDatePicker(String dateArr){
        Calendar calendar = Calendar.getInstance();

        if (dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    OverviewFragment.this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            dpd.initialize(
                    OverviewFragment.this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
        }
        dpd.show(getActivity().getFragmentManager(),"DatePickerDialog");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        String[] depaturedates = dateArr.split(",");
        java.util.Date date = null;
        Date currentDate = new Date();

        for (int i = 0;i < depaturedates.length; i++) {
            try {
                date = sdf.parse(depaturedates[i]);
                if(date.after(currentDate)){
                    calendar = dateToCalendar(date);
                    System.out.println(calendar.getTime());

                    List<Calendar> dates = new ArrayList<>();
                    dates.add(calendar);
                    Calendar[] enabledDays1 = dates.toArray(new Calendar[dates.size()]);
                    dpd.setSelectableDays(enabledDays1);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public class OverviewPaymentAdapter extends RecyclerView.Adapter<OverviewPaymentAdapter.ViewHolder> {
        private Context mContext;

        List<OverviewPaymentOptionModel> list;

        @Override
        public OverviewPaymentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.overview_payment_rowlayout, parent, false);

            OverviewPaymentAdapter.ViewHolder vh = new OverviewPaymentAdapter.ViewHolder(v);
            return vh;
        }


        public OverviewPaymentAdapter(Context mContext, List<OverviewPaymentOptionModel> list) {
            this.mContext = mContext;
            this.list = list;

        }

        @Override
        public void onBindViewHolder(OverviewPaymentAdapter.ViewHolder holder, int position) {
            OverviewPaymentOptionModel overviewPaymentModel = list.get(position);

            holder.name.setText(overviewPaymentModel.name);

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView name;
            public CardView mCardView;

            public ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.tv1);
                mCardView = itemView.findViewById(R.id.card_view);
            }
        }
    }

    private List<OverviewPaymentOptionModel> getPaymentData(JSONArray jsonArray) {
        List<OverviewPaymentOptionModel> paymentData = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    OverviewPaymentOptionModel rowData = new OverviewPaymentOptionModel();
                    JSONObject ltData = jsonArray.getJSONObject(i);

                    rowData.name = ltData.getString("name");
                    rowData.paymentId = ltData.getString("id");

                    paymentData.add(rowData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return paymentData;
    }

    private void checkPromoCode(){
        progressDialog.show();
        String tag_json_req = "sync_promo";
        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.main_url) + getResources()
                        .getString(R.string.tours_url) + "promo/?" + "appKey=" + apiKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            //progressDialog.dismiss();
                            Log.d("promo response is ", response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject errorObj = jsonObject.getJSONObject("error");
                            String status = errorObj.getString("status");
                            if(status.equalsIgnoreCase("false")){
                                new AwesomeErrorDialog(getActivity())
                                        .setTitle("Success")
                                        .setMessage("Promo Code Applied Successfully")
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
                                btnCheck.setVisibility(View.GONE);
                                btnRemove.setVisibility(View.VISIBLE);
                                JSONObject responseObj = jsonObject.getJSONObject("response");
                                String promoPercent = responseObj.getString("promo_percentage");
                                DicountPromoCodeAmt(promoPercent);

                            }else{
                                btnCheck.setVisibility(View.VISIBLE);
                                btnRemove.setVisibility(View.GONE);
                                new AwesomeErrorDialog(getActivity())
                                        .setTitle("")
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
                        checkPromoCode();
                    } else {
                        Toast.makeText(getActivity(), "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String promoData = promoCode.getText().toString().trim();

                Map<String, String> params = new HashMap<>();
                params.put("tourid", tourID_txt);
                params.put("promo_code",promoData);
                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance().getRequestQueue().add(data).addMarker(tag_json_req);
    }

    private void DicountPromoCodeAmt(String promopercent){
        isPromo = "1";
        int percent = Integer.parseInt(promopercent);
        int totalAmt = Integer.parseInt(_totalPrice);
        int discountAmt = (percent*totalAmt)/100;
        System.out.println(discountAmt);
        _totalPrice = String.valueOf(totalAmt - discountAmt);
        System.out.println(discountAmt);
        totalPriceBtn.setText("Total " + currSym_txt + _totalPrice);
    }

    /*private void showVehicleDialog(){
        // Create a alert dialog builder.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Create SimpleAdapter list data.

        List<VehicleDataModel> vehicleData = new ArrayList<>();
        try {
            if (vehicleArr != null && vehicleArr.length() > 0) {
                for (int i = 0; i < vehicleArr.length(); i++) {
                    VehicleDataModel rowData = new VehicleDataModel();
                    JSONObject ltData = vehicleArr.getJSONObject(i);

                    rowData.tourId = ltData.getString("tour_id");
                    rowData.vehicleId = ltData.getString("vehicle_id");
                    rowData.settId = ltData.getString("sett_id");
                    rowData.settImage = ltData.getString("sett_img");
                    rowData.settName = ltData.getString("sett_name");
                    rowData.settType = ltData.getString("sett_type");
                    rowData.price = ltData.getString("price");
                    rowData.tSeat = ltData.getString("t_seat");
                    rowData.aSeat = ltData.getString("a_seat");
                    rowData.bSeat = ltData.getString("b_seat");
                    rowData.vehicleType = ltData.getString("vehicle_type");
                    rowData.date = getDepDate(ltData.getString("dep_dates"));

                    vehicleData.add(rowData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create SimpleAdapter object.
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), vehicleData,
                R.layout.vehicle_item_row,
                new String[]{"image", "name"},
                new int[]{R.id.ivVehicleImg1,R.id.tvVehiclePrice1});

        // Set the data adapter.
        builder.setAdapter(simpleAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int itemIndex) {
               // textViewTmp.setText("You choose item " + listItemArr[itemIndex]);
            }
        });

        builder.setCancelable(false);
        builder.create();
        builder.show();


    }*/
}
