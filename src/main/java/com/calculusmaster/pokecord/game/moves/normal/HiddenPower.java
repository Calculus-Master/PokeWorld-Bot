package com.calculusmaster.pokecord.game.moves.normal;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.psychic.Psyshock;

import java.util.Map;

public class HiddenPower extends Move
{
    Map<Stat, Integer> ivs = null;
    public HiddenPower()
    {
        super("Hidden Power");
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        this.ivs = user.getIVs();

        int damage = this.getDamage(user, opponent);
        opponent.changeHealth(damage);

        return this.getMoveResults(user, opponent, damage);
    }

    @Override
    public Type getType()
    {
        if(this.ivs == null) return Type.NORMAL;

        int a = this.ivs.get(Stat.HP) % 2;
        int b = this.ivs.get(Stat.ATK) % 2;
        int c = this.ivs.get(Stat.DEF) % 2;
        int d = this.ivs.get(Stat.SPD) % 2;
        int e = this.ivs.get(Stat.SPATK) % 2;
        int f = this.ivs.get(Stat.SPDEF) % 2;

        int type = (int)((15 * (a + 2 * b + 4 * c + 8 * d + 16 * e + 32 * f)) / 63.0);

        return switch(type)
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
                    default -> throw new IllegalStateException("Unexpected value: " + type);
                };
    }
}
