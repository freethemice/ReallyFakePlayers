package com.firesoftitan.play.titanbox.rfp.managers;

import com.firesoftitan.play.titanbox.rfp.TitanBoxRFP;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private SaveManager configFile;
    private int auto_minimum = 3;
    private int auto_maximum = 7;
    private int auto_loging_minimum = 3;
    private int auto_loging_maximum = 7;
    private boolean auto_enable = true;
    private int maximum = 60;
    private boolean interaction = true;
    private String message = "This player is on DND (Do Not Disturb)!";
    private List<String> firstJoinMessages;
    private List<String> reJoinMessages;
    private String textFormat = null;
    public ConfigManager() {
        configFile = new SaveManager(TitanBoxRFP.instants.getName(), "config");
        if (!configFile.contains("settings.maximum"))
        {
            configFile.set("settings.maximum", 60);
        }
        if (!configFile.contains("settings.auto.enable"))
        {
            configFile.set("settings.auto.enable", true);
        }
        if (!configFile.contains("settings.auto.firstJoinMessages"))
        {
            firstJoinMessages = new ArrayList<String>();
            firstJoinMessages.add("Welcome!");
            firstJoinMessages.add("Welcome <name>!");
            firstJoinMessages.add("Whats up <name>!");
            firstJoinMessages.add("<name>!");
            firstJoinMessages.add("Finally the one and only <name>!");
            configFile.set("settings.auto.firstJoinMessages", firstJoinMessages, true);
        }
        if (!configFile.contains("settings.auto.reJoinMessages"))
        {
            reJoinMessages = new ArrayList<String>();
            reJoinMessages.add("WB");
            reJoinMessages.add("Welcome back <name>!");
            reJoinMessages.add("Hey, <name>!");
            reJoinMessages.add("Whats up <name>!");
            reJoinMessages.add("<name>!");
            reJoinMessages.add("Hello <name>!");
            reJoinMessages.add("WB <name>!");
            configFile.set("settings.auto.reJoinMessages", reJoinMessages, true);
        }
        if (!configFile.contains("settings.auto.loging.minimum"))
        {
            configFile.set("settings.auto.loging.minimum", 5);
        }
        if (!configFile.contains("settings.auto.loging.maximum"))
        {
            configFile.set("settings.auto.loging.maximum", 60);
        }
        if (!configFile.contains("settings.auto.minimum"))
        {
            configFile.set("settings.auto.minimum", 3);
        }
        if (!configFile.contains("settings.auto.minimum"))
        {
            configFile.set("settings.auto.minimum", 3);
        }
        if (!configFile.contains("settings.auto.maximum"))
        {
            configFile.set("settings.auto.maximum", 7);
        }
        if (!configFile.contains("settings.interaction"))
        {
            configFile.set("settings.interaction", true);
        }
        if (!configFile.contains("settings.message"))
        {
            configFile.set("settings.message", "This player is on DND (Do Not Disturb)!");
        }
        this.auto_enable = configFile.getBoolean("settings.auto.enable");
        if (configFile.contains("settings.auto.textFormat")) this.textFormat = configFile.getString("settings.auto.textFormat");
        this.firstJoinMessages = configFile.getStringListFromText("settings.auto.firstJoinMessages");
        this.reJoinMessages = configFile.getStringListFromText("settings.auto.reJoinMessages");
        this.auto_minimum = configFile.getInt("settings.auto.minimum");
        this.auto_maximum = configFile.getInt("settings.auto.maximum");
        this.auto_loging_minimum = configFile.getInt("settings.auto.loging.minimum");
        this.auto_loging_maximum = configFile.getInt("settings.auto.loging.maximum");
        this.maximum = configFile.getInt("settings.maximum");
        this.interaction = configFile.getBoolean("settings.interaction");
        this.message = configFile.getString("settings.message");

        configFile.save();
    }

    public String getTextFormat() {
        return textFormat;
    }

    public void setTextFormat(String textFormat) {
        this.textFormat = textFormat;
        configFile.set("settings.auto.textFormat", this.textFormat);
        configFile.save();

    }

    public int getAutoLogingMinimum() {
        return auto_loging_minimum;
    }

    public int getAutoLogingMaximum() {
        return auto_loging_maximum;
    }

    public List<String> getFirstJoinMessages() {
        return firstJoinMessages;
    }

    public List<String> getReJoinMessages() {
        return reJoinMessages;
    }

    public boolean isAutoEnable() {
        return auto_enable;
    }

    public int getMaximum() {
        return maximum;
    }

    public boolean isInteraction() {
        return interaction;
    }

    public String getMessage() {
        return message;
    }

    public int getAutoMinimum() {
        return auto_minimum;
    }

    public int getAutoMaximum() {
        return auto_maximum;
    }
}
