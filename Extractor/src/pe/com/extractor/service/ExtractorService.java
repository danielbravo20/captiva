package pe.com.extractor.service;

import java.util.List;

import pe.com.extractor.bean.Tabla;
import pe.com.extractor.dao.impl.DBDao;

public class ExtractorService {

	public void escribirRegistros(String esquema){
		DBDao dbDao = new DBDao();
		List<Tabla> tablas = dbDao.obtenerTablas(esquema);
		for (int i = 0; i < tablas.size(); i++) {
			Tabla tabla = tablas.get(i);
			dbDao.obtenerRegistros(tabla);
		}
	}
	
}
