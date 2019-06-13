package oditek.com.tuurbus;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import oditek.com.tuurbus.adapter.dataholder.MyWishlistModel;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.util.ApiHelper;
import oditek.com.tuurbus.util.NetworkConnection;

public class MyWishlistFragment extends Fragment {
    CardView cardView;
    TextView tvWishlist;
    RecyclerView mRecyclerView;
    NetworkConnection nw;
    String userID_ = "";
    List<MyWishlistModel> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_my_wishlist, container, false);

        nw = new NetworkConnection(getActivity());
        tvWishlist=view.findViewById(R.id.tvWishlist);
        cardView = (CardView) view.findViewById(R.id.card_view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMW);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);


        userID_ = ApiClient.getDataFromKey(getActivity(), "user_id");

        if (nw.isConnectingToInternet()) {
            new ApiHelper(getActivity(), "mywishlist", userID_, getWishlistListener).execute();
        } else {
            NoInternetDialog();
        }

        return view;
    }

    private ApiHelper.TaskDelegate getWishlistListener = new ApiHelper.TaskDelegate() {
        @Override
        public void onTaskFisnishGettingData(Object result) {
            try {
                if (result != null) {
                    list = new ArrayList<MyWishlistModel>();
                    JSONObject json = (JSONObject) result;
                    System.out.println("json----" + json);

                    JSONObject error_ = json.getJSONObject("error");
                    String status_ = error_.getString("status");
                    String msg_ = error_.getString("msg");

                    if ("false".equalsIgnoreCase(status_)) {

                        JSONArray jsonArray = json.getJSONArray("response");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject responseObj = jsonArray.getJSONObject(i);

                            String wishid = responseObj.getString("wishid");
                            String slug = responseObj.getString("slug");
                            String location = responseObj.getString("location");
                            String itemid = responseObj.getString("item_id");
                            String image = responseObj.getString("thumbnail");
                            String name = responseObj.getString("title");
                            int rating = Integer.parseInt(responseObj.getString("starcount"));
                            String date = responseObj.getString("date");
                            MyWishlistModel obj = new MyWishlistModel(name, date, image, rating,wishid);
                            list.add(obj);


                            //Length count of array
                            int number = jsonArray.length();
                            tvWishlist.setText("My Wishlist "+"[ "+jsonArray.length()+" ]");

                            //store values in sharedpreferences
                           // ApiClient.saveDataWithKeyAndValue(getActivity(), "wishid", wishid);
                        }

                        MyWishlistAdapter wishlistAdapter = new MyWishlistAdapter(getActivity(), list);
                        mRecyclerView.setAdapter(wishlistAdapter);

                    } else {
                        Toast.makeText(getActivity(), msg_, Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (ClassCastException e) {

            } catch (JSONException e) {

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    };

    public void NoInternetDialog() {
        new AwesomeErrorDialog(getActivity())
                .setTitle("No Internet!")
                .setMessage("Make sure that WI-FI or Mobile data is turned on, then try again...")
                .setCancelable(false)
                .setColoredCircle(R.color.white)
                .setDialogIconOnly(R.drawable.main_logo)
                .setCancelable(true).setButtonText(getString(R.string.dialog_ok_button))
                .setButtonBackgroundColor(R.color.colorPrimaryDark)
                .setButtonText(getString(R.string.dialog_ok_button))
                .setErrorButtonClick(new Closure() {
                    @Override
                    public void exec() {
                    }
                })
                .show();
    }



    public class MyWishlistAdapter extends RecyclerView.Adapter<MyWishlistAdapter.ViewHolder> {
        private Context mContext;
        List<MyWishlistModel> list;
        String wishID_ = "";
        NetworkConnection nw;


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_wishlist_rowlayout, parent, false);

            ViewHolder vh = new ViewHolder(v);

            return vh;
        }


        public MyWishlistAdapter(Context mContext, List<MyWishlistModel> list) {
            this.mContext = mContext;
            this.list = list;
            nw = new NetworkConnection(mContext);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,final int position) {
            MyWishlistModel model = list.get(position);
            final String imageUrl = model.getImage();
            final String name1 = model.getName();
            final int rating = model.getRating();
            final String date = model.getDate();
            final String wishid = model.getWishid();


            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.mipmap.no_image)
                    .error(R.mipmap.no_image)
                    .into(holder.ivMW);
            holder.tvName.setText(name1);
            holder.ratingbarMW.setRating(rating);
            holder.tvDate.setText(date);

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AwesomeSuccessDialog(mContext)
                            .setTitle("Do you want to remove item from wishlist?")
                            .setMessage("")
                            .setColoredCircle(R.color.white)
                            .setDialogIconOnly(R.drawable.main_logo)
                            .setCancelable(false)
                            .setPositiveButtonText("Yes")
                            .setPositiveButtonbackgroundColor(R.color.red)
                            .setPositiveButtonTextColor(R.color.white)
                            .setNegativeButtonText("No")
                            .setNegativeButtonbackgroundColor(R.color.deepgreen)
                            .setNegativeButtonTextColor(R.color.white)
                            .setPositiveButtonClick(new Closure() {
                                @Override
                                public void exec() {
                                    if (nw.isConnectingToInternet()) {
                                        new ApiHelper(mContext, "remove_wishlist", wishid, getRemoveWishlistListener).execute();
                                        System.out.println("wishId=======" + wishid);
                                        removeItem(position);
                                        new ApiHelper(getActivity(), "mywishlist", userID_, getWishlistListener).execute();
                                    } else {
                                        NoInternetDialog();
                                    }

                                }
                            })
                            .setNegativeButtonClick(new Closure() {
                                @Override
                                public void exec() {
                                    //click
                                }
                            })
                            .show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tvName, tvDate;
            public ImageView ivMW, ivDelete;
            public SimpleRatingBar ratingbarMW;
            public CardView mCardView;


            public ViewHolder(View itemView) {
                super(itemView);
                ivMW = (ImageView) itemView.findViewById(R.id.ivMW);
                ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvDate = (TextView) itemView.findViewById(R.id.tvDate);
                ratingbarMW = (SimpleRatingBar) itemView.findViewById(R.id.ratingbarMW);

                mCardView = (CardView) itemView.findViewById(R.id.mCardView);

            }

        }

        private void removeItem(int position) {
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
        }

        private ApiHelper.TaskDelegate getRemoveWishlistListener = new ApiHelper.TaskDelegate() {
            @Override
            public void onTaskFisnishGettingData(Object result) {
                try {
                    if (result != null) {
                        JSONObject json = (JSONObject) result;
                        System.out.println("json----" + json);
                        String response_ = json.getString("response");
                        JSONObject error_ = json.getJSONObject("error");
                        String status_ = error_.getString("status");
                        String msg_ = error_.getString("msg");
                        if ("false".equalsIgnoreCase(status_)) {
                            Toast.makeText(mContext, msg_, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, msg_, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (ClassCastException e) {

                } catch (JSONException e) {

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        };

    }

}
