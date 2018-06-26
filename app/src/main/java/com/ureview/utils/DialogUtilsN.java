package com.ureview.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ureview.R;
import com.ureview.adapters.OptionsAdapter;
import com.ureview.listeners.ISearchClickListener;
import com.ureview.utils.views.CustomTextView;

public class DialogUtilsN {

    private static AlertDialog alert;

    public static void showDropDownListStrings(String title, Context context, final TextView textView, final String[] categoryNames, final View.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(categoryNames, (dialog, item) -> {
            alert.dismiss();
            if (textView != null) {
                textView.setText(categoryNames[item]);
                textView.setTag(categoryNames[item]);
            }
            if (clickListener != null) {
                if (textView != null)
                    clickListener.onClick(textView);
            }
        });
        alert = builder.create();
        alert.show();
    }

    public static void showDropDownListStrings(Context context, final String[] categoryNames, final View view, final View.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.app_name));
        builder.setItems(categoryNames, (dialog, item) -> {
            alert.dismiss();
            if (view != null) view.setTag(categoryNames[item]);
            if (clickListener != null) {
                clickListener.onClick(view);
            }
        });
        alert = builder.create();
        alert.setCancelable(true);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public static void showCustomDropDownListStrings(Context context, final String[] categoryNames, final ISearchClickListener clickListener) {
        showCustomDropDownListStrings(context, "", categoryNames, clickListener);
    }

    public static void showCustomDropDownListStrings(Context context, String heading, final String[] categoryNames, final ISearchClickListener clickListener) {
        try {
            CustomTextView txtHeading, txtHeading2, txtNegativeButton;
            RecyclerView recyclerView;
            final Dialog alertDialog = new Dialog(context, R.style.AlertDialogCustom);
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.setContentView(R.layout.layout_list_dialog);
            txtHeading = alertDialog.findViewById(R.id.txtHeading);
            txtHeading2 = alertDialog.findViewById(R.id.txtHeading2);
            recyclerView = alertDialog.findViewById(R.id.recyclerViewListOptions);
            txtNegativeButton = alertDialog.findViewById(R.id.txtNegativeButton);

            if (TextUtils.isEmpty(heading)) {
                txtHeading.setText(context.getString(R.string.app_name));
                txtHeading2.setVisibility(View.GONE);
            } else {
                txtHeading.setText(heading);
                txtHeading2.setVisibility(View.VISIBLE);
                txtHeading2.setText(TextUtils.isEmpty(heading) ? context.getString(R.string.app_name) : heading);
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new OptionsAdapter(categoryNames, clickListener));

            alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = alertDialog.getWindow();
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            txtNegativeButton.setOnClickListener(v -> alertDialog.dismiss());

            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCameraDialog(Context context, final ISearchClickListener clickListener) {
        try {
            String[] categoryNames = new String[]{"Upload From Gallery", "Record"};
            CustomTextView txtHeading, txtHeading2, txtNegativeButton;
            RecyclerView recyclerView;
            final Dialog alertDialog = new Dialog(context, R.style.AlertDialogCustom);
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.setContentView(R.layout.layout_list_dialog);
            txtHeading = alertDialog.findViewById(R.id.txtHeading);
            txtHeading2 = alertDialog.findViewById(R.id.txtHeading2);
            recyclerView = alertDialog.findViewById(R.id.recyclerViewListOptions);
            txtNegativeButton = alertDialog.findViewById(R.id.txtNegativeButton);

            txtHeading.setText("Upload Video");
            txtHeading2.setVisibility(View.VISIBLE);
            txtHeading2.setText("Video limit is 60 seconds else it will be automatically cropped");
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new OptionsAdapter(categoryNames, text -> {
                alertDialog.dismiss();
                if (clickListener != null) clickListener.onClick(text);
            }));

            alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = alertDialog.getWindow();
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            txtNegativeButton.setOnClickListener(v -> alertDialog.dismiss());

            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSimpleDialog(final Context mContext, final String message, final View.OnClickListener positiveClick, final View.OnClickListener negativeClick) {
        showSimpleDialog(mContext, null, message, null, null, positiveClick, negativeClick, false);
    }

    public static void showSimpleDialog(final Context mContext, final String message, final View.OnClickListener positiveClick, final View.OnClickListener negativeClick, final boolean singleButton) {
        showSimpleDialog(mContext, null, message, null, null, positiveClick, negativeClick, singleButton);
    }

    public static void showSimpleDialog(final Context mContext, final String heading, final String message, final String positiveText, final String negativeText, final View.OnClickListener positiveClick, final View.OnClickListener negativeClick, final boolean singleButton) {
        showSimpleDialog(mContext, heading, message, positiveText, negativeText, positiveClick, negativeClick, singleButton, true);
    }

    public static void showUnFollowConfirmationPopup(final Context mContext, final String userName, final View.OnClickListener positiveClick) {
        try {
            CustomTextView txtHeading, txtMessage, txtPositiveButton, txtNegativeButton;

            final Dialog alertDialog = new Dialog(mContext, R.style.AlertDialogCustom);
            alertDialog.setCancelable(false);
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.setContentView(R.layout.layout_dialog);
            txtHeading = alertDialog.findViewById(R.id.txtHeading);
            txtMessage = alertDialog.findViewById(R.id.txtMessage);
            txtPositiveButton = alertDialog.findViewById(R.id.txtPositive);
            txtNegativeButton = alertDialog.findViewById(R.id.txtNegative);

            alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = alertDialog.getWindow();
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            txtHeading.setText(mContext.getString(R.string.app_name));
            txtMessage.setText("Do you want to Unfollow ".concat(userName).concat("?"));

            txtPositiveButton.setText("Yes");

            txtNegativeButton.setText("No");

            txtPositiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    if (positiveClick != null) {
                        positiveClick.onClick(v);
                    }
                }
            });

            txtNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSimpleDialog(final Context mContext, final String heading, final String message, final String positiveText, final String negativeText, final View.OnClickListener positiveClick, final View.OnClickListener negativeClick, final boolean singleButton, final boolean isCancelable) {
        try {
            CustomTextView txtHeading, txtMessage, txtPositiveButton, txtNegativeButton;

            final Dialog alertDialog = new Dialog(mContext, R.style.AlertDialogCustom);
            alertDialog.setCancelable(false);
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.setContentView(R.layout.layout_dialog);
            txtHeading = alertDialog.findViewById(R.id.txtHeading);
            txtMessage = alertDialog.findViewById(R.id.txtMessage);
            txtPositiveButton = alertDialog.findViewById(R.id.txtPositive);
            txtNegativeButton = alertDialog.findViewById(R.id.txtNegative);

            alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = alertDialog.getWindow();
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            txtHeading.setText(TextUtils.isEmpty(heading) ? mContext.getString(R.string.app_name) : heading);
            txtMessage.setText(message);

            txtPositiveButton.setText(TextUtils.isEmpty(positiveText) ? "OK" : positiveText);

            if (singleButton) {
                txtNegativeButton.setVisibility(View.GONE);
            }

            txtNegativeButton.setText(TextUtils.isEmpty(negativeText) ? "Close" : negativeText);

            txtPositiveButton.setOnClickListener(v -> {
                alertDialog.dismiss();
                if (positiveClick != null) {
                    positiveClick.onClick(v);
                }
            });

            txtNegativeButton.setOnClickListener(v -> {
                alertDialog.dismiss();
                if (negativeClick != null) {
                    negativeClick.onClick(v);
                }
            });

            alertDialog.setCancelable(isCancelable);
            alertDialog.setCanceledOnTouchOutside(isCancelable);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showRatingDialog(final Context mContext, final View.OnClickListener positiveClick, final TextView givenRating) {
        try {
            CustomTextView txtHeading, txtPositiveButton;
            final Dialog alertDialog = new Dialog(mContext, R.style.AlertDialogCustom);
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.setContentView(R.layout.layout_dialog_new);
            txtHeading = alertDialog.findViewById(R.id.txtHeading);
            RatingBar ratingBar = alertDialog.findViewById(R.id.ratingBar);
            ratingBar.setVisibility(View.VISIBLE);
            alertDialog.findViewById(R.id.txtMessage).setVisibility(View.GONE);
            txtPositiveButton = alertDialog.findViewById(R.id.txtPositive);

//            imgStar1 = alertDialog.findViewById(R.id.imgStar1);
//            imgStar2 = alertDialog.findViewById(R.id.imgStar2);
//            imgStar3 = alertDialog.findViewById(R.id.imgStar3);
//            imgStar4 = alertDialog.findViewById(R.id.imgStar4);
//            imgStar5 = alertDialog.findViewById(R.id.imgStar5);
//
//            imgStar1.setOnClickListener(this);
//            imgStar2.setOnClickListener(this);
//            imgStar3.setOnClickListener(this);
//            imgStar4.setOnClickListener(this);
//            imgStar5.setOnClickListener(this);

            alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = alertDialog.getWindow();
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            txtHeading.setText("Rate this Video");
            txtPositiveButton.setText("Submit your rating");
            txtPositiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    if (positiveClick != null) {
                        Toast.makeText(mContext, ratingBar.getRating() + "", Toast.LENGTH_LONG).show();
//                        givenRating.setText(String.valueOf(getRating()));
                        givenRating.setText(String.valueOf(ratingBar.getRating()));
                        positiveClick.onClick(v);
                    }
                }
            });

            alertDialog.setCancelable(true);
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showLogoutDialog(final Context mContext, final String heading, final String message, final String positiveText, final String negativeText, final View.OnClickListener positiveClick, final View.OnClickListener negativeClick) {
        try {
            CustomTextView txtHeading, txtMessage, txtPositiveButton, txtNegativeButton;

            final Dialog alertDialog = new Dialog(mContext, R.style.AlertDialogCustom);
            alertDialog.setCancelable(false);
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.setContentView(R.layout.layout_logout_dialog);
            txtHeading = alertDialog.findViewById(R.id.txtHeading);
            txtMessage = alertDialog.findViewById(R.id.txtMessage);
            txtPositiveButton = alertDialog.findViewById(R.id.txtPositive);
            txtNegativeButton = alertDialog.findViewById(R.id.txtNegative);

            alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = alertDialog.getWindow();
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            txtHeading.setText(TextUtils.isEmpty(heading) ? mContext.getString(R.string.app_name) : heading);
            txtMessage.setText(message);

            txtPositiveButton.setText(TextUtils.isEmpty(positiveText) ? "Ok" : positiveText);
            txtNegativeButton.setText(TextUtils.isEmpty(negativeText) ? "Close" : negativeText);

            txtPositiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    if (positiveClick != null) {
                        positiveClick.onClick(v);
                    }
                }
            });

            txtNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    if (negativeClick != null) {
                        negativeClick.onClick(v);
                    }
                }
            });

            alertDialog.setCancelable(true);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showRatingSuccessDialog(final Context mContext, final String message) {
        try {
            CustomTextView txtMessage, txtPositiveButton;

            final Dialog alertDialog = new Dialog(mContext, R.style.AlertDialogCustom);
            alertDialog.setCancelable(false);
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.setContentView(R.layout.layout_dialog_new);
            txtMessage = alertDialog.findViewById(R.id.txtMessage);
            txtPositiveButton = alertDialog.findViewById(R.id.txtPositive);

            alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = alertDialog.getWindow();
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            txtMessage.setText(message);
            txtMessage.setSelected(true);

            txtPositiveButton.setOnClickListener(v -> {
                alertDialog.dismiss();
            });

            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
