package com.carga.portal.modelo;

import java.util.List;

public class Tarea {
	
	private Integer codigo;
	private Proyecto proyecto;
	private Proceso proceso;
	private String paquete;
	private String clase; 
	private String plantilla;
	private boolean flujoCompletar = false;
	private boolean flujoGrabar = false;
	private boolean flujoSalir = false;
	private Consulta consultaTrabajar;
	private String aleas;
	private String dataSource;
	private String sufijoBanca;
	private String sufijoProducto;
	private String sufijoProceso;
	private String nombre;
	private String webParametroComentario;
	private String webParametroAccion;
	private ConsultaTareaCompletar consultaCompletar;
	private String version;
	private int tiempoRojo;
	private int tiempoAmarillo;
	private boolean adjuntaArchivos; 
	private boolean adjuntaArchivosAdicionales;
	private boolean observarFuncion; 
	private boolean subsanarFuncion; 
	private List<CampoSQLTarea> camposTrabajar;
	private List<CampoSQLTarea> camposCompletar;
	private List<CampoSQLTarea> camposCancelar;
	private List<CampoSQLTarea> camposRechazar;
	private List<CampoSQLTarea> camposObservar;
	private List<AtributoTarea> atributosTrabajar;
	private List<AtributoTarea> atributosCompletar;
	private List<AtributoTarea> atributosCancelar;
	private List<AtributoTarea> atributosRechazar;
	private List<AtributoTarea> atributosObservar;
	
	private List<AtributoTarea> atributosTrabajarSinPK;
	private List<AtributoTarea> atributosCompletarSinPK;
	private List<AtributoTarea> atributosCancelarSinPK;
	private List<AtributoTarea> atributosRechazarSinPK;
	private List<AtributoTarea> atributosObservarSinPK;
	
	private Clase clasePadre;
	private List<Clase> clasesCompletar;
	private List<Clase> clasesCancelar;
	private List<Clase> clasesRechazar;
	private List<Clase> clasesObservar;
	private List<TablaTarea> tablasTrabajar;
	private List<ObjetoBPM> objetosBPMCompletar;
	private List<ObjetoBPM> objetosBPMTrabajar;
	private String webNombreConfiguracion;
	private List<AtributoTarea> atributosCompletarValidacionWeb;
	
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public Proyecto getProyecto() {
		return proyecto;
	}
	public void setProyecto(Proyecto proyecto) {
		this.proyecto = proyecto;
	}
	public Proceso getProceso() {
		return proceso;
	}
	public void setProceso(Proceso proceso) {
		this.proceso = proceso;
	}
	public String getPaquete() {
		return paquete;
	}
	public void setPaquete(String paquete) {
		this.paquete = paquete;
	}
	public String getClase() {
		return clase;
	}
	public void setClase(String clase) {
		this.clase = clase;
	}
	public String getPlantilla() {
		return plantilla;
	}
	public void setPlantilla(String plantilla) {
		this.plantilla = plantilla;
	}
	public boolean isFlujoCompletar() {
		return flujoCompletar;
	}
	public void setFlujoCompletar(boolean flujoCompletar) {
		this.flujoCompletar = flujoCompletar;
	}
	public boolean isFlujoGrabar() {
		return flujoGrabar;
	}
	public void setFlujoGrabar(boolean flujoGrabar) {
		this.flujoGrabar = flujoGrabar;
	}
	public boolean isFlujoCancelar() {
		if(this.getAtributosCancelar()!=null && this.getAtributosCancelar().size()>0){
			return true;
		}else{
			return false;
		}
	}
	public boolean isFlujoObservar() {
		if(this.getAtributosObservar()!=null && this.getAtributosObservar().size()>0){
			return true;
		}else{
			return false;
		}
	}
	public boolean isFlujoRechazar() {
		if(this.getAtributosRechazar()!=null && this.getAtributosRechazar().size()>0){
			return true;
		}else{
			return false;
		}
	}
	public boolean isFlujoSalir() {
		return flujoSalir;
	}
	public void setFlujoSalir(boolean flujoSalir) {
		this.flujoSalir = flujoSalir;
	}
	public Consulta getConsultaTrabajar() {
		return consultaTrabajar;
	}
	public void setConsultaTrabajar(Consulta consultaTrabajar) {
		this.consultaTrabajar = consultaTrabajar;
	}
	public String getAleas() {
		return aleas;
	}
	public void setAleas(String aleas) {
		this.aleas = aleas;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public String getSufijoBanca() {
		return sufijoBanca;
	}
	public void setSufijoBanca(String sufijoBanca) {
		this.sufijoBanca = sufijoBanca;
	}
	public String getSufijoProducto() {
		return sufijoProducto;
	}
	public void setSufijoProducto(String sufijoProducto) {
		this.sufijoProducto = sufijoProducto;
	}
	public String getSufijoProceso() {
		return sufijoProceso;
	}
	public void setSufijoProceso(String sufijoProceso) {
		this.sufijoProceso = sufijoProceso;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getWebParametroComentario() {
		return webParametroComentario;
	}
	public void setWebParametroComentario(String webParametroComentario) {
		this.webParametroComentario = webParametroComentario;
	}
	public String getWebParametroAccion() {
		return webParametroAccion;
	}
	public void setWebParametroAccion(String webParametroAccion) {
		this.webParametroAccion = webParametroAccion;
	}
	public ConsultaTareaCompletar getConsultaCompletar() {
		return consultaCompletar;
	}
	public void setConsultaCompletar(ConsultaTareaCompletar consultaCompletar) {
		this.consultaCompletar = consultaCompletar;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getTiempoRojo() {
		return tiempoRojo;
	}
	public void setTiempoRojo(int tiempoRojo) {
		this.tiempoRojo = tiempoRojo;
	}
	public int getTiempoAmarillo() {
		return tiempoAmarillo;
	}
	public void setTiempoAmarillo(int tiempoAmarillo) {
		this.tiempoAmarillo = tiempoAmarillo;
	}
	public boolean isAdjuntaArchivos() {
		return adjuntaArchivos;
	}
	public void setAdjuntaArchivos(boolean adjuntaArchivos) {
		this.adjuntaArchivos = adjuntaArchivos;
	}
	public boolean isAdjuntaArchivosAdicionales() {
		return adjuntaArchivosAdicionales;
	}
	public void setAdjuntaArchivosAdicionales(boolean adjuntaArchivosAdicionales) {
		this.adjuntaArchivosAdicionales = adjuntaArchivosAdicionales;
	}
	public boolean isObservarFuncion() {
		return observarFuncion;
	}
	public void setObservarFuncion(boolean observarFuncion) {
		this.observarFuncion = observarFuncion;
	}
	public boolean isSubsanarFuncion() {
		return subsanarFuncion;
	}
	public void setSubsanarFuncion(boolean subsanarFuncion) {
		this.subsanarFuncion = subsanarFuncion;
	}
	public List<CampoSQLTarea> getCamposCompletar() {
		return camposCompletar;
	}
	public void setCamposCompletar(List<CampoSQLTarea> camposCompletar) {
		this.camposCompletar = camposCompletar;
	}
	public List<CampoSQLTarea> getCamposCancelar() {
		return camposCancelar;
	}
	public void setCamposCancelar(List<CampoSQLTarea> camposCancelar) {
		this.camposCancelar = camposCancelar;
	}
	public List<CampoSQLTarea> getCamposRechazar() {
		return camposRechazar;
	}
	public void setCamposRechazar(List<CampoSQLTarea> camposRechazar) {
		this.camposRechazar = camposRechazar;
	}
	public List<CampoSQLTarea> getCamposObservar() {
		return camposObservar;
	}
	public void setCamposObservar(List<CampoSQLTarea> camposObservar) {
		this.camposObservar = camposObservar;
	}
	public List<AtributoTarea> getAtributosTrabajar() {
		return atributosTrabajar;
	}
	public void setAtributosTrabajar(List<AtributoTarea> atributosTrabajar) {
		this.atributosTrabajar = atributosTrabajar;
	}
	public List<AtributoTarea> getAtributosCompletar() {
		return atributosCompletar;
	}
	public void setAtributosCompletar(List<AtributoTarea> atributosCompletar) {
		this.atributosCompletar = atributosCompletar;
	}
	public List<AtributoTarea> getAtributosObservar() {
		return atributosObservar;
	}
	public void setAtributosObservar(List<AtributoTarea> atributosObservar) {
		this.atributosObservar = atributosObservar;
	}
	public List<AtributoTarea> getAtributosCancelar() {
		return atributosCancelar;
	}
	public void setAtributosCancelar(List<AtributoTarea> atributosCancelar) {
		this.atributosCancelar = atributosCancelar;
	}
	public List<AtributoTarea> getAtributosRechazar() {
		return atributosRechazar;
	}
	public void setAtributosRechazar(List<AtributoTarea> atributosRechazar) {
		this.atributosRechazar = atributosRechazar;
	}
	public List<Clase> getClasesCompletar() {
		return clasesCompletar;
	}
	public void setClasesCompletar(List<Clase> clasesCompletar) {
		this.clasesCompletar = clasesCompletar;
	}
	public List<Clase> getClasesCancelar() {
		return clasesCancelar;
	}
	public void setClasesCancelar(List<Clase> clasesCancelar) {
		this.clasesCancelar = clasesCancelar;
	}
	public List<Clase> getClasesRechazar() {
		return clasesRechazar;
	}
	public void setClasesRechazar(List<Clase> clasesRechazar) {
		this.clasesRechazar = clasesRechazar;
	}
	public List<Clase> getClasesObservar() {
		return clasesObservar;
	}
	public void setClasesObservar(List<Clase> clasesObservar) {
		this.clasesObservar = clasesObservar;
	}
	public List<TablaTarea> getTablasTrabajar() {
		return tablasTrabajar;
	}
	public void setTablasTrabajar(List<TablaTarea> tablasTrabajar) {
		this.tablasTrabajar = tablasTrabajar;
	}
	public List<ObjetoBPM> getObjetosBPMCompletar() {
		return objetosBPMCompletar;
	}
	public void setObjetosBPMCompletar(List<ObjetoBPM> objetosBPMCompletar) {
		this.objetosBPMCompletar = objetosBPMCompletar;
	}
	public List<ObjetoBPM> getObjetosBPMTrabajar() {
		return objetosBPMTrabajar;
	}
	public void setObjetosBPMTrabajar(List<ObjetoBPM> objetosBPMTrabajar) {
		this.objetosBPMTrabajar = objetosBPMTrabajar;
	}
	public String getWebNombreConfiguracion() {
		return webNombreConfiguracion;
	}
	public void setWebNombreConfiguracion(String webNombreConfiguracion) {
		this.webNombreConfiguracion = webNombreConfiguracion;
	}
	public List<AtributoTarea> getAtributosCompletarValidacionWeb() {
		return atributosCompletarValidacionWeb;
	}
	public void setAtributosCompletarValidacionWeb(
			List<AtributoTarea> atributosCompletarValidacionWeb) {
		this.atributosCompletarValidacionWeb = atributosCompletarValidacionWeb;
	}
	public Clase getClasePadre() {
		return clasePadre;
	}
	public void setClasePadre(Clase clasePadre) {
		this.clasePadre = clasePadre;
	}
	public List<CampoSQLTarea> getCamposTrabajar() {
		return camposTrabajar;
	}
	public void setCamposTrabajar(List<CampoSQLTarea> camposTrabajar) {
		this.camposTrabajar = camposTrabajar;
	}
	public List<AtributoTarea> getAtributosTrabajarSinPK() {
		return atributosTrabajarSinPK;
	}
	public void setAtributosTrabajarSinPK(List<AtributoTarea> atributosTrabajarSinPK) {
		this.atributosTrabajarSinPK = atributosTrabajarSinPK;
	}
	public List<AtributoTarea> getAtributosCompletarSinPK() {
		return atributosCompletarSinPK;
	}
	public void setAtributosCompletarSinPK(
			List<AtributoTarea> atributosCompletarSinPK) {
		this.atributosCompletarSinPK = atributosCompletarSinPK;
	}
	public List<AtributoTarea> getAtributosCancelarSinPK() {
		return atributosCancelarSinPK;
	}
	public void setAtributosCancelarSinPK(List<AtributoTarea> atributosCancelarSinPK) {
		this.atributosCancelarSinPK = atributosCancelarSinPK;
	}
	public List<AtributoTarea> getAtributosRechazarSinPK() {
		return atributosRechazarSinPK;
	}
	public void setAtributosRechazarSinPK(List<AtributoTarea> atributosRechazarSinPK) {
		this.atributosRechazarSinPK = atributosRechazarSinPK;
	}
	public List<AtributoTarea> getAtributosObservarSinPK() {
		return atributosObservarSinPK;
	}
	public void setAtributosObservarSinPK(List<AtributoTarea> atributosObservarSinPK) {
		this.atributosObservarSinPK = atributosObservarSinPK;
	}
	
	
}