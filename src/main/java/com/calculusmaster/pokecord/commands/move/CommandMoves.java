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
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.evolution.EvolutionRegistry;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import kotlin.Pair;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.SplitUtil;

import java.util.*;
import java.util.stream.Collectors;

public class CommandMoves extends PokeWorldCommand
{
    private static final Map<String, Pair<String, MoveEntity>> MOVE_LEARN_REQUESTS = new HashMap<>();

    public static void init()
    {
        CommandData
                .create("moves")
                .withConstructor(CommandMoves::new)
                .withCommand(Commands
                        .slash("moves", "View your Pokemon's moves, learn new ones, analyze information about them, and more!")
                        .addSubcommands(
                                new SubcommandData("info", "View information about a specific move.")
                                        .addOption(OptionType.STRING, "move", "The name of the move to view information about.", true, true),
                                new SubcommandData("view", "View your active Pokemon's moves, and moves they could learn."),
                                new SubcommandData("learn", "Teach your active Pokemon a new move that they can learn by leveling up.")
                                        .addOption(OptionType.STRING, "move", "The name of the move to learn.", true, true)
                                        .addOption(OptionType.INTEGER, "slot", "Optional: Include the slot to learn this move into, skipping the replace command.", false, false),
                                new SubcommandData("replace", "Upon request, replace a slot in your active Pokemon's moveset with the move you want to learn.")
                                        .addOption(OptionType.INTEGER, "slot", "The slot to replace with the new move.", true, false),
                                new SubcommandData("search", "Search for Pokemon that know a particular move.")
                                        .addOption(OptionType.STRING, "move", "The name of the move to search for.", true, true)
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("info"))
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
        else if(subcommand.equals("view"))
        {
            if(this.isInvalidMasteryLevel(Feature.VIEW_MOVES)) return this.respondInvalidMasteryLevel(Feature.VIEW_MOVES);

            Duel duel = DuelHelper.instance(this.player.getId());

            if(duel != null)
            {
                UserPlayer active = (UserPlayer)duel.getPlayer(this.player.getId());
                Player opponent = duel.getOpponent(this.player.getId());

                List<String> moveList = new ArrayList<>();
                List<String> moveTypeEffectList = new ArrayList<>();
                List<String> moveUseCommandList = new ArrayList<>();
                for(int i = 0; i < active.active.getMoves().size(); i++)
                {
                    Move m = active.active.getMove(i);
                    if(active.active.isDynamaxed()) m = DuelHelper.getMaxMove(active.active, m);

                    moveList.add((i + 1) + ": " + m.getName());
                    moveTypeEffectList.add(m.getEffectivenessOverview(opponent.active));
                    moveUseCommandList.add("`/use move slot:" + (i + 1) + "`");
                }

                ZCrystal crystal = ZCrystal.cast(active.data.getEquippedZCrystal());
                String zmove = !this.serverData.areZMovesEnabled() ? "**Disabled in this server!**" : ((active.usedZMove ? "Used." : "Available!") + "\t\t" + "Equipped Z-Crystal: **" + (crystal == null ? "None" : crystal.getStyledName()) + "**");
                String dynamax = !this.serverData.isDynamaxEnabled() ? "**Disabled in this server!**" : (active.usedDynamax ? "Used." : "Available!");

                this.embed
                        .setTitle(active.active.getName() + "'s Moveset")
                        .setDescription("""
                                *You're currently in a Duel.*
                                This is the equipped moveset for your active Pokemon, %s.
                                Also shown are the status of your Z-Move and Dynamax usage in the Duel. You can only use each one once, so choose wisely!
                                """.formatted(active.active.getName()))

                        .addField("Moves", String.join("\n", moveList), true)
                        .addField("Type Effectiveness", String.join("\n", moveTypeEffectList), true)
                        .addField("Use", String.join("\n", moveUseCommandList), true)

                        .addField("Techniques", """
                                Z-Move: %s
                                Dynamax: %s
                                """.formatted(zmove, dynamax), false);
            }
            else
            {
                Pokemon active = this.playerData.getSelectedPokemon();
                List<String> activeMoves = new ArrayList<>();
                List<String> activeMoveSources = new ArrayList<>();

                for(int i = 0; i < active.getMoves().size(); i++)
                {
                    Move m = active.getMove(i);

                    String source = "";
                    if(active.getData().getLevelUpMoves().containsKey(m.getEntity())) source = "Level Up";
                    else if(active.hasTM() && active.getTM().getMove().equals(m.getEntity())) source = "TM";
                    else if(active.is(PokemonEntity.ZYGARDE_50, PokemonEntity.ZYGARDE_10, PokemonEntity.ZYGARDE_COMPLETE) && active.getItem().equals(Item.ZYGARDE_CUBE) && Move.ZYGARDE_CUBE_MOVES.contains(m.getEntity())) source = "Zygarde Cube";
                    else if(MoveTutorRegistry.MOVE_TUTOR_MOVES.contains(m.getEntity())) source = "Move Tutor";
                    else if(active.getData().getEggMoves().contains(m.getEntity())) source = "Breeding";
                    else if(m.is(MoveEntity.TACKLE)) source = "Default";

                    activeMoveSources.add("*" + source + "*");
                    activeMoves.add((i + 1) + ": **" + m.getName() + "**");
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

                        .addField("Active Moves", String.join("\n", activeMoves), true)
                        .addField("Source", String.join("\n", activeMoveSources), true)
                        .addBlankField(true)

                        .addField("Available Moves", String.join("\n", levelUpMoves), false)
                        .addField("Version", "**%s**".formatted(active.getData().getLevelUpMovesVersion()), false)

                        .setFooter("A green circle indicates a move that can be learned. A yellow circle indicates a move that is currently in your moveset. A lock indicates a move that cannot be learned yet. An exclamation point signifies that the move is not implemented yet, meaning it will not work within duels.");
            }
        }
        else if(subcommand.equals("learn"))
        {
            if(this.isInvalidMasteryLevel(Feature.LEARN_REPLACE_MOVES)) return this.respondInvalidMasteryLevel(Feature.LEARN_REPLACE_MOVES);

            OptionMapping moveOption = Objects.requireNonNull(event.getOption("move"));
            OptionMapping slotOption = event.getOption("slot");

            MoveEntity move = MoveEntity.cast(moveOption.getAsString());
            if(move == null) return this.error("\"" + moveOption.getAsString() + "\" is not a valid Move name. Please check your spelling.");
            else if(DuelHelper.isInDuel(this.player.getId())) return this.error("You cannot learn new moves while in a Duel.");

            Pokemon active = this.playerData.getSelectedPokemon();

            if(!active.availableMoves().contains(move)) return this.error(active.getName() + " cannot learn " + move.data().getName() + ". Either it is not a high enough level, or cannot learn the move at all. Use `/moves view` to see what " + active.getName() + " can learn currently.");
            else
            {
                int slot = slotOption == null ? -1 : slotOption.getAsInt();

                if(slot == -1) //Forward to /moves replace
                {
                    MOVE_LEARN_REQUESTS.put(this.player.getId(), new Pair<>(active.getUUID(), move));

                    List<String> movesetDisplay = new ArrayList<>();

                    for(int i = 0; i < active.getMoves().size(); i++)
                        movesetDisplay.add("`/moves replace slot:" + (i + 1) + "` – Current Move: *" + active.getMoves().get(i).data().getName() + "*");

                    this.embed.setDescription("""
                            Which of your moves would you like to replace with **%s**?
                            %s
                            """.formatted(move.data().getName(), String.join("\n", movesetDisplay)));
                }
                else //Automatically do the replacement
                {
                    if(slot < 1 || slot > 4) return this.error("Invalid slot number: " + slot + ". The slot must be either 1, 2, 3, or 4.");

                    MoveEntity oldMove = active.getMove(slot - 1).getEntity();
                    active.learnMove(move, slot - 1);
                    active.updateMoves();

                    this.playerData.getStatistics().increase(StatisticType.MOVES_LEARNED);

                    EvolutionRegistry.checkAutomaticEvolution(active, this.playerData, this.server.getId());

                    this.response = active.getDisplayName() + " learned **" + move.data().getName() + "**! It replaced *" + oldMove.data().getName() + "* in Slot " + slot + ".";
                }
            }
        }
        else if(subcommand.equals("replace"))
        {
            if(this.isInvalidMasteryLevel(Feature.LEARN_REPLACE_MOVES)) return this.respondInvalidMasteryLevel(Feature.LEARN_REPLACE_MOVES);

            OptionMapping slotOption = Objects.requireNonNull(event.getOption("slot"));
            int slot = slotOption.getAsInt();

            Pokemon active = this.playerData.getSelectedPokemon();

            if(!MOVE_LEARN_REQUESTS.containsKey(this.player.getId())) return this.error("You do not have an active learn request. Please use `/moves learn` first to learn a new Move!");
            else if(!MOVE_LEARN_REQUESTS.get(this.player.getId()).getFirst().equals(active.getUUID()))
            {
                MOVE_LEARN_REQUESTS.remove(this.player.getId());
                return this.error("Your active move learn request is not with your active Pokemon, " + active.getDisplayName() + ". Your old request has now been deleted - please use `/moves learn` to learn a move for your active Pokemon!");
            }
            else if(slot < 1 || slot > 4) return this.error("Invalid slot number: " + slot + ". The slot must be either 1, 2, 3, or 4.");
            else if(DuelHelper.isInDuel(this.player.getId()))
            {
                MOVE_LEARN_REQUESTS.remove(this.player.getId());
                return this.error("You cannot learn new moves while in a Duel.");
            }

            MoveEntity move = MOVE_LEARN_REQUESTS.remove(this.player.getId()).getSecond();
            MoveEntity oldMove = active.getMove(slot - 1).getEntity();

            active.learnMove(move, slot - 1);
            active.updateMoves();

            this.playerData.getStatistics().increase(StatisticType.MOVES_LEARNED);

            EvolutionRegistry.checkAutomaticEvolution(active, this.playerData, this.server.getId());

            this.response = active.getDisplayName() + " learned **" + move.data().getName() + "**! It replaced *" + oldMove.data().getName() + "* in Slot " + slot + ".";
        }
        else if(subcommand.equals("search"))
        {
            OptionMapping moveOption = Objects.requireNonNull(event.getOption("move"));
            MoveEntity move = MoveEntity.cast(moveOption.getAsString());

            if(move == null) return this.error("\"" + moveOption.getAsString() + "\" is not a valid move name!");

            List<String> knowByLevelUp = new ArrayList<>();
            List<String> knowByTM = new ArrayList<>();
            List<String> knowByEgg = new ArrayList<>();

            for(PokemonEntity e : PokemonEntity.values())
            {
                PokemonData data = e.data();

                if(data.getLevelUpMoves().containsKey(move)) knowByLevelUp.add(e.getName());
                if(data.getTMs().contains(move)) knowByTM.add(e.getName());
                if(data.getEggMoves().contains(move)) knowByEgg.add(e.getName());
            }

            int max = 1021;

            String levelUp = knowByLevelUp.isEmpty() ? "None" : String.join(", ", knowByLevelUp);
            String tm = knowByTM.isEmpty() ? "None" : String.join(", ", knowByTM);
            String egg = knowByEgg.isEmpty() ? "None" : String.join(", ", knowByEgg);

            if(levelUp.length() > max) levelUp = SplitUtil.split(levelUp, max, SplitUtil.Strategy.WHITESPACE).get(0) + "...";
            if(tm.length() > max) tm = SplitUtil.split(tm, max, SplitUtil.Strategy.WHITESPACE).get(0) + "...";
            if(egg.length() > max) egg = SplitUtil.split(egg, max, SplitUtil.Strategy.WHITESPACE).get(0) + "...";

            //TODO: Add pagination for move search results somehow

            this.embed
                    .setTitle("Move Search Results: " + move.getName())
                    .setDescription("""
                            These are all the Pokemon that can learn **%s** from various Move sources.
                            """.formatted(move.getName()))
                    .addField("Learn by Level Up", levelUp, false)
                    .addField("Learn by TM", tm, false)
                    .addField("Learn by Breeding (Egg Move)", egg, false);
        }

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        if(event.getFocusedOption().getName().equals("move")) //Covers both "move" autocompletes
        {
            String currentInput = event.getFocusedOption().getValue();

            event.replyChoiceStrings(this.getAutocompleteOptions(currentInput,
                    Arrays.stream(MoveEntity.values()).map(e -> e.data().getName()).toList())).queue();
        }

        return true;
    }
}
