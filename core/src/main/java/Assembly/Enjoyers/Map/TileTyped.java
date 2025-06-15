package Assembly.Enjoyers.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.HashMap;

/**
 * Перерахування, яке представляє типи плиток (tiles) на мапі гри.
 * Кожен тип має свій унікальний ID, назву, ефект і ознаку про колізійність.
 */
public enum TileTyped {

    SteelSpike(268, true, "SteelSpike", TileEffectType.SPIKE),
    BrickWall(248, true, "BrickWall", TileEffectType.NONE),
    RightCastleWall(247, true, "BrickWall", TileEffectType.NONE),
    GrassBlock(327, true, "GrassBlock", TileEffectType.NONE),
    CastleFloor(2438, true, "CastleFloor",  TileEffectType.NONE),
    ToxicBlock(2448, true, "ToxicBlock",  TileEffectType.SPIKE),
    ToxicWoodBlock(2449, true, "ToxicWoodBlock",  TileEffectType.SPIKE),
    CastleSmoothBlock(2441, true, "CastleSmoothBlock",  TileEffectType.NONE),
    RightGrassBlock(328, true, "RightGrassBlock", TileEffectType.NONE),
    LeftGrassBlock(326, true, "LeftGrassBlock", TileEffectType.NONE),
    LeftDirtWall(337, true, "LeftDirtWall", TileEffectType.NONE),
    RightDirtWall(339, true, "RightDirtWall", TileEffectType.NONE),
    BoneSpike(614, true, "BoneSpike",  TileEffectType.SPIKE),
    LeftWoodPlatform(272, true, "LeftWoodPlatform",  TileEffectType.NONE),
    FlatWoodPlatform(273, true, "FlatWoodPlatform",  TileEffectType.NONE),
    RightWoodPlatform(274, true, "RightWoodPlatform",  TileEffectType.NONE),
    CastlePlatform(238, true, "CastlePlatform",  TileEffectType.NONE),
    RightCastleCorner(239, true, "StoneBlock", TileEffectType.NONE),
    CastleBrick(246, true, "StoneBlock", TileEffectType.NONE),
    StoneBlock(249, true, "StoneBlock", TileEffectType.NONE),
    CrumblingBlock(2434, true, "CrumblingBlock",  TileEffectType.CRUMBLING),
    DarkStoneBlock(264, true, "DarkStoneBlock",  TileEffectType.NONE),
    Jump_Pad(673, true, "Jump_Pad", TileEffectType.JUMP_PAD),
    CheckPoint(182, true, "CheckPoint",  TileEffectType.CHECK_POINT);


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

    private static Int2ObjectMap<TileTyped> tileMap;

    static {
        tileMap = new Int2ObjectOpenHashMap<>();
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
        CHECK_POINT,
        JUMP_PAD,
        CRUMBLING,
    }
}
