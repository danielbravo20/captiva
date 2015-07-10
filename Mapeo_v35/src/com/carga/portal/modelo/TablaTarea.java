package com.carga.portal.modelo;

public class TablaTarea extends Tabla{

	private boolean flgRequiereEliminar;
	private boolean flgListado;
	
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
	
}
