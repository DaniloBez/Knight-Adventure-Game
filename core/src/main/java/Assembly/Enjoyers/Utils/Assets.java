package Assembly.Enjoyers.Utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Клас-утиліта для централізованого завантаження та отримання ігрових ресурсів через AssetManager.
 * Використовується для управління текстурами, звуками, картами та інтерфейсними скінами.
 */
public class Assets {
    /** Єдиний екземпляр AssetManager для всієї гри. */
    private static final AssetManager manager = new AssetManager();

    /**
     * Ініціалізує завантажувач для TiledMap та реєструє базові ресурси для гри.
     * Має бути викликаний на початку завантаження (наприклад, у методі create()).
     */
    public static void init() {
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

    /**
     * Завантажує та повертає TiledMap для рівнів.
     * Завантаження блокує подальший код до повного завантаження.
     * @param levelPath Шлях до карти.
     * @return завантажена карта рівня.
     */
    public static TiledMap getLevel(String levelPath){
        manager.load(levelPath, TiledMap.class);
        manager.finishLoading();
        return manager.get(levelPath, TiledMap.class);
    }

    /**
     * Вивантажує ресурси, пов'язані з рівнем, щоб звільнити пам'ять.
     * @param levelPath Шлях до рівня.
     */
    public static void unloadLevel(String levelPath){
        manager.unload( levelPath);
    }

    /**
     * Блокує виконання до остаточного завершення попередньо запущених операцій завантаження.
     */
    public static void finishLoading() {
        manager.finishLoading();
    }

    /**
     * Повертає завантажений ресурс за шляхом і типом.
     * @param path   шлях до ресурсу у внутрішньому каталозі
     * @param type   клас ресурсу (наприклад, Sound.class, TextureAtlas.class)
     * @param <T>    тип ресурсу
     * @return екземпляр вказаного ресурсу
     */
    public static <T> T get(String path, Class<T> type) {
        return manager.get(path, type);
    }

    /**
     * Оновлює стан менеджера завантаження.
     * @return true, якщо всі поточні завдання завантаження завершені, інакше false
     */
    public static boolean update() {
        return manager.update();
    }

    /**
     * Повертає відсоток прогресу завантаження.
     * @return значення від 0.0 до 1.0
     */
    public static float getProgress() {
        return manager.getProgress();
    }

    /**
     * Звільняє усі ресурси, завантажені через AssetManager. Викликати при завершенні гри.
     */
    public static void dispose() {
        manager.dispose();
    }

    /**
     * Повертає доступ до самого AssetManager для розширених операцій.
     * @return екземпляр AssetManager
     */
    public static AssetManager getManager() {
        return manager;
    }
}
