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

package com.tnf.ptm.entities.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.tnf.ptm.common.PtmMath;
import com.tnf.ptm.entities.item.Engine;
import com.tnf.ptm.common.PtmGame;
import com.tnf.ptm.common.PtmObject;
import com.tnf.ptm.handler.dra.Dra;
import com.tnf.ptm.handler.dra.DraLevel;
import com.tnf.ptm.handler.input.Pilot;
import com.tnf.ptm.gfx.particle.EffectConfig;
import com.tnf.ptm.gfx.particle.LightSrc;
import com.tnf.ptm.gfx.particle.PartMan;
import com.tnf.ptm.gfx.particle.ParticleSrc;

import java.util.ArrayList;
import java.util.List;

public class ShipEngine {
    public static final float MAX_RECOVER_ROT_SPD = 5f;
    public static final float RECOVER_MUL = 15f;
    public static final float RECOVER_AWAIT = 2f;

    private final ParticleSrc myFlameSrc1;
    private final ParticleSrc myFlameSrc2;
    private final LightSrc myLightSrc1;
    private final LightSrc myLightSrc2;
    private final Engine myItem;
    private final List<Dra> myDras;
    private float myRecoverAwait;

    public ShipEngine(PtmGame game, Engine ei, Vector2 e1RelPos, Vector2 e2RelPos, PtmShip ship) {
        myItem = ei;
        myDras = new ArrayList<Dra>();
        EffectConfig ec = myItem.getEffectConfig();
        Vector2 shipPos = ship.getPosition();
        Vector2 shipSpd = ship.getSpd();
        myFlameSrc1 = new ParticleSrc(ec, -1, DraLevel.PART_BG_0, e1RelPos, true, game, shipPos, shipSpd, 0);
        myDras.add(myFlameSrc1);
        myFlameSrc2 = new ParticleSrc(ec, -1, DraLevel.PART_BG_0, e2RelPos, true, game, shipPos, shipSpd, 0);
        myDras.add(myFlameSrc2);
        float lightSz = ec.sz * 2.5f;
        myLightSrc1 = new LightSrc(game, lightSz, true, .7f, new Vector2(e1RelPos), ec.tint);
        myLightSrc1.collectDras(myDras);
        myLightSrc2 = new LightSrc(game, lightSz, true, .7f, new Vector2(e2RelPos), ec.tint);
        myLightSrc2.collectDras(myDras);
    }

    public List<Dra> getDras() {
        return myDras;
    }

    public void update(float angle, PtmGame game, Pilot provider, Body body, Vector2 spd, PtmObject owner,
                       boolean controlsEnabled, float mass) {
        boolean working = applyInput(game, angle, provider, body, spd, controlsEnabled, mass);

        myFlameSrc1.setWorking(working);
        myFlameSrc2.setWorking(working);

        myLightSrc1.update(working, angle, game);
        myLightSrc2.update(working, angle, game);
        if (working) {
            game.getSoundManager().play(game, myItem.getWorkSound(), myFlameSrc1.getPos(), owner); // hack with pos
        }
    }

    private boolean applyInput(PtmGame cmp, float shipAngle, Pilot provider, Body body, Vector2 spd,
                               boolean controlsEnabled, float mass) {
        boolean spdOk = PtmMath.canAccelerate(shipAngle, spd);
        boolean working = controlsEnabled && provider.isUp() && spdOk;

        Engine e = myItem;
        if (working) {
            Vector2 v = PtmMath.fromAl(shipAngle, mass * e.getAcc());
            body.applyForceToCenter(v, true);
            PtmMath.free(v);
        }

        float ts = cmp.getTimeStep();
        float rotSpd = body.getAngularVelocity() * PtmMath.radDeg;
        float desiredRotSpd = 0;
        float rotAcc = e.getRotAcc();
        boolean l = controlsEnabled && provider.isLeft();
        boolean r = controlsEnabled && provider.isRight();
        float absRotSpd = PtmMath.abs(rotSpd);
        if (absRotSpd < e.getMaxRotSpd() && l != r) {
            desiredRotSpd = PtmMath.toInt(r) * e.getMaxRotSpd();
            if (absRotSpd < MAX_RECOVER_ROT_SPD) {
                if (myRecoverAwait > 0) {
                    myRecoverAwait -= ts;
                }
                if (myRecoverAwait <= 0) {
                    rotAcc *= RECOVER_MUL;
                }
            }
        } else {
            myRecoverAwait = RECOVER_AWAIT;
        }
        body.setAngularVelocity(PtmMath.degRad * PtmMath.approach(rotSpd, desiredRotSpd, rotAcc * ts));
        return working;
    }

    public void onRemove(PtmGame game, Vector2 basePos) {
        PartMan pm = game.getPartMan();
        pm.finish(game, myFlameSrc1, basePos);
        pm.finish(game, myFlameSrc2, basePos);
    }

    public Engine getItem() {
        return myItem;
    }
}
