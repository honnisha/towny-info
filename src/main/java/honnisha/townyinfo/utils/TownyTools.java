package honnisha.townyinfo.utils;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.metadata.IntegerDataField;
import honnisha.townyinfo.Townyinfo;
import org.bukkit.entity.Player;

public class TownyTools {

    public static int getNationMainPoints(Nation nation) {
        IntegerDataField pointsField = (IntegerDataField) nation.getMetadata(Townyinfo.mainNationPointsField.getKey());
        if (pointsField == null || pointsField.getValue() == null) return 0;
        return pointsField.getValue();
    }

    public static int getNationMainPoints(Player player) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        if (resident != null && resident.hasTown() && resident.hasNation()) {
            try {
                return getNationMainPoints(resident.getTown().getNation());
            } catch (NotRegisteredException ignore) { }
        }
        return 0;
    }

    public static void changeNationMainPoints(Nation nation, int points) {
        int currentPoints = getNationMainPoints(nation);

        IntegerDataField mainNationPoints = new IntegerDataField(Townyinfo.mainNationPointsField.getKey(), currentPoints + points);
        nation.addMetaData(mainNationPoints);
    }

    public static void changeNationMainPoints(Player player, int points) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        if (resident != null && resident.hasTown() && resident.hasNation()) {
            try {
                changeNationMainPoints(resident.getTown().getNation(), points);
            } catch (NotRegisteredException ignore) { }
        }
    }

    public static void sendMessageToNation(Nation nation, String message) {
        for (Resident resident: nation.getResidents()) {
            Player player = resident.getPlayer();
            if (player != null) player.sendMessage(message);
        }
    }
}
