package oditek.com.tuurbus.navigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import oditek.com.tuurbus.AboutUsFragment;
import oditek.com.tuurbus.BlogsFragment;
import oditek.com.tuurbus.ContactUsFragment;
import oditek.com.tuurbus.CrazyToursFragment;
import oditek.com.tuurbus.Home;
import oditek.com.tuurbus.MainLoginFragment;
import oditek.com.tuurbus.MyBookingsFragment;
import oditek.com.tuurbus.MyProfileFragment;
import oditek.com.tuurbus.MyWishlistFragment;
import oditek.com.tuurbus.OtpPage;
import oditek.com.tuurbus.PackageToursFragment;
import oditek.com.tuurbus.R;
import oditek.com.tuurbus.SignUp;
import oditek.com.tuurbus.VehicleRentalFragment;
import oditek.com.tuurbus.navigationdrawer.menu.DrawerAdapter;
import oditek.com.tuurbus.navigationdrawer.menu.DrawerItem;
import oditek.com.tuurbus.navigationdrawer.menu.SimpleItem;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.webservices.VolleySingleton;


public class HomeSlidingActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private static final int POS_HOME = 0;
    private static final int POS_LOGINREGISTER = 1;
    private static final int POS_PACKAGETOUR = 2;
    private static final int POS_VEHICLERENTALS = 3;
    private static final int POS_CRAZYTOURS = 4;
    private static final int POS_BLOGS = 5;
    private static final int POS_CONTACTUS= 6;
    private static final int POS_ABOUTUS = 7;
    private static final int POS_MYPROFILE = 8;
    private static final int POS_MYWISHLIST = 9;
    private static final int POS_MYBOOKINGS = 10;
    private static final int POS_LOGOUT = 11;


    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;
    DrawerAdapter adapter;
    private int i = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getAppApiKey();

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        System.out.println(ApiClient.getDataFromKey(HomeSlidingActivity.this,"user_id"));

        if(ApiClient.getDataFromKey(HomeSlidingActivity.this,"user_id").equals("0")||ApiClient.getDataFromKey(HomeSlidingActivity.this,"user_id").equals("")){
             adapter = new DrawerAdapter(Arrays.asList(
                    createItemFor(POS_HOME).setChecked(true),
                    createItemFor(POS_LOGINREGISTER),
                    createItemFor(POS_PACKAGETOUR),
                    createItemFor(POS_VEHICLERENTALS),
                    createItemFor(POS_CRAZYTOURS),
                    createItemFor(POS_BLOGS),
                    createItemFor(POS_CONTACTUS),
                    createItemFor(POS_ABOUTUS)));
                    adapter.setListener(this);
        }else{
             adapter = new DrawerAdapter(Arrays.asList(
                    createItemFor(POS_HOME).setChecked(true),
                    createItemFor(POS_PACKAGETOUR),
                    createItemFor(POS_VEHICLERENTALS),
                    createItemFor(POS_CRAZYTOURS),
                    createItemFor(POS_BLOGS),
                    createItemFor(POS_CONTACTUS),
                    createItemFor(POS_ABOUTUS),
                    createItemFor(POS_MYPROFILE),
                    createItemFor(POS_MYWISHLIST),
                    createItemFor(POS_MYBOOKINGS),
                     createItemFor(POS_LOGOUT)));
            adapter.setListener(this);
        }



        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_HOME);
    }

    @Override
    public void onItemSelected(int position) {
        slidingRootNav.closeMenu();
        if(ApiClient.getDataFromKey(HomeSlidingActivity.this,"user_id").equals("0")||ApiClient.getDataFromKey(HomeSlidingActivity.this,"user_id").equals("")){
            if(position==0){
                Fragment selectedScreen =new Home();
                showFragment(selectedScreen);
            }else if(position==1){
                Fragment selectedScreen =new MainLoginFragment();
                showFragment(selectedScreen);

            }else if(position==2){
                Fragment selectedScreen =new PackageToursFragment();
                showFragment(selectedScreen);
            }else if(position==3){
                Fragment selectedScreen =new VehicleRentalFragment();
                showFragment(selectedScreen);
            }else if(position==4){
                Fragment selectedScreen =new CrazyToursFragment();
                showFragment(selectedScreen);
            }else if(position==5){
                Fragment selectedScreen =new BlogsFragment();
                showFragment(selectedScreen);
            }else if(position==6){
                Fragment selectedScreen =new ContactUsFragment();
                showFragment(selectedScreen);
            }else if(position==7){
                Fragment selectedScreen =new AboutUsFragment();
                showFragment(selectedScreen);
            }/*else if(position==8){
                Fragment selectedScreen =new MyProfileFragment();
                showFragment(selectedScreen);
            }else if(position==9){

            }else if(position==10){

            }else if(position==11){

            }*/
        }else{
            if(position==0){
                Fragment selectedScreen =new Home();
                showFragment(selectedScreen);
            }else if(position==1){
                Fragment selectedScreen =new PackageToursFragment();
                showFragment(selectedScreen);

            }else if(position==2){
                Fragment selectedScreen =new VehicleRentalFragment();
                showFragment(selectedScreen);
            }else if(position==3){
                Fragment selectedScreen =new CrazyToursFragment();
                showFragment(selectedScreen);
            }else if(position==4){
                Fragment selectedScreen =new BlogsFragment();
                showFragment(selectedScreen);
            }else if(position==5){
                Fragment selectedScreen =new ContactUsFragment();
                showFragment(selectedScreen);
            }else if(position==6){
                Fragment selectedScreen =new AboutUsFragment();
                showFragment(selectedScreen);
            }else if(position==7){
                Fragment selectedScreen =new MyProfileFragment();
                showFragment(selectedScreen);
            }else if(position==8){
                Fragment selectedScreen =new MyWishlistFragment();
                showFragment(selectedScreen);

            }else if(position==9){
                Fragment selectedScreen =new MyBookingsFragment();
                showFragment(selectedScreen);

            }else if(position==10){
                confirmLogout();
            }
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.white))
                .withTextTint(color(R.color.white))
                .withSelectedIconTint(color(R.color.white))
                .withSelectedTextTint(color(R.color.white));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
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
                                ApiClient.saveDataWithKeyAndValue(HomeSlidingActivity.this,"api_key", key);
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
                        Toast.makeText(HomeSlidingActivity.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        //progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(HomeSlidingActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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

    private void confirmLogout(){
        /*new AwesomeErrorDialog(HomeSlidingActivity.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setColoredCircle(R.color.white)
                .setDialogIconOnly(R.drawable.main_logo)
                .setCancelable(false)
                .setButtonBackgroundColor(R.color.colorPrimaryDark)
                .setButtonText("Logout")
                .setErrorButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        Intent mIntent=new Intent(HomeSlidingActivity.this, HomeSlidingActivity.class);
                        mIntent.putExtra("fragmentPosition",1);
                        startActivity(mIntent);
                        SharedPreferences preferences =getSharedPreferences("turrbus", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        finishAffinity();
                    }
                })
                .show();*/

        new AwesomeSuccessDialog(HomeSlidingActivity.this)
                .setTitle("LOGOUT")
                .setMessage("Are you sure that you want to logout?")
                .setColoredCircle(R.color.white)
                .setDialogIconOnly(R.drawable.main_logo)
                .setCancelable(false)
                .setPositiveButtonText("Logout")
                .setPositiveButtonbackgroundColor(R.color.red)
                .setPositiveButtonTextColor(R.color.white)
                .setNegativeButtonText("Cancel")
                .setNegativeButtonbackgroundColor(R.color.deepgreen)
                .setNegativeButtonTextColor(R.color.white)
                .setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        Intent mIntent=new Intent(HomeSlidingActivity.this, HomeSlidingActivity.class);
                        mIntent.putExtra("fragmentPosition",1);
                        startActivity(mIntent);
                        SharedPreferences preferences =getSharedPreferences("turrbus", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        finishAffinity();
                    }
                })
                .setNegativeButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        //click
                    }
                })
                .show();

    }
}
