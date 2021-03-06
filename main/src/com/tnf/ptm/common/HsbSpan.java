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

import com.badlogic.gdx.graphics.Color;
import com.tnf.ptm.common.ColorSpan;
import com.tnf.ptm.common.PtmColorUtil;
import com.tnf.ptm.common.PtmMath;

public class HsbSpan extends ColorSpan {
    private final float[] myHsbaStart;
    private final float[] myHsbaEnd;

    HsbSpan(float[] start, float[] end) {
        myHsbaStart = start;
        myHsbaEnd = end;
    }

    @Override
    public void set(float perc, Color col) {
        perc = PtmMath.clamp(perc, 0, 1);
        float hue = midVal(0, perc);
        float sat = midVal(1, perc);
        float br = midVal(2, perc);
        float a = midVal(3, perc);
        PtmColorUtil.fromHSB(hue, sat, br, a, col);
    }

    private float midVal(int idx, float perc) {
        float s = myHsbaStart[idx];
        float e = myHsbaEnd[idx];
        return s + perc * (e - s);
    }

}
