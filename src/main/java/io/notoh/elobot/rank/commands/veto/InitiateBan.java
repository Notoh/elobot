package io.notoh.elobot.rank.commands.veto;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import net.dv8tion.jda.api.entities.Message;

import java.util.HashMap;
import java.util.Map;

public class InitiateBan extends Command {

    static boolean isBanning = false;
    static Map<String, Boolean> maps = new HashMap<>();

    public InitiateBan(Database database) {
        super("initiateban");
        maps.put("overgrown", false);
        maps.put("atomic", false);
        maps.put("reserve", false);
        maps.put("sandstorm", false);
        maps.put("melonfactory", false);
        maps.put("carrier", false);
        maps.put("alleyway", false);
    }

    @Override
    public void run(Message msg) {
        isBanning = true;
        msg.getChannel().sendMessage("Bans starting. Captains alternate bans with -ban <map>. To see maps not banned," +
                " do -notbanned").queue();
    }
}
