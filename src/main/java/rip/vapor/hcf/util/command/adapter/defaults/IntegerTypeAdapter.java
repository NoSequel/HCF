package rip.vapor.hcf.util.command.adapter.defaults;

import org.bukkit.command.CommandSender;
import rip.vapor.hcf.util.command.adapter.TypeAdapter;

public class IntegerTypeAdapter implements TypeAdapter<Integer> {

    @Override
    public Integer convert(CommandSender sender, String source) throws Exception {
        return Integer.parseInt(source);
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}
