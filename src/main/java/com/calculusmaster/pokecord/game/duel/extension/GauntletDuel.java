package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.DUELS;
import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.DuelStatus;

public class GauntletDuel extends WildDuel
{
    private int level;

    public static GauntletDuel start(String playerID, MessageReceivedEvent event)
    {
        GauntletDuel duel = new GauntletDuel();

        duel.setStatus(DuelStatus.WAITING);
        duel.setEvent(event);
        duel.setPlayers(playerID, "BOT", 1);
        duel.setWildPokemon("");
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
            this.onWildDuelWon(false);

            this.level++;

            this.setWildPokemon("");
            this.setDuelPokemonObjects(1);
            this.queuedMoves.clear();
            this.results.clear();

            embed.setDescription("You won! Gauntlet Level increased to %s!".formatted(this.level));
        }
        //Player lost
        else
        {
            int xp = this.level * 100;
            int credits = (int)(Math.pow(this.level, 1.05) * 100);

            this.players[0].data.changeCredits(credits);
            this.players[0].data.addPokePassExp(xp, this.event);

            embed.setDescription("You lost! Your Gauntlet Run ended at Level %s! You earned %s credits!".formatted(this.level, credits));

            DuelHelper.delete(this.players[0].ID);
        }

        this.event.getChannel().sendMessageEmbeds(embed.build()).queue();
        if(this.getWinner().ID.equals(this.players[0].ID)) this.sendTurnEmbed();
    }

    @Override
    public void setDefaults()
    {
        super.setDefaults();
        this.level = 0;
    }
}
