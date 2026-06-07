package puppy.code;
 
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
 
/**
 * Power-up que cae al destruir un enemigo
 * Extiende de GameObject template method.
 */
public class PowerUp extends GameObject
{
	public enum TipoPowerUp {
        UPGRADE_DISPARO,   // +1 nivel de disparo
        ESCUDO,            // absorbe 1 golpe
        DISPARO_FUERTE,    // balas x2 daño por 10s
        DISPARO_ESPECIAL,  // láser por 8s (drop de boss)
        VIDA_EXTRA         // +1 vida
    }
	
	private TipoPowerUp tipo;
    private static final float VELOCIDAD_CAIDA = -2f;
    
    public PowerUp(float x, float y, TipoPowerUp tipo, Texture tx) {
        this.tipo = tipo;
        spr = new Sprite(tx);
        spr.setPosition(x, y);
        spr.setSize(24, 24);
        yVel = VELOCIDAD_CAIDA;
    }
    
    //Template method: el powerup cae hacia abajo
    @Override
    protected void mover()
    {
        spr.setPosition(spr.getX(), spr.getY() + yVel);
    }
    //Hook, si sale por abajo se destruye
    @Override
    protected void verificarBordes() 
 	{
        if (spr.getY() + spr.getHeight() < 0) destroyed = true;
    }
    
    /**
     * Aplica el efecto del powerupp al GameManager y a la nave
     * @param nave la nave del jugador
     */
    public void aplicar(Nave4 nave) 
    {
        GameManager gm = GameManager.getInstance();
        switch (tipo) 
        {
            case UPGRADE_DISPARO:
                gm.subirNivelDisparo();
                nave.actualizarEstrategiaDisparo();
                break;
            case ESCUDO:
                gm.activarEscudo();
                break;
            case DISPARO_FUERTE:
                gm.activarDisparoFuerte(10f);
                break;
            case DISPARO_ESPECIAL:
                gm.activarDisparoEspecial(8f);
                break;
            case VIDA_EXTRA:
                gm.agregarVida();
                break;
        }
        destroyed = true;
    }
    
    public boolean colisionaConNave(Nave4 nave) 
    {
        return !destroyed && spr.getBoundingRectangle().overlaps(nave.getArea());
    }
 
    public TipoPowerUp getTipo() { return tipo; }
}
