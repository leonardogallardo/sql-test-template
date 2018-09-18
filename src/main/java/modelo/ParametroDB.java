package modelo;

import exception.InesperadoException;
import oracle.jdbc.OracleTypes;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ParametroDB {

    public ParametroDB(final String nome, final int tipo, final String entradaSaida, final Object valor) {
        this.nome = nome;
        this.tipo = tipo;
        this.entradaSaida = entradaSaida;

        if(tipo == OracleTypes.DATE) {
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date data;
            try {
                data = sdf.parse(valor.toString());
            } catch (ParseException e) {
                throw new InesperadoException(e);
            }

            this.valor = new Timestamp(data.getTime());

        } else {
            this.valor = valor;
        }
    }

    public ParametroDB(final String nome, final int tipo, final String entradaSaida) {
        this.nome = nome;
        this.tipo = tipo;
        this.entradaSaida = entradaSaida;
    }

    private String nome;
    private int tipo;
    private Object valor;
    private String entradaSaida;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public boolean isEntrada() {
        return entradaSaida.equalsIgnoreCase(Parametro.ENTRADA.getValor());
    }

    public static List<ParametroDB> geraLista(Map<String, List<Object>> params) {

        List<ParametroDB> parametros = new ArrayList<>();
        params.forEach((chave, valores) -> {

            int tipo;
            try {
                tipo = OracleTypes.class.getDeclaredField(valores.get(0).toString()).getInt(OracleTypes.class);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new InesperadoException(e);
            }

            String entradaSaida = valores.get(1).toString();

            if(entradaSaida.equalsIgnoreCase(Parametro.ENTRADA.getValor())
                && valores.size() == 3) {
                Object valor = valores.get(2);
                parametros.add(new ParametroDB(chave, tipo, entradaSaida, valor));
            } else if(entradaSaida.equalsIgnoreCase(Parametro.SAIDA.getValor())
                    && valores.size() == 3) {
                Object valor = valores.get(2);
                parametros.add(new ParametroDB(chave, tipo, entradaSaida, valor));
            } else {
                parametros.add(new ParametroDB(chave, tipo, entradaSaida));
            }
        });
        return parametros;
    }

    @Override
    public String toString() {
        return "\n    {" +
                " nome: '" + nome + '\'' +
                ", tipo: " + tipo +
                ", valor: " + valor +
                ", entradaSaida: '" + entradaSaida + '\'' +
                " }";
    }


}
