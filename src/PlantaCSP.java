// PlantaCSP.java
// (paso de mensajes)
import org.jcsp.lang.*;
import es.upm.babel.cclib.ConcIO;

class PlantaCSP {
    public static final void main(final String[] args)
        throws InterruptedException {
        // Array de procesos CSP
        CSProcess[] procesos = new CSProcess[Robots.NUM_ROBOTS+2];
        CSProcess sistema;
        RoboFabCSP cr = new RoboFabCSP();
        int p = 0;

        // Creamos los procesos
        procesos[p++] = cr;
        for (int i = 0; i < Robots.NUM_ROBOTS; i++) {
	    try {
		procesos[p++] = new ControlRobotCSP(i, cr);
	    } catch (Exception x) {}
        }
        procesos[p++] = new ControlCintaCSP(cr);

        // Creamos el sistema
        sistema = new Parallel(procesos);

        // Ponemos en marcha el sistema
        sistema.run();
    }

    // Clase para los procesos que controlan los contenedores 
    static class ControlCintaCSP implements CSProcess {
	private RoboFabCSP cr;
	
	protected ControlCintaCSP () {
	}
	
	public ControlCintaCSP (RoboFabCSP cr) {
	    this.cr = cr;
	}
	
	public void run () {
	    while (true) {
		ConcIO.printfnl("Solicitar avanzar contenedor del recurso");
		cr.solicitarAvance();
		ConcIO.printfnl("Permiso de avanzar contenedor obtenido");
		ConcIO.printfnl("Empieza a sustituir contenedor");
		Cinta.avance();
		ConcIO.printfnl("Contenedor sustuituido");
		ConcIO.printfnl("Informando el recurso sobre sustitucion del contenedor");
		cr.contenedorNuevo();
		ConcIO.printfnl("Fin de sustitución");

		// Retardo para provocar potenciales entrelazados
		try {
		    Thread.sleep(100);
		} catch (InterruptedException x) {}
	    }
	}
    }

    // Clase para los procesos que controlan las robots 
    static class ControlRobotCSP implements CSProcess {
	private RoboFabCSP cr;
	private int indice;
	
	protected ControlRobotCSP (){
	}
	
	public ControlRobotCSP (int indice, RoboFabCSP cr) throws Exception {
	    this.cr = cr;
	    if (indice >= 0 && indice <= Robots.NUM_ROBOTS) { 
		this.indice = indice;
	    } else {
		throw new IllegalArgumentException("Índice de robot fuera de rango");
	    }
	}
	
	public void run () {
	    int peso;
	    while (true) {
		ConcIO.printfnl ("Robot " + indice + " inicio recoger.");
		peso = Robots.recoger(indice);
		ConcIO.printfnl ("Robot " + indice + " recogió " + peso);
		ConcIO.printfnl ("Robot " + indice + " empieza a notificar peso "+peso);
		cr.notificarPeso(indice,peso);
		ConcIO.printfnl ("Robot " + indice + " notificó peso "+peso);
		ConcIO.printfnl ("Robot " + indice + " pide permiso de soltar");
		cr.permisoSoltar(indice);
		ConcIO.printfnl ("Robot " + indice + " obtuvo permiso de soltar");
		ConcIO.printfnl ("Robot " + indice + " inicia soltar.");
		Robots.soltar(indice);
		ConcIO.printfnl ("Robot " + indice + " soltó " + peso);
	    }
	}
    }

}
