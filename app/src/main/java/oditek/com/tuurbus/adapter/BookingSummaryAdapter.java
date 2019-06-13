package oditek.com.tuurbus.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import oditek.com.tuurbus.R;
import oditek.com.tuurbus.adapter.dataholder.BookingSummaryModel;


public class BookingSummaryAdapter extends RecyclerView.Adapter<BookingSummaryAdapter.ViewHolder> {
    private Context mContext;
    List<BookingSummaryModel> list;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_summary_rowlayout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public BookingSummaryAdapter(Context mContext, List<BookingSummaryModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final BookingSummaryModel bookingsummaryModel = list.get(position);

        holder.name.setText(bookingsummaryModel.getName());
        holder.passport.setText(bookingsummaryModel.getPassport());
        holder.age.setText(bookingsummaryModel.getAge());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public EditText name, passport, age;
        public CardView mCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            name =itemView.findViewById(R.id.etName);
            passport =itemView.findViewById(R.id.etPassport);
            age =itemView.findViewById(R.id.etAge);

            mCardView = (CardView) itemView.findViewById(R.id.card_view);
        }

    }

}
