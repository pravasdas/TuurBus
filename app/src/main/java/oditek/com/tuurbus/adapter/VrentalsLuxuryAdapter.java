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
import oditek.com.tuurbus.adapter.dataholder.VrentalsLuxuryModel;

public class VrentalsLuxuryAdapter extends RecyclerView.Adapter<VrentalsLuxuryAdapter.ViewHolder> {
    private Context mContext;
    List<VrentalsLuxuryModel> list;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.v_rentals_luxury_rowlayout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public VrentalsLuxuryAdapter(Context mContext, List<VrentalsLuxuryModel> list) {
        this.mContext = mContext;
        this.list = list;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VrentalsLuxuryModel model = list.get(position);
        final int image = model.getImage();
        final String name = model.getName();
        final String location = model.getLocation();
        final String guest = model.getGuest();
        final String gear = model.getGear();
        final String doors = model.getDoors();
        final String baggage = model.getBaggage();



        //holder.ivPic.setImageResource(image);
        holder.tvName.setText(name);
        holder.tvLocation.setText(location);
        holder.tvGuest.setText(guest);
        holder.tvGear.setText(gear);
        holder.tvDoors.setText(doors);
        holder.tvBaggage.setText(baggage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvLocation, tvGuest,tvGear,tvDoors,tvBaggage;
        public ImageView ivPic;
        public CardView mCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPic = (ImageView) itemView.findViewById(R.id.ivPic);
            tvName = (TextView) itemView.findViewById(R.id.tv1);
            tvLocation = (TextView) itemView.findViewById(R.id.tv2);
            tvGuest = (TextView) itemView.findViewById(R.id.tvGuest);
            tvGear = (TextView) itemView.findViewById(R.id.tvGear);
            tvDoors = (TextView) itemView.findViewById(R.id.tvDoors);
            tvBaggage = (TextView) itemView.findViewById(R.id.tvBaggage);


            mCardView = (CardView) itemView.findViewById(R.id.card_view);

        }

    }

}

