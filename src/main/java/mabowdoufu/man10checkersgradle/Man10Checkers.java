package mabowdoufu.man10checkersgradle;
//Man10Checkers.mcheckers.getLogger().info("join");
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
public final class Man10Checkers extends JavaPlugin {

    /// board createを誰でもできるようにする
    ///yml:ボード名は主催者のユーザー名を自動設定
    /// Recruiting状態に応じたjoinコマンド補完
    /// join処理
    ///ボード開始時の両者gyi表示
    ///gui中断時の再オープンコマンド
    /// ---ここまでやった
    ///gui上でのインプットアウトプット
    ///
    ///
    public static JavaPlugin mcheckers;
    public static mabowdoufu.man10checkersgradle.BoardGameSys bgs;
    @Override
    public void onEnable() {
        mcheckers = this;
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
}
