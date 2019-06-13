package oditek.com.tuurbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import oditek.com.tuurbus.adapter.CustomDrawerAdapter;
import oditek.com.tuurbus.adapter.dataholder.DrawerItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment fragment=new Home();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame,fragment);
        ft.commit();

        ListView mDrawerListView=findViewById(R.id.mDrawerListView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);



        ArrayList<DrawerItem> dataList = new ArrayList<DrawerItem>();
        dataList.add(new DrawerItem(getString(R.string.home), R.drawable.home));
        dataList.add(new DrawerItem(getString(R.string.login_register), R.drawable.login_register));
        dataList.add(new DrawerItem(getString(R.string.package_tour), R.drawable.package_tours_30x30));
        dataList.add(new DrawerItem(getString(R.string.vehicle_rentals), R.drawable.vehicle_rentals));
        dataList.add(new DrawerItem(getString(R.string.crazy_tours), R.drawable.crazy_hours));
        dataList.add(new DrawerItem(getString(R.string.blogs), R.drawable.blogs));
        dataList.add(new DrawerItem(getString(R.string.contact_us), R.drawable.contact_us));
        dataList.add(new DrawerItem(getString(R.string.about_us), R.drawable.about_us));


        mDrawerListView.setAdapter(new CustomDrawerAdapter(MainActivity.this,
                R.layout.custom_drawer_layout,
                dataList));


        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (position == 0) {

                    Fragment fragment1 = new Home();
                    callTransaction(fragment1);
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);

                } else if (position == 1) {
                    Intent in = new Intent(getBaseContext(), Login.class);
                    startActivity(in);
                } else if (position == 2) {
                    Toast.makeText(MainActivity.this, "position 2", Toast.LENGTH_SHORT).show();
                } else if (position == 3) {
                    Toast.makeText(MainActivity.this, "position 3", Toast.LENGTH_SHORT).show();

                } else if (position == 4) {

                    Fragment fragment1 = new CrazyToursFragment();
                    callTransaction(fragment1);
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);

                } else if (position == 5) {
                    Toast.makeText(MainActivity.this, "position 5", Toast.LENGTH_SHORT).show();
                } else if (position == 6) {
                    Toast.makeText(MainActivity.this, "position 6", Toast.LENGTH_SHORT).show();
                } else if (position == 7) {
                    Toast.makeText(MainActivity.this, "position 7", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        Fragment fragment1 = null;
//        if (id == R.id.home) {
//            fragment1 = new Home();
//            callTransaction(fragment1);
//        } else if (id == R.id.login_register) {
//            fragment1 = new LoginT();
//            callTransaction(fragment1);
//        }

        return true;
    }

    public void callTransaction(Fragment fragment1) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment1);
        ft.addToBackStack(null);
        ft.commit();
    }
}
