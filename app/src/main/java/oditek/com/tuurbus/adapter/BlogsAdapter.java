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

import oditek.com.tuurbus.BlogDetails;
import oditek.com.tuurbus.adapter.dataholder.BlogModel;
import oditek.com.tuurbus.R;
import oditek.com.tuurbus.webservices.VolleySingleton;

public class BlogsAdapter extends RecyclerView.Adapter<BlogsAdapter.ViewHolder> {
    private Context mContext;
    private ImageLoader imageLoader;
    List<BlogModel> list;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.blogs_rowlayout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public BlogsAdapter(Context mContext, List<BlogModel> list) {
        this.mContext = mContext;
        this.list = list;
        imageLoader = VolleySingleton.getInstance().getImageLoader();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final BlogModel blogmodel = list.get(position);

        holder.name.setText(blogmodel.name);
        holder.date.setText(blogmodel.date);
        holder.details.setText(blogmodel.details);

        imageLoader.get(blogmodel.imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.image.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, BlogDetails.class);
                intent.putExtra("blogId_", blogmodel.blogId);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, details,date;
        public ImageView image;
        public CardView mCardView;
        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv1);
            name = (TextView) itemView.findViewById(R.id.tv1);
            date = (TextView) itemView.findViewById(R.id.tv2);
            details = (TextView) itemView.findViewById(R.id.tv3);

            mCardView = (CardView) itemView.findViewById(R.id.card_view);
        }

    }

}