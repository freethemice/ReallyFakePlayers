package com.firesoftitan.play.titanbox.rfp.info;

import com.firesoftitan.play.titanbox.rfp.TitanBoxRFP;
import com.firesoftitan.play.titanbox.rfp.managers.FakeNetworkManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class FakePlayerInfo {

    public static Property getPlayerTextureProperty(UUID uuid) throws IOException
    {
        return getPlayerTextureProperty(uuid.toString());
    }
    public static Property getPlayerTextureProperty(String uuid) throws IOException
    {
        if (uuid == null) {
            throw new NullPointerException("name is marked non-null but is null");
        } else {
            InputStreamReader profileReader = null;
            Iterator var7;
            try {
                InputStreamReader sessionReader = null;

                try {
                    uuid = uuid.replace("-", "");
                    URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                    InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
                    BufferedReader in = new BufferedReader(reader_1);
                    String inputLine;
                    String allInput = "";
                    while ((inputLine = in.readLine()) != null)
                        allInput = allInput + inputLine;
                    in.close();
                    String[] NotTheRightWay = allInput.split("value\" : \"");
                    NotTheRightWay =  NotTheRightWay[1].split("\",");
                    String texture = NotTheRightWay[0];

                    NotTheRightWay = allInput.split("signature\" : \"");
                    NotTheRightWay =  NotTheRightWay[1].split("\"");
                    String signature = NotTheRightWay[0];
                    Property property = new Property("textures", texture, signature);
                    return property;
                } finally {
                    if (Collections.singletonList(sessionReader).get(0) != null) {
                        sessionReader.close();
                    }

                }
            } finally {
                if (Collections.singletonList(profileReader).get(0) != null) {
                    profileReader.close();
                }

            }
        }
    }
    public static UUID getPlayerUUID(String name) throws IOException
    {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        } else {
            InputStreamReader profileReader = null;
            Iterator var7;
            try {
                InputStreamReader sessionReader = null;

                try {
                    URL url_1 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name + "?at=" + System.currentTimeMillis());
                    InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
                    BufferedReader in = new BufferedReader(reader_1);
                    String inputLine;
                    String allInput = "";
                    while ((inputLine = in.readLine()) != null)
                        allInput = allInput + inputLine;
                    in.close();
                    if (allInput.length() < 5) return null;
                    String[] NotTheRightWay = allInput.split("id\":\"");
                    String digits =  NotTheRightWay[1].replace("\"}", "");
                    String uuid = digits.replaceAll(
                            "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                            "$1-$2-$3-$4-$5");
                    return UUID.fromString(uuid);
                } finally {
                    if (Collections.singletonList(sessionReader).get(0) != null) {
                        sessionReader.close();
                    }

                }
            } finally {
                if (Collections.singletonList(profileReader).get(0) != null) {
                    profileReader.close();
                }

            }
        }
    }
    private EntityPlayer entityPlayer;
    private long joinTime;

    public FakePlayerInfo(String name) {
        setupPlayer(name, UUID.randomUUID());
    }
    public FakePlayerInfo(String name, UUID uuid) {
        setupPlayer(name, uuid);
    }
    private void setupPlayer(String name, UUID uuid)
    {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        World world = Bukkit.getWorlds().get(0);
        WorldServer nmsWorld = ((CraftWorld)world).getHandle();
        if (name.length() > 16) name = name.substring(0, 16);
        GameProfile gameProfile = new GameProfile(uuid, name);
        this.entityPlayer = new EntityPlayer(nmsServer, nmsWorld, gameProfile);
        this.entityPlayer.b = new PlayerConnection(nmsServer, new FakeNetworkManager(EnumProtocolDirection.a), this.entityPlayer);
        this.joinTime = System.currentTimeMillis();

        //update(UUID.fromString("aa88b6c8-b31d-4282-b423-0adc5f331ba2"));


    }

    public long getJoinTime() {
        return joinTime;
    }

    public GameProfile getGameProfile()
    {
        return this.entityPlayer.getProfile();
    }
    public PlayerConnection getConnection()
    {
        return this.entityPlayer.b;
    }
    public EntityPlayer getEntityPlayer() {
        return entityPlayer;
    }
    public CraftPlayer getCraftPlayer()
    {
        return entityPlayer.getBukkitEntity();
    }
    private void loadSkinToServer(UUID uuid)
    {
        String name = uuid.toString();
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        World world = Bukkit.getWorlds().get(0);
        WorldServer nmsWorld = ((CraftWorld)world).getHandle();
        if (name.length() > 16) name = name.substring(0, 16);
        GameProfile gameProfile = new GameProfile(uuid, name);
        EntityPlayer entityPlayer = new EntityPlayer(nmsServer, nmsWorld, gameProfile);
        entityPlayer.b = new PlayerConnection(nmsServer, new FakeNetworkManager(EnumProtocolDirection.a), this.entityPlayer);
    }
    public void setSkin( UUID skin)
    {
        //\/ \/ \/ This loads the skin to the server
        loadSkinToServer(skin);
        ///\ /\ /\ This loads the skin to the server


        try {
            Property property = FakePlayerInfo.getPlayerTextureProperty(skin);
            update(property);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void update(Property property)
    {

        EntityPlayer npc = this.getEntityPlayer();
        GameProfile gameProfile = npc.getProfile();
        PropertyMap properties = gameProfile.getProperties();
        properties.clear();
        properties.put("textures", property);
    }
    public void sendChatMessageOut(String message)
    {
        Set<Player> playerSet = new HashSet<Player>(Bukkit.getOnlinePlayers());

        String formattedMessage = TitanBoxRFP.configManager.getTextFormat();
        if (formattedMessage == null || formattedMessage.length() < 1) return;
        formattedMessage = formattedMessage.replace("<message>", message);
        formattedMessage = formattedMessage.replace("<fakename>", this.getName());
        for(Player player: playerSet)
        {
            if (TitanBoxRFP.hasAdminPermission(player) || player.hasPermission("titanbox.rfp.show"))
            {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', formattedMessage) + ChatColor.GRAY +" [" +  "I'm Fake]");
            }
            else
            {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', formattedMessage));
            }
        }

    }

    public String getName() {
        return entityPlayer.getName();
    }

    public UUID getUniqueID() {
        return entityPlayer.getUniqueID();
    }
}
