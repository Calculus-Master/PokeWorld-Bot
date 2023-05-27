package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.elements.Weather;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.Arrays;

public class FireMoves
{
    public String Ember(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.BURNED, 10);
    }

    public String FireFang(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BURNED, 10)
                .addStatusEffect(StatusCondition.FLINCHED, 10)
                .execute();
    }

    public String FlameBurst(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Flamethrower(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.BURNED, 10);
    }

    public String FireSpin(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.BOUND, 100);
    }

    public String Inferno(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.BURNED, 100);
    }

    public String HeatWave(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.BURNED, 10);
    }

    public String FlareBlitz(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BURNED, 10)
                .addRecoilEffect(1 / 3D)
                .execute();
    }

    public String SunnyDay(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addWeatherEffect(Weather.HARSH_SUNLIGHT)
                .execute();
    }

    public String BurnUp(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(user.isType(Type.FIRE)) user.removeType(Type.FIRE);

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move) + " " + user.getName() + " is no longer a Fire type!";
    }

    public String BlastBurn(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String VCreate(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.DEF, -1, 100, true)
                                .add(Stat.SPDEF, -1)
                                .add(Stat.SPD, -1))
                .execute();
    }

    public String FireBlast(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.BURNED, 10);
    }

    public String Eruption(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower(150 * user.getHealth() / (double)user.getStat(Stat.HP));
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String LavaPlume(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.BURNED, 30);
    }

    public String FirePunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.BURNED, 10);
    }

    public String FusionFlare(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String WillOWisp(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.BURNED, 100)
                .execute();
    }

    public String Overheat(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPATK, -2, 100, true);
    }

    public String MysticalFire(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPATK, -1, 100, false);
    }

    public String BlazeKick(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .addStatusEffect(StatusCondition.BURNED, 10)
                .execute();
    }

    public String PyroBall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BURNED, 10)
                .execute();
    }

    public String FireLash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.DEF, -1, 100, false)
                .execute();
    }

    public String FieryDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPATK, 1, 50, true)
                .execute();
    }

    public String FlameWheel(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BURNED, 10)
                .execute();
    }

    public String BlueFlare(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BURNED, 20)
                .execute();
    }

    public String MagmaStorm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BOUND)
                .execute();
    }

    public String MindBlown(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addRecoilEffect(1 / 2D)
                .execute();
    }

    public String SacredFire(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BURNED, 50)
                .execute();
    }

    public String FlameCharge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPD, 1, 100, true)
                .execute();
    }

    public String Incinerate(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        String b = "";

        if(opponent.hasItem() && opponent.getItem().isBerry())
        {
            opponent.removeItem();
            b = opponent.getName() + "'s " + opponent.getItem().getName() + " was burned up!";
        }

        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute() + b;
    }

    public String FirePledge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: Deals extra damage (160 base power) if teammate (2v2/3v3) uses Water/Grass Pledge
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public String HeatCrash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        double r = user.getWeight() / opponent.getWeight();

        if(r >= 5) move.setPower(120);
        else if(r >= 4) move.setPower(100);
        else if(r >= 3) move.setPower(80);
        else if(r >= 2) move.setPower(60);
        else move.setPower(40);


        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public String SearingShot(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BURNED, 30)
                .execute();
    }

    public String BurningJealousy(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        String burned = "";

        if(Arrays.stream(Stat.values()).anyMatch(s -> opponent.changes().get(s) > 0))
        {
            opponent.addStatusCondition(StatusCondition.BURNED);
            burned = opponent.getName() + " was burned! ";
        }

        return burned + MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public String TorchSong(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPATK, 1, 100, true);
    }

    public String BitterBlade(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addDamageHealEffect(1 / 2.)
                .execute();
    }

    public String ArmorCannon(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.DEF, -1, 100, true)
                        .add(Stat.SPDEF, -1)
                )
                .execute();
    }
}
