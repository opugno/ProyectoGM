package puppy.code;
 
import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
 
/**
 * GM-7: Interfaz del patron Strategy para disparo.
 * Cada implementación define un aforma de disparo
 * se inyecta en tiempo de ejecucióncuando se recogen los power-ups.
 */
public interface EstrategiaDisparo 
{
    /**
     * Crea las balas correspondientes a esta estrategia.
     * @param x       posición x de inicio centro-base del objeto
     * @param y       posición y de inicio tope del objeto
     * @param txBala  textura a usar para las balas
     * @param esEnemiga true si las balas van hacia abajo de un enemigo
     * @return la lista de balas creadas listas para agregarse al juego
     */
    ArrayList<Bullet> crear(float x, float y, Texture txBala, boolean esEnemiga);
}
