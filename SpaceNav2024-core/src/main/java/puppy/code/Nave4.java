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
 * Física de rotación:
 *   La rotación usa un modelo de aceleración + amortiguación, igual que
 *   el empuje lineal. Mantener LEFT/RIGHT acelera la rotación hasta
 *   maxRotacion, y al soltar la tecla la nave decelera suavemente
 *   gracias a dampRotacion. Esto elimina la sensación de giro brusco
 *   del modelo anterior (velRotacion constante).
 *
 * Física lineal:
 *   Inercia real — la nave sigue moviéndose aunque no se presione UP.
 *   El freno (DOWN) reduce la velocidad. Sin freno, se frena muy poco solo.
 */
public class Nave4 extends GameObject implements Disparable {

    // ─── Rotación — modelo aceleración + amortiguación ────────────
    /** Cuántos grados/frame se suma a vRot al mantener LEFT o RIGHT. */
    private float acelRotacion  = 1.3f;
    /** Factor de frenado pasivo de la rotación (0..1). 1 = sin freno. */
    private float dampRotacion  = 0.84f;
    /** Velocidad de rotación máxima en grados/frame. */
    private float maxRotacion   = 6.0f;
    /** Velocidad de rotación actual (acumulada). */
    private float vRotacion     = 0f;
    /** Ángulo actual de la nave en grados (0 = apunta arriba). */
    private float rotacion      = 0f;

    // ─── Física lineal ────────────────────────────────────────────
    private float aceleracion   = 0.22f;
    private float rozamiento    = 0.985f;
    private float frenadoActivo = 0.90f;
    private float velMaxima     = 5.5f;

    // ─── Estado ──────────────────────────────────────────────────
    private boolean herido          = false;
    private int     tiempoHeridoMax = 60;
    private int     tiempoHerido;

    // ─── Audio / texturas ─────────────────────────────────────────
    private Sound   sonidoHerido;
    private Sound   soundBala;
    private Texture txBala;

    // ─── Strategy ────────────────────────────────────────────────
    private EstrategiaDisparo estrategiaDisparo;

    // ═══════════════════════════════════════════════════════════════
    // CONSTRUCTOR
    // ═══════════════════════════════════════════════════════════════
    public Nave4(int x, int y, Texture tx, Sound soundChoque, Texture txBala, Sound soundBala) {
        sonidoHerido   = soundChoque;
        this.soundBala = soundBala;
        this.txBala    = txBala;

        spr = new Sprite(tx);
        spr.setSize(45, 45);
        spr.setPosition(x, y);
        spr.setOriginCenter();

        actualizarEstrategiaDisparo();
    }

    // ═══════════════════════════════════════════════════════════════
    // TEMPLATE METHOD: mover()
    // ═══════════════════════════════════════════════════════════════
    @Override
    protected void mover() {
        if (herido) return;

        // ── Rotación con aceleración + amortiguación ──────────────
        //
        // En lugar de sumar/restar velRotacion fija directamente a
        // rotacion, ahora acumulamos en vRotacion y la amortiguamos
        // cada frame. El resultado: girar acelera suavemente, y
        // soltar la tecla desacelera en lugar de parar en seco.
        //
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  vRotacion += acelRotacion;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) vRotacion -= acelRotacion;

        // Amortiguación: reduce vRotacion cada frame aunque se siga
        // presionando (limita la velocidad máxima orgánicamente).
        vRotacion *= dampRotacion;

        // Clampear por si dampRotacion deja un margen pequeño
        vRotacion = MathUtils.clamp(vRotacion, -maxRotacion, maxRotacion);

        // Aplicar al ángulo acumulado
        rotacion += vRotacion;

        // ── Empuje lineal ─────────────────────────────────────────
        // Se suma 90° porque LibGDX setRotation() parte del eje X
        // (derecha = 0°) pero el sprite apunta hacia arriba visualmente.
        // Sin el offset, UP empuja a la derecha cuando la nave está
        // sin rotar. Con +90° el empuje coincide con la punta del sprite.
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            float rad = MathUtils.degreesToRadians * (rotacion + 90f);
            xVel +=  MathUtils.cos(rad) * aceleracion;
            yVel +=  MathUtils.sin(rad) * aceleracion;
        }

        // ── Freno activo ──────────────────────────────────────────
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            xVel *= frenadoActivo;
            yVel *= frenadoActivo;
        }

        // ── Rozamiento pasivo ─────────────────────────────────────
        xVel *= rozamiento;
        yVel *= rozamiento;

        // ── Limitar velocidad lineal máxima ───────────────────────
        float speed = (float) Math.sqrt(xVel * xVel + yVel * yVel);
        if (speed > velMaxima) {
            float factor = velMaxima / speed;
            xVel *= factor;
            yVel *= factor;
        }

        // ── Mover sprite ──────────────────────────────────────────
        float nx = spr.getX() + xVel;
        float ny = spr.getY() + yVel;

        // Wrap-around: sale por un borde, aparece por el opuesto
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        if (nx + spr.getWidth()  < 0) nx =  w;
        if (nx > w)                   nx = -spr.getWidth();
        if (ny + spr.getHeight() < 0) ny =  h;
        if (ny > h)                   ny = -spr.getHeight();

        spr.setPosition(nx, ny);
        spr.setRotation(rotacion);
    }

    // ═══ Hook: wrap-around manejado en mover(), no destruir ═══════
    @Override
    protected void verificarBordes() { /* wrap en mover() */ }

    // ═══ Hook: temblor al recibir daño ════════════════════════════
    @Override
    protected void efectoAdicional() {
        if (herido) {
            spr.setX(spr.getX() + MathUtils.random(-2, 2));
            tiempoHerido--;
            if (tiempoHerido <= 0) herido = false;
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // DRAW — Template Method + disparo
    // ═══════════════════════════════════════════════════════════════
    public void draw(SpriteBatch batch, PantallaJuego juego) {
        actualizar(); // mover → verificarBordes → efectoAdicional
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

    // ═══════════════════════════════════════════════════════════════
    // DISPARABLE — Strategy Pattern
    // ═══════════════════════════════════════════════════════════════
    @Override
    public void disparar(ArrayList<Bullet> listaBalas) {
        if (txBala == null) return;

        // Mismo offset +90° que el empuje para que las balas salgan
        // por la punta visual del sprite.
        float rad    = MathUtils.degreesToRadians * (rotacion + 90f);
        float cx     = spr.getX() + spr.getWidth()  / 2f;
        float cy     = spr.getY() + spr.getHeight() / 2f;
        float spawnX = cx + MathUtils.cos(rad) * (spr.getHeight() / 2f - 2f);
        float spawnY = cy + MathUtils.sin(rad) * (spr.getHeight() / 2f - 2f);

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

    // ═══════════════════════════════════════════════════════════════
    // COLISIONES
    // ═══════════════════════════════════════════════════════════════
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

    // ═══════════════════════════════════════════════════════════════
    // GETTERS
    // ═══════════════════════════════════════════════════════════════
    public boolean estaDestruido() { return !herido && destroyed; }
    public boolean estaHerido()    { return herido; }
    public int     getVidas()      { return GameManager.getInstance().getVidas(); }
    public void    setVidas(int v) { /* legacy */ }
    public float   getRotacion()   { return rotacion; }
    public Rectangle getArea()     { return spr.getBoundingRectangle(); }

    // ── Getters/setters de física de rotación (para tuning en runtime) ──
    public float getAcelRotacion()          { return acelRotacion; }
    public void  setAcelRotacion(float v)   { acelRotacion = v; }
    public float getDampRotacion()          { return dampRotacion; }
    public void  setDampRotacion(float v)   { dampRotacion = v; }
    public float getMaxRotacion()           { return maxRotacion; }
    public void  setMaxRotacion(float v)    { maxRotacion = v; }
}