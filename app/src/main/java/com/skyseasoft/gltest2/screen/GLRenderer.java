package com.skyseasoft.gltest2.screen;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.skyseasoft.gltest2.R;
import com.skyseasoft.gltest2.RawResourceReader;

/**
 * Created by junodeveloper on 15. 6. 27..
 */
public class GLRenderer {
    public static final int mBytesPerFloat = 4;

    public float[] mProjectionMatrix = new float[16];
    public float[] mMVPMatrix = new float[16];
    public final float[] mLightPosInEyeSpace = new float[4];

    private  int mVertexProgramHandle;

    public int mMVPMatrixHandle;
    public int mMVMatrixHandle;
    public int mLightPosHandle;
    public int mTexUniformHandle;
    public int mPositionHandle;
    //public int mColorHandle;
    public int mNormalHandle;
    public int mTexCoordHandle;

    private GLViewer mView;
    private GLLight mLight;
    private Context mContext;

    public GLRenderer(Context context) {
        mContext = context;
    }

    public void setup() {
        createProgram();
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glUseProgram(mVertexProgramHandle);

    }

    public void setView(GLViewer view) {
        mView = view;
    }

    public GLViewer getView() { return mView; };

    public void setLight(GLLight light) {
        mLight = light;
    }

    public void resetProjection(int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 1000.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public void drawObject(GLObject object) {
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        if(object.mTexture != null) {
            // Bind the texture to this unit.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, object.mTexture.texHandle);
        }
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTexUniformHandle, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, object.mPositionIdx);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, GLResource.POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, object.mNormalIdx);
        GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glVertexAttribPointer(mNormalHandle, GLResource.NORMAL_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, object.mTexIdx);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, GLResource.TEX_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mView.mViewMatrix, 0, object.mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mView.mViewMatrix, 0, mLight.mLightPosInWorldSpace, 0);
        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

        GLES20.glDrawArrays(object.glMode, 0, object.mVertices);
    }

    private void createProgram() {
        final int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, RawResourceReader.readTextFileFromRawResource(mContext, R.raw.per_pixel_vertex_shader));
        final int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, RawResourceReader.readTextFileFromRawResource(mContext, R.raw.per_pixel_fragment_shader3));
        mVertexProgramHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[]{"a_Position", "a_Normal", "a_TexCoordinate"});

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mVertexProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mVertexProgramHandle, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(mVertexProgramHandle, "u_LightPos");
        mTexUniformHandle = GLES20.glGetUniformLocation(mVertexProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mVertexProgramHandle, "a_Position");
        mNormalHandle = GLES20.glGetAttribLocation(mVertexProgramHandle, "a_Normal");
        mTexCoordHandle = GLES20.glGetAttribLocation(mVertexProgramHandle, "a_TexCoordinate");
    }

    private int compileShader(final int shaderType, final String shaderSource)
    {
        int shaderHandle = GLES20.glCreateShader(shaderType);

        if (shaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(shaderHandle, shaderSource);

            // Compile the shader.
            GLES20.glCompileShader(shaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                Log.e("GLRenderer", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }

        if (shaderHandle == 0)
        {
            throw new RuntimeException("Error creating shader.");
        }

        return shaderHandle;
    }

    private int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes)
    {
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            if (attributes != null)
            {
                final int size = attributes.length;
                for (int i = 0; i < size; i++)
                {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                Log.e("GLRenderer", "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        return programHandle;
    }

    /*public void drawObject(GLObject object) {
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        if(object.mTexture != null) {
            // Bind the texture to this unit.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, object.mTexture.texHandle);
        }
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTexUniformHandle, 0);

        object.mPositions.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, object.mPositionDataSize, GLES20.GL_FLOAT, false, object.mPositionDataSize * mBytesPerFloat, object.mPositions);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        object.mNormals.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, object.mNormalDataSize, GLES20.GL_FLOAT, false, object.mNormalDataSize * mBytesPerFloat, object.mNormals);
        GLES20.glEnableVertexAttribArray(mNormalHandle);

        object.mTexCoords.position(0);
        GLES20.glVertexAttribPointer(mTexCoordHandle, object.mTexCoordDataSize, GLES20.GL_FLOAT, false, object.mTexCoordDataSize * mBytesPerFloat, object.mTexCoords);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);

        Matrix.multiplyMM(mMVPMatrix, 0, mView.mViewMatrix, 0, object.mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mView.mViewMatrix, 0, mLight.mLightPosInWorldSpace, 0);
        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

        GLES20.glDrawArrays(object.glMode, 0, object.mVertices);
    }*/
}