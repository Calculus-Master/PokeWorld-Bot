package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.EntryHazard;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class BugMoves
{
    public String SilverWind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, 1, 10, true)
                                .add(Stat.DEF, 1)
                                .add(Stat.SPATK, 1)
                                .add(Stat.SPDEF, 1))
                .execute();
    }

    public String BugBuzz(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPDEF, -1, 10, false)
                .execute();
    }

    public String RagePowder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String QuiverDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.SPATK, 1, 100, true)
                                .add(Stat.SPDEF, 1)
                                .add(Stat.SPD, 1))
                .execute();
    }

    public String Twineedle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedMultiStrikeEffect(2)
                .addStatusEffect(StatusCondition.POISONED, 20)
                .execute();
    }

    public String PinMissile(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    public String FellStinger(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        String result = MoveEffectBuilder.make(user, opponent, duel, move).addDamageEffect().execute();

        if(opponent.isFainted())
        {
            user.changeStatMultiplier(Stat.ATK, 3);
            result += " " + user.getName() + "'s Attack rose by 3 stages!";
        }

        return result;
    }

    public String LeechLife(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addDamageHealEffect(1 / 2D)
                .execute();
    }

    public String StickyWeb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.hazardData(opponent.getUUID()).addHazard(EntryHazard.STICKY_WEB);
        return user.getName() + " laid a Sticky Web trap!";
    }

    public String Infestation(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.BOUND, 100)
                .execute();
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

    public String DefendOrder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.DEF, 1, 100, true)
                                .add(Stat.SPDEF, 1))
                .execute();
    }

    public String TailGlow(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPATK, 3, 100, true)
                .execute();
    }

    //TODO: Should work after the user swaps
    public String FirstImpression(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return duel.turn == 1 ? Move.simpleDamageMove(user, opponent, duel, move) : move.getNothingResult();
    }

    public String StruggleBug(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPATK, -1, 100, false)
                .execute();
    }

    public String XScissor(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Megahorn(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }
}
