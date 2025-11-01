package mabowdoufu.man10checkersgradle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.Math.abs;
import static mabowdoufu.man10checkersgradle.BoardGameSys.*;
import static mabowdoufu.man10checkersgradle.Data.*;
import static org.bukkit.Material.*;
import static org.bukkit.Material.WHITE_GLAZED_TERRACOTTA;


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
        if(!e.getView().getTitle().contains(Config.prefix)) return;
        e.setCancelled(true);
        if(e.getRawSlot()== -999) return;
        if(e.getRawSlot() > 53) return;
        if(e.getView().getTitle().contains("アビリティ選択")){
            AbilityInventoryClick(e);
            return;
        }

        if(e.getView().getTitle().contains("Ability")){
            abilityUseInventoryClick(e);
            return;
        }

        Man10Checkers.mcheckers.getLogger().info("Turn:"+String.valueOf(BoardGameSys.Turn));
        if(e.getRawSlot() == 45){
            //ability画面に移行
            e.getWhoClicked().openInventory(getAbilityInv());
            return;
        }
        Man10Checkers.mcheckers.getLogger().info("invevent2");
        Player Clicker = (Player) e.getWhoClicked();
        LoadData(getBoard(e.getWhoClicked().getName()));
        if(Clicker != Players.get(Turn-1)){
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
            Man10Checkers.mcheckers.getLogger().info("Click0--------------------");
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
                    Click = 1;

                    saveData(CurrentBoard);
                    Man10Checkers.mcheckers.getLogger().info("click sets 1");
            }
        }else if(Click == 1){
            Man10Checkers.mcheckers.getLogger().info("Click1--------------------");
            y2 = e.getRawSlot() % 9;
            x2 = (e.getRawSlot() - y2)/9;
            //作業メモ： error等の挙動をまず確認・二回目のマス選択で、離れたマスを選択した場合に即returnさせる・warning消す
            BoardInput(CurrentBoard,x1,y1,x2,y2);

            switch (ErrorType) {
                case 1:
                    Clicker.openInventory(SetLoreMessage("Error:チェッカーで使用しないマスを選択しています。", e.getInventory()));
                    Man10Checkers.mcheckers.getLogger().info("2ndCheckError1");
                    LoadData(CurrentBoard);
                    Click = 0;
                    saveData(CurrentBoard);
                    return;
                case 2:
                    Clicker.openInventory(SetLoreMessage("Error:相手の駒を飛び越えられる手が存在します。飛び越えられる手を選択してください。", e.getInventory()));
                    Man10Checkers.mcheckers.getLogger().info("2ndCheckError2");
                    LoadData(CurrentBoard);
                    Click = 0;
                    saveData(CurrentBoard);
                    return;
                case 3:
                    Clicker.openInventory(SetLoreMessage("Error:最初に選択した駒の一つ斜めの駒か、相手の駒を飛び越えられる場合は二つ斜め前の駒を選択してください。", e.getInventory()));
                    Man10Checkers.mcheckers.getLogger().info("2ndCheckError3");
                    LoadData(CurrentBoard);
                    Click = 0;
                    saveData(CurrentBoard);
                    return;
                case 4:
                    Clicker.openInventory(SetLoreMessage("Error:その方向に駒は進めません。", e.getInventory()));
                    Man10Checkers.mcheckers.getLogger().info("2ndCheckError4");
                    LoadData(CurrentBoard);
                    Click = 0;
                    saveData(CurrentBoard);
                    return;
                default:
                    GameData g = Data.getGameFromUUID(e.getWhoClicked().getUniqueId());

                    Man10Checkers.mcheckers.getLogger().info("2ndCheckNoError");
                    if (!g.continuousTurn) {
                        ChangeTurn();
                    } else {
                        g.continuousTurn = false;
                    }
                    Man10Checkers.mcheckers.getLogger().info("click sets 0");
                    Click = 0;

                    saveData(CurrentBoard);
                    Players.get(0).openInventory(getInv(""));
                    Players.get(1).openInventory(getInv(""));
                    if (g.traps[x2][y2]){
                        g.continuousTurn = true;
                        Player Trapped = null;
                        Player Opponent = null;
                        if (Turn == 1) {
                            Trapped = Players.get(0);
                            Opponent = Players.get(1);
                        } else if (Turn == 2) {
                            Trapped = Players.get(1);
                            Opponent = Players.get(0);
                        }
                        Trapped.openInventory(getInv("あなたは罠にかかった！次のターンは一回休みです"));
                        Opponent.openInventory(getInv("相手は罠にかかった！次は二回続けて行動できます"));
                    }
                    break;
            }
            //invのタイトル変更方法(pass
            //ゲーム終了時の判定
            SetLoreMessage("",e.getInventory());
            Man10Checkers.mcheckers.getLogger().info("before wincheck");

            String WinnerName = "";
            Player Opponent = null;

            if(WinCheck(CurrentBoard)==0){

                switch (RestTurn()){
                    case 0:
                        return;
                    case 1:
                        WinnerName = Players.get(0).getName();
                        Opponent = Players.get(1);
                        break;
                    case 2:
                        WinnerName = Players.get(1).getName();
                        Opponent = Players.get(0);
                        break;
                    case 3:
                        if(Turn==1){
                            Opponent = Players.get(1);
                        }else{
                            Opponent = Players.get(0);
                        }
                        deleteData(CurrentBoard);
                        Clicker.openInventory(getInv("引き分けになりました！"));
                        Opponent.openInventory(getInv("引き分けになりました！"));
                        Clicker.sendMessage(Config.prefix+"引き分けになりました！");
                        Opponent.sendMessage(Config.prefix+"引き分けになりました！");
                        return;
                }

            }else{
                //yml削除
                Man10Checkers.mcheckers.getLogger().info("pass wincheck");
                //title変更
                //opponentゲット

                if (WinCheck(CurrentBoard) == 1) {
                    WinnerName = Players.get(0).getName();
                    Opponent = Players.get(1);
                } else if (WinCheck(CurrentBoard) == 2) {
                    WinnerName = Players.get(1).getName();
                    Opponent = Players.get(0);
                }
            }

            Clicker.openInventory(getInv(WinnerName+"が勝利しました！！"));
            Opponent.openInventory(getInv(WinnerName+"が勝利しました！！"));
            Clicker.sendMessage(Config.prefix+WinnerName +"が勝利しました！！");
            Opponent.sendMessage(Config.prefix+WinnerName+"が勝利しました！！");
            deleteData(CurrentBoard);
        }




    }
    public void AbilityInventoryClick(InventoryClickEvent e){
        //アビリティinvの場合
        LoadData(Data.getBoardNameFromUUID(e.getWhoClicked().getUniqueId()));
        Ability a = null;
        String invTitle = null;
        switch (e.getSlot()) {
            case 19:
                a =Ability.Trap;
                invTitle = "トラップの設置場所を選択してください";
                break;
            case 20:
                a =Ability.CreateKing;
                invTitle = "キングにする自分の駒を選択してください";

                break;
            case 21:
                a =Ability.ForceMove;
                invTitle = "強制的に動かす駒を選択してください";

                break;
            case 22:
                a =Ability.Mine;
                invTitle = "地雷の設置場所を選択してください";
                break;
            case 23:
                a =Ability.CreateMen;
                invTitle = "新しく駒をおく場所を選択してください";
                boolean creatable = false;
                int endRowSlot = (Turn == 1) ? 0 : 8;
                int[] availableColumnSlot = {0,2,4};

                for(int column : availableColumnSlot){
                    if(Board[column][endRowSlot] == 0) creatable = true;
                }
                if(!creatable){
                    ItemStack Icon = e.getInventory().getItem(e.getRawSlot());
                    ItemMeta IconMeta = e.getInventory().getItem(e.getRawSlot()).getItemMeta();
                    List<Component> lore = new ArrayList<>(List.of(Component.text("§c新しく駒を設置できる箇所がありません！")));
                    lore.addAll(IconMeta.lore());
                    IconMeta.lore(lore);
                    Icon.setItemMeta(IconMeta);
                    e.getInventory().setItem(e.getRawSlot(),Icon);
                    return;
                }

                break;
            case 45:
                e.getWhoClicked().openInventory(getInv(getBoard(e.getWhoClicked().getName())));
                return;
        }

        GameData gameData = getGameFromUUID(e.getWhoClicked().getUniqueId());
        if(e.getWhoClicked().getUniqueId() == gameData.p1){
            if(gameData.p1AbilityPoint.get(a) == 0) return;
        }else{
            if(gameData.p2AbilityPoint.get(a) == 0) return;
        }
        Inventory abilityUseInv = Bukkit.createInventory(null,54,Config.prefix +"Ability|"+ invTitle);
        abilityUseInv.setItem(45,createGUIItem(BARRIER,"アビリティの使用をキャンセルする",""));
        abilityUseInv.setItem(53,createGUIItem(LIGHT_GRAY_CONCRETE,"アビリティを使用するマスを選択してください",""));

        e.getWhoClicked().openInventory(abilityUseInv);

    }

    public void abilityUseInventoryClick(InventoryClickEvent e) {
        if (e.getRawSlot() == 45) {
            e.getWhoClicked().openInventory(getAbilityInv());
            return;
        }

        if (e.getRawSlot() % 2 == 1) return;
        Ability a = null;
        int stage = 1;
        int y = (int) Math.floor(((double) e.getRawSlot() / 9));
        int x = e.getRawSlot() % 9;
        GameData g = Data.getGameFromUUID(e.getWhoClicked().getUniqueId());

        if (e.getView().getTitle().contains("トラップを置くマスを選択してください")) {
            a = Ability.Trap;
        } else if (e.getView().getTitle().contains("キングにする駒を選択してください")) {
            a = Ability.CreateKing;
        } else if (e.getView().getTitle().contains("移動させる駒を選択してください")) {
            a = Ability.ForceMove;
        } else if (e.getView().getTitle().contains("選択した駒の移動先を選択してください")) {
            a = Ability.ForceMove;
            stage = 2;
        } else if (e.getView().getTitle().contains("地雷を設置するマスを選択してください")) {
            a = Ability.Mine;
        } else if (e.getView().getTitle().contains("新しい駒を置くマスを選択してください")) {
            a = Ability.CreateMen;
        }
        if (e.getRawSlot() == 53 && e.getInventory().getItem(53).getType() == LIME_CONCRETE) {
            switch (a) {
                case Ability.Trap:
                    g.traps[y][x] =true;
                case Ability.CreateKing:
                    Board[y][x] = Turn - 1;
                case ForceMove:
                    if (stage == 1) return;
                    Board[y][x] = Board[g.abilityY][g.abilityX];
                    Board[g.abilityY][g.abilityX] = 0;
                    IsKing[y][x] = IsKing[g.abilityY][g.abilityX];
                    IsKing[g.abilityY][g.abilityX] = false;
                case Ability.Mine:
                    g.mines[y][x] =true;
                case Ability.CreateMen:
                    Board[y][x] = Turn-1;
                    IsKing[y][x] = false;
            }
            if(Turn ==1){
                g.p1AbilityPoint.replace(a,g.p1AbilityPoint.get(a)-1);
            }else{
                g.p2AbilityPoint.replace(a,g.p2AbilityPoint.get(a)-1);
            }

            String boardName = getBoardNameFromUUID(e.getWhoClicked().getUniqueId());
            LoadData(boardName);
            e.getWhoClicked().openInventory(getInv(boardName));
            return;
        }


        if (Board[y][x] != 0 && !(a == Ability.CreateMen || a == Ability.ForceMove)) return;
        if (Board[y][x] == Turn - 1 && (!(a == Ability.ForceMove && stage == 1) || a == Ability.CreateKing)) return;
        if (abs(x - g.abilityX) < 2 && abs(y - g.abilityY) < 2 && a == Ability.ForceMove && stage == 2) return;
        Inventory inv;
        if (a == Ability.ForceMove) {
            inv = getInv("Ability|" + g.toString());
        } else {
            inv = getInv("");
            //ここに処理
        }
        if (a == Ability.CreateKing || a == Ability.CreateMen || (a == Ability.ForceMove && stage == 2)) {
            inv.setItem(e.getRawSlot(), createGUIItem(GREEN_CONCRETE, "", ""));
        }
        if (a == Ability.Mine) {
            inv.setItem(e.getRawSlot(), createGUIItem(TNT, "", ""));
        }
        if (a == Ability.Trap) {
            inv.setItem(e.getRawSlot(), createGUIItem(TRIPWIRE_HOOK, "", ""));
        }
        if (a == Ability.ForceMove && stage == 1) {
            inv.setItem(e.getRawSlot(), createGUIItem(AIR, "", ""));
        }
        if (!(a == Ability.ForceMove && stage == 1)) {
            inv.setItem(53, createGUIItem(LIME_CONCRETE, "確定する", ""));
        }

    }

    public Inventory getAbilityInv(){
        Inventory abilityInv = Bukkit.createInventory(null, 54, Config.prefix + "アビリティを選択してください");
        int i = 0;
        //Mapのkeyからenumの列挙子を取得
        for(Data.Ability a: Data.ability_details.keySet()){
            ItemStack item = createGUIItem(material.get(a),"黒の駒","");
            ItemMeta meta = item.getItemMeta();
            meta.lore(Data.ability_details.get(a));
            //meta.loreはkyori.adventureのcomponentに対応
            item.setItemMeta(meta);
            abilityInv.setItem(i+19, item);
            i++;
        }
        abilityInv.setItem(45,createGUIItem(END_CRYSTAL,"アビリティ選択画面を開く",""));
        return abilityInv;
    }
}