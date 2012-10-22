package com.mridang.moko.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/*
 * This is a custom TextView class that is used for
 * displaying Octicon icons.
 */
public class Octicon extends TextView {

    /*
     * @see android.widget.TextView#TextView(android.content.Context, android.util.AttributeSet, int)
     */
	public Octicon(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		init();

	}

    /*
     * @see android.widget.TextView#TextView(android.content.Context, android.util.AttributeSet)
     */
	public Octicon(Context context, AttributeSet attrs) {

		super(context, attrs);
		init();

	}

    /*
     * @see android.widget.TextView#TextView(android.content.Context)
     */
	public Octicon(Context context) {

		super(context);
		init();

	}

    /*
     * @see android.widget.TextView
     */
	private void init() {

		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/octicons.ttf");
			setTypeface(tf);
		}

	}

}