package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.world.RaidEvent;
import com.calculusmaster.pokecord.util.helpers.event.RaidEventHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;

public class CommandRaid extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("raid")
                .withConstructor(CommandRaid::new)
                .withFeature(Feature.PVE_DUELS_RAID)
                .withCommand(Commands
                        .slash("raid", "Participate in Raids against tough Pokemon!")
                        .addSubcommands(
                                new SubcommandData("join", "Join an active Raid."),
                                new SubcommandData("leave", "Leave an active Raid.")
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());
        boolean join = subcommand.equals("join");

        if(!RaidEventHelper.hasRaid(this.server.getId())) return this.error("There is not an active Raid in this server.");

        RaidEvent raidEvent = RaidEventHelper.getRaidEvent(this.server.getId());
        if(raidEvent.isDuelActive()) return this.error("The active Raid has already started!");
        else if(join && raidEvent.hasPlayer(this.player.getId())) return this.error("You have already joined the active Raid. If you want to leave, use `/raid leave`.");
        else if(join && raidEvent.isFull()) return this.error("The Raid is full!");
        else if(!join && !raidEvent.hasPlayer(this.player.getId())) return this.error("You have not joined the active Raid. If you want to join, use `/raid join`.");

        this.ephemeral = true;

        if(join)
        {
            raidEvent.addPlayer(event.getMember());
            this.response = "You have joined the Raid with " + this.playerData.getSelectedPokemon().getName() + "!\n*Note:* You will not be able to change your active Pokemon until the Raid starts.";
        }
        else
        {
            raidEvent.removePlayer(event.getMember());
            this.response = "You have left the Raid!";
        }

        return true;
    }
}
