package Assembly.Enjoyers.Screens;

import Assembly.Enjoyers.MainGame;
import Assembly.Enjoyers.Utils.TimeConverter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Екран вибору рівнів у грі.
 * Відображає список рівнів з інформацією про найкращий час і кількість смертей,
 * дозволяє перейти до обраного рівня або повернутись у головне меню.
 */
public class LevelsScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private MainGame game;
    private Texture background;
    private Label[] deathLabels;
    private Label[] timeLabels;
    private TextButton[] levelButtons;
    private Preferences pref;

    private final int levelCount = 3;

    /**
     * Створює новий екран вибору рівнів.
     *
     * @param game посилання на головний об'єкт гри
     */
    public LevelsScreen(MainGame game) {
        this.game = game;
        background = new Texture("temp/background.png");
        pref = Gdx.app.getPreferences("levels");
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        deathLabels = new Label[levelCount];
        timeLabels = new Label[levelCount];
        levelButtons = new TextButton[levelCount];

        for (int i = 0; i < levelCount; i++) {
            deathLabels[i] = new Label("Death", skin);
            timeLabels[i] = new Label("Time", skin);
            levelButtons[i] = new TextButton("Level " + i, skin);
        }

        createUI();
    }

    /**
     * Створює UI компоненти: кнопки рівнів, інформаційні ярлики та кнопку "Назад".
     */
    private void createUI() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label title = new Label("Оберiть рiвень", skin);
        title.setFontScale(4f);
        table.add(title).colspan(2).padBottom(50).center().row();

        for (int i = 1; i <= levelCount; i++) {
            final String levelId = "levelId-" + i;

            TextButton levelButton = new TextButton("Рiвень " + i, skin);
            levelButtons[i - 1] = levelButton;

            levelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!levelButton.isDisabled()) {
                        game.buttonPress();
                        game.setScreen(new GameScreen(game, levelId));
                    }
                }
            });

            Table infoTable = new Table();
            infoTable.add(timeLabels[i-1]).left().row();
            infoTable.add(deathLabels[i-1]).left();

            table.add(levelButton).width(200).height(60).padBottom(20).padRight(20);
            table.add(infoTable).left().padBottom(20).row();
        }

        TextButton backButton = new TextButton("Назад", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.buttonPress();
                game.setScreen(game.mainMenuScreen);
            }
        });

        table.add(backButton).colspan(2).padTop(30).width(200).height(50);

        updateUI();
    }

    /**
     * Оновлює текст ярликів (час проходження та кількість смертей) та кнопки для кожного рівня.
     */
    private void updateUI() {
        pref = Gdx.app.getPreferences("Levels");

        for (int i = 0; i < levelCount; i++) {
            final String levelId = "levelId-" + (i + 1);
            float bestTime = pref.getFloat(levelId + "BestTime", 0);
            int deaths = pref.getInteger(levelId + "Deaths", 0);

            timeLabels[i].setText("Час: " + (bestTime != 0 ? TimeConverter.formatTime(bestTime) : "--:--"));
            deathLabels[i].setText("Смертей: " + deaths);

            boolean unlocked = (i == 0) || pref.getFloat("levelId-" + i + "BestTime", 0) > 0;
            TextButton btn = levelButtons[i];
            btn.setDisabled(!unlocked);
        }
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
     *
     * @param delta час у секундах з моменту останнього кадру
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

        updateUI();

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
        background.dispose();
        skin.dispose();
        stage.dispose();

        game = null;
    }
}
