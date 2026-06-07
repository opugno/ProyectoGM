package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Bala del juego. Extiende GameObject.
 * Puede ser tanto de un jugador o de un enemigo, y puede ser fuerte.
 */
public class Bullet extends GameObject 
{

	private int xSpeed;
	private int ySpeed;
	//private boolean destroyed = false;
	//private Sprite spr;
	
	private boolean esEnemiga; //true -> dana al jugador, false -> dana al enemigo
	private boolean esFuerte; // true -> hace el doble de dano
	    
    public Bullet(float x, float y, int xSpeed, int ySpeed, Texture tx, boolean esEnemiga) 
    {
    	spr = new Sprite(tx);
    	spr.setPosition(x, y);
    	spr.setSize(8, 16); // tamaño estandar de bala
        this.xSpeed   = xSpeed;
        this.ySpeed   = ySpeed;
        this.esEnemiga = esEnemiga;
        this.esFuerte  = false;
        
    }
 
    //template method, implementa mover
    @Override
    protected void mover() 
    {
        spr.setPosition(spr.getX() + xSpeed, spr.getY() + ySpeed);
    }
 
    //hook, destruye si sale de la pantalla
    @Override
    protected void verificarBordes() 
    {
        float bx = spr.getX();
        float by = spr.getY();
        if (bx < 0 || bx + spr.getWidth() > Gdx.graphics.getWidth()) destroyed = true;
        if (by < 0 || by + spr.getHeight() > Gdx.graphics.getHeight()) destroyed = true;
    }
 
    /** Verifica colision con un enemigo para las balas del jugador. */
    public boolean checkCollisionEnemigo(Enemigo e) 
    {
        if (esEnemiga || destroyed) return false;
        if (spr.getBoundingRectangle().overlaps(e.getArea())) 
        {
            destroyed = true;
            return true;
        }
        return false;
    }
 
    /** Verifica colision con la nave para las balas enemigas. */
    public boolean checkCollisionNave(Nave4 nave) 
    {
        if (!esEnemiga || destroyed) return false;
        if (spr.getBoundingRectangle().overlaps(nave.getArea())) 
        {
            destroyed = true;
            return true;
        }
        return false;
    }
 
    /** compatibilidad con Ball2 */
    public boolean checkCollision(Ball2 b2) 
    {
        if (destroyed) return false;
        if (spr.getBoundingRectangle().overlaps(b2.getArea())) 
        {
            destroyed = true;
            return true;
        }
        return false;
    }
 
    public void draw(SpriteBatch batch)
    {
        dibujar(batch);
    }
 
    public void update() 
    {
        actualizar();
    }
 
    public boolean isDestroyed() { return destroyed; }
    public boolean isEsEnemiga() { return esEnemiga; }
    public boolean isEsFuerte()  { return esFuerte; }
    
    public void setEsFuerte(boolean esFuerte) { this.esFuerte = esFuerte; }
    public int getDano() { return esFuerte ? 2 : 1; }
}
