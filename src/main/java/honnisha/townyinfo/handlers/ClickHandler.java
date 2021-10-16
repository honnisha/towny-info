package honnisha.townyinfo.handlers;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import honnisha.townyinfo.MainConfigManager;
import honnisha.townyinfo.Townyinfo;
import honnisha.townyinfo.signs.NationSignsUpdater;
import honnisha.townyinfo.signs.TownSignsUpdater;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.Optional;

public class ClickHandler implements Listener {

    @EventHandler
    public void onBlockClick(PlayerInteractEvent event) {
        MainConfigManager mainConfig = Townyinfo.getInstance().getMainConfig();

        if (mainConfig.isDebug())
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
        MainConfigManager mainConfig = Townyinfo.getInstance().getMainConfig();

        Optional<Integer> index = Optional.empty();
        for (String signInfo : mainConfig.getTownSigns()) {
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
            if (mainConfig.isDebug())
                Townyinfo.logger.info(String.format("DEBUG onBlockClick: town sign not found in x:%s z:%s", block.getX(), block.getZ()));
            return;
        }
        List<Town> towns = TownyAPI.getInstance().getDataSource().getTowns();
        TownSignsUpdater.sortTowns(towns);
        Optional<Town> town = TownSignsUpdater.getTown(index.get(), towns);

        if (!town.isPresent()) {
            if (mainConfig.isDebug())
                Townyinfo.logger.info(String.format("DEBUG onBlockClick: town not found with index %s", index.get()));
            return;
        }
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        if (resident == null) return;

        String command = mainConfig.getSignTownClickCommand().replace("%town%", town.get().getName());
        player.performCommand(command);
    }

    private void EventClickNationSign(Block block, Player player) {
        MainConfigManager mainConfig = Townyinfo.getInstance().getMainConfig();

        Optional<Integer> index = Optional.empty();
        for (String signInfo : mainConfig.getNationSigns()) {
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
            if (mainConfig.isDebug())
                Townyinfo.logger.info(String.format("DEBUG onBlockClick: nation sign not found in x:%s z:%s", block.getX(), block.getZ()));
            return;
        }
        List<Nation> nations = TownyAPI.getInstance().getDataSource().getNations();
        NationSignsUpdater.sortNations(nations);
        Optional<Nation> optionalNation = NationSignsUpdater.getNation(index.get(), nations);

        if (!optionalNation.isPresent()) {
            if (mainConfig.isDebug())
                Townyinfo.logger.info(String.format("DEBUG onBlockClick: town not found with index %s", index.get()));
            return;
        }
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        if (resident == null) return;

        String command = mainConfig.getSignNationClickCommand().replace("%nation%", optionalNation.get().getName());
        player.performCommand(command);
    }
}
