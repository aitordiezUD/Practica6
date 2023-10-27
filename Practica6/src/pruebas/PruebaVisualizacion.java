package pruebas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class PruebaVisualizacion extends JPanel{

	
	public PruebaVisualizacion() {
		setSize(280,600);
		setPreferredSize(new Dimension(280,600));
		setBorder(new LineBorder(Color.BLACK, 10));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		
		double scale = 1.5;
		Graphics2D g2d = (Graphics2D) g;
		
		int w = 280;// real width of canvas
		int h = 600;// real height of canvas
				// Translate used to make sure scale is centered
		g2d.translate(w/2, h/2);
		g2d.scale(scale, scale);
		g2d.translate(-w/2, -h/2);
		
		

		g2d.setColor(Color.cyan);
		Rectangle2D.Double r1 = new Rectangle2D.Double(0,400,50,200);
		g2d.fill(r1);
		
		g2d.setColor(Color.orange);
		Rectangle2D.Double r2 = new Rectangle2D.Double(0,300,50,200);
		g2d.fill(r2);
	}
	
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(650,700);
		frame.setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new PruebaVisualizacion());

		
		frame.setVisible(true);
	}
}
