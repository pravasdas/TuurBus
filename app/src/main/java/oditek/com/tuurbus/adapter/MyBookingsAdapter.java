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
import oditek.com.tuurbus.adapter.dataholder.MyBookingsModel;

public class MyBookingsAdapter extends RecyclerView.Adapter<MyBookingsAdapter.ViewHolder> {
    private Context mContext;

    List<MyBookingsModel> list;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mybooking_rowlayout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public MyBookingsAdapter(Context mContext, List<MyBookingsModel> list) {
        this.mContext = mContext;
        this.list = list;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyBookingsModel model = list.get(position);
        final int image = model.getImage();
        final String name = model.getName();
        final String location = model.getLocation();
        final String price = model.getPrice();
        final String bookedDate = model.getBookedDate();
        final String dueDate = model.getDueDate();
        final String bookingID = model.getBookingID();
        final String bookingCode = model.getBookingCode();


        holder.image.setImageResource(image);
        holder.name.setText(name);
        holder.location.setText(location);
        holder.price.setText(price);
        holder.bookedDate.setText(bookedDate);
        holder.dueDate.setText(dueDate);
        holder.bookingID.setText(bookingID);
        holder.bookingCode.setText(bookingCode);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name,location,price,bookedDate,dueDate,bookingID,bookingCode;
        public ImageView image;
        public CardView mCardView;
        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv1);
            name = (TextView) itemView.findViewById(R.id.tv1);
            location=(TextView) itemView.findViewById(R.id.tv2);
            price=(TextView) itemView.findViewById(R.id.tv3);
            bookedDate=(TextView) itemView.findViewById(R.id.tv4);
            dueDate=(TextView) itemView.findViewById(R.id.tv5);
            bookingID=(TextView) itemView.findViewById(R.id.tv6);
            bookingCode=(TextView) itemView.findViewById(R.id.tv7);


            mCardView = (CardView) itemView.findViewById(R.id.card_view);
        }

    }
}