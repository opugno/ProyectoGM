package puppy.code;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
 
/** Upgrade 2: 4 balas en abanico (izquierda, centro, derecha). */
public class DisparoCuadruple implements EstrategiaDisparo
{
	@Override
    public ArrayList<Bullet> crear(float x, float y, Texture txBala, boolean esEnemiga) 
	{
        ArrayList<Bullet> balas = new ArrayList<>();
        int dir = esEnemiga ? -1 : 1;
        balas.add(new Bullet(x - 10, y, -3, 5 * dir, txBala, esEnemiga)); // diagonal exterior izquiera
        balas.add(new Bullet(x -  3, y,  0, 6 * dir, txBala, esEnemiga)); // recta izquierda
        balas.add(new Bullet(x +  3, y,  0, 6 * dir, txBala, esEnemiga)); // recta derecha
        balas.add(new Bullet(x + 10, y,  3, 5 * dir, txBala, esEnemiga)); // diagonal exterior derecha
        return balas;
	}
}