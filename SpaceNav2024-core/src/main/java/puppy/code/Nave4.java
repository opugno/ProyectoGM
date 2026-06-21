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
 * Nave del jugador — movimiento libre 360° estilo Asteroids.
 *
 * Controles:
 *   LEFT / RIGHT  → rotar la nave
 *   UP            → empuje en la dirección que apunta (aceleración)
 *   DOWN          → freno (reduce velocidad gradualmente)
 *   SPACE         → disparar en la dirección que apunta
 *   E             → activar barra de poder
 *
 * Física: inercia real — la nave sigue moviéndose aunque no se presione UP.
 * El freno (DOWN) reduce la velocidad. Sin freno, se frena muy poco solo.
 */
public class Nave4 extends GameObject implements Disparable {

    // ─── Rotación y física ───────────────────────────────────
    private float rotacion     = 0f;    // grados; 0 = apunta arriba
    private float velRotacion  = 3.5f;  // grados por frame al girar
    private float aceleracion  = 0.22f; // empuje por frame
    private float rozamiento   = 0.985f;// factor de frenado pasivo (1 = sin freno)
    private float frenadoActivo= 0.90f; // factor al mantener DOWN
    private float velMaxima    = 5.5f;  // píxeles/frame máximos

    // ─── Estado ─────────────────────────────────────────────
    private boolean herido       = false;
    private int     tiempoHeridoMax = 60;
    private int     tiempoHerido;

    // ─── Audio / texturas ────────────────────────────────────
    private Sound sonidoHerido;
    private Sound soundBala;
    private Texture txBala;

    // ─── Strategy ────────────────────────────────────────────
    private EstrategiaDisparo estrategiaDisparo;

    // ═══════════════════════════════════════════════
    // CONSTRUCTOR
    // ═══════════════════════════════════════════════
    public Nave4(int x, int y, Texture tx, Sound soundChoque, Texture txBala, Sound soundBala) {
        sonidoHerido   = soundChoque;
        this.soundBala = soundBala;
        this.txBala    = txBala;

        spr = new Sprite(tx);
        // setOriginCenter para que rote sobre su centro
        spr.setSize(45, 45);
        spr.setPosition(x, y);
        spr.setOriginCenter();

        actualizarEstrategiaDisparo();
    }

    // ═══════════════════════════════════════════════
    // TEMPLATE METHOD: mover()
    // ═══════════════════════════════════════════════
    @Override
    protected void mover() {
        if (herido) return;

        // ── Rotación ──────────────────────────
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  rotacion += velRotacion;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) rotacion -= velRotacion;

        // ── Empuje hacia donde apunta ─────────
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            float rad = MathUtils.degreesToRadians * rotacion;
            xVel +=  MathUtils.sin(rad) * aceleracion;
            yVel +=  MathUtils.cos(rad) * aceleracion;
        }

        // ── Freno activo ──────────────────────
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            xVel *= frenadoActivo;
            yVel *= frenadoActivo;
        }

        // ── Rozamiento pasivo (inercia suave) ─
        xVel *= rozamiento;
        yVel *= rozamiento;

        // ── Limitar velocidad máxima ──────────
        float speed = (float) Math.sqrt(xVel * xVel + yVel * yVel);
        if (speed > velMaxima) {
            float factor = velMaxima / speed;
            xVel *= factor;
            yVel *= factor;
        }

        // ── Mover el sprite ───────────────────
        float nx = spr.getX() + xVel;
        float ny = spr.getY() + yVel;

        // Wrap-around: sale por un borde, aparece por el opuesto
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        if (nx + spr.getWidth()  < 0) nx = w;
        if (nx > w)                   nx = -spr.getWidth();
        if (ny + spr.getHeight() < 0) ny = h;
        if (ny > h)                   ny = -spr.getHeight();

        spr.setPosition(nx, ny);

        // Aplicar rotación visual
        spr.setRotation(rotacion);
    }

    // ═══ Hook: no destruye al salir de pantalla — hace wrap ═══
    @Override
    protected void verificarBordes() { /* manejado en mover() con wrap */ }

    // ═══ Hook: temblor al recibir daño ═══
    @Override
    protected void efectoAdicional() {
        if (herido) {
            spr.setX(spr.getX() + MathUtils.random(-2, 2));
            tiempoHerido--;
            if (tiempoHerido <= 0) herido = false;
        }
    }

    // ═══════════════════════════════════════════════
    // DRAW — ejecuta Template Method + disparo
    // ═══════════════════════════════════════════════
    public void draw(SpriteBatch batch, PantallaJuego juego) {
        actualizar(); // Template Method: mover → verificarBordes → efectoAdicional
        spr.draw(batch);

        // Disparo con SPACE
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            disparar(juego.getListaBalas());
            soundBala.play();
        }

        // Barra de poder con E
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (GameManager.getInstance().activarBarraPoder()) {
                disparar(juego.getListaBalas());
                disparar(juego.getListaBalas());
                disparar(juego.getListaBalas());
            }
        }
    }

    // ═══════════════════════════════════════════════
    // DISPARABLE — Strategy Pattern
    // ═══════════════════════════════════════════════
    @Override
    public void disparar(ArrayList<Bullet> listaBalas) {
        if (txBala == null) return;

        // Punto de spawn: punta del sprite en la dirección que apunta
        float rad = MathUtils.degreesToRadians * rotacion;
        float cx  = spr.getX() + spr.getWidth()  / 2f;
        float cy  = spr.getY() + spr.getHeight() / 2f;
        // Desplazar hacia la punta de la nave (la mitad del alto en esa dirección)
        float spawnX = cx + MathUtils.sin(rad) * (spr.getHeight() / 2f - 2f);
        float spawnY = cy + MathUtils.cos(rad) * (spr.getHeight() / 2f - 2f);

        ArrayList<Bullet> nuevas = estrategiaDisparo.crear(spawnX, spawnY, rotacion, txBala, false);

        if (GameManager.getInstance().isDisparoFuerteActivo()) {
            for (Bullet b : nuevas) b.setEsFuerte(true);
        }
        listaBalas.addAll(nuevas);
    }

    @Override
    public void setEstrategiaDisparo(EstrategiaDisparo estrategia) {
        this.estrategiaDisparo = estrategia;
    }

    /** Sincroniza la estrategia con el nivel de disparo del GameManager. */
    public void actualizarEstrategiaDisparo() {
        int nivel = GameManager.getInstance().getNivelDisparo();
        switch (nivel) {
            case 1:  estrategiaDisparo = new DisparoSimple();    break;
            case 2:  estrategiaDisparo = new DisparoDoble();     break;
            case 3:  estrategiaDisparo = new DisparoTriple();    break;
            case 4:  estrategiaDisparo = new DisparoCuadruple(); break;
            case 5:  estrategiaDisparo = new DisparoQuintuple(); break;
            case 6:  estrategiaDisparo = new DisparoSextuple();  break;
            default: estrategiaDisparo = new DisparoSimple();    break;
        }
    }

    // ═══════════════════════════════════════════════
    // COLISIONES
    // ═══════════════════════════════════════════════

    public boolean checkCollisionEnemigo(Enemigo e) {
        if (herido || e.isDestroyed()) return false;
        if (e.getArea().overlaps(spr.getBoundingRectangle())) {
            sufrirDano();
            return true;
        }
        return false;
    }

    public boolean checkCollisionBala(Bullet bala) {
        if (herido || bala.isDestroyed() || !bala.isEsEnemiga()) return false;
        if (bala.getArea().overlaps(spr.getBoundingRectangle())) {
            sufrirDano();
            bala.setDestroyed(true);
            return true;
        }
        return false;
    }

    /** Compatibilidad con Ball2 original. */
    public boolean checkCollision(Ball2 b) {
        if (herido) return false;
        if (b.getArea().overlaps(spr.getBoundingRectangle())) {
            sufrirDano();
            return true;
        }
        return false;
    }

    private void sufrirDano() {
        boolean muerta = GameManager.getInstance().recibirDano();
        herido       = true;
        tiempoHerido = tiempoHeridoMax;
        sonidoHerido.play();
        if (muerta) destroyed = true;
    }

    // ═══════════════════════════════════════════════
    // GETTERS
    // ═══════════════════════════════════════════════
    public boolean estaDestruido() { return !herido && destroyed; }
    public boolean estaHerido()    { return herido; }
    public int     getVidas()      { return GameManager.getInstance().getVidas(); }
    public void    setVidas(int v) { /* legacy */ }
    public float   getRotacion()   { return rotacion; }
    public Rectangle getArea()     { return spr.getBoundingRectangle(); }
}