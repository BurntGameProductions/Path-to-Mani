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
package com.tnf.ptm.handler.input;

import com.badlogic.gdx.math.Vector2;
import com.tnf.ptm.common.Const;
import com.tnf.ptm.common.PtmMath;
import com.tnf.ptm.handler.ObjectManager;
import com.tnf.ptm.common.PtmObject;
import com.tnf.ptm.common.PtmGame;
import com.tnf.ptm.entities.planet.Planet;
import com.tnf.ptm.entities.ship.FarShip;
import com.tnf.ptm.entities.ship.PtmShip;
import com.tnf.ptm.entities.ship.hulls.HullConfig;

import java.util.List;

/**
 * Flies near the given ship. When the ship is destroyed, floats
 */
public class Guardian implements MoveDestProvider {
    public static final float DIST = 1.5f;

    private final Pilot myTargetPilot;
    private final Vector2 myDest;
    private final float myRelAngle;

    private PtmShip myTarget;
    private FarShip myFarTarget;

    public Guardian(PtmGame game, HullConfig hullConfig, Pilot targetPilot, Vector2 targetPos, HullConfig targetHc,
                    float relAngle) {
        myTargetPilot = targetPilot;
        myDest = new Vector2();
        myRelAngle = relAngle;
        setDest(game, targetPos, targetHc.getApproxRadius(), hullConfig);
    }

    @Override
    public Vector2 getDest() {
        return myDest;
    }

    @Override
    public boolean shouldAvoidBigObjs() {
        return false;
    }

    @Override
    public float getDesiredSpdLen() {
        return Const.MAX_MOVE_SPD;
    }

    @Override
    public boolean shouldStopNearDest() {
        return true;
    }

    @Override
    public void update(PtmGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, PtmShip nearestEnemy) {
        updateTarget(game);
        myDest.set(shipPos);
        Vector2 targetPos;
        float targetApproxRad;
        if (myTarget == null) {
            if (myFarTarget == null) {
                return;
            }
            targetPos = myFarTarget.getPos();
            targetApproxRad = myFarTarget.getHullConfig().getApproxRadius();
        } else {
            targetPos = myTarget.getPosition();
            targetApproxRad = myTarget.getHull().config.getApproxRadius();
        }
        setDest(game, targetPos, targetApproxRad, hullConfig);
    }

    public void updateTarget(PtmGame game) {
        ObjectManager om = game.getObjMan();
        List<PtmObject> objs = om.getObjs();
        if (myTarget != null && objs.contains(myTarget)) {
            return;
        }
        myTarget = null;
        List<FarShip> farShips = om.getFarShips();
        if (myFarTarget != null && farShips.contains(myFarTarget)) {
            return;
        }
        myFarTarget = null;

        for (int i = 0, objsSize = objs.size(); i < objsSize; i++) {
            PtmObject o = objs.get(i);
            if (!(o instanceof PtmShip)) {
                continue;
            }
            PtmShip other = (PtmShip) o;
            if (other.getPilot() != myTargetPilot) {
                continue;
            }
            myTarget = other;
            return;
        }
        for (int i = 0, farObjsSize = farShips.size(); i < farObjsSize; i++) {
            FarShip other = farShips.get(i);
            if (other.getPilot() != myTargetPilot) {
                continue;
            }
            myFarTarget = other;
            return;
        }
    }

    private void setDest(PtmGame game, Vector2 targetPos, float targetApproxRad, HullConfig hullConfig) {
        Planet np = game.getPlanetMan().getNearestPlanet(targetPos);
        float desiredAngle = myRelAngle;
        if (np.isNearGround(targetPos)) {
            desiredAngle = PtmMath.angle(np.getPos(), targetPos);
        }
        PtmMath.fromAl(myDest, desiredAngle, targetApproxRad + DIST + hullConfig.getApproxRadius());
        myDest.add(targetPos);
    }

    @Override
    public Boolean shouldManeuver(boolean canShoot, PtmShip nearestEnemy, boolean nearGround) {
        if (!canShoot) {
            return null;
        }
        Vector2 targetPos = null;
        if (myTarget != null) {
            targetPos = myTarget.getPosition();
        } else if (myFarTarget != null) {
            targetPos = myFarTarget.getPos();
        }
        float maxManeuverDist = 2 * (nearGround ? Const.CAM_VIEW_DIST_GROUND : Const.CAM_VIEW_DIST_SPACE);
        if (targetPos != null && maxManeuverDist < targetPos.dst(nearestEnemy.getPosition())) {
            return null;
        }
        return true;
    }

    @Override
    public Vector2 getDestSpd() {
        return myTarget == null ? Vector2.Zero : myTarget.getSpd();
    }

    public float getRelAngle() {
        return myRelAngle;
    }
}
