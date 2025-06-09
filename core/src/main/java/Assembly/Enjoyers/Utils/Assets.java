package Assembly.Enjoyers.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {
    private static final AssetManager manager = new AssetManager();

    public static void init(){
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

        manager.load("player/adventurer.atlas", TextureAtlas.class);

        manager.load("sounds/button.ogg", Sound.class);
        manager.load("sounds/climb.ogg", Sound.class);
        manager.load("sounds/dash.ogg", Sound.class);
        manager.load("sounds/death.ogg", Sound.class);
        manager.load("sounds/jump.ogg", Sound.class);
        manager.load("sounds/land.ogg", Sound.class);
        manager.load("sounds/steps.ogg", Sound.class);
        manager.load("sounds/wallSlide.ogg", Sound.class);

        manager.load("skin/uiskin.json", Skin.class);
    }

    public static TiledMap getLevel1(){
        manager.load("maps/night_level/map.tmx", TiledMap.class);
        manager.finishLoading();
        return manager.get("maps/night_level/map.tmx", TiledMap.class);
    }

    public static void unloadLevel1(){
        manager.unload("maps/night_level/map.tmx");
    }

    public static void finishLoading() {
        manager.finishLoading();
    }

    public static <T> T get(String path, Class<T> type) {
        return manager.get(path, type);
    }

    public static boolean update() {
        return manager.update();
    }

    public static float getProgress() {
        return manager.getProgress();
    }

    public static void dispose() {
        manager.dispose();
    }

    public static AssetManager getManager() {
        return manager;
    }
}
