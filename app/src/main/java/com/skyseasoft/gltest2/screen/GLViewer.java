package com.skyseasoft.gltest2.screen;

import android.opengl.Matrix;
import android.util.Log;

/**
 * Created by junodeveloper on 15. 6. 28..
 */
public class GLViewer {
    public float[] mViewMatrix = new float[16];
    public float[] mDirMatrix = new float[16];

    public float x, y, z;
    public float[] refDirVec = {0.0f, 0.0f, -1.0f, 1.0f};
    public float[] dirVec = new float[4];
    public float[] refUpVec = {0.0f, 1.0f, 0.0f, 1.0f};
    public float[] upVec = new float[4];
    public float yAngle, xAngle, zAngle;

    public void initView() {
        setEyePosition(0.0f, 0.0f, 5.5f);
        setUpVector(0.0f, 1.0f, 0.0f);
        yAngle = 0.0f;
        xAngle = 0.0f;
        zAngle = 0.0f;
        updateView();
    }

    public void setEyePosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setUpVector(float upX, float upY, float upZ) {
        upVec[0] = upX;
        upVec[1] = upY;
        upVec[2] = upZ;
        upVec[3] = 1.0f;
    }

    public void rotateY(float degree) {
        yAngle += degree;
    }
    public void rotateX(float degree) {
        xAngle += degree;
    }
    public void rotateZ(float degree) {
        zAngle += degree;
    }
    public void setyAngle(float degree) {
        yAngle = degree;
    }
    public void setxAngle(float degree) {
        xAngle = degree;
    }
    public void setzAngle(float degree) {
        zAngle = degree;
    }
    public void setDirMatrixByAngle() {
        Matrix.setIdentityM(mDirMatrix, 0);
        Matrix.rotateM(mDirMatrix, 0, zAngle, 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(mDirMatrix, 0, yAngle, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mDirMatrix, 0, xAngle, 1.0f, 0.0f, 0.0f);
    }
    public void setDirMatrix(float[] matrix) {
        mDirMatrix = matrix;
    }

    public float getStepX(float step) {
        return dirVec[0] * step;
    }
    public float getStepY(float step) {
        return dirVec[1] * step;
    }
    public float getStepZ(float step) {
        return dirVec[2] * step;
    }

    public void updateView(float x, float y, float z, float[] duVector) {
        for(int i=0;i<4;i++)
            dirVec[i] = duVector[i];
        for(int i=4;i<8;i++)
            upVec[i-4] = duVector[i];
        this.x = x;
        this.y = y;
        this.z = z;
        Matrix.setLookAtM(mViewMatrix, 0, x, y, z, x + dirVec[0], y + dirVec[1], z + dirVec[2], upVec[0], upVec[1], upVec[2]);
    }

    public void updateView() {
        setDirMatrixByAngle();
        Matrix.multiplyMV(dirVec, 0, mDirMatrix, 0, refDirVec, 0);
        Matrix.multiplyMV(upVec, 0, mDirMatrix, 0, refUpVec, 0);
        Log.d("UPDATEVIEW", "DOT:" + Dot(dirVec, upVec));
        Matrix.setLookAtM(mViewMatrix, 0, x, y, z, x + dirVec[0], y + dirVec[1], z + dirVec[2], upVec[0], upVec[1], upVec[2]);
    }

    float[] Cross(float[] A, float[] B) {
        float[] result = {A[1]*B[2]-A[2]*B[1], -(A[0]*B[2] - A[2]*B[0]), A[0]*B[1]-A[1]*B[0]};
        return result;
    }

    float Dot(float[] A, float[] B) {
        return A[0]*B[0] + A[1]*B[1] + A[2]*B[2];
    }
}