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
package com.tnf.ptm.entities.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import com.tnf.ptm.common.PtmGame;
import com.tnf.ptm.assets.Assets;
import com.tnf.ptm.assets.json.Json;
import org.terasology.assets.ResourceUrn;

public class AbilityCharge implements PtmItem {
    private final Config myConfig;

    public AbilityCharge(Config config) {
        myConfig = config;
    }

    @Override
    public String getDisplayName() {
        return myConfig.displayName;
    }

    @Override
    public float getPrice() {
        return myConfig.price;
    }

    @Override
    public String getDesc() {
        return myConfig.desc;
    }

    @Override
    public PtmItem copy() {
        return new AbilityCharge(myConfig);
    }

    @Override
    public boolean isSame(PtmItem item) {
        return item instanceof AbilityCharge && ((AbilityCharge) item).myConfig == myConfig;
    }

    @Override
    public TextureAtlas.AtlasRegion getIcon(PtmGame game) {
        return myConfig.icon;
    }

    @Override
    public PtmItemType getItemType() {
        return myConfig.itemType;
    }

    @Override
    public String getCode() {
        return myConfig.code;
    }

    @Override
    public int isEquipped() {
        return 0;
    }

    @Override
    public void setEquipped(int equipped) { }

    public static class Config {
        public final PtmItemType itemType;
        public final String code;
        public final TextureAtlas.AtlasRegion icon;
        public final float price;
        public final String displayName;
        public final String desc;
        public final AbilityCharge example;

        public Config(TextureAtlas.AtlasRegion icon, float price, String displayName, String desc, PtmItemType itemType,
                      String code) {
            this.icon = icon;
            this.price = price;
            this.displayName = displayName;
            this.desc = desc;
            this.itemType = itemType;
            this.code = code;
            this.example = new AbilityCharge(this);
        }

        public static void load(ResourceUrn abilityName, ItemManager itemManager, PtmItemTypes types) {
            Json json = Assets.getJson(abilityName);
            JsonValue rootNode = json.getJsonValue();

            float price = rootNode.getFloat("price");
            String displayName = rootNode.getString("displayName");
            String desc = rootNode.getString("desc");

            json.dispose();

            TextureAtlas.AtlasRegion icon = Assets.getAtlasRegion(new ResourceUrn(abilityName + "Icon"));

            Config abilityConfig = new Config(icon, price, displayName, desc, types.abilityCharge, abilityName.toString());
            itemManager.registerItem(abilityConfig.example);
        }
    }
}
