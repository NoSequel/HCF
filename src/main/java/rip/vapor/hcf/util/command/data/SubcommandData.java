package rip.vapor.hcf.util.command.data;

import rip.vapor.hcf.util.command.annotation.Subcommand;
import lombok.Data;

import java.lang.reflect.Method;

@Data
public class SubcommandData {

    private final Subcommand subcommand;
    private final Method method;
    private final Object commandObject;

    public SubcommandData(Object commandObject, Method method) {
        this.subcommand = method.getAnnotation(Subcommand.class);
        this.commandObject = commandObject;
        this.method = method;
    }

}
