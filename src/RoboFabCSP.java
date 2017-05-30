import org.jcsp.lang.Alternative;
import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.Any2OneChannel;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.Guard;
import org.jcsp.lang.One2OneChannel;
    

class PetNotificar {
    int robotId;
    int peso;
    
    public PetNotificar(int robotId, int peso) {
	this.robotId = robotId;
	this.peso = peso;
    }
}

// RoboFabCSP: Solución con replicación de canales
// Completad las líneas marcadas "TO DO"

public class RoboFabCSP implements RoboFab, CSProcess {


    // Un canal para notificarPeso
    Any2OneChannel chNotificar;
    // NUM_ROBOTS canales para permisoSoltar
    Any2OneChannel chSoltar[];
    // Un canal para solicitarAvance
    Any2OneChannel chAvanzar;
    // Un canal para contenedorNuevo
    Any2OneChannel chNuevo;
    
    public RoboFabCSP() {

	// Creamos los canales
    	chNotificar = Channel.any2one();
    	chSoltar = new Any2OneChannel[Robots.NUM_ROBOTS];
    	for (int i=0; i<Robots.NUM_ROBOTS; i++){
    		chSoltar[i] = Channel.any2one();
    	}
    	chAvanzar = Channel.any2one();
    	chNuevo = Channel.any2one();
    }

    public void permisoSoltar(int robotId) {
    	chSoltar[robotId].out().write(null);
    }

    public void notificarPeso(int robotId, int peso) {
    	PetNotificar pet = new PetNotificar(robotId,peso);
    	chNotificar.out().write(pet);
    }

    public void solicitarAvance() {
    	chAvanzar.out().write(null);
    }

    public void contenedorNuevo() {
    	chNuevo.out().write(null);
    }

    public void run() {
	// declaramos estado del recurso: peso, pendientes...
    	int[] pendientes;
    	int pesoContenedor;
    	
	// TO DO

	// Inicializamos el estado del recurso
    	pendientes = new int[Robots.NUM_ROBOTS];
		pesoContenedor = 0;

	// Estructuras para recepción alternativa condicional
	final AltingChannelInput[] guards = new AltingChannelInput[Robots.NUM_ROBOTS+3];
	// reservamos NUM_ROBOTS entradas para permisoSoltar y una entrada cada una de
	// notificarPeso, solicitarAvance y contenedorNuevo
		for (int k = 0; k < Robots.NUM_ROBOTS;k++){
			guards[k] = chSoltar[k].in();
		}
		final int NOTIFICAR = Robots.NUM_ROBOTS;
		final int AVANZAR   = Robots.NUM_ROBOTS + 1;
		final int NUEVO     = Robots.NUM_ROBOTS + 2;
	// 
		guards[NOTIFICAR] = chNotificar.in();
		guards[AVANZAR]   = chAvanzar.in();
		guards[NUEVO]     = chNuevo.in();

	// array de booleanos para sincronización por condición
		boolean enabled[] = new boolean[Robots.NUM_ROBOTS+3];
	// inicializamos las condiciones de activación de los canales
		for(int k = 0; k < Robots.NUM_ROBOTS; k++){
			enabled[k] = (pesoContenedor + pendientes[k] <= Cinta.MAX_P_CONTENEDOR);
		}
	// TO DO 
	// TO DO
	// TO DO
	// TO DO
	// TO DO
	// TO DO
	// TO DO
	// TO DO

	final Alternative services = new Alternative(guards);

	while (true) {
	    // refrescamos el vector enabled:
	    // TO DO
	    // TO DO
            // TO DO
	    // TO DO
            // TO DO
	    // TO DO
	    // TO DO
	    // TO DO
	    // TO DO
	    // TO DO

	    // la SELECT:
	    int i = services.fairSelect(enabled);
	    if (i == NOTIFICAR) {
		// TO DO
		// TO DO
		// TO DO
		// TO DO
	    } else if (i == AVANZAR) {
		// TO DO
	    } else if (i == NUEVO) {
		// TO DO
   		// TO DO
	    } else if (/*rellenar esta condicion, el true me lo he inventado*/true) { // permisoSoltar
		// TO DO
		// TO DO
		// TO DO
	    } 
	}
    }	
}

