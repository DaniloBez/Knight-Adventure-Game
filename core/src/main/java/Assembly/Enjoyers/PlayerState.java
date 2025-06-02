package Assembly.Enjoyers;

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
    WALL_CLIMBING,
    WALL_SLIDING,
    WALL_GRABBING,
    ATTACKING,
    DAMAGED
}
