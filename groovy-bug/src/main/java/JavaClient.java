import java.util.List;
import java.util.Map;

public class JavaClient {
    public static void main(String[] args) {
        GroovyClient groovyClient = new GroovyClient();
        Map<String, List<String>> properties = groovyClient.getProperties();
        for (String key : properties.keySet()) {
            System.out.println(properties.get(key));
        }
    }
}
