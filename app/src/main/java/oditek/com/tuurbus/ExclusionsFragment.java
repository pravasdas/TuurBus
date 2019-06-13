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

import oditek.com.tuurbus.adapter.ExclusionsAdapter;
import oditek.com.tuurbus.adapter.InclusionsAdapter;
import oditek.com.tuurbus.adapter.dataholder.Inclusions_ExclusionsModel;

public class ExclusionsFragment extends Fragment {
    CardView cardView;
    RecyclerView mRecyclerView;
    String data = "";
    JSONArray jsonArray;

    public static ExclusionsFragment newInstance(JSONArray jsonArray) {
        ExclusionsFragment f = new ExclusionsFragment();
        Bundle args = new Bundle();
        args.putString("ArrayString", jsonArray.toString());
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exclusions, container, false);

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
            ExclusionsAdapter exclusionsAdapter = new ExclusionsAdapter(getActivity(), getExclusionData(jsonArray));
            mRecyclerView.setAdapter(exclusionsAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private List<Inclusions_ExclusionsModel> getExclusionData(JSONArray jsonArray) {
        List<Inclusions_ExclusionsModel> exclusionData = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    Inclusions_ExclusionsModel rowData = new Inclusions_ExclusionsModel();
                    JSONObject ltData = jsonArray.getJSONObject(i);

                    rowData.exclusionId = ltData.getString("id");
                    rowData.details = ltData.getString("name");
                    rowData.iconUrl = ltData.getString("icon");

                    exclusionData.add(rowData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return exclusionData;
    }
}
