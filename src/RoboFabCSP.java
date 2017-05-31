//Escrito por Pablo Beltr·n y Eduardo Freyre
//31 de Mayo de 2017

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

// RoboFabCSP: Soluci√≥n con replicaci√≥n de canales
// Completad las l√≠neas marcadas "TO DO"

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

		// Estructuras para recepci√≥n alternativa condicional
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

		// array de booleanos para sincronizaci√≥n por condici√≥n
		boolean enabled[] = new boolean[Robots.NUM_ROBOTS+3];
	
		// Las condiciones de activaciÛn de los canales
		
	

		final Alternative services = new Alternative(guards);
		boolean control;
		boolean avanzando = false;
		boolean[] notificado = new boolean[Robots.NUM_ROBOTS];

		while (true) {
			control = false;
			PetNotificar notificacion;
			// refrescamos el vector enabled:
			for(int k = 0; k < Robots.NUM_ROBOTS; k++){
				boolean pesoSeguro = (pesoContenedor + pendientes[k] <= Cinta.MAX_P_CONTENEDOR);
				enabled[k] = (notificado[k] && pesoSeguro && !avanzando);
				control = enabled[k] || control || pesoSeguro;
			}
		
			//La precondicion de notificar es true, asÌ que siempre se permite a un robot
			//notificar el peso que ha recogido
			enabled[NOTIFICAR] = true;
		
			//La variable control comprueba si alguno de los robots puede descargar a˙n con seguridad
			//En caso afirmativo, no se da permiso para avanzar la cinta.
			enabled[AVANZAR] = !control;
		
			//La precondicion de nuevoContenedor es true, asÌ que siempre que se haya dado permiso
			//para avanzar, se tendr· permiso para notificar el contenedor nuevo
			enabled[NUEVO] = avanzando;
	    

			// la SELECT:
			int i = services.fairSelect(enabled);
			if (i == NOTIFICAR) {
				notificacion = (PetNotificar) guards[NOTIFICAR].read();
				pendientes[notificacion.robotId] = notificacion.peso;
				notificado[notificacion.robotId] = true;
				// TO DO
			} else if (i == AVANZAR) {
				guards[AVANZAR].read();
				avanzando = true;
			} else if (i == NUEVO) {
				guards[NUEVO].read();
				pesoContenedor = 0;
				avanzando = false;
			} else if (i >=0 && i < Robots.NUM_ROBOTS) { // permisoSoltar
				guards[i].read();
				pesoContenedor = pesoContenedor + pendientes[i];
				pendientes[i] = 0;
				notificado[i] = false;
			} 
		}
    }	
}

