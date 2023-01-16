package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.Player;
import com.calculusmaster.pokecord.game.duel.players.TrainerPlayer;
import com.calculusmaster.pokecord.game.duel.players.UserPlayer;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerData;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerManager;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.LinkedHashMap;
import java.util.Random;
import java.util.SplittableRandom;

import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.DUELS;
import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.DuelStatus;

public class TrainerDuel extends Duel
{
    public static Duel create(String playerID, MessageReceivedEvent event, TrainerData trainer)
    {
        TrainerDuel duel = new TrainerDuel();

        duel.setStatus(DuelStatus.WAITING);
        duel.setTurn();
        duel.addChannel(event.getTextChannel());
        duel.setPlayers(playerID, trainer.getName(), trainer.getTeam().size());
        duel.setTrainer(trainer);
        duel.limitPlayerPokemon(trainer.getAveragePokemonLevel());
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
            TrainerPlayer botTrainer = this.getTrainer();

            embed.setDescription("You defeated " + botTrainer.getName() + "!");

            Achievements.grant(this.players[0].ID, Achievements.WON_FIRST_TRAINER_DUEL, null);
            player.data.updateBountyProgression(ObjectiveType.WIN_TRAINER_DUEL);

            //Regular Trainer

            if(!player.data.hasDefeatedTrainer(botTrainer.ID))
            {
                player.data.addDefeatedTrainer(botTrainer.ID);

                int trainerClass = botTrainer.getData().getTrainerClass();

                if(player.data.hasDefeatedAllTrainersOfClass(trainerClass))
                {
                    //TODO: Rewards for defeating all trainers of a class

                    int credits = switch(trainerClass) {
                        case 1 -> 500;
                        case 2 -> 1250;
                        case 3 -> 3000;
                        case 4 -> 4500;
                        case 5 -> 7500;
                        case 6 -> 7000;
                        default -> throw new IllegalStateException("Invalid Trainer Class: " + trainerClass);
                    };

                    player.data.changeCredits(credits);

                    String trainerClassRoman = TrainerManager.getRoman(trainerClass);
                    player.data.directMessage("You've defeated all Class %s Trainers! Reward: %s Credits!".formatted(trainerClassRoman, credits));
                }

                if(player.data.hasDefeatedAllTrainerClasses())
                {
                    //TODO: Rewards for defeating all trainers totally

                    player.data.directMessage("You've defeated all the current rotation of Trainers! Congratulations! Reward: ?? Credits!");

                    Achievements.grant(this.players[0].ID, Achievements.DEFEATED_ROTATION_TRAINERS, null);
                }
            }

            player.data.getStatistics().incr(PlayerStatistic.TRAINER_DUELS_WON);

        }
        //Player lost
        else embed.setDescription("You were defeated by " + this.players[1].getName() + ". Take note of the team used by the Trainer, and develop a counter-strategy!");

        this.uploadEVs(0);
        this.uploadExperience();

        this.getUser().data.updateBountyProgression(ObjectiveType.COMPLETE_TRAINER_DUEL);
        this.getUser().data.getStatistics().incr(PlayerStatistic.TRAINER_DUELS_COMPLETED);

        this.sendEmbed(embed.build());
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

            if(((TrainerPlayer)this.players[1]).hasZCrystal(ZCrystal.getCrystalOfType(move.getType()))) type = 'z';
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
        this.players = new Player[]{new UserPlayer(PlayerDataQuery.ofNonNull(player1ID), size), null};
        this.size = size;
    }

    private void setTrainer(TrainerData data)
    {
        this.players[1] = new TrainerPlayer(data);

        //TODO: Determine EV handling for trainer pokemon
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

    protected UserPlayer getUser()
    {
        return (UserPlayer)this.players[0];
    }

    protected TrainerPlayer getTrainer()
    {
        return (TrainerPlayer)this.players[1];
    }
}
