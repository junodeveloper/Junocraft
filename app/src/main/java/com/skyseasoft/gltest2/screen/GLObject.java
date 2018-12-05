package com.skyseasoft.gltest2.screen;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by junodeveloper on 15. 6. 27..
 */
public class GLObject {
    public float[] mModelMatrix = new float[16];
    public int mVertices;
    public int mPositionIdx, mColorIdx, mNormalIdx, mTexIdx;

    public GLTexture mTexture;

    public int glMode;

    ArrayList<Float> mPositionList = new ArrayList<Float>();
    ArrayList<Float> mColorList = new ArrayList<Float>();
    ArrayList<Float> mNormalList = new ArrayList<Float>();
    ArrayList<Float> mTexCoordList = new ArrayList<Float>();

    public GLObject() {

    }

    public GLObject(int glMode, float[] positionList, float[] normalList, float[] colorList) {
        setPosition(positionList);
        setColor(colorList);
        setNormal(normalList);
        this.glMode = glMode;
    }

    public GLObject(int glMode, float[] positionList, float[] normalList) {
        setPosition(positionList);
        setNormal(normalList);
        this.glMode = glMode;
    }

    public void setTexture(GLTexture texture) {
        mTexture = texture;
    }

    public void addPosition(float x, float y, float z) {
        mPositionList.add(x);
        mPositionList.add(y);
        mPositionList.add(z);
    }

    public void setPosition(float[] positions) {
        mPositionList.clear();
        for(float f : positions)
            mPositionList.add(f);
    }

    public void addColor(float r, float g, float b, float a) {
        mColorList.add(r);
        mColorList.add(g);
        mColorList.add(b);
        mColorList.add(a);
    }

    public void setColor(float[] colors) {
        mColorList.clear();
        for(float f : colors)
            mColorList.add(f);
    }

    public void addNormal(float vx, float vy, float vz) {
        mNormalList.add(vx);
        mNormalList.add(vy);
        mNormalList.add(vz);
    }

    public void setNormal(float[] normals) {
        mNormalList.clear();
        for(float f : normals) {
            mNormalList.add(f);
        }
    }

    public void addTexCoord(float s, float t) {
        mTexCoordList.add(s);
        mTexCoordList.add(t);
    }

    public void setTexCoord(float[] coords) {
        mTexCoordList.clear();
        for(float f : coords) {
            mTexCoordList.add(f);
        }
    }

    public void setMode(int glMode) {
        this.glMode = glMode;
    }

    public void allocate() {
        Object[] tmpPositions = mPositionList.toArray();
        Object[] tmpColors = mColorList.toArray();
        Object[] tmpNormals = mNormalList.toArray();
        Object[] tmpTexCoords = mTexCoordList.toArray();
        float[] positions = new float[tmpPositions.length];
        float[] colors = new float[tmpColors.length];
        float[] normals = new float[tmpNormals.length];
        float[] texCoords = new float[tmpTexCoords.length];
        for(int i=0;i<tmpPositions.length;i++)
            positions[i] = (float) tmpPositions[i];
        for(int i=0;i<tmpColors.length;i++)
            colors[i] = (float) tmpColors[i];
        for(int i=0;i<tmpNormals.length;i++)
            normals[i] = (float) tmpNormals[i];
        for(int i=0;i<tmpTexCoords.length; i++)
            texCoords[i] = (float) tmpTexCoords[i];
        mVertices = tmpPositions.length / GLResource.POSITION_DATA_SIZE;
        FloatBuffer mPositions, mColors, mNormals, mTexCoords;
        mPositions = ByteBuffer.allocateDirect(positions.length * GLRenderer.mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPositions.put(positions).position(0);
        mColors = ByteBuffer.allocateDirect(colors.length * GLRenderer.mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColors.put(colors).position(0);
        mNormals = ByteBuffer.allocateDirect(normals.length * GLRenderer.mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mNormals.put(normals).position(0);
        mTexCoords = ByteBuffer.allocateDirect(texCoords.length * GLRenderer.mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTexCoords.put(texCoords).position(0);
        final int buffers[] = new int[3];
        GLES20.glGenBuffers(3, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mPositions.capacity() * GLRenderer.mBytesPerFloat,
                mPositions, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mNormals.capacity() * GLRenderer.mBytesPerFloat,
                mNormals, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mTexCoords.capacity() * GLRenderer.mBytesPerFloat,
                mTexCoords, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        mPositionIdx = buffers[0];
        mNormalIdx = buffers[1];
        mTexIdx = buffers[2];
        mPositions.limit(0);
        mPositions = null;
        mNormals.limit(0);
        mNormals = null;
        mColors.limit(0);
        mColors = null;
        mTexCoords.limit(0);
        mTexCoords = null;
    }

    public void release() {
        // Delete buffers from OpenGL's memory
        final int[] buffersToDelete = new int[] { mPositionIdx, mNormalIdx,
                mTexIdx };
        GLES20.glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }
}