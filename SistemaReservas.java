import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SistemaReservas {
    private static final double DESCUENTO_SOCIO = 0.15;

    // TECNICA UTILIZADA: HIDE METHOD
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // db y contador eran public, los pusimos private porque solo esta clase deberia
    // guardar reservas y dar los ids. Tambien pusimos private los metodos que solo se usan
    // aqui adentro (validarDatos, validarDisponibilidad, calcularPrecio, calcularPrecioGeneral,
    // calcularPrecioSocio y aplicarDescuento). A db le cambiamos el nombre a "reservas"
    // para que se entienda que guarda.
    //
    // TECNICA UTILIZADA: ENCAPSULATE DOWNCAST
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // el mapa era Map<Integer, Object> y por eso habia que hacer (Reserva) db.get(id)
    // cada vez. Lo cambiamos a Map<Integer, Reserva> y todo se busca con buscarReserva(id),
    // asi ya no hay que castear ni aqui ni en Main.
    private final Map<Integer, Reserva> reservas = new HashMap<>();
    private int contador = 1;

    // TECNICA UTILIZADA: RENAME METHOD
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // el metodo se llamaba hacer() y no se entendia que hacia, lo cambiamos a crearReserva().
    //
    // TECNICA UTILIZADA: REMOVE PARAMETER
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // quitamos el parametro boolean imprimir porque nunca se usaba dentro del metodo
    // (siempre se mandaba false).
    //
    // TECNICA UTILIZADA: PRESERVE WHOLE OBJECT
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // antes se mandaban nombre, email y socio por separado y el Cliente se creaba aqui adentro.
    // Ahora se manda directamente el objeto Cliente.
    //
    // TECNICA UTILIZADA: INTRODUCE PARAMETER OBJECT
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // cambiamos los arrays char[] filas e int[] numeros por una List<Asiento>.
    //
    // TECNICA UTILIZADA: ADD PARAMETER
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // el canal que antes se ignoraba ahora se le pasa a Reserva para que se guarde.
    //
    // TECNICA UTILIZADA: REPLACE ERROR CODE WITH EXCEPTION
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // ya no regresa -1, -2 o -3. Si todo sale bien regresa el id de la reserva y si hay
    // un error lanza ReservaException con un mensaje.
    //
    // BUG CORREGIDO: antes se iban ocupando los asientos uno por uno y si uno fallaba,
    // los anteriores se quedaban ocupados (por eso F7 se quedaba bloqueado). Ahora primero
    // revisamos que todos esten libres y ya despues los ocupamos.
    public int crearReserva(Cliente cliente, Funcion funcion, List<Asiento> asientos, String canal) {
        validarDatos(cliente, funcion, asientos);
        validarDisponibilidad(funcion, asientos);

        for (Asiento asiento : asientos) {
            funcion.ocupar(asiento);
        }

        double precio = calcularPrecio(cliente, funcion, asientos);
        Reserva reserva = new Reserva(contador, cliente, funcion, asientos, canal, precio);
        reservas.put(contador, reserva);
        contador++;
        return reserva.getId();
    }

    // NOTA: tambien revisamos que no vengan asientos repetidos (por ejemplo F5 y F5),
    // porque antes eso lo detectaba la excepcion de ocupar() y al separarlo se perdia.
    private void validarDatos(Cliente cliente, Funcion funcion, List<Asiento> asientos) {
        if (cliente == null || cliente.getNombre() == null || funcion == null) {
            throw new ReservaException("Datos incompletos: falta el cliente o la funcion");
        }
        if (asientos == null || asientos.isEmpty()) {
            throw new ReservaException("Debe solicitar al menos un asiento");
        }
        if (new HashSet<>(asientos).size() != asientos.size()) {
            throw new ReservaException("La solicitud tiene asientos repetidos");
        }
    }

    // TECNICA UTILIZADA: REPLACE EXCEPTION WITH TEST
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // antes se llamaba ocupar() dentro de un try y con el catch se sabia si el asiento
    // estaba ocupado, o sea se usaba la excepcion para controlar el flujo. Quitamos el
    // try/catch y ahora preguntamos antes con estaOcupado(). Como revisamos todos los
    // asientos antes de ocupar alguno, esto tambien arregla el bug de F7.
    // Si si hay uno ocupado se lanza ReservaException para avisarle a quien llamo.
    private void validarDisponibilidad(Funcion funcion, List<Asiento> asientos) {
        for (Asiento asiento : asientos) {
            if (funcion.estaOcupado(asiento)) {
                throw new ReservaException("Asiento ocupado: " + asiento.getCodigo());
            }
        }
    }

    // TECNICA UTILIZADA: REPLACE PARAMETER WITH METHOD
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // antes se mandaba el tipo de tarifa como texto (socio ? "socio" : "general"), pero ese
    // dato ya lo tiene el cliente. Quitamos el parametro tipo y ahora el metodo lo saca
    // con cliente.esSocio(). Recibe cliente, funcion y asientos en vez de la Reserva porque
    // el precio se calcula antes de crear la reserva.
    private double calcularPrecio(Cliente cliente, Funcion funcion, List<Asiento> asientos) {
        double base = funcion.getPrecio() * asientos.size();
        if (cliente.esSocio()) {
            return calcularPrecioSocio(base);
        }
        return calcularPrecioGeneral(base);
    }

    // TECNICA UTILIZADA: REPLACE PARAMETER WITH EXPLICIT METHODS
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // la tarifa se escogia comparando textos con tipo.equals("general") y tipo.equals("socio"),
    // y si se escribia mal el texto no marcaba error. Hicimos un metodo para cada tarifa:
    // calcularPrecioGeneral() y calcularPrecioSocio(), y ya no se usan los Strings.
    private double calcularPrecioGeneral(double base) {
        return base;
    }

    private double calcularPrecioSocio(double base) {
        return aplicarDescuento(base, DESCUENTO_SOCIO);
    }

    // TECNICA UTILIZADA: PARAMETERIZE METHOD
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // descuentoDel15() y descuentoDel25() eran casi iguales, solo cambiaba el porcentaje.
    // Los juntamos en aplicarDescuento(precio, porcentaje). El de 25 no se usaba, pero si
    // luego se necesita solo se llama aplicarDescuento(precio, 0.25).
    // Dejamos la misma formula para que el precio de Ana siga saliendo 20.4.
    private double aplicarDescuento(double precio, double porcentaje) {
        return precio - precio * porcentaje;
    }

    // TECNICA UTILIZADA: RENAME METHOD
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // cambiamos cancelar() a cancelarReserva() para que vaya igual que crearReserva()
    // y buscarReserva().
    //
    // TECNICA UTILIZADA: REPLACE ERROR CODE WITH EXCEPTION
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // antes regresaba 0 si salia bien, -1 si no existia y -2 si ya estaba cancelada.
    // Ahora es void y si hay error lanza ReservaException.
    //
    // TECNICA UTILIZADA: ENCAPSULATE DOWNCAST
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // ya no se hace (Reserva) db.get(id), se usa buscarReserva(id) que ya regresa una Reserva.
    public void cancelarReserva(int id) {
        Reserva reserva = buscarReserva(id);
        if (reserva.estaCancelada()) {
            throw new ReservaException("La reserva " + id + " ya estaba cancelada");
        }
        for (Asiento asiento : reserva.getAsientos()) {
            reserva.getFuncion().liberar(asiento);
        }
        reserva.cancelar();
    }

    // TECNICA UTILIZADA: ENCAPSULATE DOWNCAST
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // buscar() regresaba Object y el que lo usaba tenia que castear a Reserva.
    // Ahora regresa Reserva directo, entonces en Main ya no se hace (Reserva) s.buscar(1).
    //
    // TECNICA UTILIZADA: RENAME METHOD
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // le cambiamos el nombre de buscar() a buscarReserva() para que diga que busca.
    //
    // TECNICA UTILIZADA: REPLACE ERROR CODE WITH EXCEPTION
    // REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
    // si no encontraba la reserva regresaba null. Ahora lanza ReservaException.
    public Reserva buscarReserva(int id) {
        Reserva reserva = reservas.get(id);
        if (reserva == null) {
            throw new ReservaException("No existe la reserva con id " + id);
        }
        return reserva;
    }
}
