package oditek.com.tuurbus.adapter;

import android.content.Context;
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

import oditek.com.tuurbus.R;
import oditek.com.tuurbus.adapter.dataholder.HomeModel;
import oditek.com.tuurbus.webservices.VolleySingleton;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private Context mContext;
    private ImageLoader imageLoader;
    List<HomeModel> list;
    String data="";


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_rowlayout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public HomeAdapter(Context mContext, List<HomeModel> list, String data) {
        this.mContext = mContext;
        imageLoader = VolleySingleton.getInstance().getImageLoader();
        this.list = list;
        this.data = data;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        HomeModel model = list.get(position);
        //holder.image.setImageResource(image);
        holder.tvName.setText(model.name);
        holder.tvAdd.setText(model.add);
        if(data.equalsIgnoreCase("vehicle_rental")){
            holder.tvRate.setText(model.rate);
            holder.tvRate.setVisibility(View.VISIBLE);
        }else {
            holder.tvRate.setVisibility(View.GONE);
        }
        imageLoader.get(model.imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.image.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName,tvAdd,tvRate;
        public ImageView image;
        public CardView mCardView;
        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv);
            tvName=(TextView)itemView.findViewById(R.id.tvName);
            tvAdd=(TextView)itemView.findViewById(R.id.tvAdd);
            tvRate=(TextView)itemView.findViewById(R.id.tvRate);

            mCardView = (CardView) itemView.findViewById(R.id.card_view);

        }

    }

}
