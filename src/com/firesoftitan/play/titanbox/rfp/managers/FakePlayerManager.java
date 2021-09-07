package com.firesoftitan.play.titanbox.rfp.managers;

import com.firesoftitan.play.titanbox.rfp.TitanBoxRFP;
import com.firesoftitan.play.titanbox.rfp.info.FakePlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;
public class FakePlayerManager {

    private HashMap<String, FakePlayerInfo> playerHashMap;
    private List<String> playerNames;
    private SaveManager nameList = new SaveManager(TitanBoxRFP.instants.getName(), "NameList");

    private List<EntityPlayer> playerListFree;
    private Map<UUID, EntityPlayer> onlineListFree;
    public FakePlayerManager() {
        playerHashMap = new HashMap<String, FakePlayerInfo>();
        playerNames = new ArrayList<String>();
        if (!nameList.contains("names"))
        {
            playerNames.add("Billy");
            playerNames.add("Tammy");
            nameList.set("names", playerNames, true);
            nameList.save();
        }
        playerNames = nameList.getStringListFromText("names");

        DedicatedPlayerList playerList = ((CraftServer) Bukkit.getServer()).getServer().getPlayerList();

        Field privateStringField = null;
        try {
            privateStringField = PlayerList.class.getDeclaredField("j");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        privateStringField.setAccessible(true);

        try {
            playerListFree =  (List<EntityPlayer>)privateStringField.get(playerList);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            privateStringField = PlayerList.class.getDeclaredField("k");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        privateStringField.setAccessible(true);

        try {
            onlineListFree =  (Map<UUID, EntityPlayer>)privateStringField.get(playerList);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }
    public List<FakePlayerInfo> getPlayerInfoList()
    {
        return new ArrayList<>(playerHashMap.values());
    }
    public FakePlayerInfo getPlayer(String name)
    {
        for(FakePlayerInfo pl: getPlayerInfoList())
        {
            if (pl.getName().equals(name)) return pl;
        }
        return null;
    }
    public void welcomePlayer(Player player) {

        FakePlayerManager fakePlayerManager = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                List<String> firstJoinMessages = TitanBoxRFP.configManager.getReJoinMessages();
                if (player.getFirstPlayed() + 10000 > System.currentTimeMillis())
                    firstJoinMessages = TitanBoxRFP.configManager.getFirstJoinMessages();
                Random random = new Random(System.currentTimeMillis());
                for (FakePlayerInfo entityPlayer : fakePlayerManager.getPlayerInfoList()) {
                    if (random.nextInt(3) < 2 && !player.getName().equals(entityPlayer.getName())) {
                        String message = firstJoinMessages.get(random.nextInt(firstJoinMessages.size()));
                        message = message.replace("<name>", player.getName());
                        message = ChatColor.translateAlternateColorCodes('&', message);
                        String finalMessage = message;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                entityPlayer.sendChatMessageOut(finalMessage);
                            }
                        }.runTaskLater(TitanBoxRFP.instants, random.nextInt(100));
                    }
                }
            }
        }.runTaskLater(TitanBoxRFP.instants, 30);
    }
    public void add(String name, boolean welcome)
    {
        if (name.length() > 16) name = name.substring(0, 16);
        FakePlayerInfo npc = new FakePlayerInfo(name);
        playerHashMap.put(name, npc);
        playerListFree.add(npc.getEntityPlayer());
        onlineListFree.put(npc.getEntityPlayer().getUniqueID(), npc.getEntityPlayer());
        //Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + name + " joined the game.");
        Set<Player> playerSet = new HashSet<Player>(Bukkit.getOnlinePlayers());
        for(Player player: playerSet) {
            if (TitanBoxRFP.hasAdminPermission(player) || player.hasPermission("titanbox.rfp.show")) {
                player.sendMessage(ChatColor.YELLOW + name + " joined the game." + ChatColor.GRAY + " [Fake Player]");
            }
            else
            {
                player.sendMessage(ChatColor.YELLOW + name + " joined the game.");
            }
        }
        if (welcome) this.welcomePlayer(npc.getCraftPlayer());
    }
    public int count()
    {
        return playerHashMap.size();
    }
    public void addMore(int number)
    {
        boolean welcome = false;
        if (number == 1) welcome = true;
        for(int i = 0; i < number; i++)
        {
            String nameF = getRandomName();
            add(nameF, welcome);
        }
        for(Player all: Bukkit.getOnlinePlayers()) {
            showList(all);
        }
    }
    public boolean contains(String name)
    {
        return playerHashMap.containsKey(name);
    }
    public void removeAll()
    {
        for (FakePlayerInfo pl: getPlayerInfoList()) {
            remove(pl);
        }
    }
    public void remove(FakePlayerInfo pl)
    {
        if (pl != null)
        {
            if (pl.getName().equalsIgnoreCase(pl.getName().toLowerCase())) {
                this.playerNames.remove(pl.getName());
                this.playerHashMap.remove(pl.getName());

                this.playerListFree.remove(pl.getEntityPlayer());
                this.onlineListFree.remove(pl.getUniqueID());
                for(Player all: Bukkit.getOnlinePlayers()) {
                    ((CraftPlayer) all).getHandle().b.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, pl.getEntityPlayer())); //REMOVE PLAYER
                }
                //PlayerQuitEvent event = new PlayerQuitEvent(pl.getCraftPlayer(), "Not real");
                //Bukkit.getPluginManager().callEvent(event);
                Set<Player> playerSet = new HashSet<Player>(Bukkit.getOnlinePlayers());
                for(Player player: playerSet) {
                    if (TitanBoxRFP.hasAdminPermission(player) || player.hasPermission("titanbox.rfp.show")) {
                        player.sendMessage(ChatColor.YELLOW + pl.getName() + " left the game." + ChatColor.GRAY + " [Fake Player]");
                    }
                    else
                    {
                        player.sendMessage(ChatColor.YELLOW + pl.getName() + " left the game.");
                    }
                }
            }
        }
    }

    public void remove(String name)
    {
        FakePlayerInfo pl = getPlayer(name);
        remove(pl);
    }
    public void showList(Player player)
    {
        if (player != null)
        {
            for (FakePlayerInfo pl: getPlayerInfoList()) {
                ((CraftPlayer) player).getHandle().b.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, pl.getEntityPlayer())); //ADD_PLAYER
            }
        }
    }
    public String getRandomName()
    {
      return getRandomName(0);
    }
    private String getRandomName(int count)
    {
        count++;
        Random random = new Random(System.currentTimeMillis());
        int nameIndex = random.nextInt(playerNames.size());
        String nameF = playerNames.get(nameIndex);
        if (count > 5) nameF = nameF + "_" + random.nextInt(100000);
        if (playerHashMap.containsKey(nameF)) return getRandomName(count);
        return nameF;
    }
}
