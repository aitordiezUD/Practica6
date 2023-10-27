package pruebas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import clases.DataSetMunicipios;
import clases.PnlVisualizacion;

public class MainFrame extends JFrame {

    private PnlVisualizacion2 pnlVisualizacion;
    private JSlider zoomSlider;
    private JScrollPane scrollPane;
    

    public MainFrame() throws IOException {
    	DataSetMunicipios dataset = new DataSetMunicipios( "datasetMunicipios50k.csv" );
		String provincia = "Islas Baleares";
    	
    	// Inicializa tu panel de visualización y otros componentes
        pnlVisualizacion = new PnlVisualizacion2(dataset,provincia);
        zoomSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        scrollPane = new JScrollPane(pnlVisualizacion);

        // Configura el panel de visualización dentro del JScrollPane
        scrollPane.setPreferredSize(new Dimension(800, 600));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Configura el modo de desplazamiento del JViewport
        JViewport viewport = scrollPane.getViewport();
        viewport.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);

        // Agrega un oyente de cambio al JSlider
        zoomSlider.addChangeListener(e -> {
            int zoomLevel = zoomSlider.getValue();
            pnlVisualizacion.setZoomLevel(zoomLevel);
            pnlVisualizacion.repaint();
        });

        // Agrega componentes al frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(zoomSlider, BorderLayout.SOUTH);

        // Configura otras propiedades del frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame;
				try {
					frame = new MainFrame();
					frame.setVisible(true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
            }
        });
    }
}
    
