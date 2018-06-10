package com.ureview.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.ureview.BuildConfig;
import com.ureview.R;
import com.ureview.models.CountriesModel;
import com.ureview.utils.pickimage.ScalingUtilities;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class StaticUtils {

    private static final int IMAGE_SAMPLE_SIZE = 4;
    private static final String DISPLAY_DATE_TIME_FORMAT = "dd-MM-yyyy hh:mm a";
    public static final String storageDir = Environment.getExternalStorageDirectory().getPath() + "/" + "Devotted" + "/";
    public static int screen_height, screen_width;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int TAKE_PICTURE_FROM_CAMERA = 3;
    public static final int PHONE_GALLERY_CLICK = 4;
    public static final int TAKE_VIDEO_FROM_CAMERA = 5;
    public static final int PICK_VIDEO_GALLERY = 6;
    public static final int PROFILE_IMAGE_SIZE = 300;

    public static boolean checkInternetConnection(Context context) {
        NetworkInfo _activeNetwork = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return _activeNetwork != null && _activeNetwork.isConnectedOrConnecting();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void getHeightAndWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        screen_height = displaymetrics.heightPixels;
        screen_width = displaymetrics.widthPixels;
    }

    private String calculateAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return String.valueOf(age);
    }

    public static int getAgeInMonths(int year, int month, int day) {
        int age;
        Calendar now = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.set(year, month, day);

        int year1 = now.get(Calendar.YEAR);
        int year2 = dob.get(Calendar.YEAR);
        age = year1 - year2;
        int month1 = now.get(Calendar.MONTH);
        int month2 = dob.get(Calendar.MONTH);
        if (month2 > month1) {
            age--;
        } else if (month1 == month2) {
            int day1 = now.get(Calendar.DAY_OF_MONTH);
            int day2 = dob.get(Calendar.DAY_OF_MONTH);
            if (day2 > day1) {
                age--;
            }
        }
        return age;
    }

    public static boolean isGreaterThan18Years(int year, int month, int day) {
        return getAgeInMonths(year, month, day) >= 18;//216
    }

    public static boolean isAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static Uri getOutputMediaFileUri(Context context, int type) {
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", getOutputMediaFile(type));
    }

    public static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "UReview");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    private static File getOutputMediaFile() {
        String imageFileName = "UReview" + "_" + System.currentTimeMillis();
        File image;
        File storageDirFile = new File(storageDir);
        if (!storageDirFile.exists()) {
            storageDirFile.mkdirs();
        }
        if (storageDirFile.exists()) {
            image = new File(storageDirFile, imageFileName + ".jpeg");
            return image;
        }
        return null;
    }

    public static Bitmap getImageFromCamera(Context mContext, Uri IMAGE_CAPTURE_URI) {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = IMAGE_SAMPLE_SIZE;
        options.inJustDecodeBounds = false;

        if (manufacturer.equalsIgnoreCase("samsung") || model.equalsIgnoreCase("samsung")) {
            int rotation = getCameraPhotoOrientation(mContext, IMAGE_CAPTURE_URI, IMAGE_CAPTURE_URI.getPath());
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            final Bitmap bitmap = BitmapFactory.decodeFile(IMAGE_CAPTURE_URI.getPath(), options);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            return BitmapFactory.decodeFile(IMAGE_CAPTURE_URI.getPath(), options);
        }
    }

    private static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            try {
                if (imageUri != null)
                    context.getContentResolver().notifyChange(imageUri, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    rotate = 0;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static File bitmapToFile(Bitmap bitmap) {
        try {
            File file = null;
            try {
                file = getOutputMediaFile();
                if (file != null) {
                    FileOutputStream fOut;
                    fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
                    fOut.flush();
                    fOut.close();
                } else {
                    return null;
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getResizeImage(Context context, int dstWidth, int dstHeight, ScalingUtilities.ScalingLogic scalingLogic, boolean rotationNeeded, String currentPhotoPath, Uri IMAGE_CAPTURE_URI) {
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            bmOptions.inJustDecodeBounds = false;
            if (bmOptions.outWidth < dstWidth && bmOptions.outHeight < dstHeight) {
                Bitmap bitmap;
                bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
                return setSelectedImage(bitmap, context, currentPhotoPath, IMAGE_CAPTURE_URI);
            } else {
                Bitmap unscaledBitmap = ScalingUtilities.decodeResource(currentPhotoPath, dstWidth, dstHeight,
                        scalingLogic);
                Matrix matrix = new Matrix();
                if (rotationNeeded) {
                    matrix.setRotate(getCameraPhotoOrientation(context, Uri.fromFile(new File(currentPhotoPath)), currentPhotoPath));
                    unscaledBitmap = Bitmap.createBitmap(unscaledBitmap, 0, 0, unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), matrix, false);
                }
                /*Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, dstWidth, dstHeight, scalingLogic);
                unscaledBitmap.recycle();
                if (scaledBitmap.getWidth() > 0 && scaledBitmap.getHeight() > 0) {
                    return scaledBitmap;
                } else {*/
                return unscaledBitmap;
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap setSelectedImage(Bitmap orignalBitmap, Context context, String imagePath, Uri IMAGE_CAPTURE_URI) {
        try {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (manufacturer.equalsIgnoreCase("samsung") || model.equalsIgnoreCase("samsung")) {
                return rotateBitmap(context, orignalBitmap, imagePath, IMAGE_CAPTURE_URI);
            } else {
                return orignalBitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return orignalBitmap;
        }
    }

    public static Bitmap rotateBitmap(Context context, Bitmap bit, String imagePath, Uri IMAGE_CAPTURE_URI) {
        int rotation = StaticUtils.getCameraPhotoOrientation(context, IMAGE_CAPTURE_URI, imagePath);
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        return Bitmap.createBitmap(bit, 0, 0, bit.getWidth(), bit.getHeight(), matrix, true);
    }

    public static String getDateAndTime(Calendar calendar) {
        return new SimpleDateFormat(DISPLAY_DATE_TIME_FORMAT, Locale.getDefault()).format(calendar.getTime());
    }

    public static void hideKeyboard(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(Activity act) {
        try {
            if (act.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        Log.e("parse Code : ", code);
        return code;
    }

    public static char[] parseCodeArray(String message) {
        return parseCode(message).toCharArray();
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int pxFromDp(final Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static String getAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            String add0 = obj.getAddressLine(0);
            Log.e("add 0 ", "Address 0" + add0);
            add = add + "\n" + obj.getFeatureName();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPremises();
            add = add + "\n" + obj.getSubLocality();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();
            Log.e("adddress tot :  ", "Address Total : " + add);
//            StaticUtils.showToast(context, add0);
            return obj.getFeatureName() + ", " + obj.getSubLocality() + ", " + obj.getLocality();
        } catch (IOException e) {
            e.printStackTrace();
            StaticUtils.showToast(context, e.getMessage());
        }
        return "";
    }

    public static RequestBody getRequestBody(JSONObject jsonObject) {
        MediaType MEDIA_TYPE_TEXT = MediaType.parse(Constants.CONTENT_TYPE_TEXT_PLAIN);
        return RequestBody.create(MEDIA_TYPE_TEXT, jsonObject.toString());
    }

    public static RequestBody getRequestBodyJson(JSONObject jsonObject) {
        MediaType MEDIA_TYPE_TEXT = MediaType.parse(Constants.CONTENT_TYPE_JSON);
        return RequestBody.create(MEDIA_TYPE_TEXT, jsonObject.toString());
    }

    public static CountriesModel getCurrentCountryModel(Context context) {
        String countryCode = getCountryCodeNew(context);
        CountriesModel currentCountriesModel = null;
        String[] arrContryCode = context.getResources().getStringArray(R.array.DialingCountryCode);
        for (String anArrContryCode : arrContryCode) {
            String[] arrDial = anArrContryCode.split(",");
            if (arrDial[1].trim().equals(countryCode)) {
                currentCountriesModel = new CountriesModel(anArrContryCode);
                break;
            }
        }
        return currentCountriesModel;
    }

    public static String getCountryCodeNew(Context context) {
        return Build.VERSION.SDK_INT >= 22 ? getCountryISO2New(context) : getCountryDisplayNameNew(context);
    }

    public static String getCountryISO2New(Context context) {
        String countryCode;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = tm.getSimState();
        if (tm.getSimCountryIso() != null && simState != TelephonyManager.SIM_STATE_ABSENT) {
            countryCode = tm.getSimCountryIso().toUpperCase();
            if (TextUtils.isEmpty(countryCode)) {
                countryCode = tm.getNetworkCountryIso().toUpperCase();
            }
            if (TextUtils.isEmpty(countryCode)) {
                countryCode = context.getResources().getConfiguration().locale.getCountry();
            }
        } else {
            countryCode = context.getResources().getConfiguration().locale.getCountry();
        }
        return countryCode;
    }

    public static String getCountryDisplayNameNew(Context context) {
        String countryName;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = tm.getSimState();
        if (tm.getSimCountryIso() != null && simState != TelephonyManager.SIM_STATE_ABSENT) {
            countryName = telephonyManager.getSimCountryIso().toUpperCase();
            if (TextUtils.isEmpty(countryName)) {
                countryName = tm.getNetworkCountryIso().toUpperCase();
            }
            if (TextUtils.isEmpty(countryName)) {
                countryName = tm.getNetworkCountryIso().toUpperCase();
            }
        } else {
            countryName = context.getResources().getConfiguration().locale.getCountry();
        }
        return countryName;
    }

    public static RequestBody getFileRequestBody(File file) {
        MediaType MEDIA_TYPE = MediaType.parse(getMimeType(file.getAbsolutePath()));
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE, file);
        return requestBody;
    }

    public RequestBody getFileRequestBodyPdf(File file) {
        MediaType MEDIA_TYPE = MediaType.parse(file.getAbsolutePath());
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE, file);
        return requestBody;
    }

    public static RequestBody getStringRequestBody(String value) {
        MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_TEXT, value);
        return requestBody;
    }

    private static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getFileUploadKey(String key, File file) {
        return "" + key + "\"; filename=\"" + file.getName();
    }
}



