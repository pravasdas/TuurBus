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

import oditek.com.tuurbus.adapter.VrentalsLuxuryAdapter;
import oditek.com.tuurbus.adapter.dataholder.VrentalsLuxuryModel;


public class VrentalsLuxury extends Fragment {
    CardView cardView;
    RecyclerView mRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_luxury, container, false);

        cardView = (CardView)view.findViewById(R.id.card_view);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        getLuxury();

        return view;
    }

    private void getLuxury() {
        ArrayList<VrentalsLuxuryModel> list = new ArrayList<>();

        VrentalsLuxuryModel s = new VrentalsLuxuryModel();
        s.setImage(R.mipmap.car1);
        s.setName("TOYOTA ETIOS");
        s.setLocation("Agra");
        s.setGuest("2");
        s.setGear("Manual");
        s.setDoors("4");
        s.setBaggage("x1");
        list.add(s);

        s = new VrentalsLuxuryModel();
        s.setImage(R.mipmap.car2);
        s.setName("DATSUN");
        s.setLocation("Gangtok");
        s.setGuest("4");
        s.setGear("Automatic");
        s.setDoors("2");
        s.setBaggage("x1");
        list.add(s);

        s = new VrentalsLuxuryModel();
        s.setImage(R.mipmap.car3);
        s.setName("Ford Focus");
        s.setLocation("Gangtok");
        s.setGuest("4");
        s.setGear("Automatic");
        s.setDoors("2");
        s.setBaggage("x1");
        list.add(s);

        s = new VrentalsLuxuryModel();
        s.setImage(R.mipmap.car4);
        s.setName("DATSUN");
        s.setLocation("Gangtok");
        s.setGuest("4");
        s.setGear("Automatic");
        s.setDoors("2");
        s.setBaggage("x1");
        list.add(s);

        s = new VrentalsLuxuryModel();
        s.setImage(R.mipmap.car2);
        s.setName("TOYOTA ETIOS");
        s.setLocation("Manali");
        s.setGuest("4");
        s.setGear("Manual");
        s.setDoors("2");
        s.setBaggage("x1");
        list.add(s);

        s = new VrentalsLuxuryModel();
        s.setImage(R.mipmap.car2);
        s.setName("DATSUN");
        s.setLocation("Mysore");
        s.setGuest("4");
        s.setGear("Manual");
        s.setDoors("2");
        s.setBaggage("x1");
        list.add(s);


        VrentalsLuxuryAdapter customAdapter = new VrentalsLuxuryAdapter(getActivity(), list);
        mRecyclerView.setAdapter(customAdapter);

    }
}
