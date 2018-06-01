package com.ureview.utils.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.ureview.R;


public class CustomEditText extends AppCompatEditText {
    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        String fontPath;
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CustomTextView);
        int font_val = typedArray.getInteger(R.styleable.CustomTextView_txt_font_type, 1);
        switch (font_val) {
            case 0:
                fontPath = "light.otf";
                break;
            case 1:
                fontPath = "regular.otf";
                break;
            case 2:
                fontPath = "bold.otf";
                break;
            case 3:
                fontPath = "italic.ttf";
                break;
            default:
                fontPath = "regular.otf";
                break;
        }
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), fontPath);
        setTypeface(tf);
        typedArray.recycle();
    }

}
