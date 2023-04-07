package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.world.RegionManager;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class CommandWorld extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("world")
                .withConstructor(CommandWorld::new)
                .withCommand(Commands
                        .slash("world", "View the current Region and Time in " + Pokecord.NAME + "!")
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        this.embed
                .setTitle("World Info")
                .setDescription("""
                        Here you can view the current simulated Region, and Time within the world.
                        """)
                .addField("Region", "`" + Global.normalize(RegionManager.getCurrentRegion().toString()) + "`", true)
                .addField("Time", "`" + Global.normalize(RegionManager.getCurrentTime().toString()) + "`", true)
                .addBlankField(true)
                .addField("Region Info", """
                        The current region will influence certain Pokemon evolutions. Pokemon that are originally from the current Region will have **boosted spawn rates** until the Region changes!
                        
                        Every couple of hours, the Region will change in an ordered cycle (so the next Region will be the one after the current one). 
                        Also, if the current Region is Paldea, the next one will be Kanto, restarting the cycle.
                        """, false)
                .addField("Time Info", """
                        The current time will also influence certain Pokemon evolutions, and has some minor effects on other things such as some Augments. 
                        
                        Also, evolutions that require daytime will activate if the current time is Dusk (with the exception of Rockruff).
                        
                        *Note*: The time is **not synchronized** with your timezone.
                        *%s operates off of __UTC-7__!* 
                        As a result daytime in the bot may be nighttime for you. Use `/world` to check if you're not sure!
                        """.formatted(Pokecord.NAME), false)
                .setTimestamp(Global.timeNow());

        return true;
    }
}
