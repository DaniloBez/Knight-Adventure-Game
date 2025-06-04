package Assembly.Enjoyers.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;


/**
 * Представляє блок, який кришиться в кілька етапів анімації та зрештою зникає.
 */
public class CrumblingBlock {
    private final Rectangle bounds;
    private final CrumblingAnimationManager animationManager;

    private int stage = 1;
    private float timer = 0;
    private boolean isActive = true;


    /**
     * Створює новий блок, що кришиться, з вказаними координатами та розмірами.
     *
     * @param x      координата X верхнього лівого кута блоку
     * @param y      координата Y верхнього лівого кута блоку
     * @param width  ширина блоку
     * @param height висота блоку
     */
    public CrumblingBlock(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
        this.animationManager = new CrumblingAnimationManager();
    }

    /**
     * Оновлює стан блоку згідно з часом, що минув.
     *
     * @param delta час у секундах з моменту останнього оновлення
     */
    public void update(float delta) {
        if (!isActive) {
            isActive = true;
            stage = 1;
            timer = 0;
            return;
        }

        timer += delta;
        float currentStageDuration = animationManager.getStageDuration(stage);

        if (timer >= currentStageDuration) {
            timer = 0;
            stage++;

            if (stage > 5) {
                stage = 5;
                isActive = false;
            }
        }
    }

    /**
     * Перевіряє, чи блок повністю зруйнований.
     *
     * @return true, якщо блок завершив руйнування
     */
    public boolean isDestroyed() {
        return stage == 5 && !isActive;
    }

    /**
     * Перевіряє, чи блок перебуває у процесі руйнування.
     *
     * @return true, якщо блок у проміжному етапі руйнування
     */
    public boolean isCrumbling() {
        return stage > 1 && stage < 5;
    }

    /**
     * Повертає поточний кадр анімації відповідно до стадії руйнування.
     *
     * @param delta час у секундах з моменту останнього оновлення
     * @return поточний кадр анімації блоку
     */
    public TextureRegion getCurrentFrame(float delta) {
        return animationManager.getFrame(stage, delta);
    }

    /**
     * Повертає прямокутник меж блоку.
     *
     * @return об'єкт Rectangle, що представляє межі блоку
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Повертає, чи є блок активним (тобто ще не завершив руйнування).
     *
     * @return true, якщо блок активний
     */
    public boolean isActive() {
        return isActive;
    }


    /**
     * Повертає поточну стадію руйнування блоку.
     *
     * @return ціле число від 1 до 5, що позначає стадію руйнування
     */
    public int getStage() {
        return stage;
    }
}
