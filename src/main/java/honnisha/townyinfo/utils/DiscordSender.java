package honnisha.townyinfo.utils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import honnisha.townyinfo.Townyinfo;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Optional;

public class DiscordSender {
    private Optional<TextChannel> LogChannelOptional = Optional.empty();

    public DiscordSender() {
        FileConfiguration config = Townyinfo.getInstance().getConfig();
        String channelName = config.getString("notifications-discord-channel");

        if (channelName != null) {
            LogChannelOptional = Optional.ofNullable(DiscordSRV.getPlugin().getOptionalTextChannel(channelName));
        }
    }

    public boolean send(String message) {
        if (this.LogChannelOptional.isPresent()) {
            TextChannel destinationChannel = this.LogChannelOptional.get();
            message = DiscordUtil.translateEmotes(message, destinationChannel.getGuild());
            DiscordUtil.queueMessage(destinationChannel, message, false);
            return true;
        }
        return false;
    }
}
