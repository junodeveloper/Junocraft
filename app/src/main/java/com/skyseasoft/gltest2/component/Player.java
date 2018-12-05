package com.skyseasoft.gltest2.component;

import android.opengl.Matrix;

import com.skyseasoft.gltest2.screen.GLRenderer;
import com.skyseasoft.gltest2.screen.GLResource;
import com.skyseasoft.gltest2.tools.VectorHelper;

/**
 * Created by junodeveloper on 15. 7. 27..
 */
public class Player {
    float x, y, z;
    float velocityY;
    float speed;
    float[] angles = new float[3];

    int id;

    GLRenderer renderer;
    GLResource res;

    public Player(GLRenderer renderer, GLResource res) {
        this.renderer = renderer;
        this.res = res;
        speed = 3.0f;
    }

    public void moveTo(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public void updateGravityEffect() {
        y += velocityY;
        velocityY -= 0.1f;
    }

    public void accelerateY(float accY) {
        velocityY += accY;
    }

    public float getSpeed() {
        return speed;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }

    public void setAngles(float a0, float a1, float a2) {
        angles[0] = a0;
        angles[1] = a1;
        angles[2] = a2;
    }

    public float[] getAngles() {
        return angles;
    }

    public void moveForward(float step) {
        float[] pos = getForwardPos(step);
        x = pos[0];
        //y = pos[1];
        z = pos[2];
    }

    public float[] getForwardPos(float step) {
        float[] duVector = VectorHelper.getDUVector(angles[0], angles[1], angles[2]);
        return new float[]{x + duVector[0] * speed * step, y + duVector[1] * speed * step, z + duVector[2] * speed * step};
    }

    public void drawPlayer() {
        res.cuboid.setTexture(res.tile);
        Matrix.setIdentityM(res.cuboid.mModelMatrix, 0);
        Matrix.translateM(res.cuboid.mModelMatrix, 0, x, y, z);
        Matrix.scaleM(res.rectTop.mModelMatrix, 0, 3.0f, 3.0f, 1.0f);
        renderer.drawObject(res.cuboid);
    }
}
