package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.Random;

public class FireMoves
{
    public String Ember(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.BURNED, 10);
    }

    public String FireFang(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        String burned = "";
        String flinched = "";

        if((!opponent.getType()[0].equals(Type.FIRE) && !opponent.getType()[1].equals(Type.FIRE)) && new Random().nextInt(100) < 10)
        {
            opponent.addStatusCondition(StatusCondition.BURNED);
            burned = " " + opponent.getName() + " is burned!";
        }

        if(new Random().nextInt(100) < 10)
        {
            opponent.addStatusCondition(StatusCondition.FLINCHED);
            flinched = " " + opponent.getName() + " flinched!";
        }

        return move.getDamageResult(opponent, damage) + burned + flinched;
    }

    public String FlameBurst(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Flamethrower(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.BURNED, 10);
    }

    public String FireSpin(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: Does damage per turn
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Inferno(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.BURNED, 100);
    }

    public String HeatWave(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.BURNED, 10);
    }

    public String FlareBlitz(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        user.damage(damage / 3);

        if(!opponent.isType(Type.FIRE) && new Random().nextInt(100) < 10)
        {
            opponent.addStatusCondition(StatusCondition.BURNED);
            return move.getDamageResult(opponent, damage) + move.getRecoilDamageResult(user, damage / 3) + " " + opponent.getName() + " is burned!";
        }

        return move.getDamageResult(opponent, damage) + " " + move.getRecoilDamageResult(user, damage / 3);
    }

    public String SunnyDay(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return user.getName() + " caused harsh sunlight!";
    }

    public String BurnUp(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(user.getType()[0].equals(Type.FIRE)) user.setType(Type.NORMAL, 0);
        if(user.getType()[1].equals(Type.FIRE)) user.setType(Type.NORMAL, 1);

        return Move.simpleDamageMove(user, opponent, duel, move) + " " + user.getName() + " is no longer a Fire type!";
    }

    public String BlastBurn(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String VCreate(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.DEF, -1);
        user.changeStatMultiplier(Stat.SPDEF, -1);
        user.changeStatMultiplier(Stat.SPD, -1);

        return Move.simpleDamageMove(user, opponent, duel, move) + " " + user.getName() + "'s Defense, Special Defense, and Speed";
    }

    public String FireBlast(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.BURNED, 10);
    }

    public String Eruption(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower(150 * user.getHealth() / (double)user.getStat(Stat.HP));
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String LavaPlume(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.BURNED, 30);
    }

    public String FirePunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.BURNED, 10);
    }

    public String FusionFlare(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }
}
