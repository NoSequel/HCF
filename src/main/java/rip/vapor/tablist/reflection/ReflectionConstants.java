package rip.vapor.tablist.reflection;


import java.util.Collection;
import java.util.UUID;

public class ReflectionConstants {

    // base tablist
    public static final Class<?> TAB_PACKET_CLASS = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo");
    public static final Reflection.ConstructorInvoker TAB_PACKET_CONSTRUCTOR = Reflection.getConstructor(TAB_PACKET_CLASS);
    public static final Reflection.FieldAccessor<Integer> TAB_PACKET_ACTION = Reflection.getField(TAB_PACKET_CLASS, int.class, 5);

    // 1.7 tablist
    public static final Reflection.FieldAccessor<String> TAB_PACKET_NAME = Reflection.getField(TAB_PACKET_CLASS, String.class, 0);
    public static final Reflection.FieldAccessor<Integer> TAB_PACKET_LATENCY = Reflection.getField(TAB_PACKET_CLASS, int.class, 7);

    // 1.8 tablist
    public static final Class<Object> GAME_PROFILE_CLASS = getUntypedClasses("net.minecraft.util.com.mojang.authlib.GameProfile", "com.mojang.authlib.GameProfile");
    public static final Reflection.ConstructorInvoker GAME_PROFILE_CONSTRUCTOR = Reflection.getConstructor(GAME_PROFILE_CLASS, UUID.class, String.class);
    public static final Reflection.FieldAccessor<String> GAME_PROFILE_NAME = Reflection.getField(GAME_PROFILE_CLASS, String.class, 0);
    public static final Reflection.FieldAccessor<Object> TAB_PACKET_PROFILE = Reflection.getField(TAB_PACKET_CLASS, GAME_PROFILE_CLASS, 0);

    public static final Class<Object> PROPERTY_MAP_CLASS = getUntypedClasses("net.minecraft.util.com.mojang.authlib.properties.PropertyMap", "com.mojang.authlib.properties.PropertyMap");
    public static final Reflection.MethodInvoker PROPERTY_MAP_GET = Reflection.getMethod(GAME_PROFILE_CLASS, "getProperties");
    public static final Reflection.MethodInvoker PROPERTY_MAP_PUT = Reflection.getMethod(PROPERTY_MAP_CLASS, "put", Object.class, Object.class);

    public static final Class<Object> PROPERTY_CLASS = getUntypedClasses("net.minecraft.util.com.mojang.authlib.properties.Property", "com.mojang.authlib.properties.Property");
    public static final Reflection.ConstructorInvoker PROPERTY_CONSTRUCTOR = Reflection.getConstructor(PROPERTY_CLASS, String.class, String.class, String.class);

    // misc
    public static final Class<?> CRAFT_PLAYER_CLASS = Reflection.getCraftBukkitClass("entity.CraftPlayer");
    public static final Class<?> NMS_PACKET_CLASS = Reflection.getMinecraftClass("Packet");
    public static final Class<?> NMS_PLAYER_CLASS = Reflection.getMinecraftClass("EntityPlayer");
    public static final Class<?> PLAYER_CONNECTION_CLASS = Reflection.getMinecraftClass("PlayerConnection");
    public static final Class<?> NETWORK_MANAGER_CLASS = Reflection.getMinecraftClass("NetworkManager");
    public static final Reflection.MethodInvoker GET_HANDLE_METHOD = Reflection.getMethod(CRAFT_PLAYER_CLASS, "getHandle");
    public static final Reflection.MethodInvoker GET_PROFILE_METHOD = Reflection.getMethod(CRAFT_PLAYER_CLASS, "getProfile");
    public static final Reflection.MethodInvoker VERSION_METHOD = Reflection.getMethod(NETWORK_MANAGER_CLASS, "getVersion");
    public static final Reflection.MethodInvoker SEND_PACKET = Reflection.getMethod(PLAYER_CONNECTION_CLASS, "sendPacket", NMS_PACKET_CLASS);
    public static final Reflection.FieldAccessor<?> PLAYER_CONNECTION = Reflection.getField(NMS_PLAYER_CLASS, PLAYER_CONNECTION_CLASS, 0);
    public static final Reflection.FieldAccessor<?> NETWORK_MANAGER = Reflection.getField(PLAYER_CONNECTION_CLASS, NETWORK_MANAGER_CLASS, 0);
    public static final Class<?> SCOREBOARD_TEAM_CLASS = Reflection.getMinecraftClass("PacketPlayOutScoreboardTeam");
    public static final Reflection.ConstructorInvoker SCOREBOARD_TEAM_CONSTRUCTOR = Reflection.getConstructor(SCOREBOARD_TEAM_CLASS);
    public static final Reflection.FieldAccessor<String> SCOREBOARD_TEAM_NAME = Reflection.getField(SCOREBOARD_TEAM_CLASS, String.class, 0);
    public static final Reflection.FieldAccessor<String> SCOREBOARD_TEAM_PREFIX = Reflection.getField(SCOREBOARD_TEAM_CLASS, String.class, 2);
    public static final Reflection.FieldAccessor<String> SCOREBOARD_TEAM_SUFFIX = Reflection.getField(SCOREBOARD_TEAM_CLASS, String.class, 3);
    public static final Reflection.FieldAccessor<Collection> SCOREBOARD_TEAM_PLAYERS = Reflection.getField(SCOREBOARD_TEAM_CLASS, Collection.class, 0);
    public static final Reflection.FieldAccessor<Integer> SCOREBOARD_TEAM_ACTION = Reflection.getField(SCOREBOARD_TEAM_CLASS, Integer.TYPE, 0);

    public static Class<Object> getUntypedClasses(String... lookupNames) {
        for (String lookupName : lookupNames) {
            try {
                return Reflection.getUntypedClass(lookupName);
            } catch (IllegalArgumentException ignored) {
            }
        }
        throw new IllegalArgumentException("No class found in selection given");
    }
}