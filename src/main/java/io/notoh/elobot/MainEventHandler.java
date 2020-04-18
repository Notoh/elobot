package io.notoh.elobot;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class MainEventHandler extends ListenerAdapter {

    private Map<String, Command> commands = new HashMap<>();
    private Database ds;

    public MainEventHandler(Database ds) {
        this.ds = ds;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        if(ds.messageCache.size() >= Util.MESSAGE_CACHE_MAX) {
            ds.messageCache = new HashMap<>();
        }
        ds.messageCache.put(msg.getId(), msg);
        if(msg.getAuthor().isBot()) {
            return;
        }

        if(msg.getGuild() == null) {
            return;
        }
        int index = msg.getContentRaw().indexOf(' ');
        String commandName = msg.getContentRaw().substring(Util.PREFIX.length(), index < 0 ?
                msg.getContentRaw().length() : index).toLowerCase();
        if (!commands.containsKey(commandName) || !msg.getContentRaw().startsWith("-")) {
            return;
        }
        commands.get(commandName).run(msg);
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("looks like notohs not a completely shit coder");
    }

    public void addCommand(Command cmd) {
        commands.put(cmd.command, cmd);
    }



}
