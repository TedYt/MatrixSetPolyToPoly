package com.ted.empty;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

/**
 * Copyright (C) 2008 The Android Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * Created by Ted.Yt on 9/8/16.
 */
public class TouchFoldLayout extends FoldLayout {

    private GestureDetector mScrollGestureDetector;

    private int mTransition = -1;

    public TouchFoldLayout(Context context) {
        super(context);
    }

    public TouchFoldLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mScrollGestureDetector = new GestureDetector(context,
                 new ScrollGestureDetector());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return mScrollGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas){
        if (mTransition == -1){
            mTransition = getWidth();
        }
        super.dispatchDraw(canvas);
    }

    private class ScrollGestureDetector extends SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            mTransition -= distanceX;
            if (mTransition < 0){
                mTransition = 0;
            }
            if (mTransition > getWidth()){
                mTransition = getWidth();
            }

            float factor = Math.abs(((float)mTransition)
                        /((float)getWidth()));

            setFactor(factor);

            return true;
        }
    }
}
