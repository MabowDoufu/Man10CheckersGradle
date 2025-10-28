package mabowdoufu.man10checkersgradle;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameData {
    public UUID p1;
    public UUID p2;
    public int[][] traps;
    public int[][] mines;
    public Map<Data.Ability, Integer> p1AbilityPoint = Map.ofEntries(
            Map.entry(Data.Ability.Trap,0),
            Map.entry(Data.Ability.CreateKing,0),
            Map.entry(Data.Ability.ForceMove,0),
            Map.entry(Data.Ability.Mine,0),
            Map.entry(Data.Ability.CreateMen,0)
    );
    public Map<Data.Ability, Integer> p2AbilityPoint = Map.ofEntries(
            Map.entry(Data.Ability.Trap,0),
            Map.entry(Data.Ability.CreateKing,0),
            Map.entry(Data.Ability.ForceMove,0),
            Map.entry(Data.Ability.Mine,0),
            Map.entry(Data.Ability.CreateMen,0)
    );
}


