package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.mongo.Mongo;
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
        if(this.insufficientMasteryLevel(Feature.CREATE_REPORT)) return this.invalidMasteryLevel(Feature.CREATE_REPORT);

        if(this.msg.length >= 2)
        {
            String report = this.getMultiWordContent(1);

            if(report.trim().length() < 50) this.response = "Please explain the problem in more detail! Minimum length: 50 characters";
            else
            {
                Document reportInfo = new Document("user", this.player.getName())
                        .append("report", this.getMultiWordContent(1));

                Mongo.ReportData.insertOne(reportInfo);
                this.response = "Successfully submitted!";

                Achievements.grant(this.player.getId(), Achievements.SUBMITTED_BUG_REPORT, this.event);
            }
        }
        else this.invalid();

        return this;
    }
}
