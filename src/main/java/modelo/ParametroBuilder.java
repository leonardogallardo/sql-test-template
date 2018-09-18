package modelo;

public final class ParametroBuilder {

    private ParametroBuilder() {
    }

    public static EntradaBuilder entrada() {
        return new EntradaBuilder();
    }
    public static SaidaBuilder saida() {
        return new SaidaBuilder();
    }

    public static final class EntradaBuilder {
        private String nome;
        private int tipo;
        private Object valor;

        private EntradaBuilder() {
        }

        public static ParametroBuilder.EntradaBuilder novo() {
            return new ParametroBuilder.EntradaBuilder();
        }

        public EntradaBuilder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public EntradaBuilder tipo(int tipo) {
            this.tipo = tipo;
            return this;
        }

        public EntradaBuilder valor(Object valor) {
            this.valor = valor;
            return this;
        }

        public ParametroDB build() {
            return new ParametroDB(nome, tipo, Parametro.ENTRADA.getValor(), valor);
        }
    }

    public static final class SaidaBuilder {
        private String nome;
        private int tipo;

        private SaidaBuilder() {
        }

        public static ParametroBuilder.SaidaBuilder novo() {
            return new ParametroBuilder.SaidaBuilder();
        }

        public SaidaBuilder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public SaidaBuilder tipo(int tipo) {
            this.tipo = tipo;
            return this;
        }

        public ParametroDB build() {
            return new ParametroDB(nome, tipo, Parametro.SAIDA.getValor());
        }
    }
}
