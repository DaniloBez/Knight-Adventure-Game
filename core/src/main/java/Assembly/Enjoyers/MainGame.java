package Assembly.Enjoyers;

import Assembly.Enjoyers.Screens.MainMenuScreen;
import Assembly.Enjoyers.Screens.SettingsScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Головний клас гри, з якого починається виконання.
 * Ініціалізує глобальні ресурси та стартовий екран.
 */
public class MainGame extends Game {
    /** Основний batch для рендерингу всіх спрайтів */
    public SpriteBatch batch;

    /** Головне меню гри */
    public MainMenuScreen mainMenuScreen;

    /** Екран налаштувань гри */
    public SettingsScreen settingsScreen;

    /**
     * Метод викликається при запуску гри. Ініціалізує ресурси та встановлює головний екран.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();

        mainMenuScreen = new MainMenuScreen(this);
        settingsScreen = new SettingsScreen(this);

        setScreen(mainMenuScreen);
    }

    /**
     * Метод викликається при завершенні гри. Очищає ресурси.
     */
    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}
