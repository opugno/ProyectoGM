package puppy.code;
 
import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
 
/** Upgrade 4: 5 balas — abanico amplio de cobertura. */
public class DisparoQuintuple implements EstrategiaDisparo 
{
    @Override
    public ArrayList<Bullet> crear(float x, float y, Texture txBala, boolean esEnemiga) 
    {
        ArrayList<Bullet> balas = new ArrayList<>();
        int dir = esEnemiga ? -1 : 1;
        balas.add(new Bullet(x - 12, y, -4, 4 * dir, txBala, esEnemiga)); // extremo izquierdo
        balas.add(new Bullet(x -  5, y, -2, 5 * dir, txBala, esEnemiga)); // semi izquierdo
        balas.add(new Bullet(x,      y,  0, 6 * dir, txBala, esEnemiga)); // centro
        balas.add(new Bullet(x +  5, y,  2, 5 * dir, txBala, esEnemiga)); // semi derecho
        balas.add(new Bullet(x + 12, y,  4, 4 * dir, txBala, esEnemiga)); // extremo derecho
        return balas;
    }
}
