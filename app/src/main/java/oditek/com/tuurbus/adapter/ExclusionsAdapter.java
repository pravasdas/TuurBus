package oditek.com.tuurbus.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import oditek.com.tuurbus.R;
import oditek.com.tuurbus.adapter.dataholder.Inclusions_ExclusionsModel;

public class ExclusionsAdapter extends RecyclerView.Adapter<ExclusionsAdapter.ViewHolder> {
    private Context mContext;
    List<Inclusions_ExclusionsModel> list;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_exclusions_rowlayout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public ExclusionsAdapter(Context mContext, List<Inclusions_ExclusionsModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Inclusions_ExclusionsModel model = list.get(position);
        holder.tvDetails.setText(model.details);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDetails;
        public CardView mCardView;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDetails = itemView.findViewById(R.id.tv1);
            mCardView = itemView.findViewById(R.id.card_view);

        }

    }

}

