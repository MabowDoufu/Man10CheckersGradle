package mabowdoufu.man10checkersgradle;

import mabowdoufu.man10checkersgradle.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.io.File;
import java.util.*;

import static mabowdoufu.man10checkersgradle.Man10Checkers.logg;
import static mabowdoufu.man10checkersgradle.Man10Checkers.mcheckers;

public class Commands implements @Nullable CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //if (!sender.hasPermission("mcheckers.op")) return true;
        BoardGameSys.ResetVars();
        switch (args.length){
            case 1:
                if (args[0].equals("help")){
                    //sender.sendMessage(Config.prefix + "§r/mcheckers list : 開催中のリバーシのリストを表示");廃止
                    sender.sendMessage(Config.prefix + "§r/mcheckers start : リバーシを開始します");
                    sender.sendMessage(Config.prefix + "§r/mcheckers join [ボード名] : リバーシに参加します");
                    sender.sendMessage(Config.prefix + "§r/mcheckers open : リバーシの画面を再表示します（ゲーム参加時のみ有効）");
                    if (sender.hasPermission("mcheckers.op")){
                        sender.sendMessage(Config.prefix + "§r=== 管理者コマンド ===");
                        sender.sendMessage(Config.prefix + "§r/mcheckers [on/off] : システムを稼働/停止します");
                        //sender.sendMessage(Config.prefix + "§r/mcheckers Board create [名前] : ボードを作成します（廃止）");
                        sender.sendMessage(Config.prefix + "§r/mcheckers end [ボード名] : ゲームを強制終了します");
                    }
                }
                else if (args[0].equals("on") && sender.hasPermission("mcheckers.op")){
                    if (Config.system){
                        sender.sendMessage(Config.prefix + "§r既にONです");
                        return true;
                    }
                    Config.system = true;
                    mcheckers.getConfig().set("system", Config.system);
                    mcheckers.saveConfig();
                    sender.sendMessage(Config.prefix + "§rONにしました");
                    return true;
                }
                else if (args[0].equals("off") && sender.hasPermission("mcheckers.op")){
                    if (!Config.system){
                        sender.sendMessage(Config.prefix + "§r既にOFFです");
                        return true;
                    }
                    Config.system = false;
                    mcheckers.getConfig().set("system", Config.system);
                    mcheckers.saveConfig();
                    sender.sendMessage(Config.prefix + "§rOFFにしました");
                    return true;
                }
                else if (args[0].equals("start")){
                    //finish
                    if (!Config.system){
                        sender.sendMessage(Config.prefix + "§rシステムはOFFです");
                        return true;
                    }
                    if (!sender.hasPermission("mcheckers.open")){
                        sender.sendMessage(Config.prefix + "§r権限がありません");
                        return true;
                    }
                    UUID sender_uuid = ((Player) sender).getUniqueId();
                    File gameyml = new File("plugins/Man10Checkers/game.yml");
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(gameyml);
                    boolean ExistBoard = false;
                    boolean IsJoining = false;
                    for (String BoardName : yml.getKeys(false)) {
                        if (sender.getName().equals(BoardName)){
                            ExistBoard = true;
                        }
                        //同じ個所ここ含めて3つあり..ここだけ修正中
                        if(yml.getList(BoardName + ".Players") !=null) {
                            for (Player joinning_player : (List<Player>) yml.getList(BoardName + ".Players")) {
                                if (sender_uuid.toString().equals(joinning_player.getName())) {
                                    IsJoining = true;
                                }
                            }
                        }
                        //
                    }
                    if (IsJoining){
                        sender.sendMessage(Config.prefix + "§r別のボードでゲーム中は参加できません");
                        return true;
                    }
                    if (ExistBoard){
                        sender.sendMessage(Config.prefix + "§rあなたはすでにボードを開いています");
                        return true;
                    }
                    mcheckers.getLogger().info("sender.toString():"+sender.toString());
                    BoardGameSys.ResetYml((sender).getName());
                    BoardGameSys.Recruiting = true;
                    BoardGameSys.DuringGame = false;
                    BoardGameSys.Players.add((Player) sender);
                    mcheckers.getLogger().info("BoardGameSys.Players.toString():"+BoardGameSys.Players.toString());
                    BoardGameSys.saveData((sender).getName());
                    Recruitment.waitingTimer(sender.getName());
                    /// 以下検証用
                    File gameyml2 = new File("plugins/Man10Checkers/game.yml");
                    YamlConfiguration yml2 = YamlConfiguration.loadConfiguration(gameyml);
                    mcheckers.getLogger().info("yml2.getList(sender.getName() + \".Players\").getFirst():"+yml2.getList(sender.getName() + ".Players").getFirst());
                    mcheckers.getLogger().info("((Player) yml2.getList(sender.getName() + \".Players\").getFirst()).toString():"+ ((Player) yml2.getList(sender.getName() + ".Players").getFirst()).toString());
                    Player p = (Player) yml2.getList(sender.getName() + ".Players").getFirst();
                    return true;
                }
                else if (args[0].equals("open")){
                    ///再度gui開く処理
                    UUID sender_uuid = ((Player) sender).getUniqueId();
                    File gameyml = new File("plugins/Man10Checkers/game.yml");
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(gameyml);
                    for (String BoardName : yml.getKeys(false)) {
                        for (Player joinning_player : (List<Player>) yml.getList(BoardName + ".Players")) {
                            if (sender.getName().equals(joinning_player.getName())) {
                                BoardGameSys.LoadData(BoardName);
                                ((Player) sender).openInventory(BoardGameSys.getInv(""));
                                return true;
                            }
                        }
                    }
                    sender.sendMessage(Config.prefix + "あなたは試合中のゲームに参加していません");
                    return true;
                }
                else if (args[0].equals("list")){
                    return true;
                }
                break;
            case 2:
                if (args[0].equals("join")){
                    if (!Config.system){
                        sender.sendMessage(Config.prefix + "§rシステムはOFFです");
                        return true;
                    }
                    File gameyml = new File("plugins/Man10Checkers/game.yml");
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(gameyml);
                    UUID sender_uuid = ((Player) sender).getUniqueId();
                    boolean IsJoining = false;
                    boolean ExistBoard = false;
                    for (String BoardName : yml.getKeys(false)) {
                        if (args[1].equals(BoardName)){
                            ExistBoard = true;
                            mcheckers.getLogger().info("BoardName:"+BoardName);
                        }
                        
                        for (Player joinning_player : (List<Player>) yml.getList(BoardName + ".Players")) {
                            if (sender.getName().equals(joinning_player.getName())) {
                                IsJoining = true;
                            }
                        }
                    }
                    if (IsJoining){
                        sender.sendMessage(Config.prefix + "§r別のボードでゲーム中は参加できません");
                        return true;
                    }
                    if (!ExistBoard) {
                        sender.sendMessage(Config.prefix + "§rそのボードのゲームは存在しません");
                        return true;
                    }
                    BoardGameSys.LoadData(args[1]);
                    if (!BoardGameSys.Recruiting){
                        sender.sendMessage(Config.prefix + "§rそのボードはプレイヤーを募集していません");
                        return true;
                    }
                    ///ゲーム参加処理
                    BoardGameSys.Players.add((Player) sender);
                    BoardGameSys.Recruiting = false;
                    logg("DuringGameVar set true");
                    BoardGameSys.DuringGame = true;
                    BoardGameSys.saveData(args[1]);
                    //gui開く処理
                    /// ここまで到達
                    Inventory inv = BoardGameSys.getInv("");
                    (BoardGameSys.Players.get(0)).openInventory(inv);
                    (BoardGameSys.Players.get(1)).openInventory(inv);
                    return true;
                }
                else if (args[0].equals("end") && sender.hasPermission("mcheckers.op")){
                    File gameyml = new File("plugins/Man10Checkers/game.yml");
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(gameyml);
                    if(!yml.getKeys(false).contains(args[1])){
                        sender.sendMessage(Config.prefix + "§r指定したボードは存在しません");
                        return true;
                    }
                    try{
                        yml.set(args[1],null);
                        ///ゲーム強制終了処理を書く(events終わった後に書く)
                        yml.save(gameyml);
                        sender.sendMessage(Config.prefix + "§r終了しました");
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender.sendMessage(Config.prefix + "§rエラーが発生しました");
                    }
                    return true;
                }
                break;
        }
        sender.sendMessage(Config.prefix + "§r/mcheckers helpでコマンドを確認");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        //if (!sender.hasPermission("mcheckers.op")) return List.of();
        if (args.length == 1){
            if (sender.hasPermission("mcheckers.op")) return Arrays.asList("help", "start", "join", "on", "off", "end", "open");
            else return Arrays.asList("help","start", "join", "open");
        }
        else if (args.length == 2){
            File gameyml = new File("plugins/Man10Checkers/game.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(gameyml);
            if (args[0].equals("join")){
                //Recruiting
                List<String> Joinable_Boards = new ArrayList<String>();
                for (String BoardName : yml.getKeys(false)) {
                        if(yml.getBoolean(BoardName + ".Recruiting")) {
                            Joinable_Boards.add(BoardName);
                        }
                    }
                return Joinable_Boards;
            }
            if (args[0].equals("end") && sender.hasPermission("mcheckers.op")){
                List<String> BoardList = new ArrayList<String>();
                for (String BoardName : yml.getKeys(false)) {
                        BoardList.add(BoardName);
            }
                return BoardList;
        }
        else if (args.length == 3){
            if (args[0].equals("Board") && args[1].equals("create") && sender.hasPermission("mcheckers.op")){
                Collections.singletonList("[名前]");
                }
            }
        }
        return List.of();
    }
}