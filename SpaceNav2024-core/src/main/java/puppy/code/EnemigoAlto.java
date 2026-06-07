package puppy.code;
 
import java.util.ArrayList;
import java.util.Random;
 
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
 
/**
 * Enemigo alto: 100 puntos, 4 vidas, movimiento sinusoidal, dispara triple.
 * Drop: Escudo (25%) o Disparo Fuerte (15%).
 * Implementa Disparable, usa EstrategiaDisparo por composicion.
 */
public class EnemigoAlto extends Enemigo implements Disparable 
{
 
    private float tiempoSeno = 0f;       // para movimiento sinusoidal
    private float amplitudSeno = 30f;    // amplitud de oscilación
    private float frecuenciaSeno = 2f;   // frecuencia de oscilación
    private float baseX;                 // posición X base de la formación
    private EstrategiaDisparo estrategiaDisparo;
 
    public EnemigoAlto(float x, float y, Texture tx, Texture txBala) 
    {
        spr              = new Sprite(tx);
        spr.setBounds(x, y, 50, 50);
        puntos           = 100;
        vida             = 4;
        vidaMax          = 4;
        intervaloDisparo = 2.0f; // dispara cada 2 segundos
        chanceDrop       = 40;   // procentaje escudo + porcentaje disparo fuerte
        this.txBala      = txBala;
        this.baseX       = x;
        estrategiaDisparo = new DisparoTriple(); // usa triple por defecto
    }
 
    //Template Method: movimiento sinusoidal sobre la base de la formacion
    @Override
    protected void mover() 
    {
        tiempoSeno += 0.05f;
        baseX += velFormacionX;
        float newX = baseX + (float)(amplitudSeno * Math.sin(tiempoSeno * frecuenciaSeno));
        spr.setPosition(newX, spr.getY() + velFormacionY);
    }
 
    @Override
    protected void dispararEnemigo(ArrayList<Bullet> listaBalas) 
    {
        if (txBala == null) return;
        float cx = spr.getX() + spr.getWidth() / 2;
        float cy = spr.getY();
        listaBalas.addAll(estrategiaDisparo.crear(cx, cy, txBala, true));
    }
 
    // Implementa DISPARABLE
    @Override
    public void disparar(ArrayList<Bullet> listaBalas) 
    {
        dispararEnemigo(listaBalas);
    }
 
    @Override
    public void setEstrategiaDisparo(EstrategiaDisparo estrategia) 
    {
        this.estrategiaDisparo = estrategia;
    }
 
    @Override
    protected PowerUp.TipoPowerUp seleccionarTipoDrop(Random r) 
    {
        int roll = r.nextInt(100);
        if (roll < 25) return PowerUp.TipoPowerUp.ESCUDO;
        if (roll < 40) return PowerUp.TipoPowerUp.DISPARO_FUERTE;
        return null; // No dropeo nada en el resto del 40%
    }
 
    @Override
    public void setVelFormacion(float vx, float vy) 
    {
        super.setVelFormacion(vx, vy);
        baseX = spr.getX(); // resetear base al cambiar dirección
    }
}