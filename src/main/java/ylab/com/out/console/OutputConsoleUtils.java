package ylab.com.out.console;

import ylab.com.model.console.Method;

public class OutputConsoleUtils {
    public void printMethods() {
        System.out.println("Выберите один из следующих методов");
        for (int i = 0; i < Method.values().length; i++) {
            System.out.println(("[" + i + 1 + "]" + ". " + Method.values()[i]));
        }
    }

    public void printPath() {
        System.out.println("Введите путь");
    }

    public void printObjectFilling() {
        System.out.println("Введите поле и значение через пробел.\nПри вводе пустой строки этап закончится");
    }

    public void printHeadersFilling() {
        System.out.println("Введите заголовок и значение через пробел.\nПри вводе пустой строки этап закончится");
    }
}
