package io.notoh.elobot;


import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainEventHandler extends ListenerAdapter {

    private final Map<List<String>, Command> commands = new HashMap<>();
    private final Database ds;

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

        int index = msg.getContentRaw().indexOf(' ');
        if(!msg.getContentRaw().startsWith(Util.PREFIX)) {
            return;
        }
        String commandName = msg.getContentRaw().substring(Util.PREFIX.length(), index < 0 ?
                msg.getContentRaw().length() : index).toLowerCase();
        for(Map.Entry<List<String>, Command> entry : commands.entrySet()) {
            if(entry.getKey().contains(commandName)) {
                entry.getValue().run(msg);
                return;
            }
        }
    }

    public void addCommand(Command cmd) {
        commands.put(Arrays.asList(cmd.command), cmd);
    }



}
