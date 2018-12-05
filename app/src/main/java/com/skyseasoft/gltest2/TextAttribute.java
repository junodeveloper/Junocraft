package com.skyseasoft.gltest2;

/**
 * Created by junodeveloper on 15. 7. 5..
 */
public class TextAttribute {
    public int size;
    public int a,r,g,b;
    public float x, y;
    public String str;
    public TextAttribute(String str, float x, float y, int size, int a, int r, int g, int b) {
        this.size = size;
        this.x = x;
        this.y = y;
        this.a = a;
        this.r = r;
        this.g = g;
        this.b = b;
        this.str = str;
    }
}
