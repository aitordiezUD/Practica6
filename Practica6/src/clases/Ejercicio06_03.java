package clases;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Ejercicio06_03 {
	
	private static JFrame ventana;
	private static DataSetMunicipios dataset;
	
	private static Ventana vent;
	
	public static void main(String[] args) {
		ventana = new JFrame( "Municipios" );
		ventana.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		ventana.setLocationRelativeTo( null );
		ventana.setSize( 200, 80 );

		JButton bCargaMunicipios = new JButton( "Carga municipios > 50" );
		ventana.add( bCargaMunicipios );
		
		bCargaMunicipios.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cargaMunicipios();
			}
		});
		
		ventana.setVisible( true );
	}
	
	private static void cargaMunicipios() {
		try {
			dataset = new DataSetMunicipios( "datasetMunicipios50k.csv" );
			System.out.println( "Cargados municipios:" );
			for (Municipio m : dataset.getMunicipios() ) {
				System.out.println( "\t" + m );
			}
			// TODO Resolver el ejercicio 6.3
			vent = new Ventana( ventana);
			vent.setDatosIniciales( dataset );
			vent.setVisible( true );
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
//			System.err.println( "Error en carga de municipios" );
		}
	}
	
}
