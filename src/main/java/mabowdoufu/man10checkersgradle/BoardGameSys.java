package mabowdoufu.man10checkersgradle;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import static mabowdoufu.man10checkersgradle.Man10Checkers.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.spi.AbstractResourceBundleProvider;
import static java.lang.Math.*;
import static org.bukkit.Material.*;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
public class BoardGameSys extends JavaPlugin{
    // 盤面情報
    // 0:未設置, 1:黒 2:白

    public static File f;
    public static YamlConfiguration yml;
    public static int[][] Board = new int[6][9];
    public static boolean[][] IsKing = new boolean[6][9];
    public static int PlayerPiece;
    public static boolean DuringGame;
    public static boolean Recruiting;
    public static int Turn;
    public static int Click;
    public static List<Player> Players = new ArrayList<>();
    public static int ErrorType;
    //のちのちsysから切り離し
    public static int FirstClickCheck(int x1, int y1){
        Man10Checkers.mcheckers.getLogger().info("FirstClickCheck----");
        Man10Checkers.mcheckers.getLogger().info("Turn:"+ Turn);
        Man10Checkers.mcheckers.getLogger().info("Board[x1][y1]:" +Board[x1][y1]);
        Man10Checkers.mcheckers.getLogger().info("FirstClickCheck---end-");
        if ((((x1 % 2) + (y1 % 2) % 2) == 1)) return 1;
        if (Board[x1][y1] != Turn) return 2;
        return 0;
    }
    public static void ChangeTurn(){
        Turn++;
        if(Turn ==3) Turn = 1;
    }
    public static void ResetVars(){
        Board = new int[6][9];
        IsKing = new boolean[6][9];
        Players = new ArrayList<>();
        Turn = 1;
        Click = 0;
        logg("Click sets 0");

    }
        //x軸はinv左上から下方向　y軸は右方向
    public static void ResetYml(String Boardname) {
        f = new File("plugins/Man10Checkers/game.yml");
        yml = YamlConfiguration.loadConfiguration(f);
        Board[0][0] = 1;
        Board[2][0] = 1;
        Board[4][0] = 1;
        Board[1][1] = 1;
        Board[3][1] = 1;
        Board[5][1] = 1;
        Board[0][2] = 1;
        Board[2][2] = 1;
        Board[4][2] = 1;
        Board[0][6] = 2;
        Board[2][6] = 2;
        Board[4][6] = 2;
        Board[1][7] = 2;
        Board[3][7] = 2;
        Board[5][7] = 2;
        Board[0][8] = 2;
        Board[2][8] = 2;
        Board[4][8] = 2;
        yml.set(Boardname+ ".Board", Board);
        yml.set(Boardname+ ".IsKing", IsKing);
        yml.set(Boardname+ ".DuringGame", false);
        yml.set(Boardname+ ".Recruiting", false);
        yml.set(Boardname+ ".Turn", 1);
        yml.set(Boardname+ ".Click", 1);
        PlayerPiece =1;
        try {
            yml.save(f);
        } catch (IOException ignored) {

        }
    }

    public static void LoadData(String Boardname) {
        f = new File("plugins/Man10Checkers/game.yml");
        yml = YamlConfiguration.loadConfiguration(f);
        /// gpt----------------
        // YAML から読み込んだオブジェクトを List<List<Integer>> として取得
        List<List<Integer>> list2d = (List<List<Integer>>) yml.get(Boardname+".Board");

        // int[][] に変換
        int[][] board = new int[list2d.size()][list2d.get(0).size()];
        for (int i = 0; i < list2d.size(); i++) {
            for (int j = 0; j < list2d.get(i).size(); j++) {
                board[i][j] = list2d.get(i).get(j);
            }
        }

        // グローバル Board に代入
        Board = board;
        // YAML から読み込んだオブジェクトを List<List<Boolean>> として取得
        List<List<Boolean>> list2d_2 = (List<List<Boolean>>) yml.get(Boardname+".IsKing");

        // boolean[][] に変換
        boolean[][] isKing = new boolean[list2d_2.size()][list2d_2.get(0).size()];
        for (int i = 0; i < list2d_2.size(); i++) {
            for (int j = 0; j < list2d_2.get(i).size(); j++) {
                isKing[i][j] = list2d_2.get(i).get(j);
            }
        }

        // グローバル IsKing に代入
        IsKing = isKing;
        /// gpt----------------
        //Board = (int[][]) yml.get(Boardname+".Board");
        //IsKing = (boolean[][]) yml.get(Boardname+".IsKing");
        DuringGame = yml.getBoolean(Boardname+".DuringGame");
        Recruiting = yml.getBoolean(Boardname+".Recruiting");
        Turn = yml.getInt(Boardname+".Turn");
        Click = yml.getInt(Boardname+".Click");
        logg("Click sets "+ Click);
        Players = (List<Player>) yml.getList(Boardname+".Players");
    }

    public static void saveData(String Boardname) {
        f = new File("plugins/Man10Checkers/game.yml");
        yml = YamlConfiguration.loadConfiguration(f);
        yml.set(Boardname+".Board", Board);
        yml.set(Boardname+".IsKing", IsKing);
        yml.set(Boardname+".DuringGame", DuringGame);
        yml.set(Boardname+".Recruiting", Recruiting);
        yml.set(Boardname+".Turn", Turn);
        yml.set(Boardname+".Click", Click);
        yml.set(Boardname+".Players", Players);

        try {
            yml.save(f);
        } catch (Exception ignored) {
        }
    }
    public static void deleteData(String Boardname) {
        f = new File("plugins/Man10Checkers/game.yml");
        yml = YamlConfiguration.loadConfiguration(f);
        yml.set(Boardname, null);
        try {
            yml.save(f);
        } catch (Exception ignored) {

        }
    }
    protected static ItemStack createGUIItem(final Material material, final String name, final String... lore){
        final ItemStack item=new ItemStack(material,1);
        final ItemMeta meta=item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    ///テキサスホールデムプラグインより
    public static String getBoard(String player_name){
        for (String BoardName : yml.getKeys(false)) {
            for (Player joinning_player : (List<Player>) yml.getList(BoardName + ".Players"))
                if(player_name.equals(joinning_player.getName())) {
                    return BoardName;
                }
        }
        return null;
    }
    public static Inventory getInv(String title){
        Inventory inv;

        inv= Bukkit.createInventory(null,54,Config.prefix + title);
        int j =0;
        for(int[] BoardRow : Board){
            int i =0;
            for (int Men : BoardRow){
                int slot = i + j*9;
                /// 181行でエラー j or iが out of index
                if(Men == 1 && !IsKing[j][i]) inv.setItem(slot,createGUIItem(BLACK_CONCRETE,"黒の駒",""));
                if(Men == 2 && !IsKing[j][i]) inv.setItem(slot,createGUIItem(WHITE_CONCRETE,"白の駒",""));
                if(Men == 1 && IsKing[j][i]) inv.setItem(slot,createGUIItem(BLACK_GLAZED_TERRACOTTA,"黒のキング",""));
                if(Men == 2 && IsKing[j][i]) inv.setItem(slot,createGUIItem(WHITE_GLAZED_TERRACOTTA,"白のキング",""));
                i++;
            }
            j++;
        }
        return inv;
    }

    public static boolean IsMyMen(String Boardname,int x1, int y1) {
        LoadData(Boardname);
        int selectpiece = Board[x1][y1];
        //相手の駒選択しているかどうか
        if (PlayerPiece == 1 && ((selectpiece == 3) || (selectpiece == 4))) {
            return false;
        }
        if (PlayerPiece == 2 && ((selectpiece == 1) || (selectpiece == 2))) {
            return false;
        }
        return true;
    }

    //directionは1または-1のみをとる
    private static boolean IsJumpable(int x1, int y1, int xdirection, int ydirection) {
        int enemypiece;
        if (Turn == 1) {
            enemypiece = 2;
        } else {
            enemypiece = 1;
        }
        try {
            //out of bounds

            if (Board[x1 + xdirection][y1 + ydirection] == enemypiece &&
                    Board[x1 + 2 * xdirection][y1 + 2 * ydirection] == 0) {
                logg(String.valueOf("2 * xdirection:"+(2 * xdirection)));
                logg(String.valueOf("2 * ydirection:"+(2 * ydirection)));
                logg(String.valueOf("enemypiece:"+(enemypiece)));
                logg(String.valueOf("Turn:"+(Turn)));
                logg(String.valueOf("Board[x1 + xdirection][y1 + ydirection] == enemypiece"+(Board[x1 + xdirection][y1 + ydirection] ==enemypiece)));
                logg(String.valueOf("Board[x1 + 2 * xdirection][y1 + 2 * ydirection] == 0"+(Board[x1 + 2 * xdirection][y1 + 2 * ydirection] == 0)));
                Man10Checkers.mcheckers.getLogger().info("Jumpable true");
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {

        }
        //Man10Checkers.mcheckers.getLogger().info("Jumpable false");
        return false;
    }

    private static void ContinuousMove(int x1, int y1) {
        List<Integer> Movable = new ArrayList<Integer>();
        Man10Checkers.mcheckers.getLogger().info("ContinousMove start");
        Man10Checkers.mcheckers.getLogger().info("x1:"+x1);
        Man10Checkers.mcheckers.getLogger().info("y1:"+y1);
        if (PlayerPiece == 1 || (PlayerPiece == 2 && IsKing[x1][y1])) {
            try {
                if (SelectCorrectMove(x1, y1, x1 + 2, y1 + 2) && IsJumpable(x1,y1,1,1)) {
                    Movable.add(1);
                    Man10Checkers.mcheckers.getLogger().info("右下追加");
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
            try {
                if (SelectCorrectMove(x1, y1, x1 - 2, y1 + 2) && IsJumpable(x1,y1,-1,1)) {
                    Movable.add(2);
                    Man10Checkers.mcheckers.getLogger().info("右上追加");
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
        }
        if (PlayerPiece == 2 || (PlayerPiece == 1 && IsKing[x1][y1])) {
            try {
                if (SelectCorrectMove(x1, y1, x1 + 2, y1 - 2) && IsJumpable(x1,y1,1,-1)) {
                    Movable.add(3);
                    Man10Checkers.mcheckers.getLogger().info("左下追加");
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
            try {
                if (SelectCorrectMove(x1, y1, x1 - 2, y1 - 2) && IsJumpable(x1,y1,-1,-1)) {
                    Movable.add(4);
                    Man10Checkers.mcheckers.getLogger().info("左上追加");
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
        }
        Random random = new Random();
        logg("Movable.toString():"+Movable.toString());
        logg(String.valueOf(Movable.size() - 1));
        if(Movable.isEmpty()) return;
        int movePattern;
        if(Movable.size() == 1){
            movePattern = Movable.getFirst();
        }else{
            movePattern = Movable.get(random.nextInt(Movable.size() - 1)); // 0からMovable.size()-1までの整数を生成
        }
        if (movePattern == 1) {
            try {
                Man10Checkers.mcheckers.getLogger().info("右下");
                logg2(Board);

                IsKing[x1 + 2][y1 + 2] = IsKing[x1][y1];
                Board[x1][y1] = 0;
                Board[x1 + 2][y1 + 2] = PlayerPiece;
                IsKing[x1][y1] = false;

                Board[x1 + 1][y1 + 1] = 0;
                IsKing[x1 + 1][y1 + 1] = false;
                logg2(Board);
                ContinuousMove(x1 + 2, y1 + 2);
            } catch (Exception ignored) {

            }
        } else if (movePattern == 2) {
            try {
                Man10Checkers.mcheckers.getLogger().info("右上");
                logg2(Board);
                IsKing[x1 - 2][y1 + 2] = IsKing[x1][y1];
                Board[x1][y1] = 0;
                Board[x1 - 2][y1 + 2] = PlayerPiece;
                IsKing[x1][y1] = false;

                Board[x1 - 1][y1 + 1] = 0;
                IsKing[x1 - 1][y1 + 1] = false;
                logg2(Board);
                ContinuousMove(x1 - 2, y1 + 2);
            } catch (Exception ignored) {

            }
        } else if (movePattern == 3) {
            try {
                Man10Checkers.mcheckers.getLogger().info("左下");
                logg2(Board);
                IsKing[x1 + 2][y1 - 2] = IsKing[x1][y1];
                Board[x1][y1] = 0;
                Board[x1 + 2][y1 - 2] = PlayerPiece;
                IsKing[x1][y1] = false;

                Board[x1 + 1][y1 - 1] = 0;
                IsKing[x1 + 1][y1 - 1] = false;
                logg2(Board);
                ContinuousMove(x1 + 2, y1 - 2);
            } catch (Exception ignored){

            }
        } else if (movePattern == 4) {
            try {
                Man10Checkers.mcheckers.getLogger().info("左上");
                logg2(Board);
                IsKing[x1 - 2][y1 - 2] = IsKing[x1][y1];
                Board[x1][y1] = 0;
                Board[x1 - 2][y1 - 2] = PlayerPiece;
                IsKing[x1][y1] = false;

                Board[x1 - 1][y1 - 1] = 0;
                IsKing[x1 - 1][y1 - 1] = false;
                logg2(Board);
                ContinuousMove(x1 - 2, y1 - 2);
            } catch (Exception ignored){

            }
        }
    }

    //相手の駒をジャンプ可能な手があり、その場合にその手を選択しているかどうか
    private static boolean SelectCorrectMove(int x1, int y1, int x2, int y2) {
        //yml読み込み
        int enemypiece;
        if (PlayerPiece == 1) {
            enemypiece = 2;
        } else {
            enemypiece = 1;
        }
        //placeableチェック
        int checkX = 0;
        boolean exist = false;
        for (int[] row : Board) {
            int checkY = 0;
            for (int piece : row) {
                if (piece == PlayerPiece) {
                    if (IsJumpable(checkX, checkY, 1, 1)) {
                        exist = true;
                        if (((checkX == x1) && (checkY == y1) && (x2-x1==2) && (y2-y1==2))&&(Turn== 1|| IsKing[x1][y1])) {
                            return true;
                        }
                    }
                    if (IsJumpable(checkX, checkY, -1, 1)) {
                        exist = true;
                        if (((checkX == x1) && (checkY == y1) && (x2-x1==-2) && (y2-y1==2))&&(Turn== 1|| IsKing[x1][y1])) {
                            return true;
                        }
                    }
                    if (IsJumpable(checkX, checkY, 1, -1)) {
                        exist = true;
                        if (((checkX == x1) && (checkY == y1) && (x2-x1==2) && (y2-y1==-2))&&(Turn== 2|| IsKing[x1][y1])) {
                            return true;
                        }
                    }
                    if (IsJumpable(checkX, checkY, -1, -1)) {
                        exist = true;
                        if (((checkX == x1) && (checkY == y1) && (x2-x1==-2) && (y2-y1==-2))&&(Turn== 2|| IsKing[x1][y1])) {
                            return true;
                        }
                    }
                }
                checkY++;
            }
            checkX++;
        }
        return !exist;
    }
    private static boolean ExistJumpMove(int PlayerPiece) {
        int checkX = 0;
        for (int[] row : Board) {
            int checkY = 0;
            for (int piece : row) {
                if (piece == PlayerPiece) {
                    if (IsJumpable(checkX, checkY, 1, 1)) {
                        return true;
                    }
                    if (IsJumpable(checkX, checkY, -1, 1)) {
                        return true;
                    }
                    if (IsJumpable(checkX, checkY, 1, -1)) {
                        return true;
                    }
                    if (IsJumpable(checkX, checkY, -1, -1)) {
                        return true;
                    }
                }
                checkY++;
            }
            checkX++;
        }
        return false;
    }

    private static boolean ExistOneMove(int PlayerPiece) {
        int checkX = 0;

        Man10Checkers.mcheckers.getLogger().info("ExistOneMove");
        for (int[] row : Board) {
            int checkY = 0;
            //Man10Checkers.mcheckers.getLogger().info("row:"+ Arrays.toString(row));
            for (int piece : row) {
                //Man10Checkers.mcheckers.getLogger().info("piece:"+piece);
                //Man10Checkers.mcheckers.getLogger().info("PlayerPiece:"+PlayerPiece);
                //Man10Checkers.mcheckers.getLogger().info("((checkX+checkY) %2 == 0)):"+((checkX+checkY) %2 == 0));
                //Man10Checkers.mcheckers.getLogger().info("check x,y="+checkX+","+checkY);
                if (piece == PlayerPiece && ((checkX+checkY) %2 == 0)) {
                    if (IsOneMovable(checkX, checkY, 1, 1)) {
                        //Man10Checkers.mcheckers.getLogger().info("1 1");
                        return true;
                    }
                    if (IsOneMovable(checkX, checkY, -1, 1)) {
                        //Man10Checkers.mcheckers.getLogger().info("-1 1");
                        return true;
                    }
                    if (IsOneMovable(checkX, checkY, 1, -1)) {
                        //Man10Checkers.mcheckers.getLogger().info("1 -1");
                        return true;
                    }
                    if (IsOneMovable(checkX, checkY, -1, -1)) {
                        //Man10Checkers.mcheckers.getLogger().info("-1 -1");
                        return true;
                    }
                }
                checkY++;
            }
            checkX++;
        }
        return false;
    }
    private static boolean IsOneMovable(int x,int y,int xdirection,int ydirection){
        try{
            Man10Checkers.mcheckers.getLogger().info(String.valueOf(Board[x + xdirection][y + ydirection]));
            return Board[x + xdirection][y + ydirection] == 0;
        }catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            return false;
        }
    }
    //駒をおけるか
    public static void BoardInput(String Boardname,int x1, int y1, int x2, int y2) {
        Man10Checkers.mcheckers.getLogger().info("int x1:"+x1);
        Man10Checkers.mcheckers.getLogger().info("int y1:"+y1);
        Man10Checkers.mcheckers.getLogger().info("int x2:"+x2);
        Man10Checkers.mcheckers.getLogger().info("int y2:"+y2);
        ErrorType =0;
        LoadData(Boardname);
        PlayerPiece = yml.getInt(Boardname +".Turn");
        Man10Checkers.mcheckers.getLogger().info("BoardInput:1");
        //チェッカーで使用しないマスを選択
        if ((((x2 % 2) + (y2 % 2) % 2) == 1)) {
            ErrorType =1;
            Man10Checkers.mcheckers.getLogger().info("BoardInput:2");
            return;
        }

        int selectpiece = Board[x1][y1];

        //破壊可能な手があるのにも関わらずその手を選択していない場合を除外
        if (!SelectCorrectMove(x1, y1, x2, y2)) {
            //相手の駒を飛び越えられる手が存在します。飛び越えられる手を選択してください。
            ErrorType =2;
            Man10Checkers.mcheckers.getLogger().info("BoardInput:3");
            return;
        }

        //
        if (abs(x1 - x2) == 1 && abs(y1 - y2) == 1) {
            Man10Checkers.mcheckers.getLogger().info("BoardInput:4");
            if (Board[x1][y1] == PlayerPiece) {
                Board[x1][y1] = 0;
                IsKing[x2][y2] = IsKing[x1][y1];
                Board[x2][y2] = PlayerPiece;
                IsKing[x1][y1] = false;
                Man10Checkers.mcheckers.getLogger().info("BoardInput:5");
            }
        } else if (abs(x1 - x2) == 2 && abs(y1 - y2) == 2) {
            Man10Checkers.mcheckers.getLogger().info("BoardInput:6");
            if (IsJumpable(x1, y1, (x2 - x1)/2, (y2 - y1)/2)) {
                Board[x1][y1] = 0;
                IsKing[x2][y2] = IsKing[x1][y1];
                Board[x2][y2] = PlayerPiece;
                IsKing[x1][y1] = false;

                Board[x1+(x2 - x1)/2][y1+(y2 - y1)/2] = 0;
                IsKing[x1+(x2 - x1)/2][y1+(y2 - y1)/2] = false;

                saveData(Boardname);
                logg2(Board);
                Man10Checkers.mcheckers.getLogger().info("BoardInput:7");
                ContinuousMove(x2, y2);
            }
        } else {
            //最初に選択した駒の一つ斜めの駒か、相手の駒を飛び越えられる場合は二つ斜め前の駒を選択してください。
            ErrorType =3;
            Man10Checkers.mcheckers.getLogger().info("BoardInput:8");
            return;
        }

        saveData(Boardname);
        if(PlayerPiece ==1) {
            PlayerPiece =2;
            Man10Checkers.mcheckers.getLogger().info("BoardInput:9");
        }else {
            PlayerPiece =1;
            Man10Checkers.mcheckers.getLogger().info("BoardInput:10");
        }
        CreateKing();
    }
    private static void CreateKing() {
        if(Board[0][0]==2) IsKing[0][0] = true;
        if(Board[2][0]==2) IsKing[2][0] = true;
        if(Board[4][0]==2) IsKing[4][0] = true;
        if(Board[0][8]==1) IsKing[0][8] = true;
        if(Board[2][8]==1) IsKing[2][8] = true;
        if(Board[4][8]==1) IsKing[4][8] = true;
    }

    public static int WinCheck(String Boardname) {
        LoadData(Boardname);
        boolean ExistBlackMen = false;
        for (int i = 0; i < Board.length; i++) {
            for (int j = 0; j < Board[i].length; j++) {
                if (Board[i][j] == 1) {
                    ExistBlackMen = true;
                    break; // 内側のループを抜ける
                }
            }
            if (ExistBlackMen) {
                break; // 外側のループも抜ける
            }
        }
        if (!ExistBlackMen) return 2;

        boolean ExistWhiteMen = false;
        for (int i = 0; i < Board.length; i++) {
            for (int j = 0; j < Board[i].length; j++) {
                if (Board[i][j] == 2) {
                    ExistWhiteMen = true;
                    break; // 内側のループを抜ける
                }
            }
            if (ExistWhiteMen) {
                break; // 外側のループも抜ける
            }
        }
        if (!ExistWhiteMen) return 1;

        if(!ExistJumpMove(1) && !ExistOneMove(1)) return 2;
        if(!ExistJumpMove(2) && !ExistOneMove(2)) return 1;
        //jumpmoveだけでなく通常移動ができるか否かもゲーム終了かの判断に加える
        return 0;
    }
}