package database;

import com.google.common.collect.Maps;
import exception.InesperadoException;
import modelo.ParametroDB;
import oracle.jdbc.OracleTypes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import properties.PropertyLoader;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class DbUtils {

    private static final String NLINE = "\n";
    private static final String ALTER_SCHEMA = "alter session set current_schema = {0}";

    private final JdbcTemplate jdbc;

    public DbUtils() {
        String url = PropertyLoader.load(PropertyLoader.Properties.DB_CONFIG, "url");
        String username = PropertyLoader.load(PropertyLoader.Properties.DB_CONFIG, "username");
        String password = PropertyLoader.load(PropertyLoader.Properties.DB_CONFIG, "password");
        String driver = PropertyLoader.load(PropertyLoader.Properties.DB_CONFIG, "driverClassName");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        jdbc = new JdbcTemplate(dataSource);
    }

    public DbUtils(Map<String, Object> config) {
        String url = (String) config.get("url");
        String username = (String) config.get("username");
        String password = (String) config.get("password");
        String driver = (String) config.get("driverClassName");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        jdbc = new JdbcTemplate(dataSource);
    }

    public Object getValor(String query) {
        return jdbc.queryForObject(query, Object.class);
    }

    public Object getValor(String query, String... params) {
        query = replaceParams(query, params);
        return getValor(query);
    }

    public Map<String, Object> getLinha(String query) {
        Map<String, Object> map = jdbc.queryForMap(query);
        return getStringObjectMap(map);
    }

    public Map<String, Object> getLinha(String query, String... params) {
        query = replaceParams(query, params);
        return getLinha(query);
    }

    public List<Map<String, Object>> getLinhas(String query) {
        return jdbc.queryForList(query);
    }

    public List<Map<String, Object>> getLinhas(String query, String... params) {
        query = replaceParams(query, params);
        return getLinhas(query);
    }

    public void update(final String sql) {
        jdbc.update(sql);
    }

    public void update(String sql, final String... params) {
        sql = replaceParams(sql, params);
        update(sql);
    }

    public void delete(final String sql) {
        update(sql);
    }

    public void delete(final String sql, final String... params) {
        update(sql, params);
    }

    private String replaceParams(String query, String[] params) {
        for (String param: params) {
            query = query.replaceFirst(Pattern.quote("?"), param);
        }
        return query;
    }

    private Map<String, Object> getStringObjectMap(Map<String, Object> map) {
        Map newMap = new ConcurrentHashMap(map);
        map.forEach((chave, valor) -> {
            Object o = map.get(chave);
            if (o == null) {
                newMap.remove(chave);
                newMap.put(chave, "");
            }
        });
        return newMap;
    }

    // Métodos de execução de functions e procedures utilizando CallableStatement
    public Map<String, Object> executaFunction(final String pkgNomeFunction, List<ParametroDB> parametros) {
        return executaNoDB(parametros, pkgNomeFunction, true, null);
    }

    public Map<String, Object> executaProcedure(final String pkgNomeFunction, List<ParametroDB> parametros) {
        return executaNoDB(parametros, pkgNomeFunction, false, null);
    }

    public Map<String, Object> executaFunction(final String pkgNomeFunction, final List<ParametroDB> parametros, final String schema) {
        return executaNoDB(parametros, pkgNomeFunction, true, schema);
    }

    public Map<String, Object> executaProcedure(final String pkgNomeFunction, final List<ParametroDB> parametros, final String schema) {
        return executaNoDB(parametros, pkgNomeFunction, false, schema);
    }

    private Map<String, Object> executaNoDB(List<ParametroDB> parametros, String funcaoProcedure, boolean isFunction, String schema) {

        String sttmtString = getExecutionString(parametros, funcaoProcedure);
        boolean isRetornoBoolean = parametros.stream()
                .filter(parametroDB -> parametroDB.getTipo() == OracleTypes.BIT
                        && parametroDB.getNome().equalsIgnoreCase("result")).count() > 0;

        if(schema != null) {
            jdbc.execute(MessageFormat.format(ALTER_SCHEMA, schema));
        }

        Map<String, Object> retorno = Maps.newHashMap();
        try (Connection connection = jdbc.getDataSource().getConnection()) {
            CallableStatement cs = getStatement(sttmtString, isFunction, isRetornoBoolean, connection);
            registraParametros(parametros, cs);
            cs.execute();

            getRetornos(parametros, retorno, cs);

        } catch (SQLException e) {
            throw new InesperadoException(e);
        }

        return retorno;
    }

    private void getRetornos(List<ParametroDB> parametros, Map<String, Object> retorno, CallableStatement cs) {
        int index = 1;
        for (ParametroDB parametro: parametros) {
            if(!parametro.isEntrada()) {
                try {
                    retorno.put(parametro.getNome(), cs.getObject(index));
                } catch (SQLException e) {
                    throw new InesperadoException(e);
                }
            }
            index++;
        }
    }

    private String getExecutionString(List<ParametroDB> parametros, String funcaoProcedure) {
        int size = 0;
        if (parametros.stream().filter(parametroDB -> parametroDB.getNome().equalsIgnoreCase("result")).count() == 1) {
            size = -1;
        }

        int sizeFinal = size + parametros.size();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < sizeFinal; i++) {
            ParametroDB param = null;

            if(size == -1) {
                param = parametros.get(1 + i);
            } else {
                param = parametros.get(i);
            }

            if(param.getTipo() == OracleTypes.BIT) {
                sb.append("(CASE ? WHEN 1 THEN TRUE ELSE FALSE END), ");
            } else {
                sb.append("?, ");
            }

        }
        String str = (funcaoProcedure + "(" + sb.toString() + ")");
        str = str.replace(", )", ")");

        return str;
    }

    private CallableStatement getStatement(String executar, boolean isFunction, boolean isRetornoBoolean, Connection conn) throws SQLException {
        CallableStatement callableStatement = null;
        String chamada;
        if (isFunction) {
            if(isRetornoBoolean) {
                chamada = MessageFormat.format(new StringBuilder()
                        .append("BEGIN").append(NLINE)
                        .append("    ? := CASE {0} ").append(NLINE)
                        .append("        WHEN TRUE THEN 1 ").append(NLINE)
                        .append("        ELSE 0 ").append(NLINE)
                        .append("    END; ").append(NLINE)
                        .append("END; ").append(NLINE)
                        .toString(), executar);


            } else {
                chamada = "{ ? = call ".concat(executar).concat(" }");
            }
        } else {
            chamada = "{ call ".concat(executar).concat(" }");
        }
        callableStatement = conn.prepareCall(chamada);
        return callableStatement;
    }

    private void registraParametros(List<ParametroDB> parametros, CallableStatement callableStatement) {
        int index = 1;

        for (ParametroDB parametroDB: parametros) {
            try {
                if(parametroDB.isEntrada()) {
                    callableStatement.setObject(index, parametroDB.getValor(), parametroDB.getTipo());
                } else if(parametroDB.getValor() != null) {
                    callableStatement.registerOutParameter(index, parametroDB.getTipo(), parametroDB.getValor().toString());
                } else {
                    callableStatement.registerOutParameter(index, parametroDB.getTipo());
                }
            } catch (SQLException e) {
                throw new InesperadoException(e);
            }
            index++;
        }
    }

}