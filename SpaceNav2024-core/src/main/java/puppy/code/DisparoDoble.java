package puppy.code;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

/** 2 balas paralelas a los lados del eje de la nave. */
public class DisparoDoble implements EstrategiaDisparo {
    @Override
    public ArrayList<Bullet> crear(float x, float y, float angulo, Texture txBala, boolean esEnemiga) {
        ArrayList<Bullet> balas = new ArrayList<>();
        float rad  = MathUtils.degreesToRadians * angulo;
        float vel  = esEnemiga ? -7f : 7f;
        float vx   =  MathUtils.sin(rad) * vel;
        float vy   =  MathUtils.cos(rad) * vel;
        // perpendicular al eje de disparo (para separar las balas lateralmente)
        float px   = MathUtils.cos(rad) * 8f;
        float py   = -MathUtils.sin(rad) * 8f;
        balas.add(new Bullet(x - px, y - py, vx, vy, txBala, esEnemiga));
        balas.add(new Bullet(x + px, y + py, vx, vy, txBala, esEnemiga));
        return balas;
    }
}
