package honnisha.townyinfo.betonquest.events;

import honnisha.townyinfo.Townyinfo;
import honnisha.townyinfo.utils.TownyTools;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

public class EventChangeNationMainPoints extends QuestEvent {

    private final int points;

    public EventChangeNationMainPoints(Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        points = Integer.parseInt(instruction.next());
    }

    @Override
    protected Void execute(String playerID) throws QuestRuntimeException {
        Player player = PlayerConverter.getPlayer(playerID);
        TownyTools.changeNationMainPoints(player, points);
        Townyinfo.logger.info(String.format("BetonQuest: user: %s changed nation points with: %s", player.getName(), points));
        return null;
    }
}
