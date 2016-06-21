package com.example.mypermissionsapp;

import android.animation.TimeAnimator;
import android.animation.TimeAnimator.TimeListener;
import android.annotation.TargetApi;
import android.os.Build;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
class FpsTimeListener implements TimeListener {
    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private double mFps;
    private TextView mTextView;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------
    public FpsTimeListener(TextView textView) {
        this.mTextView = textView;
        this.mFps = -1.0;
    }

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {

        double currentFps;
        if (deltaTime != 0) {
            currentFps = 1000.0 / (double) deltaTime;
        } else {
            currentFps = 0.9 * mFps;
        }
        if (mFps < 0.0) {
            mFps = currentFps;
        } else {
            mFps = 0.9 * mFps + 0.1 * currentFps;
        }
        mTextView.setText(String.format("FPS: %.2f", mFps));
    }
}
