package oditek.com.tuurbus;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class VehicleRentalFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.mipmap.luxury_car_gray,
            R.mipmap.bus_gray,
            R.mipmap.suv_gray,
            R.mipmap.compact_car_gray,
            R.mipmap.sedan_gray,
            R.mipmap.minibus_gray
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vehicle_rental, container, false);

        viewPager = (ViewPager)view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout)view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(0);
        setupTabIcons();

        return view;
    }

    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabOne.setText("Luxury");
        tabOne.setGravity(Gravity.CENTER_HORIZONTAL);
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.luxury, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Bus");
        tabTwo.setGravity(Gravity.CENTER_HORIZONTAL);
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bus, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabThree.setText("SUV");
        tabThree.setGravity(Gravity.CENTER_HORIZONTAL);
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.suv, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabFour.setText("Compact");
        tabFour.setGravity(Gravity.CENTER_HORIZONTAL);
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.compact, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);

        TextView tabFive = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabFive.setText("Sedan");
        tabFive.setGravity(Gravity.CENTER_HORIZONTAL);
        tabFive.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sedan, 0, 0);
        tabLayout.getTabAt(4).setCustomView(tabFive);

        TextView tabSix = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabSix.setText("Mini-Bus");
        tabSix.setGravity(Gravity.CENTER_HORIZONTAL);
        tabSix.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.minibus, 0, 0);
        tabLayout.getTabAt(5).setCustomView(tabSix);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFrag(new VrentalsLuxury(), "Luxury");
        adapter.addFrag(new VrentalsLuxury(), "Bus");
        adapter.addFrag(new VrentalsLuxury(), "SUV");
        adapter.addFrag(new VrentalsLuxury(), "Compact");
        adapter.addFrag(new VrentalsLuxury(), "Sedan");
        adapter.addFrag(new VrentalsLuxury(), "Mini-Bus");
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
            return mFragmentList.get(position);
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

}


