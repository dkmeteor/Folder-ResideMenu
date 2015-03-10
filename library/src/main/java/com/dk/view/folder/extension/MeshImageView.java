package com.dk.view.folder.extension;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.View;
import android.widget.ImageView;

public class MeshImageView extends View {
    private Bitmap mBitmap;
    private Bitmap mShaderBitmap;
    private float[] mVerts;
    private Paint mPaint;
    private Shader mShader;

    public MeshImageView(Context context) {
        super(context);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//    }
//
//    @Override
//    public void draw(Canvas canvas) {
//        super.draw(canvas);
//        if (mPaint == null)
//            mPaint = new Paint();
//
//        if (mShader != null) {
//            mShaderBitmap = Bitmap.createBitmap(canvas.getWidth(),
//                    canvas.getHeight(), Bitmap.Config.ARGB_8888);
//            Canvas tempCanvas = new Canvas(mShaderBitmap);
//            Paint paint = new Paint();
//            paint.setShader(mShader);
//            tempCanvas.drawPaint(paint);
//        }
//        if (mVerts != null) {
//            canvas.drawBitmapMesh(mBitmap, 50, 5, mVerts, 0, null, 0, null);
//            if (mShaderBitmap != null)
//                canvas.drawBitmapMesh(mShaderBitmap, 50, 5, mVerts, 0, null, 0,
//                        null);
//        } else
//            canvas.drawBitmap(mBitmap, new Matrix(), mPaint);
//    }

    public void setImageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        postInvalidate();
    }

    public void setMeshVerts(float[] verts) {
        mVerts = verts;
        if (mBitmap != null) {
            postInvalidate();
        }
    }

    public void setShader(Shader shader) {
        mShader = shader;
    }
}
