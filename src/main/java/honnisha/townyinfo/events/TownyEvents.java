package honnisha.townyinfo.events;

import com.palmergames.bukkit.towny.event.*;
import com.palmergames.bukkit.towny.event.town.TownReclaimedEvent;
import com.palmergames.bukkit.towny.event.town.TownRuinedEvent;
import com.palmergames.bukkit.towny.event.town.toggle.TownToggleOpenEvent;
import honnisha.townyinfo.Townyinfo;
import honnisha.townyinfo.utils.DiscordSender;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class TownyEvents implements Listener {

    public void SendDefaultTownMessage(String settingPath, String townName, String mayorName) {
        FileConfiguration config = Townyinfo.getInstance().getConfig();

        String message = Objects.requireNonNull(config.getString(settingPath)).replace(
                "%town%", townName
        ).replace(
                "%mayor%", mayorName
        );
        DiscordSender discordSender = new DiscordSender();
        discordSender.send(message);
    }

    public void SendDefaultNationMessage(String settingPath, String nationName, String kingName, String enemy, String ally) {
        FileConfiguration config = Townyinfo.getInstance().getConfig();

        String message = Objects.requireNonNull(config.getString(settingPath)).replace(
                "%nation%", nationName
        ).replace(
                "%king%", kingName
        ).replace(
                "%enemy%", enemy
        ).replace(
                "%ally%", ally
        );
        DiscordSender discordSender = new DiscordSender();
        discordSender.send(message);
    }

    public void SendDefaultNationMessage(String settingPath, String nationName, String kingName) {
        this.SendDefaultNationMessage(settingPath, nationName, kingName, "", "");
    }

    @EventHandler
    public void NewTown(NewTownEvent event) {
        this.SendDefaultTownMessage("messages.new-town", event.getTown().getName(), event.getTown().getMayor().getName());
    }

    @EventHandler
    public void TownRuined(TownRuinedEvent event) {
        this.SendDefaultTownMessage("messages.town-ruined", event.getTown().getName(), event.getTown().getMayor().getName());
    }

    @EventHandler
    public void TownReclaimed(TownReclaimedEvent event) {
        this.SendDefaultTownMessage("messages.town-reclaimed", event.getTown().getName(), event.getTown().getMayor().getName());
    }

    @EventHandler
    public void TownDeleted(DeleteTownEvent event) {
        Player player = Bukkit.getPlayer(event.getMayorUUID());
        this.SendDefaultTownMessage("messages.town-deleted", event.getTownName(), player != null ? player.getName() : "");
    }

    @EventHandler
    public void TownToggleOpen(TownToggleOpenEvent event) {
        if (event.getFutureState()) {
            this.SendDefaultTownMessage("messages.town-toggle-open", event.getTown().getName(), event.getTown().getMayor().getName());
        } else {
            this.SendDefaultTownMessage("messages.town-toggle-close", event.getTown().getName(), event.getTown().getMayor().getName());
        }
    }

    @EventHandler
    public void NewNation(NewNationEvent event) {
        this.SendDefaultNationMessage("messages.new-nation", event.getNation().getName(), event.getNation().getKing().getName());
    }

    @EventHandler
    public void NationAddEnemy(NationAddEnemyEvent event) {
        this.SendDefaultNationMessage(
                "messages.nation-new-enemy", event.getNation().getName(), event.getNation().getKing().getName(),
                event.getEnemy().getName(), ""
        );
    }
    @EventHandler
    public void NationRemoveEnemy(NationRemoveEnemyEvent event) {
        this.SendDefaultNationMessage(
                "messages.nation-remove-enemy", event.getNation().getName(), event.getNation().getKing().getName(),
                event.getEnemy().getName(), ""
        );
    }

    @EventHandler
    public void NationAcceptAlly(NationAcceptAllyRequestEvent event) {
        this.SendDefaultNationMessage(
                "messages.nation-accept-ally", event.getSenderNation().getName(), event.getSenderNation().getKing().getName(),
                "", event.getInvitedNation().getName()
        );
    }
    @EventHandler
    public void NationRemoveAlly(NationRemoveAllyEvent event) {
        this.SendDefaultNationMessage(
                "messages.nation-remove-ally", event.getNation().getName(), event.getNation().getKing().getName(),
                "", event.getRemovedNation().getName()
        );
    }
    @EventHandler
    public void NationDeleted(DeleteNationEvent event) {
        this.SendDefaultNationMessage(
                "messages.nation-deleted", event.getNationName(), "", "", ""
        );
    }
}
