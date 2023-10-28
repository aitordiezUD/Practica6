package pruebas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.border.LineBorder;

import clases.DataSetMunicipios;
import clases.Municipio;

public class CopiaVentana extends JFrame{
	private JPanel contentPanel;
	private JPanel pnlSuperior;
	private JPanel pnlJTree;
	private JPanel pnlBtns;

	
	public CopiaVentana() throws IOException {
		setSize(1200,720);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Municipios mayores de 50k habitantes");
		
		DataSetMunicipios dataset = new DataSetMunicipios( "datasetMunicipios50k.csv" );
		
		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		
		JPanel pnlTabla = new JPanel();
		pnlTabla.setBackground(Color.BLACK);
		contentPanel.add(pnlTabla, BorderLayout.CENTER);
		
		pnlSuperior = new JPanel();
		pnlSuperior.setPreferredSize(new Dimension(1200,30));
		pnlSuperior.setLayout(new FlowLayout());
		pnlSuperior.setBackground(Color.LIGHT_GRAY);
		contentPanel.add(pnlSuperior, BorderLayout.NORTH);

		pnlJTree = new JPanel();
		pnlJTree.setLayout(new BorderLayout());
		pnlJTree.setPreferredSize(new Dimension(200,595));
		pnlJTree.setBackground(getForeground());
		contentPanel.add(pnlJTree, BorderLayout.WEST);
		
		pnlBtns = new JPanel();
		pnlBtns.setLayout(new FlowLayout());
		pnlBtns.setPreferredSize(new Dimension(1200,50));
		pnlBtns.setBackground(Color.cyan);
		contentPanel.add(pnlBtns,BorderLayout.SOUTH);
		
		PnlVisualizacion2 pnlVisu= new PnlVisualizacion2(dataset, "Vizcaya");
		JScrollPane jsp = new JScrollPane(pnlVisu);
		jsp.setPreferredSize(new Dimension(280,600));
		contentPanel.add(jsp,BorderLayout.EAST);
		
		setContentPane(contentPanel);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public static void main(String[] args) throws IOException {
		CopiaVentana cv = new CopiaVentana();
	}
	
	private static class PnlVisualizacion2 extends JPanel implements Scrollable{
		private final int WIDTH_BARRA = 50;
		private final int MIN_HEIGHT_BARRA = 500;
		private final int POBLACION_ESTADO;
		
		private String provincia;
		private static int zoomLevel = 10;
		private int preferredY = 80+MIN_HEIGHT_BARRA*zoomLevel; 
		private DataSetMunicipios dataset;
		private ArrayList<barraVis> listaBarras;
		private int heightAnterior = 0;
		
		@Override
        public Dimension getPreferredSize() {
            return new Dimension(280, preferredY);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(280, 128);
        }
		
		
		private PnlVisualizacion2(DataSetMunicipios dataset, String provincia){
			this.provincia = provincia;
			this.dataset=dataset;
			this.dataset=dataset;
			POBLACION_ESTADO = dataset.getPoblacionEstado();
			setBorder(new LineBorder(Color.BLACK, 1));
			
			addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {
	                if (e.getButton() == MouseEvent.BUTTON1) {
	                    incZoomLevel();
	                    PnlVisualizacion2.this.repaint();
	                } else if (e.getButton() == MouseEvent.BUTTON3) {
	                    decZoomLevel();
	                    PnlVisualizacion2.this.repaint();
	                }
	            }
	        });
		}
		

		public static void incZoomLevel() {
	        if (zoomLevel<10) {
	        	zoomLevel++;
	        }
	    }
		public static void decZoomLevel() {
	        if (zoomLevel>0) {
	        	zoomLevel--;
	        }
	    }
		
		
		@Override
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponent(g);
			if (!provincia.equals("")) {
				crearListaBarras();

				heightAnterior = 0;
				
//				Pintar la barra de habitantes totales del estado con un String encima de la barra
				g.setColor(Color.cyan);
				g.fillRect(200, preferredY-MIN_HEIGHT_BARRA*zoomLevel, WIDTH_BARRA, MIN_HEIGHT_BARRA*zoomLevel);
				g.setColor(Color.BLACK);
				g.drawString("Estado",200,preferredY-MIN_HEIGHT_BARRA*zoomLevel-30);
				for (barraVis bv: listaBarras) {
					int height = bv.getDefaultHeight()*zoomLevel;//!!!!!!!!!!!!!!!!!!!!! Quitar el 50
					int y = preferredY-height-heightAnterior;
					
					g.setColor(bv.getColorBarra());
					g.fillRect(20, y, 50, height);
					
					g.setColor(Color.black);
					g.drawLine(20, y, 70, y);
					
					g.drawString(bv.getMuni().getNombre(), 73, y);
					
					heightAnterior = height+heightAnterior;
				}
				g.drawString(provincia, 20, preferredY-heightAnterior-30);
			}
			
		}
		
		
		public void crearListaBarras() {
			int poblacionProvincia = 0;
			ArrayList<barraVis> listaBarras2 = new ArrayList<barraVis>();
			HashMap<String,Municipio> mapaBusquedaMunis = dataset.mapaBusquedaMunis();
			HashMap<String,ArrayList<String>> mapaProvinciasMunis = dataset.mapaProvinciasMunis();
			for (String m: mapaProvinciasMunis.get(provincia)) {
				
				Municipio muni = mapaBusquedaMunis.get(m);
				Random rand = new Random();
		        int red = rand.nextInt(256); 
		        int green = rand.nextInt(256); 
		        int blue = rand.nextInt(256);
		        poblacionProvincia = poblacionProvincia + muni.getHabitantes();
				int heightBarra = (500*muni.getHabitantes())/POBLACION_ESTADO;
				barraVis bv = new barraVis(muni,new Color(red,green,blue),heightBarra);				
				listaBarras2.add(bv);
			}
			if (!listaBarras2.equals(listaBarras)) {
				listaBarras = listaBarras2;
			}
		}


		@Override
		public Dimension getPreferredScrollableViewportSize() {
			// TODO Auto-generated method stub
			return new Dimension(280,preferredY);
		}


		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			// TODO Auto-generated method stub
			return 128;
		}


		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
			// TODO Auto-generated method stub
			return 128;
		}


		@Override
		public boolean getScrollableTracksViewportWidth() {
			// TODO Auto-generated method stub
			return getPreferredSize().width
                    <= getParent().getSize().width;
		}


		@Override
		public boolean getScrollableTracksViewportHeight() {
			// TODO Auto-generated method stub
			return getPreferredSize().height
                    <= getParent().getSize().height;
		}
	}
	
	private static class barraVis{
		private Municipio muni;
		private Color colorBarra;
		private int defaultHeight;
		public Municipio getMuni() {
			return muni;
		}
		public Color getColorBarra() {
			return colorBarra;
		}
		public int getDefaultHeight() {
			return defaultHeight;
		}
		public barraVis(Municipio muni, Color colorBarra, int defaultHeight) {
			this.muni = muni;
			this.colorBarra = colorBarra;
			this.defaultHeight = defaultHeight;
		}
		@Override
		public boolean equals(Object obj) {
		    if (this == obj) {
		        return true;  // Si es la misma instancia, son iguales
		    }
		    if (obj == null || getClass() != obj.getClass()) {
		        return false;  // Si el objeto es nulo o no es de la misma clase, no son iguales
		    }
		    
		    // Convierte el objeto pasado en el mismo tipo que esta instancia
		    barraVis other = (barraVis) obj;
		    
		    // Realiza la comparación de los dos atributos específicos
		    return muni.equals(other.muni);
		}
		
	}
	

	
}
