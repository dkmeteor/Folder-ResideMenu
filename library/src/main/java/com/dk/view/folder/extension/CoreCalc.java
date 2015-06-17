package com.dk.view.folder.extension;

import android.graphics.LinearGradient;
import android.graphics.Shader;

import com.dk.view.folder.TouchDisableView;

public class CoreCalc {

    final float p1 = (float) Math.sqrt(Math.PI / 2 * SIN_lENGTH_FACTOR);
    final float p2 = (float) Math.sqrt((Math.PI / 2 + 1 * Math.PI) * SIN_lENGTH_FACTOR);
    final float p3 = (float) Math.sqrt((Math.PI / 2 + 2 * Math.PI) * SIN_lENGTH_FACTOR);
    final float p4 = (float) Math.sqrt((Math.PI / 2 + 3 * Math.PI) * SIN_lENGTH_FACTOR);
    final float p5 = (float) Math.sqrt((Math.PI / 2 + 4 * Math.PI) * SIN_lENGTH_FACTOR);
    final float p6 = (float) Math.sqrt((Math.PI / 2 + 5 * Math.PI) * SIN_lENGTH_FACTOR);
    final float p7 = (float) Math.sqrt((Math.PI / 2 + 6 * Math.PI) * SIN_lENGTH_FACTOR);
    final float p8 = (float) Math.sqrt((Math.PI / 2 + 7 * Math.PI) * SIN_lENGTH_FACTOR);
    final float p9 = (float) Math.sqrt((Math.PI / 2 + 8 * Math.PI) * SIN_lENGTH_FACTOR);
    //λ
    private static final int SIN_lENGTH_FACTOR = 60000;
    private static final float SIN_A = 32;
    private static final int GRAY = 0xff444444;
    private static final int TRANSPARENT = 0x00000000;
    private static int mAlpha = 0xff;

    private float[] originVerts = new float[6 * 51 * 2];
    private float[] meshVerts = new float[6 * 51 * 2];
    private Shader mShader;
    private int mDirection = TouchDisableView.DIRECTION_LEFT;

    public void setDirection(int direction) {
        mShader = null;
        mDirection = direction;
    }

    public Shader getShader() {
        return mShader;
    }

    private int width;
    private int height;

    public CoreCalc(int width, int height) {
        this.width = width;
        this.height = height;
        originVerts = createOriginVerts();
    }

    public float[] createOffsetVerts(float offset, float pointerY) {
        applyCurveXEffect(offset);
        applyScaleXEffect(offset, pointerY);
        if (mShader == null)
            mShader = applyShadow(offset);
        return meshVerts;
    }

    /**
     * f(x)=SIN_A*(1-offset)*sin(x*x/SIN_lENGTH_FACTOR)
     * <p/>
     * Notice: when x is in 0~720 , it works well. You can see the Graph through
     * this link.
     * https://www.google.com/?gws_rd=ssl#newwindow=1&q=y%3D64+sin(x*x%2F20000)+
     * <p/>
     * for other screen resolution , may be need other function
     *
     * @param offset
     * @return
     */
    private float[] applyCurveXEffect(float offset) {
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 51; j++) {
                if (mDirection == TouchDisableView.DIRECTION_RIGHT) {
                    meshVerts[i * 102 + 2 * j] = originVerts[i * 102 + 2 * j];
                    meshVerts[i * 102 + 2 * j + 1] = originVerts[i * 102 + 2 * j
                            + 1]
                            + (float) (SIN_A * (1 - offset) * Math.sin(originVerts[i
                            * 102 + 2 * j]
                            * originVerts[i * 102 + 2 * j] / SIN_lENGTH_FACTOR));
                } else {
                    meshVerts[i * 102 + 2 * j] = originVerts[i * 102 + 2 * j];
                    meshVerts[i * 102 + 2 * j + 1] = originVerts[i * 102 + 2 * j
                            + 1]
                            + (float) (SIN_A * (1 - offset) * Math.sin(originVerts[i
                            * 102 + 2 * j]
                            * originVerts[i * 102 + 2 * j] / SIN_lENGTH_FACTOR));
                }
            }
        return meshVerts;
    }

    /**
     * WTF
     * <p/>
     * g(x) = offset * f(x)* (1+ (f(y)-pointerY)^2/5000/width)
     *
     * @param offset
     * @param pointerY
     * @return
     */
    private float[] applyScaleXEffect(float offset, float pointerY) {
        float curveFactor = 0;
        curveFactor = offset;
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 51; j++) {
                if (mDirection == TouchDisableView.DIRECTION_RIGHT) {
                    meshVerts[i * 102 + 2 * j] = meshVerts[i * 102 + 2 * j]
                            * (0.4f + 0.6f * offset * offset * offset * offset);

                    meshVerts[i * 102 + 2 * j] = meshVerts[i * 102 + 2 * j]
                            * (1 + (1 - curveFactor) *
                            (meshVerts[i * 102 + 2 * j + 1] - pointerY)
                            * (meshVerts[i * 102 + 2 * j + 1] - pointerY)
                            / 5000 / width / curveFactor);
                } else {
                    meshVerts[i * 102 + 2 * j] = width - (width - meshVerts[i * 102 + 2 * j])
                            * (0.4f + 0.6f * offset * offset * offset * offset);

                    meshVerts[i * 102 + 2 * j] = width - (width - meshVerts[i * 102 + 2 * j])
                            * (1 + (1 - curveFactor) *
                            (meshVerts[i * 102 + 2 * j + 1] - pointerY)
                            * (meshVerts[i * 102 + 2 * j + 1] - pointerY)
                            / 5000 / width / curveFactor);
                }
            }
        return meshVerts;
    }

    /**
     * (sinx^2)'=2x cosx^2
     * 2xcosx^2 = 0
     * x^2 = π/2+2nπ
     * x^2 = (π/2+2nπ)*SIN_lENGTH_FACTOR
     *
     * @param offset
     * @return
     */
    private Shader applyShadow(float offset) {
//        int gray = ((int) (mAlpha * ((1l - offset) * 1f)) << 24)
//                | GRAY;

        int gray = GRAY;
        Shader shader;
        if (mDirection == TouchDisableView.DIRECTION_RIGHT) {
            shader = new LinearGradient(0, 0, width, 0, new int[]{gray,
                    TRANSPARENT, gray, TRANSPARENT, gray, TRANSPARENT, gray,
                    TRANSPARENT, gray}, new float[]{p1 / width, p2 / width,
                    p3 / width, p4 / width, p5 / width, p6 / width, p7 / width,
                    p8 / width, p9 / width}, Shader.TileMode.REPEAT);
        } else {
            shader = new LinearGradient(0, 0, width, 0, new int[]{gray,
                    TRANSPARENT, gray, TRANSPARENT, gray, TRANSPARENT, gray,
                    TRANSPARENT, gray}, new float[]{(1 - p9 / width), (1 - p8 / width),
                    (1 - p7 / width), (1 - p6 / width), (1 - p5 / width), (1 - p4 / width), (1 - p3 / width),
                    (1 - p2 / width), (1 - p1 / width)}, Shader.TileMode.REPEAT);
        }
        return shader;
    }

    /**
     *
     *  build Mesh verts 50 X 5
     *
     * @return
     */
    private float[] createOriginVerts() {
        float[] result = new float[6 * 51 * 2];
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 51; j++) {
                result[i * 102 + 2 * j] = j * width / 50f;
                result[i * 102 + 2 * j + 1] = i * height / 5f;
            }
        return result;
    }

    public float[] getOriginVerts() {
        return originVerts;
    }

    public void setOriginVerts(float[] originVerts) {
        this.originVerts = originVerts;
    }

    public float[] getMeshVerts() {
        return meshVerts;
    }

    public void setMeshVerts(float[] meshVerts) {
        this.meshVerts = meshVerts;
    }
}
