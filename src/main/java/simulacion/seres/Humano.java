package simulacion.seres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import simulacion.entorno.Mapa;
import simulacion.exceptions.killedHumanException;
import simulacion.exceptions.unexpectedPriorityException;

import java.util.Random;

public class Humano extends Thread implements Comparable<Humano>, Ser {
    private static final Logger logger = LogManager.getLogger(Zombie.class);

    private final Random r = new Random();

    private int prioridadTunel = -1;

    private final String id;

    private int comida = 0;

    private final Mapa mapa;

    private boolean marcado = false;

    private boolean asesinado = false;

    public Humano(String id, Mapa mapa) {
        this.id = id;
        this.mapa = mapa;
    }

    public void marcar() {
        marcado = true;
    }

    public void run() {
        while (!mapa.isPausado()) {
            try {
                try {
                    mapa.getZonaComun().prepararse(this);

                    int zona = r.nextInt(0, 4);
                    mapa.getTuneles()[zona].esperarSeguro(this);

                    mapa.getZonasRiesgo()[zona].entrarZonaRiesgo(this);
                    mapa.getZonasRiesgo()[zona].recolectarComida(this);

                    mapa.getTuneles()[zona].esperarRiesgo(this);
                    mapa.getZonasRiesgo()[zona].salirZonaRiesgo(id, true);

                    mapa.getComedor().depositarComida(this);

                    mapa.getDescanso().descansar(this, 2000, 4000);

                    mapa.getComedor().comer(this);
                } catch (killedHumanException e) {
                    logger.info("El humano {} ha sido asesinado.", id);
                    break;
                }
            } catch (Exception e) {
                logger.error("La ejecución del humano {} ha sido interrumpida inesperadamente: {}", id, e);
            }
        }
    }


    @Override
    public int compareTo(@NotNull Humano otro) {
        if (this.prioridadTunel == -1) {
            throw new unexpectedPriorityException();
        } else {
            return Integer.compare(prioridadTunel, otro.getPrioridadTunel());
        }
    }

    public int getPrioridadTunel() {
        return prioridadTunel;
    }

    public void setPrioridadTunel(int prioridadTunel) {
        this.prioridadTunel = prioridadTunel;
    }

    public String getIdHumano() {
        return id;
    }

    public Boolean getMarcado() {
        return marcado;
    }

    public Mapa getMapa() {
        return mapa;
    }

    public int getComida() {
        return comida;
    }

    public void añadirComida (int comida) {
        this.comida += comida;
    }

    public boolean isAsesinado() {
        return asesinado;
    }

    public void setAsesinado(boolean asesinado) {
        this.asesinado = asesinado;
    }
}
