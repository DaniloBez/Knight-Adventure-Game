package Assembly.Enjoyers;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.Gdx.gl;

public class Main implements ApplicationListener {

    private SpriteBatch batch;
    private Viewport viewport;
    private OrthographicCamera camera;
    private Character character;
    private Sprite floorSprite;
    private Sprite wallSprite1;
    private Sprite wallSprite2;
    private List<Rectangle> bounds;

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

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

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

        character.drawStaminaBar(camera);
    }

    private void draw() {
        floorSprite.draw(batch);
        wallSprite1.draw(batch);
        wallSprite2.draw(batch);
        character.sprite.draw(batch);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
