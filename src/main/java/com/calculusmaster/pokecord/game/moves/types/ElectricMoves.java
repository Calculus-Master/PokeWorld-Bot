package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Terrain;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class ElectricMoves
{
    public String ElectricTerrain(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addTerrainEffect(Terrain.ELECTRIC_TERRAIN)
                .execute();
    }

    public String ThunderShock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.PARALYZED, 10)
                .execute();
    }

    public String ThunderWave(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.PARALYZED)
                .execute();
    }

    public String Spark(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.PARALYZED, 30)
                .execute();
    }

    public String ElectroBall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        double ratio = (double)user.getStat(Stat.SPD) / opponent.getStat(Stat.SPD);

        if(ratio >= 4) move.setPower(150);
        else if(ratio >= 3) move.setPower(120);
        else if(ratio >= 2) move.setPower(80);
        else if(ratio >= 1) move.setPower(60);
        else move.setPower(40);

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move) + " " + user.getName() + "'s Speed was " + ((int)(ratio * 100) / 100) + " times as fast as " + opponent.getName() + "!";
    }

    public String Discharge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.PARALYZED, 30)
                .execute();
    }

    public String MagnetRise(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).magnetRiseTurns = 5;
        duel.data(user.getUUID()).isRaised = true;

        return user.getName() + " is now immune to Ground type moves for 5 turns!";
    }

    public String ZapCannon(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.PARALYZED)
                .execute();
    }

    public String Thunderbolt(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.PARALYZED, 10);
    }

    public String Thunder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.PARALYZED, 30);
    }

    public String MagneticFlux(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Charge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPDEF, 1, 100, true)
                .execute() + " " + user.getName() + " is charged up!";
    }

    public String VoltTackle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.PARALYZED, 10)
                .addRecoilEffect(1 / 3D)
                .execute();
    }

    public String ChargeBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPATK, 1, 70, true);
    }

    public String ThunderFang(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.PARALYZED, 10)
                .addStatusEffect(StatusCondition.FLINCHED, 10)
                .execute();
    }

    public String Electroweb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPD, -1, 100, false);
    }

    public String ShockWave(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String ThunderCage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.BOUND, 100);
    }

    public String ThunderPunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.PARALYZED, 10);
    }

    public String FusionBolt(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String BoltStrike(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.PARALYZED, 20);
    }

    public String AuraWheel(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String ElectricCharge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addRecoilEffect(1 / 4D)
                .execute();
    }

    public String ParabolicCharge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addDamageHealEffect(1 / 2D)
                .execute();
    }

    public String Overdrive(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String ZingZap(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.FLINCHED, 30)
                .execute();
    }

    public String Nuzzle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.PARALYZED, 100);
    }

    public String PlasmaFists(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).plasmaFistsUsed = true;

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Electrify(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.first.equals(user.getUUID()))
        {
            duel.data(user.getUUID()).electrifyUsed = true;

            return user.getName() + " electrified " + opponent.getName() + "'s next move!";
        }
        else return move.getNothingResult();
    }

    public String BoltBeak(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addPowerBoostEffect(() -> duel.first.equals(user.getUUID()), 2.0)
                .addDamageEffect()
                .execute();
    }

    public String WildCharge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addRecoilEffect(1 / 4D)
                .execute();
    }

    public String EerieImpulse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPATK, -2, 100, false)
                .execute();
    }

    public String RisingVoltage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addPowerBoostEffect(() -> duel.terrain.is(Terrain.ELECTRIC_TERRAIN), 2.0)
                .addDamageEffect()
                .execute();
    }

    public String ElectroDrift(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String DoubleShock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return user.isType(Type.ELECTRIC) ? MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addCustomEffect(() ->
                {
                    user.removeType(Type.ELECTRIC);
                    return user.getName() + " is no longer an Electric-Type!";
                })
                .execute() : move.getNothingResult();
    }

    public String WildboltStorm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.PARALYZED, 20, false)
                .execute();
    }
}
