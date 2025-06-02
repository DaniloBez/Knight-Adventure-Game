package Assembly.Enjoyers;

import Assembly.Enjoyers.Player.Player;
import Assembly.Enjoyers.Utils.MusicManager;
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
 *
 * @Deprecated
 * Вже не використовуватиметься, слід використовувати {@link MainGame} або {@link Assembly.Enjoyers.Screens}
 */
@Deprecated
public class Main implements ApplicationListener {
    //region variables
    private SpriteBatch batch;
    private Viewport viewport;
    private OrthographicCamera camera;
    private Player player;
    private Sprite floorSprite;
    private Sprite wallSprite1;
    private Sprite wallSprite2;
    private List<Rectangle> bounds;
    private Sprite background;
    //endregion

    /**
     * Ініціалізація об'єктів сцени, персонажа, камери, спрайтів та колізій.
     */
    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1920, 1080, camera);
        batch = new SpriteBatch();
        player = new Player();

        MusicManager.init();

        floorSprite = new Sprite(new Texture("temp\\floor.png"));
        floorSprite.setSize(10000f, 200f);
        floorSprite.setPosition(-2000,  0);

        wallSprite1 = new Sprite(new Texture("temp\\wall.png"));
        wallSprite1.setSize(200f, 2000f);
        wallSprite1.setPosition(1000,  500);

        wallSprite2 = new Sprite(new Texture("temp\\wall.png"));
        wallSprite2.setSize(200f, 2000f);
        wallSprite2.setPosition(1700,  0);

        background = new Sprite(new Texture("temp\\background.jpg"));
        background.setSize(1920, 1080);

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
        player.move(bounds);
        camera.position.set(
            player.sprite.getX() + player.sprite.getWidth() / 2,
            player.sprite.getY() + player.sprite.getHeight(),
            0
        );
        camera.update();
        background.setPosition(camera.position.x - background.getWidth() / 2, camera.position.y - background.getHeight() / 2);

        gl.glClearColor(0, 0, 0, 1.0f);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        draw();
        batch.end();

        //player.drawHitBox(camera);
        player.drawStaminaBar(camera);
    }

    /**
     * Відповідає за малювання всіх спрайтів у грі (платформа, стіни, гравець).
     */
    private void draw() {
        background.draw(batch);
        floorSprite.draw(batch);
        wallSprite1.draw(batch);
        wallSprite2.draw(batch);
        TextureRegion currentPlayerFrame = player.getFrame(Gdx.graphics.getDeltaTime(), false);
        batch.draw(currentPlayerFrame, player.sprite.getX(), player.sprite.getY(), player.sprite.getWidth(), player.sprite.getHeight());
    }

    /**
     * Метод паузи, що викликається коли вікно не активне
     */
    @Override
    public void pause() {
        MusicManager.pause();
    }

    /**
     * Метод, що поновлює гру, коли вікно знову стає активним
     */
    @Override
    public void resume() {
        MusicManager.resume();
    }

    /**
     * Метод, що прибирає сміття, коли об'єкт видаляється
     */
    @Override
    public void dispose() {
        batch.dispose();
        MusicManager.dispose();
        player.dispose();
    }
}
