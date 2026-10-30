// NOTA: como ahora mandamos el objeto Cliente completo (Preserve whole object),
// pusimos los atributos como private y les hicimos getters. No tiene setters porque
// los datos del cliente no cambian durante la reserva. Usamos esSocio() en vez de getSocio()
// porque se lee mejor en los if.
public class Cliente {
    private final String nombre;
    private final String email;
    private final boolean socio;

    public Cliente(String nombre, String email, boolean socio) {
        this.nombre = nombre;
        this.email = email;
        this.socio = socio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public boolean esSocio() {
        return socio;
    }
}
