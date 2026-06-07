package puppy.code;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
 
/** Upgrade 2: 3 balas en abanico (izquierda, centro, derecha). */
public class DisparoTriple implements EstrategiaDisparo
{
	@Override
    public ArrayList<Bullet> crear(float x, float y, Texture txBala, boolean esEnemiga) 
	{
        ArrayList<Bullet> balas = new ArrayList<>();
        int dir = esEnemiga ? -1 : 1;
        balas.add(new Bullet(x - 5, y, -2, 5 * dir, txBala, esEnemiga)); // diagonal izquierda
        balas.add(new Bullet(x,     y,  0, 6 * dir, txBala, esEnemiga)); // recta al centro
        balas.add(new Bullet(x + 5, y,  2, 5 * dir, txBala, esEnemiga)); // diagonal derecha
        return balas;
	}
}
