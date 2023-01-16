package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.TrainerPlayer;
import com.calculusmaster.pokecord.game.duel.players.UserPlayer;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerData;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.player.level.PMLExperience;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.DUELS;

public class EliteDuel extends TrainerDuel
{
    public static Duel create(String playerID, MessageReceivedEvent event)
    {
        EliteDuel duel = new EliteDuel();

        duel.setStatus(DuelHelper.DuelStatus.WAITING);
        duel.setTurn();
        duel.addChannel(event.getTextChannel());
        duel.setPlayers(playerID, "Elite Trainer", 6);
        duel.setEliteTrainer();
        duel.setDefaults();
        duel.setDuelPokemonObjects(0);
        duel.setDuelPokemonObjects(1);

        DUELS.add(duel);
        return duel;
    }

    @Override
    public void sendWinEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();

        //Player won
        if(this.getWinner() instanceof UserPlayer player)
        {
            Achievements.grant(this.players[0].ID, Achievements.WON_FIRST_TRAINER_DUEL, null);
            Achievements.grant(this.players[0].ID, Achievements.DEFEATED_FIRST_ELITE_TRAINER, null);

            int credits = new SplittableRandom().nextInt(500, 1000);
            player.data.changeCredits(credits);

            player.data.addExp(PMLExperience.DUEL_ELITE, 95);

            player.data.updateBountyProgression(ObjectiveType.WIN_ELITE_DUEL);

            player.data.getStatistics().incr(PlayerStatistic.ELITE_DUELS_WON);
            player.data.getStatistics().incr(PlayerStatistic.TRAINER_DUELS_WON);

            embed.setDescription("You defeated the Elite Trainer and earned " + credits + " credits!");
        }
        //Player lost
        else embed.setDescription("You were defeated by the Elite Trainer!");

        this.sendEmbed(embed.build());

        this.uploadEVs(0);
        this.uploadExperience();

        this.getUser().data.updateBountyProgression(ObjectiveType.COMPLETE_ELITE_DUEL);
        this.getUser().data.getStatistics().incr(PlayerStatistic.ELITE_DUELS_COMPLETED);

        DuelHelper.delete(this.players[0].ID);
    }

    private void setEliteTrainer()
    {
        Random r = new Random();

        List<String> pool = new ArrayList<>();
        pool.addAll(PokemonRarity.LEGENDARY);
        pool.addAll(PokemonRarity.MYTHICAL);
        pool.addAll(PokemonRarity.ULTRA_BEAST);
        pool.addAll(PokemonRarity.MEGA);

        String[] team = new String[6];
        for(int i = 0; i < 6; i++) team[i] = pool.get(r.nextInt(pool.size()));

        TrainerData elite = new TrainerData("Elite Trainer", -1, List.of(team), ZCrystal.values()[r.nextInt(18)], 100, 1.25F);

        this.players[1] = new TrainerPlayer(elite);

        for(int i = 0; i < this.players[1].team.size(); i++)
        {
            Pokemon p = this.players[1].team.get(i);

            Arrays.stream(Stat.values()).forEach(s -> {
                p.setIV(s, r.nextInt(20, 32));
                p.setEV(s, r.nextInt(50, 203));
            });

            p.setHealth(p.getStat(Stat.HP));
            p.setShiny(r.nextInt(100) < 20);

            if(r.nextFloat() < 0.2F)
            {
                List<Integer> levelPool = new ArrayList<>();
                for(int prestigeLevel = 1; prestigeLevel < p.getMaxPrestigeLevel() + 1; prestigeLevel++) for(int k = 0; k < p.getMaxPrestigeLevel() + 1 - prestigeLevel; k++) levelPool.add(prestigeLevel);
                p.setPrestigeLevel(levelPool.get(r.nextInt(levelPool.size())));
            }
        }

        this.players[1].active = this.players[1].team.get(0);
    }
}
