package Assembly.Enjoyers.Player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Менеджер анімацій для персонажа.
 * Зберігає анімації для кожного стану {@link PlayerState} та повертає відповідні кадри.
 */
public class PlayerAnimationManager {
    private final List<Texture> loadedTextures = new ArrayList<>();
    private final EnumMap<PlayerState, Animation<TextureRegion>> animations = new EnumMap<>(PlayerState.class);
    private float stateTime = 0;

    /**
     * Ініціалізує всі анімації для кожного стану персонажа.
     */
    public PlayerAnimationManager() {
        animations.put(PlayerState.IDLE, loadAnimation("player/adventurer-idle-", 4, 0.2f));
        animations.put(PlayerState.RUNNING, loadAnimation("player/adventurer-run-", 6, 0.15f));
        animations.put(PlayerState.JUMPING, loadAnimation("player/adventurer-jump-", 2, 0.35f));
        animations.put(PlayerState.FALLING, loadAnimation("player/adventurer-fall-", 2, 0.15f));
        animations.put(PlayerState.DASHING, loadAnimation("player/adventurer-smrslt-", 4, 0.15f));
        animations.put(PlayerState.WALL_CLIMBING, loadAnimation("player/adventurer-ladder-climb-", 4, 0.2f));
        animations.put(PlayerState.WALL_SLIDING, loadAnimation("player/adventurer-wall-slide-", 2, 0.2f));
        animations.put(PlayerState.WALL_GRABBING, loadAnimation("player/adventurer-crnr-grb-", 4, 0.3f));
    }

    /**
     * Завантажує масив кадрів анімації з окремих файлів.
     *
     * @param basePath      базовий шлях до кадрів (без номера кадру та розширення)
     * @param frameCount    кількість кадрів
     * @param frameDuration тривалість одного кадру
     * @return готова анімація
     */
    private Animation<TextureRegion> loadAnimation(String basePath, int frameCount, float frameDuration) {
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            Texture texture = new Texture(basePath + String.format("%02d.png", i));
            loadedTextures.add(texture);
            frames[i] = new TextureRegion(texture);
        }
        return new Animation<>(frameDuration, frames);
    }

    /**
     * Повертає поточний кадр анімації залежно від стану персонажа.
     *
     * @param state        поточний стан персонажа
     * @param facingRight  напрямок руху (true — праворуч, false — ліворуч)
     * @param deltaTime    час між кадрами (для анімації)
     * @return поточний кадр анімації
     */
    public TextureRegion getCurrentFrame(PlayerState state, boolean facingRight, float deltaTime) {
        stateTime += deltaTime;
        TextureRegion frame = animations.get(state).getKeyFrame(stateTime, true);
        if (!facingRight && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (facingRight && frame.isFlipX()) {
            frame.flip(true, false);
        }
        return frame;
    }

    /**
     * Скидає час анімаційного стану (наприклад, при зміні стану).
     */
    public void resetStateTime() {
        stateTime = 0;
    }

    /**
     * Звільняє ресурси (текстури анімацій)
     */
    public void dispose() {
        for (Texture texture : loadedTextures) {
            texture.dispose();
        }
    }
}
