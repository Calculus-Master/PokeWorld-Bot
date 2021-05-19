package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.elements.Weather;
import com.calculusmaster.pokecord.util.Global;

public class NormalMoves
{
    public String Tackle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String Growl(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.changeStatMultiplier(Stat.ATK, -1);
        return "It lowered " + opponent.getName() + "'s Attack by one stage!";
    }

    public String Growth(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean harshSun = duel.getDuelWeather().equals(Weather.HARSH_SUNLIGHT);
        user.changeStatMultiplier(Stat.ATK, harshSun ? 2 : 1);
        user.changeStatMultiplier(Stat.SPATK, harshSun ? 2 : 1);
        return "It increased " + user.getName() + "'s Attack and Special Attack by " + (harshSun ? 2 : 1) + " stage" + (harshSun ? "s" : "") + "!";
    }

    public String HiddenPower(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int a = user.getIVs().get(Stat.HP) % 2;
        int b = user.getIVs().get(Stat.ATK) % 2;
        int c = user.getIVs().get(Stat.DEF) % 2;
        int d = user.getIVs().get(Stat.SPD) % 2;
        int e = user.getIVs().get(Stat.SPATK) % 2;
        int f = user.getIVs().get(Stat.SPDEF) % 2;

        int typeVal = (int)((15 * (a + 2 * b + 4 * c + 8 * d + 16 * e + 32 * f)) / 63.0);

        Type t = switch(typeVal)
                {
                    case 0 -> Type.FIGHTING;
                    case 1 -> Type.FLYING;
                    case 2 -> Type.POISON;
                    case 3 -> Type.GROUND;
                    case 4 -> Type.ROCK;
                    case 5 -> Type.BUG;
                    case 6 -> Type.GHOST;
                    case 7 -> Type.STEEL;
                    case 8 -> Type.FIRE;
                    case 9 -> Type.WATER;
                    case 10 -> Type.GRASS;
                    case 11 -> Type.ELECTRIC;
                    case 12 -> Type.PSYCHIC;
                    case 13 -> Type.ICE;
                    case 14 -> Type.DRAGON;
                    case 15 -> Type.DARK;
                    default -> throw new IllegalStateException("Unexpected value: " + typeVal);
                };

        move.setType(t);

        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage) + "Hidden Power's type was " + Global.normalCase(t.toString()) + "! ";
    }

    //TODO: Come up with a custom idea for it
    public String Roar(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String WorkUp(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.ATK, 1);
        user.changeStatMultiplier(Stat.SPATK, 1);

        return "It increased " + user.getName() + "'s Attack and Special Attack by 1 stage!";
    }

    public String TakeDown(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);

        opponent.damage(damage);
        user.damage(damage / 4);

        return move.getDamageResult(opponent, damage) + " " + move.getRecoilDamageResult(user, damage / 4);
    }

    //TODO: Evasion or custom idea
    public String SweetScent(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String DoubleEdge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);

        opponent.damage(damage);
        user.damage(damage / 3);

        return move.getDamageResult(opponent, damage) + " " + move.getRecoilDamageResult(user, damage / 3);
    }

    public String Scratch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String Smokescreen(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: Accuracy Stat Multiplier
        return move.getNotImplementedResult();
    }

    public String ScaryFace(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.changeStatMultiplier(Stat.SPD, -2);

        return opponent.getName() + "'s Speed fell by 2 stages!";
    }

    public String Slash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setCrit(3);
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);
        user.setCrit(1);

        return move.getDamageResult(opponent, damage);
    }

    public String TailWhip(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.changeStatMultiplier(Stat.DEF, -1);

        return opponent.getName() + "'s Defense was lowered by 1 stage!";
    }

    public String RapidSpin(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        user.changeStatMultiplier(Stat.SPD, 1);

        return move.getDamageResult(opponent, damage) + " " + user.getName() + "'s Speed rose by 1 stage!";
    }

    public String Protect(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: Protect!
        return move.getNotImplementedResult();
    }

    public String SkullBash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        user.changeStatMultiplier(Stat.DEF, 1);

        return move.getDamageResult(opponent, damage) + " " + user.getName() + "'s Defense rose by 1 stage!";
    }

    public String ShellSmash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.DEF, -1);
        user.changeStatMultiplier(Stat.SPDEF, -1);
        user.changeStatMultiplier(Stat.ATK, 2);
        user.changeStatMultiplier(Stat.SPATK, 2);
        user.changeStatMultiplier(Stat.SPD, 2);

        return user.getName() + "'s Defense and Special Defense were lowered by 1 stage each! " + user.getName() + "'s Attack, Special Attack and Speed rose by 2 stages each!";
    }

    public String DefenseCurl(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.DEF, 1);

        return user.getName() + "'s Defense rose by 1 stage!";
    }
}
