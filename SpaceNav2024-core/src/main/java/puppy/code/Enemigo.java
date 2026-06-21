package puppy.code;
import java.util.ArrayList;
import java.util.Random;
 
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
 
/**
 * Clase abstracta para todos los enemigos
 * Extiende de GameObject (Template Method)
 * Las subclases definen el comportamiento, puntos y probabilidad de drop
 */
public abstract class Enemigo  extends GameObject
{
	//ATRIBUTOS
	protected int puntos;
    protected int vida;
    protected int vidaMax;
    protected float velFormacionX = 0; // velocidad lateral de la formación
    protected float velFormacionY = 0; // velocidad vertical de la formación
    protected float tiempoDisparoAcum = 0f;
    protected float intervaloDisparo;  // segundos entre disparos (0 = no dispara)
    protected Texture txBala;
    protected int chanceDrop;          // % de probabilidad de drop (0-100)
    
    //Template method: mover() es abstracto
    @Override
    protected abstract void mover();
    
    //Hook: comportamiento extra 
    @Override
    protected void efectoAdicional() { }
    
    /**
     * Recibe daño, retorna true si el enemigo fue destruido
     * @param dano puntos de daño a recibir
     */
    public boolean recibirDano(int dano) 
    {
        vida -= dano;
        return vida <= 0;
    }
    
    /**
     * Actualiza el temporizador de disparo.
     * @param delta tiempo desde el ultimo frame
     * @param listaBalas lista donde se agregan las balas generadas
     */
    public void actualizarDisparo(float delta, ArrayList<Bullet> listaBalas) 
    {
        if (intervaloDisparo <= 0 || txBala == null) return;
        tiempoDisparoAcum += delta;
        if (tiempoDisparoAcum >= intervaloDisparo) 
        {
            tiempoDisparoAcum = 0f;
            dispararEnemigo(listaBalas);
        }
    }
    
    // En Enemigo.java - cambiar actualizarDisparo
    public void acumularTiempoDisparo(float delta) {
        if (intervaloDisparo <= 0) return;
        tiempoDisparoAcum += delta;
    }
    
    public boolean debeDisparar() {
        return intervaloDisparo > 0 && tiempoDisparoAcum >= intervaloDisparo;
    }
    
    public void resetTiempoDisparo() {
        tiempoDisparoAcum = 0f;
    }
    
    /** Las subclases que disparan sobreescriben */
    protected void dispararEnemigo(ArrayList<Bullet> listaBalas) { }
    
    /**
     * Genera un powerUp al morir de acuerdo a la probabilidad del tipo de enemigo
     * @return PowerUp o null si no se dropeo nada
     */
    public PowerUp generarDrop(Texture[] txPowerUps) 
    {
        if (txPowerUps == null || txPowerUps.length == 0) return null;
        Random r = new Random();
        if (r.nextInt(100) >= chanceDrop) return null;
        // selecciona el tipo de powerup
        PowerUp.TipoPowerUp tipo = seleccionarTipoDrop(r);
        if (tipo == null) return null;
        // usa una textura generica del arreglo segun el tipo
        int idx = tipo.ordinal() % txPowerUps.length;
        return new PowerUp(spr.getX() + spr.getWidth()/2, spr.getY(), tipo, txPowerUps[idx]);
    }
    
    /** Cada subclase define los tipos de powerup q puede dropear. */
    protected abstract PowerUp.TipoPowerUp seleccionarTipoDrop(Random r);
 
    // setters para mover la formación entera
    public void setVelFormacion(float vx, float vy) {
        velFormacionX = vx;
        velFormacionY = vy;
    }
 
    // dibujar con estado de daño (parpadeo opcional)
    @Override
    public void dibujar(SpriteBatch batch) 
    {
        if (!destroyed) spr.draw(batch);
    }
 
    public int getPuntos()   { return puntos; }
    public int getVida()     { return vida; }
    public int getVidaMax()  { return vidaMax; }
    public void setTxBala(Texture tx) { this.txBala = tx; }
}
