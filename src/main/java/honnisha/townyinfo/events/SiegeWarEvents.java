package honnisha.townyinfo.events;

import com.gmail.goosius.siegewar.enums.SiegeStatus;
import com.gmail.goosius.siegewar.events.SiegeEndEvent;
import com.gmail.goosius.siegewar.events.SiegeWarStartEvent;
import honnisha.townyinfo.Townyinfo;
import honnisha.townyinfo.utils.DiscordSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class SiegeWarEvents implements Listener {
    @EventHandler
    public void WarStarted(SiegeWarStartEvent event) {
        FileConfiguration config = Townyinfo.getInstance().getConfig();

        String message = Objects.requireNonNull(config.getString("messages.siege-start-message")).replace(
                "%attacker%", event.getSiege().getAttacker().getName()
        ).replace(
                "%defender%", event.getSiege().getDefender().getName()
        ).replace(
                "%town%", event.getSiege().getTown().getName()
        );
        DiscordSender discordSender = new DiscordSender();
        discordSender.send(message);
    }

    @EventHandler
    public void WarEnded(SiegeEndEvent event) {
        FileConfiguration config = Townyinfo.getInstance().getConfig();

        String status;
        if (event.getSiege().getStatus() == SiegeStatus.ATTACKER_WIN) {
            status = Objects.requireNonNull(config.getString("messages.siege-end-message-attacker-win"));
        } else if (event.getSiege().getStatus() == SiegeStatus.DEFENDER_SURRENDER) {
            status = Objects.requireNonNull(config.getString("messages.siege-end-message-defender-surrender"));
        } else if (event.getSiege().getStatus() == SiegeStatus.ATTACKER_ABANDON) {
            status = Objects.requireNonNull(config.getString("messages.siege-end-message-attacker-abandon"));
        } else if (event.getSiege().getStatus() == SiegeStatus.DEFENDER_WIN) {
            status = Objects.requireNonNull(config.getString("messages.siege-end-message-defender-win"));
        } else {
            status = Objects.requireNonNull(config.getString("messages.siege-end"));
        }

        String message = status.replace(
                "%attacker%", event.getSiege().getAttacker().getName()
        ).replace(
                "%defender%", event.getSiege().getDefender().getName()
        ).replace(
                "%town%", event.getSiege().getTown().getName()
        );
        DiscordSender discordSender = new DiscordSender();
        discordSender.send(message);
    }
}
