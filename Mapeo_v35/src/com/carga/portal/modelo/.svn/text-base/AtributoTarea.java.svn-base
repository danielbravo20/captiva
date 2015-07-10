package com.carga.portal.modelo;

public class AtributoTarea extends Atributo{
	
	private boolean bpmListado;
	private boolean requiereEliminar;
	private boolean requiereValidacion;
	private String webMensajeValidacion;
	private String webValorOmision;
	private String webFlgEntrada;
	private String nombreCatalogo;
	private CampoSQLTarea campoSQLTarea;
	private String procesoObjeto;
	private String procesoObjetoTipoDato;
	private String procesoObjetoAtributo;
	private int    webTabCampo;
	
	public boolean isBpmListado() {
		return bpmListado;
	}
	public void setBpmListado(boolean bpmListado) {
		this.bpmListado = bpmListado;
	}
	public boolean isRequiereEliminar() {
		return requiereEliminar;
	}
	public void setRequiereEliminar(boolean requiereEliminar) {
		this.requiereEliminar = requiereEliminar;
	}

	public String getWebValorOmision() {
		return webValorOmision;
	}
	public void setWebValorOmision(String webValorOmision) {
		this.webValorOmision = webValorOmision;
	}
	public String getNombreCatalogo() {
		return nombreCatalogo;
	}
	public void setNombreCatalogo(String nombreCatalogo) {
		this.nombreCatalogo = nombreCatalogo;
	}
	public boolean isRequiereValidacion() {
		return requiereValidacion;
	}
	public void setRequiereValidacion(boolean requiereValidacion) {
		this.requiereValidacion = requiereValidacion;
	}
	public String getWebMensajeValidacion() {
		return webMensajeValidacion;
	}
	public void setWebMensajeValidacion(String webMensajeValidacion) {
		this.webMensajeValidacion = webMensajeValidacion;
	}
	
	public CampoSQLTarea getCampoSQLTarea() {
		return campoSQLTarea;
	}
	public void setCampoSQLTarea(CampoSQLTarea campoSQLTarea) {
		this.campoSQLTarea = campoSQLTarea;
	}
	public boolean isFlgExisteCampoSQL(){
		if(getCampoSQLTarea()!=null && getCampoSQLTarea().getCodigo()>0){
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
	public String getProcesoObjeto() {
		return procesoObjeto;
	}
	public void setProcesoObjeto(String procesoObjeto) {
		this.procesoObjeto = procesoObjeto;
	}
	public String getProcesoObjetoTipoDato() {
		return procesoObjetoTipoDato;
	}
	public void setProcesoObjetoTipoDato(String procesoObjetoTipoDato) {
		this.procesoObjetoTipoDato = procesoObjetoTipoDato;
	}
	public String getProcesoObjetoAtributo() {
		return procesoObjetoAtributo;
	}
	public void setProcesoObjetoAtributo(String procesoObjetoAtributo) {
		this.procesoObjetoAtributo = procesoObjetoAtributo;
	}
	public boolean isProcesoAtributoTieneBO(){
		if(this.getProcesoObjeto()!=null && 
				this.getProcesoObjeto().trim().length()>0 &&
				this.getProcesoObjetoAtributo()!=null && 
				this.getProcesoObjetoAtributo().length()>0){
			return true;
		}
		return false;
	}
	public void setWebFlgEntrada(String webFlgEntrada) {
		this.webFlgEntrada = webFlgEntrada;
	}
	public String getWebFlgEntrada() {
		return webFlgEntrada;
	}
	public boolean isExiteObjetoBPM(){
		if(procesoObjetoAtributo!=null && procesoObjetoAtributo.length()>0){
			return true;
		}
		return false;
	}
	public int getWebTabCampo() {
		return webTabCampo;
	}
	public void setWebTabCampo(int webTabCampo) {
		this.webTabCampo = webTabCampo;
	}
	
}
