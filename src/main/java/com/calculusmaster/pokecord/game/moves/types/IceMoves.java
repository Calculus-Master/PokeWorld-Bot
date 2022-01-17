package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.FieldBarrier;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Weather;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class IceMoves
{
    public String Hail(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addWeatherEffect(Weather.HAIL)
                .execute();
    }

    public String IceBall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String IcicleCrash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }

    public String IcicleSpear(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.multiDamage(user, opponent, duel, move);
    }

    public String IceBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.FROZEN, 10);
    }

    public String Blizzard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.FROZEN, 10);
    }

    public String SheerCold(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addOHKOEffect()
                .execute();
    }

    public String PowderSnow(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.FROZEN, 10);
    }

    public String Mist(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).statImmuneTurns = 3;

        return user.getName() + " is immune to any stat changes for 3 turns!";
    }

    public String IceShard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String FreezeDry(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String IceFang(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.FROZEN, 10)
                .addStatusEffect(StatusCondition.FLINCHED, 10)
                .execute();
    }

    public String IcePunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.FROZEN, 10);
    }

    public String Glaciate(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPD, -1, 100, false);
    }

    public String FreezeShock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.PARALYZED, 30);
    }

    public String IceBurn(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.BURNED, 30);
    }

    public String IcyWind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPD, -1, 100, false);
    }

    public String GlacialLance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Avalanche(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.first.equals(opponent.getUUID()) && duel.data(user.getUUID()).lastDamageTaken > 0) move.setPower(2.0);

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String AuroraVeil(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(!duel.weather.equals(Weather.HAIL)) return move.getNothingResult();

        duel.barriers[duel.playerIndexFromUUID(user.getUUID())].addBarrier(FieldBarrier.AURORA_VEIL, user.getItem().equals(Item.LIGHT_CLAY));

        return user.getName() + " sets up a Veil, and is defended from Physical and Special attacks!";
    }

    public String FrostBreath(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect(24)
                .execute();
    }

    public String AuroraBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.ATK, -1, 10, false);
    }

    public String Haze(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changes().clear();

        return user.getName() + " has been cleared of all stat changes!";
    }

    public String IceHammer(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPD, -1, 100, true);
    }

    public String TripleAxel(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addConsecutiveDamageEffect(20, 40, 60)
                .execute();
    }
}
