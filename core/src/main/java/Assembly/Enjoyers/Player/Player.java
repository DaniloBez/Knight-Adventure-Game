package Assembly.Enjoyers.Player;

import Assembly.Enjoyers.Map.GameMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

import static com.badlogic.gdx.Gdx.graphics;
import static com.badlogic.gdx.Gdx.input;

public class Player {
    //region variables
    // --- Sprite ---
    private final Texture texture;
    public Sprite sprite;
    private Rectangle hitBox;
    private final float hitBoxXOffset = 52f;
    private final float hitBoxYOffset = 18f;
    private PlayerState currentState = PlayerState.IDLE;
    private final PlayerAnimationManager animationManager = new PlayerAnimationManager();
    private final PlayerSoundManager soundManager = new PlayerSoundManager();

    // --- Basic movement ---
    private float velocityY = 0;
    private float velocityX = 0;
    private final float gravity = -1200f;
    private final float moveSpeed = 600f;
    private final float jumpForce = 600f;
    public boolean facingRight = false;

    // --- Walls ---
    private boolean onGround = false;
    private boolean prevOnGround = false;
    private boolean touchingWall = false;
    private boolean lastWallRight = false;
    private final float wallSlideSpeed = -200f;
    private final float wallJumpForceX = 1200f;

    // --- Dash ---
    private float dashXVelocity = 0;
    private float dashYVelocity = 0;
    private final float dashForce = 1000f;
    private int dashCount = 1;

    // --- Stamina ---
    private float stamina = 100f;
    private final float staminaDrain = 20f;

    // --- UI ---
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    // respawn
    private float respawnX = 920;
    private float respawnY = 450;

    //endregion

    /**
     * Конструктор персонажа, ініціалізує текстуру, спрайт та хитбокс.
     */
    public Player(){
        texture = new Texture("player\\adventurer-idle-01.png");
        sprite = new Sprite(texture);
        sprite.setSize(texture.getWidth() * 3, texture.getHeight() * 3);
        sprite.setPosition(respawnX, respawnY);
        hitBox = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
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
        shapeRenderer.end();
    }

    /**
     * Основна функція оновлення руху та взаємодії з рівнем.
     * @param bounds список прямокутників колізій
     */
    public void move(List<Rectangle> bounds) {
        float delta = graphics.getDeltaTime();

        currentState = PlayerState.IDLE;

        float moveX = handleHorizontalInput(delta);
        applyGravity(delta);
        dash();

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
     * Обробка поведінки стану гравця залежно від ситуацій під час гри.
     * Увімкнення звукових ефектів гравця.
     *
     * @param moveX Рух по осі OX
     * @param delta Час між кадрами
     */
    private void playerStateHandler(float moveX, float delta){
        soundManager.playWallSlideRepeatable(currentState == PlayerState.WALL_SLIDING);

        if(currentState != PlayerState.WALL_CLIMBING && currentState != PlayerState.WALL_SLIDING && currentState != PlayerState.WALL_GRABBING && currentState != PlayerState.JUMPING) {
            if (!isDashReady())
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
    public TextureRegion getFrame(float delta) {
        return animationManager.getCurrentFrame(currentState, facingRight, delta);
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
                    sprite.setX(bound.x - hitBox.width - hitBoxXOffset);
                else if (moveX < 0)
                    sprite.setX(bound.x + bound.width - hitBoxXOffset);

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
                    sprite.setY(bound.y - hitBox.height - hitBoxYOffset);
                } else if (velocityY < 0) {
                    sprite.setY(bound.y + bound.height - hitBoxYOffset);
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
        if (touchingWall && !onGround) {
            if (input.isButtonPressed(Input.Buttons.RIGHT) && stamina > 0) {
                currentState = PlayerState.WALL_GRABBING;

                if(velocityY <= 150) {
                    if (input.isKeyPressed(Keys.W)) {
                        currentState = PlayerState.WALL_CLIMBING;
                        velocityY = 150;
                    }
                    else if (input.isKeyPressed(Keys.S)) {
                        currentState = PlayerState.WALL_CLIMBING;
                        velocityY = -150;
                    }
                    else if (velocityY < 0)
                        velocityY = 0;

                }

                if(input.isKeyPressed(Keys.W) && input.isKeyJustPressed(Keys.SPACE)) {
                    velocityY = jumpForce;
                    stamina -= staminaDrain;
                    currentState = PlayerState.JUMPING;
                }

                stamina -= staminaDrain * delta;
                if (stamina < 0) stamina = 0;
            } else if (!input.isKeyPressed(Keys.S) && velocityY < wallSlideSpeed) {
                velocityY = wallSlideSpeed;
                currentState = PlayerState.WALL_SLIDING;
            }

            if(input.isKeyJustPressed(Keys.SPACE) && !input.isKeyPressed(Keys.W)){
                if (checkRightTouching(bounds)) {
                    if (lastWallRight && stamina > 0) {
                        velocityX = -wallJumpForceX;
                        velocityY = jumpForce;
                        stamina -= staminaDrain;
                    }
                    else if(!lastWallRight) {
                        velocityX = -wallJumpForceX;
                        velocityY = jumpForce;
                    }
                    lastWallRight = true;
                    currentState = PlayerState.JUMPING;
                }
                else if (checkLeftTouching(bounds)) {
                    if (!lastWallRight && stamina > 0) {
                        velocityX = wallJumpForceX;
                        velocityY = jumpForce;
                        stamina -= staminaDrain;
                    } else if (lastWallRight) {
                        velocityX = wallJumpForceX;
                        velocityY = jumpForce;
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
        if (onGround && input.isKeyJustPressed(Keys.SPACE)) {
            velocityY = jumpForce;
            currentState = PlayerState.JUMPING;
        }
    }

    /**
     * Скидає лічильники деша та стаміни, коли персонаж на землі.
     */
    private void resetDashAndStamina() {
        dashCount = 1;
        stamina = 100f;
    }
    /**
     * Обробляє вхідні команди AD для руху вліво/вправо.
     * @param delta Проміжок часу між поточним та останнім кадром у секундах.
     * @return зсув по осі OX
     */
    private float handleHorizontalInput(float delta) {
        float moveX = 0;
        if (input.isKeyPressed(Keys.A)) {
            moveX -= moveSpeed * delta;
            facingRight = false;
        }
        if (input.isKeyPressed(Keys.D)) {
            moveX += moveSpeed * delta;
            facingRight = true;
        }
        return moveX;
    }

    /**
     * Застосовує гравітацію до вертикальної швидкості.
     * @param delta Проміжок часу між поточним та останнім кадром у секундах.
     */
    private void applyGravity(float delta) {
        velocityY += gravity * delta;
    }

    /**
     * Обробляє деш у різні сторони (LMB), з лімітом на кількість.
     */
    private void dash(){
        dashXVelocity *= 0.98f;
        dashYVelocity *= 0.98f;

        if(Math.abs(dashXVelocity) < 400)
            dashXVelocity = 0;
        if(Math.abs(dashYVelocity) < 400)
            dashYVelocity = 0;

        if((input.isButtonJustPressed(Input.Buttons.LEFT) || input.isKeyJustPressed(Keys.SHIFT_LEFT) ) && dashCount !=0 && isDashReady()){
            if(input.isKeyPressed(Keys.D))
                dashXVelocity = dashForce;
            if (input.isKeyPressed(Keys.A))
                dashXVelocity = -dashForce;
            if (input.isKeyPressed(Keys.W))
                dashYVelocity = dashForce;
            if (input.isKeyPressed(Keys.S))
                dashYVelocity = -dashForce;

            dashCount--;

            soundManager.play(PlayerState.DASHING);
        }
    }

    /**
     * Повертає чи готовий гравець до наступного ривку.
     * @return Повертає чи готовий гравець до наступного ривку.
     */
    private boolean isDashReady(){
        return dashXVelocity == 0 && dashYVelocity == 0;
    }

    /**
     * Оновлює координати прямокутника hitBox за позицією спрайта.
     */
    private void updateHitBox() {
        hitBox.set(sprite.getX() + hitBoxXOffset, sprite.getY(), sprite.getWidth() - 2 * hitBoxXOffset, sprite.getHeight() - hitBoxYOffset);
    }

    /**
     * Перевіряє, чи персонаж стоїть на землі.
     * @param bounds Список хіт боксів у рівні
     * @return чи персонаж стоїть на землі
     */
    private boolean checkFeetTouching(List<Rectangle> bounds) {
        Rectangle feet = new Rectangle(hitBox.x + hitBox.width / 8, hitBox.y - 1, hitBox.width / 4 * 3, 2);
        for (Rectangle bound : bounds)
            if (feet.overlaps(bound))
                return true;

        return false;
    }

    /**
     * Перевіряє контакт правої сторони з перешкодою.
     * @param bounds Список хіт боксів у рівні
     * @return чи персонаж дотикаєтеся справа
     */
    private boolean checkRightTouching(List<Rectangle> bounds) {
        Rectangle right = new Rectangle( hitBox.x + hitBox.width - 2, hitBox.y - hitBox.height / 8, 4, hitBox.height / 4 * 3);
        for (Rectangle bound : bounds)
            if (right.overlaps(bound))
                return true;

        return false;
    }

    /**
     * Перевіряє контакт лівої сторони з перешкодою.
     * @param bounds Список хіт боксів у рівні
     * @return чи персонаж дотикаєтеся зліва
     */
    private boolean checkLeftTouching(List<Rectangle> bounds) {
        Rectangle left = new Rectangle( hitBox.x - 2, hitBox.y - hitBox.height / 8, 4, hitBox.height / 4 * 3);
        for (Rectangle bound : bounds)
            if (left.overlaps(bound))
                return true;

        return false;
    }

    /**
     * Перевіряє, чи персонаж знаходиться поруч зі стіною (ліворуч або праворуч) (для ковзання).
     * @param bounds Список хіт боксів у рівні
     * @return чи персонаж дотикаєтеся справа або зліва
     */
    private boolean checkWallTouching(List<Rectangle> bounds) {
        Rectangle testX1 = new Rectangle(hitBox);
        Rectangle testX2 = new Rectangle(hitBox);
        testX1.x += moveSpeed * graphics.getDeltaTime();
        testX2.x -= moveSpeed * graphics.getDeltaTime();
        for (Rectangle bound : bounds)
            if (testX1.overlaps(bound) || testX2.overlaps(bound))
                return true;

        return false;
    }

    /**
     * Малює HUD-полоску стаміни у верхній частині екрана.
     * @param camera камера для HUD
     */
    public void drawStaminaBar(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        float maxWidth = 200f;
        float height = 20f;

        float x = camera.position.x - camera.viewportWidth / 2 + 20;
        float y = camera.position.y + camera.viewportHeight / 2 - 40;

        float staminaRatio = stamina / 100f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.LIGHT_GRAY);
        shapeRenderer.rect(x, y, maxWidth, height);

        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(x, y, maxWidth * staminaRatio, height);

        shapeRenderer.end();
    }

    /**
     * Звільняє ресурси
     */
    public void dispose(){
        soundManager.dispose();
        texture.dispose();
        animationManager.dispose();
    }
}
