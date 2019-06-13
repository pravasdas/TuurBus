package oditek.com.tuurbus.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ApiHelper extends AsyncTask<String, String, Object> {

    ServiceHandler jsonParser = new ServiceHandler();
    ProgressDialog dialog;

    Context context;
    String callReference;
    boolean showDialog_;
    String fname_;
    String lname_;
    String emailID_;
    String phone_;
    String password_;
    String confirmPassword_,con_code;
    String loginemailID_;
    String loginPassword_;
    String userID_;
    String profileFname_;
    String profileLname_;
    String profilePhone_;
    String profileAge_;
    String profileGender_;
    String profileTravelpref_;
    String profileEmailID_;
    String profilePassword_;
    String profileAddress1_;
    String profileAddress2_;
    String profileCity_;
    String profileState_;
    String profilePostalCode_;
    String profileCountry_;

    public ApiHelper(Context context, String action,String fname,String lname,String emailID,
                     String phone,String password,String con_code, TaskDelegate delegate) {
        this.context = context;
        this.callReference = action;
        this.delegate = delegate;
        this.fname_=fname;
        this.lname_=lname;
        this.emailID_=emailID;
        this.phone_=phone;
        this.password_=password;
        this.con_code=con_code;
    }
    public ApiHelper(Context context, String action, TaskDelegate delegate) {
        this.context = context;
        this.callReference = action;
        this.delegate = delegate;
    }
    public ApiHelper(Context context, String action,String userID, TaskDelegate delegate) {
        this.context = context;
        this.callReference = action;
        this.delegate = delegate;
        this.userID_=userID;
    }

    public ApiHelper(Context context, String action,String userId, String firstname,String lastname,String phone,
                     String age,String gender,String travel_preferences,String email,String password,String address1,
                     String address2,String city,String state,String zip,String country,TaskDelegate delegate) {
        this.context = context;
        this.callReference = action;
        this.delegate = delegate;
        this.userID_ = userId;
        this.profileFname_=firstname;
        this.profileLname_=lastname;
        this.profilePhone_=phone;
        this.profileAge_=age;
        this.profileGender_=gender;
        this.profileTravelpref_=travel_preferences;
        this.profileEmailID_=email;
        this.profilePassword_=password;
        this.profileAddress1_=address1;
        this.profileAddress2_=address2;
        this.profileCity_=city;
        this.profileState_=state;
        this.profilePostalCode_=zip;
        this.profileCountry_=country;

    }

    private TaskDelegate delegate;

    public interface TaskDelegate {

        void onTaskFisnishGettingData(Object result);
    }

    @Override
    protected void onPreExecute() {
        try {
            showDialog_ = true;
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected Object doInBackground(String... args) {
        String response = null;
        JSONObject jsonObject = null;
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            if (AllStaticVariables.apiAction.signup.equalsIgnoreCase(callReference)) {
                nameValuePairs.add(new BasicNameValuePair("first_name", fname_));
                nameValuePairs.add(new BasicNameValuePair("last_name", lname_));
                nameValuePairs.add(new BasicNameValuePair("email", emailID_));
                nameValuePairs.add(new BasicNameValuePair("con_code", con_code));
                nameValuePairs.add(new BasicNameValuePair("phone", phone_));
                nameValuePairs.add(new BasicNameValuePair("password", password_));
                response = jsonParser.makeServiceCall(ApiClient.signup, ServiceHandler.POST, nameValuePairs);
                jsonObject = new JSONObject(response);
                System.out.println("nameValuePairs========" + nameValuePairs);
            } else if (AllStaticVariables.apiAction.myprofile.equalsIgnoreCase(callReference)) {
                nameValuePairs.add(new BasicNameValuePair("id", userID_));
                response = jsonParser.makeServiceCall(ApiClient.myprofile, ServiceHandler.POST, nameValuePairs);
                jsonObject = new JSONObject(response);
                System.out.println("nameValuePairs========" + nameValuePairs);
            } else if (AllStaticVariables.apiAction.updateprofile.equalsIgnoreCase(callReference)) {
                nameValuePairs.add(new BasicNameValuePair("id", userID_));
                nameValuePairs.add(new BasicNameValuePair("firstname", profileFname_));
                nameValuePairs.add(new BasicNameValuePair("lastname", profileLname_));
                nameValuePairs.add(new BasicNameValuePair("phone", profilePhone_));
                nameValuePairs.add(new BasicNameValuePair("gender", profileGender_));
                nameValuePairs.add(new BasicNameValuePair("travel_preferences", profileTravelpref_));
                nameValuePairs.add(new BasicNameValuePair("email", profileEmailID_));
                nameValuePairs.add(new BasicNameValuePair("password", profilePassword_));
                nameValuePairs.add(new BasicNameValuePair("address1", profileAddress1_));
                nameValuePairs.add(new BasicNameValuePair("address2", profileAddress2_));
                nameValuePairs.add(new BasicNameValuePair("city", profileCity_));
                nameValuePairs.add(new BasicNameValuePair("state", profileState_));
                nameValuePairs.add(new BasicNameValuePair("zip", profilePostalCode_));
                nameValuePairs.add(new BasicNameValuePair("age", profileAge_));
                nameValuePairs.add(new BasicNameValuePair("country", profileCountry_));
                response = jsonParser.makeServiceCall(ApiClient.updateprofile, ServiceHandler.POST, nameValuePairs);
                jsonObject = new JSONObject(response);
                System.out.println("nameValuePairs========" + nameValuePairs);
            } else if ("filtersearch".equalsIgnoreCase(callReference)) {
                response = jsonParser.makeServiceCall(ApiClient.filtersearch_url, ServiceHandler.GET);
                jsonObject = new JSONObject(response);
                System.out.println("nameValuePairs========" + nameValuePairs);
            }else if ("mywishlist".equalsIgnoreCase(callReference)) {
                nameValuePairs.add(new BasicNameValuePair("id", userID_));
                response = jsonParser.makeServiceCall(ApiClient.mywishlist_url, ServiceHandler.POST, nameValuePairs);
                jsonObject = new JSONObject(response);
                System.out.println("nameValuePairs========" + nameValuePairs);
            }else if ("remove_wishlist".equalsIgnoreCase(callReference)) {
                nameValuePairs.add(new BasicNameValuePair("wish_id", userID_));
                response = jsonParser.makeServiceCall(ApiClient.remove_wishlist_url, ServiceHandler.POST, nameValuePairs);
                jsonObject = new JSONObject(response);
                System.out.println("nameValuePairs========" + nameValuePairs);
            }
            Log.e("response", response);
            return jsonObject;

        } catch (Exception e1) {
            System.out.println(e1);
            return null;
        }
    }


    protected void onPostExecute(Object result) {
        try {
            if ((dialog != null) && dialog.isShowing() && showDialog_) {
                dialog.dismiss();
            }
            if (delegate != null)
                delegate.onTaskFisnishGettingData(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

