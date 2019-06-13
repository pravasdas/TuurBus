package oditek.com.tuurbus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import oditek.com.tuurbus.adapter.MyBookingsAdapter;
import oditek.com.tuurbus.adapter.dataholder.MyBookingsModel;

public class MyBookingsFragment extends Fragment {

    CardView cardView;
    RecyclerView bRecyclerView;
    List<MyBookingsModel> list = new ArrayList<>();


    public MyBookingsFragment() {
        // Required empty public constructor
    }


    public static MyBookingsFragment newInstance(String param1, String param2) {
        MyBookingsFragment fragment = new MyBookingsFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_my_bookings, container, false);

        cardView = (CardView)view.findViewById(R.id.card_view);
        bRecyclerView= (RecyclerView)view.findViewById(R.id.bookingRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        bRecyclerView.setLayoutManager(linearLayoutManager);
        getData();
        return view;
    }

    private void getData()
    {
        ArrayList<MyBookingsModel> list=new ArrayList<>();

        MyBookingsModel s=new MyBookingsModel();
        s.setImage(R.mipmap.blogs1);
        s.setName("Virgin Gorda beaches and lob...");
        s.setLocation("Teixeira de Freitas");
        s.setPrice("INR ₹4000");
        s.setBookedDate("05/11/2018");
        s.setDueDate("06/11/2018");
        s.setBookingID("121");
        s.setBookingCode("4697");
        list.add(s);

        s=new MyBookingsModel();
        s.setImage(R.mipmap.blogs2);
        s.setName("Virgin Gorda beaches and lob...");
        s.setLocation("Teixeira de Freitas");
        s.setPrice("INR ₹4000");
        s.setBookedDate("05/11/2018");
        s.setDueDate("06/11/2018");
        s.setBookingID("121");
        s.setBookingCode("4697");
        list.add(s);

        s=new MyBookingsModel();
        s.setImage(R.mipmap.blogs3);
        s.setName("Virgin Gorda beaches and lob...");
        s.setLocation("Teixeira de Freitas");
        s.setPrice("INR ₹4000");
        s.setBookedDate("05/11/2018");
        s.setDueDate("06/11/2018");
        s.setBookingID("121");
        s.setBookingCode("4697");
        list.add(s);

        s=new MyBookingsModel();
        s.setImage(R.mipmap.blogs4);
        s.setName("Virgin Gorda beaches and lob...");
        s.setLocation("Teixeira de Freitas");
        s.setPrice("INR ₹4000");
        s.setBookedDate("05/11/2018");
        s.setDueDate("06/11/2018");
        s.setBookingID("121");
        s.setBookingCode("4697");
        list.add(s);

        s=new MyBookingsModel();
        s.setImage(R.mipmap.blogs5);
        s.setName("Virgin Gorda beaches and lob...");
        s.setLocation("Teixeira de Freitas");
        s.setPrice("INR ₹4000");
        s.setBookedDate("05/11/2018");
        s.setDueDate("06/11/2018");
        s.setBookingID("121");
        s.setBookingCode("4697");
        list.add(s);

        MyBookingsAdapter bookingsAdapter = new MyBookingsAdapter(getActivity(), list);
        bRecyclerView.setAdapter(bookingsAdapter);

    }


}
