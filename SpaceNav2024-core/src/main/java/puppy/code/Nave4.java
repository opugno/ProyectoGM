package puppy.code;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * Nave del jugador
 * Extiende GameObject (template method) e implementa Disparable (strategy)
 * Usa GameManager para vidas, escudo y nivel de disparo (singleton).
 */
public class Nave4 extends GameObject implements Disparable
{
	
	//private boolean destruida = false;
    //private int vidas = 3;
    //private float xVel = 0;
    //private float yVel = 0;
    //private Sprite spr;
    private Sound sonidoHerido;
    private Sound soundBala;
    private Texture txBala;
    private boolean herido = false;
    private int tiempoHeridoMax=50;
    private int tiempoHerido;
    
    private EstrategiaDisparo estrategiaDisparo; // Strategy actual
    
    //CONSTRUCTOR
    public Nave4(int x, int y, Texture tx, Sound soundChoque, Texture txBala, Sound soundBala) 
    {
        sonidoHerido = soundChoque;
        this.soundBala = soundBala;
        this.txBala = txBala;
        spr = new Sprite(tx);
        spr.setBounds(x, y, 45, 45);
 
        //Ver nivel de disparo del GameManager y aplicar la estrategia
        actualizarEstrategiaDisparo();
    }
    
    //TEMPLATE METHOD
    @Override
    protected void mover() 
    {
        if (herido) return; // no acepta input mientras estA herido
 
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))  xVel--;
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) xVel++;
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN))  yVel--;
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP))    yVel++;
 
        //pa mantener dentro de pantalla (rebota)
        float nx = spr.getX() + xVel;
        float ny = spr.getY() + yVel;
        if (nx < 0 || nx + spr.getWidth() > Gdx.graphics.getWidth()) xVel *= -1;
        if (ny < 0 || ny + spr.getHeight() > Gdx.graphics.getHeight()) yVel *= -1;
 
        spr.setPosition(spr.getX() + xVel, spr.getY() + yVel);
    }
    
    //HOOK, verifica bordes, la nave no se destruye si no que rebota
    @Override
    protected void verificarBordes() 
    {
        //la logica de rebote la definimos en mover
    }
    
    //HOOK, efecto de temblor cuando esta herida
    @Override
    protected void efectoAdicional() 
    {
        if (herido) {
            spr.setX(spr.getX() + MathUtils.random(-2, 2));
            tiempoHerido--;
            if (tiempoHerido <= 0) herido = false;
        }
    }
    
    //DIBUJO, override para dispara tmb
    public void draw(SpriteBatch batch, PantallaJuego juego)
    {
    	actualizar(); // se ejecuta Template Method
        spr.draw(batch);
 
        // disparo con SPACE (Strategy)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) 
        {
            disparar(juego.getListaBalas());
            soundBala.play();
        }
 
        // activar barra de poder con E
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) 
        {
            if (GameManager.getInstance().activarBarraPoder()) 
            {
                // Rafaga, se dispara 3 veces seguidas
                disparar(juego.getListaBalas());
                disparar(juego.getListaBalas());
                disparar(juego.getListaBalas());
            }
        } 
    }
    
    //DISPARABLE INTERFACE, patron Strategy
    @Override
    public void disparar(ArrayList<Bullet> listaBalas) 
    {
        if (txBala == null) return;
        float cx = spr.getX() + spr.getWidth() / 2 - 4;
        float cy = spr.getY() + spr.getHeight() - 5;
        ArrayList<Bullet> nuevas = estrategiaDisparo.crear(cx, cy, txBala, false);
        // si disparo fuerte esta activo se marcan las balas
        if (GameManager.getInstance().isDisparoFuerteActivo()) {
            for (Bullet b : nuevas) b.setEsFuerte(true);
        }
        listaBalas.addAll(nuevas);
    }
 
    @Override
    public void setEstrategiaDisparo(EstrategiaDisparo estrategia) 
    {
        this.estrategiaDisparo = estrategia;
    }
    
    /**
     * Se lee el nivel de disparo del GameManager y se asigna la estrategia correspondiente
     * Llamar despues de recoger un powerup de upgrade.
     */
    public void actualizarEstrategiaDisparo() 
    {
        int nivel = GameManager.getInstance().getNivelDisparo();
        switch (nivel) 
        {
            case 1: estrategiaDisparo = new DisparoSimple();    break;
            case 2: estrategiaDisparo = new DisparoDoble();     break;
            case 3: estrategiaDisparo = new DisparoTriple();    break;
            case 4: estrategiaDisparo = new DisparoCuadruple(); break;
            case 5: estrategiaDisparo = new DisparoQuintuple(); break;
            case 6: estrategiaDisparo = new DisparoSextuple();  break;
            default: estrategiaDisparo = new DisparoSimple();   break;
        }
    }
    
    //COLISIONES
    
    /** Colision con un enemigo (Enemigo). Retorna true si perdio vida. */
    public boolean checkCollisionEnemigo(Enemigo e) 
    {
    	if (herido || e.isDestroyed()) return false;
        if (e.getArea().overlaps(spr.getBoundingRectangle())) 
        {
            sufrirDano();
            return true;
        }
        return false;
    }
    
    /** Colision con una bala enemiga. */
    public boolean checkCollisionBala(Bullet bala) 
    {
        if (herido || bala.isDestroyed() || !bala.isEsEnemiga()) return false;
        if (bala.getArea().overlaps(spr.getBoundingRectangle())) 
        {
            sufrirDano();
            bala.setDestroyed(true);
            return true;
        }
        return false;
    }
 
    /** Colision con Ball2. */
    public boolean checkCollision(Ball2 b) 
    {
        if (herido) return false;
        if (b.getArea().overlaps(spr.getBoundingRectangle())) 
        {
            sufrirDano();
            return true;
        }
        return false;
    }
 
    private void sufrirDano() {
        boolean muerta = GameManager.getInstance().recibirDano();
        herido      = true;
        tiempoHerido = tiempoHeridoMax;
        sonidoHerido.play();
        if (muerta) destroyed = true;
    }
    
    
    //ESTADO
    
    public boolean estaDestruido() 
    {
       return !herido && destroyed;
    }
    public boolean estaHerido() 
    {
 	   return herido;
    }
    
    public int getVidas() {return GameManager.getInstance().getVidas();}
    //public boolean isDestruida() {return destruida;}
    //public int getX() {return (int) spr.getX();}
    //public int getY() {return (int) spr.getY();}
	public void setVidas(int v) {/* legacy — ahora lo maneja GameManager */}
	public Rectangle getArea() {return spr.getBoundingRectangle();}
}
