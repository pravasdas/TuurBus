package oditek.com.tuurbus.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import oditek.com.tuurbus.R;
import oditek.com.tuurbus.webservices.VolleySingleton;

public class SliderAdapter extends PagerAdapter {
    private Context mContext;
    private List<String> image;
    private ImageLoader imageLoader;

    public SliderAdapter(Context context, List<String> image) {
        this.mContext = context;
        this.image = image;
        imageLoader = VolleySingleton.getInstance().getImageLoader();
    }

    @Override
    public int getCount() {
        return image.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_slider, null);

        final ImageView imageView =  view.findViewById(R.id.imageView);

        //imageView.setImageResource(Integer.parseInt(image.get(position)));
        imageLoader.get(image.get(position), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                imageView.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
