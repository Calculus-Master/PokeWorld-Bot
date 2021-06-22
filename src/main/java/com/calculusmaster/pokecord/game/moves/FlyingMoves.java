package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;

import java.util.Random;

public class FlyingMoves
{
    public String AirSlash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.FLINCHED, 30)
                .execute();
    }

    public String Gust(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(opponent.getUUID()).flyUsed || duel.data(opponent.getUUID()).bounceUsed) move.setPower(2 * move.getPower());
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Tailwind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Roost(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(1 / 2D)
                .execute();
    }

    public String Hurricane(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.CONFUSED, 30);
    }

    public String Peck(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String DrillPeck(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Pluck(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String WingAttack(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SkyAttack(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .addStatusEffect(StatusCondition.FLINCHED, 30)
                .execute();
    }

    public String FeatherDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.ATK, -2, 100, false)
                .execute();
    }

    public String DragonAscent(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.DEF, -1, 100, true)
                                .add(Stat.SPDEF, -1))
                .execute();
    }

    public String AerialAce(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Fly(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(user.getUUID()).flyUsed)
        {
            duel.data(user.getUUID()).flyUsed = false;
            return Move.simpleDamageMove(user, opponent, duel, move);
        }
        else
        {
            duel.data(user.getUUID()).flyUsed = true;
            return user.getName() + " flew high up!";
        }
    }

    public String Bounce(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(user.getUUID()).bounceUsed)
        {
            duel.data(user.getUUID()).bounceUsed = false;
            return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.PARALYZED, 30);
        }
        else
        {
            duel.data(user.getUUID()).bounceUsed = true;
            return user.getName() + " sprung up!";
        }
    }

    public String BraveBird(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addRecoilEffect(1 / 3D)
                .execute();
    }

    //TODO: Removes barriers and lowers evasion by one stage
    //TODO: barrier moves: mist, light screen, reflect, safeguard
    public String Defog(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.entryHazards[duel.playerIndexFromUUID(user.getUUID())] = new DuelHelper.EntryHazardHandler();
        duel.entryHazards[duel.playerIndexFromUUID(opponent.getUUID())] = new DuelHelper.EntryHazardHandler();

        return "All Entry Hazards were removed!";
    }

    //TODO: Copies last move
    public String MirrorMove(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }
}
