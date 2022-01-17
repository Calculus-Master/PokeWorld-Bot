package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.EntryHazard;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

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
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
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
                .addEntryHazardEffect(EntryHazard.TOXIC_SPIKES)
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
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.BADLY_POISONED, 50);
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

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
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
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.POISONED, 30);
    }

    public String CrossPoison(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .addStatusEffect(StatusCondition.POISONED, 10)
                .execute();
    }

    public String PoisonGas(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.POISONED)
                .execute();
    }

    public String Sludge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.POISONED, 30)
                .execute();
    }

    public String CorrosiveGas(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.hasItem())
        {
            opponent.setItem(Item.NONE);

            return opponent.getName() + "'s Item melted away!";
        }
        else return move.getNoEffectResult(opponent);
    }

    public String Acid(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPDEF, -1, 10, false)
                .execute();
    }

    public String Smog(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.POISONED, 40)
                .execute();
    }

    public String SludgeBomb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.POISONED, 30)
                .execute();
    }

    public String GunkShot(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.POISONED, 30)
                .execute();
    }

    public String SludgeWave(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.POISONED, 10)
                .execute();
    }

    public String Coil(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, 1, 100, true)
                        .add(Stat.DEF, 1))
                .addAccuracyChangeEffect(1, 100, true)
                .execute();
    }

    public String AcidSpray(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPDEF, -2, 100, false)
                .execute();
    }

    public String ClearSmog(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCustomEffect(() -> {
                    opponent.changes().clear();
                    return opponent.getName() + "'s Stat changes were removed!";
                })
                .execute();
    }

    public String ToxicThread(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPD, -1, 100, false)
                .addStatusEffect(StatusCondition.POISONED)
                .execute();
    }

    public String Purify(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addConditionalCustomEffect(user.hasAnyStatusCondition(), () -> {
                    int healAmount = user.getMaxHealth() / 2;
                    user.heal(healAmount);
                    user.clearStatusConditions();
                    return user.getName() + " was cleared of its Status Conditions and " + user.getName() + " healed for " + healAmount + " HP!";
                }, move::getNothingResult)
                .addStatusEffect(StatusCondition.POISONED)
                .execute();
    }
}
