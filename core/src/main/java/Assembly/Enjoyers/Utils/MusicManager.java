package Assembly.Enjoyers.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * Менеджер музики гри. Відповідає за відтворення фонової музики.
 */
public class MusicManager {
    private static Music backgroundMusic;
    private static Music windNoise;
    private static float volume;

    /**
     * Ініціалізує і запускає музику в циклі.
     */
    public static void init() {
        volume = Gdx.app.getPreferences("settings").getFloat("musicVolume", 0.5f);

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Creepy-Forest.ogg"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.04f * volume);
        backgroundMusic.play();

        windNoise = Gdx.audio.newMusic(Gdx.files.internal("music/wind.ogg"));
        windNoise.setLooping(true);
        windNoise.setVolume(0.4f * volume);
        windNoise.play();
    }

    /**
     * Ставить відтворення музики на паузу
     */
    public static void pause() {
        if (backgroundMusic != null) backgroundMusic.pause();
        if (windNoise != null) windNoise.pause();
    }

    /**
     * Поновлює відтворення музики
     */
    public static void resume() {
        if (backgroundMusic != null) backgroundMusic.play();
        if (windNoise != null) windNoise.play();
    }

    /**
     * Звільняє ресурси
     */
    public static void dispose() {
        if (backgroundMusic != null) backgroundMusic.dispose();
        if (windNoise != null) windNoise.dispose();
    }
}
