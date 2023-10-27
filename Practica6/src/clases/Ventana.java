package clases;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
	private PnlVisualizacion pnlVisualizacion;
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

	
	
//	private String provinciaVisualizacion = "";
	
//	Atributos necesarios para el listener del click derecho
	private String nMuniSel = "";
	private Boolean clickDerecho = false;
	private Municipio muniSel;
	
//	Atributos implementacion INSERCION
	
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
//					System.out.println("Leaf");
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
				Municipio muni = new Municipio(DataSetMunicipios.getCod(),nombreMuni,500000,autonomiaSel,provinciaSel);
				Object[] fila = {DataSetMunicipios.getCod(),nombreMuni,500000,500000,autonomiaSel,provinciaSel};
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
		pnlVisualizacion = new PnlVisualizacion(datosMunis);
		pnlVisualizacion.setPreferredSize(new Dimension(280,600));
		contentPanel.add(pnlVisualizacion, BorderLayout.EAST);
		
		
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
		// raiz.add(nodo1);  -- atención, si lo hacemos así el modelo no se entera y no se refresca
		// En ese caso habría que informar explícitamente al modelo del árbol que se ha insertado un nodo, refrescando así la GUI
		// modeloArbol.nodesWereInserted( raiz, new int[] { raiz.getChildCount()-1 } );
		modeloArbol.insertNodeInto( nodo1, nodoPadre, posi ); // Este método hace las dos cosas: inserta y notifica a los escuchadores del modelo
//		tree.expandir( new TreePath(nodo1.getPath()), true );
		return nodo1;
	}
	
	
}
