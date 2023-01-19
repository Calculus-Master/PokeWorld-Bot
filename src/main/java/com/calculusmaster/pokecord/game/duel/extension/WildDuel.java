package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.Player;
import com.calculusmaster.pokecord.game.duel.players.UserPlayer;
import com.calculusmaster.pokecord.game.duel.players.WildPlayer;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonAI;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
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
        duel.setTurn();
        duel.addChannel(event.getChannel().asTextChannel());
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

        //No swapping, No Z-Moves
        if(!this.isComplete())
        {
            //Get moves - [1] is the bot so get a random move
            this.moveAction(0);

            List<Move> botMoves = new ArrayList<>();
            for(String s : this.players[1].active.allMoves()) if(Move.isImplemented(s)) botMoves.add(new Move(s));

            //TODO: Better AI (progress made 5-11-22)
            if(this.players[1].active.getHealth() <= this.players[1].active.getStat(Stat.HP) / 4) this.players[1].move = new Move(new PokemonAI(this.players[1].active).getHighestDamageMove(this.players[0].active));
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
        if(this.getWinner() instanceof UserPlayer player)
        {
            this.onWildDuelWon(true);

            player.data.getStatistics().incr(PlayerStatistic.WILD_DUELS_WON);

            embed.setDescription("You won! Your " + this.players[0].active.getName() + " earned some EVs!");
        }
        //Player lost
        else
        {
            this.getUser().data.updateBountyProgression(ObjectiveType.COMPLETE_WILD_DUEL);

            embed.setDescription("You lost! Your " + this.getUser().active.getName() + " didn't earn any EVs...");
        }

        this.getUser().data.getStatistics().incr(PlayerStatistic.WILD_DUELS_COMPLETED);

        this.sendEmbed(embed.build());
        DuelHelper.delete(this.players[0].ID);
    }

    protected void onWildDuelWon(boolean evs)
    {
        int exp = this.players[0].active.getDefeatExp(this.players[1].active);
        Pokemon p = this.getUser().data.getSelectedPokemon();
        p.addExp(exp);

        if(evs) this.getUser().active.updateEVs();
        p.updateExperience();

        Achievements.grant(this.players[0].ID, Achievements.WON_FIRST_WILD_DUEL, null);
        this.getUser().data.updateBountyProgression(b -> {
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

        if(this.players[p] instanceof UserPlayer player) sb.append(player.getName()).append("'s ");

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
        PlayerDataQuery p = PlayerDataQuery.ofNonNull(player1ID);

        this.players = new Player[]{new UserPlayer(p, p.getSelectedPokemon()), null};
    }

    public void setWildPokemon(String pokemon)
    {
        int level = Math.min(100, this.getUser().active.getLevel() + (new Random().nextInt(5) + 1));

        if(pokemon.equals("")) this.players[1] = new WildPlayer(level);
        else this.players[1] = new WildPlayer(pokemon, level);

        this.players[1].active.getBoosts().setStatBoost(level > 60 ? Math.random() * 1.5 + 1 : 1);
        this.players[1].active.setHealth(this.players[1].active.getStat(Stat.HP));
    }

    protected UserPlayer getUser()
    {
        return (UserPlayer)this.players[0];
    }

    protected WildPlayer getWildPokemon()
    {
        return (WildPlayer)this.players[1];
    }
}
