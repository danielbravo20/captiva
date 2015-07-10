package com.carga.mapeo.dao.impl;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.carga.mapeo.dao.ConexionBD;
import com.carga.portal.modelo.Catalogo;
import com.carga.portal.modelo.Configuracion;
import com.carga.portal.modelo.Perfil;
import com.carga.portal.modelo.PerfilModulo;
import com.carga.portal.modelo.PerfilSubModuloProducto;
import com.carga.portal.modelo.Producto;
import com.carga.portal.modelo.Proyecto;
import com.carga.portal.modelo.SubModulo;
import com.carga.portal.modelo.UnidadNegocio;

public class ProyectoDaoImpl extends ConexionBD{
	
	public Proyecto obtenerProyecto(String codigoProyecto, Connection conn){
		Proyecto proyecto = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COD_PROYECTO, NOMBRE, JAV_PRO_LIBRERIA, JAV_PRO_EJB, JAV_PRO_EJB_EXT, JAV_PRO_CLI_EJB, JAV_PRO_WEB, JAV_PAQUETE, JAV_PAQ_CONTROLADOR, JAV_PRE_CONTROLADOR FROM BFP_CARTA_FIANZA.PROYECTO WHERE COD_PROYECTO = "+codigoProyecto+"");
			while (rs.next()) {
				proyecto = new Proyecto();
				proyecto.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
				proyecto.setNombre(rs.getString("NOMBRE"));
				proyecto.setJavaNombreProyectoLibreria(rs.getString("JAV_PRO_LIBRERIA"));
				proyecto.setJavaNombreProyectoEJB(rs.getString("JAV_PRO_EJB"));
				proyecto.setJavaNombreProyectoEJBExtension(rs.getString("JAV_PRO_EJB_EXT"));
				proyecto.setJavaNombreProyectoEJBCliente(rs.getString("JAV_PRO_CLI_EJB"));
				proyecto.setJavaNombreProyectoWeb(rs.getString("JAV_PRO_WEB"));
				proyecto.setJavaNombrePaquete(rs.getString("JAV_PAQUETE"));
				proyecto.setJavaNombrePaqueteControlador(rs.getString("JAV_PAQ_CONTROLADOR"));
				proyecto.setJavaPreSufijoControlador(rs.getString("JAV_PRE_CONTROLADOR"));
				proyecto.setCatalogos(obtenerCatalogos(proyecto, conn));
			 }
			
	    } catch ( Exception e ) {
	      e.printStackTrace();
	      System.exit(0);
	    } finally {
	    	try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
		return proyecto;
	}
	
	public Configuracion obtenerConfiguracion(Proyecto proyecto, String usuario, Connection conn) throws Exception{
		Configuracion configuracion = null;
		Statement stmt = null;
		ResultSet rs = null;
			
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COD_PROYECTO, USUARIO, RUTA_WORKSPACE, RUTA_SCRIPT_SQL FROM BFP_CARTA_FIANZA.CONFIGURACION WHERE COD_PROYECTO = "+proyecto.getCodigoProyecto()+" AND USUARIO = '"+usuario+"'");
			while (rs.next()) {
				configuracion = new Configuracion();
				configuracion.setUsuarioCreacion(rs.getString("USUARIO"));
				
				String ruta = rs.getString("RUTA_WORKSPACE");
				File rutaFile = new File(ruta);
				if(rutaFile.exists() && rutaFile.isDirectory()){
					configuracion.setDirectorioWorkspace(rutaFile);
					configuracion.setDirectorioEJB(new File(rutaFile.getAbsolutePath()+"\\"+proyecto.getJavaNombreProyectoEJB()));
					configuracion.setDirectorioEJBExt(new File(rutaFile.getAbsolutePath()+"\\"+proyecto.getJavaNombreProyectoEJBExtension()));
					configuracion.setDirectorioEJBCliente(new File(rutaFile.getAbsolutePath()+"\\"+proyecto.getJavaNombreProyectoEJBCliente()));
					configuracion.setDirectorioLib(new File(rutaFile.getAbsolutePath()+"\\"+proyecto.getJavaNombreProyectoLibreria()));
					configuracion.setDirectorioWEB(new File(rutaFile.getAbsolutePath()+"\\"+proyecto.getJavaNombreProyectoWeb()));
					
					if(configuracion.getDirectorioEJB().exists() && configuracion.getDirectorioEJB().isDirectory()){
						System.out.println("...directorio: "+configuracion.getDirectorioEJB().getAbsolutePath());
					}else{
						throw new Exception("la ruta: '"+configuracion.getDirectorioEJB().getAbsolutePath()+"', no existe o no es un directorio...");
					}
					
					if(configuracion.getDirectorioEJBExt().exists() && configuracion.getDirectorioEJBExt().isDirectory()){
						System.out.println("...directorio: "+configuracion.getDirectorioEJBExt().getAbsolutePath());
					}else{
						throw new Exception("la ruta: '"+configuracion.getDirectorioEJB().getAbsolutePath()+"', no existe o no es un directorio...");
					}
					
					if(configuracion.getDirectorioEJBCliente().exists() && configuracion.getDirectorioEJBCliente().isDirectory()){
						System.out.println("...directorio: "+configuracion.getDirectorioEJBCliente().getAbsolutePath());
					}else{
						throw new Exception("la ruta: '"+configuracion.getDirectorioEJBCliente().getAbsolutePath()+"', no existe o no es un directorio...");
					}
					
					if(configuracion.getDirectorioLib().exists() && configuracion.getDirectorioLib().isDirectory()){
						System.out.println("...directorio: "+configuracion.getDirectorioLib().getAbsolutePath());
					}else{
						throw new Exception("la ruta: '"+configuracion.getDirectorioLib().getAbsolutePath()+"', no existe o no es un directorio...");
					}
					
					if(configuracion.getDirectorioWEB().exists() && configuracion.getDirectorioWEB().isDirectory()){
						System.out.println("...directorio: "+configuracion.getDirectorioWEB().getAbsolutePath());
					}else{
						throw new Exception("la ruta: '"+configuracion.getDirectorioWEB().getAbsolutePath()+"', no existe o no es un directorio...");
					}
					
				}else{
					throw new Exception("la ruta: '"+ruta+"', no existe o no es un directorio...");
				}
				
				String rutaSQL = rs.getString("RUTA_SCRIPT_SQL");
				File rutaFileSQL = new File(rutaSQL);
				if(rutaFileSQL.exists() && rutaFileSQL.isDirectory()){
					configuracion.setDirectorioSQL(rutaFileSQL);
				}else{
					throw new Exception("la ruta: '"+ruta+"', no existe o no es un directorio...");
				}
			 }
			
	    } catch ( Exception e ) {
	      e.printStackTrace();
	      System.exit(0);
	    } finally {
	    	try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
		return configuracion;
	}
	
	public List<UnidadNegocio> obtenerUnidadesNegocio(Connection conn){
		List<UnidadNegocio> unidades = new ArrayList<UnidadNegocio>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT COD_UNI_NEGOCIO, NOMBRE FROM BFP_CARTA_FIANZA.MAE_UNI_NEGOCIO";
			cs = conn.prepareCall(sql);
			rs = cs.executeQuery();

			UnidadNegocio bean = null;
			while (rs.next()) {
				bean = new UnidadNegocio();
				bean.setCodigo(rs.getString("COD_UNI_NEGOCIO"));
				bean.setDescripcion(rs.getString("NOMBRE"));
				unidades.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null){
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return unidades;
	}
	 
	public List<Catalogo> obtenerCatalogos(Proyecto proyecto, Connection conn){
		List<Catalogo> catalogos = new ArrayList<Catalogo>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT COD_CATALOGO, COD_ATRIBUTO, VALOR_1, VALOR_2, DESCRIPCION, LIM_COD_ATRIBUTO, LIM_VALOR_1, LIM_VALOR_2, CABECERA FROM BFP_CARTA_FIANZA.CATALOGO WHERE COD_PROYECTO = "+proyecto.getCodigoProyecto();
			cs = conn.prepareCall(sql);
			rs = cs.executeQuery();

			
			while (rs.next()) {
				Catalogo bean = new Catalogo();
				bean.setCodigoCatalogo(rs.getString("COD_CATALOGO"));
				bean.setCodigoAtributo(rs.getString("COD_ATRIBUTO"));
				bean.setValor1(rs.getString("VALOR_1"));
				bean.setValor2(rs.getString("VALOR_2"));
				bean.setDescripcion(rs.getString("DESCRIPCION"));
				bean.setLimiteCodAtributo(rs.getInt("LIM_COD_ATRIBUTO"));
				bean.setLimiteValor1(rs.getInt("LIM_VALOR_1"));
				bean.setLimiteValor2(rs.getInt("LIM_VALOR_2"));
				bean.setCabecera(rs.getBoolean("CABECERA"));
				catalogos.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null){
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return catalogos;
	}
	
	//TODO Edwin 
	public List<Producto> obtenerProductos(Proyecto proyecto){
		List<Producto> productos = new ArrayList<Producto>();
		
		
		
		return productos;
	}
	
	//TODO Edwin 
	public Producto obtenerProducto(String codigoProducto){
		Producto producto = new Producto();
		
		
		return producto;
	}
	
	//TODO Edwin 
	public List<Perfil> obtenerPerfiles(Proyecto proyecto){
		List<Perfil> productos = new ArrayList<Perfil>();
		
		
		
		return productos;
	}

	//TODO Edwin 
	public List<PerfilModulo> obtenerPerfilModulos(Proyecto proyecto){
		List<PerfilModulo> perfilModulos = new ArrayList<PerfilModulo>();
		
		
		return perfilModulos;
	}
	
	//TODO Edwin 
	public List<SubModulo> obtenerSubModulos(Proyecto proyecto){
		List<SubModulo> subModulos = new ArrayList<SubModulo>();
		
		
		return subModulos;
	}
	
	//TODO Edwin 
	public List<PerfilSubModuloProducto> obtenerPerfilSubModuloProductos(Proyecto proyecto){
		List<PerfilSubModuloProducto> perfilSubModuloProductos = new ArrayList<PerfilSubModuloProducto>();
		
		
		
		return perfilSubModuloProductos;
	}
	
	
}
