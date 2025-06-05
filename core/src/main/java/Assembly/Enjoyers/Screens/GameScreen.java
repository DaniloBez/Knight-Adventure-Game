package Assembly.Enjoyers.Screens;

import Assembly.Enjoyers.MainGame;
import Assembly.Enjoyers.Map.GameMap;
import Assembly.Enjoyers.Map.TiledGameMap;
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
    //region variables
    private MainGame game;

    private Viewport viewport;
    private OrthographicCamera camera;

    private Player player;
    private List<Rectangle> bounds, spikes;
    private GameMap gameMap;

    private boolean isPaused = false;

    private Stage pauseStage;
    private Skin skin;
    private InputProcessor inputProcessor;
    //endregion

    public GameScreen(MainGame game) {
        this.game = game;
        setUpGame();

        createUI();
    }

    private void setUpGame(){
        camera = new OrthographicCamera();
        viewport = new StretchViewport(1920, 1080, camera);

        gameMap = new TiledGameMap();
        bounds = gameMap.getCollisionRects();
        spikes = gameMap.getSpikes();

        player = new Player();
        MusicManager.init();
    }

    private void createUI() {
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        pauseStage = new Stage(new ScreenViewport());

        inputProcessor = new InputMultiplexer(pauseStage);

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
        delta = Math.min(delta, 1/60f);

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (isPaused) resume();
            else pause();
        }

        if (!isPaused) {
            player.move(bounds, spikes, delta);

            camera.position.set(
                player.sprite.getX() + player.sprite.getWidth() / 2,
                player.sprite.getY() + 2 * player.sprite.getHeight(),
                0
            );
            camera.update();
        }


        Gdx.gl.glClearColor(0, 0, 0, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameMap.render(camera);

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
        TextureRegion currentPlayerFrame = player.getFrame(delta, isPaused);
        game.batch.draw(currentPlayerFrame, player.sprite.getX(), player.sprite.getY(), player.sprite.getWidth(), player.sprite.getHeight());
        player.drawCorpse(game.batch, bounds, delta);
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
        gameMap.dispose();
        MusicManager.dispose();

        game = null;
    }
    @Override public void show() {
        Gdx.input.setCursorCatched(true);
    }
}
