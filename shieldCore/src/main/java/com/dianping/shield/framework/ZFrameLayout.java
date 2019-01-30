package com.dianping.shield.framework;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.LinkedList;

/**
 * created by yheng on 2019/1/15.
 */
public final class ZFrameLayout extends FrameLayout {
    private LinkedList<Integer> zPostionList = new LinkedList();
    private LinkedList<View> childsIndex = new LinkedList<>();
    public ZFrameLayout(@NonNull Context context) {
        super(context);
    }

    public ZFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ZFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onViewRemoved(View child) {
        int index = childsIndex.indexOf(child);
        if( index >= 0 ){
            childsIndex.remove( index );
            zPostionList.remove( index );
        }
    }

    public void addView(View child, ViewGroup.LayoutParams params, int zPostion) {
        int index = findAddIndexByZ( zPostion );
        super.addView(child, index, params);
        zPostionList.add( index, zPostion );
        childsIndex.add( index,child );
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        int index = findAddIndexByZ( 0 );
        super.addView(child, index, params);
        zPostionList.add( index, 0 );
        childsIndex.add( index,child );
    }

    @Override
    public void addView(View child, int width, int height) {
        final ViewGroup.LayoutParams params = generateDefaultLayoutParams();
        params.width = width;
        params.height = height;
        int index = findAddIndexByZ( 0 );
        super.addView(child, index, params);
        zPostionList.add( index, 0 );
        childsIndex.add( index,child );
    }

    @Override
    public void addView(View child) {
        int index = findAddIndexByZ( 0 );
        super.addView(child, index);
        zPostionList.add( index, 0 );
        childsIndex.add( index,child );
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        index = findAddIndexByZ( 0 );
        super.addView( child, index, params );
        zPostionList.add( index, 0 );
        childsIndex.add( index,child );
    }

    private int findAddIndexByZ(int zPositon){
        int childCount = getChildCount();
        for ( int index = 0; index < childCount; index ++ ) {
            int zValue = zPostionList.get( index );
            if( zValue > zPositon ){
                return index ;
            }
        }
        return childCount ;
    }
}
