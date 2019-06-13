package oditek.com.tuurbus.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import oditek.com.tuurbus.R;
import oditek.com.tuurbus.adapter.dataholder.PlanWithFriendsModel;


public class PlanWithFriendsAdapter extends RecyclerView.Adapter<PlanWithFriendsAdapter.ViewHolder> {
    private Context mContext;

    List<PlanWithFriendsModel> list;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_with_friends_rowlayout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public PlanWithFriendsAdapter(Context mContext, List<PlanWithFriendsModel> list) {
        this.mContext = mContext;
        this.list = list;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlanWithFriendsModel PlanWithFriendsModel = list.get(position);
        final String email = PlanWithFriendsModel.getEmail();
        final String mobile = PlanWithFriendsModel.getMobile();

        holder.etEmail.setText(email);
        holder.etMobile.setText(mobile);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView etEmail, etMobile;
        public Button btnAdd,btnDelete;
        public CardView mCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            etEmail = (TextView) itemView.findViewById(R.id.etEmail);
            etMobile = (TextView) itemView.findViewById(R.id.etMobile);

            btnAdd = (Button) itemView.findViewById(R.id.btnAdd);
            btnDelete = (Button) itemView.findViewById(R.id.btnDelete);

            mCardView = (CardView) itemView.findViewById(R.id.card_view);
        }

    }

}
