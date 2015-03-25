package com.example.richykapadia.jboxtutorial;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by richykapadia on 3/24/15.
 */
public class BaseObject {

    public Color3 color = new Color3(255, 255, 255);
    public boolean visible = true;

    private int id;
    private Body body = null;

    protected Vec2 position = new Vec2(0.0f, 0.0f);
    protected float rotation = 0.0f;
    protected FloatBuffer vertBuffer;
    protected float[] vertices;

    // Saved for when body is recreated on a vert refresh
    private float friction;
    private float density;
    private float restitution;

    public BaseObject() {

        this.id = MyRenderer.getNextId();
        MyRenderer.actors.add(this);
    }

    public void setVertices(float[] _vertices) {

        this.vertices = _vertices;

        // Allocate a new byte buffer to move the vertices into a FloatBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertBuffer = byteBuffer.asFloatBuffer();
        vertBuffer.put(vertices);
        vertBuffer.position(0);

        if(body != null) {
            destroyPhysicsBody();
            createPhysicsBody(density, friction, restitution);
        }
    }

    public void draw(GL10 gl) {

        if(!visible) { return; }

        // Update local data from physics engine, if applicable
        if(body != null) {
            position = MyRenderer.worldToScreen(body.getPosition());
            rotation = body.getAngle() * 57.2957795786f;
        }

        // Save the current state of things
        gl.glPushMatrix();

        // Enabling this allows us to give GL a pointer to an array containing our vertices
        // This is instead of manually drawing every triangle ourselves using glVertex3f() statements
        // which are missing from GL ES anyways. As far as I know (not that far), this is the only way
        // to actually draw in 1.1
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        // Move to where our object is positioned.
        gl.glTranslatef(position.x, position.y, 1.0f);

        // Set the angle on each axis, 0 on x and y, our angle on z
        gl.glRotatef(0.0f, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);

        // Grab our color, convert it to the 0.0 - 1.0 range, then set it
        Vec3 renderCol = color.toFloat();
        gl.glColor4f(renderCol.x, renderCol.y, renderCol.z, 1.0f);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glPopMatrix();
    }

    public void createPhysicsBody(float _density, float _friction, float _restitution) {

        if(body != null) { return; }

        // Save values
        friction = _friction;
        density = _density;
        restitution = _restitution;

        // Create the body
        BodyDef bd = new BodyDef();

        if(density > 0) {
            bd.type = BodyType.DYNAMIC;
        } else {
            bd.type = BodyType.STATIC;
        }

        bd.position = MyRenderer.screenToWorld(position);

        // Add to physics world body creation queue, will be finalized when possible
        Physics.requestBodyCreation(new BodyQueueDef(id, bd));
    }

    public void destroyPhysicsBody() {

        if(body == null) { return; }

        Physics.destroyBody(body);
        body = null;
    }

    public void onBodyCreation(Body _body) {

        // Threads ftw
        body = _body;

        // Body has been created, make fixture and finalize it
        // Physics world waits for completion before continuing

        // Create fixture from vertices
        PolygonShape shape = new PolygonShape();
        Vec2[] verts = new Vec2[vertices.length / 3];

        int vertIndex = 0;
        for(int i = 0; i < vertices.length; i += 3) {
            verts[vertIndex] = new Vec2(vertices[i] / MyRenderer.getPPM(), vertices[i + 1] / MyRenderer.getPPM());
            vertIndex++;
        }

        shape.set(verts, verts.length);

        // Attach fixture
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = density;
        fd.friction = friction;
        fd.restitution = restitution;

        body.createFixture(fd);
    }

    // Modify the actor or the body
    public void setPosition(Vec2 position) {
        if(body == null) {
            this.position = position;
        } else {
            body.setTransform(MyRenderer.screenToWorld(position), body.getAngle());
        }
    }

    // Modify the actor or the body
    public void setRotation(float rotation) {
        if(body == null) {
            this.rotation = rotation;
        } else {
            body.setTransform(body.getPosition(), rotation * 0.0174532925f); // Convert to radians
        }
    }

    // Get from the physics body if avaliable
    public Vec2 getPosition() {
        if(body == null) {
            return position;
        } else {
            return MyRenderer.worldToScreen(body.getPosition());
        }
    }
    public float getRotation() {
        if(body == null) {
            return rotation;
        } else {
            return body.getAngle() * 57.2957795786f;
        }
    }

    public int getId() { return id; }
}