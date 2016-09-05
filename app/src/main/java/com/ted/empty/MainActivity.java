package com.ted.empty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new PolyToPolyView(this));

    }

    class PolyToPolyView extends View{

        private static final int  NUM_OF_POINT = 8;

        /**
         * 图片折叠后的总宽度
         */
        private int mTranslateDis;

        /**
         * 折叠后的总宽度与原图宽度的比例
         */
        private float mFactor = 0.8f;

        /**
         * 折叠的块数
         */
        private final int mNumOffFolds = 8;

        private Matrix[] mMatrices = new Matrix[mNumOffFolds];

        /**
         * 原图每块的宽度
         */
        private int mFlodWidth;

        /**
         * 折叠时 每快的宽度
         */
        private int mTranslateDisPerFlod;

        Bitmap mBitmap;

        Matrix mMatrix;

        private Paint mShadowPaint;
        private Matrix mShadowGradienMatrix;
        private LinearGradient mShadowGradientShader;

        public PolyToPolyView(Context context) {
            super(context);
            mBitmap = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.test);
            //折叠后每快的总宽度
            mTranslateDis = (int)(mBitmap.getWidth() * mFactor);
            //原图每快的宽度
            mFlodWidth = mBitmap.getWidth() / mNumOffFolds;
            //折叠时，没快的宽度
            mTranslateDisPerFlod = mTranslateDis / mNumOffFolds;

            for (int i = 0; i < mNumOffFolds; i++){
                mMatrices[i] = new Matrix();
            }

            //纵轴减小的高度，用够股定理计算
            int depth = (int) Math.sqrt(mFlodWidth * mFlodWidth -
                    mTranslateDisPerFlod * mTranslateDisPerFlod) / 2;

            //转换点
            float[] src = new float[mNumOffFolds];
            float[] dst = new float[mNumOffFolds];

            for (int i = 0; i < mNumOffFolds; i++){
                src[0] = i * mFlodWidth;
                src[1] = 0;
                src[2] = src[0] + mFlodWidth;
                src[3] = 0;
                src[4] = src[0] + mFlodWidth;
                src[5] = mBitmap.getHeight();
                src[6] = src[0];
                src[7] = src[5];

                boolean isEven = i % 2 == 0;
                dst[0] = i * mTranslateDisPerFlod;
                dst[1] = isEven ? 0 : depth;
                dst[2] = dst[0] + mTranslateDisPerFlod;
                dst[3] = isEven ? depth : 0;
                dst[4] = dst[2];
                dst[5] = isEven ? mBitmap.getHeight() - depth : mBitmap.getHeight();
                dst[6] = dst[0];
                dst[7] = isEven ? mBitmap.getHeight() : mBitmap.getHeight() - depth;

                mMatrices[i].setPolyToPoly(src,0,dst,0,src.length >> 1);

            }

            /*mMatrix = new Matrix();

            mShadowPaint = new Paint();
            mShadowPaint.setStyle(Paint.Style.FILL);
            mShadowGradientShader = new LinearGradient(0,0,0.5f,0,
                    Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);

            mShadowPaint.setShader(mShadowGradientShader);

            mShadowGradienMatrix = new Matrix();
            mShadowGradienMatrix.setScale(mBitmap.getWidth(), 1);
            mShadowGradientShader.setLocalMatrix(mShadowGradienMatrix);
            mShadowPaint.setAlpha((int)0.9*255);*/


        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            //canvas.save();

            /*float[] src = {0, 0,
                    mBitmap.getWidth(), 0,
                    mBitmap.getWidth(), mBitmap.getHeight(),
                    0, mBitmap.getHeight()};
            float[] dst = {0,0,
                    mBitmap.getWidth(), 100,
                    mBitmap.getWidth(),mBitmap.getHeight() - 100,
                    0, mBitmap.getHeight()};
            mMatrix.setPolyToPoly(src,0,dst,0,src.length >> 1);*/

            for (int i = 0; i < mNumOffFolds; i ++){
                canvas.save();
                canvas.concat(mMatrices[i]);
                canvas.clipRect(mFlodWidth * i, 0, mFlodWidth * i + mFlodWidth,
                            mBitmap.getHeight());
                canvas.drawBitmap(mBitmap,0,0, null);

                canvas.restore();
            }

            //canvas.concat(mMatrix);
            //canvas.drawBitmap(mBitmap,mMatrix, null);

            //canvas.drawRect(0,0,mBitmap.getWidth(),mBitmap.getHeight(), mShadowPaint);
            //canvas.restore();
        }
    }
}
