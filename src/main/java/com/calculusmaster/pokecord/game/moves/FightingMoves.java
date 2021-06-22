package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;

import java.util.Random;

public class FightingMoves
{
    public String BulkUp(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, 1, 100, true)
                                .add(Stat.DEF, 1))
                .execute();
    }

    public String RockSmash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.DEF, -1, 50, false)
                .execute();
    }

    public String BrickBreak(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String QuickGuard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).quickGuardUsed = true;

        return user.getName() + " defended itself from increased priority moves!";
    }

    public String ThunderousKick(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.DEF, -1, 100, false)
                .execute();
    }

    public String Counter(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Detect(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).detectUsed = true;

        return user.getName() + " is now protected!";
    }

    public String CloseCombat(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.DEF, -1, 100, true)
                                .add(Stat.SPDEF, -1))
                .execute();
    }

    public String Reversal(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        double hpRatio = user.getHealth() / (double)user.getStat(Stat.HP);

        if(hpRatio > 0.7) move.setPower(20);
        else if(hpRatio > 0.35) move.setPower(40);
        else if(hpRatio > 0.2) move.setPower(80);
        else if(hpRatio > 0.1) move.setPower(100);
        else if(hpRatio > 0.04) move.setPower(150);
        else move.setPower(200);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String AuraSphere(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SecretSword(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String LowKick(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.getWeight() < 10) move.setPower(20);
        else if(opponent.getWeight() < 25) move.setPower(40);
        else if(opponent.getWeight() < 50) move.setPower(60);
        else if(opponent.getWeight() < 100) move.setPower(80);
        else if(opponent.getWeight() < 200) move.setPower(100);
        else move.setPower(120);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String HammerArm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPD, -1, 100, true);
    }

    public String FocusBlast(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPDEF, -1, 10, false);
    }

    public String Revenge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.first.equals(opponent.getUUID())) move.setPower(2 * move.getPower());
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Superpower(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, -1, 100, true)
                                .add(Stat.DEF, -1))
                .execute();
    }

    public String DynamicPunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.CONFUSED, 100);
    }

    public String SacredSword(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SkyUppercut(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MachPunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SeismicToss(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedDamageEffect(user.getLevel())
                .execute();
    }

    public String MeteorAssault(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ArmThrust(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    public String KarateChop(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String JumpKick(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }
}
