package plsql;

import com.google.common.collect.Lists;
import modelo.ChamadaPL;
import modelo.ParametroBuilder;
import modelo.ParametroDB;
import oracle.jdbc.OracleTypes;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class PLBuilderTest extends BaseTest {

    @Test
    public void vanilla() {

        final String agencia = "0726";

        List<ParametroDB> parametros = Lists.newArrayList(
                ParametroBuilder.entrada().nome("pi_cod_agencia").tipo(OracleTypes.VARCHAR).valor(agencia).build(),
                ParametroBuilder.entrada().nome("pi_num_conta").tipo(OracleTypes.VARCHAR).valor("16854-4").build(),
                ParametroBuilder.saida().nome("po_vlr_saldo_disp").tipo(OracleTypes.NUMBER).build(),
                ParametroBuilder.saida().nome("po_vlr_saldo_bloq").tipo(OracleTypes.NUMBER).build(),
                ParametroBuilder.saida().nome("po_vlr_saldo_bloq_jud").tipo(OracleTypes.NUMBER).build(),
                ParametroBuilder.saida().nome("po_cod_status").tipo(OracleTypes.VARCHAR).build(),
                ParametroBuilder.entrada().nome("pi_lbanco").tipo(OracleTypes.BIT).valor(false).build(),
                ParametroBuilder.entrada().nome("pi_cod_sist_orig").tipo(OracleTypes.VARCHAR).valor("10").build()
        );

        Map<String, Object> retorno = ChamadaPL.novo()
                .usuario("CCOR_APP_OWNER")
                .pacote("PKGL_CCR1_UTIL")
                .pl("PRCL_CONSULTA_SALDO_CCOR")
                .schema("AG" + agencia)
                .parametros(parametros)
                .executaProcedure();

        System.out.println(retorno);
    }

    @Test
    public void paramsFromJson() {

        final String agencia = "0726";
        final Map<String, Map<String, List<Object>>> entradas = jsonToMap("parametros.json");
        List<ParametroDB> parametros = ParametroDB.geraLista(entradas.get("consulta_saldo_ccor"));

        Map<String, Object> retorno = ChamadaPL.novo()
                .usuario("CCOR_APP_OWNER")
                .pacote("PKGL_CCR1_UTIL")
                .pl("PRCL_CONSULTA_SALDO_CCOR")
                .schema("AG" + agencia)
                .parametros(parametros)
                .executaProcedure();

        System.out.println(retorno);
    }

}
