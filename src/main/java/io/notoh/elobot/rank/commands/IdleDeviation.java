package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.rank.Glicko2;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;

public class IdleDeviation extends Command {

    private final Database database;

    public IdleDeviation(Database database) {
        super("secretidledeviation");
        this.database = database;
    }

    @Override
    public void run(Message msg) {
        if (msg.getMember() == null || !msg.getMember().getId().equals("129712117837332481") || getArguments(msg).length != 1) {
            return;
        }
        String player = getArguments(msg)[0];
        database.getSortedPlayers().forEach(PlayerWrapper::idleDeviation);
        PlayerWrapper playerWrapper = database.getPlayers().get(player);

        msg.getChannel().sendMessage(Arrays.toString(playerWrapper.getGlicko()) + " g2: " + Arrays.toString(Glicko2.glicko1ToGlicko2(playerWrapper.getGlicko()))).queue();
    }

}
