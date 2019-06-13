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
import oditek.com.tuurbus.adapter.dataholder.Inclusions_ExclusionsModel;
import oditek.com.tuurbus.webservices.VolleySingleton;


public class InclusionsAdapter extends RecyclerView.Adapter<InclusionsAdapter.ViewHolder> {
    private Context mContext;
    List<Inclusions_ExclusionsModel> list;
    private ImageLoader imageLoader;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_inclusions_rowlayout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public InclusionsAdapter(Context mContext, List<Inclusions_ExclusionsModel> list) {
        this.mContext = mContext;
        this.list = list;
        imageLoader = VolleySingleton.getInstance().getImageLoader();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Inclusions_ExclusionsModel model = list.get(position);
        holder.tvDetails.setText(model.details);
        /*imageLoader.get(model.iconUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.icon.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });*/

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDetails;
        public CardView mCardView;
        public ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDetails = itemView.findViewById(R.id.tvDetails);
            icon = itemView.findViewById(R.id.ivIcon);
            mCardView = itemView.findViewById(R.id.card_view);

        }

    }

}