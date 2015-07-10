package com.carga.portal.modelo;

public class CampoSQLTarea extends CampoSQL{

	private boolean flgRequiereEliminar;
	private boolean flgListado;
	private CampoSQLTarea fk;
	private AtributoTarea atributoTarea;
	
	public boolean isFlgRequiereEliminar() {
		return flgRequiereEliminar;
	}

	public void setFlgRequiereEliminar(boolean flgRequiereEliminar) {
		this.flgRequiereEliminar = flgRequiereEliminar;
	}

	public boolean isFlgListado() {
		return flgListado;
	}

	public void setFlgListado(boolean flgListado) {
		this.flgListado = flgListado;
	}

	public CampoSQLTarea getFk() {
		return fk;
	}

	public void setFk(CampoSQLTarea fk) {
		this.fk = fk;
	}

	public AtributoTarea getAtributoTarea() {
		return atributoTarea;
	}

	public void setAtributoTarea(AtributoTarea atributoTarea) {
		this.atributoTarea = atributoTarea;
	}
	
}
