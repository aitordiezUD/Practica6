package clases;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;

public class Ventana extends JFrame{
	private static final int COL_MUNICIPIO = 1;
	
	private JTable tabla;
	private DefaultTableModel modeloTabla;
	private JTree arbol;
	private DefaultTreeModel modeloArbol;
	private JPanel pnlJTree;
	private JPanel pnlVisualizacion;
	private JPanel contentPanel;
	private JPanel pnlBtns;
	private DataSetMunicipios datosMunis;
	
	public Ventana (JFrame ventanaOrigen) {
		setSize(1200,700);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Municipios mayores de 50k habitantes");
		
		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		
		tabla = new JTable();
		JScrollPane spTabla = new JScrollPane(tabla);
		spTabla.setPreferredSize(new Dimension(700,600));
		contentPanel.add(spTabla, BorderLayout.CENTER);
		

		pnlJTree = new JPanel();
		pnlJTree.setLayout(new BorderLayout());
		pnlJTree.setPreferredSize(new Dimension(200,595));
		arbol=new JTree();
		JScrollPane spArbol = new JScrollPane(arbol);
		spArbol.setPreferredSize(new Dimension(195,595));
		pnlJTree.add(spArbol,BorderLayout.EAST);
		contentPanel.add(pnlJTree, BorderLayout.WEST);
		
		pnlVisualizacion = new JPanel();
		pnlVisualizacion.setBackground(Color.green);
		pnlVisualizacion.setPreferredSize(new Dimension(280,600));
		contentPanel.add(pnlVisualizacion, BorderLayout.EAST);
		
		pnlBtns = new JPanel();
		pnlBtns.setPreferredSize(new Dimension(1200,100));
		pnlBtns.setBackground(Color.cyan);
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
                	modeloTabla.setRowCount(0);
                	setDatosTabla(provincia.toString());
                }
                
			}
		});
		

		
		
	}
	
	public void setDatosIniciales(DataSetMunicipios datosMunis){
		this.datosMunis = datosMunis;
		DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Municipios");
		modeloArbol = new DefaultTreeModel(raiz);
		HashMap<String,ArrayList<String>> mapa = datosMunis.mapaCCAAprovincias();
		int count = 0;
		
//		Para ordenar las claves por orden alfabético se usan las siguientes dos lineas:
		ArrayList<String> listaClaves = new ArrayList<>(mapa.keySet());
		Collections.sort(listaClaves);
		
		for (String autonomia:listaClaves) {
			DefaultMutableTreeNode nodoAuto = new DefaultMutableTreeNode(autonomia);
			modeloArbol.insertNodeInto(nodoAuto , raiz, count);
			count++;
			ArrayList<String> provincias = mapa.get(autonomia);
			for (int i = 0; i<provincias.size(); i++) {
				modeloArbol.insertNodeInto(new DefaultMutableTreeNode(provincias.get(i)) , nodoAuto, i);
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
					if (col == COL_MUNICIPIO) {
						
					}
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
				
				if (column == 3) {
					int valor = Integer.parseInt(value.toString());
					pb.setValue(valor);
					int red = (int) (((valor-50000.0)/4950000)*255);
					int green = (int) (255-((valor-50000.0)/4950000)*255);
					Color colorValor = new Color(red, green, 0);
					pb.setForeground(colorValor);
					return pb;
				}
				Component comp =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
	
	
}
