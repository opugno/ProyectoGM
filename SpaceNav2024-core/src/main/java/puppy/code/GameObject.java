package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * GM-8: Patrón Template Method
 */
public class GameObject 
{
	protected Sprite spr;
    protected boolean destroyed = false;
    protected float xVel = 0;
    protected float yVel = 0;
 
    // TEMPLATE METHOD — final: nadie lo sobreescribe
    public final void actualizar() 
    {
        mover();              // subclase se mueve distinto (abstracto)
        verificarBordes();    // límites de pantalla 
        efectoAdicional();    // lógica extra opcional (hook vacio)
    }
 

    // Método abstracto — obligatorio en cada subclase
    protected abstract void mover();
 
    // Hooks 
    /**Por defecto: destruye el objeto si sale de pantalla */
    /**Sobreescritura*/
    protected void verificarBordes() 
    {
        if (spr == null) return;
        if (spr.getX() + spr.getWidth() < 0 || spr.getX() > Gdx.graphics.getWidth())
        {
            destroyed = true;
        }
        if (spr.getY() + spr.getHeight() < 0 || spr.getY() > Gdx.graphics.getHeight()) 
        {
            destroyed = true;
        }
    }
 
    /** Por defecto: este no añade nada extra */
    protected void efectoAdicional() { }
 
    //Métodos comunes entre los game objects
    public void dibujar(SpriteBatch batch) 
    {
        if (spr != null && !destroyed) 
        {
            spr.draw(batch);
        }
    }
 
  //Getters y Setters
    public Rectangle getArea() 
    {
        return spr != null ? spr.getBoundingRectangle() : new Rectangle();
    }
 
    public boolean isDestroyed() 
    {
        return destroyed;
    }
 
    public void setDestroyed(boolean destroyed) 
    {
        this.destroyed = destroyed;
    }
 
    public float getX() 
    { 	
    	return spr != null ? spr.getX() : 0; 
    }
    public float getY() 
    { 
    	return spr != null ? spr.getY() : 0; 
    }
    public float getWidth()  
    { 
    	return spr != null ? spr.getWidth()  : 0; 
    }
    public float getHeight() 
    { 
    	return spr != null ? spr.getHeight() : 0; 
    }
}
