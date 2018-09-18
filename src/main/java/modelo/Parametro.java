package modelo;

public enum Parametro {

    ENTRADA("ENTRADA"), SAIDA("SAIDA");

    private String valor;

    Parametro(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return this.valor;
    }

}
