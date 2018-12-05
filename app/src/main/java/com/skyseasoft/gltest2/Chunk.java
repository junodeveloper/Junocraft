package com.skyseasoft.gltest2;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.skyseasoft.gltest2.screen.GLRenderer;
import com.skyseasoft.gltest2.screen.GLResource;
import com.skyseasoft.gltest2.tools.OpenSimplexNoise;
import com.skyseasoft.gltest2.tools.PerlinNoise;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by junodeveloper on 15. 7. 23..
 */
public class Chunk {
    public int mBufferIdx;

    public static final int CX = 16;
    public static final int CY = 8;
    public static final int CZ = 16;

    public static final float BLOCK_SIZE = 10.0f;

    public int[][][] blk = new int[CX][CY][CZ];
    int sx, sy, sz;

    int renderCount;

    public GLRenderer renderer;
    public boolean changed;

    public Chunk(GLRenderer renderer, int sx, int sy, int sz) {
        final int buffers[] = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);
        mBufferIdx = buffers[0];
        //GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferIdx);
        //GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, CX * CY * CZ * 288 * GLRenderer.mBytesPerFloat, null, GLES20.GL_DYNAMIC_DRAW);
        //GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        this.sx = sx;
        this.sy = sy;
        this.sz = sz;
        this.renderer = renderer;
        changed = true;
    }


    public int noise() {
        OpenSimplexNoise noise = new OpenSimplexNoise();
        //PerlinNoise noise = new PerlinNoise();
        int totalBlocks = 0;
        for(int i=0;i<CX;i++) {
            for(int j=0;j<CZ;j++) {
                //float n = noise.noise((i + sx) / 64.0f, (j + sz) / 64.0f, 5, 1.0f);
                double n = (noise.eval((i + sx) * BLOCK_SIZE / 128.0, (j + sz) * BLOCK_SIZE / 128.0, 2.0)+1) * 5;
                int height = (int)n - sy;
                for(int k=0;k<height && k<Chunk.CY;k++) {
                    blk[i][k][j] = 1;
                    totalBlocks++;
                }
            }
        }
        return totalBlocks;
    }

    public void update() {
        if(!changed) return;
        float[] cubePosition = new float[108 * CX * CY * CZ];
        float[] blockType = new float[CX * CY * CZ];
        int element = 0, blocks = 0;
        for(int i=0;i<CX;i++) {
            for (int j = 0; j < CY; j++) {
                for (int k = 0; k < CZ; k++) {
                    if(blk[i][j][k] == 0) continue;
                    if(i>0 && blk[i-1][j][k]!=0 && k>0 && blk[i][j][k-1]!=0 && i<CX-1 && blk[i+1][j][k]!=0 && k<CZ-1 && blk[i][j][k+1]!=0
                            && j<CY-1 && blk[i][j+1][k]!=0) continue;
                    float x = i * BLOCK_SIZE;
                    float y = j * BLOCK_SIZE;
                    float z = (k+1) * BLOCK_SIZE;
                    blockType[blocks++] = blk[i][j][k];
                    /* Front Face */
                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    /* Right Face */
                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    /* Back Face */
                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    /* Left Face */
                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    /* Top Face */
                    cubePosition[element++] = x;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y + BLOCK_SIZE;
                    cubePosition[element++] = z;

                    /* Bottom Face */
                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - BLOCK_SIZE;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x + BLOCK_SIZE;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z;

                    cubePosition[element++] = x;
                    cubePosition[element++] = y;
                    cubePosition[element++] = z - BLOCK_SIZE;
                }
            }
        }
        FloatBuffer cubeBuffer = getInterleavedBuffer(cubePosition, MeshData.cuboidNormal, MeshData.cuboidTexCoord, blockType, blocks);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferIdx);
        //GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, cubeBuffer.capacity() * GLRenderer.mBytesPerFloat, cubeBuffer);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cubeBuffer.capacity() * GLRenderer.mBytesPerFloat, cubeBuffer,GLES20.GL_DYNAMIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        cubeBuffer.limit(0);
        cubeBuffer = null;

        renderCount = blocks;

        changed = false;
    }

    public FloatBuffer getInterleavedBuffer(float[] cubePositions, float[] cubeNormals, float[] cubeTextureCoordinates, float[] blockType, int len) {
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
                if(blockType[i] == 1) {
                    if(v>=24) cubeBuffer.put(MeshData.dirtTopTexCoord, cubeTextureOffset, GLResource.TEX_DATA_SIZE);
                    else cubeBuffer.put(MeshData.dirtSideTexCoord, cubeTextureOffset, GLResource.TEX_DATA_SIZE);
                    cubeTextureOffset = (cubeTextureOffset + GLResource.TEX_DATA_SIZE)%12;
                    //cubeBuffer.put(cubeTextureCoordinates, cubeTextureOffset, GLResource.TEX_DATA_SIZE);
                    //cubeTextureOffset += GLResource.TEX_DATA_SIZE;
                }
            }

            // The normal and texture data is repeated for each cube.
            cubeNormalOffset = 0;
            cubeTextureOffset = 0;
        }

        cubeBuffer.position(0);

        return cubeBuffer;
    }

    public void createBlock(int x, int y, int z, int type) {
        blk[x][y][z] = type;
        changed = true;
    }

    public void removeBlock(int x, int y, int z) {
        blk[x][y][z] = 0;
        changed = true;
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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, renderCount * 36); // 6 Vertices per face
    }
}