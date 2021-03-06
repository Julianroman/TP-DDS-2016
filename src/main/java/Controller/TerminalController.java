package Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager;
import org.uqbarproject.jpa.java8.extras.transaction.TransactionalOps;

import Model.Terminal;
import ObserversTerminal.AccionesTerminal;
import ObserversTerminal.AlmacenarBusqueda;
import ObserversTerminal.NotificarAdministrador;
import ObserversTerminal.ReporteParcial;
import ObserversTerminal.ReportePorFecha;
import ObserversTerminal.ReporteTotalesPorUsuario;
import POIsExt.Comuna;
import Repos.RepositorioTerminales;
import spark.ModelAndView;
import spark.Request;
import spark.Response;


public class TerminalController implements WithGlobalEntityManager, TransactionalOps{
	
	private String id;

	public ModelAndView home(Request req, Response res){
		Map<String, Terminal> model = new HashMap<>();
		id = req.params("id");
		Terminal terminal = RepositorioTerminales.getInstance().buscar(Long.parseLong(id));
		model.put("terminal", terminal);
		return new ModelAndView(model, "terminal/home.hbs");
	}
	
	public ModelAndView listar(Request req, Response res) throws Exception {
		Map<String, List<Terminal>> model = new HashMap<>();		
		List<Terminal> terminales = new ArrayList<>();
		String numeroComuna = req.queryParams("comunaBuscada");
		String deshacer = req.queryParams("deshacer");
		if(deshacer != null) numeroComuna = null;
		if(numeroComuna != null && !numeroComuna.equals(""))
			try{
				terminales = RepositorioTerminales.getInstance().buscarPorComuna(Long.parseLong(numeroComuna));
			}catch(Exception e){}
		else if(numeroComuna == null || numeroComuna.equals(""))
			terminales.addAll(RepositorioTerminales.getInstance().listar());
		model.put("terminales", terminales);
		return new ModelAndView(model, "admin/terminal/terminales.hbs");
	}
	
	public ModelAndView crearView(Request req, Response res){
		return new ModelAndView(null,"admin/terminal/crear.hbs");
	}
	
	public Exception crear(Request req, Response res){
		Terminal nuevaTerminal = new Terminal();
		String nombre = req.queryParams("nombre");
		String comunaNumero = req.queryParams("comuna");
		String X = req.queryParams("coordenadaXNueva");
		String Y = req.queryParams("coordenadaYNueva");
		nuevaTerminal.setNombre(nombre);
		try{
			Comuna comuna = new Comuna(Long.parseLong(comunaNumero));
			nuevaTerminal.setComuna(comuna);
		}catch(Exception e){
			return e;
		}
		if(X != null && !X.equals("") && Y != null && !Y.equals(""))
			try{ // mas le vale que me de numeritos
				nuevaTerminal.setCoordenadas(Double.parseDouble(X), Double.parseDouble(Y));
			}catch(Exception e){
				return e;
			}
		List<AccionesTerminal> acciones = asignarAcciones(req);
		nuevaTerminal.setObservers(acciones);
		RepositorioTerminales.getInstance().agregar(nuevaTerminal);
		res.redirect("/admin/terminales");
		return null;
	}
	
	public ModelAndView modifView(Request req, Response res){
		Map<String, Terminal> model = new HashMap<>();
		String id = req.params("id");
		Terminal terminal = RepositorioTerminales.getInstance().buscar(Long.parseLong(id));
		model.put("terminal", terminal);
		return new ModelAndView(model, "admin/terminal/modificar.hbs");
	}
	
	public Exception modificar(Request req, Response res){
		String id = req.params("id");
		Terminal terminal = RepositorioTerminales.getInstance().buscar(Long.parseLong(id));
		String nombre = req.queryParams("nombreNuevo");
		String comunaNumero = req.queryParams("comunaNueva");
		terminal.getObservers().clear(); // vacio la lista
		List<AccionesTerminal> acciones = asignarAcciones(req);
		if(nombre != null && !nombre.equals("")) terminal.setNombre(nombre);
		if(comunaNumero != null && !comunaNumero.equals("")){
			try{
				Comuna comuna = new Comuna(Long.parseLong(comunaNumero));
				terminal.setComuna(comuna);
			}catch(Exception e){
				return e;
			}
		}
		terminal.setObservers(acciones);
		RepositorioTerminales.getInstance().update(terminal);
		res.redirect("/admin/terminales");  
		return null;
	}
	
	public List<AccionesTerminal> asignarAcciones(Request req){
		String almacenarBusquedas = req.queryParams("almacenarBusquedas");
		String notificarAdmin = req.queryParams("notificarAdmin");
		String reporteParcial = req.queryParams("reporteParcial");
		String reportePorFecha = req.queryParams("reportePorFecha");
		String reporteTotalesPorUsuario = req.queryParams("reporteTotalesPorUsuario");
		List<AccionesTerminal> acciones = new ArrayList<>();
		if(almacenarBusquedas != null) acciones.add(new AlmacenarBusqueda());
		if(notificarAdmin != null) acciones.add(new NotificarAdministrador());
		if(reporteParcial != null) acciones.add(new ReporteParcial());
		if(reportePorFecha != null) acciones.add(new ReportePorFecha());
		if(reporteTotalesPorUsuario != null) acciones.add(new ReporteTotalesPorUsuario());
		return acciones;
	}
	
	public Exception eliminar(Request req, Response res) {
		String id = req.params("id");
		try{
		RepositorioTerminales.getInstance().eliminarTerminal(Long.parseLong(id));
		}catch(Exception e){
			return new Exception("No se puede eliminar esta terminal debido a que se borraran otras entidades que se encuentran en uso por otra terminal", e);
		}
		res.redirect("/admin/terminales");
		return null;
	}
}
