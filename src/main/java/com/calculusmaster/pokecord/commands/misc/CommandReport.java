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
        if(this.msg.length >= 3)
        {
            Document reportInfo = new Document("user", this.player.getName())
                    .append("command", this.msg[1])
                    .append("report", this.restOfString());

            Mongo.ReportData.insertOne(reportInfo);
            this.embed.setDescription("Successfully submitted!");

            Achievements.grant(this.player.getId(), Achievements.SUBMITTED_BUG_REPORT, this.event);
        }
        else this.embed.setDescription(CommandInvalid.getFull());

        return this;
    }

    private String restOfString()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 2; i < this.msg.length; i++) sb.append(this.msg[i] + " ");
        return sb.toString().trim();
    }
}
