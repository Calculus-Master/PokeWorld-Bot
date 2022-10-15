package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.WildPokemon;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;

import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.DUELS;

//PVE Duel to Earn a Typed Z-Crystal
public class ZTrialDuel extends WildDuel
{
    private Type type;

    public static Duel create(String playerID, MessageReceivedEvent event, Type type)
    {
        ZTrialDuel duel = new ZTrialDuel();

        duel.setStatus(DuelHelper.DuelStatus.WAITING);
        duel.setEvent(event);
        duel.setPlayers(playerID, "BOT", 1);
        duel.setWildPokemon(type);
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
        ZCrystal crystal = Objects.requireNonNull(ZCrystal.getCrystalOfType(this.type));

        //Player won
        if(this.getWinner().ID.equals(this.players[0].ID))
        {
            this.onWildDuelWon(true);

            this.players[0].data.addZCrystal(crystal.toString());
            Achievements.grant(this.players[this.current].ID, Achievements.ACQUIRED_FIRST_TYPED_ZCRYSTAL, this.event);

            embed.setDescription("You won! You acquired `%s`!".formatted(crystal.getStyledName()));
        }
        //Player lost
        else
        {
            this.players[0].data.updateBountyProgression(ObjectiveType.COMPLETE_WILD_DUEL);
            embed.setDescription("You lost! You weren't able to earn `%s`.".formatted(crystal.getStyledName()));
        }

        this.event.getChannel().sendMessageEmbeds(embed.build()).queue();
        DuelHelper.delete(this.players[0].ID);
    }

    private void setWildPokemon(Type type)
    {
        this.players[1] = new WildPokemon(type, Math.max(80, this.players[0].active.getLevel()));
        this.type = type;
    }

    //TODO: Edit getImage and increase the Trial pokemon's size
}
