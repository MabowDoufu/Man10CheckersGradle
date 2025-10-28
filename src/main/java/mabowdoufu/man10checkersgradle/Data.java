package mabowdoufu.man10checkersgradle;


import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import static mabowdoufu.man10checkersgradle.BoardGameSys.*;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
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
import static org.bukkit.Material.*;
import static org.bukkit.Material.WHITE_GLAZED_TERRACOTTA;



public class Data {
    public static Map<String, GameData> games;

    public enum Ability{
        //列挙子の作成

        Trap("スキップトラップ",0),
        CreateKing("王様即位",1),
        ForceMove("神の手",2),
        Mine("地雷",3),
        CreateMen("救援",4);


        //列挙子の中に入れる要素の作成
        private final String label;
        private final int id;

        //列挙型の定義
        private Ability(String label, int id) {
            this.label = label;
            this.id = id;
        }


        //列挙子内の要素へのアクセス方法は自分で作る必要がある
        public String getLabel() {
            return label;
        }

        public int getId(){
            return id;
        }

        public static Ability fromLabel(String str){
            for (Ability a : Ability.values()) {
                if (a.getLabel().equals(str)) {
                    return a;
                }
            }
            return null;
        }

        public static Ability fromId(int i){
            for (Ability a : Ability.values()) {
                if (a.getId() == i) {
                    return a;
                }
            }
            return null;
        }

        public static Ability getRandom(){
            Random rand = new Random();
            int i = rand.nextInt(5) + 1;
            return fromId(i);
        }
    }

    //列挙型Abilityをキーとしたability_detailsマップ
    //Abilityを取得済みのときに、それをキーとしてability_detailsを呼び出せばそのマップの中身が得られる
    public static final Map<Ability, List<Component>> ability_details = Map.ofEntries(
            Map.entry(
                    Ability.Trap, List.of(
                            Component.text("ボード上のマスにトラップを仕掛けることができる"),
                            Component.text("このトラップの上に駒を置いた人は、次のターンがスキップされる")
                    )
            ),
            Map.entry(
                    Ability.CreateKing, List.of(
                            Component.text("好きな駒をキングにすることができる")
                    )
            ),
            Map.entry(
                    Ability.ForceMove, List.of(
                            Component.text("チェッカーのルールを無視して"),
                            Component.text("指定した駒を斜め四方向のうち好きな方向に動かすことができる"),
                            Component.text("移動先に駒が置いてあった場合、その駒は消滅する")
                    )
            ),
            Map.entry(
                    Ability.Mine, List.of(
                            Component.text("地雷をボード上に仕掛けることができる"),
                            Component.text("このマスに駒を置いた場合、その駒と、地雷に隣接する駒が消滅する")
                    )
            ),
            Map.entry(
                    Ability.CreateMen, List.of(
                            Component.text("自分の駒を新しく作ることができる"),
                            Component.text("自分側の最も端の列のマスに設置できる")
                    )
            )
    );


    public static final Map<Ability, Material> material = Map.ofEntries(
            Map.entry(Ability.Trap, TRIPWIRE_HOOK),
            Map.entry(Ability.CreateKing, TOTEM_OF_UNDYING),
            Map.entry(Ability.ForceMove, NETHER_STAR),
            Map.entry(Ability.Mine, TNT),
            Map.entry(Ability.CreateMen,SKELETON_HORSE_SPAWN_EGG)
    );

    public static String getBoardNameFromUUID(UUID uuid){
        for (GameData g: games.values()){
            if(g.p1 == uuid || g.p2 == uuid) return g.toString();
        }
        return null;
    }

    public static GameData getGameFromUUID(UUID uuid){
        for (GameData g: games.values()){
            if(g.p1 == uuid || g.p2 == uuid) return g;
        }
        return null;
    }
}
