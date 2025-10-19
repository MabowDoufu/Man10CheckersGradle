package mabowdoufu.man10checkersgradle;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import mabowdoufu.man10checkersgradle.BoardGameSys.*;

import static mabowdoufu.man10checkersgradle.Man10Checkers.logg;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;

public class Recruitment {
    public static void waitingTimer(String BoardName){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            int recruitment_time = Config.recruitment_time;
            int count = Config.recruitment_time;
            int stay = Config.recruitment_interval;
            boolean stop = false;
            while (count > 0 && !stop) {
                if (count < stay) stay = count;
                if((recruitment_time - count) % stay ==0){
                    Bukkit.broadcast(Component.text(Config.prefix + "§c§l" + BoardName + "§rがリバーシを募集中！あと" + count + "秒"));
                    Bukkit.broadcast(Component.text("§e§l[ここをクリックで参加する]").clickEvent(runCommand("/mcheckers join " + BoardName)));
                }
                File f = new File("plugins/Man10Checkers/game.yml");
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
                logg("waitingTimer");
                logg("yml.getBoolean(BoardName+\".Recruiting\"):"+yml.getBoolean(BoardName+".Recruiting"));
                stop = !yml.getBoolean(BoardName+".Recruiting");
                try {
                    Thread.sleep(1000L); // 1000ミリ秒（=1秒）スリープ
                } catch (InterruptedException e) {
                    e.printStackTrace(); // 割り込みされたときの処理
                }
                count -= 1;
            }
            if(!(count > 0)){
                Bukkit.broadcast(Component.text(Config.prefix + "§c§l" + BoardName + "§rのリバーシは対戦相手が現れなかったため解散しました"));
                BoardGameSys.deleteData(BoardName);
            }

            executor.shutdown();
        });

    }
}
