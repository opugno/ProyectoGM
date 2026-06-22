package puppy.code;

/**
 * GM-9: Director del Builder
 * Conoce la estructura de cada nivel y las construye usando NivelBuilder.
 *
 * BALANCE:
 *   - Velocidad máxima fijada en 4 (antes escalaba sin tope).
 *   - Extra enemigos limitado a +2 por ronda (antes +3).
 *   - Intervalo de disparo mínimo 1.4s (antes podía bajar a 1.0s).
 *   - Estos cambios evitan que niveles 5+ sean injugables.
 */
public class NivelDirector
{
    private final NivelBuilder builder;

    public NivelDirector()
    {
        builder = new NivelBuilder();
    }

    /**
     * Crea la configuración del nivel indicado.
     * Niveles 1-7 están predefinidos. Boss en niveles múltiplos de 7.
     * Luego del 7 escala automáticamente con límites razonables.
     */
    public NivelConfig crearNivel(int numero)
    {
        switch (numero)
        {
            case 1: return builder.conBasicos(12).conVelocidad(1).build();
            case 2: return builder.conBasicos(15).conVelocidad(1).build();
            case 3: return builder.conBasicos(10).conMedios(4).conVelocidad(2).build();
            case 4: return builder.conBasicos(8).conMedios(6).conVelocidad(2).build();
            case 5: return builder.conBasicos(6).conMedios(6).conElite(2).conVelocidad(2)
                                  .conIntervaloDisparo(3.0f).build();
            case 6: return builder.conBasicos(4).conMedios(8).conElite(4).conVelocidad(3)
                                  .conIntervaloDisparo(2.8f).build();
            case 7: return builder.conMedios(2).conElite(2).conBoss(30).conVelocidad(3)
                                  .conIntervaloDisparo(2.5f).build();
            default: return crearNivelEscalado(numero);
        }
    }

    /**
     * Para niveles 8+: repite el ciclo de 7 escalando suavemente.
     *
     * Cambios respecto a la versión anterior:
     *   - velocidad máxima = 4 (no crece más allá de eso)
     *   - extraEnemigos crece de a 2 por ronda (no 3)
     *   - intervalo disparo mínimo = 1.4f (no 1.0f)
     */
    private NivelConfig crearNivelEscalado(int numero)
    {
        int ciclo = ((numero - 1) % 7) + 1;
        int ronda = (numero - 1) / 7;          // 0 para niveles 1-7, 1 para 8-14...

        // Extra enemigos: +2 por ronda, tope en +8
        int extraEnemigos  = Math.min(ronda * 2, 8);

        // Velocidad: sube 1 por ronda pero tope en 4 para que sea jugable
        int extraVelocidad = Math.min(ronda, 1);   // máximo +1 sobre la base

        // Vida boss: sube 10 por ronda
        int vidaBossExtra  = ronda * 10;

        // Intervalo disparo: baja 0.2s por ronda, mínimo 1.4s
        float intervaloBase = Math.max(1.4f, 2.5f - ronda * 0.2f);

        switch (ciclo)
        {
            case 1:
                return builder.conBasicos(12 + extraEnemigos)
                              .conVelocidad(1 + extraVelocidad).build();
            case 2:
                return builder.conBasicos(15 + extraEnemigos)
                              .conVelocidad(1 + extraVelocidad).build();
            case 3:
                return builder.conBasicos(10 + extraEnemigos).conMedios(4 + ronda)
                              .conVelocidad(2 + extraVelocidad).build();
            case 4:
                return builder.conBasicos(8 + extraEnemigos).conMedios(6 + ronda)
                              .conVelocidad(2 + extraVelocidad).build();
            case 5:
                return builder.conBasicos(6 + extraEnemigos).conMedios(6 + ronda)
                              .conElite(2 + ronda).conVelocidad(2 + extraVelocidad)
                              .conIntervaloDisparo(intervaloBase + 0.5f).build();
            case 6:
                return builder.conBasicos(4 + extraEnemigos).conMedios(8 + ronda)
                              .conElite(4 + ronda).conVelocidad(3 + extraVelocidad)
                              .conIntervaloDisparo(intervaloBase + 0.3f).build();
            case 7:
                return builder.conMedios(2 + ronda).conElite(2 + ronda)
                              .conBoss(30 + vidaBossExtra)
                              .conVelocidad(3 + extraVelocidad)
                              .conIntervaloDisparo(intervaloBase).build();
            default:
                return builder.conBasicos(10).conVelocidad(1).build();
        }
    }
}