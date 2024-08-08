package ylab.com;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import ylab.com.configure.PostgresConnector;
import ylab.com.in.console.ConsoleListener;
import ylab.com.in.console.InputConsoleController;
import ylab.com.in.console.InputConsoleDispatcher;
import ylab.com.in.console.impl.CarConsoleController;
import ylab.com.in.console.impl.CarOrderConsoleController;
import ylab.com.in.console.impl.EmployerConsoleController;
import ylab.com.in.console.impl.LogConsoleController;
import ylab.com.in.console.impl.UserConsoleController;
import ylab.com.mapper.CarMapperImpl;
import ylab.com.mapper.LogMapperImpl;
import ylab.com.mapper.UserMapperImpl;
import ylab.com.repository.CarOrderRepository;
import ylab.com.repository.CarRepository;
import ylab.com.repository.LogRepository;
import ylab.com.repository.UserRepository;
import ylab.com.repository.impl.CarOrderRepositoryImpl;
import ylab.com.repository.impl.CarRepositoryImpl;
import ylab.com.repository.impl.LogRepositoryImpl;
import ylab.com.repository.impl.UserRepositoryImpl;
import ylab.com.service.AuthService;
import ylab.com.service.CarOrderService;
import ylab.com.service.CarService;
import ylab.com.service.LogService;
import ylab.com.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ContextLoader {
    public static void start() {
        UserMapperImpl userMapper = new UserMapperImpl();
        CarMapperImpl carMapper = new CarMapperImpl();
        LogMapperImpl logMapper = new LogMapperImpl();

        PostgresConnector connector = PostgresConnector.getInstance();

        UserRepository userRepository = new UserRepositoryImpl(connector, userMapper);
        CarRepository carRepository = new CarRepositoryImpl(connector, carMapper);
        CarOrderRepository carOrderRepository = new CarOrderRepositoryImpl(connector, carMapper);
        LogRepository logRepository = new LogRepositoryImpl(connector, logMapper);

        UserService userService = new UserService(userRepository, userMapper);
        AuthService authService = new AuthService(userRepository);
        CarService carService = new CarService(carOrderRepository, carRepository, carMapper);
        CarOrderService carOrderService = new CarOrderService(carOrderRepository, carMapper, userService, carService);
        LogService logService = new LogService(logRepository, userService, logMapper);

        UserConsoleController userConsoleController =
            new UserConsoleController(authService, userService, userMapper, logService);
        CarConsoleController carConsoleController =
            new CarConsoleController(carService, carMapper, authService, logService);
        CarOrderConsoleController carOrderConsoleController =
            new CarOrderConsoleController(carMapper, carOrderService, authService, logService);
        EmployerConsoleController employerConsoleController =
            new EmployerConsoleController(authService, userService, userMapper, logService);
        LogConsoleController logConsoleController =
            new LogConsoleController(logService, logMapper, authService);

        List<InputConsoleController> consoleControllers = new ArrayList<>();
        consoleControllers.add(userConsoleController);
        consoleControllers.add(carConsoleController);
        consoleControllers.add(carOrderConsoleController);
        consoleControllers.add(employerConsoleController);
        consoleControllers.add(logConsoleController);

        InputConsoleDispatcher inputConsoleDispatcher = new InputConsoleDispatcher(consoleControllers);

        ConsoleListener consoleListener = new ConsoleListener(inputConsoleDispatcher);
        consoleListener.start();

        Properties properties = new Properties();
// load properties from classpath


    }
}
