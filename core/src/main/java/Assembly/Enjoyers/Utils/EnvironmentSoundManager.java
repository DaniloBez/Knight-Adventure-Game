package Assembly.Enjoyers.Utils;

import com.badlogic.gdx.audio.Sound;
import static com.badlogic.gdx.Gdx.*;

/**
 * Відповідає за відтворення звуків середовища, зокрема звуку трампліна.
 */
public class EnvironmentSoundManager {
    private final Sound jumpPadSound;

    /**
     * Відповідає за відтворення звуків середовища, зокрема звуку трампліна.
     */
    public EnvironmentSoundManager() {
        jumpPadSound = audio.newSound(files.internal("sounds/jump_pad_sound.ogg"));
    }

    /**
     * Відтворює звук трампліна.
     */
    public void playJumpPad() {
        jumpPadSound.play(1.0f);
    }

    /**
     * Звільняє ресурси звукових ефектів.
     */
    public void dispose() {
        jumpPadSound.dispose();
    }
}
