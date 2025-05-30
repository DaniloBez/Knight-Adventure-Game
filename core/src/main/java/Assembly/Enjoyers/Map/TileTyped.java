package Assembly.Enjoyers.Map;

import java.util.HashMap;

/**
 * Перерахування, яке представляє типи плиток (tiles) на мапі гри.
 * Кожен тип має свій унікальний ID, назву, ефект і ознаку про колізійність.
 */
public enum TileTyped {

    //for night_level
    SteelSpike(628, true, "SteelSpike", TileEffectType.SPIKE),
    BrickWall(608, true, "BrickWall", TileEffectType.NONE),
    GrassBlock(687, true, "GrassBlock", TileEffectType.NONE),
    BoneSpike(974, true, "BoneSpike",  TileEffectType.SPIKE),
    LeftWoodPlatform(632, true, "LeftWoodPlatform",  TileEffectType.NONE),
    FlatWoodPlatform(633, true, "FlatWoodPlatform",  TileEffectType.NONE),
    RightWoodPlatform(634, true, "RightWoodPlatform",  TileEffectType.NONE),
    BrickPlatform(598, true, "BrickPlatform",  TileEffectType.NONE),
    StoneBlock(609, true, "StoneBlock",  TileEffectType.NONE),
    DarkStoneBlock(624, true, "DarkStoneBlock",  TileEffectType.NONE),
    CheckPoint(542, true, "CheckPoint",  TileEffectType.CHECK_POINT);

    /** Розмір плитки в пікселях */
    public static final int TILE_SIZE = 32;

    private int id;
    private boolean collidable;
    private String name;
    private TileEffectType effectType;

    /**
     * Конструктор для типу плитки.
     *
     * @param id         унікальний ідентифікатор
     * @param collidable чи має плитка колізію
     * @param name       ім'я плитки
     * @param effectType тип ефекту плитки
     */
    TileTyped(int id, boolean collidable, String name, TileEffectType effectType) {
        this.id = id;
        this.collidable = collidable;
        this.name = name;
        this.effectType = effectType;
    }

    /**
     * Повертає ідентифікатор плитки.
     *
     * @return ID плитки
     */
    public int getId() {
        return id;
    }

    /**
     * Повертає ім’я плитки.
     *
     * @return назва плитки
     */
    public String getName() {
        return name;
    }

    /**
     * Визначає, чи є плитка колізійною.
     *
     * @return true, якщо колізійна; false — інакше
     */
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

    /**
     * Повертає тип плитки за її ID.
     *
     * @param id ідентифікатор плитки
     * @return відповідний TileTyped або null, якщо не знайдено
     */
    public static TileTyped getTileTypeById(int id) {
        return tileMap.get(id);
    }


    /**
     * Повертає тип ефекту, пов’язаний із плиткою.
     *
     * @return тип ефекту
     */
    public TileEffectType getEffectType() {
        return effectType;
    }

    /**
     * Перерахування можливих ефектів плиток.
     */
    public enum TileEffectType {
        NONE,
        SPIKE,
        SLIME_BlOCK,
        CHECK_POINT
    }
}
