package Assembly.Enjoyers.Utils;

import com.badlogic.gdx.audio.Sound;
import static com.badlogic.gdx.Gdx.*;

public class EnvironmentSoundManager {
    private final Sound jumpPadSound;

    public EnvironmentSoundManager() {
        jumpPadSound = audio.newSound(files.internal("sounds/jump_pad_sound.ogg"));
    }

    public void playJumpPad() {
        jumpPadSound.play(1.0f);
    }

    public void dispose() {
        jumpPadSound.dispose();
    }
}
