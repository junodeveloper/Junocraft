package com.skyseasoft.gltest2.tools;

import android.opengl.Matrix;

/**
 * Created by junodeveloper on 15. 7. 27..
 */
public class VectorHelper {
    public static float[] getDUVector(float xAngle, float yAngle, float zAngle) {
        float[] refDirVec = {0.0f, 0.0f, -1.0f, 1.0f};
        float[] refUpVec = {0.0f, 1.0f, 0.0f, 1.0f};
        float[] dirMatrix = new float[16];
        float[] result = new float[8];
        Matrix.setIdentityM(dirMatrix, 0);
        Matrix.rotateM(dirMatrix, 0, zAngle, 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(dirMatrix, 0, yAngle, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(dirMatrix, 0, xAngle, 1.0f, 0.0f, 0.0f);
        Matrix.multiplyMV(result, 0, dirMatrix, 0, refDirVec, 0);
        Matrix.multiplyMV(result, 4, dirMatrix, 0, refUpVec, 0);
        return result;
    }

    public static float cross(float[] a, float[] b) {
        return a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
    }

    public static float cross(float a0, float a1, float a2, float b0, float b1, float b2) {
        return a0*b0 + a1*b1 + a2*b2;
    }
}
