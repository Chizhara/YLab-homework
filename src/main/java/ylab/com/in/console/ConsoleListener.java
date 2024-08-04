package ylab.com.in.console;

import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.console.HandlerKey;
import ylab.com.model.console.Method;
import ylab.com.out.console.OutputConsoleUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConsoleListener {
    private final InputConsoleDispatcher inputConsoleDispatcher;
    private final OutputConsoleUtils outputConsoleUtils;
    private final Scanner scanner;
    private Thread process;
    private boolean isStarted = false;

    public ConsoleListener(InputConsoleDispatcher inputConsoleDispatcher) {
        this.inputConsoleDispatcher = inputConsoleDispatcher;
        this.scanner = new Scanner(System.in);
        this.outputConsoleUtils = new OutputConsoleUtils();
    }

    public synchronized boolean start() {
        if (isStarted) {
            return false;
        }
        process = new Thread(this::listen);
        process.start();
        return isStarted = true;
    }

    public synchronized boolean stop() {
        if (!isStarted) {
            return true;
        }
        process.interrupt();
        return isStarted = false;
    }

    private void listen() {
        while (isStarted) {
            try {
                HandlerKey handlerKey = getProcessorKey();

                Map<String, String> rawObject = getRawObject();
                Map<String, String> headers = getHeaders();

                try {
                    System.out.println(inputConsoleDispatcher
                        .dispatch(new ConsoleRequest(handlerKey, rawObject, headers)));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            } catch (RuntimeException e) {
                System.out.println("Произошла непредвиденная ошибка!" + e.getMessage());
            }
        }
    }

    private HandlerKey getProcessorKey() {
        Method method = getMethod();
        String path = getPath();
        return new HandlerKey(method, path);
    }

    private Method getMethod() {
        outputConsoleUtils.printMethods();
        int pos = Integer.parseInt(scanner.nextLine());
        return Method.values()[pos - 1];
    }

    private String getPath() {
        outputConsoleUtils.printPath();
        return scanner.nextLine();
    }

    private Map<String, String> getRawObject() {
        outputConsoleUtils.printObjectFilling();
        Map<String, String> rawObject = new HashMap<>();
        String line;
        while (!(line = scanner.nextLine()).isBlank()) {
            int index = line.indexOf(" ");
            String name = line.substring(0, index);
            String value = line.substring(index + 1);
            rawObject.put(name, value);
        }

        return rawObject;
    }

    private Map<String, String> getHeaders() {
        outputConsoleUtils.printHeadersFilling();
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = scanner.nextLine()).isBlank()) {
            String[] rawField = line.split(" ");
            headers.put(rawField[0], rawField[1]);
        }

        return headers;
    }
}
