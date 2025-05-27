package Assembly.Enjoyers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * Менеджер музики гри. Відповідає за відтворення фонової музики.
 */
public class MusicManager {
    private static Music backgroundMusic;
    private static Music windNoise;

    /**
     * Ініціалізує і запускає музику в циклі.
     */
    public static void init() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Creepy Forest.wav"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.02f);
        backgroundMusic.play();

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/wind.wav"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.2f);
        backgroundMusic.play();
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
