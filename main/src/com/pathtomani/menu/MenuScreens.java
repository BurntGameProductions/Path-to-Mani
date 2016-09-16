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

package com.pathtomani.menu;

import com.pathtomani.GameOptions;
import com.pathtomani.TextureManager;
import com.pathtomani.ui.ManiLayouts;

public class MenuScreens {
  public final MainScreen main;
  public final OptionsScreen options;
  public final InputMapScreen inputMapScreen;
  public final ResolutionScreen resolutionScreen;
  public final CreditsScreen credits;
  public final LoadingScreen loading;
  public final NewGameScreen newGame;
  public final NewShipScreen newShip;
  public final PlayScreen playScreen;

  public MenuScreens(ManiLayouts layouts, TextureManager textureManager, boolean mobile, float r, GameOptions gameOptions) {
    MenuLayout menuLayout = layouts.menuLayout;
    main = new MainScreen(menuLayout, textureManager, mobile, r, gameOptions);
    options = new OptionsScreen(menuLayout, gameOptions);
    inputMapScreen = new InputMapScreen(r, gameOptions);
    resolutionScreen = new ResolutionScreen(menuLayout, gameOptions);
    credits = new CreditsScreen(r, gameOptions);
    loading = new LoadingScreen();
    newGame = new NewGameScreen(menuLayout, gameOptions);
    newShip = new NewShipScreen(menuLayout, gameOptions);
    playScreen = new PlayScreen(menuLayout, textureManager, gameOptions);
  }

}
