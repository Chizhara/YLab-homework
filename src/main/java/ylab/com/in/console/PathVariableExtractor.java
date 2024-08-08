package ylab.com.in.console;

public class PathVariableExtractor {

    private PathVariableExtractor() {
    }

    /**
     * Извлекает {@link  Long} значение из пути.
     *
     * @param path строковое представление пути
     * @param index индекс, с которого начинается значение в строковом представлении пути.
     * @return Извлеченное значение.
     */
    public static Long extractLong(String path, Integer index) {
        path = path.substring(index);
        int tempIndex = !path.contains("/") ? path.length() : path.indexOf("/");
        String res = path.substring(0, tempIndex);
        return Long.parseLong(res);
    }
}
