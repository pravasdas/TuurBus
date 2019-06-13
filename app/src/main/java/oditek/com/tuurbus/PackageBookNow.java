package oditek.com.tuurbus;

import android.app.ProgressDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import oditek.com.tuurbus.adapter.SliderAdapter;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.webservices.VolleySingleton;

public class PackageBookNow extends AppCompatActivity {
    ImageView ivBack;
    ViewPager viewPager;
    TabLayout indicator;
    private TabLayout tabLayout;
    private ViewPager viewpager2;
    List<String> image;
    private String tourID = "",apiKey = "", from = "", vehicleImg = "", vehiclePrice = "",
                   date = "", currSym = "", vehicleName = "", dateToSend = "", vehicleId = "",
                   dateString = "", description = "", policy = "", appModule = "";
    private int i = 0;
    ProgressDialog progressDialog;
    private JSONArray inArr, exArr, reviewArr, vehicleArr, paymentArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_book_now);

        progressDialog = new ProgressDialog(PackageBookNow.this);
        progressDialog.setMessage("Loading");
        apiKey = ApiClient.getDataFromKey(PackageBookNow.this,"api_key");
        ivBack = findViewById(R.id.ivBack);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (TabLayout) findViewById(R.id.indicator);
        viewpager2 = (ViewPager) findViewById(R.id.viewpager2);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        image = new ArrayList<>();

        tourID = getIntent().getStringExtra("TourID_");
        from = getIntent().getStringExtra("From_");
        vehicleImg = getIntent().getStringExtra("VehicleImage_");
        vehiclePrice = getIntent().getStringExtra("VehiclePrice_");
        date = getIntent().getStringExtra("AvlDates_");
        currSym = getIntent().getStringExtra("CurrSymbol_");
        vehicleName = getIntent().getStringExtra("VehicleName_");
        vehicleId = getIntent().getStringExtra("vehicleId_");
        dateString = getIntent().getStringExtra("DateString");

        if(from.equalsIgnoreCase("PackageAdapter")){
            if(!date.equalsIgnoreCase("")){
                List<String> myList = new ArrayList<String>(Arrays.asList(date.split(",")));
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
                            dateToSend = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(strDate);
                            break;
                        }
                        else{
                            dateToSend = "";
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                dateToSend = "";
            }
        }else{
            if(!date.equalsIgnoreCase("")){
                dateToSend = date;
            }else {
                dateToSend = "";
            }
        }
        syncTourDetails();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 2000, 4000);
    }
    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            PackageBookNow.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < image.size() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(PackageBookNow.this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Overview");
        tabOne.setGravity(Gravity.CENTER_HORIZONTAL);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(PackageBookNow.this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Inclusions");
        tabTwo.setGravity(Gravity.CENTER_HORIZONTAL);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(PackageBookNow.this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Exclusions");
        tabThree.setGravity(Gravity.CENTER_HORIZONTAL);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(PackageBookNow.this).inflate(R.layout.custom_tab, null);
        tabFour.setText("Reviews");
        tabFour.setGravity(Gravity.CENTER_HORIZONTAL);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    private void setupViewPager(ViewPager viewPager) {
        PackageBookNow.ViewPagerAdapter adapter = new PackageBookNow.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new OverviewFragment(), "Overview");
        adapter.addFrag(new InclusionsFragment(), "Inclusions");
        adapter.addFrag(new ExclusionsFragment(), "Exclusions");
        adapter.addFrag(new ReviewsFragment(), "Reviews");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            //return mFragmentList.get(position);
            final Fragment result;
            switch (position) {
                case 0:
                    // First Fragment of First TabvehicleImg = "", vehiclePrice = "",
                    //                   date = "", currSym = "", vehicleName = "", dateToSend = "", vehicleId = ""
                    result = OverviewFragment.newInstance(vehicleArr, paymentArr, vehicleImg, vehiclePrice, currSym, vehicleName, dateToSend, vehicleId, tourID, dateString, description, policy);
                    break;
                case 1:
                    // First Fragment of Second Tab
                    result = InclusionsFragment.newInstance(inArr);
                    break;
                case 2:
                    // First Fragment of Third Tab
                    result = ExclusionsFragment.newInstance(exArr);
                    break;
                case 3:
                    // First Fragment of Third Tab
                    result = ReviewsFragment.newInstance(reviewArr, tourID, appModule);
                    break;
                default:
                    result = null;
                    break;
            }

            return result;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void syncTourDetails(){
        String tag_json_req = "sync_details";

        StringRequest data = new StringRequest(Request.Method.GET,
                getResources().getString(R.string.main_url) + getResources()
                        .getString(R.string.tours_url) + "details/?" + "appKey=" + apiKey + "&id=" + tourID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //progressDialog.dismiss();
                            Log.d("details response is ", response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject errorObj = jsonObject.getJSONObject("error");
                            String statusCode = errorObj.getString("status_code");
                            if(statusCode.equalsIgnoreCase("-1")){
                                //call the appkey API
                                Toast.makeText(PackageBookNow.this, "App ApiKey has Changed",Toast.LENGTH_SHORT).show();
                                //getAppApiKey();
                            }
                            JSONObject responseObj = jsonObject.getJSONObject("response");
                            JSONObject tourObj = responseObj.getJSONObject("tour");
                            reviewArr = responseObj.getJSONArray("reviews");
                            vehicleArr = tourObj.getJSONArray("vresult");
                            paymentArr = tourObj.getJSONArray("paymentOptions");
                            description = tourObj.getString("desc");
                            policy = tourObj.getString("policy");
                            appModule = tourObj.getString("appModule");

                            //code for slider image fetch
                            JSONArray imageArr = tourObj.getJSONArray("sliderImages");
                            for(int i = 0; i < imageArr.length(); i++){
                                JSONObject ltData = imageArr.getJSONObject(i);
                                image.add(ltData.getString("fullImage"));
                            }
                            viewPager.setAdapter(new SliderAdapter(PackageBookNow.this, image));
                            indicator.setupWithViewPager(viewPager, true);

                            inArr = tourObj.getJSONArray("inclusions");
                            exArr = tourObj.getJSONArray("exclusions");

                            setupViewPager(viewpager2);
                            tabLayout.setupWithViewPager(viewpager2);
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
                        syncTourDetails();
                    } else {
                        Toast.makeText(PackageBookNow.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        //progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(PackageBookNow.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
