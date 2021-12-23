package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.Player;
import com.calculusmaster.pokecord.game.duel.players.WildPokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.DUELS;
import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.DuelStatus;

//PVE Duel - 1v1 Against a Wild Pokemon for EXP and EV Grinding
public class WildDuel extends Duel
{
    public static Duel create(String playerID, MessageReceivedEvent event, String pokemon)
    {
        WildDuel duel = new WildDuel();

        duel.setStatus(DuelStatus.WAITING);
        duel.setEvent(event);
        duel.setPlayers(playerID, "BOT", 1);
        duel.setWildPokemon(pokemon);
        duel.setDefaults();
        duel.setDuelPokemonObjects(0);
        duel.setDuelPokemonObjects(1);

        DUELS.add(duel);
        return duel;
    }

    @Override
    public void turnHandler()
    {
        this.turnSetup();

        //Status Conditions
        if(!this.players[0].active.isFainted()) this.data(0).canUseMove = this.statusConditionEffects(0);
        if(!this.players[1].active.isFainted()) this.data(1).canUseMove = this.statusConditionEffects(1);

        //No swapping, No Z-Moves
        if(!this.isComplete())
        {
            //Get moves - [1] is the bot so get a random move
            this.moveAction(0);

            List<Move> botMoves = new ArrayList<>();
            for(String s : this.players[1].active.getAllMoves()) if(Move.isImplemented(s)) botMoves.add(new Move(s));

            //TODO: Better AI
            Move mostDamage = botMoves.get(0);
            for(Move m : botMoves) if(m.getPower() > mostDamage.getPower()) mostDamage = m;

            if(this.players[1].active.getHealth() <= this.players[1].active.getStat(Stat.HP) / 4) this.players[1].move = mostDamage;
            else this.players[1].move = botMoves.get(new Random().nextInt(botMoves.size()));

            this.fullMoveTurn();
        }

        this.onTurnEnd();
    }

    @Override
    public void sendWinEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();

        //Player won
        if(this.getWinner().ID.equals(this.players[0].ID))
        {
            this.onWildDuelWon(true);

            embed.setDescription("You won! Your " + this.players[0].active.getName() + " earned some EVs!");
        }
        //Player lost
        else
        {
            this.players[0].data.updateBountyProgression(ObjectiveType.COMPLETE_WILD_DUEL);
            embed.setDescription("You lost! Your " + this.players[0].active.getName() + " didn't earn any EVs...");
        }

        this.event.getChannel().sendMessageEmbeds(embed.build()).queue();
        DuelHelper.delete(this.players[0].ID);
    }

    protected void onWildDuelWon(boolean evs)
    {
        int exp = this.players[0].active.getDuelExp(this.players[1].active);
        Pokemon p = this.players[0].data.getSelectedPokemon();
        p.addExp(exp);

        if(evs) Pokemon.updateEVs(this.players[this.current].active);
        Pokemon.updateExperience(p);

        Achievements.grant(this.players[0].ID, Achievements.WON_FIRST_WILD_DUEL, this.event);
        this.players[0].data.addExp(10, 25);
        this.players[0].data.getStats().incr(PlayerStatistic.WILD_DUELS_WON);
        this.players[0].data.updateBountyProgression(b -> {
            if(b.getType().equals(ObjectiveType.WIN_WILD_DUEL) || b.getType().equals(ObjectiveType.COMPLETE_WILD_DUEL)) b.update();
        });
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
    protected String getHB(int p)
    {
        StringBuilder sb = new StringBuilder();

        if(this.isNonBotPlayer(p)) sb.append(this.players[p].data.getUsername()).append("'s ");
        else sb.append("The Wild ");

        sb.append(this.players[p].active.getName()).append(": ");

        if(this.players[p].active.isFainted()) sb.append("FAINTED");
        else sb.append(this.players[p].active.getHealth()).append(" / ").append(this.players[p].active.getStat(Stat.HP)).append(" HP ").append(this.players[p].active.getActiveStatusConditions());

        return sb.toString();
    }

    @Override
    public boolean isComplete()
    {
        return this.players[0].active.isFainted() || this.players[1].active.isFainted();
    }

    @Override
    public void setPlayers(String player1ID, String player2ID, int size)
    {
        this.players = new Player[]{new Player(player1ID, size), null};
    }

    public void setWildPokemon(String pokemon)
    {
        int levelBuff = this.players[0].active.getLevel() + (new Random().nextInt(5) + 2);
        if(pokemon.equals("")) this.players[1] = new WildPokemon(this.players[0].active.getLevel() + levelBuff);
        else this.players[1] = new WildPokemon(pokemon, this.players[0].active.getLevel() + levelBuff);

        this.players[1].active.statBuff = this.players[1].active.getLevel() > 60 ? Math.random() * 1.5 + 1 : 1;
        this.players[1].active.setLevel(this.players[0].active.getLevel());
        this.players[1].active.setHealth(this.players[1].active.getStat(Stat.HP));
    }
}
