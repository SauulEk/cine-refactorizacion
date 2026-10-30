import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Funcion {
    private final String titulo;
    private final int sala;
    private final String hora;

    // TECNICA UTILIZADA: REMOVE SETTING METHOD
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // quitamos setPrecio() porque nadie lo usaba y el precio de la funcion solo se pone
    // cuando se crea. Ahora precio es final y se lee con getPrecio().
    private final double precio;

    // TECNICA UTILIZADA: HIDE METHOD
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // la lista ocupados era public y cualquier clase la podia modificar (SistemaReservas
    // hacia r.funcion.ocupados.remove(a)). La pusimos private y solo se puede cambiar
    // con ocupar() y liberar(). Para leerla esta getOcupados(), que regresa una lista
    // que no se puede modificar.
    private final List<String> ocupados = new ArrayList<>();

    public Funcion(String titulo, int sala, String hora, double precio) {
        this.titulo = titulo;
        this.sala = sala;
        this.hora = hora;
        this.precio = precio;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getSala() {
        return sala;
    }

    public String getHora() {
        return hora;
    }

    public double getPrecio() {
        return precio;
    }

    public List<String> getOcupados() {
        return Collections.unmodifiableList(ocupados);
    }

    // TECNICA UTILIZADA: SEPARATE QUERY FROM MODIFIER
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // el metodo ocupar() hacia dos cosas: revisaba si el asiento estaba libre (y si no
    // lanzaba excepcion) y ademas lo marcaba como ocupado. Lo separamos en dos:
    // estaOcupado() que solo pregunta, y ocupar() que solo marca el asiento.
    // Ahora el que llama tiene que preguntar primero con estaOcupado().
    public boolean estaOcupado(Asiento asiento) {
        return ocupados.contains(asiento.getCodigo());
    }

    public void ocupar(Asiento asiento) {
        ocupados.add(asiento.getCodigo());
    }

    public void liberar(Asiento asiento) {
        ocupados.remove(asiento.getCodigo());
    }
}
