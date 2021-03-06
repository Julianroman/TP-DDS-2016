package Procesos;

import java.util.List;
import java.util.stream.Collectors;

import Model.Terminal;
import POIsExt.Comuna;

public class CriterioSegunComuna extends TipoDeCriterio {
	
	//atributos
	private Comuna comunaSeleccionada;
	
	//getters y setters
	public Comuna getComunaSeleccionada() {
		return comunaSeleccionada;
	}
	public void setComunaSeleccionada(Comuna comunaSeleccionada) {
		this.comunaSeleccionada = comunaSeleccionada;
	}
	
	//Metodos
	public List<Terminal> terminalesAModificar(){ // Obtengo las terminales pertenecientes a la comuna
		return this.getTodasLasTerminales().stream()
		.filter(terminal -> terminal.getComuna() == this.getComunaSeleccionada())
		.collect(Collectors.toList());
	}

		
}
