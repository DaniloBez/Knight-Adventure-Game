package Assembly.Enjoyers.Map.AnimatedBlocks;

import Assembly.Enjoyers.Utils.EnvironmentSoundManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Клас, що реалізує анімований прыжковий майданчик (джамп-пад) у грі.
 * Відповідає за відтворення анімації при активації гравцем.
 */
public class JumpPad {
    /** Межі для відображення текстури джамп-пада. */
    private final Rectangle drawBounds;
    /** Межі, при перетині яких гравець активує джамп-пад. */
    private final Rectangle triggerBounds;
    /** Менеджер анімації джамп-пада. */
    private final JumpPadAnimationManager animationManager;
    /** Прапорець, що вказує на активований стан джамп-пада. */
    private boolean isTriggered = false;
    private final EnvironmentSoundManager soundManager = new EnvironmentSoundManager();

    /**
     * Створює новий джамп-пад із заданими параметрами.
     *
     * @param x      Координата X лівого нижнього кута
     * @param y      Координата Y лівого нижнього кута
     * @param width  Ширина джамп-пада
     * @param height Висота джамп-пада
     */
    public JumpPad(float x, float y, float width, float height) {
        this.drawBounds = new Rectangle(x, y, width, height);
        this.triggerBounds = new Rectangle(x, y, width, height / 4);
        this.animationManager = new JumpPadAnimationManager();
    }

    /**
     * Активує джамп-пад, запускаючи його анімацію.
     */
    public void trigger() {
        if (!isTriggered) {
            animationManager.startAnimation();
            soundManager.playJumpPad();
            isTriggered = true;
        }
    }

    /**
     * Оновлює стан анімації джамп-пада.
     *
     * @param delta Час з останнього оновлення (у секундах)
     */
    public void update(float delta) {
        if (isTriggered) {
            animationManager.update(delta);

            if (animationManager.isAnimationFinished()) {
                isTriggered = false;
            }
        }
    }

    /**
     * Повертає поточний кадр анімації джамп-пада.
     *
     * @return Поточний кадр анімації
     */
    public TextureRegion getCurrentFrame() {
        return animationManager.getCurrentFrame();
    }

    /**
     * Повертає межі тригерної зони джамп-пада.
     *
     * @return Прямокутник тригерної зони
     */
    public Rectangle getTriggerBounds() {
        return triggerBounds;
    }

    /**
     * Повертає межі для відображення джамп-пада.
     *
     * @return Прямокутник з межами відображення
     */
    public Rectangle getDrawBounds() {
        return drawBounds;
    }

    /**
     * Перевіряє, чи активовано джамп-пад.
     *
     * @return true, якщо джамп-пад активовано
     */
    public boolean isTriggered() {
        return isTriggered;
    }
}
