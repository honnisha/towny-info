package honnisha.townyinfo.handlers;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import honnisha.townyinfo.signs.NationSignsUpdater;
import honnisha.townyinfo.signs.TownSignsUpdater;
import honnisha.townyinfo.Townyinfo;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ClickHandler implements Listener {

    @EventHandler
    public void onBlockClick(PlayerInteractEvent event) {
        FileConfiguration config = Townyinfo.getInstance().getConfig();
        if (config.getBoolean("debug"))
            Townyinfo.logger.info(String.format("DEBUG onBlockClick: User %s clicked on block:%s action:%s", event.getPlayer().getName(), event.getClickedBlock(), event.getAction()));

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null && block.getState() instanceof Sign) {

                EventClickTownSign(block, event.getPlayer());
                EventClickNationSign(block, event.getPlayer());
            }
        }
    }

    private void EventClickTownSign(Block block, Player player) {
        FileConfiguration config = Townyinfo.getInstance().getConfig();

        Optional<Integer> index = Optional.empty();
        for (String signInfo : config.getStringList("town-signs")) {
            ;
            String[] signArgs = signInfo.split(" ");
            String worldName = signArgs[0];
            int number = Integer.parseInt(signArgs[1]);
            int signX = Integer.parseInt(signArgs[2]);
            int signZ = Integer.parseInt(signArgs[4]);

            if (block.getX() == signX && block.getZ() == signZ && block.getWorld().getName().equals(worldName))
                index = Optional.of(number - 1);
        }
        if (!index.isPresent()) {
            if (config.getBoolean("debug"))
                Townyinfo.logger.info(String.format("DEBUG onBlockClick: town sign not found in x:%s z:%s", block.getX(), block.getZ()));
            return;
        }
        List<Town> towns = TownyAPI.getInstance().getDataSource().getTowns();
        TownSignsUpdater.sortTowns(towns, config);
        Optional<Town> town = TownSignsUpdater.getTown(index.get(), towns);

        if (!town.isPresent()) {
            if (config.getBoolean("debug"))
                Townyinfo.logger.info(String.format("DEBUG onBlockClick: town not found with index %s", index.get()));
            return;
        }
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        if (resident == null) return;

        if (town.get().isOpen() || town.get().getResidents().contains(resident)) {
            Location spawnLocation = null;
            try {
                spawnLocation = town.get().getSpawn();
            } catch (TownyException e) {
                player.sendMessage(Objects.requireNonNull(config.getString("messages.town-no-spawn-point")).replace("&", "§"));
                return;
            }
            player.teleport(spawnLocation);
            player.sendMessage(Objects.requireNonNull(config.getString("messages.tp-to-town")).replace("%town%", town.get().getName()).replace("&", "§"));
        } else {
            player.sendMessage(Objects.requireNonNull(config.getString("messages.town-doesnt-open-or-not-resident")).replace("&", "§"));
        }
    }

    private void EventClickNationSign(Block block, Player player) {
        FileConfiguration config = Townyinfo.getInstance().getConfig();

        Optional<Integer> index = Optional.empty();
        for (String signInfo : config.getStringList("nation-signs")) {
            ;
            String[] signArgs = signInfo.split(" ");
            String worldName = signArgs[0];
            int number = Integer.parseInt(signArgs[1]);
            int signX = Integer.parseInt(signArgs[2]);
            int signZ = Integer.parseInt(signArgs[4]);

            if (block.getX() == signX && block.getZ() == signZ && block.getWorld().getName().equals(worldName))
                index = Optional.of(number - 1);
        }
        if (!index.isPresent()) {
            if (config.getBoolean("debug"))
                Townyinfo.logger.info(String.format("DEBUG onBlockClick: nation sign not found in x:%s z:%s", block.getX(), block.getZ()));
            return;
        }
        List<Nation> nations = TownyAPI.getInstance().getDataSource().getNations();
        NationSignsUpdater.sortNations(nations, config);
        Optional<Nation> optionalNation = NationSignsUpdater.getNation(index.get(), nations);

        if (!optionalNation.isPresent()) {
            if (config.getBoolean("debug"))
                Townyinfo.logger.info(String.format("DEBUG onBlockClick: town not found with index %s", index.get()));
            return;
        }
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        if (resident == null) return;

        boolean isAlly;
        try {
            isAlly = resident.hasTown() && optionalNation.get().getAllies().contains(resident.getTown().getNation());
        } catch (NotRegisteredException e) {
            return;
        }

        if (optionalNation.get().isOpen() || optionalNation.get().getResidents().contains(resident) || isAlly) {
            Location spawnLocation = null;
            try {
                spawnLocation = optionalNation.get().getSpawn();
            } catch (TownyException e) {
                player.sendMessage(Objects.requireNonNull(config.getString("messages.nation-no-spawn-point")).replace("&", "§"));
                return;
            }
            player.teleport(spawnLocation);
            player.sendMessage(Objects.requireNonNull(config.getString("messages.tp-to-nation")).replace("%nation%", optionalNation.get().getName()).replace("&", "§"));
        } else {
            player.sendMessage(Objects.requireNonNull(config.getString("messages.nation-doesnt-open-or-not-resident")).replace("&", "§"));
        }
    }
}
