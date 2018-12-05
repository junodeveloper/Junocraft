package com.skyseasoft.gltest2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.IntBuffer;

/**
 * Created by junodeveloper on 15. 6. 28..
 */
public class TextureHelper {
    public static final int ATLAS_WIDTH = 2;
    public static final int ATLAS_HEIGHT = 1;
    public static int loadTexture(final Context context, final Bitmap bitmap)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    public static void deleteTexture(int texId){
        if(texId == 0)
            return;
        int[] texArr = {texId};
        GLES20.glDeleteTextures(1, texArr, 0);
    }

    public float[] getCuboidTexCoordFromAtlas(int texNum) {
        int x = texNum % 8;
        int y = texNum / 8;
        float[] coord = new float[72];
        for(int i=0;i<72;i++) {
            if(i%2==0)
                coord[i] = MeshData.cuboidTexCoord2[i%12] / ATLAS_WIDTH + x * 1.0f / ATLAS_WIDTH;
            else
                coord[i] = MeshData.cuboidTexCoord2[i%12] / ATLAS_HEIGHT + y * 1.0f / ATLAS_HEIGHT;
        }
        return coord;
    }
}
