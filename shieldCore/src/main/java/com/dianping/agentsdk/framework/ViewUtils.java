package com.dianping.agentsdk.framework;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dianping.shield.env.ShieldEnvironment;

public class ViewUtils {

    private static int screenWidthPixels;
    private static int screenHeightPixels;

    public static void showView(View v) {
        if (v == null) {
            return;
        }
        v.setVisibility(View.VISIBLE);
    }

    public static void showView(View v, boolean isShow) {
        showView(v, isShow, false);
    }

    public static void showView(View v, boolean isShow, boolean isGone) {
        if (isShow) {
            showView(v);
        } else {
            hideView(v, isGone);
        }
    }

    public static void hideView(View v) {
        hideView(v, false);
    }

    public static void hideView(View v, boolean isGone) {
        if (v == null) {
            return;
        }
        if (isGone) {
            v.setVisibility(View.GONE);
        } else {
            v.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 设置View的可见性
     *
     * @param view
     * @param visibility
     */
    public static void updateViewVisibility(View view, int visibility) {
        if (view == null)
            return;

        view.setVisibility(visibility);
    }

    public static void readOnlyView(EditText v) {
        if (v == null) {
            return;
        }
        v.setKeyListener(null);
    }

    public static void enableView(View v) {
        if (v == null) {
            return;
        }
        v.setEnabled(true);
    }

    public static void disableView(View v) {
        if (v == null) {
            return;
        }
        v.setEnabled(false);
    }

    public static boolean isShow(View v) {
        if (v == null) {
            return false;
        }
        return v.getVisibility() == View.VISIBLE;
    }

    /**
     * Determines if given points are inside view
     *
     * @param x    - x coordinate of point
     * @param y    - y coordinate of point
     * @param view - view object to compare
     * @return true if the points are within view bounds, false otherwise
     */
    public static boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        // point is inside view bounds
        if ((x > viewX && x < (viewX + view.getWidth()))
                && (y > viewY && y < (viewY + view.getHeight()))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        if (context == null) {
            return (int) dipValue;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        if (context == null) {
            return (int) pxValue;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int measureTextView(TextView tv) {
        if (tv == null) {
            return -1;
        }
        Paint p = tv.getPaint();
        return (int) p.measureText(tv.getText().toString());
    }

    /**
     * @param context
     * @param sp
     * @return
     */
    public static float sp2px(Context context, float sp) {
        if (context == null) {
            return sp;
        }
        Resources r = context.getResources();
        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                r.getDisplayMetrics());
        return size;
    }

    /**
     * @param context
     * @return
     */
    public static int getScreenWidthPixels(Context context) {
        if (ShieldEnvironment.INSTANCE.getPageWidth() > -1) {
            return ShieldEnvironment.INSTANCE.getPageWidth();
        }

        if (context == null) {
            return 0;
        }

        if (screenWidthPixels > 0) {
            return screenWidthPixels;
        }
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        screenWidthPixels = dm.widthPixels;
        return screenWidthPixels;
    }

    /**
     * @param context
     * @return
     */
    public static int getScreenHeightPixels(Context context) {
        if (ShieldEnvironment.INSTANCE.getPageHeight() > -1) {
            return ShieldEnvironment.INSTANCE.getPageHeight();
        }

        if (context == null) {
            return 0;
        }

        if (screenHeightPixels > 0) {
            return screenHeightPixels;
        }
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        screenHeightPixels = dm.heightPixels;
        return screenHeightPixels;
    }

    /**
     * TextView的可见性依赖于其文本内容的有无
     *
     * @param view
     * @param content
     */
    public static void setVisibilityAndContent(TextView view, String content) {
        if (!TextUtils.isEmpty(content)) {
            view.setText(content);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }


    public static int getTextViewWidth(TextView textView, String text, int textSize) {
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredWidth();
    }

    public static int getTextViewWidth(TextView textView, String text) {
        textView.setText(text);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredWidth();
    }


    public static int getViewHeight(View view) {
        if (view == null) {
            return 0;
        }
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredHeight();
    }

    public static int getViewWidth(View view) {
        if (view == null) {
            return 0;
        }
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredWidth();
    }



}
