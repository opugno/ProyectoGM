package puppy.code;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

/** 1 bala en la dirección que apunta la nave. */
public class DisparoSimple implements EstrategiaDisparo {
    @Override
    public ArrayList<Bullet> crear(float x, float y, float angulo, Texture txBala, boolean esEnemiga) {
        ArrayList<Bullet> balas = new ArrayList<>();
        float rad = MathUtils.degreesToRadians * angulo;
        float vel = esEnemiga ? -7f : 7f;
        float vx =  MathUtils.sin(rad) * vel;
        float vy =  MathUtils.cos(rad) * vel;
        balas.add(new Bullet(x, y, vx, vy, txBala, esEnemiga));
        return balas;
    }
}