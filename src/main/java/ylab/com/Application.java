package ylab.com;

import ylab.com.configure.LiquibaseConfig;

public class Application {
    public static void main(String[] args) throws Exception {
        LiquibaseConfig.configure();
        //PropertiesStorage propertiesInitializer = new PropertiesStorage();
        //System.out.println( propertiesInitializer.getProperty("name"));

        ContextLoader.start();
    }
}
