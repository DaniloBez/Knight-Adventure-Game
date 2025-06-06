package Assembly.Enjoyers.Map.AnimatedBlocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;

/**
 * Керує анімаційними кадрами блоку, що кришиться.
 * Завантажує текстури, зберігає тривалість кадрів та повертає відповідні кадри за стадією.
 */
public class CrumblingAnimationManager {
    private final List<Texture> loadedTextures = new ArrayList<>();
    private final TextureRegion[] frames = new TextureRegion[5];
    private final float[] frameDurations = {0.3f, 0.3f, 0.3f, 0.3f, 2f};

    /**
     * Створює менеджер анімації та завантажує всі необхідні текстури для етапів руйнування.
     */
    public CrumblingAnimationManager() {
        for (int i = 0; i < 5; i++) {
            Texture texture = new Texture("maps/night_level/crumble/crumble-" + (i+1) + ".png");
            loadedTextures.add(texture);
            frames[i] = new TextureRegion(texture);
        }
    }

    /**
     * Повертає тривалість конкретної стадії руйнування.
     *
     * @param stage номер стадії (від 1 до 5)
     * @return тривалість у секундах для відповідного етапу; значення за замовчуванням — 0.75f
     */
    public float getStageDuration(int stage) {
        if (stage >= 1 && stage <= frameDurations.length) {
            return frameDurations[stage - 1];
        }
        return 0.75f; // значення за замовчуванням
    }


    /**
     * Повертає кадр анімації, що відповідає заданій стадії.
     *
     * @param stage номер стадії (від 1 до 5)
     * @param delta час, що минув (не використовується, але залишений для сумісності)
     * @return кадр TextureRegion, який відповідає поточній стадії
     */
    public TextureRegion getFrame(int stage, float delta) {
        if (stage >= 1 && stage <= 5) {
            return frames[stage - 1];
        }
        return frames[0];
    }

    /**
     * Звільняє пам’ять, очищаючи всі завантажені текстури.
     * Викликається при знищенні об’єкта або завершенні роботи.
     */
    public void dispose() {
        for (Texture texture : loadedTextures) {
            texture.dispose();
        }
    }
}
