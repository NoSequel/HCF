package rip.vapor.hcf.listeners.team;

import rip.vapor.hcf.module.ModuleHandler;
import rip.vapor.hcf.team.TeamModule;
import rip.vapor.hcf.team.enums.TeamType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageListeners implements Listener {

    private final TeamModule teamModule;

    /**
     * Constructor to make a new damage listener object
     *
     * @param handler the handler to get the modules from
     */
    public DamageListeners(ModuleHandler handler) {
        this.teamModule = handler.find(TeamModule.class);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!event.getDamager().equals(event.getEntity()) && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Player damager = null;

            if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
                damager = (Player) ((Projectile) event.getDamager()).getShooter();
            } else if (event.getDamager() instanceof Player) {
                damager = (Player) event.getDamager();
            }

            if (teamModule.findTeam(player.getLocation()).get().getGeneralData().getType().equals(TeamType.SAFEZONE_TEAM)) {
                event.setCancelled(true);
            }

            if (damager != null) {
                if (teamModule.findTeam(player).isPresent() && teamModule.findTeam(damager).isPresent() && teamModule.findTeam(damager).get().equals(teamModule.findTeam(player).get())) {
                    damager.sendMessage(ChatColor.YELLOW + "You cannot hurt " + ChatColor.DARK_GREEN + player.getName() + ChatColor.YELLOW + ".");
                    event.setCancelled(true);
                }
            }
        }
    }
}