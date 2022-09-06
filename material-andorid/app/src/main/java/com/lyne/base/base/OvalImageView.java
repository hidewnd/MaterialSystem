package com.lyne.base.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 圆形的ImageView
 */

public class OvalImageView extends AppCompatImageView {
    private Paint mPaintBitmap = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿
    private Bitmap mRawBitmap;
    private BitmapShader mShader;
    private Matrix mMatrix = new Matrix();
    private int strokeColor = 0xFFFFFFFF;//默认边框是白色
    private float strokeWidth = 0;//单位是像素的边框宽度

    public OvalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        获取资源图片并转为Bitmap
        Bitmap rawBitmap = getBitmap(getDrawable());
        if (rawBitmap != null) {
//            取较短的那一个作为圆的半径，保证整张图能填满整个圆
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            int viewMinSize = Math.min(viewWidth, viewHeight);
            float dstWidth = viewMinSize;
            float dstHeight = viewMinSize;
            if (mShader == null || !rawBitmap.equals(mRawBitmap)) {
                mRawBitmap = rawBitmap;
                mShader = new BitmapShader(mRawBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            }
            if (mShader != null) {
                mMatrix.setScale(dstWidth / rawBitmap.getWidth(), dstHeight / rawBitmap.getHeight());
                mShader.setLocalMatrix(mMatrix);
            }
            mPaintBitmap.setShader(mShader);
            float radius = viewMinSize / 2.0f;

            // 如果边框宽度不为0,则画出边框
            if (strokeWidth != 0) {
                Paint whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                whitePaint.setColor(strokeColor);
                // 首先画一个圆，填充的是边框的颜色,大小就是此控件设置的大小
                canvas.drawCircle(radius, radius, radius, whitePaint);
                // 在边框的圆的基础上再画一个圆，画的是图片，半径 = 此控件设置的大小 - 边框宽度，就露出了边框
                canvas.drawCircle(radius, radius, radius - strokeWidth, mPaintBitmap);
            } else {
                // 如果边框为0，直接画一个圆形图片即可
                canvas.drawCircle(radius, radius, radius, mPaintBitmap);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    private Bitmap getBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof ColorDrawable) {
            Rect rect = drawable.getBounds();
            int width = rect.right - rect.left;
            int height = rect.bottom - rect.top;
            int color = ((ColorDrawable) drawable).getColor();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * @param strokeWidth 要设置的边框宽度，单位是px
     */
    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    /**
     * @param strokeColor 要设置的边框颜色，必须是带透明度的16进制，例如：0xFF0000FF
     */
    public void setStrokeColot(int strokeColor) {
        this.strokeColor = strokeColor;
    }
}
