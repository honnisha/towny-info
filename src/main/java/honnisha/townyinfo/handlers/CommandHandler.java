package honnisha.townyinfo.handlers;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import honnisha.townyinfo.Townyinfo;
import honnisha.townyinfo.tasks.NationUpdate;
import honnisha.townyinfo.utils.DiscordSender;
import honnisha.townyinfo.utils.TownyConditions;
import honnisha.townyinfo.utils.TownyTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;

import static honnisha.townyinfo.signs.NationSignsUpdater.UpdateNationSigns;
import static honnisha.townyinfo.signs.TownSignsUpdater.UpdateTownSigns;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = Townyinfo.getInstance().getConfig();

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player && !(sender.hasPermission("townyinfo.reload") || sender.isOp())) {
                sender.sendMessage(Objects.requireNonNull(config.getString("messages.permission-error")).replace("&", "§"));
                return true;
            }
            Townyinfo.getInstance().reloadConfig();
            try {
                UpdateTownSigns();
                UpdateNationSigns();
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }
            Townyinfo.getInstance().loadTasks();
            sender.sendMessage(Objects.requireNonNull(config.getString("messages.plugin-reloaded")).replace("&", "§"));

        } else if (args.length == 1 && args[0].equalsIgnoreCase("update")) {
            if (sender instanceof Player && !(sender.hasPermission("townyinfo.update") || sender.isOp())) {
                sender.sendMessage(Objects.requireNonNull(config.getString("messages.permission-error")).replace("&", "§"));
                return true;
            }
            (new NationUpdate(false)).runTask();
            sender.sendMessage(Objects.requireNonNull(config.getString("messages.towns-updated")).replace("&", "§"));

        } else if (args[0].equalsIgnoreCase("givepoints")) {
            if (sender instanceof Player && !(sender.hasPermission("townyinfo.givepoints") || sender.isOp())) {
                sender.sendMessage(Objects.requireNonNull(config.getString("messages.permission-error")).replace("&", "§"));
                return true;
            }
            Optional<Nation> nationOptional = Optional.empty();
            try {
                nationOptional = Optional.ofNullable(TownyAPI.getInstance().getDataSource().getNation(args[1]));
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }
            if (!nationOptional.isPresent()) {
                sender.sendMessage(Objects.requireNonNull(Objects.requireNonNull(config.getString("messages.nation-not-found-error")).replace("%nation%", args[1]).replace("&", "§")));
                return true;
            }

            int points = Integer.parseInt(args[2]);
            TownyTools.changeNationMainPoints(nationOptional.get(), points);
            String message = Objects.requireNonNull(config.getString("messages.nation-give-reward-points")).replace("%admin%", sender.getName()).replace("%nation%", nationOptional.get().getName()).replace("%points%", Integer.toString(points)).replace("&", "§");
            TownyTools.sendMessageToNation(nationOptional.get(), message);
            sender.sendMessage(message);

        } else if (args[0].equalsIgnoreCase("info")) {
            if (sender instanceof Player && !(sender.hasPermission("townyinfo.status") || sender.isOp())) {
                sender.sendMessage(Objects.requireNonNull(config.getString("messages.permission-error")).replace("&", "§"));
                return true;
            }
            Optional<Nation> nationOptional = Optional.empty();
            try {
                nationOptional = Optional.ofNullable(TownyAPI.getInstance().getDataSource().getNation(args[1]));
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }
            if (!nationOptional.isPresent()) {
                sender.sendMessage(Objects.requireNonNull(Objects.requireNonNull(config.getString("messages.nation-not-found-error")).replace("%nation%", args[1]).replace("&", "§")));
                return true;
            }
            Nation nation = nationOptional.get();
            String isMain = TownyConditions.isNationMain(nation) ? Objects.requireNonNull(config.getString("yes-string")) : Objects.requireNonNull(config.getString("no-string"));

            sender.sendMessage(Objects.requireNonNull(config.getString("messages.nation-info-title")).replace("%nation-name%", nation.getName()).replace("&", "§"));
            sender.sendMessage(Objects.requireNonNull(config.getString("messages.nation-info-1")).replace("%points%", Integer.toString(TownyTools.getNationMainPoints(nation))).replace("&", "§"));
            sender.sendMessage(Objects.requireNonNull(config.getString("messages.nation-info-2")).replace("%is-main%", isMain).replace("&", "§"));
            sender.sendMessage(Objects.requireNonNull(config.getString("messages.nation-info-3")).replace("%balance%", Double.toString(nation.getAccount().getCachedBalance())).replace("&", "§"));

        } else if (args[0].equalsIgnoreCase("announce")) {
            if (sender instanceof Player && !(sender.hasPermission("townyinfo.announce") || sender.isOp())) {
                sender.sendMessage(Objects.requireNonNull(config.getString("messages.permission-error")).replace("&", "§"));
                return true;
            }
            DiscordSender discordSender = new DiscordSender();
            boolean sended = discordSender.send(String.format("%s: %s", sender.getName(), args[1]));
            if (sended) {
                sender.sendMessage(Objects.requireNonNull(config.getString("messages.message-sended")).replace("%message%", args[1]).replace("&", "§"));
            } else {
                String channelName = Objects.requireNonNull(config.getString("notifications-discord-channel"));
                sender.sendMessage(Objects.requireNonNull(config.getString("messages.message-not-sended")).replace("%channel%", channelName).replace("&", "§"));
            }
        }
        return true;
    }
}