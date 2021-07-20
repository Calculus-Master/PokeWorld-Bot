package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.util.Mongo;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;

public class CommandReport extends Command
{
    public CommandReport(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length >= 2)
        {
            Document reportInfo = new Document("user", this.player.getName())
                    .append("report", this.getMultiWordContent(1));

            Mongo.ReportData.insertOne(reportInfo);
            this.sendMsg("Successfully submitted!");

            Achievements.grant(this.player.getId(), Achievements.SUBMITTED_BUG_REPORT, this.event);
        }
        else this.sendMsg(CommandInvalid.getFull());

        return this;
    }
}
