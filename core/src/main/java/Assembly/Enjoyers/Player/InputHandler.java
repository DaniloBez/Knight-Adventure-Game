package Assembly.Enjoyers.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

import static com.badlogic.gdx.Gdx.input;

public class InputHandler {
    private static int up, down, left, right, jump, dash, climb;

    static {
        update();
    }

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

    public enum KeyBinds{
        UP,
        DOWN,
        LEFT,
        RIGHT,
        JUMP,
        DASH,
        CLIMB
    }

    public static boolean getButtonPressed(KeyBinds key){
        int buttonCode = getButtonCode(key);
        if(buttonCode >= 0) return input.isKeyPressed(buttonCode);
        else {
            buttonCode += 1000;
            return input.isButtonPressed(buttonCode);
        }
    }

    public static boolean getButtonJustPressed(KeyBinds key){
        int buttonCode = getButtonCode(key);
        if(buttonCode >= 0) return input.isKeyJustPressed(buttonCode);
        else {
            buttonCode += 1000;
            return input.isButtonJustPressed(buttonCode);
        }
    }

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
