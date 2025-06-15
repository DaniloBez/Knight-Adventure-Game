package Assembly.Enjoyers.Utils;

import com.badlogic.gdx.audio.Sound;
import static com.badlogic.gdx.Gdx.*;

/**
 * Менеджер звуків довкілля (environmental sounds), відповідає за завантаження,
 * відтворення та звільнення ресурсів звуків, пов'язаних з оточенням гравця.
 * Наразі керує звуком стрибкової платформи (jump pad).
 */
public class EnvironmentSoundManager {
    /** Звук активації стрибкової платформи. */
    private final Sound jumpPadSound;

    /**
     * Ініціалізує менеджер, завантажуючи необхідні звуки з внутрішніх ресурсів.
     */
    public EnvironmentSoundManager() {
        jumpPadSound = audio.newSound(files.internal("sounds/jump_pad_sound.ogg"));
    }

    /**
     * Програє звук активації стрибкової платформи із повною гучністю.
     */
    public void playJumpPad() {
        jumpPadSound.play(1.0f);
    }

    /**
     * Звільняє ресурси, зайняті під час завантаження звуків.
     * Після виклику цього методу екземпляр менеджера використовувати не можна.
     */
    public void dispose() {
        jumpPadSound.dispose();
    }
}
