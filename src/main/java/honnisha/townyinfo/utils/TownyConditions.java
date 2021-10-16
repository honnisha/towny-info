package honnisha.townyinfo.utils;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import honnisha.townyinfo.Townyinfo;
import org.bukkit.entity.Player;

import java.util.Optional;

public class TownyConditions {

    static public boolean isPlayerTownAdmin(Player player) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        if (resident != null && resident.hasTown()) {
            try {
                if (resident.getTown().getMayor().getName().equals(player.getName()) || resident.getTown().getRank("assistant").contains(resident))
                    return true;
            } catch (NotRegisteredException ignore) { }
        }
        return false;
    }

    static public Optional<Nation> getPlayerNation(Player player) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        if (resident != null && resident.hasTown() && resident.hasNation()) {
            try {
                return Optional.of(resident.getTown().getNation());
            } catch (NotRegisteredException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    static public boolean isNationMain(Nation nation) {
        BooleanDataField mainStatus = (BooleanDataField) nation.getMetadata(Townyinfo.isMainNationField.getKey());
        if (mainStatus == null || mainStatus.getValue() == null) return false;
        return mainStatus.getValue();
    }

    static public boolean isPlayerInNationMain(Player player) {
        Optional<Nation> optionalNation = getPlayerNation(player);
        return optionalNation.isPresent() && TownyConditions.isNationMain(optionalNation.get());
    }

    static public boolean isPlayerNationAdmin(Player player) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        if (resident != null && resident.hasTown() && resident.hasNation()) {
            try {
                if (resident.getTown().getNation().getKing().getName().equals(player.getName()) || resident.getTown().getNation().getAssistants().contains(resident))
                    return true;
            } catch (NotRegisteredException ignore) { }
        }
        return false;
    }
}
