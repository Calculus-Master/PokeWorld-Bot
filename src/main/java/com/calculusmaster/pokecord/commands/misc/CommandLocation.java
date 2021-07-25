package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.elements.Location;
import com.calculusmaster.pokecord.game.enums.elements.Region;
import com.calculusmaster.pokecord.game.enums.elements.Time;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.event.LocationEventHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandLocation extends Command
{
    public CommandLocation(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        Location l = LocationEventHelper.getLocation(this.server.getId());
        Region r = l.region;
        Time t = LocationEventHelper.getTime();

        this.embed.setTitle("Location Info for " + this.server.getName())
                .addField("Location", Global.normalCase(l.toString()), true)
                .addField("Region", Global.normalCase(r.toString()), true)
                .addField("Time", Global.normalCase(t.toString()), true)
                .setFooter("Locations will primarily affect certain evolutions!");

        return this;
    }
}
