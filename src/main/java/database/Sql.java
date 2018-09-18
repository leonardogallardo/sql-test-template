package database;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.extended.NamedMapConverter;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Sql {

    private Sql() {
    }

    private static Map<String, String> sqls = new ConcurrentHashMap<>();

    public static String get(String name) {
        if (sqls.isEmpty()) {
            XStream xStream = new XStream();
            xStream.registerConverter(
                    new NamedMapConverter(xStream.getMapper(), "sql", "chave", String.class, "valor", String.class));
            sqls.putAll((Map<String, String>) xStream.fromXML(new File(
                    Thread.currentThread().getContextClassLoader().getResource("sql.xml").getFile())));

        }
        return sqls.get(name);
    }

}
