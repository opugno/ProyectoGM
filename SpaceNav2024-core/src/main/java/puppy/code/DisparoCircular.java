package puppy.code;
 
import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
 
/**
 * Disparo circular: 8 balas en todas direcciones
 * Este lo usa mas qeu nada el Boss
 */
public class DisparoCircular implements EstrategiaDisparo 
{
    @Override
    public ArrayList<Bullet> crear(float x, float y, Texture txBala, boolean esEnemiga) 
    {
        ArrayList<Bullet> balas = new ArrayList<>();
        int velocidad = 4;
        // 8 direcciones: N, NE, E, SE, S, SO, O, NO
        int[][] dirs = {
            {0, velocidad},           // norte
            {velocidad, velocidad},   // nor este
            {velocidad, 0},           // este
            {velocidad, -velocidad},  // sur este
            {0, -velocidad},          // sur
            {-velocidad, -velocidad}, // sur oeste
            {-velocidad, 0},          // oeste
            {-velocidad, velocidad}   // norte oeste
        };
        for (int[] d : dirs) {
            balas.add(new Bullet(x, y, d[0], d[1], txBala, true));
        }
        return balas;
    }
}
