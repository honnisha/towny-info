package honnisha.townyinfo.signs;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import honnisha.townyinfo.MainConfigManager;
import honnisha.townyinfo.Townyinfo;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.*;

public class TownSignsUpdater {

    public static void sortTowns(List<Town> towns) {
        MainConfigManager mainConfig = Townyinfo.getInstance().getMainConfig();

        String sortMethod = mainConfig.getTownSortMethod();
        if (sortMethod != null && sortMethod.equals("residents"))
            towns.sort(Comparator.comparingInt(Town::getNumResidents));
        Collections.reverse(towns);
    }

    public static Optional<Town> getTown(int index, List<Town> towns) {
        if (towns.size() < index + 1) return Optional.empty();
        return Optional.of(towns.get(index));
    }

    public static void UpdateTownSigns() throws NotRegisteredException {
        MainConfigManager mainConfig = Townyinfo.getInstance().getMainConfig();

        List<Town> towns = TownyAPI.getInstance().getDataSource().getTowns();
        sortTowns(towns);

        if (mainConfig.isDebug())
            Townyinfo.getInstance().getLogger().info(String.format("DEBUG UpdateTownSigns: started (towns count: %s)", towns.size()));

        for (String signInfo : mainConfig.getTownSigns()) {
            String[] signArgs = signInfo.split(" ");
            String worldName = signArgs[0];
            int number = Integer.parseInt(signArgs[1]);
            int signX = Integer.parseInt(signArgs[2]);
            int signY = Integer.parseInt(signArgs[3]);
            int signZ = Integer.parseInt(signArgs[4]);
            World world = Bukkit.getServer().getWorld(worldName);

            if (world == null) {
                Townyinfo.getInstance().getLogger().warning(String.format("World \"%s\" not exists", worldName));
                continue;
            }

            Optional<Town> optionalTown = getTown(number - 1, towns);
            if (!optionalTown.isPresent()) {
                if (mainConfig.isDebug())
                    Townyinfo.getInstance().getLogger().info(String.format("DEBUG UpdateTownSigns: Town with number %s not fund", number));

                // Clear signs
                int sectionClearCount = 0;
                for (String sectionName : mainConfig.getTownSignLines()) {
                    Block locatedBlock = world.getBlockAt(signX, signY - sectionClearCount, signZ);
                    if ((locatedBlock.getState() instanceof Sign)) {
                        Sign sign = (Sign) locatedBlock.getState();
                        for (int i = 0; i <= 3; i++) {
                            sign.setLine(i, "");
                        }
                        sign.update();
                    }
                    sectionClearCount += 1;
                }
                continue;
            }
            Town town = optionalTown.get();

            String taxPlot = String.format("%s%s", (int) town.getPlotTax(), town.isTaxPercentage() ? "%" : "");
            String tax = Integer.toString((int) town.getTaxes());
            String isOpen = town.isOpen() ? mainConfig.getOpenName() : mainConfig.getCloseName();
            String nationName = town.hasNation() ? town.getNation().getName() : mainConfig.getNoString();
            Resident townMayor = town.getMayor();
            String mayor = townMayor != null ? town.getMayor().getName() : "-";
            List<Resident> assistants = town.getRank("assistant");
            String assistant1 = assistants.size() < 1 ? "" : assistants.get(0).getName();
            String assistant2 = assistants.size() < 2 ? "" : assistants.get(1).getName();
            String assistant3 = assistants.size() < 3 ? "" : assistants.get(2).getName();
            String isNeutral = mainConfig.getYesOrNo(town.isNeutral());
            String isPVP = mainConfig.getYesOrNo(town.isPVP());

            int sectionCount = 0;
            for (String sectionName : mainConfig.getTownSignLines()) {

                Block locatedBlock = world.getBlockAt(signX, signY - sectionCount, signZ);
                if (!(locatedBlock.getState() instanceof Sign)) {
                    if (mainConfig.isDebug())
                        Townyinfo.getInstance().getLogger().info(String.format("DEBUG UpdateTownSigns: Block in %s %s %s is not a sign", signX, signY - sectionCount, signZ));
                } else {
                    Sign sign = (Sign) locatedBlock.getState();

                    for (int i = 0; i <= 3; i++) {
                        String townSignLine = mainConfig.getTownSignLine(sectionName, i + 1);
                        if (townSignLine != null) {
                            String line = townSignLine.replace(
                                    "%name%", town.getName()
                            ).replace(
                                    "%num-residents%", Integer.toString(town.getNumResidents())
                            ).replace(
                                    "%is-open%", isOpen
                            ).replace(
                                    "%mayor%", town.getMayor().getName()
                            ).replace(
                                    "%tax-plot%", taxPlot
                            ).replace(
                                    "%tax%", tax
                            ).replace(
                                    "%prefix%", town.getPrefix()
                            ).replace(
                                    "%nation%", Objects.requireNonNull(nationName)
                            ).replace(
                                    "%mayor%", mayor
                            ).replace(
                                    "%is-pvp%", isPVP
                            ).replace(
                                    "%assistant1%", assistant1
                            ).replace(
                                    "%assistant2%", assistant2
                            ).replace(
                                    "%assistant3%", assistant3
                            ).replace(
                                    "%is-neutral%", isNeutral
                            ).replace("&", "§");
                            sign.setLine(i, line);
                        }
                    }
                    sign.update();
                }
                sectionCount += 1;
            }
            if (mainConfig.isDebug())
                Townyinfo.getInstance().getLogger().info(String.format("DEBUG UpdateTownSigns: Town sign \"%s\" updated for town %s", signInfo, town.getName()));
        }
    }
}