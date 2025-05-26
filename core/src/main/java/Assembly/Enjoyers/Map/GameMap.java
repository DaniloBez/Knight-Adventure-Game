package Assembly.Enjoyers.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;

public abstract class GameMap {
    public abstract void render(OrthographicCamera camera);
    public abstract void update(float delta);
    public abstract void dispose();

    public TileTyped getTileTypeByLocation(int layer, float x, float y) {
        return this.getTileTypeByCoordinate(layer, (int) (x / TileTyped.TILE_SIZE), (int) (y / TileTyped.TILE_SIZE));
    }

    public int getPixelWidth() {
        return this.getWidth() * TileTyped.TILE_SIZE;
    }

    private int getPixelHeight() {
        return this.getHeight() * TileTyped.TILE_SIZE;
    }

    public boolean doesReactCollideWithCoordinate(float x, float y, int width, int height) {
        if(x < 0 || y < 0 || x + width > getPixelWidth() || y+height > getPixelHeight())
            return true;

        for(int row = (int) (y / TileTyped.TILE_SIZE); row < Math.ceil((y+height) / TileTyped.TILE_SIZE); row++) {
            for(int col = (int) (x / TileTyped.TILE_SIZE); col < Math.ceil((x + width) / TileTyped.TILE_SIZE); col++) {
                for(int layer = 0; layer < getLayers(); layer++) {
                    TileTyped tile = getTileTypeByCoordinate(layer, row, col);
                    if(tile != null && tile.isCollidable())
                        return true;
                }
            }
        }
        return false;
    }

    public abstract TileTyped getTileTypeByCoordinate(int layer, int col, int row);

    public abstract int getWidth();
    public abstract int getHeight();
    public abstract int getLayers();
}
