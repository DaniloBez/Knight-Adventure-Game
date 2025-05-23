package Assembly.Enjoyers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class TiledGameMap extends GameMap {
    TiledMap tiledMap;
    OrthogonalTiledMapRenderer tiledMapRender;

    public TiledGameMap() {
        tiledMap = new TmxMapLoader().load("maps/map.tmx");
        tiledMapRender = new OrthogonalTiledMapRenderer(tiledMap);
    }

    @Override
    public void render(OrthographicCamera camera) {
        tiledMapRender.setView(camera);
        tiledMapRender.render();
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void dispose() {
        tiledMap.dispose();
    }

    @Override
    public TileTyped getTileTypeByCoordinate(int layer, int col, int row) {

        return null;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getLayers() {
        return 0;
    }
}
