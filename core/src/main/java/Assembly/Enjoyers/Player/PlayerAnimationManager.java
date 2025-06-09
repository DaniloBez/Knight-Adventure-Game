package Assembly.Enjoyers.Player;

import Assembly.Enjoyers.Utils.Assets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Менеджер анімацій для персонажа.
 * Зберігає анімації для кожного стану {@link PlayerState} та повертає відповідні кадри.
 */
public class PlayerAnimationManager {
    private final EnumMap<PlayerState, Animation<TextureRegion>> animations = new EnumMap<>(PlayerState.class);
    private float stateTime = 0;
    private final TextureAtlas atlas;

    /**
     * Ініціалізує всі анімації для кожного стану персонажа.
     */
    public PlayerAnimationManager() {
        this.atlas = Assets.get("player/adventurer.atlas", TextureAtlas.class);

        animations.put(PlayerState.IDLE, loadAnimation("adventurer-idle-", 4, 0.2f));
        animations.put(PlayerState.RUNNING, loadAnimation("adventurer-run-", 6, 0.15f));
        animations.put(PlayerState.JUMPING, loadAnimation("adventurer-jump-", 2, 0.35f));
        animations.put(PlayerState.FALLING, loadAnimation("adventurer-fall-", 2, 0.15f));
        animations.put(PlayerState.DASHING, loadAnimation("adventurer-smrslt-", 4, 0.15f));
        animations.put(PlayerState.WALL_CLIMBING, loadAnimation("adventurer-ladder-climb-", 4, 0.2f));
        animations.put(PlayerState.WALL_SLIDING, loadAnimation("adventurer-wall-slide-", 2, 0.2f));
        animations.put(PlayerState.WALL_GRABBING, loadAnimation("adventurer-crnr-grb-", 4, 0.3f));
        animations.put(PlayerState.DYING, loadAnimation("adventurer-die-", 7, 0.2f));
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
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < frameCount; i++)
            frames.add(atlas.findRegion(basePath + String.format("%02d", i)));

        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    /**
     * Повертає поточний кадр анімації залежно від стану персонажа.
     *
     * @param state        поточний стан персонажа
     * @param facingRight  напрямок руху (true — праворуч, false — ліворуч)
     * @param deltaTime    час між кадрами (для анімації)
     * @return поточний кадр анімації
     */
    public TextureRegion getCurrentFrame(PlayerState state, boolean facingRight, float deltaTime, boolean paused) {
        if (!paused)
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

    public float getAnimationDuration(PlayerState state) {
        return animations.get(state).getAnimationDuration();
    }
}
