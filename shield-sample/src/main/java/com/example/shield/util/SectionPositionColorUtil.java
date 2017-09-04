package com.example.shield.util;

import android.content.Context;
import android.widget.TextView;

/**
 * Created by bingweizhou on 17/8/8.
 */

public class SectionPositionColorUtil {
    public static void setSectionPositionColor(TextView v, Context context, int sectionPosition, int rowPosition) {
        if (sectionPosition == 0 && rowPosition == 0) {
            v.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
        } else if (rowPosition == 0) {
            v.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
        } else {
            v.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }
    }
}
