package com.ureview.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.listeners.IParserListener;
import com.ureview.models.CountriesModel;
import com.ureview.models.UserInfoModel;
import com.ureview.utils.DialogUtils;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.RuntimePermissionUtils;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.pickimage.ScalingUtilities;
import com.ureview.utils.views.CircleImageView;
import com.ureview.utils.views.CustomDialog;
import com.ureview.utils.views.CustomEditText;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class EditProfileFragment extends BaseFragment implements View.OnClickListener, IParserListener<JsonElement> {

    private View rootView;
    private MainActivity mainActivity;
    private CustomTextView txtUpdate, txtCountryCode, txtDob;
    private CountriesModel currentCountriesModel;
    private DatePickerDialog mDatePickerDialog;
    private Calendar myCalendar = Calendar.getInstance();
    private String firstName, lastName, email, userId, dob, mobile, address, about, countryCode, imagePath;
    private CustomEditText edtFirstName, edtLastName, edtEmail, edtMobileNumber, edtLocation, edtAbout;
    public static final int DIALOG_FRAGMENT = 1;
    private CustomDialog customDialog;
    private UserInfoModel userInfoModel;
    private CircleImageView imgProfile;
    private RelativeLayout relImage;
    private String profileImage = "";
    private Bitmap profilePicBitmap;
    private final int PERMISSION_FOR_CAMERA = 121;
    private final int PERMISSION_FOR_STORAGE = 122;
    public Uri IMAGE_CAPTURE_URI;

    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    public static EditProfileFragment newInstance(Bundle bundle) {
        EditProfileFragment signup1Fragment = new EditProfileFragment();
        signup1Fragment.setArguments(bundle);
        return signup1Fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        currentCountriesModel = StaticUtils.getCurrentCountryModel(mainActivity);
        userInfoModel = BaseApplication.userInfoModel;
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");

        firstName = userInfoModel.first_name;
        lastName = userInfoModel.last_name;
        email = userInfoModel.email;
        dob = userInfoModel.date_of_birth;
        mobile = userInfoModel.mobile;
        address = userInfoModel.address;
        about = userInfoModel.user_description;
        countryCode = userInfoModel.country_code;
        imagePath = userInfoModel.user_image;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        initComponents();
        return rootView;
    }

    private void initComponents() {
        txtUpdate = rootView.findViewById(R.id.txtUpdate);
        txtCountryCode = rootView.findViewById(R.id.txtCountryCode);
        txtDob = rootView.findViewById(R.id.txtDob);

        edtFirstName = rootView.findViewById(R.id.edtFirstName);
        edtLastName = rootView.findViewById(R.id.edtLastName);
        edtLocation = rootView.findViewById(R.id.edtLocation);
        edtEmail = rootView.findViewById(R.id.edtEmail);
        edtMobileNumber = rootView.findViewById(R.id.edtMobileNumber);
        edtAbout = rootView.findViewById(R.id.edtAbout);
        imgProfile = rootView.findViewById(R.id.imgProfile);
        relImage = rootView.findViewById(R.id.relImage);

        txtCountryCode.setOnClickListener(this);
        relImage.setOnClickListener(this);
        txtDob.setOnClickListener(this);
        txtUpdate.setOnClickListener(this);
        initDatePicker();
        setData();

    }

    private void setData() {
        if (!TextUtils.isEmpty(firstName)) {
            edtFirstName.setText(firstName);
        }
        if (!TextUtils.isEmpty(lastName)) {
            edtLastName.setText(lastName);
        }
        if (!TextUtils.isEmpty(email)) {
            edtEmail.setText(email);
            edtEmail.setEnabled(false);
        }
        if (!TextUtils.isEmpty(dob)) {
            txtDob.setText(dob);
        }
        if (!TextUtils.isEmpty(mobile)) {
            edtMobileNumber.setText(mobile);
            edtMobileNumber.setEnabled(false);
        }
        if (!TextUtils.isEmpty(countryCode)) {
            txtCountryCode.setText(countryCode.contains("+") ? countryCode : "+" + countryCode);
            txtCountryCode.setEnabled(false);
        }
        if (!TextUtils.isEmpty(address)) {
            edtLocation.setText(address);
        }
        if (!TextUtils.isEmpty(about)) {
            edtAbout.setText(about);
        }

        RequestOptions requestOptions = RequestOptions.bitmapTransform(new RoundedCorners(7));
        Glide.with(this).load(imagePath).apply(requestOptions).into(imgProfile);

    }

    private void initDatePicker() {
        myCalendar = Calendar.getInstance();
        myCalendar.setTimeInMillis(System.currentTimeMillis());
        myCalendar.set(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DATE));
        mDatePickerDialog = new DatePickerDialog(mainActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                txtDob.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                dob = txtDob.getText().toString().trim();
            }
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
        mainActivity.setToolBar("Edit Profile", "", "", false, true, false, false, false);
    }

    private void openCountriesDialog() {
        CountrySelectionFragment countrySelectionFragment = CountrySelectionFragment.newInstance(currentCountriesModel != null ? currentCountriesModel.countryCode : "");
        countrySelectionFragment.setTargetFragment(this, DIALOG_FRAGMENT);
        countrySelectionFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.countryCodeDialogStyle);
        countrySelectionFragment.show(mainActivity.getSupportFragmentManager(), "");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtUpdate:
                checkValidations();
                requestForRegistrationWS();
                break;
            case R.id.txtCountryCode:
                openCountriesDialog();
                break;
            case R.id.txtDob:
                showDOBDialog();
                break;
            case R.id.relImage:
                DialogUtils.showDropDownListStrings(mainActivity, new String[]{"Camera", "Gallery", "Cancel"}, relImage, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch ((String) view.getTag()) {
                            case "Camera":
                                checkAndRequestPermissionCamera();
                                break;
                            case "Gallery":
                                checkAndRequestPermissionGallery();
                                break;
                            case "Cancel":
                                break;
                            default:
                                break;
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    private void checkAndRequestPermissionCamera() {
        ArrayList<String> neededPermissions = new ArrayList<>();
        if (RuntimePermissionUtils.checkPermission(mainActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.CAMERA);
        }
        if (RuntimePermissionUtils.checkPermission(mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (neededPermissions.size() > 0) {
            requestForPermission(neededPermissions.toArray(new String[neededPermissions.size()]), PERMISSION_FOR_CAMERA);
        } else {
            clickOnCamera();
        }
    }

    private void checkAndRequestPermissionGallery() {
        ArrayList<String> neededPermissions = new ArrayList<>();
        if (RuntimePermissionUtils.checkPermission(mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (neededPermissions.size() > 0) {
            requestForPermission(neededPermissions.toArray(new String[neededPermissions.size()]), PERMISSION_FOR_STORAGE);
        } else {
            clickOnGalary();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_FOR_CAMERA) {
            if (grantResults.length < 1) {
                StaticUtils.showToast(mainActivity, "Camera Permission Denied");
                return;
            }
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    StaticUtils.showToast(mainActivity, "Camera Permission Denied");
                    return;
                }
            }
            clickOnCamera();
        } else if (requestCode == PERMISSION_FOR_STORAGE) {
            if (grantResults.length < 1) {
                StaticUtils.showToast(mainActivity, "Gallery Permission Denied");
                return;
            }

            // Verify that each required permission has been granted, otherwise return false.
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    StaticUtils.showToast(mainActivity, "Gallery Permission Denied");
                    return;
                }
            }
            clickOnGalary();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void clickOnCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        IMAGE_CAPTURE_URI = StaticUtils.getOutputMediaFileUri(mainActivity, StaticUtils.MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, IMAGE_CAPTURE_URI);
        startActivityForResult(intent, StaticUtils.TAKE_PICTURE_FROM_CAMERA);
    }

    private void clickOnGalary() {
        try {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, StaticUtils.PHONE_GALLERY_CLICK);
        } catch (Exception e) {
            e.printStackTrace();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, StaticUtils.PHONE_GALLERY_CLICK);
            }
        }

    }


    private void requestForRegistrationWS() {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder();
            requestBodyBuilder.setType(MultipartBody.FORM);
            MediaType MEDIA_TYPE_IMG = MediaType.parse("image/*");
            RequestBody requestBodyImage;

            jsonObjectReq.put("first_name", firstName);
            jsonObjectReq.put("last_name", lastName);
            jsonObjectReq.put("user_description", about);
            jsonObjectReq.put("mobile", mobile);

            jsonObjectReq.put("user_id", userId);
            jsonObjectReq.put("address", address);
            jsonObjectReq.put("date_of_birth", dob);
            jsonObjectReq.put("country_code", countryCode);
            if (profilePicBitmap == null && !TextUtils.isEmpty(profileImage)) {
                jsonObjectReq.put("user_image", StaticUtils.getStringRequestBody(profileImage));
            } else if (profilePicBitmap != null) {
                jsonObjectReq.put("user_image", StaticUtils.getFileRequestBody(StaticUtils.bitmapToFile(profilePicBitmap)));
//                jsonObjectReq.put(StaticUtils.getFileUploadKey("user_image", StaticUtils.bitmapToFile(profilePicBitmap)), StaticUtils.getFileRequestBody(StaticUtils.bitmapToFile(profilePicBitmap)));
            } else
                jsonObjectReq.put("user_image", "");

//            File file = StaticUtils.bitmapToFile(profilePicBitmap);
//            requestBodyImage = RequestBody.create(MEDIA_TYPE_IMG, file);
//            requestBodyBuilder.addFormDataPart("user_image", file.getName(), requestBodyImage);

            Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().editProfile(StaticUtils.getRequestBody(jsonObjectReq));
            new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_EDIT_PROFILE, call, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    private void requestForRegistrationWS() {
//        JSONObject jsonObjectReq = new JSONObject();
//        JSONObject imageObject = new JSONObject();
//        try {
//            MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder();
//            requestBodyBuilder.setType(MultipartBody.FORM);
//            MediaType MEDIA_TYPE_IMG = MediaType.parse("image/*");
//            RequestBody requestBodyImage;
//
//            requestBodyBuilder.addFormDataPart("first_name", null, StaticUtils.getStringRequestBody(firstName));
//            requestBodyBuilder.addFormDataPart("last_name", null, StaticUtils.getStringRequestBody(lastName));
//            requestBodyBuilder.addFormDataPart("user_description", null, StaticUtils.getStringRequestBody(about));
//            requestBodyBuilder.addFormDataPart("mobile", null, StaticUtils.getStringRequestBody(mobile));
//            requestBodyBuilder.addFormDataPart("user_id", null, StaticUtils.getStringRequestBody(userId));
//            requestBodyBuilder.addFormDataPart("address", null, StaticUtils.getStringRequestBody(address));
//            requestBodyBuilder.addFormDataPart("date_of_birth", null, StaticUtils.getStringRequestBody(dob));
//            requestBodyBuilder.addFormDataPart("country_code", null, StaticUtils.getStringRequestBody(countryCode));
//            requestBodyBuilder.addFormDataPart("user_image", null, StaticUtils.getStringRequestBody(""));
//
////            File file = StaticUtils.bitmapToFile(profilePicBitmap);
////            requestBodyImage = RequestBody.create(MEDIA_TYPE_IMG, file);
////            requestBodyBuilder.addFormDataPart("user_image", file.getName(), requestBodyImage);
//
//            Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().editProfile(requestBodyBuilder.build());
//            new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_EDIT_PROFILE, call, this);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

//    private void requestForRegistrationWS() {
//        JSONObject jsonObjectReq = new JSONObject();
//        JSONObject imageObject = new JSONObject();
//        try {
//            jsonObjectReq.put("first_name", firstName);
//            jsonObjectReq.put("last_name", lastName);
//            jsonObjectReq.put("user_description", about);
//            jsonObjectReq.put("mobile", mobile);
//
//            jsonObjectReq.put("user_id", userId);
//            jsonObjectReq.put("address", address);
//            jsonObjectReq.put("date_of_birth", dob);
//            jsonObjectReq.put("country_code", countryCode);
//            if (profilePicBitmap == null && !TextUtils.isEmpty(profileImage)) {
//                imageObject.put("user_image", StaticUtils.getStringRequestBody(profileImage));
//            } else if (profilePicBitmap != null) {
//                imageObject.put("user_image", StaticUtils.getFileRequestBody(StaticUtils.bitmapToFile(profilePicBitmap)));
////                jsonObjectReq.put(StaticUtils.getFileUploadKey("user_image", StaticUtils.bitmapToFile(profilePicBitmap)), StaticUtils.getFileRequestBody(StaticUtils.bitmapToFile(profilePicBitmap)));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        File file = StaticUtils.bitmapToFile(profilePicBitmap);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("user_image", file.getName(), StaticUtils.getFileRequestBody(file));
//
//        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().editProfile(StaticUtils.getRequestBody(jsonObjectReq), body);
//        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_EDIT_PROFILE, call, this);
//    }

    private void checkValidations() {
        firstName = edtFirstName.getText().toString().trim();
        lastName = edtLastName.getText().toString().trim();
        about = edtAbout.getText().toString().trim();
        mobile = edtMobileNumber.getText().toString().trim();
        imagePath = "";
        address = edtLocation.getText().toString().trim();
        dob = edtEmail.getText().toString().trim();
        countryCode = txtCountryCode.getText().toString().trim();
    }

    private void showDOBDialog() {
        if (!mDatePickerDialog.isShowing()) mDatePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    currentCountriesModel = data.getParcelableExtra("countriesModel");
                    txtCountryCode.setText("+" + currentCountriesModel.countryCode);
                }
                break;
            case StaticUtils.PHONE_GALLERY_CLICK:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        if (data != null) {
                            Uri _uri = data.getData();
                            if (_uri != null) {
                                Cursor cursor = mainActivity.getContentResolver().query(_uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                                cursor.moveToFirst();
                                try {
                                    imagePath = cursor.getString(0);
                                    profilePicBitmap = StaticUtils.getResizeImage(mainActivity, StaticUtils.PROFILE_IMAGE_SIZE, StaticUtils.PROFILE_IMAGE_SIZE, ScalingUtilities.ScalingLogic.CROP, true, imagePath, _uri);
                                    imgProfile.setImageBitmap(profilePicBitmap);
                                } catch (Exception e) {
                                    e.getStackTrace();
                                }
                                cursor.close();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case StaticUtils.TAKE_PICTURE_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        profilePicBitmap = StaticUtils.getImageFromCamera(mainActivity, IMAGE_CAPTURE_URI);
                        imgProfile.setImageBitmap(profilePicBitmap);
                        imagePath = IMAGE_CAPTURE_URI.getPath();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        if (response != null) {
            Log.e("response: ", response.toString());
            switch (requestCode) {
                case WSUtils.REQ_FOR_EDIT_PROFILE:
                    parseEditProfileResponse((JsonObject) response);
                    break;
                default:
                    break;
            }
        }
    }

    private void parseEditProfileResponse(JsonObject response) {
        if (response.has("status")) {
            if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                if (response.has("message")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
                if (response.has("user_info")) {
                    BaseApplication.userInfoModel = new UserInfoModel(response.get("userInfo").getAsJsonObject());
                    try {
                        String json = BaseApplication.userInfoModel.serialize();
                        LocalStorage.getInstance(mainActivity).putString(LocalStorage.PREF_USER_INFO_DATA, json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                mainActivity.popBackStack();
            } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                StaticUtils.showToast(mainActivity, response.get("message").getAsString());
            }
        }
    }

    @Override
    public void errorResponse(int requestCode, String error) {
        Log.e("error: ", error);
    }

    @Override
    public void noInternetConnection(int requestCode) {

    }

}

