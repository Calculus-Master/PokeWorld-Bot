package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Terrain;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;

public class ElectricMoves
{
    public String ElectricTerrain(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addTerrainEffect(Terrain.ELECRIC_TERRAIN)
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

        return Move.simpleDamageMove(user, opponent, duel, move) + " " + user.getName() + "'s Speed was " + ((int)(ratio * 100) / 100) + " times as fast as " + opponent.getName() + "!";
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
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.PARALYZED, 10);
    }

    public String Thunder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.PARALYZED, 30);
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
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPATK, 1, 70, true);
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
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPD, -1, 100, false);
    }

    public String ShockWave(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ThunderCage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.BOUND, 100);
    }

    public String ThunderPunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.PARALYZED, 10);
    }

    public String FusionBolt(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String BoltStrike(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.PARALYZED, 20);
    }

    //TODO: Change to Dark type when Morpeko in Hangry Mode
    public String AuraWheel(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ElectricCharge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addRecoilEffect(1 / 4D)
                .execute();
    }
}
