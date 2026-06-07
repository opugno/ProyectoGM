package puppy.code;
 
import java.util.ArrayList;
import java.util.Random;
 
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
 
/**
 * Enemigo medio: 30 puntos, 2 vidas, dispara hacia abajo cada 3 segundos.
 * Drop: Upgrade de disparo (20% chance).
 */
public class EnemigoMedio extends Enemigo 
{
 
    public EnemigoMedio(float x, float y, Texture tx, Texture txBala) 
    {
        spr              = new Sprite(tx);
        spr.setBounds(x, y, 44, 44);
        puntos           = 30;
        vida             = 2;
        vidaMax          = 2;
        intervaloDisparo = 3.0f; // dispara cada 3 segundos
        chanceDrop       = 20;   // porcentaje de un upgrade de disparo
        this.txBala      = txBala;
    }
 
    // Template Method: movimiento lateral mas una oscilacion
    @Override
    protected void mover() 
    {
        spr.setPosition(spr.getX() + velFormacionX, spr.getY() + velFormacionY);
    }
 
    @Override
    protected void dispararEnemigo(ArrayList<Bullet> listaBalas) 
    {
        if (txBala == null) return;
        float cx = spr.getX() + spr.getWidth() / 2;
        float cy = spr.getY();
        //disparo simple hacia abajo
        EstrategiaDisparo estrategia = new DisparoSimple();
        listaBalas.addAll(estrategia.crear(cx, cy, txBala, true));
    }
 
    @Override
    protected PowerUp.TipoPowerUp seleccionarTipoDrop(Random r) 
    {
        return PowerUp.TipoPowerUp.UPGRADE_DISPARO;
    }
}
}
