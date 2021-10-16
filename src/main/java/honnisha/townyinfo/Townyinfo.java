package honnisha.townyinfo;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.KeyAlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.object.metadata.IntegerDataField;
import honnisha.townyinfo.betonquest.conditions.*;
import honnisha.townyinfo.betonquest.events.EventChangeNationMainPoints;
import honnisha.townyinfo.events.SiegeWarEvents;
import honnisha.townyinfo.events.TownyEvents;
import honnisha.townyinfo.handlers.ClickHandler;
import honnisha.townyinfo.handlers.CommandHandler;
import honnisha.townyinfo.tasks.NationUpdate;
import honnisha.townyinfo.utils.Task;
import lombok.Getter;
import org.betonquest.betonquest.BetonQuest;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

import static honnisha.townyinfo.signs.NationSignsUpdater.UpdateNationSigns;
import static honnisha.townyinfo.signs.TownSignsUpdater.UpdateTownSigns;

public final class Townyinfo extends JavaPlugin {

    private static Townyinfo instance;
    @Getter private MainConfigManager mainConfig;
    public static Logger logger;

    private ArrayList<Task> tasks = new ArrayList<>();

    public static final BooleanDataField isMainNationField = new BooleanDataField("is_main_nation", false, "Is main nation");
    public static final IntegerDataField mainNationPointsField = new IntegerDataField("main_nation_points", 0, "Main nation points");

    @Override
    public void onEnable() {
        instance = this;
        logger = Bukkit.getLogger();
        this.loadConfig();

        Objects.requireNonNull(this.getCommand("townyinfo")).setExecutor(new CommandHandler());

        getServer().getPluginManager().registerEvents(new ClickHandler(), this);

        this.startSignsUpdater();
        getServer().getPluginManager().registerEvents(new TownyEvents(), this);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this).register();
            logger.info("PlaceholderAPI plugin hooked");
        }

        if(Bukkit.getPluginManager().getPlugin("SiegeWar") != null) {
            getServer().getPluginManager().registerEvents(new SiegeWarEvents(), this);
            logger.info("SiegeWar plugin hooked");
        }

        if(Bukkit.getPluginManager().getPlugin("BetonQuest") != null) {
            BetonQuest.getInstance().registerConditions("isnationmain", IsMainNation.class);
            BetonQuest.getInstance().registerConditions("isnationadmin", IsNationAdmin.class);
            BetonQuest.getInstance().registerConditions("istownadmin", IsTownAdmin.class);
            BetonQuest.getInstance().registerConditions("isnationhasmainpoints", IsNationHasMainPoints.class);
            BetonQuest.getInstance().registerConditions("isinnation", IsInNation.class);

            BetonQuest.getInstance().registerEvents("changenationpoints", EventChangeNationMainPoints.class);
            logger.info("BetonQuest plugin hooked");
        }

        try {
            TownyAPI.getInstance().registerCustomDataField(isMainNationField);
        } catch (KeyAlreadyRegisteredException ignored) { }
        try {
            TownyAPI.getInstance().registerCustomDataField(mainNationPointsField);
        } catch (KeyAlreadyRegisteredException ignored) { }

        this.loadTasks();
        this.RunTasksUpdater();
        logger.info("Townyinfo plugin loaded");
    }

    public void startSignsUpdater() {
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                    UpdateTownSigns();
                    UpdateNationSigns();
                } catch (NotRegisteredException e) {
                    e.printStackTrace();
                }
            }
        }, 0L, this.mainConfig.getUpdateSignsTicks());
        logger.info("Townyinfo sign updater started");
    }

    public void loadTasks() {
        tasks.clear();

        if (this.mainConfig.getNationStatusUpdateTime() != null && !this.mainConfig.getNationStatusUpdateTime().equals(""))
            tasks.add(new Task(this.mainConfig.getNationStatusUpdateTime(), new NationUpdate(true)));
    }

    private void RunTasksUpdater() {

        getServer().getScheduler().scheduleSyncRepeatingTask(this,new Runnable()
        {
            public void run()
            {
                for(Task task: tasks)
                    if(task.hasTime())
                        task.run();
            }
        },0L,100L);
    }

    public static Townyinfo getInstance() {
        return instance;
    }

    public void loadConfig() {
        this.saveDefaultConfig();
        mainConfig = new MainConfigManager(this.getConfig());
        logger.info("Townyinfo config loaded");
    }
}
