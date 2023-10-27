package pruebas;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

public class PruebaJTree extends JFrame{
	JTree arbol;
	DefaultTreeModel modelo;
	DefaultMutableTreeNode raiz;
	
	
	public PruebaJTree() {
		// TODO Auto-generated constructor stub
		setSize(500,500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		raiz = new DefaultMutableTreeNode("Raiz");
		arbol = new JTree();
		arbol.setCellRenderer(new DefaultTreeCellRenderer() {

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {
				// TODO Auto-generated method stub
				Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				
				if(leaf) {
					System.out.println("LEAF");
				}
				
				return c;
			}
			
		});
		modelo = new DefaultTreeModel(raiz);
		arbol.setModel(modelo);
		modelo.insertNodeInto(new DefaultMutableTreeNode("leaf") , raiz, 0);
		
		getContentPane().add(arbol);
		
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new PruebaJTree();
	}
}
