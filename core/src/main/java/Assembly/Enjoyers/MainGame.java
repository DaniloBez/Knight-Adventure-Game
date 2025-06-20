package Assembly.Enjoyers;

import Assembly.Enjoyers.Screens.FinishScreen;
import Assembly.Enjoyers.Screens.LevelsScreen;
import Assembly.Enjoyers.Screens.MainMenuScreen;
import Assembly.Enjoyers.Screens.SettingsScreen;
import Assembly.Enjoyers.Utils.Assets;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

    /**
     * Екран вибору рівнів.
     */
    public LevelsScreen levelsScreen;

    private Sound buttonPressed;
    private float volume;

    /**
     * Метод викликається при запуску гри. Ініціалізує ресурси та встановлює головний екран.
     */
    @Override
    public void create() {
        Assets.init();
        Assets.finishLoading();

        batch = new SpriteBatch();

        mainMenuScreen = new MainMenuScreen(this);
        settingsScreen = new SettingsScreen(this);
        levelsScreen = new LevelsScreen(this);

        buttonPressed = audio.newSound(files.internal("sounds/button.ogg"));
        loadVolume();

        setScreen(mainMenuScreen);
    }

    /**
     * Викликається коли гравець успішно закінчив рівень.
     * Використовується для оптимізації пам'яті.
     * @param screen Вікно кінця рівня, зі статистикою
     */
    public void gameOver(FinishScreen screen) {
        Screen s = this.getScreen();
        if (s != null) s.dispose();

        this.setScreen(screen);
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
