package mabowdoufu.man10checkersgradle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;




public class Events implements Listener {
    public Events(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void BlockBreak(BlockBreakEvent e){
        Man10Checkers.mcheckers.getLogger().info("BlockBreak");
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Man10Checkers.mcheckers.getLogger().info(event.getView().getTitle() + "が押されました！");
        if (!event.getView().getTitle().contains(Config.prefix)) {
            Man10Checkers.mcheckers.getLogger().info("チェック非通過");
            return;
        }
        Man10Checkers.mcheckers.getLogger().info("チェック通過");
        return;
    }
}