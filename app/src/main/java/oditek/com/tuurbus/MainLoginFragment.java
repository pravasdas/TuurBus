package oditek.com.tuurbus;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import oditek.com.tuurbus.adapter.CountryAdapter;
import oditek.com.tuurbus.adapter.dataholder.CountryObject;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.util.NetworkConnection;
import oditek.com.tuurbus.webservices.VolleySingleton;


public class MainLoginFragment extends Fragment {

    Button button,button_otp;
    TextView textView;
    EditText mobile_text;
    private TextView txtCountryCode;
    private ArrayList<CountryObject> countryList;
    LinearLayout ll5;
    int i = 0;
    String appKey = "";
    NetworkConnection nw;

    public MainLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public  View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_login, container, false);
        nw = new NetworkConnection(getActivity());
        textView = (TextView) view.findViewById(R.id.tv1);
        txtCountryCode = (TextView) view.findViewById(R.id.main_countryCode);
        mobile_text = (EditText) view.findViewById(R.id.etMobile);
        button_otp=(Button) view.findViewById(R.id.button_otp);
        ll5 =(LinearLayout) view.findViewById(R.id.ll5);

        appKey = ApiClient.getDataFromKey(getActivity(), "api_key");
        countryCodeList();

        getCountryDialCode(getActivity());
        if (getCountryDialCode(getActivity()) != null) {
            txtCountryCode.setText("+" + getCountryDialCode(getActivity()));
        } else {
            txtCountryCode.setText("+Code");
        }

        ll5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popUpDialogCountry();
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }

        });

        button_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mobile_text.getText().toString().trim().length()==0){
                    mobile_text.setError("Mobile number is required");
                }else if (!isValidPhoneNumber(mobile_text.getText().toString().trim())) {
                    mobile_text.setError(getResources().getString(R.string.error_mobile2));
                }else if(txtCountryCode.getText().toString().equalsIgnoreCase("+Code")){
                    txtCountryCode.setError("Mobile Code is required");
                }
                else{
                    if (nw.isConnectingToInternet()) {
                        syncGenerateOtp();
                    } else {
                        NoInternetDialog();
                    }
                }

            }

        });
        return view;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("countrycode.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void countryCodeList() {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("countries");
            countryList = new ArrayList<CountryObject>();

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String country_name = jo_inside.getString("name");
                String dial_code = jo_inside.getString("dial_code");
                String code_ = jo_inside.getString("code");

                CountryObject object_ = new CountryObject(country_name, code_, dial_code);
                countryList.add(object_);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getCountryDialCode(Context context) {
        String contryId = null;
        String contryDialCode = null;

        TelephonyManager telephonyMngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        contryId = telephonyMngr.getSimCountryIso().toUpperCase();
        String[] arrContryCode = context.getResources().getStringArray(R.array.DialingCountryCode);
        for (int i = 0; i < arrContryCode.length; i++) {
            String[] arrDial = arrContryCode[i].split(",");
            if (arrDial[1].trim().equals(contryId.trim())) {
                contryDialCode = arrDial[0];
                break;
            }
        }
        return contryDialCode;
    }

    private void popUpDialogCountry() {

        final Dialog dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new Dialog(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            dialog = new Dialog(getActivity());
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.country_dialog_list);
        dialog.setCancelable(false);
        TextView dialog_title = dialog.findViewById(R.id.dialog_title);
        RelativeLayout srch_main = dialog.findViewById(R.id.srch_main);
        srch_main.setVisibility(View.VISIBLE);
        EditText search_edittext = dialog.findViewById(R.id.search_edittext);
        search_edittext.setHint("Search country name/code");
        TextView txtNoRcrd = dialog.findViewById(R.id.txtNoRcrd);
        //ImageView imageView_custom_dialog_close = (ImageView) dialog.findViewById(R.id.imageView_custom_dialog_close);
        final ListView lv = (ListView) dialog.findViewById(R.id.dialoglv);

        final CountryAdapter adapter = new CountryAdapter(getActivity(), R.layout.country_item_text, countryList, txtNoRcrd);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {

                CountryObject selection = (CountryObject) adapter.getItemAtPosition(position);
                String list_countryCode_ = selection.getCountryDialCode();
                txtCountryCode.setText(list_countryCode_);
                dialog.dismiss();
            }
        });

        search_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(adapter != null){
                    adapter.getFilter().filter(s.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialog.show();
    }

    private void syncGenerateOtp(){
        String tag_json_req = "generate_otp";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.main_url) + getResources()
                        .getString(R.string.otp_url) + "generateotp/?" + "appKey=" + appKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //progressDialog.dismiss();
                            Log.d("otp response is ", response);
                            JSONObject jsonObject = new JSONObject(response);
                            String apiResponse = jsonObject.getString("response");
                            JSONObject errorObj = jsonObject.getJSONObject("error");

                            if(errorObj.getString("status").equalsIgnoreCase("false")){
                                Toast.makeText(getActivity(),errorObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), OtpPage.class);
                                intent.putExtra("comingFrom","MainLoginFragment");
                                intent.putExtra("MobileNo",mobile_text.getText().toString().trim());
                                startActivity(intent);
                            }else{
                                Toast.makeText(getActivity(),errorObj.getString("msg"), Toast.LENGTH_SHORT).show();
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    if (i < 3) {
                        Log.e("Retry due to error ", "for time : " + i);
                        i++;
                        syncGenerateOtp();
                    } else {
                        Toast.makeText(getActivity(), "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        //progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String phone = mobile_text.getText().toString().trim();
                String code = txtCountryCode.getText().toString().trim();

                Map<String, String> params = new HashMap<>();
                params.put("phone", phone);
                params.put("code", code);


                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance().getRequestQueue().add(data).addMarker(tag_json_req);

    }

    public static boolean isValidPhoneNumber(String phone) {
        boolean check;
        if (phone.length() < 10 || phone.length() > 12) {
            check = false;
        } else {
            check = true;
        }
        return check;
    }

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


}
