package com.firesoftitan.play.titanbox.rfp.listeners;

import com.firesoftitan.play.titanbox.rfp.TitanBoxRFP;
import com.firesoftitan.play.titanbox.rfp.info.FakePlayerInfo;
import com.firesoftitan.play.titanbox.rfp.managers.FakePlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainListener  implements Listener {


    public MainListener(){

    }
    public void registerEvents(){
        PluginManager pm = TitanBoxRFP.instants.getServer().getPluginManager();
        pm.registerEvents(this, TitanBoxRFP.instants);
    }

    @EventHandler
    public static void onPlayerLoginEvent(PlayerLoginEvent event)
    {
        Player player = event.getPlayer();
        final FakePlayerManager fakePlayerManager = TitanBoxRFP.fakePlayerManager;

        if (TitanBoxRFP.configManager.getTextFormat() == null)
        {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Set<Player> playerSet = new HashSet<Player>(Bukkit.getOnlinePlayers());
                    AsyncPlayerChatEvent asyncPlayerChatEvent = new AsyncPlayerChatEvent(false, player, "<message>", playerSet);
                    Bukkit.getPluginManager().callEvent(asyncPlayerChatEvent);
                    String formatted = String.format(asyncPlayerChatEvent.getFormat(), "<fakename>", asyncPlayerChatEvent.getMessage());
                    formatted = formatted.replace(player.getDisplayName(), "<fakename>");
                    formatted = formatted.replace(player.getName(), "<fakename>");
                    formatted = formatted.replace('§', '&');
                    TitanBoxRFP.configManager.setTextFormat(formatted);
                }
            }.runTaskLater(TitanBoxRFP.instants, 3);

        }

        fakePlayerManager.remove(player.getName());
        new BukkitRunnable() {
            @Override
            public void run() {
                fakePlayerManager.showList(player);

            }
        }.runTaskLater(TitanBoxRFP.instants, 1);

        if (TitanBoxRFP.hasAdminPermission(player)) {
            if (TitanBoxRFP.update) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        TitanBoxRFP.sendMessagePlayer(TitanBoxRFP.instants, player,"There is a new update available.");
                        TitanBoxRFP.sendMessagePlayer(TitanBoxRFP.instants, player, "https://www.spigotmc.org/resources/really-fake-players.95927");

                    }
                }.runTaskLater(TitanBoxRFP.instants, 20);
            }
        }
        if (TitanBoxRFP.configManager.isAutoEnable()) {
            TitanBoxRFP.fakePlayerManager.welcomePlayer(player);
        }



    }



    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event){
        if (!TitanBoxRFP.configManager.isInteraction()) return;
        String command = event.getMessage();
        Player player = event.getPlayer();
        if (TitanBoxRFP.hasAdminPermission(player))
        {
            if (command.toLowerCase().startsWith("/trfp") || command.toLowerCase().startsWith("/rfp")) return;
        }
        List<FakePlayerInfo> fakeList = TitanBoxRFP.fakePlayerManager.getPlayerInfoList();
        for(FakePlayerInfo fakePlayer: fakeList)
        {
            String toLowerCase = fakePlayer.getName().toLowerCase();
            if (command.toLowerCase().contains(" " + toLowerCase + " ") || command.toLowerCase().endsWith(" " + toLowerCase))
            {
                event.setCancelled(true);
                player.sendMessage(ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', TitanBoxRFP.configManager.getMessage()));
                if (TitanBoxRFP.hasAdminPermission(player) || player.hasPermission("titanbox.rfp.show"))
                {
                    player.sendMessage(ChatColor.GRAY + "Fake Player");
                }
                return;
            }
        }
    }


    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event)
    {

    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockBreakEvent(BlockBreakEvent event) {

    }

}
