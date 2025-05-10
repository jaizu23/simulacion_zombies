package servidor.seres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servidor.entorno.Mapa;
import servidor.exceptions.killedHumanException;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Humano extends Thread {
    private static final Logger logger = LogManager.getLogger(Humano.class);

    private final Random r = new Random();

    private final String id;

    private int comida = 0;

    private final Mapa mapa;

    private final AtomicBoolean marcado = new AtomicBoolean(false);
    private final AtomicBoolean asesinado = new AtomicBoolean(false);

    public Humano(String id, Mapa mapa) {
        this.id = id;
        this.mapa = mapa;
    }

    public void run() {
        while (!mapa.isPausado()) {
            try {
                try {
                    comprobarPausado();
                    mapa.getZonaComun().prepararse(this);

                    int zona = r.nextInt(0, 4);

                    comprobarPausado();
                    mapa.getTuneles()[zona].esperarSeguro(this);

                    comprobarPausado();
                    mapa.getZonasRiesgo()[zona].entrarHumano(this);
                    comprobarPausado();
                    mapa.getZonasRiesgo()[zona].recolectarComida(this);
                    comprobarPausado();
                    mapa.getZonasRiesgo()[zona].salir(id, true);

                    comprobarPausado();
                    mapa.getTuneles()[zona].esperarRiesgo(this);

                    comprobarPausado();
                    mapa.getComedor().depositarComida(this);
                    comprobarPausado();
                    mapa.getDescanso().descansar(this, 2000, 4000);
                    comprobarPausado();
                    mapa.getComedor().comer(this);

                    comprobarPausado();
                    if (marcado.get()) {
                        mapa.getDescanso().descansar(this, 2000, 4000);
                        marcado.set(false);
                    }
                } catch (killedHumanException e) {
                    logger.info("{} ha sido asesinado.", id);
                    break;
                }
            } catch (Exception e) {
                logger.error("La ejecución de {} ha sido interrumpida inesperadamente:", id);
                e.printStackTrace();
            }
        }
    }

    private void comprobarPausado() {
        while(mapa.isPausado()) {
            mapa.getLockPausado().lock();
            try {
                mapa.getConditionPausado().await();
            } catch (InterruptedException e) {
                logger.error("La ejecución de {} ha sido interrumpida mientras estaba pausado", id);
            } finally {
                mapa.getLockPausado().unlock();
            }
        }
    }

    public String getIdHumano() {
        return id;
    }

    public int getComida() {
        return comida;
    }

    public void añadirComida (int comida) {
        this.comida += comida;
    }

    public synchronized AtomicBoolean getMarcado() {
        return marcado;
    }

    public synchronized AtomicBoolean getAsesinado() {
        return asesinado;
    }
}
