package honnisha.townyinfo.betonquest.conditions;

import honnisha.townyinfo.utils.TownyTools;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

public class IsNationHasMainPoints extends Condition {

    public int points;

    public IsNationHasMainPoints(Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        points = Integer.parseInt(instruction.next());
    }

    @Override
    protected Boolean execute(String playerID) throws QuestRuntimeException {
        Player player = PlayerConverter.getPlayer(playerID);
        return this.points <= TownyTools.getNationMainPoints(player);
    }
}