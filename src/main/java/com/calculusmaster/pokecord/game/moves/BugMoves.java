package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.Random;

public class BugMoves
{
    public String SilverWind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        if(new Random().nextInt(100) < 10)
        {
            user.changeStatMultiplier(Stat.ATK, 1);
            user.changeStatMultiplier(Stat.DEF, 1);
            user.changeStatMultiplier(Stat.SPATK, 1);
            user.changeStatMultiplier(Stat.SPDEF, 1);

            return move.getDamageResult(opponent, damage) + " " + user.getName() + "'s Attack, Defense, Special Attack and Special Defense rose by 1 stage!";
        }

        return move.getDamageResult(opponent, damage);
    }

    public String BugBuzz(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        if(new Random().nextInt(100) < 10)
        {
            opponent.changeStatMultiplier(Stat.SPDEF, -1);

            return move.getDamageResult(opponent, damage) + " " + opponent.getName() + "'s Special Defense was lowered by 1 stage!";
        }

        return move.getDamageResult(opponent, damage);
    }

    public String RagePowder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String QuiverDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.SPATK, 1);
        user.changeStatMultiplier(Stat.SPDEF, 1);
        user.changeStatMultiplier(Stat.SPD, 1);

        return user.getName() + "'s Special Attack, Special Defense and Speed rose by 1 stage";
    }

    public String Twineedle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        damage += move.getDamage(user, opponent);

        if(!opponent.getType()[0].equals(Type.STEEL) && !opponent.getType()[1].equals(Type.STEEL) && !opponent.getType()[0].equals(Type.POISON) && !opponent.getType()[1].equals(Type.POISON))
        {
            if(new Random().nextInt(100) < 20)
            {
                opponent.addStatusCondition(StatusCondition.POISONED);
                return move.getDamageResult(opponent, damage) + " " + opponent.getName() + " is poisoned!";
            }
        }

        return move.getDamageResult(opponent, damage);
    }

    public String PinMissile(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    public String FellStinger(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        if(opponent.isFainted())
        {
            user.changeStatMultiplier(Stat.ATK, 3);
            return move.getDamageResult(opponent, damage) + " " + user.getName() + "'s Attack rose by 3 stages!";
        }

        return move.getDamageResult(opponent, damage);
    }

    public String LeechLife(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);
        user.heal(damage / 2);

        return move.getDamageResult(opponent, damage) + " " + user.getName() + " healed " + (damage / 2) + " HP!";
    }

    public String StickyWeb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.hazardData(opponent.getUUID()).addHazard(DuelHelper.EntryHazard.STICKY_WEB);
        return user.getName() + " laid a Sticky Web trap!";
    }

    public String Infestation(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.addStatusCondition(StatusCondition.BOUND);
        duel.data(opponent.getUUID()).boundTurns = 5;

        return Move.simpleDamageMove(user, opponent, duel, move) + " " + opponent.getName() + " was bound!";
    }

    //TODO: Switch out immediately after attacking
    public String UTurn(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Lunge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.ATK, -1, 100, false);
    }

    public String Steamroller(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }
}
