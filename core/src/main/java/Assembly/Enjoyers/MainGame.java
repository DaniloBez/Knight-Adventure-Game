package Assembly.Enjoyers;

import Assembly.Enjoyers.Screens.MainMenuScreen;
import Assembly.Enjoyers.Screens.SettingsScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;

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

    private Sound buttonPressed;
    private float volume;

    /**
     * Метод викликається при запуску гри. Ініціалізує ресурси та встановлює головний екран.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();

        mainMenuScreen = new MainMenuScreen(this);
        settingsScreen = new SettingsScreen(this);

        buttonPressed = audio.newSound(files.internal("sounds/button.mp3"));
        loadVolume();

        setScreen(mainMenuScreen);
    }

    /**
     * Програє звук натиску на кнопку.
     */
    public void buttonPress(){
        buttonPressed.play(volume);
    }

    /**
     * Завантажує гучність.
     */
    public void loadVolume(){
        volume = Gdx.app.getPreferences("settings").getFloat("soundVolume", 0.5f);
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
