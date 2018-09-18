package properties;

import java.util.HashMap;
import java.util.Map;

public class ObjectShare {

    private ObjectShare() {
    }

    private static ThreadLocal<Map<String, Object>> shared = new ThreadLocal<>();

    public static void set(final String chave, final Object valor) {
        Map<String, Object> map = shared.get();

        if (map == null) {
            map = new HashMap<>();
        }

        map.put(chave, valor);
        shared.set(map);
    }

    public static Object get(final String chave) {
        return shared.get().get(chave);
    }

}
