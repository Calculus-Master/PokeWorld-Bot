package com.calculusmaster.pokecord.commands.move;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.Player;
import com.calculusmaster.pokecord.game.duel.players.UserPlayer;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.data.CustomMoveDataRegistry;
import com.calculusmaster.pokecord.game.moves.data.MoveData;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.moves.registry.MoveTutorRegistry;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.*;
import java.util.stream.Collectors;

public class CommandMoves extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("moves")
                .withConstructor(CommandMoves::new)
                .withFeature(Feature.VIEW_MOVE_INFO)
                .withCommand(Commands
                        .slash("moves", "View your Pokemon's moves, learn new ones, analyze information about them, and more!")
                        .addSubcommands(
                                new SubcommandData("info", "View information about a specific move.")
                                        .addOption(OptionType.STRING, "move", "The name of the move to view information about.", true, true),
                                new SubcommandData("view", "View your active Pokemon's moves, and moves they could learn.")
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        if(event.getSubcommandName() == null) return this.error();

        if(event.getSubcommandName().equals("info"))
        {
            if(this.isInvalidMasteryLevel(Feature.VIEW_MOVE_INFO)) return this.respondInvalidMasteryLevel(Feature.VIEW_MOVE_INFO);

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

            String power = (data.isBasePowerNull() ? "None" : String.valueOf(data.getBasePower()));
            String accuracy = (data.isBaseAccuracyNull() ? "∞" : String.valueOf(data.getBaseAccuracy()));
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
        }
        else if(event.getSubcommandName().equals("view"))
        {
            if(this.isInvalidMasteryLevel(Feature.VIEW_MOVES)) return this.respondInvalidMasteryLevel(Feature.VIEW_MOVES);

            Duel duel = DuelHelper.instance(this.player.getId());
            boolean isInDuel = duel != null && duel.getStatus().equals(DuelHelper.DuelStatus.DUELING);

            if(isInDuel)
            {
                UserPlayer active = (UserPlayer)duel.getPlayer(this.player.getId());
                Player opponent = duel.getOpponent(this.player.getId());

                String moves;

                List<String> moveList = new ArrayList<>();
                for(int i = 0; i < active.active.getMoves().size(); i++)
                {
                    Move m = active.active.getMove(i);
                    if(active.active.isDynamaxed()) m = DuelHelper.getMaxMove(active.active, m);

                    moveList.add((i + 1) + ": " + m.getName() + " (" + m.getEffectivenessOverview(opponent.active) + ")\t |\t `/use move " + (i + 1) + "`");
                }

                moves = String.join("\n", moveList);

                ZCrystal crystal = ZCrystal.cast(active.data.getEquippedZCrystal());
                String zmove = (active.usedZMove ? "Used." : "Available!") + "\t\t" + "Equipped Z-Crystal: **" + (crystal == null ? "None" : crystal.getStyledName()) + "**";
                String dynamax = (active.usedDynamax ? "Used." : "Available!");

                this.embed
                        .setTitle(active.active.getName() + "'s Moveset")
                        .setDescription("""
                                *You're currently in a Duel.*
                                This is the equipped moveset for your active Pokemon, %s.
                                Also shown are the status of your Z-Move and Dynamax usage in the Duel. You can only use each one once, so choose wisely!
                                """)
                        .addField("Moves", moves, false)
                        .addField("Techniques", """
                                Z-Move: %s
                                Dynamax: %s
                                """.formatted(zmove, dynamax), false);
            }
            else
            {
                Pokemon active = this.playerData.getSelectedPokemon();
                List<String> activeMoves = new ArrayList<>();

                for(int i = 0; i < active.getMoves().size(); i++)
                {
                    Move m = active.getMove(i);

                    String source = "";
                    if(active.getData().getLevelUpMoves().containsKey(m.getEntity())) source = "Level Up";
                    else if(active.hasTM() && active.getTM().getMove().equals(m.getEntity())) source = "TM";
                    else if(active.is(PokemonEntity.ZYGARDE_50, PokemonEntity.ZYGARDE_10, PokemonEntity.ZYGARDE_COMPLETE) && active.getItem().equals(Item.ZYGARDE_CUBE) && Move.ZYGARDE_CUBE_MOVES.contains(m.getEntity())) source = "Zygarde Cube";
                    else if(MoveTutorRegistry.MOVE_TUTOR_MOVES.contains(m.getEntity())) source = "Move Tutor";
                    else if(active.getData().getEggMoves().contains(m.getEntity())) source = "Breeding";

                    activeMoves.add((i + 1) + ": **" + m.getName() + "** (Source: *" + source + "*)");
                }

                List<String> levelUpMoves = new ArrayList<>();
                for(int i = 0; i < active.getLevelUpMoves().size(); i++)
                {
                    MoveEntity e = active.getLevelUpMoves().get(i);

                    String tag;
                    if(active.availableMoves().contains(e))
                        tag = !active.getMoves().contains(e) ? ":green_circle:" : ":yellow_circle:";
                    else
                        tag = ":lock:";

                    if(!Move.isImplemented(e)) tag += ":exclamation:";

                    levelUpMoves.add(tag + " **" + e.data().getName() + "** – Level " + active.getData().getLevelUpMoves().get(e));
                }

                this.embed
                        .setTitle(active.getName() + "'s Moves")
                        .setDescription("""
                                This is the moveset for your active Pokemon.
                                You can learn new moves, provided your active Pokemon's level is higher than the requirement, using `/moves learn`.
                                """)
                        .addField("Active Move Set", String.join("\n", activeMoves), false)
                        .addField("Available Moves", String.join("\n", levelUpMoves), false)
                        .setFooter("A green circle indicates a move that can be learned. A yellow circle indicates a move that is currently in your moveset. A lock indicates a move that cannot be learned yet. An exclamation point signifies that the move is not implemented yet, meaning it will not work within duels.");
            }
        }

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
