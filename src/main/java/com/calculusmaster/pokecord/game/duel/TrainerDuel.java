package com.calculusmaster.pokecord.game.duel;

import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.duel.elements.Player;
import com.calculusmaster.pokecord.game.duel.elements.Trainer;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.calculusmaster.pokecord.game.duel.DuelHelper.*;

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
            //TODO: Trainer Duel per battle credits? (Bonus for defeating all is already a thing)
            //int c = this.giveWinCredits();
            int c = 0;

            this.uploadEVs(0);
            this.uploadExperience();

            embed.setDescription("You defeated " + ((Trainer)this.players[1]).info.name + "!");

            Achievements.grant(this.players[0].ID, Achievements.WON_FIRST_TRAINER_DUEL, this.event);

            String bot = ((Trainer)this.players[1]).info.name;
            List<String> playersDefeatedBot = new ArrayList<>(Trainer.PLAYER_TRAINERS_DEFEATED.get(bot));
            if(!playersDefeatedBot.contains(this.players[0].ID))
            {
                playersDefeatedBot.add(this.players[0].ID);
                Trainer.PLAYER_TRAINERS_DEFEATED.put(bot, playersDefeatedBot);

                boolean dailyComplete = true;
                for(Trainer.TrainerInfo ti : Trainer.DAILY_TRAINERS) if(!Trainer.PLAYER_TRAINERS_DEFEATED.get(ti.name).contains(this.players[0].ID)) dailyComplete = false;

                if(dailyComplete)
                {
                    //TODO: Win Credits
                    int winCredits = -1;
                    this.players[0].data.changeCredits(winCredits);
                    this.event.getChannel().sendMessage(this.players[0].data.getMention() + ": You defeated all of today's trainers! You earned a bonus " + winCredits + " credits!").queue();

                    Achievements.grant(this.players[0].ID, Achievements.DEFEATED_DAILY_TRAINERS, this.event);
                }
            }
        }
        //Player lost
        else
        {
            this.uploadEVs(0);

            embed.setDescription("You were defeated by " + ((Trainer)this.players[1]).info.name + "!");
        }

        this.event.getChannel().sendMessage(embed.build()).queue();
        DuelHelper.delete(this.players[0].ID);
    }

    @Override
    public void sendTurnEmbed()
    {
        super.sendTurnEmbed();

        if(!this.isComplete() && this.players[1].active.isFainted())
        {
            this.submitMove(this.players[1].ID, 1, false);
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

        //If active is fainted, AI needs to swap
        if(this.players[1].active.isFainted())
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

            this.queuedMoves.put(this.players[1].ID, new TurnAction(ActionType.SWAP, -1, ind));
        }
        //Z-Move
        else if(!this.players[1].usedZMove)
        {
            int index = new Random().nextInt(4);
            Move move = new Move(this.players[1].active.getLearnedMoves().get(index));
            if(this.players[1].data.hasZCrystal(ZCrystal.getCrystalOfType(move.getType()).getStyledName())) this.queuedMoves.put(this.players[1].ID, new TurnAction(ActionType.ZMOVE, index + 1, -1));
            else this.queuedMoves.put(this.players[1].ID, new TurnAction(ActionType.MOVE, index + 1, -1));

        }
        //Normal Move
        else this.queuedMoves.put(this.players[1].ID, new TurnAction(ActionType.MOVE, new Random().nextInt(4) + 1, -1));
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
    }

    private void setTrainer(Trainer.TrainerInfo info)
    {
        this.players[1] = Trainer.create(info);

        int highest = this.players[0].team.get(0).getEVTotal();
        String condensed = this.players[0].team.get(0).getVCondensed(this.players[0].team.get(0).getEVs());
        //Copy EVs
        for(int i = 0; i < this.players[0].team.size(); i++)
        {
            if(this.players[1].team.get(i).getEVTotal() > highest)
            {
                highest = this.players[0].team.get(i).getEVTotal();
                condensed = this.players[0].team.get(i).getVCondensed(this.players[0].team.get(i).getEVs());
            }
        }

        for(int i = 0; i < this.players[1].team.size(); i++)
        {
            this.players[1].team.get(i).setEVs(condensed);
            this.players[1].team.get(i).setHealth(this.players[1].team.get(i).getStat(Stat.HP));
        }

        this.players[1].active = this.players[1].team.get(0);
    }
}
