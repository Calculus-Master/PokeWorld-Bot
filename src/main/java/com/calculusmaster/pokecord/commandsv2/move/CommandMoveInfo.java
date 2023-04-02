package com.calculusmaster.pokecord.commandsv2.move;

import com.calculusmaster.pokecord.commandsv2.CommandData;
import com.calculusmaster.pokecord.commandsv2.CommandV2;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.data.CustomMoveDataRegistry;
import com.calculusmaster.pokecord.game.moves.data.MoveData;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.*;
import java.util.stream.Collectors;

public class CommandMoveInfo extends CommandV2
{
    public static void init()
    {
        CommandData
                .create("moveinfo")
                .withConstructor(CommandMoveInfo::new)
                .withFeature(Feature.VIEW_MOVE_INFO)
                .withCommand(Commands
                        .slash("moveinfo", "View information about a Move!")
                        .addOption(OptionType.STRING, "move", "The name of the move to view information about.", true, true)
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        OptionMapping moveNameOption = Objects.requireNonNull(event.getOption("move"));

        String moveName = moveNameOption.getAsString();
        MoveEntity moveEntity = MoveEntity.cast(moveName);

        if(moveEntity == null) return this.error("\"%s\" is not a valid move name. Please check the spelling of the move, and use the autocomplete options.".formatted(moveName));

        MoveData data = moveEntity.data();

        //Description (Flavor Text + Not Implemented Warning)
        List<String> description = new ArrayList<>();

        if(!Move.isImplemented(moveEntity)) description.add("***Warning: Move is not currently implemented!***\nThis likely means the Move is still in development. You will not be able to use this move within Duels until it is implemented.");
        if(!data.getFlavorText().isEmpty()) description.add("*" + data.getFlavorText().get(new Random().nextInt(data.getFlavorText().size())) + "*");
        if(Move.CUSTOM_MOVES.contains(moveEntity)) description.add("*This is a custom move! Its functionality is either slightly different from the games (most cases), or significantly altered (rare).*");

        //Fields (Contents)
        String kind = "Standard Move";
        String type = data.getType().getStyledName();
        String category = data.getCategory() != null ? Global.normalize(data.getCategory().toString()) : "";

        String power = (data.isBasePowerNull() ? "None" : data.getBasePower() + "");
        String accuracy = (data.isBaseAccuracyNull() ? "∞" : data.getBaseAccuracy() + "");
        String priority = (data.getPriority() > 0 ? "+" : (data.getPriority() < 0 ? "-" : "")) + data.getPriority();

        String effects = data.getEffectText().isEmpty() ? "" : data.getEffectText().stream().map(s -> "- *" + s + "*").collect(Collectors.joining("\n"));

        //Z-Move
        if(moveEntity.isZMove())
        {
            boolean typed = CustomMoveDataRegistry.isTypedZMove(moveEntity);
            kind = "Z-Move" + (!typed ? " (Unique)" : "");
            category = data.getCategory() == null ? "Depends on Base Move" : category;
            power = typed ? "Depends on Base Move" : power;
            accuracy = "Guaranteed";

        }
        else if(moveEntity.isMaxMove())
        {
            kind = moveEntity.isGMaxMove() ? "G-Max Move" : "Max Move";
            category = data.getCategory() == null ? "Depends on Base Move" : category;
            power = "Depends on Base Move";
            accuracy = "Guaranteed";
        }

        //Embed Construction
        this.embed
                .setTitle("Move Data: " + data.getName())
                .setFooter("Move Entity: " + moveEntity)
                .setDescription(description.isEmpty() ? "*No Description Available*" : String.join("\n\n", description))

                .addField("Kind", kind, true)
                .addField("Type", type, true)
                .addField("Category", category, true)

                .addField("Numerical Stats", """
                        Base Power: **%s**
                        Base Accuracy: **%s**
                        Priority: **%s**
                        """.formatted(power, accuracy, priority), false)

                .addField("Effects", effects.isEmpty() ? "No Information Available" : effects, false)
                .setColor(data.getType().getColor());

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        if(event.getFocusedOption().getName().equals("move"))
        {
            String currentInput = event.getFocusedOption().getValue();

            event.replyChoiceStrings(this.getAutocompleteOptions(currentInput,
                    Arrays.stream(MoveEntity.values()).map(e -> e.data().getName()).toList())).queue();
        }

        return true;
    }
}
