package com.skyseasoft.gltest2.tools;

/**
 * Created by junodeveloper on 15. 7. 27..
 */
public class Collision {

    public static boolean isCubeCollided(float x1, float y1, float z1, float w1, float h1, float d1, float x2, float y2, float z2, float w2,
                                         float h2, float d2) {
        if(x1 + w1 > x2 && x1 < x2 + w2 && y1 + h1 > y2 && y1 < y2 + h2 && z1 + d1 > z2 && z1 < z2 + d2)
        {
            return true;
        }
        return false;
    }
}
