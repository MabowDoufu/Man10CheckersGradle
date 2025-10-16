package mabowdoufu.man10checkersgradle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static mabowdoufu.man10checkersgradle.BoardGameSys.*;


public class Events implements Listener {

    private final Man10Checkers plugin;

    public Events(Man10Checkers plugin) {
    this.plugin = plugin;
    }

    public void register() {
        Man10Checkers.mcheckers.getServer().getPluginManager().registerEvents(this, plugin);
        Man10Checkers.mcheckers.getLogger().info("register event");
    }

    public static int x1;
    public static int y1;
    public static int x2;
    public static int y2;


    public Inventory SetLoreMessage(String m, Inventory inv){
        int i =0;
        List<String> lore = new ArrayList<>();
        lore.add("§c"+m);
        for(ItemStack item :inv.getContents()){
            if(item != null){
                item.setLore(lore);
                inv.setItem(i, item);
            }
            i++;

        }
        return inv;
    }

    @EventHandler
    public void InventoryClick(InventoryClickEvent e){
        Man10Checkers.mcheckers.getLogger().info("invevent");
        Man10Checkers.mcheckers.getLogger().info("e.getRawSlot():"+e.getRawSlot());
        if(!e.getView().getTitle().equals(Config.prefix)) return;
        if(e.getRawSlot()== -999) return;
        if(e.getRawSlot() > 53) return;
        e.setCancelled(true);

        Man10Checkers.mcheckers.getLogger().info("invevent2");
        Player Clicker = (Player) e.getWhoClicked();
        LoadData(getBoard(e.getWhoClicked().getName()));
        if(Clicker != Players.get(Turn)){
            Man10Checkers.mcheckers.getLogger().info("invevent3");
            return;
        }
        //クリックしたマス獲得
        //gamesysにマスの情報ぶちこむ
        ///再度gui開く処理

        File gameyml = new File("plugins/Man10Checkers/game.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(gameyml);
        String CurrentBoard ="";
        for (String BoardName : yml.getKeys(false)) {
            for (Player joinning_uuid : (List<Player>) yml.get(BoardName + ".Players")) {
                if (Clicker.getName().equals(joinning_uuid.getName())) {
                    CurrentBoard = BoardName;
                }
            }
        }
        LoadData(CurrentBoard);

        if(Click == 0){
            Click++;
            y1 = e.getRawSlot() % 9;
            x1 = (e.getRawSlot() - y1)/9;
            switch (FirstClickCheck(x1,y1)){
                case 1:
                    Clicker.openInventory(SetLoreMessage("Error:チェッカーで使用しないマスを選択しています",e.getInventory() ));
                    Man10Checkers.mcheckers.getLogger().info("FirstCheckError1");
                    return;
                case 2:
                    Clicker.openInventory(SetLoreMessage("Error:自分の駒を選択してください",e.getInventory()));
                    Man10Checkers.mcheckers.getLogger().info("FirstCheckError2");
                    return;
                default:
                    Man10Checkers.mcheckers.getLogger().info("FirstCheckNOError");
                    saveData(CurrentBoard);
                    Man10Checkers.mcheckers.getLogger().info("Click0");
            }
        }else if(Click == 1){
            Click =0;
            x2 = e.getRawSlot() % 9;
            y2 = (e.getRawSlot() - x2)/9;
            //作業メモ： error等の挙動をまず確認・二回目のマス選択で、離れたマスを選択した場合に即returnさせる・warning消す
            BoardInput(CurrentBoard,x1,y1,x2,y2);
            Man10Checkers.mcheckers.getLogger().info("Click1");
            switch (ErrorType){
                case 1:
                    Clicker.openInventory(SetLoreMessage("Error:チェッカーで使用しないマスを選択しています",e.getInventory()));
                    Man10Checkers.mcheckers.getLogger().info("2ndCheckError1");
                    break;
                case 2:
                    Clicker.openInventory(SetLoreMessage("Error:相手の駒を飛び越えられる手が存在します。飛び越えられる手を選択してください。",e.getInventory()));
                    Man10Checkers.mcheckers.getLogger().info("2ndCheckError2");
                    break;
                case 3:
                    Clicker.openInventory(SetLoreMessage("Error:最初に選択した駒の一つ斜めの駒か、相手の駒を飛び越えられる場合は二つ斜め前の駒を選択してください。",e.getInventory()));
                    Man10Checkers.mcheckers.getLogger().info("2ndCheckError3");
                    break;
                default:
                    Man10Checkers.mcheckers.getLogger().info("2ndCheckNoError");
                    ChangeTurn();
                    saveData(CurrentBoard);
                    break;
            }
            //invのタイトル変更方法(pass
            //ゲーム終了時の判定
            SetLoreMessage("",e.getInventory());
            Man10Checkers.mcheckers.getLogger().info("before wincheck");
            if(WinCheck(CurrentBoard)==0) return;
            //yml削除
            Man10Checkers.mcheckers.getLogger().info("pass wincheck");
            deleteData(CurrentBoard);
            //title変更
            //opponentゲット
            String WinnerName = "";
            Player Opponent = null;
            if(WinCheck(CurrentBoard)==1){
                WinnerName = Players.get(0).getName();
                Opponent = Players.get(1);
            } else if (WinCheck(CurrentBoard)==2) {
                WinnerName = Players.get(1).getName();
                Opponent = Players.get(0);
            }
            Clicker.sendMessage(WinnerName +"が勝利しました！！");
            Opponent.openInventory(getInv(WinnerName+"が勝利しました！！"));
            Opponent.sendMessage(WinnerName+"が勝利しました！！");
        }




    }

}