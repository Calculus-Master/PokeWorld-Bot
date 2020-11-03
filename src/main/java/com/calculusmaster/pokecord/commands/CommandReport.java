package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.util.Mongo;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;

public class CommandReport extends Command
{
    public CommandReport(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "bugreport <command> <problem>");
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 3)
        {
            Document reportInfo = new Document("user", this.player.getName())
                    .append("command", this.msg[1])
                    .append("report", this.msg[2]);

            Mongo.ReportData.insertOne(reportInfo);
            this.embed.setDescription("Successfully submitted!");
        }
        else this.embed.setDescription(CommandInvalid.getFull());

        return this;
    }
}
