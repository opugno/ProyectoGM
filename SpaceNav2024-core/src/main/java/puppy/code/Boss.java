package puppy.code;
 
import java.util.ArrayList;
import java.util.Random;
 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
 
/**
 * Boss: 500 puntos base, 30 vidas, se mueve lateralmente arriba,
 * usa varias estrategias de disparo en fases
 * Implementa Disparable y extiende de Enemigo.
 */
public class Boss extends Enemigo implements Disparable
{
	private float baseX;
	private boolean moviendoDerecha = true;
	private float limiteIzq;
	private float limiteDer;
	
    // Fases de ataque
    private EstrategiaDisparo estrategiaActual;
    private int faseAtaque = 0; // cambia segun la vida restante
 
    // Barra de vida visual
    private ShapeRenderer shapeRenderer;
    
    public Boss(float x, float y, int vidaTotal, Texture tx, Texture txBala) 
    {
        spr           = new Sprite(tx);
        spr.setBounds(x, y, 100, 80);
        puntos        = 500;
        vida          = vidaTotal;
        vidaMax       = vidaTotal;
        intervaloDisparo = 1.5f; // dispara cada 1.5s
        chanceDrop    = 100;     // siempre dropea algo al morir
        this.txBala   = txBala;
        this.baseX    = x;
        limiteIzq     = 50f;
        limiteDer     = Gdx.graphics.getWidth() - 150f;
        estrategiaActual  = new DisparoDoble();
        shapeRenderer = new ShapeRenderer();
    }
    
    //Template Method, movimiento lateral, rebota en los bordes
    @Override
    protected void mover() 
    {
        float speed = 2f;
        if (moviendoDerecha) 
        {
            spr.setX(spr.getX() + speed);
            if (spr.getX() >= limiteDer) moviendoDerecha = false;
        } else 
        {
            spr.setX(spr.getX() - speed);
            if (spr.getX() <= limiteIzq) moviendoDerecha = true;
        }
        // El boss no baja, se mantiene en Y fija
    }
    
    //Hook, cambia estrategia de disparo segunn la vida restante (fases)
    @Override
    protected void efectoAdicional() 
    {
        float porcentajeVida = (float) vida / vidaMax;
        if (porcentajeVida > 0.66f && faseAtaque != 0) 
        {
            faseAtaque = 0;
            estrategiaActual = new DisparoDoble();
        } 
        else if (porcentajeVida > 0.33f && porcentajeVida <= 0.66f && faseAtaque != 1) 
        {
            faseAtaque = 1;
            estrategiaActual = new DisparoTriple();
        } 
        else if (porcentajeVida <= 0.33f && faseAtaque != 2) 
        {
            faseAtaque = 2;
            estrategiaActual = new DisparoCircular(); // fase critica -> disparo circular
        }
    }
    
    @Override
    protected void dispararEnemigo(ArrayList<Bullet> listaBalas) 
    {
        if (txBala == null) return;
        float cx = spr.getX() + spr.getWidth() / 2;
        float cy = spr.getY();
        listaBalas.addAll(estrategiaActual.crear(cx, cy, cy, txBala, true));
    }
    
    //Implementa disparable
    @Override
    public void disparar(ArrayList<Bullet> listaBalas) 
    {
        dispararEnemigo(listaBalas);
    }
 
    @Override
    public void setEstrategiaDisparo(EstrategiaDisparo estrategia) 
    {
        this.estrategiaActual = estrategia;
    }
    
    /** Dibuja la barra de vida encima del boss. */
    public void dibujarBarraVida(SpriteBatch batch) 
    {
        if (destroyed) return;
        batch.end();
 
        float barWidth  = 200f;
        float barHeight = 14f;
        float barX      = Gdx.graphics.getWidth() / 2f - barWidth / 2f;
        float barY      = Gdx.graphics.getHeight() - 30f;
        float filled    = barWidth * ((float) vida / vidaMax);
 
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
 
        //fondo rojo oscuro
        shapeRenderer.setColor(0.5f, 0f, 0f, 1f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
 
        //vida restante, el color varia de acuerdo a la fase
        Color c = faseAtaque == 0 ? Color.GREEN : (faseAtaque == 1 ? Color.YELLOW : Color.RED);
        shapeRenderer.setColor(c);
        shapeRenderer.rect(barX, barY, filled, barHeight);
 
        shapeRenderer.end();
        batch.begin();
    }
    
    @Override
    protected PowerUp.TipoPowerUp seleccionarTipoDrop(Random r) 
    {
        return PowerUp.TipoPowerUp.DISPARO_ESPECIAL; // siempre dropea un laser especial
    }
 
    public void dispose() 
    {
        shapeRenderer.dispose();
    }
}
