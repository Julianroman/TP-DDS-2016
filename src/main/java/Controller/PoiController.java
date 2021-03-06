package Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.POI;
import Model.Terminal;
import POIsExt.Comuna;
import Repos.RepositorioPOIs;
import Repos.RepositorioTerminales;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class PoiController {
	public ModelAndView listar(Request req, Response res){
		Map<String, List<POI>> model = new HashMap<>();		
		List<POI> pois = RepositorioPOIs.getInstance().listar();
		String nombre = req.queryParams("nombreBuscado");
		String tipo = req.queryParams("tipoBuscado");
		String deshacer = req.queryParams("deshacer");
		if(deshacer == null){
			if(nombre != null && !nombre.equals(""))
				pois.retainAll(RepositorioPOIs.getInstance().buscarPorNombre(nombre));
			if(tipo != null && !tipo.equals(""))
				pois.retainAll(RepositorioPOIs.getInstance().buscarPOIsPorTipo(tipo)); 
		}
		model.put("pois", pois);
		return new ModelAndView(model, "admin/poi/pois.hbs");
	}
		
	public ModelAndView modificarView(Request req, Response res){
		Map<String, POI> model = new HashMap<>();
		String id = req.params("id");
		POI poi = RepositorioPOIs.getInstance().buscar(Long.parseLong(id));
		model.put("poi", poi);
		return new ModelAndView(model, "admin/poi/modificar.hbs");
	}
	
	public Exception modificar(Request req, Response res){
		String id = req.params("id");
		String X = req.queryParams("coordenadaXNueva");
		String Y = req.queryParams("coordenadaYNueva");
		String numeroComuna = req.queryParams("nuevaComuna");
		POI poi = RepositorioPOIs.getInstance().buscar(Long.parseLong(id));
		if(req.queryParams("nombreNuevo") != null && !req.queryParams("nombreNuevo").equals(""))
				poi.setNombre(req.queryParams("nombreNuevo"));
		if(req.queryParams("direccionNueva") != null && !req.queryParams("direccionNueva").equals(""))
				poi.setDireccion(req.queryParams("direccionNueva"));
		if(numeroComuna != null && !numeroComuna.equals(""))
			try{
				Comuna comuna = new Comuna(Long.parseLong(numeroComuna));
				poi.setComuna(comuna);
			}catch(Exception e){
				return e;
			}
		if(X != null && !X.equals("") && Y != null && !Y.equals(""))
			try{ // mas le vale que me de numeritos
				poi.setCoordenadas(Double.parseDouble(X), Double.parseDouble(Y));
			}catch(Exception e){
				return e;
			}
		RepositorioPOIs.getInstance().update(poi);
		res.redirect("/admin/pois");  
		return null;
	}
	
	public Exception eliminar(Request req, Response res){
		String id = req.params("id");
		try{
		RepositorioPOIs.getInstance().eliminarPOI(Long.parseLong(id));
		}catch(Exception e){
			return new Exception("No se puede eliminar el POI debido a que se borraran otras entidades que se encuentran en uso por otro POI", e);
		}
		res.redirect("/admin/pois");
		return null;
	}
	
	public ModelAndView mostrar(Request req, Response res){
		Map<String, POI> model = new HashMap<>();
		String id = req.params("id");
		POI poi = RepositorioPOIs.getInstance().buscar(Long.parseLong(id));
		model.put("poi", poi);
		return new ModelAndView(model, "/terminal/infoPoi.hbs");
	}
	
	public ModelAndView buscarCercanos(Request req, Response res){
		Map<String, List<POI>> model = new HashMap<>();		
		List<POI> pois = new ArrayList<>();
		String idTerminal = req.params("id");
		Terminal terminal = RepositorioTerminales.getInstance().buscar(Long.parseLong(idTerminal));
		pois.addAll(RepositorioPOIs.getInstance().buscarPOIsCercaDe(terminal));
		model.put("pois", pois);
		return new ModelAndView(model, "terminal/poisBusqueda.hbs");
	}
	
	public ModelAndView buscarDisponibles(Request req, Response res){
		Map<String, List<POI>> model = new HashMap<>();		
		List<POI> pois = new ArrayList<>();
		LocalDateTime date = LocalDateTime.now();
		pois.addAll(RepositorioPOIs.getInstance().buscarDisponibles(date)); // le mando la fecha actual 
		model.put("pois", pois);
		return new ModelAndView(model, "terminal/poisBusqueda.hbs");
	}
	
	public ModelAndView buscarPorTexto(Request req, Response res){
		Map<String, List<POI>> model = new HashMap<>();
		List<POI> pois = new ArrayList<>();
		String fraseBuscada = req.queryParams("fraseBuscada");
		pois.addAll(RepositorioPOIs.getInstance().buscarPorTexto(fraseBuscada));
		model.put("pois", pois);		
		return new ModelAndView(model, "terminal/poisBusqueda.hbs");
	}

}
