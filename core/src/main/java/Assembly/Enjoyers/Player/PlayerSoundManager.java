package Assembly.Enjoyers.Player;

import Assembly.Enjoyers.Utils.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.EnumMap;

import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;

/**
 * Менеджер звукових ефектів для гравця.
 * Зберігає звуки для кожного стану {@link PlayerState} та програє відповідні звукові ефекти.
 */
public class PlayerSoundManager {
    private final EnumMap<PlayerState, Sound> sounds = new EnumMap<>(PlayerState.class);
    private final EnumMap<PlayerState, Float> cooldowns = new EnumMap<>(PlayerState.class);
    private final EnumMap<PlayerState, Float> timers = new EnumMap<>(PlayerState.class);

    private long wallSlideSoundId = -1;

    private final float volume;

    /**
     * Ініціалізує всі звукові ефекти для кожного стану персонажа.
     */
    public PlayerSoundManager(){
        volume = Gdx.app.getPreferences("settings").getFloat("soundVolume", 0.5f);

        sounds.put(PlayerState.RUNNING, load("sounds/steps.ogg"));
        cooldowns.put(PlayerState.RUNNING, 0.3f);

        sounds.put(PlayerState.WALL_CLIMBING, load("sounds/climb.ogg"));
        cooldowns.put(PlayerState.WALL_CLIMBING, 0.5f);

        sounds.put(PlayerState.DASHING, load("sounds/dash.ogg"));
        cooldowns.put(PlayerState.DASHING, 0.1f);

        sounds.put(PlayerState.LANDING, load("sounds/land.ogg"));
        cooldowns.put(PlayerState.LANDING, 0.2f);

        sounds.put(PlayerState.WALL_SLIDING, load("sounds/wallSlide.ogg"));

        sounds.put(PlayerState.JUMPING, load("sounds/jump.ogg"));
        cooldowns.put(PlayerState.JUMPING, 0.3f);

        sounds.put(PlayerState.DYING, load("sounds/death.ogg"));

        for(PlayerState state : PlayerState.values())
            timers.put(state, 0f);
    }

    /**
     * Завантажує пісню за шляхом
     * @param path Шлях у папці assets
     * @return Sound
     */
    private Sound load(String path) {
        return Assets.get(path, Sound.class);
    }

    /**
     * Оновлює таймер звуку
     * @param delta Проміжок часу між поточним та останнім кадром у секундах.
     */
    public void update(float delta) {
        timers.replaceAll((e, v) -> timers.get(e) - delta);
    }

    /**
     * Відтворює звуковий ефект за ключем.
     * @param state Значення гравця
     */
    public void play(PlayerState state) {
        Sound sound = sounds.get(state);
        if (sound == null) return;

        float timeLeft = timers.get(state);
        float cooldown = cooldowns.getOrDefault(state, 0f);

        if (timeLeft <= 0f) {
            sound.play(volume);
            timers.put(state, cooldown);
        }
    }

    /**
     * Окрема функція для повторення звуку спуску зі стіни
     * @param repeat Чи повторювати звук
     */
    public void playWallSlideRepeatable(boolean repeat) {
        Sound sound = sounds.get(PlayerState.WALL_SLIDING);
        if (sound == null) return;

        if (repeat) {
            if (wallSlideSoundId == -1) {
                wallSlideSoundId = sound.loop(0.3f * volume);
            }
        } else {
            if (wallSlideSoundId != -1) {
                sound.stop(wallSlideSoundId);
                wallSlideSoundId = -1;
            }
        }
    }
}
