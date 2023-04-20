package com.calculusmaster.pokecord.commands.player;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.mongo.DatabaseCollection;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bson.Document;

import java.util.Objects;

public class CommandBugReport extends PokeWorldCommand
{
    private static int MIN_REPORT_LENGTH = 50;

    public static void init()
    {
        CommandData
                .create("bugreport")
                .withConstructor(CommandBugReport::new)
                .withFeature(Feature.CREATE_REPORT)
                .withCommand(Commands
                        .slash("bugreport", "Report a bug you've found, or make a suggestion!")
                        .addOption(OptionType.STRING, "type", "Either BUG for bug reports, or SUGGESTION for suggestions or feedback.", true, true)
                        .addOption(OptionType.STRING, "description", "The description of the bug or suggestion. Please be as descriptive as possible", true)
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        OptionMapping typeOption = Objects.requireNonNull(event.getOption("type"));
        OptionMapping descriptionOption = Objects.requireNonNull(event.getOption("description"));

        String type = typeOption.getAsString();
        String description = descriptionOption.getAsString();

        if(!type.equalsIgnoreCase("BUG") && !type.equalsIgnoreCase("SUGGESTION")) return this.error("Invalid report type. Valid arguments are BUG or SUGGESTION.");
        else if(description.length() < MIN_REPORT_LENGTH) return this.error("Please explain the problem or suggestion in more detail! Your report needs to be at least **" + MIN_REPORT_LENGTH + "** characters long.");

        Document report = new Document()
                .append("type", type)
                .append("description", description)
                .append("time", Global.timeNow().toString().replaceFirst("T", "  Time: "));

        Mongo.insertOne("CommandBugReport - Player Report Insert", DatabaseCollection.REPORT, report);

        this.ephemeral = true;
        this.response = "Thank you for your " + (type.equalsIgnoreCase("BUG") ? "bug report" : "suggestion") + "! It will be looked into as soon as possible.";
        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        if(event.getFocusedOption().getName().equals("type")) event.replyChoiceStrings("BUG", "SUGGESTION").queue();

        return true;
    }
}
