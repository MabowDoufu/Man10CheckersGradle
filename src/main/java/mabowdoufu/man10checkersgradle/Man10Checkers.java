package mabowdoufu.man10checkersgradle;
//Man10Checkers.mcheckers.getLogger().info("join");
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public final class Man10Checkers extends JavaPlugin {

    /// tasklist
    ///
    /// 未対策
    ///
    /// 間違った手を2回目のクリックで押したのにclick=0にならないことの対処
    ///
    /// ---
    /// 対処済み未テスト
    ///
    ///
    ///
    /// 確認すること/作業
    /// clickの値書き込んでるところに全部p.logつける→デバッグ
    ///
    public static JavaPlugin mcheckers;
    public static mabowdoufu.man10checkersgradle.BoardGameSys bgs;
    private final Events events = new Events(this);
    @Override
    public void onEnable() {
        mcheckers = this;
        events.register();
        Config.LoadConfig();
        getCommand("mcheckers").setExecutor(new Commands());
        getLogger().info("This plugin is running");

        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent event) {
                getLogger().info(event.getView().getTitle() + "が押されました！");
            }
        }, this);
    }

    @Override
    public void onDisable() {

        getLogger().info("This plugin has stopped running");

    }

    public static void logg(String string){
        Man10Checkers.mcheckers.getLogger().info(string);
    }
    public static void logg2(int[][] board){
        for(int[] b : board){
            Man10Checkers.mcheckers.getLogger().info(Arrays.toString(b));
        }
    }

}
