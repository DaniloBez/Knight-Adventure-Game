package Assembly.Enjoyers.Screens;

import Assembly.Enjoyers.MainGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SettingsScreen implements Screen {
    private MainGame game;
    private final Stage stage;
    private final Skin skin;
    private final Preferences prefs;

    private float initialMusic;
    private float initialSound;


    public SettingsScreen(MainGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        prefs = Gdx.app.getPreferences("settings");

        createUI();
    }


    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label musicLabel = new Label("Гучність музики", skin);
        Label soundLabel = new Label("Гучність звуків", skin);
        Label musicValueLabel = new Label("", skin);
        Label soundValueLabel = new Label("", skin);

        Slider musicSlider = new Slider(0f, 1f, 0.01f, false, skin);
        Slider soundSlider = new Slider(0f, 1f, 0.01f, false, skin);

        // Завантаження початкових значень
        initialMusic = prefs.getFloat("musicVolume", 0.5f);
        initialSound = prefs.getFloat("soundVolume", 0.5f);
        musicSlider.setValue(initialMusic);
        soundSlider.setValue(initialSound);
        musicValueLabel.setText(String.format("%.2f", initialMusic));
        soundValueLabel.setText(String.format("%.2f", initialSound));

        // Обробка зміни значень
        musicSlider.addListener(event -> {
            musicValueLabel.setText(String.format("%.2f", musicSlider.getValue()));
            return false;
        });
        soundSlider.addListener(event -> {
            soundValueLabel.setText(String.format("%.2f", soundSlider.getValue()));
            return false;
        });

        TextButton saveButton = new TextButton("Зберегти", skin);
        TextButton backButton = new TextButton("Назад", skin);

        final Label savedLabel = new Label("Збережено!", skin);
        savedLabel.setVisible(false);
        stage.addActor(savedLabel);
        savedLabel.setPosition(20, 20);

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                initialMusic = musicSlider.getValue();
                prefs.putFloat("musicVolume", musicSlider.getValue());
                initialSound = soundSlider.getValue();
                prefs.putFloat("soundVolume", soundSlider.getValue());
                prefs.flush();

                savedLabel.setVisible(true);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        savedLabel.setVisible(false);
                    }
                }, 2);
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean musicChanged = Math.abs(musicSlider.getValue() - initialMusic) > 0.01f;
                boolean soundChanged = Math.abs(soundSlider.getValue() - initialSound) > 0.01f;

                if (musicChanged || soundChanged) {
                    Dialog dialog = new Dialog("Увага", skin) {
                        protected void result(Object obj) {
                            if ((Boolean)obj) {
                                game.setScreen(new MainMenuScreen(game));
                            }
                        }
                    };
                    dialog.text("Ви не зберегли зміни. Вийти без збереження?");
                    dialog.button("Так", true);
                    dialog.button("Нi", false);
                    dialog.show(stage);
                } else {
                    game.setScreen(game.mainMenuScreen);
                }
            }
        });

        table.add(musicLabel).left();
        table.add(musicSlider).width(200);
        table.add(musicValueLabel).padLeft(10).row();

        table.add(soundLabel).left().padTop(20);
        table.add(soundSlider).width(200).padTop(20);
        table.add(soundValueLabel).padLeft(10).padTop(20).row();

        table.add(saveButton).padTop(50).padRight(20).width(140).height(50);
        table.add(backButton).padTop(50).width(140).height(50);
    }



    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == stage)
            Gdx.input.setInputProcessor(null);

    }

    @Override
    public void dispose() {
        skin.dispose();
        stage.dispose();
        game = null;
    }
}
