package Assembly.Enjoyers.Screens;

import Assembly.Enjoyers.MainGame;
import Assembly.Enjoyers.Utils.MusicManager;
import Assembly.Enjoyers.Player.Player;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private MainGame game;
    private Viewport viewport;
    private OrthographicCamera camera;
    private Player player;
    private Sprite floorSprite, wallSprite1, wallSprite2, background;
    private List<Rectangle> bounds;
    private boolean isPaused = false;

    private Stage pauseStage;
    private Skin skin;
    private InputProcessor inputProcessor;

    public GameScreen(MainGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new StretchViewport(1920, 1080, camera);

        player = new Player();
        MusicManager.init();

        floorSprite = new Sprite(new Texture("temp/floor.png"));
        floorSprite.setSize(10000f, 200f);
        floorSprite.setPosition(-2000,  0);

        wallSprite1 = new Sprite(new Texture("temp/wall.png"));
        wallSprite1.setSize(200f, 2000f);
        wallSprite1.setPosition(1000,  500);

        wallSprite2 = new Sprite(new Texture("temp/wall.png"));
        wallSprite2.setSize(200f, 2000f);
        wallSprite2.setPosition(1700,  0);

        background = new Sprite(new Texture("temp/background.jpg"));
        background.setSize(1920, 1080);

        bounds = new ArrayList<>();
        bounds.add(new Rectangle(-2000f, 0f, 10000f, 200f));
        bounds.add(new Rectangle(1000f, 500f, 200f, 2000f));
        bounds.add(new Rectangle(1700f, 0f, 200f, 2000f));

        createUI();
    }

    private void createUI() {
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        pauseStage = new Stage(new ScreenViewport());

        inputProcessor = new InputMultiplexer(pauseStage);

        Gdx.input.setInputProcessor(inputProcessor);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Image bg = new Image(new Texture("temp/floor.png"));
        bg.setSize(1, 1);
        bg.setColor(0, 0, 0, 0.5f);
        bg.setFillParent(true);
        pauseStage.addActor(bg);

        TextButton buttonContinue = new TextButton("Продовжити", skin);
        TextButton buttonRestart = new TextButton("Перезапустити", skin);
        TextButton buttonMainMenu = new TextButton("В головне меню", skin);

        table.add(buttonContinue).pad(10).row();
        table.add(buttonRestart).pad(10).row();
        table.add(buttonMainMenu).pad(10).row();
        pauseStage.addActor(table);

        buttonContinue.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                resume();
            }
        });
        buttonRestart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //TODO
            }
        });

        buttonMainMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.mainMenuScreen);
            }
        });
    }

    @Override
    public void render(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (isPaused) resume();
            else pause();
        }

        if (!isPaused) {
            player.move(bounds);

            camera.position.set(
                player.sprite.getX() + player.sprite.getWidth() / 2,
                player.sprite.getY() + player.sprite.getHeight(),
                0
            );
            camera.update();
            background.setPosition(
                camera.position.x - background.getWidth() / 2,
                camera.position.y - background.getHeight() / 2
            );
        }


        Gdx.gl.glClearColor(0, 0, 0, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        draw(delta);
        game.batch.end();

        player.drawStaminaBar(camera);


        if (isPaused) {
            pauseStage.act(delta);
            pauseStage.draw();
        }
    }

    private void draw(float delta) {
        background.draw(game.batch);
        floorSprite.draw(game.batch);
        wallSprite1.draw(game.batch);
        wallSprite2.draw(game.batch);
        TextureRegion currentPlayerFrame = player.getFrame(delta, isPaused);
        game.batch.draw(currentPlayerFrame, player.sprite.getX(), player.sprite.getY(), player.sprite.getWidth(), player.sprite.getHeight());
    }

    @Override public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    @Override public void pause() {
        MusicManager.pause();
        isPaused = true;
        player.stopSound();
        Gdx.input.setInputProcessor(inputProcessor);
        Gdx.input.setCursorCatched(false);
    }
    @Override public void resume() {
        MusicManager.resume();
        isPaused = false;
        Gdx.input.setInputProcessor(null);
        Gdx.input.setCursorCatched(true);
    }

    @Override public void hide() {}
    @Override public void dispose() {
        player.dispose();
        MusicManager.dispose();

        game = null;
    }
    @Override public void show() {
        Gdx.input.setCursorCatched(true);
    }
}
