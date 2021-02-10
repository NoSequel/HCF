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
    public Integer WARZONE_RADIUS = config.getInt("map.warzone_radius");

    public Integer FACTION_SIZE = config.getInt("map.faction.faction_sizes");
    public Integer ALLIES = config.getInt("map.faction.allies");

    public Integer BALANCE = config.getInt("map.default_balance");
    public Integer CLAIM_BALANCE_MULTIPLIER = config.getInt("map.claim_balance_multiplier");

    public Integer BLOCK_OPERATION_SPEED = config.getInt("map.block_operation_speed");

    // enchantment limiting
    public Map<Enchantment, Integer> ENCHANTMENT_LIMITS = ImmutableMap.of(
            Enchantment.DAMAGE_ALL, config.getInt("enchant_limiter.sharpness"),
            Enchantment.PROTECTION_ENVIRONMENTAL, config.getInt("enchant_limiter.protection")
    );
}