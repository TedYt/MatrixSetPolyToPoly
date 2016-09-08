package com.ted.empty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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
public class FoldLayout  extends ViewGroup{

    /**
     * 图片折叠后的总宽度
     */
    private float mTranslateDis;

    /**
     * 折叠后的总宽度与原图宽度的比例
     */
    private float mFactor = 1.0f;

    /**
     * 折叠的块数
     */
    private final int mNumOffFolds = 8;

    private Matrix[] mMatrices = new Matrix[mNumOffFolds];

    /**
     * 原图每块的宽度
     */
    private float mFoldWidth;

    /**
     * 折叠时 每快的宽度
     */
    private float mTranslateDisPerFold;

    /**
     * 绘制黑色透明区域
     */
    private Paint mSolidPaint;

    /**
     *绘制阴影
     */
    private Paint mShadowPaint;
    private Matrix mShadowGradientMatrix;
    private LinearGradient mShadowGradientShader;

    private Canvas mCanvas = new Canvas();
    private Bitmap mBitmap;
    private boolean isReady;

    public FoldLayout(Context context) {
        this(context,null);
    }

    public FoldLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d("tui", "Construction");
        for (int i = 0; i < mNumOffFolds; i++){
            mMatrices[i] = new Matrix();
        }

        mSolidPaint = new Paint();
        //int alpha = (int)(255 * mFactor * 0.8f);
       //mSolidPaint.setColor(Color.argb((int)(alpha*0.8f),0,0,0));

        //初始化 paint
        mShadowPaint = new Paint();
        mShadowPaint.setStyle(Paint.Style.FILL);
        //mShadowPaint.setAlpha(alpha);
        //paint 设置 shader
        mShadowGradientShader = new LinearGradient(0,0,0.5f,0,
                Color.BLACK,Color.TRANSPARENT, Shader.TileMode.CLAMP);
        mShadowPaint.setShader(mShadowGradientShader);
        //shader设置matrix
        mShadowGradientMatrix = new Matrix();
        this.setWillNotDraw(false);
        //mShadowGradientMatrix.setScale(mFoldWidth, 1);
        //mShadowGradientShader.setLocalMatrix(mShadowGradientMatrix);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("tui", "onMeasure");
        View child = getChildAt(0);
        measureChild(child, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(child.getMeasuredWidth(),child.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("tui", "onLayout");
        View child = getChildAt(0);
        child.layout(0,0,child.getMeasuredWidth(),child.getMeasuredHeight());

        mBitmap = Bitmap.createBitmap(getMeasuredWidth(),getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);
        updateFold();
    }

    /**
     * 将与mFactor 和 宽度有关的代码处理都放在这个函数里
     */
    private void updateFold() {
        Log.d("tui", "updateFold");
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        mTranslateDis = w * mFactor;
        mFoldWidth = w / mNumOffFolds;
        mTranslateDisPerFold = mTranslateDis / mNumOffFolds;

        int alpha = (int)(255*(1 - mFactor));
        mSolidPaint.setColor(Color.argb((int)(alpha * 0.8f),0,0,0));
        mShadowGradientMatrix.setScale(mFoldWidth, 1);
        mShadowGradientShader.setLocalMatrix(mShadowGradientMatrix);
        mShadowPaint.setAlpha(alpha);

        float depth = (float)(Math.sqrt(mFoldWidth * mFoldWidth
                        - mTranslateDisPerFold * mTranslateDisPerFold) / 2);

        //转换点
        float[] src = new float[mNumOffFolds];
        float[] dst = new float[mNumOffFolds];

        for (int i = 0; i < mNumOffFolds; i++){
            src[0] = i * mFoldWidth;
            src[1] = 0;
            src[2] = src[0] + mFoldWidth;
            src[3] = 0;
            src[4] = src[0] + mFoldWidth;
            src[5] = h;
            src[6] = src[0];
            src[7] = src[5];

            boolean isEven = i % 2 == 0;
            dst[0] = i * mTranslateDisPerFold;
            dst[1] = isEven ? 0 : depth;
            dst[2] = dst[0] + mTranslateDisPerFold;
            dst[3] = isEven ? depth : 0;
            dst[4] = dst[2];
            dst[5] = isEven ? h - depth : h;
            dst[6] = dst[0];
            dst[7] = isEven ? h : h - depth;

            for (int y = 0; y < 8; y++){
                dst[y] = Math.round(dst[y]);
            }

            mMatrices[i].setPolyToPoly(src,0,dst,0,src.length >> 1);

        }
    }



    @Override
    protected void dispatchDraw(Canvas canvas) {
        Log.d("tui", "dispatchDraw");
        if (mFactor == 0){
            return;
        }
        if (mFactor == 1){
            super.dispatchDraw(canvas);
            return;
        }

        for (int i = 0; i < mNumOffFolds; i ++){
            canvas.save();
            canvas.concat(mMatrices[i]);//使用矩阵
            canvas.clipRect(mFoldWidth * i, 0, mFoldWidth * i + mFoldWidth,getHeight());

            if (isReady){
                canvas.drawBitmap(mBitmap,0,0, null);
            }else {
                super.dispatchDraw(mCanvas);
                canvas.drawBitmap(mBitmap,0,0, null);
                isReady = true;
            }

            //移动绘制阴影
            canvas.translate(mFoldWidth * i, 0);
            if (i % 2 == 0){
                //绘制黑色遮盖
                canvas.drawRect(0,0,mFoldWidth,getHeight(),mSolidPaint);
            }else {
                //绘制阴影
                canvas.drawRect(0,0,mFoldWidth,getHeight(),mShadowPaint);
            }
            canvas.restore();
        }
    }

    public void setFactor(float factor){
        this.mFactor = factor;
        updateFold();
        invalidate();
    }

    public float getFactor(){
        return mFactor;
    }
}
