package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
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

            this.players[0].data.updateBountyProgression(ObjectiveType.COMPLETE_GAUNTLET_LEVELS);

            if(this.level > 3) this.players[0].data.addExp(this.level * 10);

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

            Achievements.grant(this.players[0].ID, Achievements.GAUNTLET_FIRST_COMPLETED, this.event);
            if(this.level >= 3) Achievements.grant(this.players[0].ID, Achievements.GAUNTLET_FIRST_REACHED_LEVEL_3, this.event);
            if(this.level >= 5) Achievements.grant(this.players[0].ID, Achievements.GAUNTLET_FIRST_REACHED_LEVEL_5, this.event);
            if(this.level >= 7) Achievements.grant(this.players[0].ID, Achievements.GAUNTLET_FIRST_REACHED_LEVEL_7, this.event);
            if(this.level >= 10) Achievements.grant(this.players[0].ID, Achievements.GAUNTLET_FIRST_REACHED_LEVEL_10, this.event);

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
