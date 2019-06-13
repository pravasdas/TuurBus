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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import oditek.com.tuurbus.PackageBookNow;
import oditek.com.tuurbus.R;
import oditek.com.tuurbus.adapter.dataholder.PackageVehicleModel;
import oditek.com.tuurbus.webservices.VolleySingleton;


public class PackageVehicleAdapter extends RecyclerView.Adapter<PackageVehicleAdapter.ViewHolder> {
    private Context mContext;
    private ImageLoader imageLoader;
    List<PackageVehicleModel> list2;
    private String tourId_ = "", displaydate = "";;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_vehicle_rowlayout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public PackageVehicleAdapter(Context mContext, List<PackageVehicleModel> list2, String tourId) {
        this.mContext = mContext;
        this.list2 = list2;
        this.tourId_ = tourId;
        imageLoader = VolleySingleton.getInstance().getImageLoader();

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PackageVehicleModel model = list2.get(position);
        holder.vehiclerate.setText(model.currSymbol+ " " + model.vehiclerate);

        if(model.scrolldate.equalsIgnoreCase("")){
            holder.scrolldate.setText("NA");
            displaydate = "";
        }else{
            List<String> myList = new ArrayList<String>(Arrays.asList(model.scrolldate.split(",")));
            //System.out.println(myList);
            for(int i = 0; i < myList.size(); i++){
                String depDate = myList.get(i);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date strDate = null;
                Date currentDate = new Date();
                //System.out.println(currentDate);
                try {
                    strDate = sdf.parse(depDate);
                    //System.out.println(strDate);
                    //String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                    if (strDate.after(currentDate)) {
                        displaydate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(strDate);
                        holder.scrolldate.setText(displaydate);
                        break;
                    }
                    else{
                        holder.scrolldate.setText("NA");
                        displaydate = "";
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        imageLoader.get(model.image_url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.imagevehicle.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        holder.mCardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,PackageBookNow.class);
                intent.putExtra("TourID_", tourId_);
                intent.putExtra("VehicleImage_", model.image_url);
                intent.putExtra("VehiclePrice_", model.vehiclerate);
                if(holder.scrolldate.getText().toString().equalsIgnoreCase("NA")){
                    intent.putExtra("AvlDates_", "");
                }else{
                    intent.putExtra("AvlDates_", holder.scrolldate.getText().toString());
                }
                intent.putExtra("CurrSymbol_", model.currSymbol);
                intent.putExtra("VehicleName_", model.vehicleName);
                intent.putExtra("vehicleId_", model.vehicleId);
                intent.putExtra("DateString", model.scrolldate);
                intent.putExtra("From_", "PackageVehicleAdapter");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list2.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView vehiclerate, scrolldate;
        public ImageView imagevehicle, calender;
        public CardView mCardView2;

        public ViewHolder(View itemView) {
            super(itemView);
            imagevehicle = (ImageView) itemView.findViewById(R.id.scroll_vehicle);
            vehiclerate = (TextView) itemView.findViewById(R.id.scroll_price);
            calender = (ImageView) itemView.findViewById(R.id.scroll_calender);
            scrolldate = (TextView) itemView.findViewById(R.id.scroll_date);
            mCardView2 = (CardView) itemView.findViewById(R.id.card_view2);
        }

    }
}
