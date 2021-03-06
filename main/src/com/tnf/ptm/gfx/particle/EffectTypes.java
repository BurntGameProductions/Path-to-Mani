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
package com.tnf.ptm.gfx.particle;

import org.terasology.assets.ResourceUrn;

import java.util.HashMap;

public class EffectTypes {
    private final HashMap<ResourceUrn, EffectType> myTypes;

    public EffectTypes() {
        myTypes = new HashMap<>();
    }

    public EffectType forName(ResourceUrn effectName) {
        return myTypes.computeIfAbsent(effectName, effect -> new EffectType(effect));
    }
}
