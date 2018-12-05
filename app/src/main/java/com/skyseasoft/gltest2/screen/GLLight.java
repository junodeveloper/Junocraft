package com.skyseasoft.gltest2.screen;

import android.opengl.Matrix;

/**
 * Created by junodeveloper on 15. 6. 28..
 */
public class GLLight {
    /* Light */
    public float[] mLightModelMatrix = new float[16];
    /** Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     *  we multiply this by our transformation matrices. */
    public final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    /** Used to hold the current position of the light in world space (after transformation via model matrix). */
    public final float[] mLightPosInWorldSpace = new float[4];

    public void setPosition(float x, float y, float z) {
        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, x, y, z);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
    }
}
