package rip.vapor.hcf.util.command.adapter.defaults;

import rip.vapor.hcf.util.command.adapter.TypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerTypeAdapter implements TypeAdapter<Player> {

    @Override
    public Player convert(CommandSender sender, String source) {
        if (source.equalsIgnoreCase("@SELF")) {
            return (Player) sender;
        }

        return Bukkit.getPlayer(source) == null
                ? Bukkit.getPlayer(UUID.fromString(source))
                : Bukkit.getPlayer(source);
    }

    @Override
    public Class<Player> getType() {
        return Player.class;
    }
}
