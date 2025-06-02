package Assembly.Enjoyers;

import Assembly.Enjoyers.Screens.MainMenuScreen;
import Assembly.Enjoyers.Screens.SettingsScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainGame extends Game {
    public SpriteBatch batch;

    public MainMenuScreen mainMenuScreen;
    public SettingsScreen settingsScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();

        mainMenuScreen = new MainMenuScreen(this);
        settingsScreen = new SettingsScreen(this);

        setScreen(mainMenuScreen);
    }

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}
