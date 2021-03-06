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
import com.tnf.ptm.entities.planet.Planet;
import com.tnf.ptm.entities.ship.PtmShip;

public class BattleDestProvider {
    public static final float MIN_DIR_CHANGE_AWAIT = 10f;
    public static final float MAX_DIR_CHANGE_AWAIT = 15f;
    private final Vector2 myDest;

    private boolean myStopNearDest;
    private Boolean myCw;
    private float myDirChangeAwait;

    public BattleDestProvider() {
        myDest = new Vector2();
        myCw = PtmMath.test(.5f);
    }

    public Vector2 getDest(PtmShip ship, PtmShip enemy, Planet np, boolean battle, float ts,
                           boolean canShootUnfixed, boolean nearGround) {
        myDirChangeAwait -= ts;
        if (myDirChangeAwait <= 0) {
            int rnd = PtmMath.intRnd(0, 2);
            myCw = rnd == 0 ? null : rnd == 1;
            myDirChangeAwait = PtmMath.rnd(MIN_DIR_CHANGE_AWAIT, MAX_DIR_CHANGE_AWAIT);
        }
        if (!battle) {
            throw new AssertionError("can't flee yet!");
        }
        float prefAngle;
        Vector2 enemyPos = enemy.getPosition();
        float approxRad = ship.getHull().config.getApproxRadius();
        float enemyApproxRad = enemy.getHull().config.getApproxRadius();

        if (nearGround) {
            prefAngle = PtmMath.angle(np.getPos(), enemyPos);
            myStopNearDest = false;
            float dist = canShootUnfixed ? .9f * Const.AUTO_SHOOT_GROUND : .75f * Const.CAM_VIEW_DIST_GROUND;
            dist += approxRad + enemyApproxRad;
            PtmMath.fromAl(myDest, prefAngle, dist);
            myDest.add(enemyPos);
        } else {
            Vector2 shipPos = ship.getPosition();
            float a = PtmMath.angle(enemyPos, shipPos);
            if (myCw != null) {
                a += 90 * PtmMath.toInt(myCw);
            }
            float len = canShootUnfixed ? .9f * Const.AUTO_SHOOT_SPACE : .5f * Const.CAM_VIEW_DIST_SPACE;
            len += approxRad + enemyApproxRad;
            PtmMath.fromAl(myDest, a, len);
            myDest.add(enemyPos);
            myStopNearDest = false;
        }
        return myDest;
    }

    public boolean shouldStopNearDest() {
        return myStopNearDest;
    }
}
