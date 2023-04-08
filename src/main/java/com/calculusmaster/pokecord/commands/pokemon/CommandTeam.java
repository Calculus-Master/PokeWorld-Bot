package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestrictionRegistry;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.player.PlayerTeam;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandTeam extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("team")
                .withConstructor(CommandTeam::new)
                .withFeature(Feature.CREATE_POKEMON_TEAMS)
                .withCommand(Commands
                        .slash("team", "Create your Pokemon team!")
                        .addSubcommands(
                                new SubcommandData("view", "View your current team."),
                                new SubcommandData("add", "Add a Pokemon to your current team.")
                                        .addOption(OptionType.INTEGER, "pokemon-number", "The number of the Pokemon you want to add to your team.", true),
                                new SubcommandData("remove", "Remove a Pokemon from your current team.")
                                        .addOption(OptionType.INTEGER, "team-slot-number", "The slot number in your team you want to remove the Pokemon from.", true),
                                new SubcommandData("swap", "Swap the positions of two Pokemon in your current team.")
                                        .addOption(OptionType.INTEGER, "team-slot-number-1", "The slot number in your team of the first Pokemon you want to swap.", true)
                                        .addOption(OptionType.INTEGER, "team-slot-number-2", "The slot number in your team of the second Pokemon you want to swap.", true),
                                new SubcommandData("clear", "Clear your current team, removing all Pokemon from it.")
                        )
                        .addSubcommandGroups(
                                new SubcommandGroupData("saved", "Manage saved team slots that can hold different Pokemon teams!")
                                        .addSubcommands(
                                                new SubcommandData("list", "View all your saved teams."),
                                                new SubcommandData("save", "Save your current team to one of your saved team slots.")
                                                        .addOption(OptionType.INTEGER, "saved-slot-number", "The slot number you want to save your current team to.", true),
                                                new SubcommandData("load", "Load a saved team as your current team.")
                                                        .addOption(OptionType.INTEGER, "saved-slot-number", "The slot number you want to load into your current team.", true),
                                                new SubcommandData("delete", "Clear a saved team slot.")
                                                        .addOption(OptionType.INTEGER, "saved-slot-number", "The slot number you want to clear.", true),
                                                new SubcommandData("rename", "Rename a saved team slot.")
                                                        .addOption(OptionType.INTEGER, "saved-slot-number", "The slot number you want to rename.", true)
                                                        .addOption(OptionType.STRING, "saved-slot-name", "The new name you want to give the saved team slot.", true)
                                        )
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(!subcommand.equals("view") && DuelHelper.isInDuel(this.player.getId())) return this.error("You cannot edit your team while in a Duel.");

        PlayerTeam team = this.playerData.getTeam();

        //Default Team Commands
        if(event.getSubcommandGroup() == null)
        {
            if(subcommand.equals("view"))
            {
                if(DuelHelper.isInDuel(this.player.getId()))
                {
                    List<Pokemon> duelTeam = DuelHelper.instance(this.player.getId()).getPlayer(this.player.getId()).team;

                    List<String> teamContents = new ArrayList<>();
                    for(int i = 0; i < duelTeam.size(); i++)
                    {
                        Pokemon p = duelTeam.get(i);

                        int currentHP = p.getStat(Stat.HP);
                        int maxHP = p.getMaxHealth();
                        float ratio = (float)currentHP / maxHP;

                        String status = ratio > 0.6 ? "🟢" : (ratio > 0 ? "🟡" : "🔴");
                        String name = p.hasNickname() ? p.getDisplayName() + " (" + p.getName() + ")" : p.getName();
                        String health = p.isFainted() ? "FAINTED" : currentHP + " / " + maxHP + " HP";

                        teamContents.add((i + 1) + ": %s **%s** – `%s`".formatted(status, name, health));
                    }

                    this.embed
                            .setTitle(this.player.getName() + "'s Team")
                            .setDescription("Since you're in a Duel, the team view below has been modified.")
                            .addField("Team", String.join("\n", teamContents), false);
                }
                else
                {
                    List<Pokemon> teamPokemon = team.getActiveTeamPokemon();

                    List<String> teamContents = new ArrayList<>();
                    if(team.isEmpty()) teamContents.add("***Your current team is empty. Use `/team add` to add Pokemon to your team.***");
                    else for(int i = 0; i < teamPokemon.size(); i++)
                    {
                        Pokemon p = teamPokemon.get(i);
                        String name = p.hasNickname() ? p.getDisplayName() + " (" + p.getName() + ")" : p.getName();

                        teamContents.add((i + 1) + ": **%s**\t\t(Number: %s)".formatted(name, 1 + this.playerData.getPokemonList().indexOf(p.getUUID())));
                    }
                    if(!team.isEmpty()) teamContents.add("\nStandard Restrictions Met: " + (TeamRestrictionRegistry.STANDARD.validate(teamPokemon) ? ":white_check_mark:" : ":x:"));

                    this.embed.setTitle(this.player.getName() + "'s Team")
                            .setDescription("""
                                    This is your Pokemon team, which will be used in Duels.
                                    Also shown is if your team meets the *standard team restrictions*.
                                    These are active for many kinds of Duels, and have to do with the amounts of Mega, Legendary, Mythical, and Ultra Beast Pokemon on your team.
                                    """)
                            .addField("Team", String.join("\n", teamContents), false)
                            .addField("Team Commands", """
                                    Here are some commands you can use to edit and manage your Pokemon team:
                                    `/team add` or `/team remove` – Add & Remove Pokemon from your team.
                                    `/team swap` – Swap the positions of two Pokemon in your team.
                                    `/team clear` – Clear your team, removing all Pokemon from it.
                                    
                                    You can also save teams, letting create multiple Pokemon teams for different purposes. You can switch between them easily by loading a particular slot.
                                    Additionally, you can set names for your saved teams to make it easier to distinguish between them.
                                    Saved team subcommands can be accessed with: `/team saved`.
                                    """, false);
                }
            }
            else if(subcommand.equals("add"))
            {
                OptionMapping pokemonNumberOption = Objects.requireNonNull(event.getOption("pokemon-number"));
                int number = pokemonNumberOption.getAsInt();

                if(number < 1 || number > this.playerData.getPokemonList().size()) return this.error("Invalid Pokemon number!");

                Pokemon p = Pokemon.build(this.playerData.getPokemonList().get(number - 1));

                if(team.contains(p.getUUID())) return this.error(p.getName() + " is already in your team.");
                else if(team.isMaxSize()) return this.error("Your team is full! Remove a Pokemon from your team to add another.");
                else
                {
                    team.add(p.getUUID());

                    this.response = "**" + p.getName() + "** has been *added* to your team!";
                }
            }
            else if(subcommand.equals("remove"))
            {
                OptionMapping teamSlotOption = Objects.requireNonNull(event.getOption("team-slot-number"));
                int teamNumber = teamSlotOption.getAsInt();

                if(team.isEmpty()) return this.error("Your team is empty! Add Pokemon using `/team add`.");
                else if(teamNumber < 1 || teamNumber > team.getActiveTeam().size()) return this.error("Invalid team slot number.");

                Pokemon p = Pokemon.build(team.getActiveTeam().get(teamNumber - 1));

                team.remove(teamNumber - 1);

                this.response = "**" + p.getName() + "** has been *removed* from your team!";
            }
            else if(subcommand.equals("swap"))
            {
                OptionMapping teamSlotOption1 = Objects.requireNonNull(event.getOption("team-slot-number-1"));
                OptionMapping teamSlotOption2 = Objects.requireNonNull(event.getOption("team-slot-number-2"));
                int teamNumber1 = teamSlotOption1.getAsInt();
                int teamNumber2 = teamSlotOption2.getAsInt();

                if(team.isEmpty()) return this.error("Your team is empty! Add Pokemon using `/team add`.");
                else if(team.size() == 1) return this.error("Your team only contains 1 Pokemon! You cannot swap unless there are at least 2 Pokemon in your team.");
                else if(teamNumber1 < 1 || teamNumber1 > team.getActiveTeam().size() || teamNumber2 < 1 || teamNumber2 > team.getActiveTeam().size()) return this.error("Invalid team slot number provided.");
                else if(teamNumber1 == teamNumber2) return this.error("You must provide two different team slot numbers.");

                Pokemon p1 = Pokemon.build(team.getActiveTeam().get(teamNumber1 - 1));
                Pokemon p2 = Pokemon.build(team.getActiveTeam().get(teamNumber2 - 1));

                team.swap(teamNumber1 - 1, teamNumber2 - 1);

                this.response = "**" + p1.getName() + "** has *swapped* positions with **" + p2.getName() + "** in your team!";
            }
            else if(subcommand.equals("clear"))
            {
                if(team.isEmpty()) return this.error("Your team is empty.");

                team.clear();

                this.response = "Your team has been *cleared*!";
            }

            if(!subcommand.equals("view")) this.playerData.updateTeam();
        }
        //Saved Team Commands
        else if(event.getSubcommandGroup().equals("saved"))
        {
            if(subcommand.equals("list"))
            {
                for(int i = 0; i < team.getSavedTeams().size(); i++)
                {
                    PlayerTeam.SavedTeam savedTeam = team.getSavedTeams().get(i);

                    String fieldName = "Slot " + (i + 1) + ": \"" + savedTeam.getName() + "\"";
                    String content = savedTeam.isEmpty() ? "**Empty**" : "Pokemon:\n" + savedTeam.getTeam().stream().map(id -> {
                        Pokemon p = Pokemon.build(id);
                        return "Level " + p.getLevel() + " **" + (p.hasNickname() ? p.getNickname() + " (" + p.getName() + ")" : p.getName()) + "** (#" + (this.playerData.getPokemonList().indexOf(p.getUUID()) + 1) + ")";
                    }).collect(Collectors.joining("\n"));

                    this.embed.addField(fieldName, content, false);
                }

                this.embed
                        .setTitle(this.player.getName() + "'s Saved Teams")
                        .setDescription("""
                                Here are your saved teams.
                                `/team saved save` will save your current team to specific slot.
                                `/team saved load` will load a team from a specific slot as your active team.
                                `/team saved rename` will rename a saved slot.
                                """);
            }
            else
            {
                OptionMapping slotOption = Objects.requireNonNull(event.getOption("saved-slot-number"));
                int slot = slotOption.getAsInt();

                if(slot < 1 || slot > team.getSavedTeams().size()) return this.error("Invalid saved slot number provided.");
                else if(subcommand.equals("save"))
                {
                    if(team.isEmpty()) return this.error("Unable to save current team! Your current team is empty. If you're trying to delete a saved team, use `/team saved delete`.");

                    team.save(slot - 1);
                    this.response = "Successfully saved your team to slot " + team.getSavedTeam(slot - 1).getName() + "!";
                }
                else if(subcommand.equals("load"))
                {
                    if(team.getSavedTeam(slot - 1).isEmpty()) return this.error("Unable to load saved team - slot " + team.getSavedTeam(slot - 1).getName() + " is empty.");

                    team.load(slot - 1);
                    this.response = "Successfully loaded your team from slot " + team.getSavedTeam(slot - 1).getName() + "!";
                }
                else if(subcommand.equals("delete"))
                {
                    if(team.getSavedTeam(slot - 1).isEmpty()) return this.error("Unable to delete saved team – slot " + team.getSavedTeam(slot - 1).getName() + " is empty.");

                    team.clear(slot - 1);
                    this.response = "Successfully deleted your team from slot " + team.getSavedTeam(slot - 1).getName() + "!";
                }
                else if(subcommand.equals("rename"))
                {
                    OptionMapping nameOption = Objects.requireNonNull(event.getOption("saved-slot-name"));
                    String name = nameOption.getAsString();

                    if(name.length() < 3 || name.length() > Global.MAX_NAME_LIMIT) return this.error("Saved slot name must be at least 3 characters long, but no more than " + Global.MAX_NAME_LIMIT + " characters.");

                    team.setSlotName(slot - 1, name);
                    this.response = "Successfully renamed slot #" + slot + " to \"" + name + "\"!";
                }

                this.playerData.updateTeam();
            }
        }

        return true;
    }
}
