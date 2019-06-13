package oditek.com.tuurbus.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import oditek.com.tuurbus.R;
import oditek.com.tuurbus.adapter.dataholder.CountryObject;

/**
 * Created by prakash on 16/05/17.
 */

public class CountryAdapter extends ArrayAdapter<oditek.com.tuurbus.adapter.dataholder.CountryObject> implements Filterable {

    Context mContext;
    int layoutResourceId;
    TextView txtNoRcrd;
    private ArrayList<CountryObject> CountryObject;
    private List<CountryObject> countryListFiltered;

    public CountryAdapter(Context mContext, int layoutResourceId, ArrayList<CountryObject> CountryObject, TextView txtNoRcrd) {

        super(mContext, layoutResourceId, CountryObject);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.CountryObject = CountryObject;
        this.txtNoRcrd = txtNoRcrd;
        this.countryListFiltered = CountryObject;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }
        CountryObject objectItem = countryListFiltered.get(position);

        TextView countryName = convertView.findViewById(R.id.countryName);
        TextView countryCode = convertView.findViewById(R.id.countryCode);

        countryName.setText(objectItem.getCountryName());
        countryCode.setText(objectItem.getCountryDialCode());

        return convertView;

    }
    @Override
    public int getCount() {
        return countryListFiltered.size();
    }

    @Override
    public CountryObject getItem(int position) {
        return countryListFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    countryListFiltered = CountryObject;
                } else {
                    List<CountryObject> filteredList = new ArrayList<>();
                    for (CountryObject row : CountryObject) {
                        if (row.getCountryName().toLowerCase().contains(charString.toLowerCase()) || row.getCountryDialCode().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    countryListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = countryListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                try {
                    countryListFiltered = (ArrayList<CountryObject>) filterResults.values;
                    notifyDataSetChanged();
                    if (countryListFiltered.size() > 0) {
                        txtNoRcrd.setVisibility(View.GONE);
                    } else {
                        txtNoRcrd.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        };
    }

}
