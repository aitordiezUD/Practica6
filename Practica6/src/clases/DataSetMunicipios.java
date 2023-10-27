package clases;

import java.io.BufferedReader;
import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DataSetMunicipios {
	
	private ArrayList<Municipio> municipios = new ArrayList<Municipio>();
	private int poblacionEstado = 0;
	private static int cod = 1;
	
	public DataSetMunicipios( String nombreFichero ) throws IOException {
		BufferedReader br = null;
		FileReader fr = null;
		
		try {
			fr = new FileReader(new File(nombreFichero));
			br = new BufferedReader(fr);
			String linea = br.readLine();
			while(linea != null) {
				String[] campos = linea.split(";");
				Municipio muni = new Municipio(Integer.parseInt(campos[0]), campos[1], Integer.parseInt(campos[2]), campos[3], campos[4]);
				municipios.add(muni);
				poblacionEstado = poblacionEstado + Integer.parseInt(campos[2]);
				cod++;
				linea = br.readLine();
			}
			
		}catch (Exception e) {
			System.out.println("Error cargando el DataSet");
		}
	}

	public static int getCod() {
		return cod;
	}

	public static void incCod() {
		DataSetMunicipios.cod++;
	}

	public int getPoblacionEstado() {
		return poblacionEstado;
	}

	public ArrayList<Municipio> getMunicipios() {
		return municipios;
	}

	public void setMunicipios(ArrayList<Municipio> municipios) {
		this.municipios = municipios;
	}
	
	
//	Realizo varios mapas para facilitar la búsque de datos cuando sea necesario
	
	public HashMap<String,ArrayList<String>> mapaCCAAprovincias(){
		HashMap<String,ArrayList<String>> mapa = new HashMap<String,ArrayList<String>>();
		for (Municipio m:municipios) {
			String autonomia = m.getAutonomia();
			String provincia = m.getProvincia();
			if (!mapa.containsKey(autonomia)) {
				mapa.put(autonomia, new ArrayList<String>());
				mapa.get(autonomia).add(provincia);
			}else {
				if (!mapa.get(autonomia).contains(provincia)) {
					mapa.get(autonomia).add(provincia);
					Collections.sort(mapa.get(autonomia));
				}
			}
		}
		return mapa;
	}
	
	public HashMap<String,ArrayList<String>> mapaProvinciasMunis(){
		HashMap<String,ArrayList<String>> mapa = new HashMap<String,ArrayList<String>>();
		for (Municipio m:municipios) {
			String municipio = m.getNombre();
			String provincia = m.getProvincia();
			if (!mapa.containsKey(provincia)) {
				mapa.put(provincia, new ArrayList<String>());
				mapa.get(provincia).add(municipio);
			}else {
				mapa.get(provincia).add(municipio);
				Collections.sort(mapa.get(provincia));
			}
		}
		return mapa;
	}
	
	public HashMap<String,Municipio> mapaBusquedaMunis(){
		HashMap<String,Municipio> mapa = new HashMap<String,Municipio>();
		for (Municipio m:municipios) {
			mapa.put(m.getNombre(), m);
		}
		return mapa;
	}
		
	public int habitantesTotales() {
		int res = 0;
		for (Municipio m: municipios) {
			res = res + m.getHabitantes();
		}
		return res;
	}
	
	public int habitantesProvincia(String provincia) {
		int res = 0;
		for (String nombreMun: this.mapaProvinciasMunis().get(provincia)) {
			Municipio muni = this.mapaBusquedaMunis().get(nombreMun);
			res = res + muni.getHabitantes();
		}
		return res;
	}
	
}
