package Assembly.Enjoyers.Player;

/**
 * Представляє можливі стани гравця у грі.
 * Ці стани використовуються для керування логікою анімації.
 */
public enum PlayerState {
    IDLE,
    RUNNING,
    JUMPING,
    FALLING,
    DASHING,
    LANDING,
    WALL_CLIMBING,
    WALL_SLIDING,
    WALL_GRABBING,
    ATTACKING,
    DAMAGED,
    DYING
}
