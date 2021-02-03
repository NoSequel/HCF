package rip.vapor.tablist;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.vapor.tablist.client.ClientVersion;
import rip.vapor.tablist.entry.TablistElement;
import rip.vapor.tablist.entry.TablistEntry;
import rip.vapor.tablist.reflection.ReflectionConstants;

import java.util.Arrays;
import java.util.UUID;

@Getter
public class Tablist {

    public static final Object[] GAME_PROFILES;
    public static final String[] TAB_NAMES;

    public static String[] BLANK_SKIN = {"eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=", "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw="};

    private final ClientVersion version;
    private final Player player;
    private boolean initiated;

    public Tablist(Player player) {
        this.player = player;
        this.version = ClientVersion.getVersion(player);

        for(int i = 0; i < 80; i++) {
            Object packet = ReflectionConstants.SCOREBOARD_TEAM_CONSTRUCTOR.invoke();
            ReflectionConstants.SCOREBOARD_TEAM_NAME.set(packet, TAB_NAMES[i]);
            ReflectionConstants.SCOREBOARD_TEAM_PLAYERS.get(packet).add(TAB_NAMES[i]);
            sendPacket(player, packet);
        }

        this.update();
    }

    /**
     * Send a new packet to a player
     *
     * @param player      the player
     * @param useProfiles whether profiles should be used
     * @param ping        the displayed ping
     * @param index       the slot
     * @param prefix      the prefix
     * @param suffix      the suffix
     * @param name        the name
     */
    public void sendPacket(Player player, boolean useProfiles, int ping, int index, String prefix, String suffix, String name) {
        Object packet = ReflectionConstants.SCOREBOARD_TEAM_CONSTRUCTOR.invoke();

        ReflectionConstants.SCOREBOARD_TEAM_NAME.set(packet, TAB_NAMES[index]);
        ReflectionConstants.SCOREBOARD_TEAM_ACTION.set(packet, 2);
        ReflectionConstants.SCOREBOARD_TEAM_PREFIX.set(packet, prefix);
        ReflectionConstants.SCOREBOARD_TEAM_SUFFIX.set(packet, suffix);

        this.sendPacket(player, packet);


        packet = ReflectionConstants.TAB_PACKET_CONSTRUCTOR.invoke();
        Object profile = GAME_PROFILES[index];

        ReflectionConstants.TAB_PACKET_ACTION.set(packet, 0);
        ReflectionConstants.TAB_PACKET_LATENCY.set(packet, ping);

        if (useProfiles) {
            ReflectionConstants.TAB_PACKET_PROFILE.set(packet, profile);
        } else {
            ReflectionConstants.TAB_PACKET_NAME.set(packet, name);
        }


        this.sendPacket(player, packet);
    }

    /**
     * Send a new packet to a player
     *
     * @param player the player
     * @param packet the packet
     */
    public void sendPacket(Player player, Object packet) {
        final Object handle = ReflectionConstants.GET_HANDLE_METHOD.invoke(player);
        final Object connection = ReflectionConstants.PLAYER_CONNECTION.get(handle);

        ReflectionConstants.SEND_PACKET.invoke(connection, packet);
    }

    /**
     * Update the tablist
     */
    public void update() {
        if(!this.initiated) {
            this.addFakePlayers();
        }

        final TablistManager manager = TablistManager.INSTANCE;

        if (manager != null) {
            final TablistElement element = manager.getSupplier().getEntries(player);
            final boolean useProfiles = version.ordinal() != 0;
            final int magic = useProfiles ? 4 : 3;

            for (int i = 0; i < magic * 20; i++) {
                final int x = i % magic;
                final int y = i / magic;

                final TablistEntry entry = element.getEntry(x, y);
                final String name = TAB_NAMES[i];
                final String[] splitText = this.splitText(entry.getText());

                this.sendPacket(player, useProfiles, entry.getPing(), i, splitText[0], splitText[1], name);
            }
        }
    }

    /**
     * Split the text to display on the tablist
     *
     * @param text the text to split
     * @return the split text
     */
    private String[] splitText(String text) {

        if (text.length() < 17) {
            return new String[]{text, ""};
        } else {
            final String left = text.substring(0, 16);
            final String right = text.substring(16);

            if (left.endsWith("§")) {
                return new String[]{left.substring(0, left.toCharArray().length - 1), StringUtils.left(ChatColor.getLastColors(left) + "§" + right, 16)};
            } else {
                return new String[]{left, StringUtils.left(ChatColor.getLastColors(left) + right, 16)};
            }
        }
    }

    /**
     * Hide the real players from the tablist
     *
     * @return the current tablist instance
     */
    public Tablist hideRealPlayers() {
        if (initiated) {
            Bukkit.getOnlinePlayers().stream()
                    .filter(other -> player.canSee(other) || player.equals(other))
                    .forEach(other -> {
                        final Object packet = ReflectionConstants.TAB_PACKET_CONSTRUCTOR.invoke();

                        if (version.ordinal() != 0) {
                            Object profile = ReflectionConstants.GET_PROFILE_METHOD.invoke(other);
                            ReflectionConstants.TAB_PACKET_PROFILE.set(packet, profile);
                        } else {
                            ReflectionConstants.TAB_PACKET_NAME.set(packet, other.getName());
                        }
                        ReflectionConstants.TAB_PACKET_ACTION.set(packet, 4);
                        sendPacket(player, packet);
                    });
        }
        return this;
    }


    /**
     * Hide the fake players from the tablist
     *
     * @return the current tab instance
     */
    public Tablist hideFakePlayers() {
        if (!initiated) {
            final boolean useProfiles = version.ordinal() != 0;

            Arrays.stream(GAME_PROFILES).forEach(other -> {
                Object packet = ReflectionConstants.TAB_PACKET_CONSTRUCTOR.invoke();

                if (useProfiles) {
                    ReflectionConstants.TAB_PACKET_PROFILE.set(packet, other);
                } else {
                    ReflectionConstants.TAB_PACKET_NAME.set(packet, ReflectionConstants.GAME_PROFILE_NAME.get(other));
                }

                ReflectionConstants.TAB_PACKET_ACTION.set(packet, 4);
                sendPacket(player, packet);
            });
        }

        return this;
    }

    /**
     * Add the fake players to the tablist
     */
    private void addFakePlayers() {
        if (!initiated) {
            final boolean useProfiles = version.ordinal() != 0;
            final int magic = useProfiles ? 4 : 3;

            for (int i = 0; i < (magic * 20); i++) {
                Object packet = ReflectionConstants.TAB_PACKET_CONSTRUCTOR.invoke();
                Object profile = GAME_PROFILES[i];

                ReflectionConstants.TAB_PACKET_ACTION.set(packet, 0);
                ReflectionConstants.TAB_PACKET_LATENCY.set(packet, -1);

                if (useProfiles) {
                    ReflectionConstants.TAB_PACKET_PROFILE.set(packet, profile);
                } else {
                    ReflectionConstants.TAB_PACKET_NAME.set(packet, ReflectionConstants.GAME_PROFILE_NAME.get(profile));
                }

                sendPacket(player, packet);
            }

            initiated = true;
        }
    }

    /**
     * Clear the tablist
     */
    public void clear() {
        for (int i = 0; i < 80; i++) {
            Object packet = ReflectionConstants.SCOREBOARD_TEAM_CONSTRUCTOR.invoke();
            ReflectionConstants.SCOREBOARD_TEAM_NAME.set(packet, TAB_NAMES[i]);
            ReflectionConstants.SCOREBOARD_TEAM_ACTION.set(packet, 4);
            sendPacket(player, packet);
        }
    }

    static {
        GAME_PROFILES = new Object[80];
        TAB_NAMES = new String[80];
        for (int i = 0; i < 80; i++) {
            int x = i % 4;
            int y = i / 4;
            String name = "§0§" + x + (y > 9 ? "§" + String.valueOf(y).toCharArray()[0] + "§" + String.valueOf(y).toCharArray()[1] : "§0§" + String.valueOf(y).toCharArray()[0]);
            UUID id = UUID.randomUUID();
            Object profile = ReflectionConstants.GAME_PROFILE_CONSTRUCTOR.invoke(id, name);
            TAB_NAMES[i] = name;
            GAME_PROFILES[i] = profile;
        }
    }
}