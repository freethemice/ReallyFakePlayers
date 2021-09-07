package com.firesoftitan.play.titanbox.rfp.info;

import com.firesoftitan.play.titanbox.rfp.TitanBoxRFP;
import com.firesoftitan.play.titanbox.rfp.managers.FakeNetworkManager;
import com.mojang.authlib.GameProfile;
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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FakePlayerInfo {
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
