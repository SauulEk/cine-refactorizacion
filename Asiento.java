import java.util.Objects;

// TECNICA UTILIZADA: INTRODUCE PARAMETER OBJECT
// REFACTOR: Al aplicar esta tecnica se realizaron los siguientes cambios:
// antes los asientos se mandaban en dos arrays separados (char[] filas e int[] numeros)
// y habia que checar que tuvieran el mismo tamano. Hicimos esta clase Asiento para juntar
// la fila y el numero en un solo objeto, y ahora se manda una List<Asiento>.
// Tambien aqui se arma el codigo tipo "F5" con getCodigo() para no repetirlo en otros lados.
public class Asiento {
    private final char fila;
    private final int numero;

    public Asiento(char fila, int numero) {
        this.fila = fila;
        this.numero = numero;
    }

    public char getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    public String getCodigo() {
        return "" + fila + numero;
    }

    // equals y hashCode para poder comparar asientos (lo usamos para detectar repetidos)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asiento)) return false;
        Asiento otro = (Asiento) o;
        return fila == otro.fila && numero == otro.numero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fila, numero);
    }

    @Override
    public String toString() {
        return getCodigo();
    }
}
