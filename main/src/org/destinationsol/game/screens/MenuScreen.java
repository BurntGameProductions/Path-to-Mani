/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.screens;

import org.destinationsol.GameOptions;
import org.destinationsol.ManiApplication;
import org.destinationsol.common.ManiColor;
import org.destinationsol.game.ManiGame;
import org.destinationsol.menu.MenuLayout;
import org.destinationsol.ui.ManiInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.ManiUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class MenuScreen implements ManiUiScreen {
  private final List<SolUiControl> myControls;
  private final SolUiControl myCloseCtrl;
  private final SolUiControl myExitCtrl;
  private final SolUiControl myRespawnCtrl;
  private final SolUiControl mySoundVolCtrl;
  private final SolUiControl myMusVolCtrl;

  public MenuScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<SolUiControl>();

    mySoundVolCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true);
    mySoundVolCtrl.setDisplayName("Sound Vol");
    myControls.add(mySoundVolCtrl);
    myMusVolCtrl = new SolUiControl(menuLayout.buttonRect(-1, 0), true);
    myMusVolCtrl.setDisplayName("Music Vol");
    myControls.add(myMusVolCtrl);
    myRespawnCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true);
    myRespawnCtrl.setDisplayName("Respawn");
    myControls.add(myRespawnCtrl);
    myExitCtrl = new SolUiControl(menuLayout.buttonRect(-1, 3), true);
    myExitCtrl.setDisplayName("Exit");
    myControls.add(myExitCtrl);
    myCloseCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyClose());
    myCloseCtrl.setDisplayName("Resume");
    myControls.add(myCloseCtrl);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    ManiGame g = cmp.getGame();
    g.setPaused(true);
    ManiInputManager im = cmp.getInputMan();
    GameOptions options = cmp.getOptions();
    mySoundVolCtrl.setDisplayName("Sound Volume: " + getVolName(options));
    if (mySoundVolCtrl.isJustOff()) {
      options.advanceSoundVolMul();
    }
    myMusVolCtrl.setDisplayName("Music Volume: " + getMusName(options));
    if(myMusVolCtrl.isJustOff()){
    	options.advanceMusicVolMul();
    }
    if (myRespawnCtrl.isJustOff()) {
      g.respawn();
      im.setScreen(cmp, g.getScreens().mainScreen);
      g.setPaused(false);
    }
    if (myExitCtrl.isJustOff()) {
      cmp.finishGame();
    }
    if (myCloseCtrl.isJustOff()) {
      g.setPaused(false);
      im.setScreen(cmp, g.getScreens().mainScreen);
    }
  }

  private String getVolName(GameOptions options) {
    float volMul = options.volMul;
    if (volMul == 0) return "Off";
    else if (volMul < .4f) return "Low";
    else if (volMul < .7f) return "High";
    else {return "Max";}
  }
  private String getMusName(GameOptions options)
  {
	  float musMul = options.musicMul;
	  if (musMul == 0) return "Off";
	  if (musMul < .4f) return "Low";
	  if (musMul < .7f) return "High";
	  return "Max";
  }
  @Override
  public void drawBg(UiDrawer uiDrawer, ManiApplication cmp) {
    uiDrawer.draw(uiDrawer.filler, ManiColor.UI_BG);
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, ManiApplication cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

  @Override
  public boolean isCursorOnBg(ManiInputManager.Ptr ptr) {
    return true;
  }

  @Override
  public void onAdd(ManiApplication cmp) {

  }

  @Override
  public void blurCustom(ManiApplication cmp) {

  }
}
