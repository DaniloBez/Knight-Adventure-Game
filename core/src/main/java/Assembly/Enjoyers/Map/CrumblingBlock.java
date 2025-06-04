package Assembly.Enjoyers.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class CrumblingBlock {
    private final Rectangle bounds;
    private final CrumblingAnimationManager animationManager;

    private int stage = 1;
    private float timer = 0;
    private boolean isActive = true;

    public CrumblingBlock(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
        this.animationManager = new CrumblingAnimationManager();
    }

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

    public boolean isDestroyed() {
        return stage == 5 && !isActive;
    }

    public boolean isCrumbling() {
        return stage > 1 && stage < 5;
    }

    public TextureRegion getCurrentFrame(float delta) {
        return animationManager.getFrame(stage, delta);
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
