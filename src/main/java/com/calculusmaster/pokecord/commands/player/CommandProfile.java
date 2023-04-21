package com.calculusmaster.pokecord.commands.player;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.player.components.PlayerStatisticsRecord;
import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Objects;

public class CommandProfile extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("profile")
                .withConstructor(CommandProfile::new)
                .withFeature(Feature.VIEW_PROFILE)
                .withCommand(Commands
                        .slash("profile", "View your player profile and statistics!")
                        .addOption(OptionType.USER, "player", "Optional: View the profile of another %s player.".formatted(Pokeworld.NAME), false)
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        OptionMapping playerOption = event.getOption("player");

        PlayerData target = this.playerData;
        if(playerOption != null)
        {
            String targetID = playerOption.getAsUser().getId();
            if(!PlayerData.isRegistered(targetID)) return this.error("This player has not begun their journey with " + Pokeworld.NAME + "!");
            else target = Objects.requireNonNull(PlayerData.build(targetID));
        }

        Member targetMember = Objects.requireNonNull(event.getGuild()).retrieveMemberById(target.getID()).complete();

        if(targetMember == null) return this.error("That user is not a part of this Discord server.");

        PlayerStatisticsRecord stats = target.getStatistics();

        this.embed
                .setTitle(target.getUsername() + "'s " + Pokeworld.NAME + " Profile")
                .setDescription("""
                        This is what %s has achieved so far!
                        
                        **Pokemon Mastery Level %s**
                        """.formatted(target.getUsername(), target.getLevel()))
                .setThumbnail(targetMember.getEffectiveAvatarUrl())

                .addField("Joined Discord", "<t:" + targetMember.getTimeCreated().toEpochSecond() + ":F>", true)
                .addField("Joined Server", "<t:" + targetMember.getTimeJoined().toEpochSecond() + ":F>", true)
                .addField("Joined " + Pokeworld.NAME, "<t:" + target.getJoinTime() + ":F>", true)

                .addField("Pokemon Statistics", """
                        *Caught:* %d
                        *Released:* %d
                        *Evolved:* %d
                        *Bred:* %d
                        *Dynamaxed:* %d
                        *Prestiged:* %d
                        """.formatted(stats.get(StatisticType.POKEMON_CAUGHT), stats.get(StatisticType.POKEMON_RELEASED), stats.get(StatisticType.POKEMON_EVOLVED), stats.get(StatisticType.POKEMON_BRED), stats.get(StatisticType.POKEMON_DYNAMAXED), stats.get(StatisticType.POKEMON_PRESTIGED)),
                        true)

                .addField("Move Statistics", """
                        *Moves Used:* %d
                        *Z-Moves Used:* %d
                        *Max Moves Used:* %d
                        *Pokemon Defeated*: %d
                        *Pokemon Fainted*: %d
                        """.formatted(stats.get(StatisticType.MOVES_USED), stats.get(StatisticType.ZMOVES_USED), stats.get(StatisticType.MAX_MOVES_USED), stats.get(StatisticType.POKEMON_DEFEATED), stats.get(StatisticType.POKEMON_FAINTED)),
                        true)

                .addField("Duel Statistics", """
                        *PvP Duels* | Won: %d | Total: %d
                        *Wild Duels* | Won: %d | Total: %d
                        *Trainer Duels* | Won: %d | Total: %d
                        *Elite Duels* | Won: %d | Total: %d
                        *Raid Duels* | Won: %d | Total: %d | Raid Pokemon Caught: %s
                        """.formatted(stats.get(StatisticType.PVP_DUELS_WON), stats.get(StatisticType.PVP_DUELS_COMPLETED), stats.get(StatisticType.WILD_DUELS_WON), stats.get(StatisticType.WILD_DUELS_COMPLETED), stats.get(StatisticType.TRAINER_DUELS_WON), stats.get(StatisticType.TRAINER_DUELS_COMPLETED), stats.get(StatisticType.ELITE_DUELS_WON), stats.get(StatisticType.ELITE_DUELS_COMPLETED), stats.get(StatisticType.RAIDS_WON), stats.get(StatisticType.RAIDS_COMPLETED), stats.get(StatisticType.RAID_POKEMON_CAUGHT)),
                        false)

                .addField("Inventory Statistics", """
                        *Credits Earned:* %s
                        *Credits Spent:* %s
                        """.formatted(stats.get(StatisticType.CREDITS_EARNED), stats.get(StatisticType.CREDITS_SPENT)),
                        false)
        ;

        return true;
    }
}
