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
package old.tnf.ptm.game.input;

import com.badlogic.gdx.math.Vector2;
import com.tnf.ptm.common.Const;
import com.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.planet.Planet;

public class BigObjAvoider {

    public static final float MAX_DIST_LEN = 2 * (Const.MAX_GROUND_HEIGHT + Const.ATM_HEIGHT);
    private Vector2 myProj;

    public BigObjAvoider() {
        myProj = new Vector2();
    }

    public float avoid(PtmGame game, Vector2 from, Vector2 dest, float toDestAngle) {
        float toDestLen = from.dst(dest);
        if (toDestLen > MAX_DIST_LEN) {
            toDestLen = MAX_DIST_LEN;
        }
        float res = toDestAngle;
        Planet p = game.getPlanetMan().getNearestPlanet(from);
        Vector2 pPos = p.getPos();
        float pRad = p.getFullHeight();
        if (dest.dst(pPos) < pRad) {
            pRad = p.getGroundHeight();
        }
        myProj.set(pPos);
        myProj.sub(from);
        PtmMath.rotate(myProj, -toDestAngle);
        if (0 < myProj.x && myProj.x < toDestLen) {
            if (PtmMath.abs(myProj.y) < pRad) {
                toDestLen = myProj.x;
                res = toDestAngle + 45 * PtmMath.toInt(myProj.y < 0);
            }
        }
        Vector2 sunPos = p.getSys().getPos();
        float sunRad = Const.SUN_RADIUS;
        myProj.set(sunPos);
        myProj.sub(from);
        PtmMath.rotate(myProj, -toDestAngle);
        if (0 < myProj.x && myProj.x < toDestLen) {
            if (PtmMath.abs(myProj.y) < sunRad) {
                res = toDestAngle + 45 * PtmMath.toInt(myProj.y < 0);
            }
        }
        return res;
    }
}