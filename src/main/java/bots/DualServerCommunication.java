package bots;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.HashMap;

public class DualServerCommunication extends ListenerAdapter {

    public static HashMap<TextChannel, TextChannel> connections = new HashMap<>();

    public static void connectChannels(TextChannel t1, TextChannel t2) {
        connections.put(t1, t2);
        connections.put(t2, t1);
    }

    public static void disconnectChannels(TextChannel t1) {
        TextChannel t2 = connections.get(t1);
        connections.remove(t1);
        connections.remove(t2);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        if(connections.containsKey(event.getChannel())) {
            TextChannel channel = connections.get(event.getChannel());
            channel.sendMessage("**" + event.getMember().getEffectiveName() + "**: " + event.getMessage().getContentRaw()).queue();
        }
    }
}
