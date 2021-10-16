package honnisha.townyinfo;

import com.palmergames.bukkit.towny.object.Nation;
import honnisha.townyinfo.utils.TownyConditions;
import honnisha.townyinfo.utils.TownyTools;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Placeholders extends PlaceholderExpansion {

    private Townyinfo plugin;

    public Placeholders(Townyinfo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
        return "ti";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if(player == null) return "";

        // %ti_nationismain%
        if(identifier.equals("nationismain")) {
            if (TownyConditions.isPlayerInNationMain(player)) return plugin.getMainConfig().getMessagesMainNationTitle();
            return "";
        }

        // %ti_nationmainpoints%
        else if(identifier.equals("nationmainpoints")) {
            return Integer.toString(TownyTools.getNationMainPoints(player));
        }

        // %ti_ismainking%
        else if(identifier.equals("ismainking")) {
            if (TownyConditions.isPlayerInNationMain(player)) {
                Optional<Nation> optionalNation = TownyConditions.getPlayerNation(player);
                if (optionalNation.isPresent() && optionalNation.get().getKing().getName().equals(player.getName()))
                    return plugin.getMainConfig().getMainKingPlaceholder();
            }
            return "";
        }

        return null;
    }
}