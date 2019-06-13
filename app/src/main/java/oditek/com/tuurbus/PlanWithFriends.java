package oditek.com.tuurbus;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import oditek.com.tuurbus.adapter.PlanWithFriendsAdapter;
import oditek.com.tuurbus.adapter.dataholder.PlanWithFriendsModel;

public class PlanWithFriends extends AppCompatActivity {

    CardView cardView;
    RecyclerView mRecyclerView;
    List<PlanWithFriendsModel> list = new ArrayList<>();
    LinearLayout llAddNew,llDelete;
    Button btnAdd,btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_with_friends);

        llAddNew=findViewById(R.id.llAddNew);
        llDelete=findViewById(R.id.llDelete);
        btnAdd=findViewById(R.id.btnAdd);
        btnDelete=findViewById(R.id.btnDelete);

        cardView = (CardView)findViewById(R.id.card_view);
        mRecyclerView= (RecyclerView)findViewById(R.id.recyclerViewPwf);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        getData();

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void onAddField(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.plan_with_friends_delete, null);
        // Add the new row before the add field button.
        llAddNew.addView(rowView, llAddNew.getChildCount() - 1);
    }

    public void onDelete(View v) {
        //llAddNew.removeView((View) v.getParent());
        ViewGroup layout = (ViewGroup) findViewById(R.id.llAddNew);
        View command = layout.findViewById(R.id.childMainLayout);
        layout.removeView(command);
    }

    private void getData()
    {
        ArrayList<PlanWithFriendsModel> list=new ArrayList<>();

        PlanWithFriendsModel s=new PlanWithFriendsModel();
        s.setEmail("");
        s.setMobile("");
        list.add(s);

        PlanWithFriendsAdapter pwfAdapter = new PlanWithFriendsAdapter(this, list);
        mRecyclerView.setAdapter(pwfAdapter);

    }
}
