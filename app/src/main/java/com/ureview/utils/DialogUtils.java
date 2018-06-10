package com.ureview.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ureview.R;
import com.ureview.utils.views.CustomTextView;

public class DialogUtils {

    private static AlertDialog alert;

    public static void showDropDownListStrings(String title, Context context, final TextView textView, final String[] categoryNames, final View.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(categoryNames, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                alert.dismiss();
                if (textView != null) {
                    textView.setText(categoryNames[item]);
                    textView.setTag(categoryNames[item]);
                }
                if (clickListener != null) {
                    if (textView != null)
                        clickListener.onClick(textView);
                }
            }
        });
        alert = builder.create();
        alert.show();
    }

    public static void showDropDownListStrings(Context context, final String[] categoryNames, final View view, final View.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.app_name));
        builder.setItems(categoryNames, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                alert.dismiss();
                if (view != null) view.setTag(categoryNames[item]);
                if (clickListener != null) {
                    clickListener.onClick(view);
                }
            }
        });
        alert = builder.create();
        alert.setCancelable(true);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
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

            alertDialog.setCancelable(isCancelable);
            alertDialog.setCanceledOnTouchOutside(isCancelable);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showRatingDialog(final Context mContext, final View.OnClickListener positiveClick, final TextView givenRating) {
        try {
            CustomTextView txtHeading, txtMessage, txtPositiveButton, txtNegativeButton;

            final Dialog alertDialog = new Dialog(mContext, R.style.AlertDialogCustom);
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.setContentView(R.layout.layout_dialog);
            txtHeading = alertDialog.findViewById(R.id.txtHeading);
            txtMessage = alertDialog.findViewById(R.id.txtMessage);
            final RatingBar ratingBar = alertDialog.findViewById(R.id.ratingBar);
            ratingBar.setVisibility(View.VISIBLE);
            txtPositiveButton = alertDialog.findViewById(R.id.txtPositive);
            txtNegativeButton = alertDialog.findViewById(R.id.txtNegative);
            txtNegativeButton.setVisibility(View.GONE);

            alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = alertDialog.getWindow();
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            txtHeading.setText("Rate this Video");
            txtPositiveButton.setText("Submit your rating");
            txtMessage.setVisibility(View.GONE);
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating,
                                            boolean fromUser) {
                    // TODO Auto-generated method stub
                    Toast.makeText(mContext, Float.toString(rating), Toast.LENGTH_LONG).show();

                }

            });
            txtPositiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    if (positiveClick != null) {
                        givenRating.setText(String.valueOf(ratingBar.getRating()));
                        positiveClick.onClick(v);
                    }
                }
            });

            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showLogoutDialog(final Context mContext,
                                        final String heading,
                                        final String message,
                                        final String positiveText,
                                        final String negativeText,
                                        final View.OnClickListener positiveClick,
                                        final View.OnClickListener negativeClick) {
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

}
