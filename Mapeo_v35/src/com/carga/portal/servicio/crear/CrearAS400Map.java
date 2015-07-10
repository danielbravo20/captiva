package com.carga.portal.servicio.crear;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import com.carga.mapeo.dao.impl.AS400DaoImpl;
import com.carga.portal.modelo.AS400In;
import com.carga.portal.modelo.AS400Out;
import com.carga.portal.modelo.AS400OutLista;
import com.carga.portal.modelo.AS400Programa;
import com.carga.portal.modelo.Configuracion;
import com.carga.portal.modelo.Proyecto;

public class CrearAS400Map {

	public static final String SP_CARACTER_CIERRE = "@";

	/**
	 * 
	 * @param proyecto
	 * @param configuracion
	 * @param conn
	 * @throws Exception
	 */
	public void crear(Proyecto proyecto, Configuracion configuracion, Connection conn) throws Exception{
		
		/**************************************************************************************************************/
		/** creamos los directorios */
		File directorioMAP = new File(configuracion.getDirectorioSQL().getAbsolutePath()+"\\AS400\\map");
		if(directorioMAP.exists()==false){
			directorioMAP.mkdirs();
		}
		List<AS400Programa> as400Programas = new AS400DaoImpl().obtenerAS400Programa(proyecto, conn);
		for (int x = 0; x < as400Programas.size(); x++) {
			AS400Programa as400programa = as400Programas.get(x);
			File archivoReq = new File(directorioMAP.getAbsolutePath()+"\\","XSLT_in_"+as400programa.getCodigoPrograma()+"_req_1.map");
			if(archivoReq.exists()){
				archivoReq.delete();
			}
			archivoReq.createNewFile();
			
			BufferedWriter bufferedWriterReq =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoReq,true), "ISO-8859-1"));
			bufferedWriterReq.write(contenidoXSLTreq(as400programa));
			bufferedWriterReq.close();
			
			File archivoRes = new File(directorioMAP.getAbsolutePath()+"\\","XSLT_in_"+as400programa.getCodigoPrograma()+"_res_1.map");
			if(archivoRes.exists()){
				archivoRes.delete();
			}
			archivoRes.createNewFile();
			
			BufferedWriter bufferedWriterRes =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoRes,true), "ISO-8859-1"));
			bufferedWriterRes.write(contenidoXSLTres(as400programa));
			bufferedWriterRes.close();
		}
		
	}
	
	/**
	 * METODO QUE CONSTRUYE EL MAPEO DE ENVIO DE UN PROGRAMA IBS
	 * @param as400programa
	 * @return
	 * @throws Exception
	 */
	public static String contenidoXSLTreq(AS400Programa as400programa) throws Exception{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("<mappingRoot domainID=\"com.ibm.msl.mapping.xml\" targetNamespace=\""+as400programa.getBaseNameSpace()+"/xslt/XSLT_in_"+as400programa.getCodigoPrograma()+"_req_1\" version=\"7.0.500\" xmlns=\"http://www.ibm.com/2008/ccl/Mapping\" xmlns:map=\""+as400programa.getBaseNameSpace()+"/xslt/XSLT_in_"+as400programa.getCodigoPrograma()+"_req_1\">\r\n");
		buffer.append("\t<input path=\""+as400programa.getPathRequestIn()+"\"/>\r\n");
		buffer.append("\t<output path=\""+as400programa.getPathRequestOut()+"\"/>\r\n");
		buffer.append("\t<namespaces>\r\n");
		buffer.append("\t\t<namespace kind=\"extension\" prefix=\"date\" uri=\"http://exslt.org/dates-and-times\"/>\r\n");
		buffer.append("\t</namespaces>\r\n");
		buffer.append("\t<mappingDeclaration name=\"XSLT_in_"+as400programa.getCodigoPrograma()+"_req_1\">\r\n");
		buffer.append("\t\t<input path=\"body\"/>\r\n");
		buffer.append("\t\t<output path=\"body\">\r\n");
		
		ResourceBundle rb = ResourceBundle.getBundle("tiposdatos");
		int cantidad = Integer.parseInt(rb.getString("totalDatosHead"));
		for (int i=0; i<cantidad; i++){
			buffer.append("\t\t\t<cast path=\"callProgram/requestParameters/headParameters/headParameter/value/type('anyType')\" qualifier=\"{http://www.w3.org/2001/XMLSchema}"+rb.getString("tipodatoHead_"+(i+2))+"\"/>\r\n");
		}
		
		cantidad = Integer.parseInt(rb.getString("totalDatosBody"));
		for (int i=0; i<cantidad; i++){
			buffer.append("\t\t\t<cast path=\"callProgram/requestParameters/dataParameters/dataParameter/value/type('anyType')\" qualifier=\"{http://www.w3.org/2001/XMLSchema}"+rb.getString("tipodatoBody_"+(i+2))+"\"/>\r\n");
		}
		
		buffer.append("\t\t</output>\r\n");
		buffer.append("\t\t<assign value=\""+as400programa.getCodigoPrograma()+"\">\r\n");
		buffer.append("\t\t\t<output path=\"callProgram/programName\"/>\r\n");
		buffer.append("\t\t</assign>\r\n");
		
		buffer.append("\t\t<local>\r\n");
		buffer.append("\t\t\t<output path=\"callProgram/requestParameters/headParameters/headParameter\"/>\r\n");
		buffer.append("\t\t\t<assign value=\"IHHORA\">\r\n");
		buffer.append("\t\t\t\t<output path=\"@name\"/>\r\n");
		buffer.append("\t\t\t</assign>\r\n");
		buffer.append("\t\t\t<function ref=\"date:time\">\r\n");
		buffer.append("\t\t\t\t<output path=\"spath('value',2)\"/>\r\n");
		buffer.append("\t\t\t</function>\r\n");
		buffer.append("\t\t</local>\r\n");
		
		buffer.append("\t\t<local>\r\n");
		buffer.append("\t\t\t<output path=\"callProgram/requestParameters/headParameters/headParameter\"/>\r\n");
		buffer.append("\t\t\t<assign value=\"IHFECHA\">\r\n");
		buffer.append("\t\t\t\t<output path=\"@name\"/>\r\n");
		buffer.append("\t\t\t</assign>\r\n");
		buffer.append("\t\t\t<function ref=\"date:date\">\r\n");
		buffer.append("\t\t\t\t<output path=\"spath('value',3)\"/>\r\n");
		buffer.append("\t\t\t</function>\r\n");
		buffer.append("\t\t</local>\r\n");
		
		List<AS400In> as400ins = as400programa.getAs400ins();
		for (int x = 0; x < as400ins.size(); x++) {
			AS400In as400in = as400ins.get(x);
			
			int tipoDato = 2;
			cantidad = Integer.parseInt(rb.getString("totalDatosBody"));
			if (as400in.getTipoDato().equals("java.math.BigDecimal")){
				tipoDato = obtenerNumeroTipoDato(cantidad, rb, "decimal");
			}
			else if (as400in.getTipoDato().equals("java.util.Date")){
				tipoDato = obtenerNumeroTipoDato(cantidad, rb, "date");
			}
			else if (as400in.getTipoDato().equals("java.lang.Integer")){
				tipoDato = obtenerNumeroTipoDato(cantidad, rb, "int");
			}
			buffer.append("\t\t<local>\r\n");
			buffer.append("\t\t\t<input path=\""+as400in.getAtributoOrigen()+"\"/>\r\n");
			buffer.append("\t\t\t<output path=\"callProgram/requestParameters/dataParameters/dataParameter\"/>\r\n");
			buffer.append("\t\t\t<assign value=\""+as400in.getCodigoAtributo()+"\">\r\n");
			buffer.append("\t\t\t\t<output path=\"@name\"/>\r\n");
			buffer.append("\t\t\t</assign>\r\n");
			buffer.append("\t\t\t<move>\r\n");
			buffer.append("\t\t\t\t<input path=\".\"/>\r\n");
			buffer.append("\t\t\t\t<output path=\"spath('value',"+tipoDato+")\"/>\r\n");
			buffer.append("\t\t\t</move>\r\n");
			buffer.append("\t\t</local>\r\n");
		}
		
		buffer.append("\t</mappingDeclaration>\r\n");
		buffer.append("</mappingRoot>\r\n");
		
		return buffer.toString();
	}
	
	private static int obtenerNumeroTipoDato(int cantidad, ResourceBundle rb, String tipo){
		for (int i=0; i<cantidad; i++){
			if (tipo.equals(rb.getString("tipodatoBody_"+(i+2)))){
				return (i+2);
			}
		}
		return 2;
	}

	/**
	 * METODO QUE CONSTRUYE EL MAPEO DE RESPUESTA DE UN PROGRAMA IBS
	 * @param as400programa
	 * @return
	 * @throws Exception
	 */
	public static String contenidoXSLTres(AS400Programa as400programa) throws Exception{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><mappingRoot domainID=\"com.ibm.msl.mapping.xml\" targetNamespace=\""+as400programa.getBaseNameSpace()+"/xslt/XSLT_out_"+as400programa.getCodigoPrograma()+"_res_1\" version=\"7.0.500\" xmlns=\"http://www.ibm.com/2008/ccl/Mapping\" xmlns:map=\""+as400programa.getBaseNameSpace()+"/xslt/XSLT_out_"+as400programa.getCodigoPrograma()+"_res_1\">\r\n");
		buffer.append("\t<input path=\""+as400programa.getPathResponseIn()+"\" var=\"in\" />\r\n");
		buffer.append("\t<input path=\"../../as400Proxy/AS400ProxyService_schema1.xsd\" var=\"in1\" />\r\n");
		buffer.append("\t<output path=\""+as400programa.getPathResponseOut()+"\"/>\r\n");
		buffer.append("\t<mappingDeclaration name=\"XSLT_out_"+as400programa.getCodigoPrograma()+"_res_1\">\r\n");
		
		if (as400programa.getAs400outLista().isEmpty()){
			buffer.append("\t\t<input path=\"$in/body\">\r\n");
			buffer.append("\t\t\t<cast path=\"callProgramResponse/responseParameters/headParameters/headParameter/value/type('anyType')\" qualifier=\"{http://www.w3.org/2001/XMLSchema}string\"/>\r\n");
			buffer.append("\t\t</input>\r\n");
		}
		else{
			buffer.append("\t\t<input path=\"$in/body\">\r\n");
			buffer.append("\t\t\t<cast path=\"callProgramResponse/responseParameters/headParameters/headParameter/value/type('anyType')\" qualifier=\"{http://www.w3.org/2001/XMLSchema}string\"/>\r\n");
			buffer.append("\t\t\t<cast path=\"callProgramResponse/responseParameters/dataParameters/dataParameter/value/type('anyType')\" qualifier=\"{http://www.financiero.com.pe/service/AS400Proxy}ParameterEntryTypeArrayArray\"/>\r\n");
			buffer.append("\t\t</input>\r\n");
		}
		buffer.append("\t\t<output path=\"body\"/>\r\n");
		
		List<AS400Out> as400outs = as400programa.getAs400outs();
		int indice = 0;
		AS400Out as400outCodError = null;
		for (int x = 0; x < as400outs.size(); x++) {
			AS400Out as400out = as400outs.get(x);
			as400outCodError = as400outs.get(0);
				
			if (as400out.getTipoDato().equalsIgnoreCase("java.util.List")){
				List<AS400OutLista> as400outListas = as400programa.getAs400outLista();
				int y=0;
				int z=0;
				indice++;
				String variable = "dataParameter"+indice;
				for (int f = 0; f < as400outListas.size(); f++) {
					AS400OutLista outLista = as400outListas.get(f);
					y++;
					z=y;
					z++;
					buffer.append("\t\t<foreach>\r\n");
					buffer.append("\t\t\t<input path=\"callProgramResponse/responseParameters/dataParameters/dataParameter\" var=\""+variable+"\"/>\r\n");
					buffer.append("\t\t\t<output path=\""+outLista.getObjetoDestino()+"\"/>\r\n");
					buffer.append("\t\t\t<filter lang=\"XPath\">$"+variable+"[@name='"+outLista.getNombre()+"']</filter>\r\n");
					buffer.append("\t\t\t<foreach>\r\n");
					buffer.append("\t\t\t\t<input path=\"$"+variable+"/spath('value',2)/item\" var=\"item"+z+"\"/>\r\n");
					buffer.append("\t\t\t\t<output path=\""+outLista.getObjetoDestinoLista()+"\"/>\r\n");
					
					String campoFiltro = null;
					List<AS400Out> as400outLista = outLista.getAs400outs();
					for (int n = 0; n < as400outLista.size(); n++) {
						AS400Out as400out2 = as400outLista.get(n);
						if(as400out2.isFiltro()){
							campoFiltro = as400out2.getCodigoAtributo();
						}
					}
					if(campoFiltro!=null){
						buffer.append("\t\t\t\t<filter lang=\"XPath\">$item"+z+"[./item[@name='"+campoFiltro.trim()+"']]</filter>\r\n");
					}	
					int u = z;
					
					for (int n = 0; n < as400outLista.size(); n++) {
						AS400Out as400out2 = as400outLista.get(n);
						u++;
						buffer.append("\t\t\t\t<expression>\r\n");
						buffer.append("\t\t\t\t\t<input path=\"$item"+z+"/item\" var=\"item"+u+"\"/>\r\n");
						buffer.append("\t\t\t\t\t<output path=\""+as400out2.getAtributoDestino()+"\"/>\r\n");
						buffer.append("\t\t\t\t\t<code lang=\"xpath\">$item"+u+"[@name='"+as400out2.getCodigoAtributo().trim()+"']/value</code>\r\n");
						buffer.append("\t\t\t\t\t<filter lang=\"XPath\">$item"+u+"[@name='"+as400out2.getCodigoAtributo().trim()+"']</filter>\r\n");
		                buffer.append("\t\t\t\t</expression>\r\n");
					}
					
					buffer.append("\t\t\t</foreach>\r\n");
					buffer.append("\t\t</foreach>\r\n");
					y=u;
				}
			}
			else{
				if (as400out.getAtributoDestino()!=null && !"".equals(as400out.getAtributoDestino())){
					indice++;
					String variable = "dataParameter"+indice;
					buffer.append("\t\t<expression>\r\n");
					buffer.append("\t\t\t<input path=\"callProgramResponse/responseParameters/dataParameters/dataParameter\" var=\""+variable+"\"/>\r\n");
					buffer.append("\t\t\t<output path=\""+as400out.getAtributoDestino()+"\"/>\r\n");
					buffer.append("\t\t\t<code lang=\"xpath\">$"+variable+"[@name='"+as400out.getCodigoAtributo().trim()+"']/value</code>\r\n");
					buffer.append("\t\t\t<filter lang=\"XPath\">$"+variable+"[@name='"+as400out.getCodigoAtributo().trim()+"']</filter>\r\n");
					buffer.append("\t\t</expression>\r\n");
				}
			}
		}
		if (as400outCodError.getAtributoDestino()!= null && !"".equals(as400outCodError.getAtributoDestino())){
			String objetoResponse = "";
			StringTokenizer stk = new StringTokenizer(as400outCodError.getAtributoDestino(),"/");
			objetoResponse = stk.nextToken()+"/codigoError";
			
			buffer.append("\t\t<expression>\r\n");
			buffer.append("\t\t\t<input path=\"callProgramResponse/responseParameters/headParameters/headParameter\" var=\"headParameter\"/>\r\n");
			buffer.append("\t\t\t<output path=\""+objetoResponse+"\"/>\r\n");
			buffer.append("\t\t\t<code lang=\"xpath\">$headParameter[@name='OHCOMSG']/value</code>\r\n");
			buffer.append("\t\t\t<filter lang=\"XPath\">$headParameter[@name='OHCOMSG']</filter>\r\n");
			buffer.append("\t\t</expression>\r\n");
		}
		
		buffer.append("\t</mappingDeclaration>\r\n");
		buffer.append("</mappingRoot>\r\n");
		
		return buffer.toString();
	}
	
	
}