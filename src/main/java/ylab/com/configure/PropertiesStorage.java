package ylab.com.configure;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesStorage {

    private final static String PROPERTIES_FILE_NAME = "./src/main/resources/application.properties";
    private static final PropertiesStorage INSTANCE;

    static {
        try {
            INSTANCE = new PropertiesStorage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Properties properties;

    private PropertiesStorage() throws IOException {
        this.properties = new Properties();
        properties.load(new FileInputStream(PROPERTIES_FILE_NAME));
    }

    public static PropertiesStorage getInstance() {
        return INSTANCE;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
