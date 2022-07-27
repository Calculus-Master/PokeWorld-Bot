package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.Player;
import com.calculusmaster.pokecord.game.duel.players.Trainer;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.player.level.PMLExperience;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;

import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.DUELS;
import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.DuelStatus;

public class TrainerDuel extends Duel
{
    public static Duel create(String playerID, MessageReceivedEvent event, Trainer.TrainerInfo trainer)
    {
        TrainerDuel duel = new TrainerDuel();

        duel.setStatus(DuelStatus.WAITING);
        duel.setEvent(event);
        duel.setPlayers(playerID, trainer.name, trainer.pokemon.size());
        duel.setTrainer(trainer);
        duel.limitPlayerPokemon(trainer.pokemonLevel);
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
        if(this.getWinner().ID.equals(this.players[0].ID))
        {
            Trainer botTrainer = (Trainer)this.players[1];
            String bot = botTrainer.info.name;

            embed.setDescription("You defeated " + bot + "!");

            Achievements.grant(this.players[0].ID, Achievements.WON_FIRST_TRAINER_DUEL, this.event);
            this.players[0].data.updateBountyProgression(ObjectiveType.WIN_TRAINER_DUEL);

            //Regular Daily Trainer

            List<String> playersDefeatedBot = botTrainer.info.playersDefeated;

            if(!playersDefeatedBot.contains(this.players[0].ID))
            {
                playersDefeatedBot.add(this.players[0].ID);
                Trainer.addPlayerDefeated(botTrainer.ID, this.players[0].ID);

                boolean dailyComplete = true;
                for(Trainer.TrainerInfo ti : Trainer.DAILY_TRAINERS) if(!ti.playersDefeated.contains(this.players[0].ID)) dailyComplete = false;

                if(dailyComplete)
                {
                    int winCredits = (new Random().nextInt(501) + 500) * Trainer.DAILY_TRAINERS.size();
                    this.players[0].data.changeCredits(winCredits);
                    this.players[0].data.addExp(PMLExperience.DUEL_TRAINER_DAILY_COMPLETE, 100);
                    this.event.getChannel().sendMessage(this.players[0].data.getMention() + ": You defeated all of today's trainers! You earned a bonus " + winCredits + " credits!").queue();

                    Achievements.grant(this.players[0].ID, Achievements.DEFEATED_DAILY_TRAINERS, this.event);
                }
            }

            this.players[0].data.getStatistics().incr(PlayerStatistic.TRAINER_DUELS_WON);

        }
        //Player lost
        else embed.setDescription("You were defeated by " + ((Trainer)this.players[1]).info.name + "!");

        this.uploadEVs(0);
        this.uploadExperience();

        this.players[0].data.updateBountyProgression(ObjectiveType.COMPLETE_TRAINER_DUEL);
        this.players[0].data.getStatistics().incr(PlayerStatistic.TRAINER_DUELS_COMPLETED);

        this.event.getChannel().sendMessageEmbeds(embed.build()).queue();
        DuelHelper.delete(this.players[0].ID);
    }

    @Override
    public void sendTurnEmbed()
    {
        super.sendTurnEmbed();

        if(!this.isComplete() && this.players[1].active.isFainted())
        {
            this.submitMove(this.players[1].ID, 1, 'm');
            this.checkReady();
        }
    }

    @Override
    public void checkReady()
    {
        if(this.queuedMoves.containsKey(this.players[0].ID))
        {
            turnHandler();
        }
    }

    @Override
    protected void turnSetup()
    {
        super.turnSetup();

        int index;
        char type;

        if(this.players[0].active.isFainted() && !this.players[1].active.isFainted())
        {
            index = -1;
            type = 'i';
        }
        //If active is fainted, AI needs to swap
        else if(this.players[1].active.isFainted())
        {
            int ind = -1;
            for(int i = 0; i < this.players[1].team.size(); i++)
            {
                if(!this.players[1].team.get(i).isFainted())
                {
                    ind = i + 1;
                    i = this.players[1].team.size();
                }
            }

            index = ind;
            type = 's';
        }
        //Z-Move
        else if(!this.players[1].usedZMove)
        {
            int ind = new Random().nextInt(4);
            Move move = new Move(this.players[1].active.getMoves().get(ind));

            index = ind + 1;

            if(this.players[1].data.hasZCrystal(ZCrystal.getCrystalOfType(move.getType()).getStyledName())) type = 'z';
            else type = 'm';

        }
        //Dynamax
        else if(!this.players[1].usedDynamax && new SplittableRandom().nextInt(100) < 33)
        {
            index = new SplittableRandom().nextInt(4) + 1;
            type = 'd';
        }
        //Normal Move
        else
        {
            index = new SplittableRandom().nextInt(4) + 1;
            type = 'm';
        }

        this.submitMove(this.players[1].ID, index, type);
    }

    protected void limitPlayerPokemon(int level)
    {
        for(int i = 0; i < this.players[0].team.size(); i++)
        {
            if(this.players[0].team.get(i).getLevel() >= level + 5)
            {
                this.players[0].team.get(i).setLevel(level + 5);
                this.players[0].team.get(i).setHealth(this.players[0].team.get(0).getStat(Stat.HP));
            }
        }

        this.players[0].active = this.players[0].team.get(0);
    }

    @Override
    public void setPlayers(String player1ID, String player2ID, int size)
    {
        this.players = new Player[]{new Player(player1ID, size), null};
        this.size = size;
    }

    private void setTrainer(Trainer.TrainerInfo info)
    {
        this.players[1] = Trainer.create(info);

        int highest = this.players[0].team.get(0).getEVTotal();
        LinkedHashMap<Stat, Integer> evs = this.players[0].team.get(0).getEVs();
        //Copy EVs
        for(int i = 0; i < this.players[0].team.size(); i++)
        {
            if(this.players[1].team.get(i).getEVTotal() > highest)
            {
                highest = this.players[0].team.get(i).getEVTotal();
                evs = this.players[0].team.get(i).getEVs();
            }
        }

        for(int i = 0; i < this.players[1].team.size(); i++)
        {
            this.players[1].team.get(i).setEVs(evs);
            this.players[1].team.get(i).setHealth(this.players[1].team.get(i).getStat(Stat.HP));
        }

        this.players[1].active = this.players[1].team.get(0);
    }
}
