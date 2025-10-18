package mabowdoufu.man10checkersgradle;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static mabowdoufu.man10checkersgradle.Man10Checkers.mcheckers;

public class Config {
    public static String prefix = "";
    public static File configfile;
    public static boolean system;
    public static int recruitment_time;
    public static int recruitment_interval;
    public static int max_thinking;
    public static int max_ability;

    public static void LoadConfig() {
        mcheckers.saveDefaultConfig();
        system = mcheckers.getConfig().getBoolean("system");
        prefix = mcheckers.getConfig().getString("prefix");
        recruitment_time = mcheckers.getConfig().getInt("recruitment.time");
        recruitment_interval = mcheckers.getConfig().getInt("recruitment.messageInterval");
        max_thinking = mcheckers.getConfig().getInt("game.maxThinking");
    }

}
