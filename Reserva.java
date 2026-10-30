import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reserva {
    private final int id;
    private final Cliente cliente;
    private final Funcion funcion;
    private final List<Asiento> asientos;
    private final double precio;
    private boolean cancelada;

    // TECNICA UTILIZADA: ADD PARAMETER
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // el metodo hacer() recibia el canal ("App" o "Taquilla") pero no hacia nada con el,
    // entonces ese dato se perdia. Le agregamos el parametro canal al constructor de Reserva
    // para que se guarde, y se puede consultar con getCanal().
    private final String canal;

    // NOTA: el precio tambien lo recibe el constructor (ya calculado en SistemaReservas)
    // para no tener que ponerle un setter o dejarlo public como antes.
    public Reserva(int id, Cliente cliente, Funcion funcion, List<Asiento> asientos,
                   String canal, double precio) {
        this.id = id;
        this.cliente = cliente;
        this.funcion = funcion;
        this.asientos = new ArrayList<>(asientos);
        this.canal = canal;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public List<Asiento> getAsientos() {
        return Collections.unmodifiableList(asientos);
    }

    public String getCanal() {
        return canal;
    }

    public double getPrecio() {
        return precio;
    }

    public boolean estaCancelada() {
        return cancelada;
    }

    // TECNICA UTILIZADA: REMOVE SETTING METHOD
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // quitamos setCancelada(boolean) porque con ese setter hasta se podia "descancelar"
    // una reserva poniendo false. Lo cambiamos por cancelar(), que tiene un nombre mas claro
    // y solo cambia el estado a cancelada. El atributo ahora es private y se consulta
    // con estaCancelada().
    public void cancelar() {
        this.cancelada = true;
    }
}
