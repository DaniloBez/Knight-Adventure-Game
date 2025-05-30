package Assembly.Enjoyers.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

import static Assembly.Enjoyers.Map.TileTyped.BoneSpike;

/**
 * Реалізація мапи гри на основі Tiled (.tmx).
 * Завантажує карту, рендерить її та обробляє колізії і пастки (SPIKE).
 */
public class TiledGameMap extends GameMap {
    TiledMap tiledMap;
    OrthogonalTiledMapRenderer tiledMapRender;

    /**
     * Завантажує Tiled-карту з TMX-файлу та ініціалізує рендерер.
     */
    public TiledGameMap() {
        tiledMap = new TmxMapLoader().load("maps/night_level/map.tmx");
        tiledMapRender = new OrthogonalTiledMapRenderer(tiledMap);
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
        tiledMap.dispose();
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
     * Повертає список прямокутників колізії, виключаючи плитки з ефектом SPIKE.
     *
     * @return список прямокутників колізії
     */
    @Override
    public List<Rectangle> getCollisionRects() {
        List<Rectangle> rects = new ArrayList<>();
        int tileSize = TileTyped.TILE_SIZE;

        for (int layer = 0; layer < getLayers(); layer++) {
            TiledMapTileLayer tiledLayer = (TiledMapTileLayer) tiledMap.getLayers().get(layer);

            for (int x = 0; x < tiledLayer.getWidth(); x++) {
                for (int y = 0; y < tiledLayer.getHeight(); y++) {
                    TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                    if (cell == null) continue;

                    TileTyped tileType = TileTyped.getTileTypeById(cell.getTile().getId());
                    if (tileType != null && tileType.isCollidable() && tileType.getEffectType() != TileTyped.TileEffectType.SPIKE) {
                        rects.add(new Rectangle(
                            x * tileSize,
                            y * tileSize,
                            tileSize,
                            tileSize
                        ));
                    }
//                    else if (tileType == BoneSpike) {
//                        rects.add(new Rectangle(
//                            x * tileSize + tileSize/8,
//                            y * tileSize + tileSize/8,
//                            tileSize/4,
//                            tileSize/4
//                        ));
//                    }
                }
            }
        }
        return rects;
    }

    /**
     * Повертає список прямокутників шипів (SPIKE), включно зі зміною розміру для BoneSpike.
     *
     * @return список прямокутників шипів
     */
    @Override
    public List<Rectangle> getSpikes() {
        List<Rectangle> spikeRects = new ArrayList<>();
        int tileSize = TileTyped.TILE_SIZE;

        for (int layer = 0; layer < getLayers(); layer++) {
            TiledMapTileLayer tiledLayer = (TiledMapTileLayer) tiledMap.getLayers().get(layer);

            for (int x = 0; x < tiledLayer.getWidth(); x++) {
                for (int y = 0; y < tiledLayer.getHeight(); y++) {
                    TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                    if (cell == null || cell.getTile() == null) continue;

                    TileTyped tileType = TileTyped.getTileTypeById(cell.getTile().getId());
                    if (tileType != null && tileType.getEffectType() == TileTyped.TileEffectType.SPIKE) {
                        spikeRects.add(new Rectangle(
                            x * tileSize + (tileType == TileTyped.BoneSpike ? tileSize/8 : 0),
                            y * tileSize + (tileType == TileTyped.BoneSpike ? tileSize/8 : 0),
                            tileType == TileTyped.BoneSpike ? tileSize/4 : tileSize,
                            tileType == TileTyped.BoneSpike ? tileSize/4 : tileSize
                        ));
                    }
                }
            }
        }
        return spikeRects;
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
