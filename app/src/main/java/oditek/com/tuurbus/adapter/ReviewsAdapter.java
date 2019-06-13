package oditek.com.tuurbus.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import oditek.com.tuurbus.R;
import oditek.com.tuurbus.adapter.dataholder.ReviewsModel;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private Context mContext;
    List<ReviewsModel> list;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_rowlayout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public ReviewsAdapter(Context mContext, List<ReviewsModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReviewsModel model = list.get(position);

        holder.tvDate.setText(model.date);
        holder.tvRating.setText(model.rating);
        holder.tvName.setText(model.name);
        holder.tvDetails.setText(model.details);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDate,tvRating,tvName,tvDetails;
        public ImageView ivR;
        public CardView mCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            ivR = (ImageView) itemView.findViewById(R.id.ivR);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvRating = (TextView) itemView.findViewById(R.id.tvRating);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDetails = (TextView) itemView.findViewById(R.id.tvDetails);

            mCardView = (CardView) itemView.findViewById(R.id.mCardView);

        }

    }

}

