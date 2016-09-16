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

package com.pathtomani.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.pathtomani.common.ManiMath;
import com.pathtomani.game.GameColors;
import com.pathtomani.game.gun.GunItem;
import com.pathtomani.game.particle.EffectTypes;
import com.pathtomani.game.projectile.ProjectileConfigs;
import com.pathtomani.game.ship.AbilityCharge;
import com.pathtomani.game.sound.SoundManager;
import com.pathtomani.common.TextureManager;
import com.pathtomani.game.gun.GunConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemManager {
    private final HashMap<String,ManiItem> myM;
    private final ArrayList<ManiItem> myL;
    public final ProjectileConfigs projConfigs;
    public final TextureAtlas.AtlasRegion moneyIcon;
    public final TextureAtlas.AtlasRegion medMoneyIcon;
    public final TextureAtlas.AtlasRegion bigMoneyIcon;
    public final TextureAtlas.AtlasRegion repairIcon;
    private final EngineItem.Configs myEngineConfigs;
    private final ManiItemTypes myTypes;
    private final RepairItem myRepairExample;

    public ItemManager(TextureManager textureManager, SoundManager soundManager, EffectTypes effectTypes, GameColors gameColors) {

        moneyIcon = textureManager.getTex(TextureManager.ICONS_DIR + "money", null);
        medMoneyIcon = textureManager.getTex(TextureManager.ICONS_DIR + "medMoney", null);
        bigMoneyIcon = textureManager.getTex(TextureManager.ICONS_DIR + "bigMoney", null);
        repairIcon = textureManager.getTex(TextureManager.ICONS_DIR + "repairItem", null);
        myM = new HashMap<String, ManiItem>();

        myTypes = new ManiItemTypes(soundManager, gameColors);
        projConfigs = new ProjectileConfigs(textureManager, soundManager, effectTypes, gameColors);
        myEngineConfigs = EngineItem.Configs.load(soundManager, textureManager, effectTypes, gameColors);

        Shield.Config.loadConfigs(this, soundManager, textureManager, myTypes);
        Armor.Config.loadConfigs(this, soundManager, textureManager, myTypes);
        AbilityCharge.Config.load(this, textureManager, myTypes);

        ClipConfig.load(this, textureManager, myTypes);
        GunConfig.load(textureManager, this, soundManager, myTypes);

        myRepairExample = new RepairItem(myTypes.repair);
        myM.put(myRepairExample.getCode(), myRepairExample);

        myL = new ArrayList<ManiItem>(myM.values());
    }

    public void fillContainer(ItemContainer c, String items) {
        List<ItemConfig> list = parseItems(items);
        for (ItemConfig ic : list) {
            for (int i = 0; i < ic.amt; i++) {
                if (ManiMath.test(ic.chance)) {
                    ManiItem item = ManiMath.elemRnd(ic.examples).copy();
                    c.add(item);
                }
            }
        }
    }

    public List<ItemConfig> parseItems(String items) {
        ArrayList<ItemConfig> result = new ArrayList<ItemConfig>();
        if (items.isEmpty()) {
            return result;
        }
        for (String rec : items.split(" ")) {
            String[] parts = rec.split(":");

            if (parts.length == 0) {
                continue;
            }

            String[] names = parts[0].split("\\|");

            ArrayList<ManiItem> examples = new ArrayList<ManiItem>();
            for (String name : names) {
                int wasEquipped = 0;

                if (name.endsWith("-1")) {
                    wasEquipped = 1;
                    name = name.substring(0, name.length()-2); // Remove equipped number
                }
                else if (name.endsWith("-2")) {
                    wasEquipped = 2;
                    name = name.substring(0, name.length()-2); // Remove equipped number
                }
                ManiItem example = getExample(name.trim());

                if (example == null) {
                    throw new AssertionError("unknown item " + name + "@" + parts[0] + "@" + rec + "@" + items);
                }
                ManiItem itemCopy = example.copy();
                itemCopy.setEquipped(wasEquipped);

                examples.add(itemCopy);
            }

            if (examples.isEmpty()) {
                throw new AssertionError("no item specified @ " + parts[0] + "@" + rec + "@" + items);
            }

            float chance = 1;

            if (parts.length > 1) {
                chance = Float.parseFloat(parts[1]);
                if (chance <= 0 || 1 < chance) {
                    throw new AssertionError(chance);
                }
            }

            int amt = 1;
            if (parts.length > 2) {
                amt = Integer.parseInt(parts[2]);
            }

            ItemConfig ic = new ItemConfig(examples, amt, chance);
            result.add(ic);
        }

        return result;
  }

    public ManiItem getExample(String code) {
        return myM.get(code);
    }

    public ManiItem random() {
        return myL.get(ManiMath.intRnd(myM.size())).copy();
    }

    public void registerItem(ManiItem example) {
        String code = example.getCode();
        ManiItem existing = getExample(code);
        if (existing != null) {
            throw new AssertionError("2 item types registered for item code " + code + ":\n" + existing + " and " + example);
        }
        myM.put(code, example);
    }

    public EngineItem.Configs getEngineConfigs() {
        return myEngineConfigs;
    }

  public MoneyItem moneyItem(float amt) {
    ManiItemType t;
    if (amt == MoneyItem.BIG_AMT) {
      t = myTypes.bigMoney;
    } else if (amt == MoneyItem.MED_AMT) {
      t = myTypes.medMoney;
    } else {
      t = myTypes.money;
    }
    return new MoneyItem(amt, t);
  }

  public RepairItem getRepairExample() {
    return myRepairExample;
  }

  public void addAllGuns(ItemContainer ic) {
    for (ManiItem i : myM.values()) {
      if (i instanceof ClipItem && !((ClipItem) i).getConfig().infinite) {
        for (int j = 0; j < 8; j++) {
          ic.add(i.copy());
        }
      }
    }
    for (ManiItem i : myM.values()) {
      if (i instanceof GunItem) {
        if (ic.canAdd(i)) ic.add(i.copy());
      }
    }
  }

  public List<MoneyItem> moneyToItems(float amt) {
    ArrayList<MoneyItem> res = new ArrayList<MoneyItem>();
    while (amt > MoneyItem.AMT) {
      MoneyItem example;
      if (amt > MoneyItem.BIG_AMT) {
        example = moneyItem(MoneyItem.BIG_AMT);
        amt -= MoneyItem.BIG_AMT;
      } else if (amt > MoneyItem.MED_AMT) {
        example = moneyItem(MoneyItem.MED_AMT);
        amt -= MoneyItem.MED_AMT;
      } else {
        example = moneyItem(MoneyItem.AMT);
        amt -= MoneyItem.AMT;
      }
      res.add(example.copy());
    }
    return res;
  }

    private static ItemManager instance = null;
}
