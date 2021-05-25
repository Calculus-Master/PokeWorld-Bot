package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;

public class ZMoves
{
    public String StokedSparksurfer(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.addStatusCondition(StatusCondition.PARALYZED);

        return Move.simpleDamageMove(user, opponent, duel, move) + " " + opponent.getName() + " is paralyzed!";
    }

    public String SinisterArrowRaid(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ExtremeEvoboost(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.ATK, 2);
        user.changeStatMultiplier(Stat.DEF, 2);
        user.changeStatMultiplier(Stat.SPATK, 2);
        user.changeStatMultiplier(Stat.SPDEF, 2);
        user.changeStatMultiplier(Stat.SPD, 2);

        return user.getName() + "'s stats were all raised by 2 stages!";
    }

    public String MaliciousMoonsault(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ClangorousSoulblaze(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.ATK, 1);
        user.changeStatMultiplier(Stat.DEF, 1);
        user.changeStatMultiplier(Stat.SPATK, 1);
        user.changeStatMultiplier(Stat.SPDEF, 1);
        user.changeStatMultiplier(Stat.SPD, 1);

        return Move.simpleDamageMove(user, opponent, duel, move) + " " + user.getName() + "'s stats were all raised by 1 stage!";
    }

    public String MenacingMoonrazeMaelstrom(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SplinteredStormshards(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SoulStealing7StarStrike(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String GenesisSupernova(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String LetsSnuggleForever(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Catastropika(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String TenMillionVoltThunderbolt(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setCrit(12);

        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        user.setCrit(12);

        return move.getDamageResult(opponent, damage);
    }

    public String OceanicOperetta(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String PulverizingPancake(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SearingSunrazeSmash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String GuardianOfAlola(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = opponent.getStat(Stat.HP) * 3 / 4;
        opponent.damage(damage, duel);

        return move.getDamageResult(opponent, damage);
    }

    public String LightThatBurnsTheSky(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }
}
