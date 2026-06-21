package puppy.code;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

/** 6 balas: cobertura de 150° totales, separadas 30° entre sí. */
public class DisparoSextuple implements EstrategiaDisparo {
    @Override
    public ArrayList<Bullet> crear(float x, float y, float angulo, Texture txBala, boolean esEnemiga) {
        ArrayList<Bullet> balas = new ArrayList<>();
        float vel = esEnemiga ? -7f : 7f;
        float[] offsets = { -37f, -22f, -7f, 7f, 22f, 37f };
        float rad0 = MathUtils.degreesToRadians * angulo;
        float px   = MathUtils.cos(rad0) * 5f;
        float py   = -MathUtils.sin(rad0) * 5f;
        float[] ox = { -px*1.2f, -px*0.6f, -px*0.2f, px*0.2f, px*0.6f, px*1.2f };
        float[] oy = { -py*1.2f, -py*0.6f, -py*0.2f, py*0.2f, py*0.6f, py*1.2f };
        for (int i = 0; i < 6; i++) {
            float rad = MathUtils.degreesToRadians * (angulo + offsets[i]);
            float vx  =  MathUtils.sin(rad) * vel;
            float vy  =  MathUtils.cos(rad) * vel;
            balas.add(new Bullet(x + ox[i], y + oy[i], vx, vy, txBala, esEnemiga));
        }
        return balas;
    }
}
