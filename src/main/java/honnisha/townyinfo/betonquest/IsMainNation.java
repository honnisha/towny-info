package honnisha.townyinfo.betonquest;

import honnisha.townyinfo.utils.TownyConditions;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

public class IsMainNation extends Condition {

    public IsMainNation(Instruction instruction) {
        super(instruction, false);
    }

    @Override
    protected Boolean execute(String playerID) throws QuestRuntimeException {
        Player player = PlayerConverter.getPlayer(playerID);
        return TownyConditions.isPlayerInNationMain(player);
    }
}
