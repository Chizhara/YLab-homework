package ylab.com.in.console;

import lombok.Getter;

@Getter
public abstract class AbsInputConsoleController implements InputConsoleController {

    private final String basePath;

    public AbsInputConsoleController(String basePath)
    {
        this.basePath = basePath;
    }
}
