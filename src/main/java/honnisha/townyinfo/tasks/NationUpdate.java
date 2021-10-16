package honnisha.townyinfo.tasks;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import honnisha.townyinfo.Townyinfo;
import honnisha.townyinfo.signs.NationSignsUpdater;
import honnisha.townyinfo.utils.DiscordSender;
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

    @Override
    public void runTask() {
        FileConfiguration config = Townyinfo.getInstance().getConfig();

        List<Nation> nations = TownyAPI.getInstance().getDataSource().getNations();
        NationSignsUpdater.sortNations(nations);
        Optional<Nation> optionalNation = NationSignsUpdater.getNation(0, nations);

        DiscordSender discordSender = new DiscordSender();

        if (optionalNation.isPresent()) {
            Nation nation = optionalNation.get();

            if (!TownyConditions.isNationMain(nation)) {
                Bukkit.broadcastMessage(Objects.requireNonNull(config.getString("messages.nation-become-main")).replace("%nation%", nation.getName()).replace("&", "§"));
                discordSender.send(Objects.requireNonNull(config.getString("messages.discord-nation-become-main")).replace("%nation%", nation.getName()));

                BooleanDataField MainNation = new BooleanDataField(Townyinfo.isMainNationField.getKey(), true);
                nation.addMetaData(MainNation);
            } else {
                Bukkit.broadcastMessage(Objects.requireNonNull(config.getString("messages.nation-stays-main")).replace("%nation%", optionalNation.get().getName()).replace("&", "§"));
                discordSender.send(Objects.requireNonNull(config.getString("messages.discord-nation-stays-main")).replace("%nation%", optionalNation.get().getName()));
            }

            if (this.reward) {
                int rewardMoney = config.getInt("nation-rewards.money");
                if (rewardMoney > 0) {
                    nation.getAccount().deposit(rewardMoney, Objects.requireNonNull(config.getString("messages.reward-money")).replace("&", "§"));
                    TownyTools.sendMessageToNation(nation, Objects.requireNonNull(config.getString("messages.nation-reward-money")).replace("%money%", Integer.toString(rewardMoney)).replace("&", "§"));
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
