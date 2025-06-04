package Assembly.Enjoyers.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;

public class CrumblingAnimationManager {
    private final List<Texture> loadedTextures = new ArrayList<>();
    private final TextureRegion[] frames = new TextureRegion[5];
    private final float[] frameDurations = {0.5f, 0.5f, 0.5f, 0.5f, 2,5f};

    public CrumblingAnimationManager() {
        for (int i = 0; i < 5; i++) {
            Texture texture = new Texture("maps/night_level/crumble/crumble-" + (i+1) + ".png");
            loadedTextures.add(texture);
            frames[i] = new TextureRegion(texture);
        }
    }

    public float getStageDuration(int stage) {
        if (stage >= 1 && stage <= frameDurations.length) {
            return frameDurations[stage - 1];
        }
        return 0.75f; // значення за замовчуванням
    }


    public TextureRegion getFrame(int stage, float delta) {
        if (stage >= 1 && stage <= 5) {
            return frames[stage - 1];
        }
        return frames[0];
    }

    public void dispose() {
        for (Texture texture : loadedTextures) {
            texture.dispose();
        }
    }
}
