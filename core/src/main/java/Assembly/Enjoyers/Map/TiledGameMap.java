package Assembly.Enjoyers.Map;

import Assembly.Enjoyers.Utils.Assets;
import Assembly.Enjoyers.Map.AnimatedBlocks.CrumblingBlock;
import Assembly.Enjoyers.Map.AnimatedBlocks.JumpPad;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;
import java.util.List;

import static Assembly.Enjoyers.Map.TileTyped.BoneSpike;
import static Assembly.Enjoyers.Map.TileTyped.SteelSpike;

/**
 * Реалізація мапи гри на основі Tiled (.tmx).
 * Завантажує карту, рендерить її та обробляє колізії і пастки (SPIKE).
 */
public class TiledGameMap extends GameMap {
    private final TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer tiledMapRender;

    private final List<Rectangle> collisionRects = new ArrayList<>();
    private final List<Rectangle> spikeRects = new ArrayList<>();
    private final List<CrumblingBlock> crumblingBlocks = new ArrayList<>();
    private final List<JumpPad> jumpPads = new ArrayList<>();
    private BitmapFont font;
    private SpriteBatch batch;

    /**
     * Завантажує Tiled-карту з TMX-файлу та ініціалізує рендерер.
     */
    public TiledGameMap() {
        tiledMap = Assets.getLevel1();
        tiledMapRender = new OrthogonalTiledMapRenderer(tiledMap);
        batch = (SpriteBatch) tiledMapRender.getBatch();
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        font = skin.getFont("default-font");
        generateCollisionData();
        font.getData().setScale(2f);
    }

    /**
     * Рендерить карту на основі положення камери.
     *
     * @param camera ортографічна камера
     */
    @Override
    public void render(OrthographicCamera camera) {
        tiledMapRender.setView(camera);
        tiledMapRender.render();

        batch.begin();
        for (MapObject object : tiledMap.getLayers().get("Text").getObjects()) {
            if(object instanceof TextMapObject text) {
                font.draw(batch, text.getText(), text.getX(), text.getY());
            }
        }
        batch.end();
    }

    /**
     * Оновлення стану мапи (не використовується).
     *
     * @param delta час з моменту останнього кадру
     */
    @Override
    public void update(float delta) {

    }

    /**
     * Звільняє ресурси карти.
     */
    @Override
    public void dispose() {
        Assets.unloadLevel1();
        //tiledMap.dispose();
        collisionRects.clear();
        spikeRects.clear();
        tiledMapRender.dispose();
    }

    /**
     * Генерує дані колізій, шипів, crumble-блоків та трамплінів із тайлів мапи.
     * Аналізує кожну клітинку кожного шару на основі типу тайлу.
     */
    private void generateCollisionData() {
        int tileSize = TileTyped.TILE_SIZE;

        for (int layer = 0; layer < getLayers(); layer++) {

            if(tiledMap.getLayers().get(layer) instanceof TiledMapTileLayer tiledLayer)
            {
                for (int x = 0; x < tiledLayer.getWidth(); x++) {
                    for (int y = 0; y < tiledLayer.getHeight(); y++) {
                        TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                        if (cell == null || cell.getTile() == null) continue;

                        TileTyped tileType = TileTyped.getTileTypeById(cell.getTile().getId());
                        if (tileType == null) continue;

                        int tileX = x * tileSize;
                        int tileY = y * tileSize;

                        if (tileType.getEffectType() == TileTyped.TileEffectType.SPIKE) {
                            if (tileType == BoneSpike) {
                                int offset = tileSize / 8;
                                int size = tileSize / 4;
                                spikeRects.add(new Rectangle(tileX + offset, tileY + offset, size, size));
                            } else if (tileType == SteelSpike) {
                                int offset = tileSize / 8;
                                int size = tileSize / 2;
                                spikeRects.add(new Rectangle(tileX + offset, tileY + offset, size, size));
                            }
                            else {
                                spikeRects.add(new Rectangle(tileX, tileY, tileSize, tileSize));
                            }
                        } else if (tileType.getEffectType() == TileTyped.TileEffectType.CRUMBLING) {
                            crumblingBlocks.add(new CrumblingBlock(tileX, tileY, tileSize, tileSize));
                        } else if (tileType.getEffectType() == TileTyped.TileEffectType.JUMP_PAD) {
                            jumpPads.add(new JumpPad(tileX, tileY, tileSize, tileSize));
                        }
                        else if (tileType.isCollidable()) {
                            collisionRects.add(new Rectangle(tileX, tileY, tileSize, tileSize));
                        }
                    }
                }
            }
        }
    }

    /**
     * Повертає список прямокутників колізії, виключаючи плитки з ефектом SPIKE.
     *
     * @return список прямокутників колізії
     */
    @Override
    public List<Rectangle> getCollisionRects() {
        return collisionRects;
    }

    /**
     * Повертає список прямокутників шипів (SPIKE), включно зі зміною розміру для BoneSpike.
     *
     * @return список прямокутників шипів
     */
    @Override
    public List<Rectangle> getSpikes() {
        return spikeRects;
    }

    /**
     * @return список crumble-блоків, які руйнуються після контакту
     */
    @Override
    public List<CrumblingBlock> getCrumblingBlocks() {
        return crumblingBlocks;
    }

    /**
     * @return список трамплінів, які підкидають гравця вгору
     */
    @Override
    public List<JumpPad> getJumpPads() {
        return jumpPads;
    }

    /**
     * Повертає тип плитки за координатами шару та клітинки.
     *
     * @param layer індекс шару
     * @param col   стовпчик
     * @param row   рядок
     * @return тип плитки або null, якщо не знайдено
     */
    @Override
    public TileTyped getTileTypeByCoordinate(int layer, int col, int row) {
        TiledMapTileLayer.Cell cell = ((TiledMapTileLayer) tiledMap.getLayers().get(layer)).getCell(col, row);

        if (cell != null) {
            TiledMapTile tile = cell.getTile();

            if (tile != null) {
                int id = tile.getId();
                return TileTyped.getTileTypeById(id);
            }
        }
        return null;
    }

    /**
     * @return ширина мапи (у тайлах)
     */
    @Override
    public int getWidth() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getWidth();
    }

    /**
     * @return висота мапи (у тайлах)
     */
    @Override
    public int getHeight() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getHeight();
    }

    /**
     * @return кількість шарів у мапі
     */
    @Override
    public int getLayers() {
        return tiledMap.getLayers().getCount();
    }
}
