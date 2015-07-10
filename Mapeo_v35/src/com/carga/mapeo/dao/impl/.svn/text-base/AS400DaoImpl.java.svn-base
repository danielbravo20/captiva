package com.carga.mapeo.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.carga.mapeo.dao.ConexionBD;
import com.carga.portal.modelo.AS400In;
import com.carga.portal.modelo.AS400InLista;
import com.carga.portal.modelo.AS400Out;
import com.carga.portal.modelo.AS400OutLista;
import com.carga.portal.modelo.AS400Programa;
import com.carga.portal.modelo.Proyecto;

public class AS400DaoImpl extends ConexionBD{
	
	Integer inicioLista = 0;
	Integer inicioListaOut = 0;

	public List<AS400Programa> obtenerAS400Programa(Proyecto proyecto, Connection conn){
		List<AS400Programa> lista = null;
		
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT COD_PROGRAMA, NOMBRE, USUARIO, COD_TRANSACCION, BASE_NAMESPACE, PATH_REQUEST_IN, PATH_REQUEST_OUT, " +
					"PATH_RESPONSE_IN, PATH_RESPONSE_OUT, COD_PROYECTO " +
					"FROM BFP_CARTA_FIANZA.AS400_PROGRAMA WHERE COD_PROYECTO = ?";
			cs = conn.prepareCall(sql);
			cs.setInt(1, proyecto.getCodigoProyecto());
			rs = cs.executeQuery();

			AS400Programa bean = null;
			lista = new ArrayList<AS400Programa>();
			while (rs.next()) {
				bean = new AS400Programa();
				
				bean.setCodigoPrograma(rs.getString("COD_PROGRAMA"));
				bean.setNombre(rs.getString("NOMBRE"));
				bean.setUsuario(rs.getString("USUARIO"));
				bean.setTransaccion(rs.getString("COD_TRANSACCION"));
				bean.setBaseNameSpace(rs.getString("BASE_NAMESPACE"));
				bean.setPathRequestIn(rs.getString("PATH_REQUEST_IN"));
				bean.setPathRequestOut(rs.getString("PATH_REQUEST_OUT"));
				bean.setPathResponseIn(rs.getString("PATH_RESPONSE_IN"));
				bean.setPathResponseOut(rs.getString("PATH_RESPONSE_OUT"));
				bean.setAs400ins(obtenerInAS400Programa(bean.getCodigoPrograma(),conn));
				bean.setAs400outs(obtenerOutAS400Programa(bean.getCodigoPrograma(), conn));
				bean.setAs400inLista(obtenerInListaAS400Programa(bean.getCodigoPrograma(),conn));
				bean.setAs400outLista(obtenerOutListaAS400Programa(bean.getCodigoPrograma(),conn));
				
				lista.add(bean);
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
		
		return lista;
	}

	private List<AS400OutLista> obtenerOutListaAS400Programa(
			String codigoPrograma, Connection conn) {
		List<AS400OutLista> lista = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT DISTINCT NOM_ATRIBUTO, OBJETO_LISTA, TIMES, LONGITUDLISTA, ATRIBUTO_PROCESO, OBJETO_LISTA_DESTINO " +
					"FROM BFP_CARTA_FIANZA.AS400_IN " +
					"WHERE TIPO_ATRIBUTO = 'OUT' AND LENGTH(NVL(LONGITUDLISTA,''))>0 AND COD_PROGRAMA = ?";
			cs = conn.prepareCall(sql);
			cs.setString(1, codigoPrograma);
			rs = cs.executeQuery();

			AS400OutLista bean = null;
			lista = new ArrayList<AS400OutLista>();
			while (rs.next()) {
				bean = new AS400OutLista();
				
				bean.setNombre(rs.getString("NOM_ATRIBUTO"));
				bean.setTimes(rs.getInt("TIMES"));
				bean.setInicio(inicioListaOut);
				String lonlista = rs.getString("LONGITUDLISTA");
				bean.setLongitud(0);
				if (lonlista != null && !"".equals(lonlista)){
					bean.setLongitud(Integer.parseInt(lonlista));
				}
				
				inicioListaOut = inicioListaOut + bean.getLongitud();
				bean.setObjetoDestino(rs.getString("ATRIBUTO_PROCESO"));
				bean.setObjetoDestinoLista(rs.getString("OBJETO_LISTA_DESTINO"));
				bean.setAs400outs(obtenerOutAS400ListaPrograma(codigoPrograma,conn));
				
				lista.add(bean);
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
		
		return lista;
	}

	private List<AS400Out> obtenerOutAS400ListaPrograma(String codigoPrograma,
			Connection conn) {
		List<AS400Out> lista = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT COD_PROGRAMA, NOM_ATRIBUTO, TIPO_ATRIBUTO, LONGITUD, ORDEN, TIPO_DATO, FORMATO_DATE, ESCALA, DESCRIPCION, " +
					"VALOR_DEFECTO, ATRIBUTO_PROCESO, OBJETO_LISTA " +
					"FROM BFP_CARTA_FIANZA.AS400_IN WHERE TIPO_ATRIBUTO = 'OUT' AND (LENGTH(NVL(OBJETO_LISTA,'')) > 0) AND COD_PROGRAMA = ? ORDER BY ORDENLISTA ASC";
			cs = conn.prepareCall(sql);
			cs.setString(1, codigoPrograma);
			rs = cs.executeQuery();

			AS400Out bean = null;
			lista = new ArrayList<AS400Out>();
			Integer inicioSiguiente = 1;
			while (rs.next()) {
				bean = new AS400Out();
				
				bean.setCodigoPrograma(rs.getString("COD_PROGRAMA"));
				bean.setCodigoAtributo(rs.getString("NOM_ATRIBUTO"));
				bean.setLongitud(rs.getString("LONGITUD"));
				
				bean.setInicio(inicioSiguiente.toString() );
				Integer agrega = Integer.valueOf(bean.getLongitud());
				inicioSiguiente = inicioSiguiente + agrega;
				bean.setTipoDato(rs.getString("TIPO_DATO"));
				bean.setFormatoDate(rs.getString("FORMATO_DATE"));
				bean.setEscala(rs.getString("ESCALA"));
				bean.setDescripcion(rs.getString("DESCRIPCION"));
				bean.setValorDefecto(rs.getString("VALOR_DEFECTO"));
				bean.setAtributoDestino(rs.getString("ATRIBUTO_PROCESO"));
				
				lista.add(bean);
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
		
		return lista;
	}

	private List<AS400InLista> obtenerInListaAS400Programa(
			String codigoPrograma, Connection conn) {
		List<AS400InLista> lista = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT DISTINCT OBJETO_LISTA, TIMES, LONGITUDLISTA, ATRIBUTO_PROCESO " +
					"FROM BFP_CARTA_FIANZA.AS400_IN " +
					"WHERE TIPO_ATRIBUTO = 'IN' AND LENGTH(NVL(OBJETO_LISTA,''))>0  AND COD_PROGRAMA = ?";
			cs = conn.prepareCall(sql);
			cs.setString(1, codigoPrograma);
			rs = cs.executeQuery();

			AS400InLista bean = null;
			lista = new ArrayList<AS400InLista>();
			while (rs.next()) {
				bean = new AS400InLista();
				
				bean.setNombre(rs.getString("OBJETO_LISTA"));
				bean.setTimes(rs.getInt("TIMES"));
				bean.setInicio(inicioLista);
				bean.setLongitud(rs.getInt("LONGITUDLISTA"));
				inicioLista = inicioLista + bean.getLongitud();
				bean.setObjetoDestinoLista(rs.getString("ATRIBUTO_PROCESO"));
				bean.setAs400ins(obtenerInAS400ListaPrograma(codigoPrograma,conn));
				
				lista.add(bean);
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
		
		return lista;
	}

	private List<AS400In> obtenerInAS400Programa(String codigoPrograma,
			Connection conn) {
		List<AS400In> lista = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT COD_PROGRAMA, NOM_ATRIBUTO, TIPO_ATRIBUTO, LONGITUD, ORDEN, TIPO_DATO, FORMATO_DATE, ESCALA, DESCRIPCION, " +
					"VALOR_DEFECTO, ATRIBUTO_PROCESO, OBJETO_LISTA, LONGITUDLISTA " +
					"FROM BFP_CARTA_FIANZA.AS400_IN WHERE TIPO_ATRIBUTO = 'IN' AND COD_PROGRAMA = ? ORDER BY ORDEN ASC";
			cs = conn.prepareCall(sql);
			cs.setString(1, codigoPrograma);
			rs = cs.executeQuery();

			AS400In bean = null;
			lista = new ArrayList<AS400In>();
			Integer inicioSiguiente = 1;
			while (rs.next()) {
				bean = new AS400In();
				
				bean.setCodigoPrograma(rs.getString("COD_PROGRAMA"));
				bean.setCodigoAtributo(rs.getString("NOM_ATRIBUTO"));
				bean.setLongitud(rs.getString("LONGITUD"));
				
				bean.setInicio(inicioSiguiente.toString() );
				Integer agrega = Integer.valueOf(bean.getLongitud());
				inicioSiguiente = inicioSiguiente + agrega;
				inicioLista = inicioSiguiente; 
				bean.setTipoDato(rs.getString("TIPO_DATO"));
				bean.setFormatoDate(rs.getString("FORMATO_DATE"));
				bean.setEscala(rs.getString("ESCALA"));
				bean.setDescripcion(rs.getString("DESCRIPCION"));
				bean.setValorDefecto(rs.getString("VALOR_DEFECTO"));
				bean.setAtributoOrigen(rs.getString("ATRIBUTO_PROCESO"));
				bean.setLongitudLista(rs.getString("LONGITUDLISTA"));
				
				lista.add(bean);
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
		
		return lista;
	}
	
	private List<AS400In> obtenerInAS400ListaPrograma(String codigoPrograma,
			Connection conn) {
		List<AS400In> lista = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT COD_PROGRAMA, NOM_ATRIBUTO, TIPO_ATRIBUTO, LONGITUD, ORDEN, TIPO_DATO, FORMATO_DATE, ESCALA, DESCRIPCION, " +
					"VALOR_DEFECTO, ATRIBUTO_PROCESO, OBJETO_LISTA " +
					"FROM BFP_CARTA_FIANZA.AS400_IN WHERE TIPO_ATRIBUTO = 'IN' AND LENGTH(NVL(OBJETO_LISTA,''))>0 AND COD_PROGRAMA = ? ORDER BY ORDENLISTA ASC";
			cs = conn.prepareCall(sql);
			cs.setString(1, codigoPrograma);
			rs = cs.executeQuery();

			AS400In bean = null;
			lista = new ArrayList<AS400In>();
			Integer inicioSiguiente = 1;
			while (rs.next()) {
				bean = new AS400In();
				
				bean.setCodigoPrograma(rs.getString("COD_PROGRAMA"));
				bean.setCodigoAtributo(rs.getString("NOM_ATRIBUTO"));
				bean.setLongitud(rs.getString("LONGITUD"));
				
				bean.setInicio(inicioSiguiente.toString() );
				Integer agrega = Integer.valueOf(bean.getLongitud());
				inicioSiguiente = inicioSiguiente + agrega;
				bean.setTipoDato(rs.getString("TIPO_DATO"));
				bean.setFormatoDate(rs.getString("FORMATO_DATE"));
				bean.setEscala(rs.getString("ESCALA"));
				bean.setDescripcion(rs.getString("DESCRIPCION"));
				bean.setValorDefecto(rs.getString("VALOR_DEFECTO"));
				bean.setAtributoOrigen(rs.getString("ATRIBUTO_PROCESO"));
				
				lista.add(bean);
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
		
		return lista;
	}
	
	private List<AS400Out> obtenerOutAS400Programa(String codigoPrograma,
			Connection conn) {
		List<AS400Out> lista = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT COD_PROGRAMA, NOM_ATRIBUTO, TIPO_ATRIBUTO, LONGITUD, ORDEN, TIPO_DATO, FORMATO_DATE, ESCALA, DESCRIPCION, " +
					"VALOR_DEFECTO, ATRIBUTO_PROCESO, LONGITUDLISTA, NUMEROELEMENTOS " +
					"FROM BFP_CARTA_FIANZA.AS400_IN WHERE TIPO_ATRIBUTO = 'OUT' AND (LENGTH(NVL(OBJETO_LISTA,'')) = 0) AND COD_PROGRAMA = ? ORDER BY ORDEN ASC";
			cs = conn.prepareCall(sql);
			cs.setString(1, codigoPrograma);
			rs = cs.executeQuery();

			AS400Out bean = null;
			lista = new ArrayList<AS400Out>();
			Integer inicioSiguiente = 1;
			while (rs.next()) {
				bean = new AS400Out();
				
				bean.setCodigoPrograma(rs.getString("COD_PROGRAMA"));
				bean.setCodigoAtributo(rs.getString("NOM_ATRIBUTO"));
				bean.setLongitud(rs.getString("LONGITUD"));
				
				bean.setInicio(inicioSiguiente.toString() );
				Integer agrega = Integer.valueOf(bean.getLongitud());
				bean.setTipoDato(rs.getString("TIPO_DATO"));
				bean.setFormatoDate(rs.getString("FORMATO_DATE"));
				bean.setEscala(rs.getString("ESCALA"));
				bean.setDescripcion(rs.getString("DESCRIPCION"));
				bean.setValorDefecto(rs.getString("VALOR_DEFECTO"));
				bean.setAtributoDestino(rs.getString("ATRIBUTO_PROCESO"));
				bean.setLongitudLista(rs.getString("LONGITUDLISTA"));
				int elementos = rs.getInt("NUMEROELEMENTOS");
				if (elementos > 0){
					inicioSiguiente = inicioSiguiente + agrega * elementos;
				}
				else{
					inicioSiguiente = inicioSiguiente + agrega;
					inicioListaOut = inicioSiguiente;
				}
				
				lista.add(bean);
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
		
		return lista;
	}
	
}
