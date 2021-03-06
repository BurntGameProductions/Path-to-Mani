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

package com.tnf.ptm.entities.ship.hulls;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.tnf.ptm.common.PtmMath;
import com.tnf.ptm.common.PtmGame;
import com.tnf.ptm.handler.dra.Dra;
import com.tnf.ptm.entities.gun.GunMount;
import com.tnf.ptm.entities.item.Engine;
import com.tnf.ptm.entities.item.Gun;
import com.tnf.ptm.entities.item.ItemContainer;
import com.tnf.ptm.gfx.particle.LightSrc;
import com.tnf.ptm.entities.planet.PlanetBind;
import com.tnf.ptm.entities.ship.ShipEngine;
import com.tnf.ptm.entities.ship.PtmShip;
import com.tnf.ptm.entities.Faction;
import com.tnf.ptm.handler.input.Pilot;
import com.tnf.ptm.entities.ship.Door;
import com.tnf.ptm.entities.ship.ForceBeacon;

import java.util.ArrayList;
import java.util.List;

public class Hull {

    public final HullConfig config;
    private final Body myBody;
    private final GunMount myGunMount1;
    private final GunMount myGunMount2;
    private final Fixture myBase;
    private final List<LightSrc> myLightSrcs;
    private final Vector2 myPos;
    private final Vector2 mySpd;
    private final ArrayList<ForceBeacon> myBeacons;
    private final PlanetBind myPlanetBind;
    private final float myMass;
    private final ArrayList<Door> myDoors;
    private final Fixture myShieldFixture;
    public float life;
    private float myAngle;
    private float myRotSpd;
    private ShipEngine myEngine;

    public Hull(PtmGame game, HullConfig hullConfig, Body body, GunMount gunMount1, GunMount gunMount2, Fixture base,
                List<LightSrc> lightSrcs, float life, ArrayList<ForceBeacon> forceBeacons,
                ArrayList<Door> doors, Fixture shieldFixture) {
        config = hullConfig;
        myBody = body;
        myGunMount1 = gunMount1;
        myGunMount2 = gunMount2;
        myBase = base;
        myLightSrcs = lightSrcs;
        this.life = life;
        myDoors = doors;
        myShieldFixture = shieldFixture;
        myPos = new Vector2();
        mySpd = new Vector2();
        myBeacons = forceBeacons;

        myMass = myBody.getMass();
        setParamsFromBody();

        myPlanetBind = config.getType() == HullConfig.Type.STATION ? PlanetBind.tryBind(game, myPos, myAngle) : null;

    }

    public Body getBody() {
        return myBody;
    }

    public Fixture getBase() {
        return myBase;
    }

    public GunMount getGunMount(boolean second) {
        return second ? myGunMount2 : myGunMount1;
    }

    public Gun getGun(boolean second) {
        GunMount m = getGunMount(second);
        if (m == null) {
            return null;
        }
        return m.getGun();
    }

    public void update(PtmGame game, ItemContainer container, Pilot provider, PtmShip ship, PtmShip nearestEnemy) {
        setParamsFromBody();
        boolean controlsEnabled = ship.isControlsEnabled();

        if (myEngine != null) {
            if (true || container.contains(myEngine.getItem())) {
                myEngine.update(myAngle, game, provider, myBody, mySpd, ship, controlsEnabled, myMass);
            } else {
                setEngine(game, ship, null);
            }
        }

        Faction faction = ship.getPilot().getFaction();
        myGunMount1.update(container, game, myAngle, ship, controlsEnabled && provider.isShoot(), nearestEnemy, faction);
        if (myGunMount2 != null) {
            myGunMount2.update(container, game, myAngle, ship, controlsEnabled && provider.isShoot2(), nearestEnemy, faction);
        }

        for (int i = 0, myLightSrcsSize = myLightSrcs.size(); i < myLightSrcsSize; i++) {
            LightSrc src = myLightSrcs.get(i);
            src.update(true, myAngle, game);
        }

        for (int i = 0, myBeaconsSize = myBeacons.size(); i < myBeaconsSize; i++) {
            ForceBeacon b = myBeacons.get(i);
            b.update(game, myPos, myAngle, ship);
        }

        for (int i = 0, myDoorsSize = myDoors.size(); i < myDoorsSize; i++) {
            Door door = myDoors.get(i);
            door.update(game, ship);
        }

        if (myPlanetBind != null) {
            Vector2 spd = PtmMath.getVec();
            myPlanetBind.setDiff(spd, myPos, true);
            float fps = 1 / game.getTimeStep();
            spd.scl(fps);
            myBody.setLinearVelocity(spd);
            PtmMath.free(spd);
            float angleDiff = myPlanetBind.getDesiredAngle() - myAngle;
            myBody.setAngularVelocity(angleDiff * PtmMath.degRad * fps);
        }
    }

    private void setParamsFromBody() {
        myPos.set(myBody.getPosition());
        myAngle = myBody.getAngle() * PtmMath.radDeg;
        myRotSpd = myBody.getAngularVelocity() * PtmMath.radDeg;
        mySpd.set(myBody.getLinearVelocity());
    }

    public void onRemove(PtmGame game) {
        for (Door door : myDoors) {
            door.onRemove(game);
        }
        myBody.getWorld().destroyBody(myBody);
        if (myEngine != null) {
            myEngine.onRemove(game, myPos);
        }

    }

    public void setEngine(PtmGame game, PtmShip ship, Engine ei) {
        List<Dra> dras = ship.getDras();
        if (myEngine != null) {
            List<Dra> dras1 = myEngine.getDras();
            dras.removeAll(dras1);
            game.getDraMan().removeAll(dras1);
            myEngine = null;
        }
        if (ei != null) {
            myEngine = new ShipEngine(game, ei, config.getE1Pos(), config.getE2Pos(), ship);
            List<Dra> dras1 = myEngine.getDras();
            dras.addAll(dras1);
            game.getDraMan().addAll(dras1);
        }
    }

    public float getAngle() {
        return myAngle;
    }

    public Vector2 getPos() {
        return myPos;
    }

    public Vector2 getSpd() {
        return mySpd;
    }

    public Engine getEngine() {
        return myEngine == null ? null : myEngine.getItem();
    }

    public float getRotSpd() {
        return myRotSpd;
    }

    public ArrayList<Door> getDoors() {
        return myDoors;
    }

    public Fixture getShieldFixture() {
        return myShieldFixture;
    }

    public float getMass() {
        return myMass;
    }

    public HullConfig getHullConfig() {
        return config;
    }
}
