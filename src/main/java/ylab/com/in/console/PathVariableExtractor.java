package ylab.com.in.console;

import java.util.UUID;

public class PathVariableExtractor {

    private PathVariableExtractor() {
    }

    public static UUID extractUUID(String path, Integer index) {
        path = path.substring(index);
        int tempIndex = !path.contains("/") ? path.length() : path.indexOf("/");
        String res = path.substring(0, tempIndex);
        return UUID.fromString(res);
    }
}
