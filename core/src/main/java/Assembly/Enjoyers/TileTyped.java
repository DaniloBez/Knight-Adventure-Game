package Assembly.Enjoyers;

import java.util.HashMap;

public enum TileTyped {
    Sky(1, false, "Sky"),
    BlockFlat(125, true, "BlockFlat"),
    Block(99, true, "Block"),
    Ladder(121, true, "Ladder"),
    WoodLeftArch(105, true, "WoodLeftArch"),
    WoodFLat(106, true, "WoodFLat"),
    WoodRightArch(107, true, "WoodRightArch");

    public static final int TILE_SIZE = 8;

    private int id;
    private boolean collidable;
    private String name;

    TileTyped(int id, boolean collidable, String name) {
        this.id = id;
        this.collidable = collidable;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isCollidable() {
        return collidable;
    }

    private static HashMap<Integer, TileTyped> tileMap;

    static {
        tileMap = new HashMap<Integer, TileTyped>();
        for (TileTyped tileType : TileTyped.values()) {
            tileMap.put(tileType.getId(), tileType);
        }
    }

    public static TileTyped getTileTypeById(int id) {
        return tileMap.get(id);
    }


}
