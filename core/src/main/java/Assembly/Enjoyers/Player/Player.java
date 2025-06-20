package Assembly.Enjoyers.Player;

import Assembly.Enjoyers.Utils.Assets;
import Assembly.Enjoyers.Map.AnimatedBlocks.CrumblingBlock;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

import static com.badlogic.gdx.Gdx.input;

public class Player {
    private static final float GRAVITY = -1200f;
    private static final float MOVE_SPEED = 600f;
    private static final float JUMP_FORCE = 600f;
    private static final float WALL_SLIDE_SPEED = -200f;
    private static final float WALL_CLIMB_SPEED = 150f;
    private static final float WALL_JUMP_FORCE_X = 1200f;
    private static final int MAX_DASH_COUNT = 1;
    private static final float DASH_FORCE = 1000f;
    private static final float DASH_DECAY_RAW = 0.98f;
    private static final float DASH_MIN_FORCE = 400f;
    private static final float MAX_STAMINA = 100f;
    private static final float STAMINA_DRAIN = 20f;
    private static final float HITBOX_X_OFFSET = 55f;
    private static final float HITBOX_Y_OFFSET = 22f;

    public final Sprite sprite;
    private final Rectangle hitBox;
    private final Sprite corpse;
    private final float respawnX;
    private final float respawnY;
    private final DeathListener deathListener;
    private final PlayerAnimationManager animationManager = new PlayerAnimationManager();
    private final PlayerSoundManager soundManager = new PlayerSoundManager();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private PlayerState currentState = PlayerState.IDLE;
    private boolean facingRight = true;
    private boolean onGround, prevOnGround, touchingWall, lastWallRight;
    private boolean isDashing, isDead;

    private float velocityX, velocityY;
    private float dashXVelocity, dashYVelocity;
    private int dashCount = MAX_DASH_COUNT;
    private float stamina = MAX_STAMINA;
    private float deathTimer;
    private final float deathDelay;
    private Texture[] staminaStages;

    /**
     * Конструктор персонажа, ініціалізує текстуру, спрайт та хитбокс.
     */
    public Player(DeathListener deathListener, float respawnX, float respawnY) {
        TextureAtlas atlas = Assets.get("player/adventurer.atlas", TextureAtlas.class);
        TextureRegion region = atlas.findRegion("adventurer-die-06");

        this.sprite = new Sprite(region);
        this.sprite.setSize(region.getRegionWidth() * 3, region.getRegionHeight() * 3);
        this.respawnX = respawnX;
        this.respawnY = respawnY;
        this.sprite.setPosition(respawnX, respawnY);

        this.hitBox = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        this.corpse = new Sprite(region);
        this.corpse.setSize(region.getRegionWidth() * 3, region.getRegionHeight() * 3);
        this.corpse.setAlpha(0);

        this.deathListener = deathListener;
        this.deathDelay = animationManager.getAnimationDuration(PlayerState.DYING);
        InputHandler.update();
    }

    /**
     * Застосовує jumppadVelocity до VelocityY.
     * Виштовхує гравця вгору.
     */
    public void applyJumpPadBoost() {
        float currentVelocityX = velocityX;

        dashYVelocity = 0;
        velocityY = 1200f;

        velocityX = currentVelocityX;
    }

    /**
     * Малює прямокутник хитбоксу навколо персонажа для відлагодження.
     * @param camera активна ігрова камера
     */
    public void drawHitBox(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
        shapeRenderer.rect(corpse.getX() + HITBOX_X_OFFSET, corpse.getY(), corpse.getWidth() - 1.7f * HITBOX_X_OFFSET, corpse.getHeight() - 2.2f * HITBOX_Y_OFFSET);
        shapeRenderer.end();
    }

    /**
     * @return Хідбокс гравця.
     */
    public Rectangle getHitBox(){
        return hitBox;
    }

    /**
     * Основна функція оновлення руху та взаємодії з рівнем.
     * @param bounds список прямокутників колізій.
     * @param spikes список колізій шипів.
     * @param delta час між кадрами
     */
    public void move(List<Rectangle> bounds, List<Rectangle> spikes, List<CrumblingBlock> crumblingBlocks,float delta) {
        currentState = PlayerState.IDLE;

        if (handleDeath(spikes, crumblingBlocks, delta)) return;

        float moveX = handleHorizontalInput(delta);
        dash(delta);
        applyGravityIfNeeded(delta);

        updateHitBox();
        onGround = checkFeetTouching(bounds);
        if (!prevOnGround && onGround)
            soundManager.play(PlayerState.LANDING);
        prevOnGround = onGround;
        if (onGround) resetDashAndStamina();

        touchingWall = checkWallTouching(bounds);
        if (touchingWall) currentState = PlayerState.WALL_SLIDING;
        handleJump();
        handleWallInteraction(bounds, delta);
        if(currentState == PlayerState.JUMPING) soundManager.play(PlayerState.JUMPING);

        applyVerticalMovement(bounds, delta);
        applyHorizontalMovement(moveX, bounds, delta);

        playerStateHandler(moveX, delta);
    }

    /**
     * Викликає смерть у гравця, задля його переміщення на початок рівня.
     */
    public void respawn(){
        isDashing = false;
        dashXVelocity = 0;
        dashYVelocity = 0;

        soundManager.play(PlayerState.DYING);
        animationManager.resetStateTime();
        isDead = true;
        deathTimer = deathDelay;
    }

    /**
     * Малює тіло померлого гравця, з гравітацією.
     * @param batch Малювання сцени.
     * @param bounds список колізій.
     * @param delta час між кадрами
     */
    public void drawCorpse(SpriteBatch batch, List<Rectangle> bounds, float delta) {
        corpse.draw(batch);

        Rectangle hitbox = new Rectangle(corpse.getX() + HITBOX_X_OFFSET, corpse.getY(), corpse.getWidth() - 1.7f * HITBOX_X_OFFSET, corpse.getHeight() - 2.2f * HITBOX_Y_OFFSET);
        boolean isOnGround = false;
        for (Rectangle bound : bounds) {
            if(hitbox.overlaps(bound)) {
                isOnGround = true;
                break;
            }
        }

        if (!isOnGround) {
            corpse.translateY(-MOVE_SPEED * delta);
        }
    }

    /**
     * Обробляє логіку смерті гравця: зіткнення зі шипами, відтворення анімації, респавн.
     * @param spikes список хітбоксів шипів
     * @param delta час між кадрами
     * @return true, якщо гравець помер або очікує респавну
     */
    private boolean handleDeath(List<Rectangle> spikes,List<CrumblingBlock> crumblingBlocks,  float delta) {
        if (isDead) {
            currentState = PlayerState.DYING;
            velocityY = 0;
            velocityX = 0;
            deathTimer -= delta;
            if (deathTimer <= 0f) {
                corpse.setPosition(sprite.getX(), sprite.getY() - 10);
                corpse.setAlpha(1f);
                corpse.setFlip(!facingRight, false);
                sprite.setPosition(respawnX, respawnY);
                updateHitBox();
                isDead = false;
            }
            return true;
        }

        if (isDie(spikes, crumblingBlocks)) {
            isDashing = false;
            dashXVelocity = 0;
            dashYVelocity = 0;

            deathListener.onDeath();

            soundManager.play(PlayerState.DYING);
            animationManager.resetStateTime();
            isDead = true;
            deathTimer = deathDelay;
            return true;
        }

        return false;
    }
    /**
     * Перевіряє чи гравець дотикається до небезпечних елементів.
     * @param spikes Список хідбоксів шипів.
     * @return true, якщо гравець дотикається до небезпечних елементів.
     */
    private boolean isDie(List<Rectangle> spikes, List<CrumblingBlock> crumblingBlocks) {
        for (Rectangle spike : spikes) {
            if (hitBox.overlaps(spike)) {
                return true;
            }
        }

        for (CrumblingBlock crumblingBlock : crumblingBlocks) {
            Rectangle block = crumblingBlock.getBounds();
            if (crumblingBlock.isCrumbling() &&
                (hitBox.x < block.x + block.width &&
                hitBox.x + hitBox.width > block.x &&
                hitBox.y < block.y + block.height &&
                hitBox.y + hitBox.height > block.y))
                    return true;
        }

        return false;
    }

    /**
     * Обробка поведінки стану гравця залежно від ситуацій під час гри.
     * Увімкнення звукових ефектів гравця.
     *
     * @param moveX Рух по осі OX
     * @param delta Час між кадрами
     */
    private void playerStateHandler(float moveX, float delta){
        soundManager.playWallSlideRepeatable(currentState == PlayerState.WALL_SLIDING);

        if(currentState != PlayerState.WALL_CLIMBING && currentState != PlayerState.WALL_SLIDING && currentState != PlayerState.WALL_GRABBING && currentState != PlayerState.JUMPING) {
            if (isDashing)
                currentState = PlayerState.DASHING;
            else if (!onGround)
                currentState = velocityY > 0 ? PlayerState.JUMPING : PlayerState.FALLING;
            else if (moveX != 0)
                currentState = PlayerState.RUNNING;
            else
                currentState = PlayerState.IDLE;
        }

        soundManager.update(delta);
        switch (currentState){
            case RUNNING, WALL_CLIMBING -> soundManager.play(currentState);
        }
    }

    /**
     * Повертає поточний кадр анімації відповідно до стану гравця.
     * Використовується для відображення анімованого спрайта.
     *
     * @param delta час між кадрами
     * @return TextureRegion відповідного кадру анімації
     */
    public TextureRegion getFrame(float delta, boolean paused) {
        return animationManager.getCurrentFrame(currentState, facingRight, delta, paused);
    }

    /**
     * Застосовує горизонтальний рух гравця та перевіряє зіткнення.
     * @param moveX Рух по осі OX
     * @param bounds Список хіт боксів у рівні
     * @param delta Проміжок часу між поточним та останнім кадром у секундах.
     */
    private void applyHorizontalMovement(float moveX, List<Rectangle> bounds, float delta) {
        Rectangle futureHitBox = new Rectangle(hitBox);
        futureHitBox.x += (velocityX + dashXVelocity) * delta;

        for (Rectangle bound : bounds) {
            if (futureHitBox.overlaps(bound)) {
                dashXVelocity = 0;
                velocityX = 0;
                return;
            }
        }

        if (Math.abs(velocityX) > 600) {
            sprite.translateX((velocityX + dashXVelocity) * delta);
            velocityX *= 0.95f;
            if (Math.abs(velocityX) < 10f) velocityX = 0;
        }
        else
            sprite.translateX(moveX + dashXVelocity * delta);

        updateHitBox();
        for (Rectangle bound : bounds) {
            if (hitBox.overlaps(bound)) {
                if (moveX > 0)
                    sprite.setX(bound.x - hitBox.width - HITBOX_X_OFFSET);
                else if (moveX < 0)
                    sprite.setX(bound.x + bound.width - HITBOX_X_OFFSET);

                dashXVelocity = 0;
                updateHitBox();
                break;
            }
        }
    }

    /**
     * Застосовує вертикальний рух гравця та перевіряє зіткнення.
     * @param bounds Список хіт боксів у рівні
     * @param delta Проміжок часу між поточним та останнім кадром у секундах.
     */
    private void applyVerticalMovement(List<Rectangle> bounds, float delta) {
        Rectangle futureHitBox = new Rectangle(hitBox);
        futureHitBox.y += (velocityY + dashYVelocity) * delta;

        for (Rectangle bound : bounds) {
            if (futureHitBox.overlaps(bound)) {
                dashYVelocity = 0;
                velocityY = 0;
                return;
            }
        }

        sprite.translateY((velocityY + dashYVelocity) * delta);
        updateHitBox();
        for (Rectangle bound : bounds) {
            if (hitBox.overlaps(bound)) {
                if (velocityY > 0) {
                    sprite.setY(bound.y - hitBox.height - HITBOX_Y_OFFSET);
                } else if (velocityY < 0) {
                    sprite.setY(bound.y + bound.height - HITBOX_Y_OFFSET);
                    onGround = true;
                }
                velocityY = 0;
                dashYVelocity = 0;
                updateHitBox();
                break;
            }
        }
    }

    /**
     * Обробка поведінки персонажа при взаємодії зі стіною: стаміна, ковзання, лазання.
     * @param bounds Список хіт боксів у рівні
     * @param delta Проміжок часу між поточним та останнім кадром у секундах.
     */
    private void handleWallInteraction(List<Rectangle> bounds, float delta) {
        if (touchingWall) {
            if (InputHandler.getButtonPressed(InputHandler.KeyBinds.CLIMB) && stamina > 0) {
                currentState = PlayerState.WALL_GRABBING;

                if(velocityY <= WALL_CLIMB_SPEED) {
                    if (InputHandler.getButtonPressed(InputHandler.KeyBinds.UP)) {
                        currentState = PlayerState.WALL_CLIMBING;
                        velocityY = WALL_CLIMB_SPEED;
                    }
                    else if (InputHandler.getButtonPressed(InputHandler.KeyBinds.DOWN)) {
                        currentState = PlayerState.WALL_CLIMBING;
                        velocityY = -WALL_CLIMB_SPEED;
                    }
                    else if (velocityY < 0)
                        velocityY = 0;

                }

                if(InputHandler.getButtonPressed(InputHandler.KeyBinds.UP) && InputHandler.getButtonJustPressed(InputHandler.KeyBinds.JUMP)) {
                    velocityY = JUMP_FORCE;
                    stamina -= STAMINA_DRAIN;
                    currentState = PlayerState.JUMPING;
                }

                stamina -= STAMINA_DRAIN * delta;
                if (stamina < 0) stamina = 0;
            }else if (InputHandler.getButtonPressed(InputHandler.KeyBinds.DOWN)) {
                velocityY = 0.75f * GRAVITY;
                currentState = PlayerState.WALL_SLIDING;
            }
            else if (!InputHandler.getButtonPressed(InputHandler.KeyBinds.DOWN) && velocityY < WALL_SLIDE_SPEED) {
                velocityY = WALL_SLIDE_SPEED;
                currentState = PlayerState.WALL_SLIDING;
            }

            if(InputHandler.getButtonJustPressed(InputHandler.KeyBinds.JUMP) && !InputHandler.getButtonPressed(InputHandler.KeyBinds.UP) && stamina > 0){
                if (checkRightTouching(bounds)) {
                    if (lastWallRight) {
                        velocityX = -WALL_JUMP_FORCE_X;
                        velocityY = JUMP_FORCE;
                        stamina -= STAMINA_DRAIN;
                    }
                    else {
                        velocityX = -WALL_JUMP_FORCE_X;
                        velocityY = JUMP_FORCE;
                    }
                    lastWallRight = true;
                    currentState = PlayerState.JUMPING;
                }
                else if (checkLeftTouching(bounds)) {
                    if (!lastWallRight && stamina > 0) {
                        velocityX = WALL_JUMP_FORCE_X;
                        velocityY = JUMP_FORCE;
                        stamina -= STAMINA_DRAIN;
                    } else if (lastWallRight) {
                        velocityX = WALL_JUMP_FORCE_X;
                        velocityY = JUMP_FORCE;
                    }
                    lastWallRight = false;
                    currentState = PlayerState.JUMPING;
                }
            }
        }
    }

    /**
     * Стрибок з землі.
     */
    private void handleJump() {
        if (onGround && InputHandler.getButtonJustPressed(InputHandler.KeyBinds.JUMP)) {
            velocityY = JUMP_FORCE;
            currentState = PlayerState.JUMPING;
        }
    }

    /**
     * Скидає лічильники деша та стаміни, коли персонаж на землі.
     */
    private void resetDashAndStamina() {
        dashCount = MAX_DASH_COUNT;
        stamina = MAX_STAMINA;
    }
    /**
     * Обробляє вхідні команди AD для руху вліво/вправо.
     * @param delta Проміжок часу між поточним та останнім кадром у секундах.
     * @return зсув по осі OX
     */
    private float handleHorizontalInput(float delta) {
        float moveX = 0;
        if (InputHandler.getButtonPressed(InputHandler.KeyBinds.LEFT)) {
            moveX -= MOVE_SPEED * delta;
            facingRight = false;
        }
        if (InputHandler.getButtonPressed(InputHandler.KeyBinds.RIGHT)) {
            moveX += MOVE_SPEED * delta;
            facingRight = true;
        }

        return moveX;
    }

    /**
     * Застосовує гравітацію до вертикальної швидкості.
     * @param delta Проміжок часу між поточним та останнім кадром у секундах.
     */
    private void applyGravityIfNeeded(float delta) {
        if (!isDashing || (dashXVelocity == 0 && dashYVelocity < 0)) {
            velocityY += GRAVITY * delta;
        } else {
            velocityY = 0;
        }
    }

    /**
     * Обробляє деш у різні сторони, з лімітом на кількість.
     */
    private void dash(float delta) {
        if (isDashing) {
            float dashDecay = (float) (Math.pow(DASH_DECAY_RAW, delta * 60));
            dashXVelocity *= dashDecay;
            dashYVelocity *= dashDecay;

            if (Math.abs(dashXVelocity) < DASH_MIN_FORCE && Math.abs(dashYVelocity) < DASH_MIN_FORCE) {
                dashXVelocity = 0;
                dashYVelocity = 0;
                isDashing = false;
            }
            return;
        }

        if (InputHandler.getButtonJustPressed(InputHandler.KeyBinds.DASH)
            && dashCount > 0 && !isDashing) {
            float dx = 0, dy = 0;
            if (input.isKeyPressed(Keys.D)) dx = 1;
            if (input.isKeyPressed(Keys.A)) dx = -1;
            if (input.isKeyPressed(Keys.W)) dy = 1f;
            if (input.isKeyPressed(Keys.S)) dy = -1f;

            if (dx == 0 && dy == 0)
                dx = facingRight ? 1 : -1;

            float len = (float) Math.sqrt(dx * dx + dy * dy);
            if (len != 0) {
                dx /= len;
                dy /= len;
            }

            dashXVelocity = dx * DASH_FORCE;
            dashYVelocity = (dy * 3 / 4)  * DASH_FORCE;
            if(dashYVelocity == 0)
                dashXVelocity *= (float) 3 /4;
            isDashing = true;
            dashCount--;

            soundManager.play(PlayerState.DASHING);
        }
    }

    /**
     * Оновлює координати прямокутника hitBox за позицією спрайта.
     */
    private void updateHitBox() {
        hitBox.set(sprite.getX() + HITBOX_X_OFFSET, sprite.getY(), sprite.getWidth() - 2 * HITBOX_X_OFFSET, sprite.getHeight() - HITBOX_Y_OFFSET);
    }

    /**
     * Перевіряє, чи персонаж стоїть на землі.
     * @param bounds Список хіт боксів у рівні
     * @return чи персонаж стоїть на землі
     */
    private boolean checkFeetTouching(List<Rectangle> bounds) {
        Rectangle feet = new Rectangle(hitBox.x + hitBox.width / 8, hitBox.y - 1, hitBox.width / 4 * 3, 2);
        return bounds.stream().anyMatch(feet::overlaps);
    }

    /**
     * Перевіряє контакт правої сторони з перешкодою.
     * @param bounds Список хіт боксів у рівні
     * @return чи персонаж дотикаєтеся справа
     */
    private boolean checkRightTouching(List<Rectangle> bounds) {
        Rectangle side = new Rectangle(hitBox.x + hitBox.width, hitBox.y, 2, hitBox.height);
        return bounds.stream().anyMatch(side::overlaps);
    }

    /**
     * Перевіряє контакт лівої сторони з перешкодою.
     * @param bounds Список хіт боксів у рівні
     * @return чи персонаж дотикаєтеся зліва
     */
    private boolean checkLeftTouching(List<Rectangle> bounds) {
        Rectangle side = new Rectangle(hitBox.x - 2, hitBox.y, 2, hitBox.height);
        return bounds.stream().anyMatch(side::overlaps);
    }

    /**
     * Перевіряє, чи персонаж знаходиться поруч зі стіною (ліворуч або праворуч) (для ковзання).
     * @param bounds Список хіт боксів у рівні
     * @return чи персонаж дотикаєтеся справа або зліва
     */
    private boolean checkWallTouching(List<Rectangle> bounds) {
        Rectangle right = new Rectangle(hitBox.x + hitBox.width + 1, hitBox.y, 2, hitBox.height);
        Rectangle left = new Rectangle(hitBox.x - 2, hitBox.y, 2, hitBox.height);
        return bounds.stream().anyMatch(b -> right.overlaps(b) || left.overlaps(b));
    }

    /**
     * Малює HUD-полоску стаміни у верхній частині екрана.
     * @param camera камера для HUD
     */
    public void drawStaminaBar(SpriteBatch batch, OrthographicCamera camera) {

        int index = (int) ((100f - stamina) / 25f);
        index = Math.min(Math.max(index, 0), 4); // захист від виходу за межі

        Texture staminaTexture = staminaStages[index];

        float width = staminaTexture.getWidth();
        float height = staminaTexture.getHeight();

        float x = camera.position.x - camera.viewportWidth / 2 + 20;
        float y = camera.position.y + camera.viewportHeight / 2 - height - 20;

        batch.begin();
        batch.draw(staminaTexture, x, y);
        batch.end();
    }

    /**
     * Завантажує текстури індикатора витривалості.
     */
    public void loadStaminaTextures() {
        staminaStages = new Texture[6];
        for (int i = 0; i < 6; i++) {
            staminaStages[i] = new Texture(Gdx.files.internal("player/staminaAnimation/stamina-" + (i + 1) + ".png"));
        }
    }

    /**
     * Повертає текстуру відповідно до поточного рівня витривалості.
     *
     * @return текстура витривалості
     */
    public Texture getStaminaFrame() {
        int index = Math.min((int)((100f - stamina) / 20f), 5); // 0 до 5
        return staminaStages[index];
    }

    /**
     * Звільняє ресурси
     */
    public void dispose(){
        for (Texture texture : staminaStages) {
            texture.dispose();
        }
    }

    /**
     * Зупиняє звук лазання по стіні під час паузи
     */
    public void stopSound(){
        soundManager.playWallSlideRepeatable(false);
    }
}
