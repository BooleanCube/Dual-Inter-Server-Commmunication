package bots;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;

public class Commands extends ListenerAdapter {

    public static ArrayDeque<TextChannel> stack = new ArrayDeque<>();

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        String msg = event.getMessage().getContentRaw();
        if(msg.equalsIgnoreCase("?help")) {
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                    .setTitle("Help Command")
                    .setDescription("`?connect` connects you to another server who is waiting in the queue!\n" +
                            "`?disconnect` disconnects you from your guild!\n" +
                            "`?help` shows you the list of commands")
                    .build()
            ).queue();
        }
        if(msg.equalsIgnoreCase("?connect")) {
            if(stack.contains(event.getChannel()) || DualServerCommunication.connections.containsKey(event.getChannel())) {
                event.getChannel().sendMessage("You are already in the queue!").queue();
                return;
            }
            TextChannel other = stack.pollFirst();
            if(other!=null) {
                event.getChannel().sendMessage("Connecting you to **" + other.getGuild().getName() + "**...").queue();
                other.sendMessage("Connecting you to **" + event.getGuild().getName() + "**").queue();
                DualServerCommunication.connectChannels(event.getChannel(), other);
            }
            else {
                stack.addLast(event.getChannel());
                event.getChannel().sendMessage("You have been successfully added to the waiting queue!").queue();
            }
        }
        if(msg.equalsIgnoreCase("?disconnect")) {
            DualServerCommunication.connections.get(event.getChannel()).sendMessage("You were disconnected for the guild because **" + event.getGuild().getName() + "** left!").queue();
            DualServerCommunication.disconnectChannels(event.getChannel());
            event.getChannel().sendMessage("Successfully disconnected you from the guild!").queue();
        }
    }
}
