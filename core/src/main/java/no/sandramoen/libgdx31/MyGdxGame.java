package no.sandramoen.libgdx31;


import no.sandramoen.libgdx31.screens.gameplay.LevelScreen;
import no.sandramoen.libgdx31.screens.shell.LevelSelectScreen;
import no.sandramoen.libgdx31.utils.BaseGame;

public class MyGdxGame extends BaseGame {

	@Override
	public void create() {
		super.create();
		// setActiveScreen(new SplashScreen());
		// setActiveScreen(new MenuScreen());

        setActiveScreen(new LevelSelectScreen());
		// setActiveScreen(new LevelScreen("up"));
	}
}

