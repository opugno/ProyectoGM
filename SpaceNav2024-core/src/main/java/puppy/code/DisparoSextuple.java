package puppy.code;
 
import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
 
/** Upgrade 5, maximo: 6 balas — cobertura total del arco frontal. */
public class DisparoSextuple implements EstrategiaDisparo 
{
    @Override
    public ArrayList<Bullet> crear(float x, float y, Texture txBala, boolean esEnemiga) 
    {
        ArrayList<Bullet> balas = new ArrayList<>();
        int dir = esEnemiga ? -1 : 1;
        balas.add(new Bullet(x - 15, y, -5, 3 * dir, txBala, esEnemiga)); // extremo izquierdo
        balas.add(new Bullet(x -  9, y, -2, 5 * dir, txBala, esEnemiga)); // semi izquierdo
        balas.add(new Bullet(x -  3, y,  0, 6 * dir, txBala, esEnemiga)); // centro izquierdo
        balas.add(new Bullet(x +  3, y,  0, 6 * dir, txBala, esEnemiga)); // centro derecha
        balas.add(new Bullet(x +  9, y,  2, 5 * dir, txBala, esEnemiga)); // semi derecho
        balas.add(new Bullet(x + 15, y,  5, 3 * dir, txBala, esEnemiga)); // extremo derecha
        return balas;
    }
}
