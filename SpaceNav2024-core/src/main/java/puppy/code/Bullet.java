package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Bala del juego. Extiende GameObject (Template Method).
 *
 * En lugar de depender de una textura externa (que puede ser invisible
 * si el PNG es grande o transparente), genera su propia textura de color
 * sólido en memoria. Azul brillante para el jugador, rojo para enemigos.
 */
public class Bullet extends GameObject {

    private float xSpeed;
    private float ySpeed;
    private boolean esEnemiga;
    private boolean esFuerte;

    // Textura generada en memoria — siempre visible
    private static Texture txAzul;   // bala jugador
    private static Texture txRoja;   // bala enemiga
    private static Texture txDorada; // bala fuerte

    /** Inicializa las texturas de color sólido (llamar una sola vez). */
    public static void inicializarTexturas() {
        txAzul   = crearTexturaSolida(6, 14, new Color(0.3f, 0.7f, 1.0f, 1f));
        txRoja   = crearTexturaSolida(6, 14, new Color(1.0f, 0.3f, 0.2f, 1f));
        txDorada = crearTexturaSolida(8, 16, new Color(1.0f, 0.9f, 0.0f, 1f));
    }

    private static Texture crearTexturaSolida(int w, int h, Color color) {
        Pixmap pm = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pm.setColor(color);
        pm.fill();
        // Borde un poco más oscuro para que tenga forma
        pm.setColor(color.r * 0.5f, color.g * 0.5f, color.b * 0.5f, 1f);
        pm.drawRectangle(0, 0, w, h);
        Texture tx = new Texture(pm);
        pm.dispose();
        return tx;
    }

    public static void liberarTexturas() {
        if (txAzul   != null) { txAzul.dispose();   txAzul   = null; }
        if (txRoja   != null) { txRoja.dispose();   txRoja   = null; }
        if (txDorada != null) { txDorada.dispose(); txDorada = null; }
    }

    // ── Constructor ───────────────────────────────────────────────
    public Bullet(float x, float y, float xSpeed, float ySpeed,
                  Texture txIgnorada, boolean esEnemiga) {

        // Inicializar texturas si aún no se hizo
        if (txAzul == null) inicializarTexturas();

        this.xSpeed    = xSpeed;
        this.ySpeed    = ySpeed;
        this.esEnemiga = esEnemiga;
        this.esFuerte  = false;

        // Elegir textura según quién dispara
        Texture tx = esEnemiga ? txRoja : txAzul;

        spr = new Sprite(tx);
        spr.setSize(tx.getWidth(), tx.getHeight());
        spr.setOriginCenter();

        // Centrar en el punto de spawn
        spr.setPosition(x - spr.getWidth() / 2f, y - spr.getHeight() / 2f);

        // Rotar para que apunte en la dirección de movimiento
        float angulo = (float) Math.toDegrees(Math.atan2(xSpeed, ySpeed));
        spr.setRotation(angulo);
    }

    // ═══ Template Method: mover() ═══
    @Override
    protected void mover() {
        spr.setPosition(spr.getX() + xSpeed, spr.getY() + ySpeed);
    }

    // ═══ Hook: destruye si sale de pantalla ═══
    @Override
    protected void verificarBordes() {
        float bx = spr.getX();
        float by = spr.getY();
        if (bx + spr.getWidth()  < 0 || bx > Gdx.graphics.getWidth())  destroyed = true;
        if (by + spr.getHeight() < 0 || by > Gdx.graphics.getHeight()) destroyed = true;
    }

    // ═══ Override dibujar para aplicar color fuerte ═══
    @Override
    public void dibujar(SpriteBatch batch) {
        if (destroyed || spr == null) return;
        if (esFuerte) {
            // Cambiar a textura dorada si es disparo fuerte
            spr.setTexture(txDorada);
            spr.setSize(txDorada.getWidth(), txDorada.getHeight());
        }
        spr.draw(batch);
    }

    /** Colisión con un enemigo (balas del jugador). */
    public boolean checkCollisionEnemigo(Enemigo e) {
        if (esEnemiga || destroyed) return false;
        if (spr.getBoundingRectangle().overlaps(e.getArea())) {
            destroyed = true;
            return true;
        }
        return false;
    }

    /** Colisión con la nave (balas enemigas). */
    public boolean checkCollisionNave(Nave4 nave) {
        if (!esEnemiga || destroyed) return false;
        if (spr.getBoundingRectangle().overlaps(nave.getArea())) {
            destroyed = true;
            return true;
        }
        return false;
    }

    public boolean checkCollision(Ball2 b2) {
        if (destroyed) return false;
        if (spr.getBoundingRectangle().overlaps(b2.getArea())) {
            destroyed = true;
            return true;
        }
        return false;
    }

    public void draw(SpriteBatch batch) { dibujar(batch); }
    public void update()               { actualizar(); }

    public boolean isDestroyed() { return destroyed; }
    public boolean isEsEnemiga() { return esEnemiga; }
    public boolean isEsFuerte()  { return esFuerte; }
    public void setEsFuerte(boolean f) {
        this.esFuerte = f;
    }
    public int getDano() { return esFuerte ? 2 : 1; }
}