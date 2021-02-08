package rip.vapor.hcf;

import com.google.common.collect.ImmutableMap;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.util.Map;

@UtilityClass
public class VaporConstants {

    private final FileConfiguration config = Vapor.getInstance().getConfig();

    public boolean KITMAP_ENABLED = config.getBoolean("kitmap.kitmap_enabled");
    public Integer BORDER_SIZE = config.getInt("map.border_size");

    // enchantment limiting
    public Map<Enchantment, Integer> ENCHANTMENT_LIMITS = ImmutableMap.of(
            Enchantment.DAMAGE_ALL, config.getInt("enchant_limiter.sharpness"),
            Enchantment.PROTECTION_ENVIRONMENTAL, config.getInt("enchant_limiter.protection")
    );
}