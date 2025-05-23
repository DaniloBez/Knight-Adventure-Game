package Assembly.Enjoyers;


import java.util.HashMap;

public enum TileTyped {
    Sky(1, false, "Sky"),
    Block(2, true, "Block"),
    Ladder(3, true, "Ladder"),
    WoodBlock(4, true, "WoodBlock");

    public static final int TILE_TYPE_COUNT = 8;

    private int id;
    private boolean collidable;
    private String name;

    TileTyped(int id, boolean collidable, String name) {
        this.id = id;
        this.collidable = collidable;
        this.name = name;
    }


}
