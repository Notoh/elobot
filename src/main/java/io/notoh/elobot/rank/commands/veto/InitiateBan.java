package io.notoh.elobot.rank.commands.veto;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import net.dv8tion.jda.core.entities.Message;

import java.util.HashMap;
import java.util.Map;

public class InitiateBan extends Command {

    static boolean isBanning = false;
    static Map<String, Boolean> maps = new HashMap<>();

    public InitiateBan(Database database) {
        super("initiateban");
        maps.put("train", false);
        maps.put("nuke", false);
        maps.put("overpass", false);
        maps.put("cache", false);
        maps.put("mirage", false);
        maps.put("dust2", false);
        maps.put("inferno", false);
    }

    @Override
    public void run(Message msg) {
        isBanning = true;
        msg.getChannel().sendMessage("Bans starting. Captains alternate bans with -ban <map>. To see maps not banned," +
                " do -notbanned").queue();
    }
}
