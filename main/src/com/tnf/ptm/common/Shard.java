/*
 * Copyright 2017 TheNightForum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tnf.ptm.common;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.tnf.ptm.handler.dra.Dra;

import java.util.ArrayList;
import java.util.List;

public class Shard implements PtmObject {

    private final Body myBody;
    private final Vector2 myPos;
    private final ArrayList<Dra> myDras;
    private final float myMass;

    private float myAngle;

    public Shard(Body body, ArrayList<Dra> dras) {
        myDras = dras;
        myBody = body;
        myPos = new Vector2();
        myMass = myBody.getMass();
        setParamsFromBody();
    }

    @Override
    public Vector2 getPosition() {
        return myPos;
    }

    @Override
    public FarObj toFarObj() {
        return null;
    }

    @Override
    public List<Dra> getDras() {
        return myDras;
    }

    @Override
    public float getAngle() {
        return myAngle;
    }

    @Override
    public Vector2 getSpd() {
        return null;
    }

    @Override
    public void handleContact(PtmObject other, ContactImpulse impulse, boolean isA, float absImpulse,
                              PtmGame game, Vector2 collPos) {
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public Boolean isMetal() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public void update(PtmGame game) {
        setParamsFromBody();
    }

    private void setParamsFromBody() {
        myPos.set(myBody.getPosition());
        myAngle = myBody.getAngle() * PtmMath.radDeg;
    }

    @Override
    public boolean shouldBeRemoved(PtmGame game) {
        return false;
    }

    @Override
    public void onRemove(PtmGame game) {
        myBody.getWorld().destroyBody(myBody);
    }

    @Override
    public void receiveDmg(float dmg, PtmGame game, Vector2 pos, DmgType dmgType) {
    }

    @Override
    public boolean receivesGravity() {
        return true;
    }

    @Override
    public void receiveForce(Vector2 force, PtmGame game, boolean acc) {
        if (acc) {
            force.scl(myMass);
        }
        myBody.applyForceToCenter(force, true);
    }

}
