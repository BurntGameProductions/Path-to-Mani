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
import com.tnf.ptm.common.PtmGame;
import com.tnf.ptm.entities.planet.PlanetBind;
import com.tnf.ptm.entities.ship.PtmShip;
import com.tnf.ptm.entities.ship.hulls.HullConfig;
import com.tnf.ptm.common.ShipConfig;

public class StillGuard implements MoveDestProvider {

    private final PlanetBind myPlanetBind;
    private final float myDesiredSpdLen;
    private Vector2 myDest;
    private Vector2 myDestSpd;

    public StillGuard(Vector2 target, PtmGame game, ShipConfig sc) {
        myDest = new Vector2(target);
        myPlanetBind = PlanetBind.tryBind(game, myDest, 0);
        myDesiredSpdLen = sc.hull.getType() == HullConfig.Type.BIG ? Const.BIG_AI_SPD : Const.DEFAULT_AI_SPD;
        myDestSpd = new Vector2();
    }

    @Override
    public Vector2 getDest() {
        return myDest;
    }

    @Override
    public boolean shouldAvoidBigObjs() {
        return myPlanetBind != null;
    }

    @Override
    public float getDesiredSpdLen() {
        return myDesiredSpdLen;
    }

    @Override
    public boolean shouldStopNearDest() {
        return true;
    }

    @Override
    public void update(PtmGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, PtmShip nearestEnemy) {
        if (myPlanetBind != null) {
            Vector2 diff = PtmMath.getVec();
            myPlanetBind.setDiff(diff, myDest, false);
            myDest.add(diff);
            PtmMath.free(diff);
            myPlanetBind.getPlanet().calcSpdAtPos(myDestSpd, myDest);
        }
    }

    @Override
    public Boolean shouldManeuver(boolean canShoot, PtmShip nearestEnemy, boolean nearGround) {
        return true;
    }

    @Override
    public Vector2 getDestSpd() {
        return myDestSpd;
    }
}
