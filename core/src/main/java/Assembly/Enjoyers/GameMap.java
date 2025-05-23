package Assembly.Enjoyers;

import com.badlogic.gdx.graphics.OrthographicCamera;

public abstract class GameMap {
    public abstract void render(OrthographicCamera camera);
    public abstract void update(float delta);
    public abstract void dispose();

    public TileTyped getTileTypeByLocation(int layer, float x, float y) {
        return this.getTileTypeByCoordinate(layer, (int) (x / TileTyped.TILE_SIZE), (int) (y / TileTyped.TILE_SIZE));
    }

    public abstract TileTyped getTileTypeByCoordinate(int layer, int col, int row);

    public abstract int getWidth();
    public abstract int getHeight();
    public abstract int getLayers();
}
