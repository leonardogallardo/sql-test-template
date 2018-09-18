package modelo;

import database.DbUtils;

import java.util.List;
import java.util.Map;

public class ChamadaPL {

    private String usuario;
    private String pacote;
    private String pl;
    private String schema;
    private List<ParametroDB> parametros;

    private ChamadaPL() {
    }

    public static ChamadaPL novo() {
        return new ChamadaPL();
    }

    public ChamadaPL usuario(String usuario) {
        this.usuario = usuario;
        return this;
    }

    public ChamadaPL pacote(String pacote) {
        this.pacote = pacote;
        return this;
    }

    public ChamadaPL pl(String pl) {
        this.pl = pl;
        return this;
    }

    public ChamadaPL schema(String schema) {
        this.schema = schema;
        return this;
    }

    public ChamadaPL parametros(List<ParametroDB> parametros) {
        this.parametros = parametros;
        return this;
    }

    public Map<String, Object> executaFunction() {
        return new DbUtils().executaFunction(
                usuario + "." + pacote + "." + pl,
                parametros,
                schema
        );
    }

    public Map<String, Object> executaProcedure() {
        return new DbUtils().executaProcedure(
                usuario + "." + pacote + "." + pl,
                parametros,
                schema
        );
    }
}
