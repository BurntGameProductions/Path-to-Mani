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
package com.tnf.ptm.entities.planet;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import com.tnf.ptm.gfx.TextureManager;
import com.tnf.ptm.handler.files.HullConfigManager;
import com.tnf.ptm.entities.item.TradeConfig;
import com.tnf.ptm.common.GameColors;
import com.tnf.ptm.common.ShipConfig;
import com.tnf.ptm.entities.item.ItemManager;

import java.util.ArrayList;
import java.util.List;

public class PlanetConfig {
    public final String configName;
    public final float minGrav;
    public final float maxGrav;
    public final List<DecoConfig> deco;
    public final List<ShipConfig> groundEnemies;
    public final List<ShipConfig> highOrbitEnemies;
    public final PlanetTiles planetTiles;
    public final ShipConfig stationConfig;
    public final SkyConfig skyConfig;
    public final ArrayList<TextureAtlas.AtlasRegion> cloudTexs;
    public final ArrayList<ShipConfig> lowOrbitEnemies;
    public final int rowCount;
    public final boolean smoothLandscape;
    public final TradeConfig tradeConfig;
    public final boolean hardOnly;
    public final boolean easyOnly;

    public PlanetConfig(String configName, float minGrav, float maxGrav, List<DecoConfig> deco,
                        List<ShipConfig> groundEnemies,
                        List<ShipConfig> highOrbitEnemies, ArrayList<ShipConfig> lowOrbitEnemies,
                        ArrayList<TextureAtlas.AtlasRegion> cloudTexs, PlanetTiles planetTiles,
                        ShipConfig stationConfig, SkyConfig skyConfig, int rowCount, boolean smoothLandscape, TradeConfig tradeConfig,
                        boolean hardOnly, boolean easyOnly) {
        this.configName = configName;
        this.minGrav = minGrav;
        this.maxGrav = maxGrav;
        this.deco = deco;
        this.groundEnemies = groundEnemies;
        this.highOrbitEnemies = highOrbitEnemies;
        this.lowOrbitEnemies = lowOrbitEnemies;
        this.cloudTexs = cloudTexs;
        this.planetTiles = planetTiles;
        this.stationConfig = stationConfig;
        this.skyConfig = skyConfig;
        this.rowCount = rowCount;
        this.smoothLandscape = smoothLandscape;
        this.tradeConfig = tradeConfig;
        this.hardOnly = hardOnly;
        this.easyOnly = easyOnly;
    }

    static PlanetConfig load(TextureManager textureManager, HullConfigManager hullConfigs, JsonValue rootNode,
                             GameColors cols, ItemManager itemManager) {
        float minGrav = rootNode.getFloat("minGrav");
        float maxGrav = rootNode.getFloat("maxGrav");
        List<DecoConfig> deco = DecoConfig.load(rootNode, textureManager);
        ArrayList<ShipConfig> groundEnemies = ShipConfig.loadList(rootNode.get("groundEnemies"), hullConfigs, itemManager);
        ArrayList<ShipConfig> highOrbitEnemies = ShipConfig.loadList(rootNode.get("highOrbitEnemies"), hullConfigs, itemManager);
        ArrayList<ShipConfig> lowOrbitEnemies = ShipConfig.loadList(rootNode.get("lowOrbitEnemies"), hullConfigs, itemManager);
        ShipConfig stationConfig = ShipConfig.load(hullConfigs, rootNode.get("station"), itemManager);
        String cloudPackName = rootNode.getString("cloudTexs");
        ArrayList<TextureAtlas.AtlasRegion> cloudTexs = textureManager.getPack(cloudPackName);
        String groundFolder = rootNode.getString("groundTexs");
        PlanetTiles planetTiles = new PlanetTiles(textureManager, groundFolder);
        SkyConfig skyConfig = SkyConfig.load(rootNode.get("sky"), cols);
        int rowCount = rootNode.getInt("rowCount");
        boolean smoothLandscape = rootNode.getBoolean("smoothLandscape", false);
        TradeConfig tradeConfig = TradeConfig.load(itemManager, rootNode.get("trading"), hullConfigs);
        boolean hardOnly = rootNode.getBoolean("hardOnly", false);
        boolean easyOnly = rootNode.getBoolean("easyOnly", false);
        return new PlanetConfig(rootNode.name, minGrav, maxGrav, deco, groundEnemies, highOrbitEnemies, lowOrbitEnemies, cloudTexs,
                planetTiles, stationConfig, skyConfig, rowCount, smoothLandscape, tradeConfig, hardOnly, easyOnly);
    }
}
