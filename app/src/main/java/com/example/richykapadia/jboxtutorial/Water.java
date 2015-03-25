package com.example.richykapadia.jboxtutorial;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.particle.ParticleGroup;
import org.jbox2d.particle.ParticleGroupDef;
import org.jbox2d.particle.ParticleSystem;
import org.jbox2d.particle.ParticleType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by richykapadia on 3/24/15.
 */
public class Water {

    private ParticleSystem particleSystem;
    private ParticleGroup particleGroup;
    private PolygonShape shape;
    public Color3 color = new Color3(0, 0, 255);
    protected Vec2 position = new Vec2(0.0f, 0.0f);

    private static int id = 0;
    private FloatBuffer positionBuffer;


    public Water(float size){
        shape = new PolygonShape();
        shape.setAsBox(size, size);
        this.id = getNextId();
        MyRenderer.waterActors.add(this);
    }

    public void draw(GL10 gl){

        particleSystem = new ParticleSystem( Physics.getWorld() );
        ParticleGroupDef def = new ParticleGroupDef();
        def.shape = this.shape;
        def.strength = 1;
        def.flags = ParticleType.b2_waterParticle;
        Vec2 worldPos = MyRenderer.screenToWorld(this.position);
        def.position.set(worldPos.x, worldPos.y);

        this.particleGroup = particleSystem.createParticleGroup(def);


        // Update local data from physics engine, if applicable
        if(particleGroup != null) {
            position = MyRenderer.worldToScreen( particleGroup.getPosition() );

        }

        gl.glPushMatrix();
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glTranslatef(position.x, position.y, 1.0f);
        // Set the angle on each axis, 0 on x and y, our angle on z
        gl.glRotatef(0.0f, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
        Vec3 renderCol = color.toFloat();
        gl.glColor4f(renderCol.x, renderCol.y, renderCol.z, 1.0f);

        Vec2[] posBuffer = particleSystem.getParticlePositionBuffer();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(posBuffer.length * 4 * 2);
        byteBuffer.order(ByteOrder.nativeOrder());
        positionBuffer = byteBuffer.asFloatBuffer();
        for( Vec2 p : posBuffer){
            positionBuffer.put(p.x);
            positionBuffer.put(p.y);
        }
        positionBuffer.position(0);


        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, positionBuffer);
        gl.glDrawArrays(GL10.GL_POINTS, 0, posBuffer.length / 3);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glPopMatrix();


    }


    public void setPosition(Vec2 position){
        this.position = position;
    }

    public static int getNextId(){
        return id++;
    }

}
