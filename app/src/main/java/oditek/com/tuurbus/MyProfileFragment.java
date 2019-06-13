package oditek.com.tuurbus;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import oditek.com.tuurbus.navigationdrawer.HomeSlidingActivity;
import oditek.com.tuurbus.util.AllStaticVariables;
import oditek.com.tuurbus.util.ApiClient;
import oditek.com.tuurbus.util.ApiHelper;
import oditek.com.tuurbus.webservices.VolleyMultipartRequest;
import oditek.com.tuurbus.webservices.VolleySingleton;

public class MyProfileFragment extends Fragment{

    Button submit_button;
    EditText etFname, etLname, etPhoneno, etAge, etTravelpref;
    TextView tvGender, tvprofileName;
    //ImageView ivprofilePhoto;
    CircleImageView ivprofilePhoto;
    EditText etEmail, etPassword, etConfirmPassword;
    private TextInputLayout etPasswordLayout, etRePasswordLayout;
    private String passwordVal_, cnfPasswordval_;
    EditText etAddress1, etAddress2, etCity, etState, etPostalcode, etCountry;
    int REQUEST_CAMERA = 1;
    int SELECT_FILE = 2;
    String imageFilePath = "";
    byte[] imageInByte;
    String appKey = "", userID_ = "", value = "";
    private ImageLoader imageLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        submit_button = (Button) view.findViewById(R.id.submit_button);
        etFname = (EditText) view.findViewById(R.id.et1);
        etLname = (EditText) view.findViewById(R.id.et2);
        etPhoneno = (EditText) view.findViewById(R.id.et3);
        etAge = (EditText) view.findViewById(R.id.et4);
        tvGender = (TextView) view.findViewById(R.id.tv_gender);
        etTravelpref = (EditText) view.findViewById(R.id.et6);
        etEmail = (EditText) view.findViewById(R.id.et7);
        etPassword = (EditText) view.findViewById(R.id.et8);
        etConfirmPassword = (EditText) view.findViewById(R.id.et9);
        etPasswordLayout = (TextInputLayout) view.findViewById(R.id.etPasswordLayout);
        etRePasswordLayout = (TextInputLayout) view.findViewById(R.id.etRePasswordLayout);
        etAddress1 = (EditText) view.findViewById(R.id.et10);
        etAddress2 = (EditText) view.findViewById(R.id.et11);
        etCity = (EditText) view.findViewById(R.id.et13);
        etState = (EditText) view.findViewById(R.id.et14);
        etPostalcode = (EditText) view.findViewById(R.id.et15);
        etCountry = (EditText) view.findViewById(R.id.et16);
        tvprofileName = (TextView) view.findViewById(R.id.profileName);
        ivprofilePhoto = (CircleImageView) view.findViewById(R.id.profilePhoto);
        imageLoader = VolleySingleton.getInstance().getImageLoader();

        userID_ = ApiClient.getDataFromKey(getActivity(), "user_id");
        appKey = ApiClient.getDataFromKey(getActivity(), "api_key");
        //Toast.makeText(getActivity(),"UserId"+userID_,Toast.LENGTH_SHORT).show();
        new ApiHelper(getActivity(), AllStaticVariables.apiAction.myprofile,
                userID_, getProfileDataListener).execute();

        tvGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGenderData();
            }
        });
        ivprofilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();

            }
        });


        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                passwordVal_ = etPassword.getText().toString().trim();
                cnfPasswordval_ = etConfirmPassword.getText().toString().trim();

                if (etPassword.getText().toString().trim().length() > 0) {

                    //etPassword.setError(getResources().getString(R.string.error_password));
                    if (!isValidPassword(etPassword.getText().toString().trim())) {
                        etPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                        etRePasswordLayout.setPasswordVisibilityToggleEnabled(true);
                        etPassword.setError(getResources().getString(R.string.err_msg_password_strength));

                    }

                } else if (etConfirmPassword.getText().toString().trim().length() > 0) {
                    if (!cnfPasswordval_.equals(etPassword.getText().toString().trim())) {
                        etPasswordLayout.setPasswordVisibilityToggleEnabled(true);
                        etRePasswordLayout.setPasswordVisibilityToggleEnabled(false);
                        etConfirmPassword.setError(getResources().getString(R.string.error_match_password));
                    }

                } else {
                    String profileFname_ = etFname.getText().toString();
                    String profileLname_ = etLname.getText().toString();
                    String profilePhone_ = etPhoneno.getText().toString();
                    String profileAge_ = etAge.getText().toString();
                    String profileGender_ = tvGender.getText().toString();
                    String profileTravelpref_ = etTravelpref.getText().toString();
                    String profileEmailID_ = etEmail.getText().toString();
                    String profilePassword_ = etPassword.getText().toString();
                    String profileAddress1_ = etAddress1.getText().toString();
                    String profileAddress2_ = etAddress2.getText().toString();
                    String profileCity_ = etCity.getText().toString();
                    String profileState_ = etState.getText().toString();
                    String profilePostalCode_ = etPostalcode.getText().toString();
                    String profileCountry_ = etCountry.getText().toString();


                    new ApiHelper(getActivity(), AllStaticVariables.apiAction.updateprofile,
                            profileFname_, profileLname_, profilePhone_, profileAge_, profileGender_, profileTravelpref_, profileEmailID_, profilePassword_,
                            profileAddress1_, profileAddress2_, profileCity_, profileState_, profilePostalCode_, profileCountry_, userID_, updateProfileListener).execute();
                }
            }
        });
        return view;
    }

    //This below method is to extract the profile data of user

    private ApiHelper.TaskDelegate getProfileDataListener = new ApiHelper.TaskDelegate() {
        public void onTaskFisnishGettingData(Object result) {
            try {
                if (result != null) {
                    JSONObject json = (JSONObject) result;
                    System.out.println("json----" + json);
                    JSONObject error_ = json.getJSONObject("error");
                    JSONObject responseObj = json.getJSONObject("response");
                    String status_ = error_.getString("status_code");
                    String msg_ = error_.getString("msg");

                    String fName = responseObj.getString("ai_first_name");
                    String lName = responseObj.getString("ai_last_name");
                    String phoneNo = responseObj.getString("ai_mobile");
                    String age = responseObj.getString("ai_age");
                    String gender = responseObj.getString("ai_gender");
                    String tPreference = responseObj.getString("ai_tpreference");
                    String email = responseObj.getString("accounts_email");
                    String address1 = responseObj.getString("ai_address_1");
                    String address2 = responseObj.getString("ai_address_2");
                    String city = responseObj.getString("ai_city");
                    String state = responseObj.getString("ai_state");
                    String postalCode = responseObj.getString("ai_postal_code");
                    String country = responseObj.getString("ai_country");
                    String imagePath =json.getString("imagepath") + responseObj.getString("ai_image");

                    etFname.setText(fName);
                    etLname.setText(lName);
                    etPhoneno.setText(phoneNo);
                    etEmail.setText(email);
                    if (age.equalsIgnoreCase("null") || age.equalsIgnoreCase("")) {
                        etAge.setText("");
                    } else {
                        etAge.setText(age);
                    }
                    if (gender.equalsIgnoreCase("null") || gender.equalsIgnoreCase("")) {
                        tvGender.setText("");
                    } else {
                        tvGender.setText(gender);
                    }
                    if (tPreference.equalsIgnoreCase("null") || tPreference.equalsIgnoreCase("")) {
                        etTravelpref.setText("");
                    } else {
                        etTravelpref.setText(tPreference);
                    }
                    if (address1.equalsIgnoreCase("null") || address1.equalsIgnoreCase("")) {
                        etAddress1.setText("");
                    } else {
                        etAddress1.setText(address1);
                    }
                    if (address2.equalsIgnoreCase("null") || address2.equalsIgnoreCase("")) {
                        etAddress2.setText("");
                    } else {
                        etAddress2.setText(address2);

                    }
                    if (city.equalsIgnoreCase("null") || city.equalsIgnoreCase("")) {
                        etCity.setText("");
                    } else {
                        etCity.setText(city);
                    }
                    if (state.equalsIgnoreCase("null") || state.equalsIgnoreCase("")) {
                        etState.setText("");
                    } else {
                        etState.setText(state);
                    }
                    if (postalCode.equalsIgnoreCase("null") || postalCode.equalsIgnoreCase("")) {
                        etPostalcode.setText("");
                    } else {
                        etPostalcode.setText(postalCode);
                    }
                    if (country.equalsIgnoreCase("null") || country.equalsIgnoreCase("")) {
                        etCountry.setText("");
                    } else {
                        etCountry.setText(country);
                    }
                    if (imagePath.equalsIgnoreCase("")||imagePath.equalsIgnoreCase("null")){
                        ivprofilePhoto.setImageBitmap(null);
                    }else{
                        imageLoader.get(imagePath, new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                ivprofilePhoto.setImageBitmap(response.getBitmap());
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
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


    private void getGenderData() {
        TextView textView = new TextView(getActivity());
        textView.setText("Select Gender");
        textView.setPadding(20, 30, 20, 30);
        textView.setTextSize(20F);
        textView.setBackgroundColor(Color.CYAN);
        textView.setTextColor(Color.WHITE);
        final CharSequence[] items = {"Male", "Female", "Others"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCustomTitle(textView);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
// Do as per requirement
                tvGender.setText(items[item]);
                tvGender.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showCameraDialog() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void showFileChooser() {
        boolean result = ApiClient.checkPermission(getActivity());
        if (result) {
            showCameraDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ApiClient.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCameraDialog();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        imageFilePath = destination.toString();
        ivprofilePhoto.setImageBitmap(thumbnail);

        BitmapFactory.decodeFile(imageFilePath);

        // sharedPreferenceClass.setValue_string("User_Image",imageFilePath);
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            imageInByte = bytes.toByteArray();
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //syncProfileImage(imageFilePath);
        saveProfileAccount(imageInByte);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getActivity().managedQuery(selectedImageUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        imageFilePath = cursor.getString(column_index).toString();
        BitmapFactory.decodeFile(imageFilePath);
        value = String.valueOf(Uri.fromFile(new File(imageFilePath)));

        Bitmap bm = BitmapFactory.decodeFile(imageFilePath);
        //value = Utils.base64Encode(bm);
        ivprofilePhoto.setImageBitmap(bm);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            imageInByte = bytes.toByteArray();
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //imageInByte = imageFilePath.getBytes();
        saveProfileAccount(imageInByte);
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

    //The below method is to update the user profile
    private ApiHelper.TaskDelegate updateProfileListener = new ApiHelper.TaskDelegate() {
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
                        Toast.makeText(getActivity(), msg_, Toast.LENGTH_SHORT).show();
                    } else {//false case
                        new AwesomeErrorDialog(getActivity())
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
                                        Intent mIntent = new Intent(getActivity(), HomeSlidingActivity.class);
                                        mIntent.putExtra("fragmentPosition", 1);
                                        startActivity(mIntent);
                                        getActivity().finish();
                                    }
                                })
                                .show();
                        JSONObject data_ = error_.getJSONObject("data");
                        String firstname = json.getString("firstname");
                        String lastname = json.getString("lastname");
                        String phone = json.getString("phone");
                        String age = json.getString("age");
                        String gender = json.getString("gender");
                        String travel_preferences = json.getString("travel_preferences");
                        String email = json.getString("email");
                        String password = json.getString("password");
                        String address1 = json.getString("address1");
                        String address2 = json.getString("address2");
                        String city = json.getString("city");
                        String state = json.getString("state");
                        String zip = json.getString("zip");
                        String country = json.getString("country");
                        String userID = json.getString("user_id");


                        //store values in sharedpreferences
                        ApiClient.saveDataWithKeyAndValue(getActivity(), "firstname", firstname);
                        ApiClient.saveDataWithKeyAndValue(getActivity(), "lastname", lastname);
                        ApiClient.saveDataWithKeyAndValue(getActivity(), "phone", phone);
                        ApiClient.saveDataWithKeyAndValue(getActivity(), "age", age);
                        ApiClient.saveDataWithKeyAndValue(getActivity(), "gender", gender);
                        ApiClient.saveDataWithKeyAndValue(getActivity(), "travel_preferences", travel_preferences);
                        ApiClient.saveDataWithKeyAndValue(getActivity(), "email", email);
                        ApiClient.saveDataWithKeyAndValue(getActivity(), "password", password);
                        ApiClient.saveDataWithKeyAndValue(getActivity(), "address1", address1);
                        ApiClient.saveDataWithKeyAndValue(getActivity(), "address2", address2);
                        ApiClient.saveDataWithKeyAndValue(getActivity(), "city", city);
                        ApiClient.saveDataWithKeyAndValue(getActivity(), "state", state);
                        ApiClient.saveDataWithKeyAndValue(getActivity(), "zip", zip);
                        ApiClient.saveDataWithKeyAndValue(getActivity(), "country", country);
                        ApiClient.saveDataWithKeyAndValue(getActivity(), "user_id", userID);
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

    private void saveProfileAccount(final byte[] image) {
        // loading or check internet connection or something...
        // ... then
        String tag_json_req = "sync_key";
        String url = "http://10.25.25.100/tourbusweb/Api/login/profileimage/?appKey=" + appKey;
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    System.out.println(result);
                    JSONObject errorObj = result.getJSONObject("error");
                    if(errorObj.getString("status").equalsIgnoreCase("false")){
                        new AwesomeErrorDialog(getActivity())
                                .setTitle("Success")
                                .setMessage(errorObj.getString("msg"))
                                .setColoredCircle(R.color.white)
                                .setDialogIconOnly(R.drawable.main_logo)
                                .setCancelable(false)
                                .setButtonBackgroundColor(R.color.colorPrimaryDark)
                                .setButtonText(getString(R.string.dialog_ok_button))
                                .setErrorButtonClick(new Closure() {
                                    @Override
                                    public void exec() {
                                    }
                                })
                                .show();
                    }else{
                        new AwesomeErrorDialog(getActivity())
                                .setTitle("Error")
                                .setMessage(errorObj.getString("msg"))
                                .setColoredCircle(R.color.white)
                                .setDialogIconOnly(R.drawable.main_logo)
                                .setCancelable(false)
                                .setButtonBackgroundColor(R.color.colorPrimaryDark)
                                .setButtonText(getString(R.string.dialog_ok_button))
                                .setErrorButtonClick(new Closure() {
                                    @Override
                                    public void exec() {
                                    }
                                })
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        System.out.println(response);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", userID_);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                long imagename = System.currentTimeMillis();///IMAGE NAME SETTING DURING
                params.put("file", new DataPart(imagename + ".png", image));
                return params;
            }
        };

        //VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
        multipartRequest.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance().getRequestQueue().add(multipartRequest).addMarker(tag_json_req);
    }

}


