package mabowdoufu.man10checkersgradle;
//Man10Checkers.mcheckers.getLogger().info("join");
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public final class Man10Checkers extends JavaPlugin {

    /// tasklist
    /// Eventsの303まで完了
    /// Ability別のスキル使用画面での処理記述
    /// AbilityPointの入手法考える
    /// Ability画面移行用のアイテムをinvセットする(getInvに記述？)
    ///
    /// --------------------
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
