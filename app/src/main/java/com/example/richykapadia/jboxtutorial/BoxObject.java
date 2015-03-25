package com.example.richykapadia.jboxtutorial;

import android.renderscript.BaseObj;

/**
 * Created by richykapadia on 3/24/15.
 */
public class BoxObject extends BaseObject {

    private float width;
    private float height;

    public BoxObject( float w, float h){
        super();
        vertices = new float[12];
        this.height = h;
        this.width = w;
        refreshVertices();
    }

    private void refreshVertices(){
        vertices[0] = width * -0.5f;   //x0
        vertices[1] = height * -0.5f;  //y0
        vertices[2] = 1.0f;            //z0

        vertices[3] = width * -0.5f;
        vertices[4] = height * 0.5f;
        vertices[5] = 1.0f;

        vertices[6] = width * 0.5f;
        vertices[7] = height * -0.5f;
        vertices[8] = 1.0f;

        vertices[9] = width * 0.5f;
        vertices[10] = height * 0.5f;
        vertices[11] = 1.0f;

        setVertices(vertices);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
        refreshVertices();
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
        refreshVertices();
    }
}
