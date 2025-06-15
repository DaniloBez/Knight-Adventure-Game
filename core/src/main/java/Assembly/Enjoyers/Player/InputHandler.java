package Assembly.Enjoyers.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

import static com.badlogic.gdx.Gdx.input;

/**
 * Обробляє налаштування клавіш та кнопок управління для гравця.
 * Завантажує згідно збережених налаштувань з {@link Preferences} і надає методи
 * для перевірки стану кнопок (утримується чи натиснута одинично).
 */
public class InputHandler {
    private static int up, down, left, right, jump, dash, climb;

    static {
        update();
    }

    /**
     * Завантажує налаштування управління з Preferences (файл "Settings").
     * Викликається при ініціалізації і може бути викликаний для оновлення параметрів.
     */
    public static void update(){
        Preferences preferences = Gdx.app.getPreferences("Settings");
        up = preferences.getInteger("keyUp", Input.Keys.W);
        down = preferences.getInteger("keyDown", Input.Keys.S);
        left = preferences.getInteger("keyLeft", Input.Keys.A);
        right = preferences.getInteger("keyRight", Input.Keys.D);
        jump = preferences.getInteger("keyJump", Input.Keys.SPACE);
        dash = preferences.getInteger("keyDash", Input.Buttons.LEFT - 1000);
        climb = preferences.getInteger("keyClimb", Input.Buttons.RIGHT - 1000);
    }

    /**
     * Перераховує всі можливі прив'язки клавіш та кнопок.
     */
    public enum KeyBinds {
        /** Рух угору */
        UP,
        /** Рух вниз */
        DOWN,
        /** Рух вліво */
        LEFT,
        /** Рух вправо */
        RIGHT,
        /** Стрибок */
        JUMP,
        /** Даш */
        DASH,
        /** Лазіння по стіні */
        CLIMB
    }

    /**
     * Перевіряє, чи утримується натиснутою відповідна клавіша або кнопка.
     * @param key тип прив'язки з KeyBinds
     * @return true, якщо клавіша/кнопка утримується, інакше false
     */
    public static boolean getButtonPressed(KeyBinds key){
        int buttonCode = getButtonCode(key);
        if(buttonCode >= 0) return input.isKeyPressed(buttonCode);
        else {
            buttonCode += 1000;
            return input.isButtonPressed(buttonCode);
        }
    }

    /**
     * Перевіряє, чи була натиснута відповідна клавіша або кнопка саме в поточному кадрі.
     * @param key тип прив'язки з KeyBinds
     * @return true, якщо клавіша/кнопка була натиснута цього кадру, інакше false
     */
    public static boolean getButtonJustPressed(KeyBinds key){
        int buttonCode = getButtonCode(key);
        if(buttonCode >= 0) return input.isKeyJustPressed(buttonCode);
        else {
            buttonCode += 1000;
            return input.isButtonJustPressed(buttonCode);
        }
    }

    /**
     * Повертає збережений код клавіші або кнопки (може бути від'ємним для миші).
     * @param key тип прив'язки з KeyBinds
     * @return код клавіші (>0) або (код кнопки - 1000) (<0)
     */
    private static int getButtonCode(KeyBinds key){
        return switch (key){
            case UP -> up;
            case DOWN -> down;
            case LEFT -> left;
            case RIGHT -> right;
            case JUMP -> jump;
            case DASH -> dash;
            case CLIMB -> climb;
        };
    }
}
