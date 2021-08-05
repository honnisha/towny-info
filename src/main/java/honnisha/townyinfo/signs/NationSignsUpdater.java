package honnisha.townyinfo.signs;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import honnisha.townyinfo.Townyinfo;
import honnisha.townyinfo.utils.TownyConditions;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class NationSignsUpdater {

    public static void sortNations(List<Nation> nations, FileConfiguration config) {
        String sortMethod = config.getString("nation-sort-method");
        if (sortMethod != null && sortMethod.equals("residents"))
            nations.sort(Comparator.comparingInt(Nation::getNumResidents));
        else if (sortMethod != null && sortMethod.equals("towns"))
            nations.sort(Comparator.comparingInt(Nation::getNumResidents));

        Collections.reverse(nations);
    }

    public static Optional<Nation> getNation(int index, List<Nation> nations) {
        if (nations.size() < index + 1) return Optional.empty();
        return Optional.of(nations.get(index));
    }

    public static void UpdateNationSigns() throws NotRegisteredException {
        FileConfiguration config = Townyinfo.getInstance().getConfig();

        List<Nation> nations = TownyAPI.getInstance().getDataSource().getNations();
        sortNations(nations, config);

        if (config.getBoolean("debug"))
            Townyinfo.logger.info(String.format("DEBUG UpdateNationSigns: started (nations count: %s)", nations.size()));

        for (String signInfo : config.getStringList("nation-signs")) {
            String[] signArgs = signInfo.split(" ");
            String worldName = signArgs[0];
            int number = Integer.parseInt(signArgs[1]);
            int signX = Integer.parseInt(signArgs[2]);
            int signY = Integer.parseInt(signArgs[3]);
            int signZ = Integer.parseInt(signArgs[4]);
            World world = Bukkit.getServer().getWorld(worldName);

            if (world == null) {
                Townyinfo.logger.warning(String.format("World \"%s\" not exists", worldName));
                continue;
            }

            Optional<Nation> optionalNation = getNation(number - 1, nations);
            if (!optionalNation.isPresent()) {
                if (config.getBoolean("debug"))
                    Townyinfo.logger.info(String.format("DEBUG UpdateNationSigns: Town with number %s not fund", number));

                // Clear signs
                int sectionClearCount = 0;
                for (String sectionName : Objects.requireNonNull(config.getConfigurationSection("nation-sign-lines")).getKeys(false)) {
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
            Nation nation = optionalNation.get();

            String isOpen = nation.isOpen() ? Objects.requireNonNull(config.getString("open-name")) : Objects.requireNonNull(config.getString("close-name"));

            List<Resident> assistants = nation.getAssistants();
            String assistant1 = assistants.size() < 1 ? "" : assistants.get(0).getName();
            String assistant2 = assistants.size() < 2 ? "" : assistants.get(1).getName();
            String assistant3 = assistants.size() < 3 ? "" : assistants.get(2).getName();

            List<Nation> enemies = nation.getEnemies();
            String enemy1 = enemies.size() < 1 ? "" : enemies.get(0).getName();
            String enemy2 = enemies.size() < 2 ? "" : enemies.get(1).getName();
            String enemy3 = enemies.size() < 3 ? "" : enemies.get(2).getName();

            List<Nation> allies = nation.getAllies();
            String ally1 = allies.size() < 1 ? "" : allies.get(0).getName();
            String ally2 = allies.size() < 2 ? "" : allies.get(1).getName();
            String ally3 = allies.size() < 3 ? "" : allies.get(2).getName();

            String isMain = TownyConditions.isNationMain(nation) ? Objects.requireNonNull(config.getString("yes-string")) : Objects.requireNonNull(config.getString("no-string"));

            int sectionCount = 0;
            for (String sectionName : Objects.requireNonNull(config.getConfigurationSection("nation-sign-lines")).getKeys(false)) {

                Block locatedBlock = world.getBlockAt(signX, signY - sectionCount, signZ);
                if (!(locatedBlock.getState() instanceof Sign)) {
                    if (config.getBoolean("debug"))
                        Townyinfo.logger.info(String.format("DEBUG UpdateNationSigns: Nation in %s %s %s is not a sign", signX, signY - sectionCount, signZ));
                } else {
                    Sign sign = (Sign) locatedBlock.getState();

                    for (int i = 0; i <= 3; i++) {
                        String line = Objects.requireNonNull(config.getString(String.format("nation-sign-lines.%s.%s", sectionName, i + 1))).replace(
                                "%name%", nation.getName()
                        ).replace(
                                "%num-residents%", Integer.toString(nation.getNumResidents())
                        ).replace(
                                "%num-towns%", Integer.toString(nation.getNumTowns())
                        ).replace(
                                "%is-open%", isOpen
                        ).replace(
                                "%is-main%", isMain
                        ).replace(
                                "%tax%", Integer.toString((int) nation.getTaxes())
                        ).replace(
                                "%king%", nation.getKing().getName()
                        ).replace(
                                "%capital%", nation.getCapital().getName()
                        ).replace(
                                "%assistant1%", assistant1
                        ).replace(
                                "%assistant2%", assistant2
                        ).replace(
                                "%assistant3%", assistant3
                        ).replace(
                                "%enemy1%", enemy1
                        ).replace(
                                "%enemy2%", enemy2
                        ).replace(
                                "%enemy3%", enemy3
                        ).replace(
                                "%ally1%", ally1
                        ).replace(
                                "%ally2%", ally2
                        ).replace(
                                "%ally3%", ally3
                        ).replace("&", "§");
                        sign.setLine(i, line);
                    }
                    sign.update();
                }
                sectionCount += 1;
            }
            if (config.getBoolean("debug"))
                Townyinfo.logger.info(String.format("DEBUG UpdateNationSigns: Nation sign \"%s\" updated for nation %s", signInfo, nation.getName()));
        }
    }
}
