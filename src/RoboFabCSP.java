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

// RoboFabCSP: Solución con peticiones aplazadas
// Completad las líneas marcadas con "TO DO"

public class RoboFabCSP implements RoboFab, CSProcess {

    // Un canal para notificarPeso
    Any2OneChannel chNotificar;
    // Un canal para permisoSoltar
    Any2OneChannel chSoltar;
    // Un canal para solicitarAvance
    Any2OneChannel chAvanzar;
    // Un canal para contenedorNuevo
    Any2OneChannel chNuevo;

    // las peticiones de permisoSoltar se aplazan
    class PetSoltar {
	int robotId;
	One2OneChannel cconf;

	public PetSoltar (int robotId) {
	    this.robotId = robotId;
	    cconf = Channel.one2one();
	}
    }
    
    public RoboFabCSP() {
	// Creamos los canales
    	chNotificar = Channel.any2one();
	chSoltar = Channel.any2one();
	chAvanzar = Channel.any2one();
	chNuevo = Channel.any2one();
    }

    public void notificarPeso(int robotId, int peso) {
	PetNotificar pet = new PetNotificar(robotId,peso);
	chNotificar.out().write(pet);
    }
    
    public void permisoSoltar(int robotId) {
	PetSoltar pet = new PetSoltar(robotId);
	chSoltar.out().write(pet);
	pet.cconf.in().read();
    }

    public void solicitarAvance() {
	chAvanzar.out().write(null);
    }

    public void contenedorNuevo() {
	chNuevo.out().write(null);
    }

    public void run() {
	// declaramos estado del recurso: peso, pendientes...
	// TO DO
	// TO DO

	// Inicializamos el estado del recurso
	// TO DO
	// TO DO

	// Estructuras para recepción alternativa
	final AltingChannelInput[] guards = new AltingChannelInput[4];

	final int NOTIFICAR = 0;
	final int SOLTAR    = 1;
	final int AVANZAR   = 2;
	final int NUEVO     = 3;
	
	// guards[NOTIFICAR] = ... TO DO;
	// guards[SOLTAR]    = ... TO DO;
	// guards[AVANZAR]   = ... TO DO;
	// guards[NUEVO]     = ... TO DO;

	final Alternative services = new Alternative(guards);

	// el vector de recepción condicional solo regula
	// dinámicamente el canal de solicitarAvance
	boolean enabled[] = new boolean[4];

	// notificarPeso
	enabled[NOTIFICAR] = ... ;
	// dejamos abierto el canal de soltar
	enabled[SOLTAR]    = true;
	// inicialmente, solicitarAvance...
	enabled[AVANZAR]   = ... ;
	// contenedorNuevo
	enabled[NUEVO]     = ... ;

	// para las peticiones aplazadas de permisoSoltar:
	final One2OneChannel[] confirmacion = new One2OneChannel[Robots.NUM_ROBOTS];

	// bucle de servicio
	while (true) {
	    // recalculamos la sincronización por condición
	    // de solicitarAvance:
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
	    } else if (i == SOLTAR) {
		// TO DO
		// TO DO
		// TO DO 
		// guardamos la petición
		// TO DO
	    } else if (i == AVANZAR) {
		// TO DO
	    } else if (i == NUEVO) {
		// TO DO
   		// TO DO
	    }
	    // tratamiento de peticiones aplazadas
	    for (i = 0; i < Robots.NUM_ROBOTS; i++) {
		// TO DO
		// TO DO
		// TO DO
		// TO DO
		// TO DO
		// TO DO
		// TO DO
		// TO DO 
		// TO DO
	    }
	    // Aquí ya no quedan peticiones pendientes de tratar
	}
    }	
}

