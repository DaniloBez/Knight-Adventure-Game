package Assembly.Enjoyers.Player;

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

    /**
     * Ініціалізує всі звукові ефекти для кожного стану персонажа.
     */
    public PlayerSoundManager(){
        sounds.put(PlayerState.RUNNING, load("sounds\\steps.mp3"));
        cooldowns.put(PlayerState.RUNNING, 0.3f);

        sounds.put(PlayerState.WALL_CLIMBING, load("sounds\\climb.mp3"));
        cooldowns.put(PlayerState.WALL_CLIMBING, 0.5f);

        sounds.put(PlayerState.DASHING, load("sounds\\dash.wav"));
        cooldowns.put(PlayerState.DASHING, 0.1f);

        sounds.put(PlayerState.LANDING, load("sounds\\land.wav"));
        cooldowns.put(PlayerState.LANDING, 0.2f);

        sounds.put(PlayerState.WALL_SLIDING, load("sounds\\wallSlide.wav"));

        sounds.put(PlayerState.JUMPING, load("sounds\\jump.wav"));
        cooldowns.put(PlayerState.JUMPING, 0.3f);

        for(PlayerState state : PlayerState.values())
            timers.put(state, 0f);
    }

    /**
     * Завантажує пісню за шляхом
     * @param path Шлях у папці assets
     * @return Sound
     */
    private Sound load(String path) {
        return audio.newSound(files.internal(path));
    }

    /**
     * Оновлює таймер звуку
     * @param delta Проміжок часу між поточним та останнім кадром у секундах.
     */
    public void update(float delta) {
        timers.replaceAll((e, _) -> timers.get(e) - delta);
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
            sound.play(1.0f);
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
                wallSlideSoundId = sound.loop(0.3f);
            }
        } else {
            if (wallSlideSoundId != -1) {
                sound.stop(wallSlideSoundId);
                wallSlideSoundId = -1;
            }
        }
    }

    /**
     * Звільняє ресурси при закритті гри.
     */
    public void dispose() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
    }
}
