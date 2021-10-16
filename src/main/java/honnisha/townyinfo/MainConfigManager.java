package honnisha.townyinfo;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class MainConfigManager {
    @Getter private final boolean debug;

    @Getter private final String nationStatusUpdateTime;
    @Getter private final long updateSignsTicks;

    @Getter private final String messagesMainNationTitle;
    @Getter private final String mainKingPlaceholder;
    @Getter private final String townSortMethod;
    @Getter private final String openName;
    @Getter private final String closeName;
    @Getter private final String noString;
    @Getter private final String yesString;
    @Getter private final String nationSortMethod;
    @Getter private final String signNationClickCommand;
    @Getter private final String signTownClickCommand;

    @Getter private final List<String> townSigns;
    @Getter private final Set<String> townSignLines;
    @Getter private final Map<String, String> townSignLinesMap = new HashMap<>();

    @Getter private final List<String> nationSigns;
    @Getter private final Set<String> nationSignLines;
    @Getter private final Map<String, String> nationSignLinesMap = new HashMap<>();

    public MainConfigManager(FileConfiguration config) {
        debug = config.getBoolean("debug");
        nationStatusUpdateTime = config.getString("nation-status-update-time");
        updateSignsTicks = config.getLong("update-signs-ticks");
        messagesMainNationTitle = config.getString("messages.main-nation-title");
        mainKingPlaceholder = config.getString("main-king-placeholder");
        townSortMethod = config.getString("town-sort-method");
        openName = Objects.requireNonNull(config.getString("open-name"));
        closeName = Objects.requireNonNull(config.getString("close-name"));
        yesString = Objects.requireNonNull(config.getString("yes-string"));
        noString = Objects.requireNonNull(config.getString("no-string"));
        nationSortMethod = config.getString("nation-sort-method");
        signNationClickCommand = Objects.requireNonNull(config.getString("sign-nation-click-command"));
        signTownClickCommand = Objects.requireNonNull(config.getString("sign-town-click-command"));

        townSigns = config.getStringList("town-signs");
        townSignLines = Objects.requireNonNull(config.getConfigurationSection("town-sign-lines")).getKeys(false);
        for (String sectionName : this.townSignLines) {
            for (int i = 0; i <= 3; i++) {
                String key = String.format("town-sign-lines.%s.%s", sectionName, i + 1);
                townSignLinesMap.put(key, Objects.requireNonNull(config.getString(key)));
            }
        }

        nationSigns = config.getStringList("nation-signs");
        nationSignLines = Objects.requireNonNull(config.getConfigurationSection("nation-sign-lines")).getKeys(false);
        for (String sectionName : this.nationSignLines) {
            for (int i = 0; i <= 3; i++) {
                String key = "nation-sign-lines." + sectionName + "." + (i + 1);
                nationSignLinesMap.put(key, Objects.requireNonNull(config.getString(key)));
            }
        }
    }

    public String getYesOrNo(boolean condition) {
        return condition ? yesString : noString;
    }

    public String getTownSignLine(String sectionName, int i) {
        String key = "town-sign-lines."+ sectionName + "." + (i + 1);
        if (!townSignLinesMap.containsKey(key)) {
            Townyinfo.logger.info("DEBUG MainConfigManager: townSignLinesMap is not contains: "+ key);
            return null;
        }
        return townSignLinesMap.get(key);
    }

    public String getNationSignLine(String sectionName, int i) {
        String key = "nation-sign-lines." + sectionName + "." + (i + 1);
        if (!nationSignLinesMap.containsKey(key)) {
            Townyinfo.logger.info("DEBUG MainConfigManager: nationSignLinesMap is not contains: "+ key);
            return null;
        }
        return nationSignLinesMap.get(key);
    }
}
