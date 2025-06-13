package Assembly.Enjoyers.Map.AnimatedBlocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;

/**
 * Менеджер анімації для джамп-пада.
 * Відповідає за завантаження, відтворення та керування кадрами анімації.
 */
public class JumpPadAnimationManager {
    /** Масив кадрів анімації. */
    private final TextureRegion[] frames = new TextureRegion[8];
    /** Тривалість відображення кожного кадру анімації. */
    private final float[] frameDurations = {0.1f, 0.15f, 0.2f, 0.15f, 0.2f, 0.2f, 0.2f, 0.25f};
    /** Список завантажених текстур для подальшого звільнення пам'яті. */
    private final List<Texture> loadedTextures = new ArrayList<>();

    /** Поточний кадр анімації. */
    private int currentFrame = 0;
    /** Час, що минув зі зміни поточного кадру. */
    private float stateTime = 0;
    /** Прапорець активного відтворення анімації. */
    private boolean isAnimating = false;

    /**
     * Конструктор, що ініціалізує анімацію - завантажує текстури кадрів.
     */
    public JumpPadAnimationManager() {
        for (int i = 0; i < 8; i++) {
            Texture texture = new Texture("maps/map_assets/jump_pad/jump_pad_" + (i+1) + ".png");
            loadedTextures.add(texture);
            frames[i] = new TextureRegion(texture);
        }
    }

    /**
     * Запускає відтворення анімації з початку.
     */
    public void startAnimation() {
        isAnimating = true;
        stateTime = 0;
        currentFrame = 0;
    }

    /**
     * Оновлює стан анімації.
     *
     * @param delta Час з останнього оновлення (у секундах)
     */
    public void update(float delta) {
        if (!isAnimating) return;

        stateTime += delta;
        if (stateTime >= frameDurations[currentFrame]) {
            stateTime = 0;
            currentFrame++;
            if (currentFrame >= frames.length) {
                currentFrame = 0;
                isAnimating = false;
            }
        }
    }

    /**
     * Повертає поточний кадр анімації.
     *
     * @return Поточний кадр анімації
     */
    public TextureRegion getCurrentFrame() {
        return frames[currentFrame];
    }

    /**
     * Звільняє ресурси, пов'язані з анімацією.
     */
    public void dispose() {
        for (Texture texture : loadedTextures) {
            texture.dispose();
        }
    }

    /**
     * Перевіряє, чи завершено відтворення анімації.
     *
     * @return true, якщо анімація завершена
     */
    public boolean isAnimationFinished() {
        return !isAnimating && currentFrame == 0;
    }
}
