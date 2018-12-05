package com.skyseasoft.gltest2;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.skyseasoft.gltest2.screen.GLRenderer;
import com.skyseasoft.gltest2.tools.Collision;
import com.skyseasoft.gltest2.tools.OpenSimplexNoise;
import com.skyseasoft.gltest2.tools.VectorHelper;

/**
 * Created by junodeveloper on 15. 7. 26..
 */
public class SuperChunk {
    public int SCX = 12;
    public int SCY = 6;
    public int SCZ = 12;

    public float VISIBLE_RADIUS = 300.0f;

    public Chunk[][][] c = new Chunk[SCX][SCY][SCZ];
    float[] modelMatrix = new float[16];

    public GLRenderer renderer;

    public SuperChunk(GLRenderer renderer) {
        this.renderer = renderer;
        for(int i=0;i<SCX;i++) {
            for(int j=0;j<SCY;j++) {
                for(int k=0;k<SCZ;k++) {
                    c[i][j][k] = new Chunk(renderer, i * Chunk.CX, j * Chunk.CY, k * Chunk.CZ);
                    if(c[i][j][k].noise()>0) c[i][j][k].update();
                }
            }
        }
    }

    public double distance3D(float x1, float y1, float z1, float x2, float y2, float z2) {
        return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) + (z2-z1)*(z2-z1));
    }

    public double distance2D(float x1, float y1, float x2, float y2) {
        return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    }

    public void render(float px, float py, float pz, float[] duVector) {
        int renderedCount = 0;
        for(int i=0;i<SCX;i++) {
            for(int j=0;j<SCY;j++) {
                for(int k=0;k<SCZ;k++) {
                    if(distance2D(px, pz, (c[i][j][k].sx+Chunk.CX/2) * Chunk.BLOCK_SIZE, (c[i][j][k].sz+Chunk.CZ/2) * Chunk.BLOCK_SIZE)>VISIBLE_RADIUS) continue;
                    //if(VectorHelper.cross((c[i][j][k].sx + Chunk.CX / 2) * Chunk.BLOCK_SIZE - px, (c[i][j][k].sy + Chunk.CY / 2) * Chunk.BLOCK_SIZE - py,
                    //        (c[i][j][k].sz + Chunk.CZ / 2) * Chunk.BLOCK_SIZE - pz, duVector[0], duVector[1], duVector[2])<0) continue;
                    c[i][j][k].update();
                    if(c[i][j][k].renderCount == 0) continue;
                    Matrix.setIdentityM(modelMatrix, 0);
                    Matrix.translateM(modelMatrix, 0, c[i][j][k].sx * Chunk.BLOCK_SIZE, c[i][j][k].sy * Chunk.BLOCK_SIZE,
                            c[i][j][k].sz * Chunk.BLOCK_SIZE);
                    Matrix.multiplyMM(renderer.mMVPMatrix, 0, renderer.getView().mViewMatrix, 0, modelMatrix, 0);
                    GLES20.glUniformMatrix4fv(renderer.mMVMatrixHandle, 1, false, renderer.mMVPMatrix, 0);
                    Matrix.multiplyMM(renderer.mMVPMatrix, 0, renderer.mProjectionMatrix, 0, renderer.mMVPMatrix, 0);
                    GLES20.glUniformMatrix4fv(renderer.mMVPMatrixHandle, 1, false, renderer.mMVPMatrix, 0);
                    c[i][j][k].render();
                    renderedCount++;
                }
            }
        }
        Log.d("RENDEREDCOUNT", "COUNT:" + renderedCount);
    }

    public int[] getBlockNum(float x, float y, float z) {
        return new int[]{(int)(x/Chunk.BLOCK_SIZE), (int)(y/Chunk.BLOCK_SIZE), (int)(z/Chunk.BLOCK_SIZE)};
    }

    public boolean isExists(float x, float y, float z) {
        return isExists((int)(x/Chunk.BLOCK_SIZE), (int)(y/Chunk.BLOCK_SIZE), (int)(z/Chunk.BLOCK_SIZE));
    }

    public boolean isExists(int x, int y, int z) {
        if(x<0 || y<0 || z<0 || x>=SCX*Chunk.CX || y>=SCY*Chunk.CY || z>=SCZ*Chunk.CZ)
            return false;
        int cx = x/Chunk.CX;
        int cy = y/Chunk.CY;
        int cz = z/Chunk.CZ;
        int bx = x%Chunk.CX;
        int by = y%Chunk.CY;
        int bz = z%Chunk.CZ;
        return c[cx][cy][cz].blk[bx][by][bz]!=0;
    }

    public boolean checkGroundCollision(float x, float y, float z, float w, float h, float d) {
        int[] blockNum = getBlockNum(x, y, z);
        if(isExists(blockNum[0], blockNum[1] - 1, blockNum[2]))
            if(Collision.isCubeCollided(offset(blockNum[0]), offset(blockNum[1] - 1), offset(blockNum[2]), Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE, x - w/2.0f, y - h/2.0f, z - d/2.0f, w, h, d))
                return true;
        return false;
    }

    public boolean checkCeilingCollision(float x, float y, float z, float w, float h, float d) {
        int[] blockNum = getBlockNum(x, y, z);
        if(isExists(blockNum[0], blockNum[1] + 1, blockNum[2]))
            if(Collision.isCubeCollided(offset(blockNum[0]), offset(blockNum[1] + 1), offset(blockNum[2]), Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE, x - w/2.0f, y - h/2.0f, z - d/2.0f, w, h, d))
                return true;
        return false;
    }

    public float[] getBlockPos(float x, float y, float z) {
        int[] blockNum = getBlockNum(x, y, z);
        return new float[]{offset(blockNum[0]), offset(blockNum[1]), offset(blockNum[2])};
    }

    public float offset(int block) {
        return block * Chunk.BLOCK_SIZE;
    }

    public boolean checkCubeCollision(float x, float y, float z, float w, float h, float d) {
        int[] blockNum = getBlockNum(x, y, z);
        boolean result = false;
        if(isExists(blockNum[0] - 1, blockNum[1], blockNum[2]))
            result|=Collision.isCubeCollided(offset(blockNum[0] - 1), offset(blockNum[1]), offset(blockNum[2]), Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE,
            x - w/2.0f, y - h/2.0f, z -d/2.0f, w, h, d);
        if(isExists(blockNum[0] + 1, blockNum[1], blockNum[2]))
            result|=Collision.isCubeCollided(offset(blockNum[0] + 1), offset(blockNum[1]), offset(blockNum[2]), Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE,
                    x - w/2.0f, y - h/2.0f, z -d/2.0f, w, h, d);
        if(isExists(blockNum[0], blockNum[1] - 1, blockNum[2]))
            result|=Collision.isCubeCollided(offset(blockNum[0]), offset(blockNum[1] - 1), offset(blockNum[2]), Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE,
                    x - w/2.0f, y - h/2.0f, z -d/2.0f, w, h, d);
        if(isExists(blockNum[0], blockNum[1] + 1, blockNum[2]))
            result|=Collision.isCubeCollided(offset(blockNum[0]), offset(blockNum[1] + 1), offset(blockNum[2]), Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE,
                    x - w/2.0f, y - h/2.0f, z -d/2.0f, w, h, d);
        if(isExists(blockNum[0], blockNum[1], blockNum[2] - 1))
            result|=Collision.isCubeCollided(offset(blockNum[0]), offset(blockNum[1]), offset(blockNum[2] - 1), Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE,
                    x - w/2.0f, y - h/2.0f, z -d/2.0f, w, h, d);
        if(isExists(blockNum[0], blockNum[1], blockNum[2] + 1))
            result|=Collision.isCubeCollided(offset(blockNum[0]), offset(blockNum[1]), offset(blockNum[2] + 1), Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE, Chunk.BLOCK_SIZE,
                    x - w/2.0f, y - h/2.0f, z -d/2.0f, w, h, d);
        return result;
    }

    public void createBlock(int x, int y, int z, int type) {
        int cx = x/Chunk.CX;
        int cy = y/Chunk.CY;
        int cz = z/Chunk.CZ;
        int bx = x%Chunk.CX;
        int by = y%Chunk.CY;
        int bz = z%Chunk.CZ;
        if(cx>=SCX || cy>=SCY || cz>=SCZ || cx<0 || cy<0 || cz<0 || bx>=Chunk.CX || by>=Chunk.CY || bz>=Chunk.CZ || bx<0 || by<0 || bz<0)
            return;
        c[cx][cy][cz].createBlock(bx, by, bz, type);
    }

    public void removeBlock(int x, int y, int z) {
        int cx = x/Chunk.CX;
        int cy = y/Chunk.CY;
        int cz = z/Chunk.CZ;
        int bx = x%Chunk.CX;
        int by = y%Chunk.CY;
        int bz = z%Chunk.CZ;
        if(cx>=SCX || cy>=SCY || cz>=SCZ || cx<0 || cy<0 || cz<0 || bx>=Chunk.CX || by>=Chunk.CY || bz>=Chunk.CZ || bx<0 || by<0 || bz<0)
            return;
        c[cx][cy][cz].removeBlock(bx, by, bz);
    }
}
