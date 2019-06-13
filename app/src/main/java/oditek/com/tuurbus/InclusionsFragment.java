package oditek.com.tuurbus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import oditek.com.tuurbus.adapter.InclusionsAdapter;
import oditek.com.tuurbus.adapter.dataholder.Inclusions_ExclusionsModel;

public class InclusionsFragment extends Fragment {
    CardView cardView;
    RecyclerView mRecyclerView;
    String data = "";
    JSONArray jsonArray;

    public static InclusionsFragment newInstance(JSONArray jsonArray) {
        InclusionsFragment f = new InclusionsFragment();
        Bundle args = new Bundle();
        args.putString("ArrayString", jsonArray.toString());
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inclusions, container, false);

        cardView = (CardView) view.findViewById(R.id.card_view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        Bundle args = getArguments();
        if (args != null) {
            data = args.getString("ArrayString");
            System.out.println(data);
        }
        try {
            jsonArray = new JSONArray(data);
            InclusionsAdapter inclusionsAdapter = new InclusionsAdapter(getActivity(), getInclusionData(jsonArray));
            mRecyclerView.setAdapter(inclusionsAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void getDetails() {
        ArrayList<Inclusions_ExclusionsModel> list = new ArrayList<>();

        Inclusions_ExclusionsModel s = new Inclusions_ExclusionsModel();
        s.setDetails("Cab facilities");
        list.add(s);

        s = new Inclusions_ExclusionsModel();
        s.setDetails("Local taxi");
        list.add(s);

        s = new Inclusions_ExclusionsModel();
        s.setDetails("Complimentary Breakfast");
        list.add(s);

        s = new Inclusions_ExclusionsModel();
        s.setDetails("Local Guide/Maps");
        list.add(s);

        s = new Inclusions_ExclusionsModel();
        s.setDetails("Transportation");
        list.add(s);

        s = new Inclusions_ExclusionsModel();
        s.setDetails("Parking");
        list.add(s);

        s = new Inclusions_ExclusionsModel();
        s.setDetails("Driver");
        list.add(s);

        s = new Inclusions_ExclusionsModel();
        s.setDetails("Hotel pick up & drop off");
        list.add(s);

        s = new Inclusions_ExclusionsModel();
        s.setDetails("Guides/Assistance");
        list.add(s);

        s = new Inclusions_ExclusionsModel();
        s.setDetails("Round-trip Hotel Transfers");
        list.add(s);

        s = new Inclusions_ExclusionsModel();
        s.setDetails("Service fee for tour guide");
        list.add(s);

        InclusionsAdapter inclusionsAdapter = new InclusionsAdapter(getActivity(), list);
        mRecyclerView.setAdapter(inclusionsAdapter);
    }

    private List<Inclusions_ExclusionsModel> getInclusionData(JSONArray jsonArray) {
        List<Inclusions_ExclusionsModel> inclusionData = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    Inclusions_ExclusionsModel rowData = new Inclusions_ExclusionsModel();
                    JSONObject ltData = jsonArray.getJSONObject(i);

                    rowData.inclusionId = ltData.getString("id");
                    rowData.details = ltData.getString("name");
                    rowData.iconUrl = ltData.getString("icon");

                    inclusionData.add(rowData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return inclusionData;
    }
}
