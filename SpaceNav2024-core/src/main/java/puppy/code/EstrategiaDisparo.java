package puppy.code;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;

/**
 * GM-7: Interfaz Strategy de disparo.
 * Ahora recibe el ángulo de rotación de quien dispara (en grados).
 * Esto permite que las balas salgan en la dirección que apunta la nave/enemigo.
 */
public interface EstrategiaDisparo {
    /**
     * Crea las balas de esta estrategia.
     * @param x          posición X del centro del sprite
     * @param y          posición Y del centro del sprite
     * @param anguloGrados  rotación actual en grados (0 = apunta arriba)
     * @param txBala     textura de la bala
     * @param esEnemiga  true si daña al jugador
     */
    ArrayList<Bullet> crear(float x, float y, float anguloGrados, Texture txBala, boolean esEnemiga);
}