package puppy.code;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

/** 5 balas en abanico amplio: centro ±15° ±30°. */
public class DisparoQuintuple implements EstrategiaDisparo {
    @Override
    public ArrayList<Bullet> crear(float x, float y, float angulo, Texture txBala, boolean esEnemiga) {
        ArrayList<Bullet> balas = new ArrayList<>();
        float vel = esEnemiga ? -7f : 7f;
        float[] offsets = { -30f, -15f, 0f, 15f, 30f };
        for (float off : offsets) {
            float rad = MathUtils.degreesToRadians * (angulo + off);
            float vx  =  MathUtils.sin(rad) * vel;
            float vy  =  MathUtils.cos(rad) * vel;
            balas.add(new Bullet(x, y, vx, vy, txBala, esEnemiga));
        }
        return balas;
    }
}