package Assembly.Enjoyers;

import Assembly.Enjoyers.Map.GameMap;
import Assembly.Enjoyers.Map.TileTyped;
import Assembly.Enjoyers.Map.TiledGameMap;
import Assembly.Enjoyers.Player.Player;
import Assembly.Enjoyers.Utils.MusicManager;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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
    private List<Rectangle> bounds, spikes;
    private GameMap gameMap;
    //endregion

    /**
     * Ініціалізація об'єктів сцени, персонажа, камери, спрайтів та колізій.
     */
    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1920, 1080, camera);
        batch = new SpriteBatch();
        gameMap = new TiledGameMap();
        bounds = gameMap.getCollisionRects();
        spikes = gameMap.getSpikes();
        player = new Player();
        MusicManager.init();
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
            player.sprite.getY() + 2*player.sprite.getHeight(),
            0
        );
        camera.update();

        gl.glClearColor(0, 0, 0, 1.0f);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(Gdx.input.justTouched()){
            Vector3 pos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            TileTyped type = gameMap.getTileTypeByLocation(3 , pos.x, pos.y);

            if(type != null) {
                System.out.println("Clicked on tile: " + type.getId() + " " + type.getName() + " " + type.isCollidable());
            } else {
                System.out.println("No tile found at: " + pos.x + ", " + pos.y);
            }
        }

        gameMap.render(camera);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        draw();
        batch.end();

        player.drawStaminaBar(camera);
    }

    /**
     * Відповідає за малювання всіх спрайтів у грі (платформа, стіни, гравець).
     */
    private void draw() {
        TextureRegion currentPlayerFrame = player.getFrame(Gdx.graphics.getDeltaTime());
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
        gameMap.dispose();
        batch.dispose();
        MusicManager.dispose();
        player.dispose();
    }
}
