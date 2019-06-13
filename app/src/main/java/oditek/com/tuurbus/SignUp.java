package oditek.com.tuurbus;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import oditek.com.tuurbus.adapter.CountryAdapter;
import oditek.com.tuurbus.adapter.dataholder.CountryObject;
import oditek.com.tuurbus.util.AllStaticVariables;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.util.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    Button signUpButton;
    EditText editText;
    private TextView txtCountryCode;
    TextView textView;
    EditText etFName;
    private ArrayList<CountryObject> countryList;
    EditText etLName, etEmailID, etPhone, etPassword, etConfirmPassword;
    private String firstNameVal_, lastNameVal, emailval_, mobileVal_, passwordVal_, cnfPasswordval_;
    private TextInputLayout etPasswordLayout, etRePasswordLayout;
    LinearLayout ll5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpButton = findViewById(R.id.signUp_button);
        textView = findViewById(R.id.tv2);

        etFName = findViewById(R.id.et1);
        etLName = findViewById(R.id.et2);
        etEmailID = findViewById(R.id.et3);
        etPhone = findViewById(R.id.et4);
        etPassword = findViewById(R.id.et5);
        etConfirmPassword = findViewById(R.id.et6);
        etPasswordLayout = (TextInputLayout) findViewById(R.id.etPasswordLayout);
        etRePasswordLayout = (TextInputLayout) findViewById(R.id.etRePasswordLayout);
        txtCountryCode = (TextView) findViewById(R.id.countryCode);
        ll5 = findViewById(R.id.ll5);

        countryCodeList();
        getCountryDialCode(this);
        if (getCountryDialCode(this) != null) {
            txtCountryCode.setText("+" + getCountryDialCode(this));
        } else {
            txtCountryCode.setText("+Code");
        }

        ll5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popUpDialogCountry();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(SignUp.this,Login.class);
//                startActivity(intent);

                emailval_ = etEmailID.getText().toString().trim();
                mobileVal_ = etPhone.getText().toString().trim();
                passwordVal_ = etPassword.getText().toString().trim();
                cnfPasswordval_=etConfirmPassword.getText().toString().trim();
//                new ApiHelper(SignUp.this, AllStaticVariables.apiAction.signin,
//                        email,phn,signInListener).execute();

                if (etFName.getText().toString().trim().length() == 0) {
                    etFName.setError(getResources().getString(R.string.error_name));

                } else if (etLName.getText().toString().trim().length() == 0) {
                    etLName.setError(getResources().getString(R.string.error_last_name));

                } else if (etEmailID.getText().toString().trim().length() == 0) {
                    etEmailID.setError(getResources().getString(R.string.error_email));

                } else if (!isValidEmail(emailval_)) {
                    etEmailID.setError(getResources().getString(R.string.error_email2));

                } else if (etPhone.getText().toString().trim().length() == 0) {
                    etPhone.setError(getResources().getString(R.string.error_mobile));
                } else if (!isValidPhoneNumber(mobileVal_)) {
                    etPhone.setError(getResources().getString(R.string.error_mobile2));


                } else if (etPassword.getText().toString().trim().length() == 0) {
                    etPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                    etRePasswordLayout.setPasswordVisibilityToggleEnabled(true);
                    etPassword.setError(getResources().getString(R.string.error_password));

                }else if (!isValidPassword(etPassword.getText().toString().trim())) {
                        etPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                        etRePasswordLayout.setPasswordVisibilityToggleEnabled(true);
                        etPassword.setError(getResources().getString(R.string.err_msg_password_strength));


                    } else if (etConfirmPassword.getText().toString().trim().length() == 0) {
                    etPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                    etRePasswordLayout.setPasswordVisibilityToggleEnabled(true);
                    etConfirmPassword.setError(getResources().getString(R.string.error_cnf_password));

                }else if (!cnfPasswordval_.equals(etPassword.getText().toString().trim())) {
                        etPasswordLayout.setPasswordVisibilityToggleEnabled(true);
                        etRePasswordLayout.setPasswordVisibilityToggleEnabled(false);
                        etConfirmPassword.setError(getResources().getString(R.string.error_match_password));


                } else {
                    String fname_ = etFName.getText().toString();
                    String lname_ = etLName.getText().toString();
                    String emailID_ = etEmailID.getText().toString();
                    String phone_ = etPhone.getText().toString();
                    String password_ = etPassword.getText().toString();
                    String con_code = txtCountryCode.getText().toString();

                    new ApiHelper(SignUp.this, AllStaticVariables.apiAction.signup,
                            fname_, lname_, emailID_, phone_, password_, con_code, signUpListener).execute();
                }


            }

        });


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });


    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = SignUp.this.getAssets().open("countrycode.json");
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
            dialog = new Dialog(SignUp.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            dialog = new Dialog(SignUp.this);
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

        final CountryAdapter adapter = new CountryAdapter(SignUp.this, R.layout.country_item_text, countryList, txtNoRcrd);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {

                CountryObject selection = (CountryObject) adapter.getItemAtPosition(position);
                String list_countryCode_ = selection.getCountryDialCode();
                txtCountryCode.setText(list_countryCode_);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private ApiHelper.TaskDelegate signUpListener = new ApiHelper.TaskDelegate() {
        public void onTaskFisnishGettingData(Object result) {
            try {
                if (result != null) {
                    JSONObject json = (JSONObject) result;
                    System.out.println("json----" + json);
                    String response_ = json.getString("response");

                    JSONObject error_ = json.getJSONObject("error");
                    boolean status_ = error_.getBoolean("status");
                    String msg_ = error_.getString("msg");
                    if (status_) {//true case
                        Toast.makeText(SignUp.this, msg_, Toast.LENGTH_SHORT).show();
                    } else {//false case
                        new AwesomeErrorDialog(SignUp.this)
                                .setTitle(msg_)
                                .setMessage("")
                                .setColoredCircle(R.color.white)
                                .setDialogIconOnly(R.drawable.main_logo)
                                .setCancelable(false)
                                .setButtonBackgroundColor(R.color.colorPrimaryDark)
                                .setButtonText(getString(R.string.dialog_ok_button))
                                .setErrorButtonClick(new Closure() {
                                    @Override
                                    public void exec() {
                                        Intent mIntent = new Intent(SignUp.this, OtpPage.class);
                                        mIntent.putExtra("fragmentPosition", 1);
                                        mIntent.putExtra("comingFrom", "SignUp");
                                        startActivity(mIntent);
                                        finish();
                                    }
                                })
                                .show();
                        JSONObject data_ = error_.getJSONObject("data");
                        String email = data_.getString("email");
                        String fullname = data_.getString("fullname");
                        String mobile = data_.getString("mobile");
                        String password = data_.getString("password");
                        String user_id = data_.getString("user_id");

                        //store values in sharedpreferences
                        ApiClient.saveDataWithKeyAndValue(SignUp.this, "email", email);
                        ApiClient.saveDataWithKeyAndValue(SignUp.this, "fullname", fullname);
                        ApiClient.saveDataWithKeyAndValue(SignUp.this, "mobile", mobile);
                        ApiClient.saveDataWithKeyAndValue(SignUp.this, "password", password);
                        ApiClient.saveDataWithKeyAndValue(SignUp.this, "user_id", user_id);
//                        Intent intent=new Intent(SignUp.this,Login.class);
//                        startActivity(intent);
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

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=\\S+$).{8,20}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    private class PasswordTextWatcher implements TextWatcher {

        private View view;

        private PasswordTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable editable) {
            if (etPassword.getText().toString().length() == 0) {
                etPasswordLayout.setPasswordVisibilityToggleEnabled(true);
            } else {
                if (!isValidPassword(etPassword.getText().toString().trim())) {
                    etPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                    etPassword.setError(getResources().getString(R.string.err_msg_password_strength));
                } else {
                    etPasswordLayout.setPasswordVisibilityToggleEnabled(true);
                }
            }
        }
    }


}


