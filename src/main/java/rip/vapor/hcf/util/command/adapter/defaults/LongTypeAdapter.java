package rip.vapor.hcf.util.command.adapter.defaults;

import org.bukkit.command.CommandSender;
import rip.vapor.hcf.util.command.adapter.TypeAdapter;

public class LongTypeAdapter implements TypeAdapter<Long> {
    @Override
    public Long convert(CommandSender sender, String source) throws Exception {
        return Long.parseLong(source);
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }
}
