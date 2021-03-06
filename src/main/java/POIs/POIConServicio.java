package POIs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.uqbar.geodds.Point;

import Model.POI;
import POIsExt.Servicio;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public abstract class POIConServicio extends POI {

	//ATRIBUTOS
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)  
	private List<Servicio> 	servicios;
	
	//Constructor
	public POIConServicio(Point unaUbicacion) {
		super(unaUbicacion);
	}
	
	public POIConServicio(){ }

	
	//METODOS
	public boolean estaDisponible(String unNombreDeServicio,LocalDateTime unTiempo){
		if(unNombreDeServicio == null){
			return this.algunServicioDisponible();
		} else {		
			return this.servicioDisponible(unNombreDeServicio,unTiempo);
		}
	}
	
	public boolean algunServicioDisponible(){
		LocalDateTime 	horaDelMomento = LocalDateTime.now();	//Instancio la hora del momento
		return	this.getServicios().stream().
				anyMatch(servicio -> servicio.estaDisponible(horaDelMomento));
	}
	
	public boolean servicioDisponible(String unNombreDeServicio, LocalDateTime unTiempo){
		return this.buscarServicio(unNombreDeServicio).estaDisponible(unTiempo);
	}
	
	public Servicio buscarServicio(String unNombreDeServicio){
		return this.getServicios().stream().
				filter(servicio -> servicio.getNombre() == unNombreDeServicio).
				collect(Collectors.toList()).get(0); //SE SUPONE QUE EL SERVICIO INGRESADO SIEMPRE ES VALIDO
	}

	//GETERS Y SETERS

	
	public List<Servicio> getServicios(){
		return servicios;
	}

	public void setServicios(List<Servicio> nuevaListaDeServicios){
		servicios = nuevaListaDeServicios;
	}

	public void addServicio(Servicio unServicio){
		servicios.add(unServicio);
	}
}
