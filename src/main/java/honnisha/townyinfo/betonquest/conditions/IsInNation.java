package honnisha.townyinfo.betonquest.conditions;

import com.palmergames.bukkit.towny.object.Nation;
import honnisha.townyinfo.utils.TownyConditions;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

import java.util.Optional;

public class IsInNation extends Condition {

    public IsInNation(Instruction instruction) {
        super(instruction, false);
    }

    @Override
    protected Boolean execute(String playerID) throws QuestRuntimeException {
        Player player = PlayerConverter.getPlayer(playerID);
        Optional<Nation> optionalNation = TownyConditions.getPlayerNation(player);
        return optionalNation.isPresent();
    }
}