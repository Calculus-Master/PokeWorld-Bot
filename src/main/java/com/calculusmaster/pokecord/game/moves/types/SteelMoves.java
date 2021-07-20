package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class SteelMoves
{
    public String IronDefense(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.DEF, 2, 100, true)
                .execute();
    }

    public String FlashCannon(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPDEF, -1, 10, false);
    }

    public String MetalBurst(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedDamageEffect((int)(duel.data(user.getUUID()).lastDamageTaken * 1.5))
                .execute();
    }

    public String MetalClaw(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.ATK, 1, 10, true);
    }

    public String MagnetBomb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MirrorShot(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MetalSound(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPDEF, -2, 100, false)
                .execute();
    }

    public String GyroBall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower((int)(25 * (double)opponent.getStat(Stat.SPD) / user.getStat(Stat.SPD)));

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String IronTail(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.DEF, -1, 30, false)
                .execute();
    }

    public String Autotomize(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPD, 2, 100, true)
                .execute();
    }

    public String SunsteelStrike(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String HeavySlam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        double ratio = user.getWeight() / opponent.getWeight();

        if(ratio >= 5) move.setPower(120);
        else if(ratio >= 4) move.setPower(100);
        else if(ratio >= 3) move.setPower(80);
        else if(ratio >= 2) move.setPower(40);
        else move.setPower(20);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String DoubleIronBash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedMultiStrikeEffect(2)
                .addStatusEffect(StatusCondition.FLINCHED, 30)
                .execute();
    }

    public String BehemothBash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.isDynamaxed()) move.setPower(move.getPower() * 2);
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String BehemothBlade(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.isDynamaxed()) move.setPower(move.getPower() * 2);
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String IronHead(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }

    public String BulletPunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MeteorMash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.ATK, 1, 20, true);
    }

    public String KingsShield(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).kingsShieldUsed = true;

        if(user.getName().equals("Aegislash Blade")) user.changeForm("Aegislash");

        return user.getName() + " defended itself with its Shield!";
    }

    public String SteelBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int userDamage = user.getHealth() / 2;
        user.damage(userDamage);
        return Move.simpleDamageMove(user, opponent, duel, move) + " " + user.getName() + " lost " + userDamage + " HP!";
    }

    //TODO: Opponent unable to flee
    public String AnchorShot(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ShiftGear(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, 1, 100, true)
                                .add(Stat.SPD, 2))
                .execute();
    }

    public String DoomDesire(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).doomDesireUsed = true;
        duel.data(user.getUUID()).doomDesireTurns = 2;
        return "It will strike in 2 turns!";
    }

    public String GearGrind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move, 2);
    }
}
