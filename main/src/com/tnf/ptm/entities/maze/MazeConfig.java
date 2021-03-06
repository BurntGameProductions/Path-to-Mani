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
package com.tnf.ptm.entities.maze;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import com.tnf.ptm.gfx.TextureManager;
import com.tnf.ptm.common.PtmMath;
import com.tnf.ptm.handler.files.HullConfigManager;
import com.tnf.ptm.common.CollisionMeshLoader;
import com.tnf.ptm.common.ShipConfig;
import com.tnf.ptm.entities.chunk.SpaceEnvConfig;
import com.tnf.ptm.entities.item.ItemManager;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;

public class MazeConfig {
    public final ArrayList<MazeTile> innerWalls;
    public final ArrayList<MazeTile> innerPasses;
    public final ArrayList<MazeTile> borderWalls;
    public final ArrayList<MazeTile> borderPasses;
    public final ArrayList<ShipConfig> outerEnemies;
    public final ArrayList<ShipConfig> innerEnemies;
    public final ArrayList<ShipConfig> bosses;
    public final SpaceEnvConfig envConfig;

    public MazeConfig(ArrayList<MazeTile> innerWalls, ArrayList<MazeTile> innerPasses, ArrayList<MazeTile> borderWalls,
                      ArrayList<MazeTile> borderPasses, ArrayList<ShipConfig> outerEnemies, ArrayList<ShipConfig> innerEnemies,
                      ArrayList<ShipConfig> bosses, SpaceEnvConfig envConfig) {
        this.innerWalls = innerWalls;
        this.innerPasses = innerPasses;
        this.borderWalls = borderWalls;
        this.borderPasses = borderPasses;
        this.outerEnemies = outerEnemies;
        this.innerEnemies = innerEnemies;
        this.bosses = bosses;
        this.envConfig = envConfig;
    }

    public static MazeConfig load(TextureManager textureManager, HullConfigManager hullConfigs, JsonValue mazeNode, ItemManager itemManager) {
        String dirName = "mazeTiles/" + mazeNode.name + "/";
        CollisionMeshLoader collisionMeshLoader = new CollisionMeshLoader(new ResourceUrn(mazeNode.name + "Maze"));
        CollisionMeshLoader.Model paths = collisionMeshLoader.getInternalModel();
        List<TextureAtlas.AtlasRegion> innerBgs = textureManager.getPack(dirName + "innerBg");
        List<TextureAtlas.AtlasRegion> borderBgs = textureManager.getPack(dirName + "borderBg");
        ArrayList<TextureAtlas.AtlasRegion> wallTexs = textureManager.getPack(dirName + "wall");
        ArrayList<TextureAtlas.AtlasRegion> passTexs = textureManager.getPack(dirName + "pass");

        boolean metal = mazeNode.getBoolean("isMetal");
        ArrayList<MazeTile> innerWalls = new ArrayList<>();
        buildTiles(paths, innerWalls, true, metal, innerBgs, wallTexs);
        ArrayList<MazeTile> innerPasses = new ArrayList<>();
        buildTiles(paths, innerPasses, false, metal, innerBgs, passTexs);
        ArrayList<MazeTile> borderWalls = new ArrayList<>();
        buildTiles(paths, borderWalls, true, metal, borderBgs, wallTexs);
        ArrayList<MazeTile> borderPasses = new ArrayList<>();
        buildTiles(paths, borderPasses, false, metal, borderBgs, passTexs);

        ArrayList<ShipConfig> outerEnemies = ShipConfig.loadList(mazeNode.get("outerEnemies"), hullConfigs, itemManager);
        ArrayList<ShipConfig> innerEnemies = ShipConfig.loadList(mazeNode.get("innerEnemies"), hullConfigs, itemManager);
        ArrayList<ShipConfig> bosses = ShipConfig.loadList(mazeNode.get("bosses"), hullConfigs, itemManager);

        SpaceEnvConfig envConfig = new SpaceEnvConfig(mazeNode.get("environment"), textureManager);
        return new MazeConfig(innerWalls, innerPasses, borderWalls, borderPasses, outerEnemies, innerEnemies, bosses, envConfig);
    }

    private static void buildTiles(CollisionMeshLoader.Model paths,
                                   ArrayList<MazeTile> list, boolean wall, boolean metal, List<TextureAtlas.AtlasRegion> bgTexs,
                                   ArrayList<TextureAtlas.AtlasRegion> texs) {
        for (TextureAtlas.AtlasRegion tex : texs) {
            String pathEntry = tex.name + "_" + tex.index + ".png";
            TextureAtlas.AtlasRegion bgTex = PtmMath.elemRnd(bgTexs);
            MazeTile iw = MazeTile.load(tex, paths, wall, pathEntry, metal, bgTex);
            list.add(iw);
        }
    }
}
