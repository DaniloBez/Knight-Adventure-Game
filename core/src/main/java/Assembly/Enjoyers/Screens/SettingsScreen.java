package Assembly.Enjoyers.Screens;

import Assembly.Enjoyers.MainGame;
import Assembly.Enjoyers.Utils.Assets;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


/**
 * Екран налаштувань гри.
 * Дозволяє змінювати гучність музики, звуків, переналаштовувати клавіши й зберігати зміни та повертатись до головного меню.
 */
public class SettingsScreen implements Screen {
    private MainGame game;
    private final Stage stage;
    private final Skin skin;
    private final Preferences prefs;

    private final Texture background;

    private float initialMusic;
    private float initialSound;
    private KeyBinding[] bindings;


    /**
     * Конструктор екрана налаштувань.
     * Ініціалізує сцену, завантажує збережені налаштування та створює UI.
     *
     * @param game основний клас гри
     */
    public SettingsScreen(MainGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        background = new Texture("temp/background.jpg");

        skin = Assets.get("skin/uiskin.json", Skin.class);
        prefs = Gdx.app.getPreferences("settings");

        createUI();
    }

    /**
     * Створює інтерфейс користувача: слайдери гучності, кнопки збереження та повернення назад.
     */
    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label musicLabel = new Label("Гучнiсть музики", skin);
        Label soundLabel = new Label("Гучнiсть звукiв", skin);
        Label musicValueLabel = new Label("", skin);
        Label soundValueLabel = new Label("", skin);

        Slider musicSlider = new Slider(0f, 1f, 0.01f, false, skin);
        Slider soundSlider = new Slider(0f, 1f, 0.01f, false, skin);

        initialMusic = prefs.getFloat("musicVolume", 0.5f);
        initialSound = prefs.getFloat("soundVolume", 0.5f);
        musicSlider.setValue(initialMusic);
        soundSlider.setValue(initialSound);
        musicValueLabel.setText(Math.round(initialMusic * 100) + "%");
        soundValueLabel.setText(Math.round(initialSound * 100) + "%");

        musicSlider.addListener(event -> {
            musicValueLabel.setText(Math.round(musicSlider.getValue() * 100) + "%");
            return false;
        });
        soundSlider.addListener(event -> {
            soundValueLabel.setText(Math.round(soundSlider.getValue() * 100) + "%");
            return false;
        });

        TextButton saveButton = new TextButton("Зберегти", skin);
        TextButton backButton = new TextButton("Назад", skin);

        final Label savedLabel = new Label("Збережено!", skin);
        savedLabel.setVisible(false);
        stage.addActor(savedLabel);
        savedLabel.setPosition(20, 50);

        table.add(new Label("Звук", skin)).colspan(2).padTop(20).center().row();

        table.add(musicLabel).left();
        table.add(musicSlider).width(200);
        table.add(musicValueLabel).padLeft(10).row();

        table.add(soundLabel).left().padTop(20);
        table.add(soundSlider).width(200).padTop(20);
        table.add(soundValueLabel).padLeft(10).padTop(20).row();

        table.add(new Label("Керування", skin)).colspan(2).padTop(20).center().row();

        bindings = new KeyBinding[]{
            new KeyBinding("Вгору", "keyUp", Input.Keys.W),
            new KeyBinding("Вниз", "keyDown", Input.Keys.S),
            new KeyBinding("Влiво", "keyLeft", Input.Keys.A),
            new KeyBinding("Вправо", "keyRight", Input.Keys.D),
            new KeyBinding("Стрибок", "keyJump", Input.Keys.SPACE),
            new KeyBinding("Ривок", "keyDash", -1000 + Input.Buttons.LEFT),
            new KeyBinding("Карабкання", "keyClimb", -1000 + Input.Buttons.RIGHT),
        };

        for (KeyBinding binding : bindings) {
            addKeyBinding(table, skin, binding);
        }

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.buttonPress();
                initialMusic = musicSlider.getValue();
                prefs.putFloat("musicVolume", musicSlider.getValue());
                initialSound = soundSlider.getValue();
                prefs.putFloat("soundVolume", soundSlider.getValue());

                for (KeyBinding binding : bindings) {
                    prefs.putInteger(binding.prefKey, binding.currentValue);
                    binding.initialValue = binding.currentValue;
                }

                prefs.flush();
                game.loadVolume();

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
                game.buttonPress();
                boolean musicChanged = Math.abs(musicSlider.getValue() - initialMusic) > 0.01f;
                boolean soundChanged = Math.abs(soundSlider.getValue() - initialSound) > 0.01f;
                boolean keyChanged = false;

                for (KeyBinding binding : bindings) {
                    if (binding.initialValue != binding.currentValue) {
                        keyChanged = true;
                        break;
                    }
                }

                if (musicChanged || soundChanged || keyChanged) {
                    Dialog dialog = new Dialog("Увага", skin) {
                        protected void result(Object obj) {
                            if ((Boolean)obj) {
                                game.setScreen(new MainMenuScreen(game));
                            }
                        }
                    };
                    dialog.text("Ви не зберегли змiни. Вийти без збереження?");
                    dialog.button("Так", true);
                    dialog.button("Нi", false);
                    dialog.show(stage);
                } else {
                    game.setScreen(game.mainMenuScreen);
                }
            }
        });

        table.add(saveButton).padTop(50).padRight(20).width(140).height(50);
        table.add(backButton).padTop(50).width(140).height(50);
    }

    /**
     * Внутрішній клас для зберігання інформації про одну клавішу керування.
     */
    private class KeyBinding {
        String label;
        String prefKey;
        int initialValue;
        int currentValue;
        TextButton button;

        /**
         * Конструктор KeyBinding.
         *
         * @param label       Назва дії
         * @param prefKey     Ключ для збереження в Preferences
         * @param defaultValue Значення за замовчуванням
         */
        KeyBinding(String label, String prefKey, int defaultValue) {
            this.label = label;
            this.prefKey = prefKey;
            this.initialValue = prefs.getInteger(prefKey, defaultValue);
            this.currentValue = initialValue;
        }
    }

    /**
     * Додає елемент керування клавішею до таблиці інтерфейсу.
     *
     * @param table Таблиця для додавання UI-елементів
     * @param skin  Скін для стилізації UI
     * @param binding Об'єкт KeyBinding, який описує налаштування клавіші
     */
    private void addKeyBinding(Table table, Skin skin, KeyBinding binding) {
        Label label = new Label(binding.label + ":", skin);
        label.setWidth(150);
        label.setWrap(false);

        TextButton keyButton = new TextButton(getKeyName(binding.currentValue), skin);
        keyButton.setWidth(120);
        binding.button = keyButton;

        keyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                keyButton.setText("Натиснiть...");
                Gdx.input.setInputProcessor(new InputAdapter() {
                    @Override
                    public boolean keyDown(int keycode) {
                        if (isKeyAlreadyUsed(keycode, binding)) {
                            showErrorDialog("Ця клавіша вже використовується!");
                            keyButton.setText(getKeyName(binding.currentValue));
                        } else {
                            binding.currentValue = keycode;
                            keyButton.setText(getKeyName(keycode));
                        }
                        Gdx.input.setInputProcessor(stage);
                        return true;
                    }

                    @Override
                    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                        int mouseCode = -1000 + button;
                        if (isKeyAlreadyUsed(mouseCode, binding)) {
                            showErrorDialog("Ця кнопка миші вже використовується!");
                            keyButton.setText(getKeyName(binding.currentValue));
                        } else {
                            binding.currentValue = mouseCode;
                            keyButton.setText(getKeyName(mouseCode));
                        }
                        Gdx.input.setInputProcessor(stage);
                        return true;
                    }
                });
            }
        });

        table.add(label).left().padTop(10).width(150);
        table.add(keyButton).left().padTop(10).width(120).row();
    }

    /**
     * Перевіряє, чи вже використовується вказана клавіша в інших налаштуваннях.
     *
     * @param keycode Ключ клавіші або кнопки миші
     * @param currentBinding Поточний KeyBinding, який змінюється
     * @return true, якщо клавіша вже використовується, інакше false
     */
    private boolean isKeyAlreadyUsed(int keycode, KeyBinding currentBinding) {
        for (KeyBinding kb : bindings) {
            if (kb != currentBinding && kb.currentValue == keycode) {
                return true;
            }
        }
        return false;
    }

    /**
     * Показує діалог з повідомленням про помилку.
     *
     * @param message Текст повідомлення
     */
    private void showErrorDialog(String message) {
        Dialog dialog = new Dialog("Увага", skin) {
            @Override
            protected void result(Object object) {
            }
        };
        dialog.text(message);
        dialog.button("ОК");
        dialog.show(stage);
    }

    /**
     * Отримує ім'я клавіші або кнопки миші за кодом.
     *
     * @param code Код клавіші або кнопки миші (для миші від'ємний код)
     * @return Назва клавіші або кнопки
     */
    private String getKeyName(int code) {
        if (code >= 0) return Input.Keys.toString(code);
        int mouseButton = code + 1000;
        return switch (mouseButton) {
            case Input.Buttons.LEFT -> "LMB";
            case Input.Buttons.RIGHT -> "RMB";
            case Input.Buttons.MIDDLE -> "MMB";
            case 3 -> "MB4";
            case 4 -> "MB5";
            default -> "Mouse" + mouseButton;
        };
    }


    /**
     * Викликається при показі екрана. Встановлює сцену як обробник вводу.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Рендерить сцену налаштувань.
     *
     * @param delta час між кадрами
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }


    /**
     * Оновлення розмірів при зміні розміру вікна.
     * @param width нова ширина
     * @param height нова висота
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Викликається при паузі (не використовується).
     */
    @Override
    public void pause() {

    }

    /**
     * Викликається при відновленні (не використовується).
     */
    @Override
    public void resume() {

    }

    /**
     * Викликається при приховуванні екрана. Скидає обробку вводу.
     */
    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == stage)
            Gdx.input.setInputProcessor(null);

    }

    /// Очищує ресурси сцени та скіна.
    @Override
    public void dispose() {
        stage.dispose();
        game = null;
    }
}
