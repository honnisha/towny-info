package honnisha.townyinfo.tasks;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import honnisha.townyinfo.Townyinfo;
import honnisha.townyinfo.signs.NationSignsUpdater;
import honnisha.townyinfo.utils.ITask;
import honnisha.townyinfo.utils.TownyConditions;
import honnisha.townyinfo.utils.TownyTools;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class NationUpdate implements ITask {

    boolean reward;

    public NationUpdate(boolean reward) {
        this.reward = reward;
    }

    public Guild getGuild() {
        return DiscordSRV.getPlugin().getJda().getGuilds().stream().findFirst().orElse(null);
    }

    @Override
    public void runTask() {
        FileConfiguration config = Townyinfo.getInstance().getConfig();

        List<Nation> nations = TownyAPI.getInstance().getDataSource().getNations();
        NationSignsUpdater.sortNations(nations, config);
        Optional<Nation> optionalNation = NationSignsUpdater.getNation(0, nations);

        Optional<TextChannel> LogChannelOptional = Optional.empty();
        String channelId = config.getString("notifications-discord-channel");
        if (channelId != null)
            LogChannelOptional = Optional.ofNullable(getGuild().getTextChannelById(channelId));

        if (optionalNation.isPresent()) {
            Nation nation = optionalNation.get();

            if (!TownyConditions.isNationMain(nation)) {
                String message = Objects.requireNonNull(config.getString("messages.nation-become-main")).replace("%nation%", nation.getName()).replace("&", "§");
                if (LogChannelOptional.isPresent()) LogChannelOptional.get().sendMessage(message);
                Bukkit.broadcastMessage(message);

                BooleanDataField MainNation = new BooleanDataField(Townyinfo.isMainNationField.getKey(), true);
                nation.addMetaData(MainNation);
            } else {
                Bukkit.broadcastMessage(Objects.requireNonNull(config.getString("messages.nation-stays-main")).replace("%nation%", optionalNation.get().getName()).replace("&", "§"));
            }

            if (this.reward) {
                int rewardMoney = config.getInt("nation-rewards.money");
                if (rewardMoney > 0) {
                    try {
                        nation.depositToBank(nation.getKing(), rewardMoney);
                        TownyTools.sendMessageToNation(nation, Objects.requireNonNull(config.getString("messages.nation-reward-money")).replace("%money%", Integer.toString(rewardMoney)).replace("&", "§"));
                    } catch (EconomyException | TownyException e) {
                        e.printStackTrace();
                    }
                }
                int rewardPoints = config.getInt("nation-rewards.main-points");
                if (rewardPoints > 0) {
                    TownyTools.changeNationMainPoints(nation, rewardPoints);
                    TownyTools.sendMessageToNation(nation, Objects.requireNonNull(config.getString("messages.nation-reward-points")).replace("%points%", Integer.toString(rewardPoints)).replace("&", "§"));
                }
            }

            // Update other nations
            for (Nation otherNation: nations) {
                if (!otherNation.getName().equals(nation.getName())){
                    if (TownyConditions.isNationMain(otherNation)) {
                        BooleanDataField notMainNation = new BooleanDataField(Townyinfo.isMainNationField.getKey(), false);
                        otherNation.addMetaData(notMainNation);

                        TownyTools.sendMessageToNation(otherNation, Objects.requireNonNull(config.getString("messages.nation-lose-main")).replace("%nation%", otherNation.getName()).replace("&", "§"));
                    }
                }
            }
        }
    }
}
