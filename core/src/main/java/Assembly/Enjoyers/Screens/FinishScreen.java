package Assembly.Enjoyers.Screens;

import Assembly.Enjoyers.MainGame;
import Assembly.Enjoyers.Utils.Assets;
import Assembly.Enjoyers.Utils.TimeConverter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Екран після проходження рівня.
 * Відображає статистику проходження рівня, дозволяє перейти до обирання рівнів.
 */
public class FinishScreen implements Screen {
    private MainGame game;

    private final Stage stage;
    private final Skin skin;
    final Texture background;

    /**
     * Створює новий екран після проходження рівня.
     * @param game посилання на головний об'єкт гри.
     * @param deathCount кількість смертей за рівень.
     * @param playTime кількість часу, яку гравець витратив на проходження рівня.
     */
    public FinishScreen(MainGame game, int deathCount, float playTime) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = Assets.get("skin/uiskin.json", Skin.class);
        background = new Texture(Gdx.files.internal("temp/background.jpg"));

        Table table = new Table(skin);
        table.setFillParent(true);
        table.center();

        String message = "Рiвень пройдено!\nСмертей: " + deathCount +
            "\nЧас: " + TimeConverter.formatTime(playTime);

        TextButton toLevelsButton = new TextButton("До вибору рiвнiв", skin);
        toLevelsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.buttonPress();
                game.setScreen(game.levelsScreen);
            }
        });

        table.add(message).pad(20).row();
        table.add(toLevelsButton).pad(10).row();
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }



    /**
     * Викликається, коли цей екран стає активним.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Малює вміст екрана кожного кадру.
     * @param delta час у секундах з моменту останнього кадру.
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

    /**
     * Викликається при зміні розмірів вікна або екрана.
     *
     * @param width  нова ширина
     * @param height нова висота
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Викликається при паузі (наприклад, коли гра згортається).
     */
    @Override
    public void pause() {

    }

    /**
     * Викликається при відновленні гри після паузи.
     */
    @Override
    public void resume() {

    }

    /**
     * Викликається, коли екран перестає бути активним.
     */
    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == stage)
            Gdx.input.setInputProcessor(null);
    }

    /**
     * Звільняє ресурси, які використовуються екраном.
     */
    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();

        game = null;
    }
}
