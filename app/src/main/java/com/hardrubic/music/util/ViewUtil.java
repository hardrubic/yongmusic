package com.hardrubic.music.util;

import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class ViewUtil {

    /**
     * 设置一个view的宽度
     */
    public static void setWidth(View view, int width) {
        LayoutParams params = view.getLayoutParams();
        params.width = width;
        view.setLayoutParams(params);
    }

    /**
     * 设置一个view的高度
     */
    public static void setHeight(View view, int height) {
        LayoutParams params = view.getLayoutParams();
        params.height = height;
        view.setLayoutParams(params);
    }

    /**
     * 设置一个view的宽高
     */
    public static void setWidthAndHeight(View view, int width, int height) {
        LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }
}
