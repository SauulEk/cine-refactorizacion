import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SistemaReservas s = new SistemaReservas();
        Funcion f = new Funcion("Interestelar", 3, "20:30", 12.0);

        // Preserve whole object: ahora creamos los clientes aqui y los mandamos completos
        Cliente ana = new Cliente("Ana", "ana@mail.com", true);
        Cliente luis = new Cliente("Luis", "luis@mail.com", false);

        // Introduce parameter object: los asientos ya no van en dos arrays
        reservar(s, "Reserva Ana", ana, f,
                Arrays.asList(new Asiento('F', 5), new Asiento('F', 6)), "App");

        // Bug corregido: F6 ya esta ocupado, entonces la reserva de Luis falla
        // y F7 ya no se queda bloqueado
        reservar(s, "Reserva Luis", luis, f,
                Arrays.asList(new Asiento('F', 7), new Asiento('F', 6)), "Taquilla");

        // Hide method: ya no se usa f.ocupados directo, se usa getOcupados()
        System.out.println("Ocupados: " + f.getOcupados());

        // Encapsulate downcast: buscarReserva ya regresa Reserva, no hay que castear
        Reserva reservaAna = s.buscarReserva(1);
        System.out.println("Precio Ana (socio): " + reservaAna.getPrecio());

        // Replace error code with exception: los errores ahora salen con mensaje
        cancelar(s, 1);
        cancelar(s, 99);
    }

    // metodo auxiliar para no repetir el try/catch en cada reserva
    private static void reservar(SistemaReservas s, String etiqueta, Cliente cliente,
                                 Funcion funcion, List<Asiento> asientos, String canal) {
        try {
            int id = s.crearReserva(cliente, funcion, asientos, canal);
            System.out.println(etiqueta + ": " + id);
        } catch (ReservaException e) {
            System.out.println(etiqueta + ": Error - " + e.getMessage());
        }
    }

    // metodo auxiliar para no repetir el try/catch en cada cancelacion
    private static void cancelar(SistemaReservas s, int id) {
        try {
            s.cancelarReserva(id);
            System.out.println("Cancelar " + id + ": OK");
        } catch (ReservaException e) {
            System.out.println("Cancelar " + id + ": Error - " + e.getMessage());
        }
    }
}
