package honnisha.townyinfo;

import honnisha.townyinfo.utils.TownyConditions;
import honnisha.townyinfo.utils.TownyTools;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
            if (TownyConditions.isPlayerInNationMain(player)) return plugin.config.getString("messages.main-nation-title");
            return "";
        }
        // %ti_nationmainpoints%
        if(identifier.equals("nationmainpoints")) {
            return Integer.toString(TownyTools.getNationMainPoints(player));
        }

        return null;
    }
}