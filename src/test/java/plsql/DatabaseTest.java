package plsql;
import database.DbUtils;
import database.Sql;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class DatabaseTest extends BaseTest {

    @Test
    public void test() {

        DbUtils database = new DbUtils();

        database.delete(Sql.get("removeDataFromtableWhere"));

        List<Map<String, Object>> retorno = database.getLinhas(Sql.get("buscaValoresNaTabela"),
                "parametro01", "parametro02");
    }

}
