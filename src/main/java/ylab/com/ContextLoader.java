package ylab.com;

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
import ylab.com.repository.impl.InMemoryCarOrderRepository;
import ylab.com.repository.impl.InMemoryCarRepository;
import ylab.com.repository.impl.InMemoryLogRepository;
import ylab.com.repository.impl.InMemoryUserRepository;
import ylab.com.service.AuthService;
import ylab.com.service.CarOrderService;
import ylab.com.service.CarService;
import ylab.com.service.LogService;
import ylab.com.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class ContextLoader {
    public static void start() {
        UserRepository userRepository = new InMemoryUserRepository();
        CarRepository carRepository = new InMemoryCarRepository();
        CarOrderRepository carOrderRepository = new InMemoryCarOrderRepository();
        LogRepository logRepository = new InMemoryLogRepository();

        UserMapperImpl userMapper = new UserMapperImpl();
        CarMapperImpl carMapper = new CarMapperImpl();
        LogMapperImpl logMapper = new LogMapperImpl();

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
    }
}
