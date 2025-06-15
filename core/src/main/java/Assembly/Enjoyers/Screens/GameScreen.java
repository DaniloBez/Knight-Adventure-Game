package Assembly.Enjoyers.Screens;

import Assembly.Enjoyers.MainGame;
import Assembly.Enjoyers.Map.AnimatedBlocks.CrumblingBlock;
import Assembly.Enjoyers.Map.AnimatedBlocks.JumpPad;
import Assembly.Enjoyers.Map.GameMap;
import Assembly.Enjoyers.Map.TiledGameMap;
import Assembly.Enjoyers.Utils.Assets;
import Assembly.Enjoyers.Utils.MusicManager;
import Assembly.Enjoyers.Player.Player;
import Assembly.Enjoyers.Utils.TimeConverter;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

/**
 * Основний ігровий екран, на якому відображається рівень, гравець та логіка паузи.
 * Обробляє рендеринг, логіку руху, обробку паузи та інтерфейс паузи.
 */
public class GameScreen implements Screen {
    //region variables
    private MainGame game;

    private Viewport viewport;
    private OrthographicCamera camera;

    private Player player;
    private List<Rectangle> staticBounds;
    private List<Rectangle> spikes;
    private List<CrumblingBlock> crumblingBlocks;
    private final List<Rectangle> activeCollisions = new ArrayList<>();
    private GameMap gameMap;

    private boolean isPaused = false;

    private Stage pauseStage;
    private Skin skin;
    private BitmapFont font;
    private InputProcessor inputProcessor;
    private Rectangle endOfTheLevel;

    private float playTime; // Таймер гри в секундах
    private final String levelId;
    private Preferences pref;
    private int deathCount;


    // Координати респауну та кінця рівня
    private float respawnX;
    private float respawnY;
    //endregion

    /**
     * Створює новий ігровий екран.
     * @param game головний об'єкт гри
     */
    public GameScreen(MainGame game, String levelId) {
        this.game = game;
        this.levelId = levelId;
        pref = Gdx.app.getPreferences("Levels");

        setUpGame();
        createUI();
    }

    /**
     * Ініціалізує ігрові об'єкти, карту, колізії, гравця і музику.
     */
    private void setUpGame(){
        deathCount = 0;
        playTime = 0f;

        camera = new OrthographicCamera();
        viewport = new StretchViewport(1920, 1080, camera);

        switch (levelId) {
            case "levelId-1":
                gameMap = new TiledGameMap("maps/level1/map.tmx");
                respawnX = 950;
                respawnY = 400;
                endOfTheLevel = new Rectangle(23420, 800, 60, 130);
                break;
            case "levelId-2":
                gameMap = new TiledGameMap("maps/level2/map.tmx");
                respawnX = 950;
                respawnY = 400;
                endOfTheLevel = new Rectangle(27005, 1025, 60, 130);
                break;
            case "levelId-3":
                gameMap = new TiledGameMap("maps/level3/map.tmx");
                respawnX = 950;
                respawnY = 4850;

                endOfTheLevel = new Rectangle(14900, 4640, 60, 130);
                break;

            default:
                throw new IllegalArgumentException("Unknown level ID: " + levelId);
        }

        staticBounds = gameMap.getCollisionRects();
        crumblingBlocks = gameMap.getCrumblingBlocks();
        spikes = gameMap.getSpikes();

        player = new Player(this::incDeath, respawnX, respawnY);
        player.loadStaminaTextures();
        MusicManager.init();
    }

    /**
     * Створює сцену паузи та інтерфейс із кнопками.
     */
    private void createUI() {
        skin = Assets.get("skin/uiskin.json", Skin.class);
        font = skin.getFont("default-font");
        font.getData().setScale(2f);
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
                game.buttonPress();
                resume();
            }
        });
        buttonRestart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.buttonPress();
                incDeath();
                resume();
                player.respawn();
            }
        });

        buttonMainMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.buttonPress();
                saveDeath();
                game.setScreen(game.mainMenuScreen);
                dispose();
            }
        });
    }

    /**
     * Збільшує кількість смертей.
     */
    public void incDeath(){
        deathCount++;
    }

    /**
     * Зберігає дані про смерть у файл.
     */
    private void saveDeath(){
        final int prevDeathCount = pref.getInteger(levelId + "Deaths", 0);
        pref.putInteger(levelId + "Deaths", deathCount + prevDeathCount);
        pref.flush();
    }

    /**
     * Зберігає дані про час у файл.
     */
    private void saveTime(){
        final float bestTime = pref.getFloat(levelId + "BestTime", 0f);
        if (bestTime > playTime || bestTime == 0) {
            pref.putFloat(levelId + "BestTime", playTime);
            pref.flush();
        }
    }

    /**
     * Основний метод рендерингу, викликається кожен кадр.
     * @param delta час між кадрами
     */
    @Override
    public void render(float delta) {
        delta = Math.min(delta, 1/60f);

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (isPaused) resume();
            else pause();
        }

        if (!isPaused) {
            activeCollisions.clear();

            activeCollisions.addAll(staticBounds);

            for (CrumblingBlock block : crumblingBlocks) {
                block.update(delta);
                if (block.isActive() && block.getStage() < 5) {
                    activeCollisions.add(block.getBounds());
                }
            }

            for (JumpPad jumpPad : gameMap.getJumpPads()) {
                jumpPad.update(delta);

                if (player.getHitBox().overlaps(jumpPad.getTriggerBounds())) {

                    if (!jumpPad.isTriggered()) {
                        jumpPad.trigger();
                        player.applyJumpPadBoost();
                    }
                }
            }

            player.move(activeCollisions, spikes,crumblingBlocks ,delta);
            playTime += delta;

            if (player.getHitBox().overlaps(endOfTheLevel)) {
                finishLevel();
                return;
            }

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

        String timeStr = TimeConverter.formatTime(playTime);
        font.draw(game.batch, "Час гри: " + timeStr, camera.position.x + viewport.getWorldWidth() / 3, camera.position.y + viewport.getWorldHeight()/2 - 20);
        if (isPaused) {
            font.draw(game.batch, "Смертей: " + deathCount, camera.position.x + viewport.getWorldWidth() / 3, camera.position.y + viewport.getWorldHeight()/2 - 60);
        }

        Texture staminaFrame = player.getStaminaFrame();
        float staminaX = camera.position.x - camera.viewportWidth / 2 + 20;
        float staminaY = camera.position.y + camera.viewportHeight / 2 - 60;

        game.batch.draw(staminaFrame, staminaX, staminaY, 215, 44);

        game.batch.end();

        if (isPaused) {
            pauseStage.act(delta);
            pauseStage.draw();
        }
    }


    /**
     * При успішному закінченню рівня, гравцеві показується вікно зі статистикою.
     */
    private void finishLevel() {
        saveDeath();
        saveTime();
        Gdx.input.setCursorCatched(false);

        game.gameOver(new FinishScreen(game, deathCount, playTime));
    }

    /**
     * Малює гравця та тіло після смерті.
     * @param delta час між кадрами
     */
    private void draw(float delta) {
        TextureRegion currentPlayerFrame = player.getFrame(delta, isPaused);
        game.batch.draw(currentPlayerFrame, player.sprite.getX(), player.sprite.getY(), player.sprite.getWidth(), player.sprite.getHeight());

        for (CrumblingBlock block : gameMap.getCrumblingBlocks()) {

            if (!block.isDestroyed()) {
                TextureRegion blockFrame = block.getCurrentFrame(delta);
                Rectangle bounds = block.getBounds();
                game.batch.draw(blockFrame, bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }

        for (JumpPad jumpPad : gameMap.getJumpPads()) {
            TextureRegion frame = jumpPad.getCurrentFrame();
            Rectangle drawBounds = jumpPad.getDrawBounds();
            game.batch.draw(frame, drawBounds.x, drawBounds.y, drawBounds.width, drawBounds.height);
        }


        player.drawCorpse(game.batch, staticBounds, delta);
    }

    /**
     * Обробка зміни розміру вікна гри.
     * @param width нова ширина
     * @param height нова висота
     */
    @Override public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Активує паузу гри.
     */
    @Override public void pause() {
        MusicManager.pause();
        isPaused = true;
        player.stopSound();
        Gdx.input.setInputProcessor(inputProcessor);
        Gdx.input.setCursorCatched(false);
    }

    /**
     * Відновлює гру з паузи.
     */
    @Override public void resume() {
        MusicManager.resume();
        isPaused = false;
        Gdx.input.setInputProcessor(null);
        Gdx.input.setCursorCatched(true);
    }

    /** Викликається при приховуванні екрана. */
    @Override public void hide() {}

    /**
     * Очищення ресурсів після завершення екрану.
     */
    @Override public void dispose() {
        game = null;

        player.dispose();
        gameMap.dispose();
        pauseStage.dispose();

        font.dispose();
        MusicManager.dispose();
    }

    /**
     * Викликається при показі екрана.
     */
    @Override public void show() {
        Gdx.input.setCursorCatched(true);
    }
}
