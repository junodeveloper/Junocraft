package com.skyseasoft.gltest2.tools;

/**
 * Created by junodeveloper on 15. 7. 28..
 */
public class PerlinNoise {

    public PerlinNoise() {

    }

    private float Noise2(int x, int y) {
        int n = x + y * 57;
        n = (n<<13) ^ n;
        return (float)(1.0 - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
    }

    private float SmoothNoise(int x, int y) {
        float corners = (Noise2(x-1, y-1) + Noise2(x+1,y-1) + Noise2(x-1, y+1) + Noise2(x+1, y+1)) / 16;
        float sides = (Noise2(x-1, y) + Noise2(x+1, y) + Noise2(x, y-1) + Noise2(x, y+1)) / 8;
        float center = Noise2(x, y) / 4;
        return corners + sides + center;
    }

    private float Interpolate(float a0, float a1, float w) {
        return (1.0f - w) * a0 + w * a1;
    }

    private float InterpolatedNoise(float x, float y) {
        int intX = (int)x;
        float fracX = x - intX;
        int intY = (int)y;
        float fracY = y - intY;
        float v1 = SmoothNoise(intX, intY);
        float v2 = SmoothNoise(intX + 1, intY);
        float v3 = SmoothNoise(intX, intY+1);
        float v4 = SmoothNoise(intX+1, intY+1);

        float i1 = Interpolate(v1, v2, fracX);
        float i2 = Interpolate(v3, v4, fracX);
        return Interpolate(i1, i2, fracY);
    }

    public float noise(float x, float y, int octaves, float persistance) {
        float total = 0;
        float frequency = 1.0f;
        float amplitude = 1.0f;
        for(int i=0;i<octaves;i++) {
            total = total + InterpolatedNoise(x * frequency, y * frequency) * amplitude;
            frequency*=2;
            amplitude*=persistance;
        }
        return total;
    }
}
