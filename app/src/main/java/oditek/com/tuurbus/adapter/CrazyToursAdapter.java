package oditek.com.tuurbus.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import oditek.com.tuurbus.CrazyToursDetails;
import oditek.com.tuurbus.R;
import oditek.com.tuurbus.adapter.dataholder.CrazyToursModel;
import oditek.com.tuurbus.webservices.VolleySingleton;

public class CrazyToursAdapter extends RecyclerView.Adapter<CrazyToursAdapter.ViewHolder> {
    private Context mContext;
    List<CrazyToursModel> listCT;
    private ImageLoader imageLoader;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.crazy_tours_rowlayout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public CrazyToursAdapter(Context mContext, List<CrazyToursModel> list) {
        this.mContext = mContext;
        this.listCT = list;
        imageLoader = VolleySingleton.getInstance().getImageLoader();

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CrazyToursModel model = listCT.get(position);
        holder.tvName.setText(model.name);
        holder.tvPrice.setText(model.currCode + " " + model.currSymbol + " " + model.price);
        holder.tvDetails.setText(model.details);
        imageLoader.get(model.imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.ivPic.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, CrazyToursDetails.class);
                intent.putExtra("tourID_", model.tourId);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listCT.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName,tvPrice,tvDetails;
        public ImageView ivPic;
        public CardView mCardView;
        public ViewHolder(View itemView) {
            super(itemView);
            ivPic = (ImageView) itemView.findViewById(R.id.ivPic);
            tvName=(TextView)itemView.findViewById(R.id.tv1);
            tvPrice=(TextView)itemView.findViewById(R.id.tv2);
            tvDetails=(TextView)itemView.findViewById(R.id.tv3);

            mCardView = (CardView) itemView.findViewById(R.id.card_view);

        }

    }

}

