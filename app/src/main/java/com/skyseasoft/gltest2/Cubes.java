package com.skyseasoft.gltest2;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.skyseasoft.gltest2.screen.GLRenderer;
import com.skyseasoft.gltest2.screen.GLResource;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by junodeveloper on 15. 7. 24..
 */
public class Cubes {
    public int mBufferIdx;

    public static final int NX = 200;
    public static final int NY = 1;
    public static final int NZ = 200;
    public static final int LEN = 10;

    public GLRenderer renderer;
    public boolean changed;

    public Cubes(GLRenderer renderer) {
        final int buffers[] = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);
        mBufferIdx = buffers[0];

        this.renderer = renderer;
        changed = true;
    }
    public void update() {
        if(!changed) return;
        float[] cubePosition = new float[108 * NX * NY * NZ];
        int element = 0;
        for(int i=0;i<NX;i++) {
            for (int j = 0; j < NY; j++) {
                for (int k = 0; k < NZ; k++) {
                    int x = i * LEN;
                    int y = j * LEN;
                    int z = k * LEN;
                    /* Front Face */
                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    /* Right Face */
                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - LEN;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z - LEN;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z - LEN;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    /* Back Face */
                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - LEN;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - LEN;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z - LEN;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z - LEN;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z - LEN;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - LEN;

                    /* Left Face */
                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - LEN;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z - LEN;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - LEN;

                    /* Top Face */
                    cubePosition[element++] = x;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z - LEN;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z - LEN;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z - LEN;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + LEN;
                    cubePosition[element++] = z;

                    /* Bottom Face */
                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - LEN;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - LEN;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + LEN;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - LEN;
                }
            }
        }
        FloatBuffer cubeBuffer = getInterleavedBuffer(cubePosition, MeshData.cuboidNormal, MeshData.cuboidTexCoord, NX * NY * NZ);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferIdx);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cubeBuffer.capacity() * GLRenderer.mBytesPerFloat, cubeBuffer,GLES20.GL_DYNAMIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        cubeBuffer.limit(0);
        cubeBuffer = null;

        changed = false;
    }

    public FloatBuffer getInterleavedBuffer(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, int len) {
        final int cubeDataLength = cubePositions.length
                + (cubeNormals.length * len)
                + (cubeTextureCoordinates.length * len);
        int cubePositionOffset = 0;
        int cubeNormalOffset = 0;
        int cubeTextureOffset = 0;

        final FloatBuffer cubeBuffer = ByteBuffer.allocateDirect(cubeDataLength * GLRenderer.mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        for (int i = 0; i < len; i++) {
            for (int v = 0; v < 36; v++) {
                cubeBuffer.put(cubePositions, cubePositionOffset, GLResource.POSITION_DATA_SIZE);
                cubePositionOffset += GLResource.POSITION_DATA_SIZE;
                cubeBuffer.put(cubeNormals, cubeNormalOffset, GLResource.NORMAL_DATA_SIZE);
                cubeNormalOffset += GLResource.NORMAL_DATA_SIZE;
                cubeBuffer.put(cubeTextureCoordinates, cubeTextureOffset, GLResource.TEX_DATA_SIZE);
                cubeTextureOffset += GLResource.TEX_DATA_SIZE;
            }

            // The normal and texture data is repeated for each cube.
            cubeNormalOffset = 0;
            cubeTextureOffset = 0;
        }

        cubeBuffer.position(0);

        return cubeBuffer;
    }

    public void render() {
        final int stride = (GLResource.POSITION_DATA_SIZE + GLResource.NORMAL_DATA_SIZE + GLResource.TEX_DATA_SIZE) * GLRenderer.mBytesPerFloat;

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferIdx);
        GLES20.glEnableVertexAttribArray(renderer.mPositionHandle);
        GLES20.glVertexAttribPointer(renderer.mPositionHandle, GLResource.POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, stride, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferIdx);
        GLES20.glEnableVertexAttribArray(renderer.mNormalHandle);
        GLES20.glVertexAttribPointer(renderer.mNormalHandle, GLResource.NORMAL_DATA_SIZE, GLES20.GL_FLOAT, false, stride, GLResource.POSITION_DATA_SIZE * GLRenderer.mBytesPerFloat);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferIdx);
        GLES20.glEnableVertexAttribArray(renderer.mTexCoordHandle);
        GLES20.glVertexAttribPointer(renderer.mTexCoordHandle, GLResource.TEX_DATA_SIZE, GLES20.GL_FLOAT, false, stride, (GLResource.POSITION_DATA_SIZE + GLResource.NORMAL_DATA_SIZE) * GLRenderer.mBytesPerFloat);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glUniformMatrix4fv(renderer.mMVMatrixHandle, 1, false, renderer.getView().mViewMatrix, 0);

        Matrix.multiplyMM(renderer.mMVPMatrix, 0, renderer.mProjectionMatrix, 0, renderer.getView().mViewMatrix, 0);
        GLES20.glUniformMatrix4fv(renderer.mMVMatrixHandle, 1, false, renderer.mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, NX * NY * NZ * 36); // 6 Vertices per face
    }

    /*public void setBuffers(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates) {
        // First, copy cube information into client-side floating point buffers.
        final FloatBuffer cubePositionsBuffer;
        final FloatBuffer cubeNormalsBuffer;
        final FloatBuffer cubeTextureCoordinatesBuffer;

        cubePositionsBuffer = ByteBuffer.allocateDirect(cubePositions.length * GLRenderer.mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        cubePositionsBuffer.put(cubePositions).position(0);

        cubeNormalsBuffer = ByteBuffer.allocateDirect(cubeNormals.length * GLRenderer.mBytesPerFloat * NX * NY * NZ)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        for (int i = 0; i < (NX * NY * NZ); i++) {
            cubeNormalsBuffer.put(cubeNormals);
        }

        cubeNormalsBuffer.position(0);

        cubeTextureCoordinatesBuffer = ByteBuffer.allocateDirect(cubeTextureCoordinates.length * GLRenderer.mBytesPerFloat * NX * NY * NZ)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        for (int i = 0; i < (NX * NY * NZ); i++) {
            cubeTextureCoordinatesBuffer.put(cubeTextureCoordinates);
        }

        cubeTextureCoordinatesBuffer.position(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mPositionIdx);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cubePositionsBuffer.capacity() * GLRenderer.mBytesPerFloat, cubePositionsBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mNormalIdx);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cubeNormalsBuffer.capacity() * GLRenderer.mBytesPerFloat, cubeNormalsBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mTexIdx);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cubeTextureCoordinatesBuffer.capacity() * GLRenderer.mBytesPerFloat, cubeTextureCoordinatesBuffer,
                GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        //return new FloatBuffer[]{cubePositionsBuffer, cubeNormalsBuffer, cubeTextureCoordinatesBuffer};
    }*/
}