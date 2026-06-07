package puppy.code;

import java.util.Random;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
 
/**
 * Enemigo básico: 10 puntos, 1 vida, se mueve con la formación. No dispara.
 * Drop: Vida extra 5% chance
 */
public class EnemigoBasico extends Enemigo
{
	public EnemigoBasico(float x, float y, Texture tx)
	{
		spr = new Sprite(tx);
		spr.setBounds(x, y, 40, 40);
		puntos = 10;
		vida = 1;
		vidaMax = 0;
		intervaloDisparo = 0; //no dispara
		chanceDrop = 5; //porcentaje de dropear vida extra
	}
	
	//Template method, mover con la formacion
	@Override
	protected void mover()
	{
		spr.setPosition(spr.getX() + velFormacionX, spr.getY() + velFormacionY);
	}
	
	 @Override
    protected PowerUp.TipoPowerUp seleccionarTipoDrop(Random r) 
	 {
        return PowerUp.TipoPowerUp.VIDA_EXTRA;
    }
}
