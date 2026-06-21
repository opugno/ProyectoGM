package puppy.code;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

/** 8 balas en todas las direcciones (para Boss). */
public class DisparoCircular implements EstrategiaDisparo {
    @Override
    public ArrayList<Bullet> crear(float x, float y, float angulo, Texture txBala, boolean esEnemiga) {
        ArrayList<Bullet> balas = new ArrayList<>();
        float vel = 4f;
        for (int i = 0; i < 8; i++) {
            float rad = MathUtils.degreesToRadians * (i * 45f);
            float vx  =  MathUtils.sin(rad) * vel;
            float vy  =  MathUtils.cos(rad) * vel;
            balas.add(new Bullet(x, y, vx, vy, txBala, true));
        }
        return balas;
    }
}
