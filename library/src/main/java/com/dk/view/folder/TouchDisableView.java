package com.dk.view.folder;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.dk.view.folder.extension.CoreCalc;
import com.dk.view.folder.extension.MeshImageView;

/**
 * Created by thonguyen on 15/4/14.
 */
public class TouchDisableView extends FrameLayout {
    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_RIGHT = 1;
    private View mContent;
    private MeshImageView mMeshImageView;
    private int mDirection;
    private CoreCalc mCoreCalc;
    //	private int mMode;
    private boolean mTouchDisabled = false;
    private float folderFactor = 1.0f;

    public TouchDisableView(Context context) {
        this(context, null);
    }

    public TouchDisableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setContent(View v) {
        if (mContent != null) {
            this.removeView(mContent);
        }
        mContent = v;
        addView(mContent);
    }

    public View getContent() {
        return mContent;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);

        final int contentWidth = getChildMeasureSpec(widthMeasureSpec, 0, width);
        final int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0, height);
        getChildAt(0).measure(contentWidth, contentHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = r - l;
        final int height = b - t;
        getChildAt(0).layout(0, 0, width, height);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mTouchDisabled;
    }

    void setTouchDisable(boolean disableTouch) {
        mTouchDisabled = disableTouch;
    }

    boolean isTouchDisabled() {
        return mTouchDisabled;
    }


    /**
     * add by Dean Ding
     */

    public void setFolderX(float factor) {
        if (factor < 0.5f)
            factor = 0.5f;

        if (factor > 1)
            factor = 1f;

        folderFactor = factor;
        System.out.println("folderFactor:" + folderFactor);
        if (getChildCount() > 0
                && !(getChildAt(0) instanceof MeshImageView)) {
            createCache();
            replaceView();
        }

        mMeshImageView.setMeshVerts(mCoreCalc.createOffsetVerts(factor, 1000));
        mMeshImageView.setShader(mCoreCalc.getShader());
    }

    public float getFolderX() {
        return folderFactor;
    }

    public void setDirection(int direction) {
        mDirection = direction;
    }

    private Bitmap mDrawingCache = null;

    private boolean createCache() {
        if (getChildCount() > 0
                && !(getChildAt(0) instanceof MeshImageView)) {
            mDrawingCache = drawViewToBitmap(mDrawingCache, mContent,
                    mContent.getWidth(), mContent.getHeight(), 1, new BitmapDrawable());

            if (mCoreCalc == null)
                mCoreCalc = new CoreCalc(mContent.getWidth(), mContent.getHeight());
            return true;
        } else {
            return false;
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void replaceView() {
//        mContent.setVisibility(View.GONE);
//        removeAllViews();
//        addView(mContent);
        mMeshImageView = new MeshImageView(getContext());
        mMeshImageView.setImageBitmap(mDrawingCache);
//        setContent(mMeshImageView);
        this.removeView(mContent);
        addView(mMeshImageView);

        mCoreCalc.setDirection(mDirection);

        mMeshImageView.setMeshVerts(mCoreCalc.createOffsetVerts(1,
                getHeight() / 2));
    }

    public void revertView() {
        if (mContent != null && mContent.getParent() == null) {
            removeAllViews();
            addView(mContent);
        }
    }

    public Bitmap drawViewToBitmap(Bitmap dest, View view, int width,
                                   int height, int downSampling, Drawable drawable) {
        float scale = 1f / downSampling;
        int heightCopy = view.getHeight();
//        view.layout(0, 0, width, height);
        int bmpWidth = (int) (width * scale);
        int bmpHeight = (int) (height * scale);
        if (dest == null || dest.getWidth() != bmpWidth
                || dest.getHeight() != bmpHeight) {
            dest = Bitmap.createBitmap(bmpWidth, bmpHeight,
                    Bitmap.Config.ARGB_8888);
        }
        Canvas c = new Canvas(dest);
        drawable.setBounds(new Rect(0, 0, width, height));
        drawable.draw(c);
        if (downSampling > 1) {
            c.scale(scale, scale);
        }
        view.draw(c);
//        view.layout(0, 0, width, heightCopy);
        return dest;
    }

}
