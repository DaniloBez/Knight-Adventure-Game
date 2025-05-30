package Assembly.Enjoyers.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import java.util.List;

/**
 * Абстрактний клас, який представляє загальний інтерфейс для ігрової мапи.
 * Дочірні класи реалізують рендеринг, оновлення, колізії та логіку взаємодії з плитками.
 */
public abstract class GameMap {
    /**
     * Рендерить мапу за допомогою переданої камери.
     *
     * @param camera ортографічна камера
     */
    public abstract void render(OrthographicCamera camera);
    /**
     * Оновлює стан мапи.
     *
     * @param delta час з моменту останнього оновлення
     */
    public abstract void update(float delta);
    /**
     * Звільняє ресурси, пов’язані з мапою.
     */
    public abstract void dispose();

    /**
     * Повертає тип плитки за координатами у пікселях.
     *
     * @param layer індекс шару
     * @param x     координата X у пікселях
     * @param y     координата Y у пікселях
     * @return тип плитки або null, якщо не знайдено
     */
    public TileTyped getTileTypeByLocation(int layer, float x, float y) {
        return this.getTileTypeByCoordinate(layer, (int) (x / TileTyped.TILE_SIZE), (int) (y / TileTyped.TILE_SIZE));
    }

    /**
     * Повертає список прямокутників для обробки колізій.
     *
     * @return список прямокутників колізій
     */
    public abstract List<Rectangle> getCollisionRects();

    /**
     * Повертає список прямокутників шипів (SPIKE), які можуть завдати шкоди гравцю.
     *
     * @return список прямокутників пасток
     */
    public abstract List<Rectangle> getSpikes();

    /**
     * Повертає тип плитки за координатами у сітці мапи.
     *
     * @param layer індекс шару
     * @param col   стовпчик (X)
     * @param row   рядок (Y)
     * @return тип плитки або null, якщо не знайдено
     */
    public abstract TileTyped getTileTypeByCoordinate(int layer, int col, int row);

    /**
     * @return ширина мапи у кількості тайлів
     */
    public abstract int getWidth();

    /**
     * @return висота мапи у кількості тайлів
     */
    public abstract int getHeight();

    /**
     * @return кількість шарів у мапі
     */
    public abstract int getLayers();
}
