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
import com.tnf.ptm.assets.audio.PlayableSound;
import com.tnf.ptm.common.PtmMath;
import com.tnf.ptm.common.DmgType;
import com.tnf.ptm.common.PtmGame;
import com.tnf.ptm.entities.projectile.ProjectileConfig;
import com.tnf.ptm.sound.OggSoundSet;
import com.tnf.ptm.assets.Assets;
import com.tnf.ptm.assets.json.Json;
import com.tnf.ptm.common.HardnessCalc;
import com.tnf.ptm.sound.OggSoundManager;
import org.terasology.assets.ResourceUrn;

import java.util.Arrays;
import java.util.List;

public class Gun implements PtmItem {

    public final Config config;
    public int ammo;
    public float reloadAwait;
    private int myEquipped;

    public Gun(Config config, int ammo, float reloadAwait) {
        this.config = config;
        this.ammo = ammo;
        this.reloadAwait = reloadAwait;
    }

    public Gun(Config config, int ammo, float reloadAwait, int equipped) {
        this(config, ammo, reloadAwait);
        this.myEquipped = equipped;
    }

    @Override
    public String getDisplayName() {
        return config.displayName;
    }

    @Override
    public float getPrice() {
        return config.price;
    }

    @Override
    public String getDesc() {
        return config.desc;
    }

    @Override
    public Gun copy() {
        return new Gun(config, ammo, reloadAwait, myEquipped);
    }

    @Override
    public boolean isSame(PtmItem item) {
        return false;
    }

    @Override
    public TextureAtlas.AtlasRegion getIcon(PtmGame game) {
        return config.icon;
    }

    @Override
    public PtmItemType getItemType() {
        return config.itemType;
    }

    @Override
    public String getCode() {
        return config.code;
    }

    public boolean canShoot() {
        return ammo > 0 || reloadAwait > 0;
    }

    public int isEquipped() {
        return myEquipped;
    }

    public void setEquipped(int equipped) {
        myEquipped = equipped;
    }

    public static class Config {
        public final float minAngleVar;
        public final float maxAngleVar;
        public final float angleVarDamp;
        public final float angleVarPerShot;
        public final float timeBetweenShots;
        public final float reloadTime;
        public final float gunLength;
        public final String displayName;
        public final TextureAtlas.AtlasRegion tex;
        public final boolean lightOnShot;
        public final int price;
        public final String desc;
        public final float dps;
        public final Gun example;
        public final Clip.Config clipConf;
        public final PlayableSound shootSound;
        public final PlayableSound reloadSound;
        public final TextureAtlas.AtlasRegion icon;
        public final boolean fixed;
        public final float meanDps;
        public final PtmItemType itemType;
        public final float texLenPerc;
        public final String code;

        public Config(float minAngleVar, float maxAngleVar, float angleVarDamp, float angleVarPerShot,
                      float timeBetweenShots,
                      float reloadTime, float gunLength, String displayName,
                      boolean lightOnShot, int price,
                      Clip.Config clipConf, PlayableSound shootSound, PlayableSound reloadSound, TextureAtlas.AtlasRegion tex,
                      TextureAtlas.AtlasRegion icon, boolean fixed, PtmItemType itemType, float texLenPerc, String code) {
            this.shootSound = shootSound;
            this.reloadSound = reloadSound;

            this.tex = tex;

            this.maxAngleVar = maxAngleVar;
            this.minAngleVar = minAngleVar;
            this.angleVarDamp = angleVarDamp;
            this.angleVarPerShot = angleVarPerShot;
            this.timeBetweenShots = timeBetweenShots;
            this.reloadTime = reloadTime;
            this.gunLength = gunLength;
            this.displayName = displayName;
            this.lightOnShot = lightOnShot;
            this.price = price;
            this.clipConf = clipConf;
            this.icon = icon;
            this.fixed = fixed;
            this.itemType = itemType;
            this.texLenPerc = texLenPerc;
            this.code = code;

            dps = HardnessCalc.getShotDps(this, clipConf.projConfig.dmg);
            meanDps = HardnessCalc.getGunMeanDps(this);
            this.desc = makeDesc();
            example = new Gun(this, 0, 0);
        }

        public static void load(ResourceUrn gunName, ItemManager itemManager, OggSoundManager soundManager, PtmItemTypes types) {
            Json json = Assets.getJson(gunName);
            JsonValue rootNode = json.getJsonValue();

            float minAngleVar = rootNode.getFloat("minAngleVar", 0);
            float maxAngleVar = rootNode.getFloat("maxAngleVar");
            float angleVarDamp = rootNode.getFloat("angleVarDamp");
            float angleVarPerShot = rootNode.getFloat("angleVarPerShot");
            float timeBetweenShots = rootNode.getFloat("timeBetweenShots");
            float reloadTime = rootNode.getFloat("reloadTime");
            float gunLength = rootNode.getFloat("gunLength");
            float texLenPerc = rootNode.getFloat("texLenPerc", 1);
            String displayName = rootNode.getString("displayName");
            boolean lightOnShot = rootNode.getBoolean("lightOnShot", false);
            int price = rootNode.getInt("price");
            String clipName = rootNode.getString("clipName");
            List<String> reloadSoundUrns = Arrays.asList(rootNode.get("reloadSounds").asStringArray());
            OggSoundSet reloadSoundSet = new OggSoundSet(soundManager, reloadSoundUrns, 1.0f);
            List<String> shootSoundUrns = Arrays.asList(rootNode.get("shootSounds").asStringArray());
            float shootPitch = rootNode.getFloat("shootSoundPitch", 1);
            OggSoundSet shootSoundSet = new OggSoundSet(soundManager, shootSoundUrns, shootPitch);
            boolean fixed = rootNode.getBoolean("fixed", false);
            PtmItemType itemType = fixed ? types.fixedGun : types.gun;

            Clip.Config clipConf = null;
            if (!clipName.isEmpty()) {
                Clip clip = ((Clip) itemManager.getExample(clipName));
                if (clip == null) {
                    Clip.Config.load(new ResourceUrn(clipName), itemManager, types);
                    clip = ((Clip) itemManager.getExample(clipName));
                }
                clipConf = clip.getConfig();
            }

            json.dispose();

            TextureAtlas.AtlasRegion tex = Assets.getAtlasRegion(gunName);
            TextureAtlas.AtlasRegion icon = Assets.getAtlasRegion(new ResourceUrn(gunName + "Icon"));

            Config gunConfig = new Config(minAngleVar, maxAngleVar, angleVarDamp, angleVarPerShot, timeBetweenShots,
                                            reloadTime, gunLength, displayName, lightOnShot, price, clipConf, shootSoundSet,
                                                reloadSoundSet, tex, icon, fixed, itemType, texLenPerc, gunName.toString());
            itemManager.registerItem(gunConfig.example);
        }

        private String makeDesc() {
            StringBuilder sb = new StringBuilder();
            ProjectileConfig pc = clipConf.projConfig;
            sb.append(fixed ? "Heavy gun (no rotation)\n" : "Light gun (auto rotation)\n");
            if (pc.dmg > 0) {
                sb.append("Dmg: ").append(PtmMath.nice(dps)).append("/s\n");
                DmgType dmgType = pc.dmgType;
                if (dmgType == DmgType.ENERGY) {
                    sb.append("Weak against armor\n");
                } else if (dmgType == DmgType.BULLET) {
                    sb.append("Weak against shields\n");
                }
            } else if (pc.emTime > 0) {
                sb.append("Disables enemy ships for ").append(PtmMath.nice(pc.emTime)).append(" s\n");
            }
            if (pc.density > 0) {
                sb.append("Knocks enemies back\n");
            }
            sb.append("Reload: ").append(PtmMath.nice(reloadTime)).append(" s\n");
            if (clipConf.infinite) {
                sb.append("Infinite ammo\n");
            } else {
                sb.append("Uses ").append(clipConf.plural).append("\n");
            }
            return sb.toString();
        }
    }
}
