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
        private int mFoldWidth;

        /**
         * 折叠时 每快的宽度
         */
        private int mTranslateDisPerFold;

        Bitmap mBitmap;

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

        public PolyToPolyView(Context context) {
            super(context);
            mBitmap = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.test);
            //折叠后每快的总宽度
            mTranslateDis = (int)(mBitmap.getWidth() * mFactor);
            //原图每快的宽度
            mFoldWidth = mBitmap.getWidth() / mNumOffFolds;
            //折叠时，没快的宽度
            mTranslateDisPerFold = mTranslateDis / mNumOffFolds;

            for (int i = 0; i < mNumOffFolds; i++){
                mMatrices[i] = new Matrix();
            }

            mSolidPaint = new Paint();
            int alpha = (int)(255 * mFactor * 0.8f);
            mSolidPaint.setColor(Color.argb((int)(alpha*0.8f),0,0,0));

            //初始化 paint
            mShadowPaint = new Paint();
            mShadowPaint.setStyle(Paint.Style.FILL);
            mShadowPaint.setAlpha(alpha);
            //paint 设置 shader
            mShadowGradientShader = new LinearGradient(0,0,0.5f,0,
                        Color.BLACK,Color.TRANSPARENT, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mShadowGradientShader);
            //shader设置matrix
            mShadowGradientMatrix = new Matrix();
            mShadowGradientMatrix.setScale(mFoldWidth, 1);
            mShadowGradientShader.setLocalMatrix(mShadowGradientMatrix);

            //纵轴减小的高度，用够股定理计算
            int depth = (int) Math.sqrt(mFoldWidth * mFoldWidth -
                    mTranslateDisPerFold * mTranslateDisPerFold);
            depth = depth / 2;//高度减半

            //转换点
            float[] src = new float[mNumOffFolds];
            float[] dst = new float[mNumOffFolds];

            for (int i = 0; i < mNumOffFolds; i++){
                src[0] = i * mFoldWidth;
                src[1] = 0;
                src[2] = src[0] + mFoldWidth;
                src[3] = 0;
                src[4] = src[0] + mFoldWidth;
                src[5] = mBitmap.getHeight();
                src[6] = src[0];
                src[7] = src[5];

                boolean isEven = i % 2 == 0;
                dst[0] = i * mTranslateDisPerFold;
                dst[1] = isEven ? 0 : depth;
                dst[2] = dst[0] + mTranslateDisPerFold;
                dst[3] = isEven ? depth : 0;
                dst[4] = dst[2];
                dst[5] = isEven ? mBitmap.getHeight() - depth : mBitmap.getHeight();
                dst[6] = dst[0];
                dst[7] = isEven ? mBitmap.getHeight() : mBitmap.getHeight() - depth;

                mMatrices[i].setPolyToPoly(src,0,dst,0,src.length >> 1);

            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            for (int i = 0; i < mNumOffFolds; i ++){
                canvas.save();
                canvas.concat(mMatrices[i]);//使用矩阵
                canvas.clipRect(mFoldWidth * i, 0, mFoldWidth * i + mFoldWidth,
                            mBitmap.getHeight());
                canvas.drawBitmap(mBitmap,0,0, null);

                //移动绘制阴影
                canvas.translate(mFoldWidth * i, 0);
                if (i % 2 == 0){
                    //绘制黑色遮盖
                    canvas.drawRect(0,0,mFoldWidth,mBitmap.getHeight(),mSolidPaint);
                }else {
                    //绘制阴影
                    canvas.drawRect(0,0,mFoldWidth,mBitmap.getHeight(),mShadowPaint);
                }
                canvas.restore();
            }

            //canvas.concat(mMatrix);
            //canvas.drawBitmap(mBitmap,mMatrix, null);

            //canvas.drawRect(0,0,mBitmap.getWidth(),mBitmap.getHeight(), mShadowPaint);
            //canvas.restore();
        }
    }
}
