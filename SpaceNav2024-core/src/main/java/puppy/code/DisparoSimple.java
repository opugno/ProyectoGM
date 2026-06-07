package puppy.code;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;

//PATRÓN STRATEGY
/** Primera fase: 1 bala recta hacia arriba, abajo si es enemigo. */
public class DisparoSimple implements EstrategiaDisparo
{
	@Override
	public ArrayList<Bullet> crear(float x, float y, Texture txBala, boolean esEnemiga)
	{
		ArrayList<Bullet> balas = new ArrayList<>();
		int dir = esEnemiga ? -1 : 1;
		balas.add(new Bullet(x, y, 0, 6 * dir, txBala, esEnemiga));
		return balas;
	}
}
