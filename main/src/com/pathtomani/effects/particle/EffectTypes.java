/*
 * Copyright 2016 BurntGameProductions
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

package com.pathtomani.effects.particle;

import java.util.HashMap;

public class EffectTypes {
    private final HashMap<String, EffectType> myTypes;

    public EffectTypes() {
        myTypes = new HashMap<String, EffectType>();
    }

    public EffectType forName(String fileName) {
        EffectType result = myTypes.get(fileName);

        if (result == null) {
            result = new EffectType(fileName);
            myTypes.put(fileName, result);
        }

        return result;
    }
}