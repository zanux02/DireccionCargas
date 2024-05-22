package es.iesjandula.direccion_cargahoraria_server.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.iesjandula.direccion_cargahoraria_server.exception.HorarioException;
import es.iesjandula.direccion_cargahoraria_server.models.Asignatura;
import es.iesjandula.direccion_cargahoraria_server.models.Curso;
import es.iesjandula.direccion_cargahoraria_server.models.Departamento;
import es.iesjandula.direccion_cargahoraria_server.models.Profesor;
import es.iesjandula.direccion_cargahoraria_server.models.Reduccion;
import es.iesjandula.direccion_cargahoraria_server.models.ReduccionHoras;
import es.iesjandula.direccion_cargahoraria_server.models.Resumen;
import es.iesjandula.direccion_cargahoraria_server.models.ResumenProfesor;
import es.iesjandula.direccion_cargahoraria_server.utils.Parse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

@RequestMapping(value="/carga_horaria")
@RestController
@Log4j2
public class RestHandler 
{

	/**
	 * endpoint para subir los departamentos
	 * @param csvFile
	 * @param session
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST,value="/departamentos",consumes="multipart/form-data")
	public ResponseEntity<?> uploadDepartamentos(@RequestParam(value="csv",required=true)MultipartFile csvFile,HttpSession session)
	{
		try
		{
			Parse parse = new Parse();
			//parseamos el csv con el endpoint
			List<Departamento> listaDepartamentos = parse.parseDepartamentos(csvFile);
			//guardamos la lista en session
			session.setAttribute("listaDepartamentos", listaDepartamentos);
			log.info(listaDepartamentos);
			return ResponseEntity.ok().body("Departamentos subidos correctamente");
		}
		catch(HorarioException horarioException)
		{
			String error = "Error de parseo";
			log.error(error,horarioException.getBodyExceptionMessage());
			return ResponseEntity.status(410).body(error);
		}
		catch(Exception exception)
		{
			String error = "Error desconocido";
			log.error(error,exception.getMessage());
			return ResponseEntity.status(400).body(error);
		}
	}
	
	/**
	 * endpoint para obtener los departamentos
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.GET,value="/departamentos")
	public ResponseEntity<?> consultaDepartamentos(HttpSession session)
	{
		try
		{
			List<Departamento> listaDepartamentos;
			//comprobamos si la lista existe
			if(session.getAttribute("listaDepartamentos")!=null)
			{
				listaDepartamentos = (List<Departamento>) session.getAttribute("listaDepartamentos");
			}
			else 
			{
				String error = "Los departamentos no han sido cargados en sesion todavía";
				throw new HorarioException(1,error);
			}

			log.info(listaDepartamentos);
			return ResponseEntity.ok(listaDepartamentos);
		}
		catch(HorarioException horarioException)
		{
			String error = "Error de carga de datos";
			log.error(error,horarioException.getMessage());
			return ResponseEntity.status(410).body(horarioException.getBodyExceptionMessage());
			
		}
		catch(Exception exception)
		{
			String error = "Error desconocido";
			log.error(error,exception.getMessage());
			return ResponseEntity.status(400).body(error);
		}
	}
	
	/**
	 * endpoint para subir los cursos
	 * @param csvFile
	 * @param session
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST,value="/cursos",consumes="multipart/form-data")
	public ResponseEntity<?> uploadCursos(@RequestParam(value="csv",required=true)MultipartFile csvFile,HttpSession session)
	{
		try
		{
			Parse parse = new Parse();
			//parseamos el csv con el endpoint
			List<Curso> listaCursos = parse.parseCursos(csvFile);
			//guardamos la lista en session
			session.setAttribute("listaCursos", listaCursos);
			log.info(listaCursos);
			return ResponseEntity.ok().body("Cursos subidos correctamente");
		}
		catch(HorarioException horarioException)
		{
			String error = "Error de parseo";
			log.error(error,horarioException.getBodyExceptionMessage());
			return ResponseEntity.status(410).body(error);
		}
		catch(Exception exception)
		{
			String error = "Error desconocido";
			log.error(error,exception.getMessage());
			return ResponseEntity.status(400).body(exception.getMessage());
		}
	}
	
	/**
	 * endpoint para obtener lista de cursos
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.GET,value="/cursos")
	public ResponseEntity<?> consultaCursos(HttpSession session)
	{
		try
		{
			List<Curso> listaCursos;
			//comprobamos si la lista existe
			if(session.getAttribute("listaCursos")!=null)
			{
				listaCursos = (List<Curso>) session.getAttribute("listaCursos");
			}
			else
			{
				String error = "Los cursos no han sido cargados en sesion todavía";
				throw new HorarioException(1,error);
			}

			log.info(listaCursos);
			return ResponseEntity.ok().body(listaCursos);
		}
		catch(HorarioException horarioException)
		{
			String error = "Error de carga de datos";
			log.error(error,horarioException.getBodyExceptionMessage());
			return ResponseEntity.status(410).body(horarioException.getBodyExceptionMessage());
			
		}
		catch(Exception exception)
		{
			String error = "Error desconocido";
			log.error(error,exception.getMessage());
			return ResponseEntity.status(400).body(error);
		}
	}
	
	/**
	 * endpoint para subir los profesores
	 * @param csvFile
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.POST,value="/profesores",consumes="multipart/form-data")
	public ResponseEntity<?> uploadProfesores(@RequestParam(value="csv",required=true)MultipartFile csvFile,HttpSession session)
	{
		try
		{
			//comprobamos si existe la lista de departamentos
			if(session.getAttribute("listaDepartamentos")==null)
			{
				String error = "Los departamentos no han sido cargados";
				throw new HorarioException(2,error);
			}
			Parse parse = new Parse();
			//obtenemos la lista de departamentos guardada en session
			List<Departamento> listaDepartamentos = (List<Departamento>) session.getAttribute("listaDepartamentos");
			//parseamos el csv con el endpoint
			List<Profesor> listaProfesores = parse.parseProfesores(csvFile,listaDepartamentos);
			//guardamos la lista en session
			session.setAttribute("listaProfesores", listaProfesores);
			log.info(listaProfesores);
			return ResponseEntity.ok().body("Profesores subidos correctamente");
		}
		catch(HorarioException horarioException)
		{
			String error = "Error de parseo";
			log.error(error,horarioException.getBodyExceptionMessage());
			return ResponseEntity.status(410).body(error);
		}
		catch(Exception exception)
		{
			String error = "Error desconocido";
			log.error(error,exception.getMessage());
			return ResponseEntity.status(400).body(error);
		}
	}
	
	/**
	 * endpoint para obtener la lista profesores
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.GET,value="/profesores")
	public ResponseEntity<?> consultaProfesores(HttpSession session)
	{
		try
		{
			List<Profesor> listaProfesores;
			//comprobamos si existe la lista profesores
			if(session.getAttribute("listaProfesores")!=null)
			{
				listaProfesores = (List<Profesor>) session.getAttribute("listaProfesores");
			}
			else 
			{
				String error = "Los profesores no han sido cargados en sesion todavía";
				throw new HorarioException(1,error);
			}

			log.info(listaProfesores);
			return ResponseEntity.ok().body(listaProfesores);
		}
		catch(HorarioException horarioException)
		{
			String error = "Error de carga de datos";
			log.error(error,horarioException.getBodyExceptionMessage());
			return ResponseEntity.status(410).body(horarioException.getBodyExceptionMessage());
			
		}
		catch(Exception exception)
		{
			String error = "Error desconocido";
			log.error(error,exception.getMessage());
			return ResponseEntity.status(400).body(exception.getMessage());
		}
	}
	
	/**
	 * endpoint para subir las asignaturas
	 * @param csvFile
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.POST,value="/asignaturas",consumes="multipart/form-data")
	public ResponseEntity<?> uploadAsignaturas(@RequestParam(value="csv",required=true)MultipartFile csvFile,HttpSession session)
	{
		try
		{
			//comprobamos si existe la lista de departamentos
			if(session.getAttribute("listaDepartamentos")==null)
			{
				String error = "Los departamentos no han sido cargados";
				throw new HorarioException(2,error);
			}
			List<Departamento> listaDepartamentos = (List<Departamento>) session.getAttribute("listaDepartamentos");
			//comprobamos si existe la lista cursos
			if(session.getAttribute("listaCursos")==null)
			{
				String error = "Los cursos no han sido cargados";
				throw new HorarioException(2,error);
			}
			List<Curso> listaCursos = (List<Curso>) session.getAttribute("listaCursos");
			
			Parse parse = new Parse();
			//parseamos el csv con el endpoint
			List<Asignatura> listaAsignaturas = parse.parseAsignaturas(csvFile,listaCursos,listaDepartamentos);
			//guardamos la lista en session
			session.setAttribute("listaAsignaturas", listaAsignaturas);
			log.info(listaAsignaturas);
			return ResponseEntity.ok().body("Asignaturas subidas correctamente");
		}
		catch(HorarioException horarioException)
		{
			return ResponseEntity.status(410).body(horarioException.getBodyExceptionMessage());
		}
		catch(Exception exception)
		{
			String error = "Error desconocido";
			log.error(error,exception.getMessage());
			return ResponseEntity.status(400).body(error);
		}
	}
	
	/**
	 * endpoint para obtener las asignaturas
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.GET,value="/asignaturas")
	public ResponseEntity<?> consultaAsignaturas(HttpSession session)
	{
		try
		{
			List<Asignatura> listaAsignaturas;
			//comprobamos si existe la lista asignaturas
			if(session.getAttribute("listaAsignaturas")!=null)
			{
				listaAsignaturas = (List<Asignatura>) session.getAttribute("listaAsignaturas");
			}
			else
			{
				String error = "Las asignatura no han sido cargados en sesion todavía";
				throw new HorarioException(1,error);
			}

			log.info(listaAsignaturas);
			return ResponseEntity.ok().body(listaAsignaturas);
		}
		catch(HorarioException horarioException)
		{
			String error = "Error de carga de datos";
			log.error(error,horarioException.getBodyExceptionMessage());
			return ResponseEntity.status(410).body(horarioException.getBodyExceptionMessage());
			
		}
		catch(Exception exception)
		{
			String error = "Error desconocido";
			log.error(error,exception.getMessage());
			return ResponseEntity.status(400).body(error);
		}
	}
	
	/**
	 * endpoint para asignar una asignatura a un profesor
	 * @param idProfesor
	 * @param nombreAsignatura
	 * @param curso
	 * @param etapa
	 * @param grupo
	 * @param session
	 * @return
	 */
	@SuppressWarnings({ "unchecked"})
	@RequestMapping(method=RequestMethod.PUT,value="/asignaturas")
	public ResponseEntity<?> asignacionAsignaturas(@RequestHeader(value="idProfesor",required=true)String idProfesor,
			@RequestHeader(value="nombreAsignatura",required=true)String nombreAsignatura,
			@RequestHeader(value="curso",required=true)Integer curso,
			@RequestHeader(value="etapa",required=true)String etapa,
			@RequestHeader(value="grupo",required=true)String grupo,HttpSession session)
	{
		try
		{
			Map<String,List<Asignatura>> asignacion = (Map<String, List<Asignatura>>) session.getAttribute("mapaAsignaturas");	
			List<Asignatura> datosAsignacion = new ArrayList<Asignatura>();
			List<Profesor> listaProfesores = (List<Profesor>) session.getAttribute("listaProfesores");
			List<Asignatura> listaAsignaturas = (List<Asignatura>) session.getAttribute("listaAsignaturas");
			List<Curso> listaCursos = (List<Curso>) session.getAttribute("listaCursos");
			Asignatura asignaturaObject = new Asignatura();
			Parse parse = new Parse();
			//metodo para validar los ids
			parse.ValidarIds(idProfesor, nombreAsignatura, listaProfesores, listaAsignaturas, asignaturaObject);
			//metodo para comprobar si el curso existe y creacion del objeto asignatura
			parse.ComprobacionCreacionObjeto(nombreAsignatura, curso, etapa, grupo, datosAsignacion, listaAsignaturas,listaCursos, asignaturaObject);
			//comprobamos si el mapa existe
			if (session.getAttribute("mapaAsignaturas") == null) 
			{
			    asignacion = new TreeMap<String, List<Asignatura>>();
			    asignacion.put(idProfesor, datosAsignacion);
			    session.setAttribute("mapaAsignaturas", asignacion);
			}
			else 
			{
			    asignacion = (Map<String, List<Asignatura>>) session.getAttribute("mapaAsignaturas");
			    //comprobamos si existe el idProfesor en el mapa
			    if (asignacion.containsKey(idProfesor)) 
			    {
			        List<Asignatura> existingAsignaturas = asignacion.get(idProfesor);
			        existingAsignaturas.addAll(datosAsignacion);
			    }
			    else
			    {
			        asignacion.put(idProfesor, datosAsignacion);
			    }
			    session.setAttribute("mapaAsignaturas", asignacion);
			}
			log.info(asignacion);
			return ResponseEntity.ok().body("Asignacion creada correctamente");
		}
		catch(HorarioException horarioException)
		{
			return ResponseEntity.status(410).body(horarioException.getBodyExceptionMessage());
		}
		catch(Exception exception)
		{
			String error = "Error desconocido";
			log.error(error,exception.getMessage());
			return ResponseEntity.status(400).body(exception.getMessage());
		}
	}
	
	/**
	 * endpoint para subir las reducciones
	 * @param csvFile
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.POST,value="/reducciones",consumes="multipart/form-data")
	public ResponseEntity<?> uploadReducciones(@RequestParam(value="csv",required=true)MultipartFile csvFile,HttpSession session)
	{
		try
		{
			//comprobamos si la lsita de cursos existe
			if(session.getAttribute("listaCursos")==null)
			{
				String error = "Los cursos no han sido cargados";
				throw new HorarioException(2,error);
			}
			List<Curso> listaCursos = (List<Curso>) session.getAttribute("listaCursos");
			
			Parse parse = new Parse();
			//llamamos el metodo para parsear reducciones
			List<Reduccion> listaReducciones = parse.parseReducciones(csvFile,listaCursos);
			//guardamos la lista en session
			session.setAttribute("listaReducciones", listaReducciones);
			log.info(listaReducciones);
			return ResponseEntity.ok().body("Reducciones subidas correctamente");
		}
		catch(HorarioException horarioException)
		{
			return ResponseEntity.status(410).body(horarioException.getBodyExceptionMessage());
		}
		catch(Exception exception)
		{
			String error = "Error desconocido";
			log.error(error,exception.getMessage());
			return ResponseEntity.status(400).body(exception.getMessage());
		}
	}
	
	/**
	 * endpoint para obtener lista de reducciones
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.GET,value="/reducciones")
	public ResponseEntity<?> consultaReducciones(HttpSession session)
	{
		try
		{
			List<Reduccion> listaReducciones;
			//comprobamos si la lista reducciones existe
			if(session.getAttribute("listaReducciones")!=null)
			{
				listaReducciones = (List<Reduccion>) session.getAttribute("listaReducciones");
			}
			else 
			{
				String error = "Las reducciones no han sido cargados en sesion todavía";
				throw new HorarioException(1,error);
			}
			log.info(listaReducciones);
			return ResponseEntity.ok().body(listaReducciones);
		}
		catch(HorarioException horarioException)
		{
			String error = "Error de carga de datos";
			log.error(error,horarioException.getMessage());
			return ResponseEntity.status(410).body(horarioException.getBodyExceptionMessage());
			
		}
		catch(Exception exception)
		{
			String error = "Error desconocido";
			log.error(error,exception.getMessage());
			return ResponseEntity.status(400).body(error);
		}
	}
	
	/**
	 * endpoint para asignar reducciones a un profesor
	 * @param idProfesor
	 * @param idReduccion
	 * @param session
	 * @return
	 */
	@SuppressWarnings({ "unchecked"})
	@RequestMapping(method=RequestMethod.PUT,value="/reducciones")
	public ResponseEntity<?> asignacionReducciones(@RequestHeader(value="idProfesor",required=true)String idProfesor,
			@RequestHeader(value="idReduccion",required=true)String idReduccion,HttpSession session)
	{
		try
		{
			Map<String,List<ReduccionHoras>> asignacionReduccion = (Map<String, List<ReduccionHoras>>) session.getAttribute("mapaReduccion");	
			
			List<Profesor> listaProfesores = (List<Profesor>) session.getAttribute("listaProfesores");
			List<Reduccion> listaReducciones = (List<Reduccion>) session.getAttribute("listaReducciones");
			List<ReduccionHoras> listaReduccionHoras = new ArrayList<ReduccionHoras>();
			boolean idProfesorExiste = false;
			//recorremos la lista de profesores para comprobar si existe el idprofesor que recibimos
			for(Profesor profesor : listaProfesores)
			{
				if(profesor.getIdProfesor().equals(idProfesor))
				{
					idProfesorExiste = true;
				}
			}
			if(!idProfesorExiste)
			{
				String error = "Profesor no encontrado";
				log.info(error);
				throw new HorarioException(13,error);
			}
			
			boolean idReduccionExiste = false;
			//recorremos la lista reducciones para comprobar si existe el idreduccion que recibimos
			for(Reduccion reduccion : listaReducciones)
			{
				if(reduccion.getIdReduccion().equals(idReduccion))
				{
					idReduccionExiste = true;
				}
			}
			if(!idReduccionExiste)
			{
				String error = "Reduccion no encontrada";
				log.info(error);
				throw new HorarioException(13,error);
			}
			Parse parse = new Parse();
			//llamamos al metodo realizarReduccion
			asignacionReduccion = parse.realizarReduccion(idProfesor, idReduccion, session, listaReducciones, listaReduccionHoras);
			
			log.info(asignacionReduccion);
			return ResponseEntity.ok().body("Asignacion de reduccion creada correctamente");
		}
		catch(HorarioException horarioException)
		{
			return ResponseEntity.status(410).body(horarioException.getBodyExceptionMessage());
		}
		catch(Exception exception)
		{
			String error = "Error desconocido";
			log.error(error,exception.getMessage());
			return ResponseEntity.status(400).body(exception.getMessage());
		}
	}

	
	/**
	 * endpoint para asignar horas de guardias a un profesor
	 * @param idProfesor
	 * @param horasAsignadas
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.POST,value="/guardias")
	public ResponseEntity<?> uploadGuardias(@RequestHeader(value="idProfesor",required=true)String idProfesor,
			@RequestHeader(value="horasAsignadas",required=true)Integer horasAsignadas,HttpSession session)
	{
		try
		{
			List<Profesor> listaProfesores = (List<Profesor>) session.getAttribute("listaProfesores");
			boolean idProfesorExiste = false;
			//bucle para comprobar si existe el idProfesor recibido
			for(Profesor profesor : listaProfesores)
			{
				if(profesor.getIdProfesor().equals(idProfesor))
				{
					idProfesorExiste = true;
				}
			}
			if(!idProfesorExiste)
			{
				String error = "Profesor no encontrado";
				log.info(error);
				throw new HorarioException(13,error);
			}
			
			Map<String,Integer> mapaGuardias;
			//comprobamos si se ha creado el mapa de guardias
			if(session.getAttribute("mapaGuardias")==null)
			{
				mapaGuardias = new TreeMap<String, Integer>();
				mapaGuardias.put(idProfesor, horasAsignadas);
				session.setAttribute("mapaGuardias", mapaGuardias);
			}
			else
			{
				mapaGuardias=(Map<String, Integer>) session.getAttribute("mapaGuardias");
				mapaGuardias.put(idProfesor, horasAsignadas);
				session.setAttribute("mapaGuardias", mapaGuardias);
			}
			
			log.info(mapaGuardias);
			return ResponseEntity.ok().body("Guardia subida correctamente");
		}
		catch(HorarioException horarioException)
		{
			return ResponseEntity.status(410).body(horarioException.getBodyExceptionMessage());
		}
		catch(Exception exception)
		{
			String error = "Error desconocido";
			log.error(error,exception.getMessage());
			return ResponseEntity.status(400).body(exception.getMessage());
		}
	}
	
	/**
	 * endpoint para obtener un resemun de un profesor
	 * @param idProfesor
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.GET,value="/profesores/resumen")
	public ResponseEntity<?> resumenProfesores(@RequestHeader(value="idProfesor",required=true)String idProfesor,HttpSession session)
	{
		try
		{
			
			List<Profesor> listaProfesores = (List<Profesor>) session.getAttribute("listaProfesores");
			boolean idProfesorExiste = false;
			//bucle para comprobar si existe el idProfesor
			for(Profesor profesor : listaProfesores)
			{
				if(profesor.getIdProfesor().equals(idProfesor))
				{
					idProfesorExiste = true;
				}
			}
			if(!idProfesorExiste)
			{
				String error = "Profesor no encontrado";
				log.info(error);
				throw new HorarioException(13,error);
			}
			Parse parse = new Parse();
			//llamamos el metodo resumenProfesor
			ResumenProfesor resumen = parse.resumenProfesor(idProfesor, session);
			return ResponseEntity.ok().body(resumen);
		}
		catch(HorarioException horarioException)
		{
			return ResponseEntity.status(410).body(horarioException.getBodyExceptionMessage());
		}
		catch(Exception exception)
		{
			String error = "Error desconocido";
			log.error(error,exception.getMessage());
			return ResponseEntity.status(400).body(exception.getMessage());
		}
	}

	
	
	/**
	 * endpoint para obtener un resumen por departamento
	 * @param nombreDepartamento
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.GET,value="/departamentos/resumen")
	public ResponseEntity<?> resumenDepartamento(@RequestHeader(value="nombreDepartamento",required=true)String nombreDepartamento,HttpSession session)
	{
		try
		{
			int numeroProfesorDepartamento = 0;
			int totalHoras=0;
			//obtenemos la lista departamentos en session
			List<Departamento> listaDepartamentos = (List<Departamento>) session.getAttribute("listaDepartamentos");
			Parse parse = new Parse();
			Departamento departamento = new Departamento(nombreDepartamento);
			//comprobamos si existe el departamento
			if(!listaDepartamentos.contains(departamento))
			{
				String error = "Departamento no encontrado";
				log.info(error);
				throw new HorarioException(13,error);
			}
			
			Resumen resumen = parse.resumenDepartamento(nombreDepartamento, session, numeroProfesorDepartamento, totalHoras);	
			return ResponseEntity.ok().body(resumen);
		}
		catch(HorarioException horarioException)
		{
			return ResponseEntity.status(410).body(horarioException.getMessage());
		}
		catch(Exception exception)
		{
			String error = "Error desconocido";
			log.error(error,exception.getMessage());
			return ResponseEntity.status(400).body(exception.getMessage());
		}
	}
}
