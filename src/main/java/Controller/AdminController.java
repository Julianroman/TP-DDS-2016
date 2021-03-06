package Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.PoisDeBusqueda;
import Model.ResultadoBusqueda;
import Model.Terminal;
import Repos.RepositorioBusquedas;
import Repos.RepositorioTerminales;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class AdminController {

	public ModelAndView menu(Request req, Response res){
		return new ModelAndView(null, "admin/index.hbs");
	}
	
	
	public ModelAndView listarTerminales(Request req, Response res){
		Map<String, List<Terminal>> model = new HashMap<>();
		List<Terminal> terminales = RepositorioTerminales.getInstance().listar();
		
		model.put("admin", terminales);
		return new ModelAndView(model, "admin/terminales.hbs");
	}
	
	public ModelAndView listarBusquedas(Request req, Response res){
		Map<String, List<ResultadoBusqueda>> model = new HashMap<>();
		List<ResultadoBusqueda> resultados = RepositorioBusquedas.getInstance().listar();
		model.put("admin", resultados);
		return new ModelAndView(model, "admin/busquedas.hbs");
	}
	
	public ModelAndView listarPoisBusquedas(Request req, Response res){
		Map<String, List<PoisDeBusqueda>> model = new HashMap<>();
		String id = req.params("id");
		ResultadoBusqueda resultBusqueda = RepositorioBusquedas.getInstance().buscar(Long.parseLong(id));
		List<PoisDeBusqueda> resultados = resultBusqueda.getResultadoBusqueda();
		model.put("busqueda", resultados);
		return new ModelAndView(model, "admin/resultadosBusqueda.hbs");
	}
	


	
}
