package com.skyseasoft.gltest2.screen;

import android.content.Context;
import android.opengl.GLES20;

import com.skyseasoft.gltest2.R;
import com.skyseasoft.gltest2.MeshData;

/**
 * Created by junodeveloper on 15. 7. 15..
 */
public class GLResource {
    /* GLObject */
    public GLObject rectTop, rectLeft, rectRight, rectFront, rectBack, rectBottom;
    public GLObject cuboid;
    /* GLTexture */
    public GLTexture grass_top;
    public GLTexture grass_side;
    public GLTexture tile;
    public GLTexture atlas;

    public static final int POSITION_DATA_SIZE = 3;
    public static final int COLOR_DATA_SIZE = 4;
    public static final int NORMAL_DATA_SIZE = 3;
    public static final int TEX_DATA_SIZE = 2;

    private Context mContext;

    public GLResource(Context context) {
        mContext = context;
        prepareTexture();
        prepareObject();
    }

    public void prepareTexture() {
        grass_top = new GLTexture(mContext, R.drawable.grass_top);
        grass_side = new GLTexture(mContext, R.drawable.grass_side);
        tile = new GLTexture(mContext, R.drawable.tile);
        //atlas = new GLTexture(mContext, R.drawable.atlas2);
        atlas = new GLTexture(mContext, R.drawable.atlas3);
    }
    public void prepareObject() {
        rectTop = new GLObject(GLES20.GL_TRIANGLES, MeshData.rectTopPosition, MeshData.rectTopNormal);
        rectTop.setTexCoord(MeshData.rectTopTexCoord);
        rectTop.allocate();
        rectLeft = new GLObject(GLES20.GL_TRIANGLES, MeshData.rectLeftPosition, MeshData.rectLeftNormal);
        rectLeft.setTexCoord(MeshData.rectLeftTexCoord);
        rectLeft.allocate();
        rectRight = new GLObject(GLES20.GL_TRIANGLES, MeshData.rectRightPosition, MeshData.rectRightNormal);
        rectRight.setTexCoord(MeshData.rectRightTexCoord);
        rectRight.allocate();
        rectFront = new GLObject(GLES20.GL_TRIANGLES, MeshData.rectFrontPosition, MeshData.rectFrontNormal);
        rectFront.setTexCoord(MeshData.rectFrontTexCoord);
        rectFront.allocate();
        rectBack = new GLObject(GLES20.GL_TRIANGLES, MeshData.rectBackPosition, MeshData.rectBackNormal);
        rectBack.setTexCoord(MeshData.rectBackTexCoord);
        rectBack.allocate();
        rectBottom = new GLObject(GLES20.GL_TRIANGLES, MeshData.rectBottomPosition, MeshData.rectBottomNormal);
        rectBottom.setTexCoord(MeshData.rectBottomTexCoord);
        rectBottom.allocate();
        cuboid = new GLObject(GLES20.GL_TRIANGLES, MeshData.cuboidPosition, MeshData.cuboidNormal);
        cuboid.setTexCoord(MeshData.cuboidTexCoord);
        cuboid.allocate();
    }
}
