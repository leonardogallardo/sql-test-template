package properties;

import java.io.File;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class PropertyLoader {

    private PropertyLoader() {
    }

    private static final String PATH = "env"
            + File.separator
            + ObjectShare.get("env").toString()
            + File.separator;

    private static Map<String, ResourceBundle> bundles = new ConcurrentHashMap<>();

    public static String load(final Properties source, final String property) {
        ResourceBundle resourceBundle = bundles.get(source.valor);
        if (resourceBundle == null) {
            resourceBundle = ResourceBundle.getBundle(source.getValor());
            bundles.put(source.valor, resourceBundle);
        }

        return resourceBundle.getString(property);
    }

    public enum Properties {

        DB_CONFIG(PATH + "dbConfig"),
        URLS(PATH + "urls");

        private String valor;

        public String getValor() {
            return this.valor;
        }

        Properties(String valor) {
            this.valor = valor;
        }

    }

}
