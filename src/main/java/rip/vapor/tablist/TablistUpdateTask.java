package rip.vapor.tablist;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.util.Objects;

@RequiredArgsConstructor
public class TablistUpdateTask extends Thread {

    private final long updateTime;

    @Override
    public void run() {
        while (true) {
            final TablistManager manager = TablistManager.INSTANCE;

            if (manager != null) {
                Bukkit.getOnlinePlayers().stream()
                        .map(manager::find)
                        .filter(Objects::nonNull)
                        .forEach(tab -> tab.hideRealPlayers().update());
            }

            try {
                Thread.sleep(updateTime);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }
}