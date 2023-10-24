package clases;

public class Municipio {
	private int codigo;
	private String nombre;
	private int habitantes;
	private String provincia;
	private String autonomia;

	public Municipio(int codigo, String nombre, int habitantes, String provincia, String autonomia) {
		super();
		this.codigo = codigo;
		this.nombre = nombre;
		this.habitantes = habitantes;
		this.provincia = provincia;
		this.autonomia = autonomia;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getHabitantes() {
		return habitantes;
	}

	public void setHabitantes(int habitantes) {
		this.habitantes = habitantes;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getAutonomia() {
		return autonomia;
	}

	public void setAutonomia(String autonomia) {
		this.autonomia = autonomia;
	}

	@Override
	public String toString() {
		return "[" + codigo + "] " + nombre + ", " + habitantes + " en " + provincia + " (" + autonomia + ")";
	}

	
	
}