//Escrito por Pablo Beltran y Eduardo Freyre
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
    	// Declaramos estado del recurso: pesoContenedor y el array de pesos pendientes
    	int[] pendientes;
    	int pesoContenedor;
    	
    	// Inicializamos el estado del recurso
    	pendientes = new int[Robots.NUM_ROBOTS];
		pesoContenedor = 0;

		// Estructuras para recepcion alternativa condicional
		final AltingChannelInput[] guards = new AltingChannelInput[Robots.NUM_ROBOTS+3];
		// Reservamos NUM_ROBOTS entradas para permisoSoltar y una entrada cada una de
		// notificarPeso, solicitarAvance y contenedorNuevo
		//Este bucle reserva las entradas de permisoSoltar
		for (int k = 0; k < Robots.NUM_ROBOTS;k++){
			guards[k] = chSoltar[k].in();
		}
		//Para no utilizar numeros magicos que creen confusion al 
		//leer el codigo, los canales para notificar, avanzar e 
		//indicar un contenedor nuevo se llamaran con unas constantes
		//que se declaran aqui
		final int NOTIFICAR = Robots.NUM_ROBOTS;
		final int AVANZAR   = Robots.NUM_ROBOTS + 1;
		final int NUEVO     = Robots.NUM_ROBOTS + 2;
		// Asignamos el resto de las entradas
		guards[NOTIFICAR] = chNotificar.in();
		guards[AVANZAR]   = chAvanzar.in();
		guards[NUEVO]     = chNuevo.in();

		//Array de booleanos para sincronizacion por condicion
		boolean enabled[] = new boolean[Robots.NUM_ROBOTS+3];	

		final Alternative services = new Alternative(guards);
		boolean control;
		
		//A partir de aqui comienza el bucle principal del programa
		//que se encargara de asignar los turnos de acceso al recurso
		while (true) {
			//Declaramos e inicializamos las variables auxiliares necesarias
			control = false;
			PetNotificar notificacion;
			//Refrescamos el vector enabled:
			for(int k = 0; k < Robots.NUM_ROBOTS; k++){
				//Las posiciones de soltar se abren cuando el robot carga un peso que 
				//se puede descargar sin problemas en el contenedor
				enabled[k] = (pesoContenedor + pendientes[k] <= Cinta.MAX_P_CONTENEDOR);
				//Cuando algun robot puede descargar, la variable control se pone a true,
				//lo que indica que la cinta no debe avanzar aun
				control = enabled[k] || control;
			}
		
			//La precondicion de notificar es true, asi que siempre se permite a un robot
			//notificar el peso que ha recogido
			enabled[NOTIFICAR] = true;
		
			//Se permite avanzar a la cinta cuando no hay robots que puedan descargar 
			enabled[AVANZAR] = !control;
		
			//La precondicion de nuevoContenedor es true, y por tanto no se hace ninguna
			//comprobacion previa, ya que el programa ejecutara las llamadas en orden
			//y no dara paso al metodo antes de tiempo
			enabled[NUEVO] = true;
	    

			//Nosotros habriamos implementado un switch aqui si no hubiesemos
			//tenido el esqueleto, pero hemos preferido no tocar la estructura
			//que se nos habia dado
			//El fairSelect devuelve el indice de una de las posiciones del vector
			//enabled siempre y cuando haya un mensaje esperando para ser enviado 
			//por ese canal
			int i = services.fairSelect(enabled);
			if (i == NOTIFICAR) {
				//Se escucha por el canal y se hace un casting para convertir
				//en la clase PetNotificar el Object que entra por el canal
				//de forma que podamos acceder a los metodos de la clase
				notificacion = (PetNotificar) guards[NOTIFICAR].read();
				//Se actualiza el array pendientes con el peso indicado
				//por el robot en la posicion que le corresponde
				pendientes[notificacion.robotId] = notificacion.peso;				
			} else if (i == AVANZAR) {
				//El metodo avanzar no tiene que hacer nada, simplemente dar
				//paso, por lo tanto se escucha por el canal sin guardar lo 
				//que entra (que no es mas que un Object null) de forma que el
				//proceso que hace el envio quede desbloqueado y pueda continuar
				guards[AVANZAR].read();
			} else if (i == NUEVO) {
				//Igual que en el metodo anterior, el proceso que hace el envio
				//no manda nada, por lo que se escucha para desbloquearlo y luego
				//se actualiza el estado del recurso, que es la postcondicion 
				//de este metodo
				guards[NUEVO].read();
				pesoContenedor = 0;
			} else if (i >=0 && i < Robots.NUM_ROBOTS) { // permisoSoltar
				//Las posiciones de 0 a NUM_ROBOTS son peticiones de soltar de
				//cada robot, por lo que se tratan individualmente. Se escucha para
				//desbloquear el robot en cuestion y se procede a actualizar el 
				//estado del recurso con los valores ya guardados anteriormente 
				guards[i].read();
				pesoContenedor = pesoContenedor + pendientes[i];
				pendientes[i] = 0;
			} 
		}
    }	
}

