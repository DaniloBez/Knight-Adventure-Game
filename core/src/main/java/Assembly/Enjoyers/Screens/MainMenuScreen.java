package Assembly.Enjoyers.Screens;

import Assembly.Enjoyers.MainGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Головне меню гри.
 * Відображає назву гри та надає користувачеві кнопки для запуску гри, переходу до налаштувань або виходу.
 */
public class MainMenuScreen implements Screen {
    private MainGame game;
    private final Texture background;
    private final BitmapFont font;
    private final Stage stage;
    private final Skin skin;

    /**
     * Конструктор головного меню.
     * Ініціалізує сцену, завантажує фон та шрифт, створює UI.
     * @param game головний клас гри
     */
    public MainMenuScreen(MainGame game) {
        this.game = game;
        background = new Texture("temp/background.jpg");
        font = new BitmapFont();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        createUI();
    }

    /**
     * Створює елементи UI: заголовок і кнопки (почати гру, налаштування, вийти).
     */
    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = font;

        Label title = new Label("Knight Adventure", titleStyle);
        title.setFontScale(2f);
        title.setAlignment(Align.center);

        TextButton startButton = new TextButton("Почати гру", skin);
        TextButton settingsButton = new TextButton("Налаштування", skin);
        TextButton exitButton = new TextButton("Вийти", skin);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.settingsScreen);
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });


        table.add(title).padBottom(80).row();
        table.add(startButton).width(300).height(60).padBottom(20).row();
        table.add(settingsButton).width(300).height(60).padBottom(20).row();
        table.add(exitButton).width(300).height(60).row();
    }

    /**
     * Відображення кожного кадру. Малює фон та сцену.
     * @param delta час між кадрами
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getViewport().apply();
        game.batch.setProjectionMatrix(stage.getCamera().combined);

        game.batch.begin();
        game.batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    /** Оновлення розмірів при зміні розміру вікна. */
    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /** Викликається при паузі (не використовується). */
    @Override public void pause() {}

    /** Викликається при відновленні (не використовується). */
    @Override public void resume() {}

    /**
     * Викликається при приховуванні екрану.
     * Вимикає обробку вводу для цієї сцени.
     */
    @Override public void hide() {
        if (Gdx.input.getInputProcessor() == stage)
            Gdx.input.setInputProcessor(null);
    }

    /**
     * Очищення ресурсів екрана.
     */
    @Override public void dispose() {
        background.dispose();
        font.dispose();
        stage.dispose();

        game = null;
    }

    /**
     * Викликається при показі екрану.
     * Встановлює сцену як обробник вводу.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
}
