package com.calculusmaster.pokecord.game.duel;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.elements.Player;
import com.calculusmaster.pokecord.game.duel.elements.Trainer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
            //TODO: Should Players get credits?
            //int c = this.giveWinCredits();
            int c = 0;

            this.uploadEVs(0);

            embed.setDescription("You won and earned " + c + " credits! Your " + this.players[0].active.getName() + " earned some EVs!");
        }
        //Player lost
        else
        {
            this.uploadEVs(0);

            embed.setDescription("You lost! Your " + this.players[0].active.getName() + " didn't earn any EVs...");
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
        else if(this.players[1].data.hasZCrystal("") && !this.players[1].usedZMove) this.queuedMoves.put(this.players[1].ID, new TurnAction(ActionType.ZMOVE, new Random().nextInt(4) + 1, -1));
        //Normal Move
        else this.queuedMoves.put(this.players[1].ID, new TurnAction(ActionType.MOVE, new Random().nextInt(4) + 1, -1));
    }

    @Override
    public void setPlayers(String player1ID, String player2ID, int size)
    {
        this.players = new Player[]{new Player(player1ID, size), null};
    }

    private void setTrainer(Trainer.TrainerInfo info)
    {
        this.players[1] = Trainer.create(info);
    }
}
