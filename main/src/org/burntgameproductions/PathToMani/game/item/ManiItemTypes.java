

package org.burntgameproductions.PathToMani.game.item;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.burntgameproductions.PathToMani.game.sound.SoundManager;
import org.burntgameproductions.PathToMani.files.FileManager;
import org.burntgameproductions.PathToMani.game.GameColors;
import org.burntgameproductions.PathToMani.game.sound.ManiSound;

public class ManiItemTypes {
  public final ManiItemType clip;
  public final ManiItemType shield;
  public final ManiItemType armor;
  public final ManiItemType abilityCharge;
  public final ManiItemType gun;
  public final ManiItemType money;
  public final ManiItemType medMoney;
  public final ManiItemType bigMoney;
  public final ManiItemType repair;
  public final ManiItemType fixedGun;

  public ManiItemTypes(SoundManager soundManager, GameColors cols) {
    JsonReader r = new JsonReader();
    FileHandle configFile = FileManager.getInstance().getItemsDirectory().child("types.json");
    JsonValue parsed = r.parse(configFile);
    clip = load("clip", soundManager, configFile, parsed, cols);
    shield = load("shield", soundManager, configFile, parsed, cols);
    armor = load("armor", soundManager, configFile, parsed, cols);
    abilityCharge = load("abilityCharge", soundManager, configFile, parsed, cols);
    gun = load("gun", soundManager, configFile, parsed, cols);
    fixedGun = load("fixedGun", soundManager, configFile, parsed, cols);
    money = load("money", soundManager, configFile, parsed, cols);
    medMoney = load("medMoney", soundManager, configFile, parsed, cols);
    bigMoney = load("bigMoney", soundManager, configFile, parsed, cols);
    repair = load("repair", soundManager, configFile, parsed, cols);
  }

  private ManiItemType load(String name, SoundManager soundManager, FileHandle configFile, JsonValue parsed, GameColors cols) {
    JsonValue node = parsed.get(name);
    Color color = cols.load(node.getString("color"));
    ManiSound pickUpSound = soundManager.getSound(node.getString("pickUpSound"), configFile);
    float sz = node.getFloat("sz");
    return new ManiItemType(color, pickUpSound, sz);
  }
}