package com.skyseasoft.gltest2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by junodeveloper on 15. 7. 5..
 */
public class BitmapHelper {
    public static Bitmap loadBitmap(Context context, int resId) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        return bitmap;
    }
}
