package com.example.testapp.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/*
 * This is a custom class to resize images by width
 */
public class Cover extends ImageView {

	/*
	 * @see android.widget.ImageView#ImageView()
	 */
    public Cover(Context context) {

    	super(context);

    }
    
	/*
	 * @see android.widget.ImageView#ImageView()
	 */
    public Cover(Context context, AttributeSet attrs) {

    	super(context, attrs);

    }

	/*
	 * @see android.widget.ImageView#ImageView()
	 */
    public Cover(Context context, AttributeSet attrs, int defStyle) {

    	super(context, attrs, defStyle);

    }

	/*
	 * @see android.widget.ImageView#onMeasure()
	 */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    	Integer width = MeasureSpec.getSize(widthMeasureSpec);
    	Integer height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        setMeasuredDimension(width, height);

    }
    
}