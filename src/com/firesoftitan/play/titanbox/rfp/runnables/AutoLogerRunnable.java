package com.firesoftitan.play.titanbox.rfp.runnables;

import com.firesoftitan.play.titanbox.rfp.TitanBoxRFP;
import com.firesoftitan.play.titanbox.rfp.info.FakePlayerInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class AutoLogerRunnable extends BukkitRunnable {

    private Random random = new Random(System.currentTimeMillis());
    @Override
    public void run() {
        long min = TitanBoxRFP.configManager.getAutoLogingMinimum() * 1000 * 60;
        long max = TitanBoxRFP.configManager.getAutoLogingMaximum() * 1000 * 60;
        List<FakePlayerInfo> playerInfoList = TitanBoxRFP.fakePlayerManager.getPlayerInfoList();

        for (int i = 0; i < playerInfoList.size(); i++)
        {
            FakePlayerInfo playerInfo = playerInfoList.get(i);
            if (playerInfo.getJoinTime()  + min < System.currentTimeMillis())
            {
                if (random.nextInt(1000) > 850) {
                    TitanBoxRFP.fakePlayerManager.remove(playerInfo);
                    return;
                }
            }
            if (playerInfo.getJoinTime()  + max < System.currentTimeMillis())
            {
                TitanBoxRFP.fakePlayerManager.remove(playerInfo);
                return;
            }
        }
        if (TitanBoxRFP.fakePlayerManager.getPlayerInfoList().size() < TitanBoxRFP.configManager.getAutoMinimum())
        {
            TitanBoxRFP.fakePlayerManager.addMore(1);
            return;
        }
        if (TitanBoxRFP.fakePlayerManager.getPlayerInfoList().size() < TitanBoxRFP.configManager.getAutoMaximum())
        {
            if (random.nextInt(1000) > 850) {
                TitanBoxRFP.fakePlayerManager.addMore(1);
                return;

            }
        }
    }
}
