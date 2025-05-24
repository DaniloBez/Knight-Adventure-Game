package Assembly.Enjoyers;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.Gdx.gl;

/**
 * Клас Main є точкою входу для гри, реалізованої з використанням libGDX.
 * Відповідає за створення камери, сцен, персонажа, об'єктів та рендер сцени.
 */
public class Main implements ApplicationListener {
    //region variables
    private SpriteBatch batch;
    private Viewport viewport;
    private OrthographicCamera camera;
    private Character character;
    private Sprite floorSprite;
    private Sprite wallSprite1;
    private Sprite wallSprite2;
    private List<Rectangle> bounds;
    //endregion

    /**
     * Ініціалізація об'єктів сцени, персонажа, камери, спрайтів та колізій.
     */
    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1920, 1080, camera);
        batch = new SpriteBatch();
        character = new Character();

        floorSprite = new Sprite(new Texture("temp\\floor.png"));
        floorSprite.setSize(10000f, 200f);
        floorSprite.setPosition(-2000,  0);

        wallSprite1 = new Sprite(new Texture("temp\\wall.png"));
        wallSprite1.setSize(200f, 2000f);
        wallSprite1.setPosition(1000,  500);

        wallSprite2 = new Sprite(new Texture("temp\\wall.png"));
        wallSprite2.setSize(200f, 2000f);
        wallSprite2.setPosition(1700,  0);

        bounds = new ArrayList<>();
        bounds.add(new Rectangle(-2000f, 0f, 10000f, 200f));
        bounds.add(new Rectangle(1000f, 500f, 200f, 2000f));
        bounds.add(new Rectangle(1700f, 0f, 200f, 2000f));
    }

    /**
     * Оновлює розміри viewport при зміні розміру вікна.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Основний метод рендеру, викликається кожен кадр. Оновлює камеру, очищає екран і малює всі об'єкти.
     */
    @Override
    public void render() {
        character.move(bounds);
        camera.position.set(
            character.sprite.getX() + character.sprite.getWidth() / 2,
            character.sprite.getY() + character.sprite.getHeight(),
            0
        );
        camera.update();

        gl.glClearColor(0, 0, 0, 1.0f);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        draw();
        batch.end();

        character.drawHitBox(camera);
        character.drawStaminaBar(camera);
    }

    /**
     * Відповідає за малювання всіх спрайтів у грі (платформа, стіни, гравець).
     */
    private void draw() {
        floorSprite.draw(batch);
        wallSprite1.draw(batch);
        wallSprite2.draw(batch);
        TextureRegion currentPlayerFrame = character.getFrame(Gdx.graphics.getDeltaTime());
        batch.draw(currentPlayerFrame, character.sprite.getX(), character.sprite.getY(), character.sprite.getWidth(), character.sprite.getHeight());
    }

    @Override
    public void pause() {
        // Нічого не реалізовано
    }

    @Override
    public void resume() {
        // Нічого не реалізовано
    }

    @Override
    public void dispose() {
        // Нічого не реалізовано
    }
}
