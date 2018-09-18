package plsql;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception.InesperadoException;
import org.junit.Before;
import properties.ObjectShare;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseTest {

    @Before
    public void before() {
        String env = System.getProperty("env");
        if (env == null || env.isEmpty()) {
            env = "homolog";
        }
        ObjectShare.set("env", env);
    }

    public Map<String, Map<String, List<Object>>> jsonToMap(final String file) {
        try {
            return new ObjectMapper().readValue(this.getClass().getResource(file), HashMap.class);
        } catch (IOException e) {
            throw new InesperadoException(e);
        }
    }

}
