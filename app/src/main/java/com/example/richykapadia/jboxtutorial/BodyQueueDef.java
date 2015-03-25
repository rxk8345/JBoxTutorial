package com.example.richykapadia.jboxtutorial;

import org.jbox2d.dynamics.BodyDef;

/**
 * Created by richykapadia on 3/24/15.
 */
public class BodyQueueDef {

    private int actorId;
    private BodyDef bd;

    public BodyQueueDef(int id, BodyDef bd){
        this.actorId = id;
        this.bd = bd;
    }

    public int getActorId() {
        return actorId;
    }

    public BodyDef getBd() {
        return bd;
    }
}
