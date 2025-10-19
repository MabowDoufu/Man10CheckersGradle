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
    /// 未記述
    ///　[...](https://github.com/yusama125718/Man10Reversi/blob/05f3b23ff4c96ad3ab373198dd0b4d9e70da7f10/src/main/java/yusama125718/man10Reversi/GameManager.java#L163)
    /// ---
    /// 記述済み未テスト
    ///
    ///
    ///
    /// 確認すること/作業
    /// ゲームの終了が正常になされるか
    ///　既存のman10のゲームとの違いを把握
    /// テクスチャ描く？
    /// 効果音つける
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
