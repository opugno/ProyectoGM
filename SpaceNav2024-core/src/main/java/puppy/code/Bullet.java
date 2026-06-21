package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Bala del juego. Extiende GameObject (Template Method).
 * Usa float para velocidad — permite disparar en cualquier ángulo.
 */
public class Bullet extends GameObject {

    // float en lugar de int para poder tener velocidades diagonales precisas
    private float xSpeed;
    private float ySpeed;
    private boolean esEnemiga;
    private boolean esFuerte;

    public Bullet(float x, float y, float xSpeed, float ySpeed, Texture tx, boolean esEnemiga) {
        spr = new Sprite(tx);
        spr.setPosition(x, y);
        spr.setSize(8, 16);
        this.xSpeed    = xSpeed;
        this.ySpeed    = ySpeed;
        this.esEnemiga = esEnemiga;
        this.esFuerte  = false;
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
        if (bx < 0 || bx + spr.getWidth()  > Gdx.graphics.getWidth())  destroyed = true;
        if (by < 0 || by + spr.getHeight() > Gdx.graphics.getHeight()) destroyed = true;
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

    /** Compatibilidad con Ball2 original. */
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
    public void setEsFuerte(boolean f) { this.esFuerte = f; }
    public int getDano() { return esFuerte ? 2 : 1; }
}