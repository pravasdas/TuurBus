package oditek.com.tuurbus;


import android.content.Intent;
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

import at.markushi.ui.CircleButton;
import oditek.com.tuurbus.adapter.ReviewsAdapter;
import oditek.com.tuurbus.adapter.dataholder.ReviewsModel;


public class ReviewsFragment extends Fragment {
    CardView cardView;
    RecyclerView mRecyclerView;
    CircleButton btnNewReview;
    String data = "", tourId = "", appModule = "";
    JSONArray jsonArray;

    public static ReviewsFragment newInstance(JSONArray jsonArray, String tourId, String appModule) {
        ReviewsFragment f = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putString("ArrayString", jsonArray.toString());
        args.putString("TourId", tourId);
        args.putString("AppModule", appModule);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        btnNewReview=view.findViewById(R.id.btnNewReview);
        cardView = (CardView) view.findViewById(R.id.card_view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //getDetails();

        Bundle args = getArguments();
        if (args != null) {
            data = args.getString("ArrayString");
            tourId = args.getString("TourId");
            appModule = args.getString("AppModule");
            //System.out.println(data);
        }
        try {
            jsonArray = new JSONArray(data);
            ReviewsAdapter reviewAdapter = new ReviewsAdapter(getActivity(), getReviewData(jsonArray));
            mRecyclerView.setAdapter(reviewAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnNewReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WriteReview.class);
                intent.putExtra("TourId_", tourId);
                intent.putExtra("AppModule_", appModule);
                startActivity(intent);
            }
        });


        return view;
    }

    private void getDetails() {
        ArrayList<ReviewsModel> list = new ArrayList<>();

        ReviewsModel s = new ReviewsModel();
        s.setImage(R.mipmap.tour1);
        s.setName("Swastik");
        s.setDate("27/12/2018");
        s.setRating("7.6");
        s.setDetails("Day1 Arrival at Bagdogra & transfer Gangtok(4hrs).Stroll around M.G Marg,overnight.\n\nDay2 Gangtok.-local sight seeing.\n\nDay3 To Pelling via Ravangla()45hrs.\n\nDay4 Pelling-Darap Village,Rambi Waterfalls,Rock Garden,Kunchenjungha Water Falls and Khecheopalri Lake.After lunch visit Pemayangtse Monastry,Helipad View Point,Rubduntse Palace Ruins and Bird Sanctury.\n\nDay5 Pilling To Darjeeling(4hrs),afternoon- roaming around Mall,Coffee at Glenery.\n\nDay6 Darjeeling-Tiger Hill, Ghoom Monastery, Toy Train, HMi, Tea Garden.\n\nDay7 To Kalimong.");
        list.add(s);

        s = new ReviewsModel();
        s.setImage(R.mipmap.tour2);
        s.setName("Pravas");
        s.setDate("29/12/2018");
        s.setRating("8.2");
        s.setDetails("Day1 Arrival at Bagdogra & transfer Gangtok(4hrs).Stroll around M.G Marg,overnight.\n\nDay2 Gangtok.-local sight seeing.\n\nDay3 To Pelling via Ravangla()45hrs.\n\nDay4 Pelling-Darap Village,Rambi Waterfalls,Rock Garden,Kunchenjungha Water Falls and Khecheopalri Lake.After lunch visit Pemayangtse Monastry,Helipad View Point,Rubduntse Palace Ruins and Bird Sanctury.\n\nDay5 Pilling To Darjeeling(4hrs),afternoon- roaming around Mall,Coffee at Glenery.\n\nDay6 Darjeeling-Tiger Hill, Ghoom Monastery, Toy Train, HMi, Tea Garden.\n\nDay7 To Kalimong.");
        list.add(s);

        ReviewsAdapter reviewAdapter = new ReviewsAdapter(getActivity(), list);
        mRecyclerView.setAdapter(reviewAdapter);
    }

    private List<ReviewsModel> getReviewData(JSONArray jsonArray) {
        List<ReviewsModel> reviewData = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    ReviewsModel rowData = new ReviewsModel();
                    JSONObject ltData = jsonArray.getJSONObject(i);

                    rowData.name = ltData.getString("review_by");
                    rowData.details = ltData.getString("review_comment");
                    rowData.date = ltData.getString("review_date");
                    rowData.rating = ltData.getString("rating");

                    reviewData.add(rowData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviewData;
    }
}
