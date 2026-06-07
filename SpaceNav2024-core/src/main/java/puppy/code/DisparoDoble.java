package puppy.code;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
 
/** Upgrade 1: 2 balas paralelas separadas 14px. */
public class DisparoDoble implements EstrategiaDisparo
{
	@Override
	public ArrayList<Bullet> crear(float x, float y, Texture txBala, boolean esEnemiga)
	
	{
		ArrayList<Bullet> balas = new ArrayList<>();
		int dir = esEnemiga ? -1 : 1;
        balas.add(new Bullet(x - 7, y, 0, 6 * dir, txBala, esEnemiga));
        balas.add(new Bullet(x + 7, y, 0, 6 * dir, txBala, esEnemiga));
        return balas;
	}
}
