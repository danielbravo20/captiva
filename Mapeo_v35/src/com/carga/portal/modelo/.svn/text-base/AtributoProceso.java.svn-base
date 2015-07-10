package com.carga.portal.modelo;

public class AtributoProceso extends Atributo{

	private int codigoProyecto;
	private int codigoProceso;
	private boolean bpmFlgEntrada;
	private String bpmObjetoReferencia;
	private boolean bpmFlgPIID;
	private boolean webFlgReferencia;
	private boolean webFlgValidacion;
	private String webMensajeValidacion;
	private String webValorOmision;
	private boolean webFlgRequerido;
	private String webNombreCatalogoCombo;
	private boolean sqlFlgAutogenerado;
	private String sqlNombreSecuencial;
	private String bpmObjeto;
	private String bpmTipo;
	private String bpmNombre;
	private CampoSQLProceso campoSQLProceso;
	
	public int getCodigoProyecto() {
		return codigoProyecto;
	}
	public void setCodigoProyecto(int codigoProyecto) {
		this.codigoProyecto = codigoProyecto;
	}
	public int getCodigoProceso() {
		return codigoProceso;
	}
	public void setCodigoProceso(int codigoProceso) {
		this.codigoProceso = codigoProceso;
	}
	public boolean isBpmFlgEntrada() {
		return bpmFlgEntrada;
	}
	public void setBpmFlgEntrada(boolean bpmFlgEntrada) {
		this.bpmFlgEntrada = bpmFlgEntrada;
	}
	public String getBpmObjetoReferencia() {
		return bpmObjetoReferencia;
	}
	public void setBpmObjetoReferencia(String bpmObjetoReferencia) {
		this.bpmObjetoReferencia = bpmObjetoReferencia;
	}
	public boolean isBpmFlgPIID() {
		return bpmFlgPIID;
	}
	public void setBpmFlgPIID(boolean bpmFlgPIID) {
		this.bpmFlgPIID = bpmFlgPIID;
	}
	public boolean isWebFlgReferencia() {
		return webFlgReferencia;
	}
	public void setWebFlgReferencia(boolean webFlgReferencia) {
		this.webFlgReferencia = webFlgReferencia;
	}
	public boolean isWebFlgValidacion() {
		return webFlgValidacion;
	}
	public void setWebFlgValidacion(boolean webFlgValidacion) {
		this.webFlgValidacion = webFlgValidacion;
	}
	public String getWebMensajeValidacion() {
		return webMensajeValidacion;
	}
	public void setWebMensajeValidacion(String webMensajeValidacion) {
		this.webMensajeValidacion = webMensajeValidacion;
	}
	public String getWebValorOmision() {
		return webValorOmision;
	}
	public void setWebValorOmision(String webValorOmision) {
		this.webValorOmision = webValorOmision;
	}
	public boolean isWebFlgRequerido() {
		return webFlgRequerido;
	}
	public void setWebFlgRequerido(boolean webFlgRequerido) {
		this.webFlgRequerido = webFlgRequerido;
	}
	public String getWebNombreCatalogoCombo() {
		return webNombreCatalogoCombo;
	}
	public void setWebNombreCatalogoCombo(String webNombreCatalogoCombo) {
		this.webNombreCatalogoCombo = webNombreCatalogoCombo;
	}
	public String getSqlNombreSecuencial() {
		return sqlNombreSecuencial;
	}
	public void setSqlNombreSecuencial(String sqlNombreSecuencial) {
		this.sqlNombreSecuencial = sqlNombreSecuencial;
	}
	public boolean isSqlFlgAutogenerado() {
		return sqlFlgAutogenerado;
	}
	public void setSqlFlgAutogenerado(boolean sqlFlgAutogenerado) {
		this.sqlFlgAutogenerado = sqlFlgAutogenerado;
	}
	
	public String getBpmObjeto() {
		return bpmObjeto;
	}
	public void setBpmObjeto(String bpmObjeto) {
		this.bpmObjeto = bpmObjeto;
	}
	public String getBpmTipo() {
		return bpmTipo;
	}
	public void setBpmTipo(String bpmTipo) {
		this.bpmTipo = bpmTipo;
	}
	public String getBpmNombre() {
		return bpmNombre;
	}
	public void setBpmNombre(String bpmNombre) {
		this.bpmNombre = bpmNombre;
	}
	public CampoSQLProceso getCampoSQLProceso() {
		return campoSQLProceso;
	}
	public void setCampoSQLProceso(CampoSQLProceso campoSQLProceso) {
		this.campoSQLProceso = campoSQLProceso;
	}

	public boolean isFlgExisteCampoSQL(){
		if(getCampoSQLProceso()!=null && getCampoSQLProceso().getCodigo()>0){
			return true;
		}
		return false;
	}
	
	public boolean isJavaTieneValorOmision(){
		if(this.webValorOmision!=null && this.webValorOmision.length()>0){
			return true;
		}
		return false;
	}	
	
	public boolean isProcesoTieneObjeto(){
		if(this.bpmObjetoReferencia!=null && this.bpmObjetoReferencia.length()>0){
			return true;
		}
		return false;
	}
}
