package puppy.code;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

/** 4 balas: 2 rectas + 2 diagonales a ±20°. */
public class DisparoCuadruple implements EstrategiaDisparo {
    @Override
    public ArrayList<Bullet> crear(float x, float y, float angulo, Texture txBala, boolean esEnemiga) {
        ArrayList<Bullet> balas = new ArrayList<>();
        float vel = esEnemiga ? -7f : 7f;
        float[] offsets = { -20f, -7f, 7f, 20f };
        float rad0 = MathUtils.degreesToRadians * angulo;
        float px   = MathUtils.cos(rad0) * 6f;
        float py   = -MathUtils.sin(rad0) * 6f;
        float[] spawnX = { x - px, x - px/2, x + px/2, x + px };
        float[] spawnY = { y - py, y - py/2, y + py/2, y + py };
        for (int i = 0; i < 4; i++) {
            float rad = MathUtils.degreesToRadians * (angulo + offsets[i]);
            float vx  =  MathUtils.sin(rad) * vel;
            float vy  =  MathUtils.cos(rad) * vel;
            balas.add(new Bullet(spawnX[i], spawnY[i], vx, vy, txBala, esEnemiga));
        }
        return balas;
    }
}