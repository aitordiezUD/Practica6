package pruebas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import clases.DataSetMunicipios;
import clases.Municipio;

public class PnlVisualizacion2 extends JPanel{
	private final int POBLACION_ESTADO;
	private String provincia;
	private int zoomLevel = 1;
	
	
	private HashMap<Municipio,Integer> mapaMunicipioBarra;
	private int heightAnterior = 0;
	
	public PnlVisualizacion2(DataSetMunicipios dataset, String provincia){
		this.provincia = provincia;
		POBLACION_ESTADO = dataset.getPoblacionEstado();
		System.out.println("Poblacion estado: " + POBLACION_ESTADO);
		setSize(280,600);
		setBorder(new LineBorder(Color.BLACK, 1));
		setPreferredSize(new Dimension(280,600));
		int poblacionProvincia = 0;
		HashMap<String,ArrayList<String>> mapaProvinciasMunis = dataset.mapaProvinciasMunis();
		HashMap<String,Municipio> mapaBusquedaMunis = dataset.mapaBusquedaMunis();
		mapaMunicipioBarra = new HashMap<Municipio,Integer>();
		for (String m: mapaProvinciasMunis.get(provincia)) {
			Municipio muni = mapaBusquedaMunis.get(m);
			poblacionProvincia = poblacionProvincia + muni.getHabitantes();
			int heightBarra = (500*muni.getHabitantes())/POBLACION_ESTADO;
//			System.out.println(muni.getNombre() + " "+ heightBarra);
			mapaMunicipioBarra.put(muni,heightBarra);
		}

	}
	
	public PnlVisualizacion2(){
		POBLACION_ESTADO = 0;
		this.setSize(300,300);
		setPreferredSize(new Dimension(300,300));
		this.setBackground(Color.red);
		 
	}
	
	public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }
	

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		
		heightAnterior = 0;
		
//		Pintar la barra de habitantes totales del estado con un String encima de la barra
		g.setColor(Color.cyan);
		g.fillRect(200, 100, 50, 500);
		g.setColor(Color.BLACK);
		g.drawString("Estado",200,90);
		int cont = 1;
		
		for (Municipio m: mapaMunicipioBarra.keySet()) {
//			System.out.println("Municipio " + cont + " " + m.getNombre());
			int height = (mapaMunicipioBarra.get(m)+20)*zoomLevel;//!!!!!!!!!!!!!!!!!!!!! Quitar el 50
			int y = (600-height-heightAnterior)*1;
			System.out.println("Municipio " + cont + " " + m.getNombre() + " ; Height barra: " + height);
			
//			Para pintar cada barra de cada ciudad de un color aleatorio distinto:
			Random rand = new Random();
	        int red = rand.nextInt(256); 
	        int green = rand.nextInt(256); 
	        int blue = rand.nextInt(256); 
			
			g.setColor(new Color(red, green, blue));
			g.fillRect(20, y, 50, height);
			
			g.setColor(Color.black);
			g.drawLine(20, y, 70, y);
			
			g.drawString(m.getNombre(), 73, y);
			
			System.out.println("Sumando height ( "+ height + " ) con heightAnterior ( " +heightAnterior+ " ).");
			heightAnterior = height+heightAnterior;
			System.out.println("Resultado: " + heightAnterior);
//			System.out.println("Barra " + cont + " " + y);
			cont++;
		}
		System.out.println(provincia);
		System.out.println("Height anterior para dibujar String de la provincia: " + heightAnterior);
		g.drawString(provincia, 20, 600-heightAnterior-30);
	}
	
	public static void main(String[] args) throws IOException {
		DataSetMunicipios dataset = new DataSetMunicipios( "datasetMunicipios50k.csv" );
		String provincia = "Islas Baleares";
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		frame.setSize(300,650);
		PnlVisualizacion2 panel = new PnlVisualizacion2(dataset,provincia);
		frame.add(panel);
		
		
		frame.setVisible(true);
	}
}


