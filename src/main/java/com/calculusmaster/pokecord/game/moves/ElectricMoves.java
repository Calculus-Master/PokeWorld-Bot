package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.DuelHelper;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;

import java.util.Random;

public class ElectricMoves
{
    public String ElectricTerrain(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.terrain = DuelHelper.Terrain.ELECRIC_TERRAIN;
        duel.terrainTurns = 5;

        return user.getName() + " generated an Electric Field!";
    }

    public String ThunderShock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean paralyzed = new Random().nextInt(100) < 10;

        if(paralyzed) opponent.addStatusCondition(StatusCondition.PARALYZED);

        return Move.simpleDamageMove(user, opponent, duel, move) + (paralyzed ? " " + opponent.getName() + " is paralyzed!" : "");
    }

    public String ThunderWave(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.addStatusCondition(StatusCondition.PARALYZED);

        return opponent.getName() + " is paralyzed!";
    }

    public String Spark(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean paralyzed = new Random().nextInt(100) < 30;
        String s = "";

        if(paralyzed)
        {
            opponent.addStatusCondition(StatusCondition.PARALYZED);
            s = " " + opponent.getName() + " is paralyzed!";
        }

        return Move.simpleDamageMove(user, opponent, duel, move) + s;
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
        return Spark(user, opponent, duel, move);
    }

    public String MagnetRise(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).magnetRiseTurns = 5;

        return user.getName() + " is now immune to Ground type moves for 5 turns!";
    }

    public String ZapCannon(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.addStatusCondition(StatusCondition.PARALYZED);
        return Move.simpleDamageMove(user, opponent, duel, move) + " " + opponent.getName() + " is paralyzed!";
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
        user.changeStatMultiplier(Stat.SPDEF, 1);

        duel.data(user.getUUID()).chargeUsed = true;

        return user.getName() + "'s Special Defense was raised by 1 stage! " + user.getName() + " is charged up!";
    }

    public String VoltTackle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        boolean paralyze = new Random().nextInt(100) < 10;

        opponent.damage(damage);
        user.damage(damage / 3);

        if(paralyze) opponent.addStatusCondition(StatusCondition.PARALYZED);

        return move.getDamageResult(opponent, damage) + " " + move.getRecoilDamageResult(user, damage / 3) + (paralyze ? " " + opponent.getName() + " is paralyzed!" : "");
    }

    public String ChargeBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPATK, 1, 70, true);
    }

    public String ThunderFang(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean paralyze = new Random().nextInt(100) < 10;
        boolean flinch = new Random().nextInt(100) < 10;

        if(paralyze) opponent.addStatusCondition(StatusCondition.PARALYZED);
        if(flinch) opponent.addStatusCondition(StatusCondition.FLINCHED);

        return Move.simpleDamageMove(user, opponent, duel, move) + (paralyze ? " " + opponent.getName() + " is paralyzed!" : "") + (flinch ? " " + opponent.getName() + " flinched!" : "");
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
}
