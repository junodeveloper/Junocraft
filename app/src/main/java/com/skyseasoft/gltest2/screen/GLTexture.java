package com.skyseasoft.gltest2.screen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.skyseasoft.gltest2.BitmapHelper;
import com.skyseasoft.gltest2.R;
import com.skyseasoft.gltest2.TextAttribute;
import com.skyseasoft.gltest2.TextureHelper;

import java.util.ArrayList;

/**
 * Created by junodeveloper on 15. 6. 28..
 */
public class GLTexture {
    public final int SIZE = 512;

    public int texHandle;
    private int resId;
    private Context mContext;
    private Bitmap mBackground;

    private ArrayList<TextAttribute> textList;

    public GLTexture(Context context, int resourceId) {
        Bitmap bitmap = BitmapHelper.loadBitmap(context, resourceId);
        texHandle = TextureHelper.loadTexture(context, bitmap);
        mContext = context;
        resId = resourceId;
        textList = new ArrayList<TextAttribute>();
    }
    public void addText(String str, float x, float y) {
        addText(str, x, y, 12, 0xff, 0x00, 0x00, 0x00);
    }
    public void addText(String str, float x, float y, int size) {
        addText(str, x, y, size, 0xff, 0x00, 0x00, 0x00);
    }
    public void addText(String str, float x, float y, int size, int a, int r, int g, int b) {
        textList.add(new TextAttribute(str, x, y, size, a, r, g, b));
    }
    public void drawAvailableText() {
        // Create an empty, mutable bitmap
        Bitmap bitmap = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_4444);
        // get a canvas to paint over the bitmap
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0);

        Drawable background = mContext.getResources().getDrawable(resId);
        background.setBounds(0, 0, SIZE, SIZE);
        background.draw(canvas); // draw the background to our bitmap
        //canvas.drawBitmap(mBackground, 0, 0, null);
        for(TextAttribute t : textList) {
            // Draw the text
            Paint textPaint = new Paint();
            textPaint.setTextSize(t.size);
            textPaint.setAntiAlias(true);
            textPaint.setARGB(t.a, t.r, t.g, t.b);
            // draw the text centered
            canvas.drawText(t.str, t.x, t.y, textPaint);
        }
        textList.clear();
        loadTextureFromBitmap(bitmap);
        bitmap.recycle();
    }
    public void loadTextureFromBitmap(Bitmap bitmap) {
        if(texHandle>0)
            TextureHelper.deleteTexture(texHandle);
        texHandle = TextureHelper.loadTexture(mContext, bitmap);
    }
}
