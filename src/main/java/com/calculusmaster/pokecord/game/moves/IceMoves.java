package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.elements.Weather;

import java.util.Random;

public class IceMoves
{
    public String Hail(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.weather = Weather.HAIL;
        duel.weatherTurns = 5;

        return user.getName() + " summoned a hailstorm!";
    }

    public String IceBall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String IcicleCrash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(new Random().nextInt(100) < 30)
        {
            opponent.addStatusCondition(StatusCondition.FLINCHED);
            return Move.simpleDamageMove(user, opponent, duel, move) + " " + opponent.getName() + " flinched!";
        }

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String IcicleSpear(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        int times = 1;

        Random r = new Random();

        if(r.nextInt(8) < 3)
        {
            move.setPower(50);
            damage += move.getDamage(user, opponent);
            times++;

            if(r.nextInt(8) < 3)
            {
                move.setPower(75);
                damage += move.getDamage(user, opponent);
                times++;

                if(r.nextInt(8) < 1)
                {
                    move.setPower(100);
                    damage += move.getDamage(user, opponent);
                    times++;

                    if(r.nextInt(8) < 1)
                    {
                        move.setPower(125);
                        damage += move.getDamage(user, opponent);
                        times++;
                    }
                }
            }
        }

        opponent.damage(damage);

        return move.getDamageResult(opponent, damage) + " Icicle Spear hit " + times + " time" + (times > 1 ? "s!" : "!");
    }

    public String IceBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean freeze = new Random().nextInt(100) < 10;

        if(freeze) opponent.addStatusCondition(StatusCondition.FROZEN);

        return Move.simpleDamageMove(user, opponent, duel, move) + (freeze ? " " + opponent.getName() + " is now frozen!" : "");
    }

    public String Blizzard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean freeze = new Random().nextInt(100) < 10;

        if(freeze) opponent.addStatusCondition(StatusCondition.FROZEN);

        return Move.simpleDamageMove(user, opponent, duel, move) + (freeze ? " " + opponent.getName() + " is now frozen!" : "");
    }

    public String SheerCold(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.isType(Type.ICE) || opponent.getLevel() > user.getLevel()) return move.getNoEffectResult(opponent);
        else
        {
            int damage = opponent.getHealth();
            opponent.damage(damage);

            return move.getDamageResult(opponent, damage);
        }
    }

    public String PowderSnow(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FROZEN, 10);
    }

    public String Mist(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setStatImmune(true);
        duel.data(user.getUUID()).statImmuneTurns = 3;

        return user.getName() + " is immune to any stat changes for 3 turns!";
    }

    public String IceShard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String FreezeDry(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String IceFang(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean freeze = new Random().nextInt(100) < 10;
        boolean flinch = new Random().nextInt(100) < 10;

        if(freeze) opponent.addStatusCondition(StatusCondition.FROZEN);
        if(flinch) opponent.addStatusCondition(StatusCondition.FLINCHED);

        return Move.simpleDamageMove(user, opponent, duel, move) + (freeze ? " " + opponent.getName() + " is frozen!" : "") + (flinch ? " " + opponent.getName() + " flinched!" : "");
    }

    public String IcePunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FROZEN, 10);
    }

    public String Glaciate(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPD, -1, 100, false);
    }

    public String FreezeShock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.PARALYZED, 30);
    }

    public String IceBurn(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.BURNED, 30);
    }

    public String IcyWind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPD, -1, 100, false);
    }
}
