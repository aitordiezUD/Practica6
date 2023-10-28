package clases;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;





enum ORDEN{
	Alfabetico,
	Numerico
}

public class Ventana extends JFrame{
	private static final int COL_MUNICIPIO = 1;
	private static final int COL_HABITANTES = 2;
	
	private JTable tabla;
	private DefaultTableModel modeloTabla;
	private JTree arbol;
	private DefaultTreeModel modeloArbol;
	private JPanel pnlJTree;
	private PnlVisualizacion2 pnlVisualizacion;
	private JPanel contentPanel;
	private JPanel pnlBtns;
	private JPanel pnlSuperior;
	private JLabel lblSup;
	private DataSetMunicipios datosMunis;
	private HashMap<String,ArrayList<String>> mapaCCAAprovincias;
	private HashMap<String,Municipio> mapaBusquedaMunis;
	private JButton btnEliminar;
	private JButton btnOrden;
	private JButton btnInser;
	private ORDEN ordenActual = ORDEN.Alfabetico;
	private String provinciaSel="";
	private String autonomiaSel="";
/*	Creo contInserc para que cuando inserte un nuevo municipio a la fila se creo que el nombre: Nombre-contInserc. 
	Ya que si añado más de una fila sin modificar los nombres de las filas añadidas anteriormente tendre problemas con 
	los mapas que he creado en la clase DataSetMunicipios ya que habrá distintos municipios con el mismo nombre ("Nombre"). 
	*/	
	private int contInserc=0;

	
	
//	Atributos necesarios para el listener del click derecho
	private String nMuniSel = "";
	private Boolean clickDerecho = false;
	private Municipio muniSel;
		
	public Ventana (JFrame ventanaOrigen) {
		setSize(1200,720);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Municipios mayores de 50k habitantes");
		
		
		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		
		tabla = new JTable();
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane spTabla = new JScrollPane(tabla);
		spTabla.setPreferredSize(new Dimension(700,600));
		contentPanel.add(spTabla, BorderLayout.CENTER);
		
		pnlSuperior = new JPanel();
		pnlSuperior.setPreferredSize(new Dimension(1200,30));
		pnlSuperior.setLayout(new FlowLayout());
		pnlSuperior.setBackground(Color.LIGHT_GRAY);
		lblSup = new JLabel("label superior");
		pnlSuperior.add(lblSup);
		contentPanel.add(pnlSuperior, BorderLayout.NORTH);

		pnlJTree = new JPanel();
		pnlJTree.setLayout(new BorderLayout());
		pnlJTree.setPreferredSize(new Dimension(200,595));
		arbol=new JTree();
		JScrollPane spArbol = new JScrollPane(arbol);
		spArbol.setPreferredSize(new Dimension(195,595));
		pnlJTree.add(spArbol,BorderLayout.EAST);
		contentPanel.add(pnlJTree, BorderLayout.WEST);
		
		arbol.setCellRenderer(new DefaultTreeCellRenderer() {
			private static final long serialVersionUID = 1L;
			JPanel panel = new JPanel();
			JProgressBar pb= new JProgressBar(50000, 7000000);

			
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {
				// TODO Auto-generated method stub
				Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) value;
				
				if(leaf) {
					panel.setLayout(new BorderLayout());
					int habitantes = datosMunis.habitantesProvincia(nodo.getUserObject().toString());
					pb.setValue(habitantes);
					panel.add(pb,BorderLayout.SOUTH);
					panel.add(c);
					return panel;
				}
				
				return c;
			}
		});

		
		pnlBtns = new JPanel();
		pnlBtns.setLayout(new FlowLayout());
		pnlBtns.setPreferredSize(new Dimension(1200,50));
		pnlBtns.setBackground(Color.cyan);
		
		btnEliminar = new JButton("Eliminar");
		btnEliminar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int index = tabla.getSelectedRow();
				if (index >= 0) {
			        int option = JOptionPane.showConfirmDialog(null, "¿Estas seguro que deseas eliminar " + tabla.getValueAt(index, 1)+ "?", "Confirmación", JOptionPane.OK_CANCEL_OPTION);
			        if (option == JOptionPane.OK_OPTION) {
			        	Municipio muni  = mapaBusquedaMunis.get(tabla.getValueAt(index, 1).toString());
			        	mapaBusquedaMunis.remove(tabla.getValueAt(index, 1).toString());
			            datosMunis.getMunicipios().remove(muni);
			            modeloTabla.removeRow(index);
			            tabla.repaint();
			            arbol.repaint();
			            pnlVisualizacion.repaint();
			        }
				}else {
					JOptionPane.showMessageDialog(null, "No hay ninguna fila seleccionada.");
				}
			}
		});
		pnlBtns.add(btnEliminar);
		
		btnOrden = new JButton("Orden");
		btnOrden.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (ordenActual == ORDEN.Alfabetico) {
					Ventana.this.ordenNumerico();
					ordenActual = ORDEN.Numerico;
				}else {
					Ventana.this.ordenAlfabetico();
					ordenActual = ORDEN.Alfabetico;
				}
			}
		});
		pnlBtns.add(btnOrden);
		
		btnInser = new JButton("Insercion");
		btnInser.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String nombreMuni = "Nombre-"+contInserc;
				contInserc++;
				Municipio muni = new Municipio(DataSetMunicipios.getCod(),nombreMuni,50000,autonomiaSel,provinciaSel);
				Object[] fila = {DataSetMunicipios.getCod(),nombreMuni,50000,50000,autonomiaSel,provinciaSel};
				modeloTabla.addRow(fila);
				mapaBusquedaMunis.put(nombreMuni, muni);
				datosMunis.getMunicipios().add(muni);
				DataSetMunicipios.incCod();
				tabla.repaint();
				pnlVisualizacion.repaint();
			}
		});
		pnlBtns.add(btnInser);
		
		contentPanel.add(pnlBtns,BorderLayout.SOUTH);
		
		setContentPane(contentPanel);
		
		this.addWindowListener( new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				ventanaOrigen.setVisible( false );
			}
			@Override
			public void windowClosed(WindowEvent e) {
				ventanaOrigen.setVisible( true );
			}
		});
		
		arbol.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		arbol.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				// TODO Auto-generated method stub
                TreePath path = e.getPath();
                if (path.getPathCount() == 3) {
                	Object provincia = arbol.getLastSelectedPathComponent();
                	autonomiaSel = path.getPathComponent(2).toString();
                	provinciaSel = provincia.toString();
                	modeloTabla.setRowCount(0);
                	setDatosTabla(provincia.toString());
                	muniSel = null;
                	nMuniSel = "";
                	clickDerecho = false;
                	pnlVisualizacion.setProvincia(provincia.toString());
                	System.out.println("Nuevo valor en provincia: " + provincia.toString());
                	pnlVisualizacion.repaint();
                	tabla.repaint();
                }
			}
		});
		

		

		
		
	}
	
	public void setDatosIniciales(DataSetMunicipios datosMunis){
		this.datosMunis = datosMunis;
		
		
//		Creacion panel visualizacion

		pnlVisualizacion= new PnlVisualizacion2(datosMunis);
		JScrollPane jsp = new JScrollPane(pnlVisualizacion);
		jsp.setPreferredSize(new Dimension(280,600));
		

		contentPanel.add(jsp,BorderLayout.EAST);
		
//		Creacion de los mapas de búsqueda
		mapaBusquedaMunis = datosMunis.mapaBusquedaMunis();
		mapaCCAAprovincias = datosMunis.mapaCCAAprovincias();
		
//		Crecion del modelo de JTree
		DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Municipios");
		modeloArbol = new DefaultTreeModel(raiz);
		int count = 0;
		
//		Para ordenar las claves por orden alfabético se usan las siguientes dos lineas:
		ArrayList<String> listaClaves = new ArrayList<>(mapaCCAAprovincias.keySet());
		Collections.sort(listaClaves);
		
//		Carga de los datos en el JTree
		for (String autonomia:listaClaves) {
			DefaultMutableTreeNode nodoAuto = new DefaultMutableTreeNode(autonomia);
			modeloArbol.insertNodeInto(nodoAuto , raiz, count);
			count++;
			ArrayList<String> provincias = mapaCCAAprovincias.get(autonomia);
			for (int i = 0; i<provincias.size(); i++) {
				crearNodo(provincias.get(i), nodoAuto, i);
			}
		}
		arbol.setModel(modeloArbol);
		

		
		modeloTabla = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

//			Considero que los habitantes solo se podrá editar desde la columna de habitantes.
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				if (column == 1 || column == 2) {
					return true;
				}else {
					return false;
				}
			}
			
//			Para que se actualize la columna Población cuando se edita la columna Habitantes
			@Override
		    public void setValueAt(Object value, int row, int column) {
		        if (column == COL_HABITANTES) {
		            int nuevoValor =  Integer.parseInt(value+"");
		            setValueAt(nuevoValor, row, 3);
		            String nombreMuni = getValueAt(row, 1).toString();
		            Municipio muni = mapaBusquedaMunis.get(nombreMuni);
		            muni.setHabitantes(nuevoValor);
		            pnlVisualizacion.repaint();
		        }
		        if (column == COL_MUNICIPIO) {
		        	String nombreAntiguo = tabla.getValueAt(row, COL_MUNICIPIO).toString();
		        	String nuevoNombre = value.toString();
		        	Municipio muni = mapaBusquedaMunis.get(nombreAntiguo);
		        	muni.setNombre(nuevoNombre);
		        	mapaBusquedaMunis.remove(nombreAntiguo);
		        	mapaBusquedaMunis.put(nuevoNombre,muni);
		        	pnlVisualizacion.repaint();
		        }
		        super.setValueAt(value, row, column);
		    }
			
			
		};
		modeloTabla.addColumn("Código");
		modeloTabla.addColumn("Municipio");
		modeloTabla.addColumn("Habitantes");
		modeloTabla.addColumn("Población");
		modeloTabla.addColumn("Provincia");
		modeloTabla.addColumn("Comunidad Autónoma");
		
		tabla.setModel(modeloTabla);
		tabla.getTableHeader().setReorderingAllowed(false);
		
//		Listener Tabla
		tabla.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getButton() == MouseEvent.BUTTON3) {
					int col = tabla.columnAtPoint(e.getPoint());
					int fila = tabla.rowAtPoint(e.getPoint());
					if (col == COL_MUNICIPIO && fila >= 0) {
//						System.out.println("Columna: "+ col + "; Fila: " + fila);
						String muni = (String) tabla.getValueAt(fila, col);
//						System.out.println("Click derecho en col Municipio");
						if (clickDerecho == false) {
							nMuniSel = muni;
//							System.out.println("Ciudad seleccionada: " + nMuniSel);
							muniSel = mapaBusquedaMunis.get(muni);
//							System.out.println("Municipio seleccionado: " + muniSel);
							clickDerecho = true;
						}else {
							if (muni == nMuniSel) {
								nMuniSel = "";
								muniSel = null;
								clickDerecho = false;
//								System.out.println("Municipio sin seleccionar " + nMuniSel);
							}else {
								nMuniSel = muni;
//								System.out.println("Ciudad seleccionada: " + nMuniSel);
								muniSel = mapaBusquedaMunis.get(muni);
//								System.out.println("Municipio seleccionado: " + muniSel);
								clickDerecho = true;
								
							}
						}
					}
					tabla.repaint();
				}
			}
		});
		
		
//		Renderer tabla
		tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;
			private JProgressBar pb = new JProgressBar(50000, 5000000);
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component comp =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				comp.setBackground(Color.WHITE);
				if (column == 1) {
					if (clickDerecho) {
						if ( Integer.parseInt(table.getValueAt(row, COL_HABITANTES)+"") > muniSel.getHabitantes() ) {
							comp.setBackground(Color.RED);
						}else if (Integer.parseInt(table.getValueAt(row, COL_HABITANTES)+"") < muniSel.getHabitantes()){
							comp.setBackground(Color.GREEN);
						}
					}else {
						comp.setBackground(Color.WHITE);
					}
				}
				
				
				if (column == 3) {
					int valor = Integer.parseInt(value.toString());
					pb.setValue(valor);
					int red = (int) (((valor-50000.0)/4950000)*255);
					int green = (int) (255-((valor-50000.0)/4950000)*255);
					Color colorValor = new Color(red, green, 0);
					pb.setForeground(colorValor);
					return pb;
				}
				
				if (isSelected) {
		            comp.setBackground(table.getSelectionBackground());
		            comp.setForeground(table.getSelectionForeground());
		        } else {
		            comp.setBackground(table.getBackground());
		            comp.setForeground(table.getForeground());
		        }
				
				return comp;
			}
		});
		
	}
	
	public void setDatosTabla(String Provincia) {
		ArrayList<String> nombresMunis = datosMunis.mapaProvinciasMunis().get(Provincia);
		for(String n: nombresMunis) {
			Municipio muni = datosMunis.mapaBusquedaMunis().get(n);
			modeloTabla.addRow(new Object[] {muni.getCodigo(),muni.getNombre(),muni.getHabitantes(),muni.getHabitantes(),muni.getProvincia(),muni.getAutonomia()});
		}
		tabla.repaint();
		

	}
	
	public void ordenAlfabetico() {
		int numFilas = modeloTabla.getRowCount();
		ArrayList<Object[]> filasTabla = new ArrayList<>();
		for (int i = 0; i < numFilas; i++) {
		    Object[] datosFila = new Object[modeloTabla.getColumnCount()];
		    for (int j = 0; j < modeloTabla.getColumnCount(); j++) {
		        datosFila[j] = modeloTabla.getValueAt(i, j);
		    }
		    filasTabla.add(datosFila);
		}
		
		Collections.sort(filasTabla, new Comparator<Object[]>() {

			@Override
			public int compare(Object[] fila1, Object[] fila2) {
				// TODO Auto-generated method stub
				return ((String) fila1[1]).compareTo((String) fila2[1]);
			}
		});
		modeloTabla.setRowCount(0);
		for (Object[] fila: filasTabla) {
			modeloTabla.addRow(fila);
		}
		tabla.repaint();
	}
	
	public void ordenNumerico() {
		int numFilas = modeloTabla.getRowCount();
		ArrayList<Object[]> filasTabla = new ArrayList<>();
		for (int i = 0; i < numFilas; i++) {
		    Object[] datosFila = new Object[modeloTabla.getColumnCount()];
		    for (int j = 0; j < modeloTabla.getColumnCount(); j++) {
		        datosFila[j] = modeloTabla.getValueAt(i, j);
		    }
		    filasTabla.add(datosFila);
		}
		
		Collections.sort(filasTabla, new Comparator<Object[]>() {

			@Override
			public int compare(Object[] fila1, Object[] fila2) {
				// TODO Auto-generated method stub
				return ((Integer) fila2[2]).compareTo((Integer) fila1[2]);
			}
		});
		modeloTabla.setRowCount(0);
		for (Object[] fila: filasTabla) {
			modeloTabla.addRow(fila);
		}
		tabla.repaint();
	}
	
	
	private DefaultMutableTreeNode crearNodo( Object dato, DefaultMutableTreeNode nodoPadre, int posi ) {
		DefaultMutableTreeNode nodo1 = new DefaultMutableTreeNode( dato );
		modeloArbol.insertNodeInto( nodo1, nodoPadre, posi );
		return nodo1;
	}
	
	private static class PnlVisualizacion2 extends JPanel implements Scrollable{
		private final int WIDTH_BARRA = 50;
		private final int MIN_HEIGHT_BARRA = 500;
		private final int POBLACION_ESTADO;
		
		private String provincia="";
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
		
		
		public String getProvincia() {
			return provincia;
		}

		public void setProvincia(String provincia) {
			this.provincia = provincia;
		}

		private PnlVisualizacion2(DataSetMunicipios dataset){
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
	        if (zoomLevel>1) {
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
