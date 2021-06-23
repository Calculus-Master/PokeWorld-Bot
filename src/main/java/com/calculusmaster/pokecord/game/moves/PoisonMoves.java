package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;

import java.util.Random;

public class PoisonMoves
{
    public String Toxic(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.BADLY_POISONED)
                .execute();
    }

    public String Venoshock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.hasStatusCondition(StatusCondition.POISONED)) move.setDamageMultiplier(2);
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String PoisonPowder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.POISONED)
                .execute();
    }

    public String ToxicSpikes(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addEntryHazardEffect(DuelHelper.EntryHazard.TOXIC_SPIKES)
                .execute();
    }

    public String PoisonJab(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.POISONED, 30)
                .execute();
    }

    public String AcidArmor(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.DEF, 2, 100, true)
                .execute();
    }

    public String PoisonFang(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.BADLY_POISONED, 50);
    }

    public String PoisonTail(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String BanefulBunker(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).banefulBunkerUsed = true;

        return user.getName() + " defended itself with its Bunker!";
    }

    public String ShellSideArm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int specialDamage = move.getDamage(user, opponent);
        move.setCategory(Category.PHYSICAL);
        int physicalDamage = move.getDamage(user, opponent);

        move.setCategory(specialDamage > physicalDamage ? Category.SPECIAL : Category.PHYSICAL);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String VenomDrench(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return opponent.hasStatusCondition(StatusCondition.POISONED) ? MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, -1, 100, false)
                                .add(Stat.SPATK, -1)
                                .add(Stat.SPD, -1))
                .execute() : move.getNoEffectResult(opponent);
    }

    public String PoisonSting(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.POISONED, 30);
    }
}
