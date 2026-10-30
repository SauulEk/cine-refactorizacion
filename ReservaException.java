// TECNICA UTILIZADA: REPLACE ERROR CODE WITH EXCEPTION
// REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
// creamos esta excepcion para ya no regresar numeros como -1, -2 o -3 cuando algo sale mal.
// Asi el error trae un mensaje que si se entiende (por ejemplo "Asiento ocupado: F6").
// La pusimos como RuntimeException para no tener que poner throws en todos los metodos.
public class ReservaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ReservaException(String mensaje) {
        super(mensaje);
    }
}
