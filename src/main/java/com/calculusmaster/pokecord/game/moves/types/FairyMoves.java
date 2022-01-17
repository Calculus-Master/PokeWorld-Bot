package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Terrain;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class FairyMoves
{
    public String Moonlight(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(switch(duel.weather) {
                    case CLEAR -> 1 / 2D;
                    case HARSH_SUNLIGHT -> 2 / 3D;
                    default -> 1 / 4D;
                })
                .execute();
    }

    public String Moonblast(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPATK, -1, 30, false);
    }

    public String DisarmingVoice(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String NaturesMadness(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedDamageEffect(opponent.getHealth() / 2)
                .execute();
    }

    public String StrangeSteam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.CONFUSED, 20);
    }

    public String DrainingKiss(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addDamageHealEffect(3 / 4D)
                .execute();
    }

    public String AromaticMist(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPDEF, 1, 100, true)
                .execute();
    }

    public String FloralHealing(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(duel.terrain.equals(Terrain.GRASSY_TERRAIN) ? 2 / 3D : 1 / 2D)
                .execute();
    }

    public String CraftyShield(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).craftyShieldUsed = true;

        return user.getName() + " is protected from Status moves!";
    }

    public String FlowerShield(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        MoveEffectBuilder builder = MoveEffectBuilder.make(user, opponent, duel, move);

        if(user.isType(Type.GRASS))
            builder.addStatChangeEffect(Stat.DEF, 1, 100, true);

        if(opponent.isType(Type.GRASS))
            builder.addStatChangeEffect(Stat.DEF, 1, 100, false);

        if(!user.isType(Type.GRASS) && !opponent.isType(Type.GRASS))
            return move.getNothingResult();

        return builder.execute();
    }

    public String Geomancy(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.SPATK, 2, 100, true)
                                .add(Stat.SPDEF, 2)
                                .add(Stat.SPD, 2))
                .execute();
    }

    public String FleurCannon(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPATK, -2, 100, true)
                .execute();
    }

    public String LightOfRuin(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addRecoilEffect(1 / 2D)
                .execute();
    }

    public String SweetKiss(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.CONFUSED)
                .execute();
    }

    public String Charm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.ATK, -2, 100, false)
                .execute();
    }

    public String MistyTerrain(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addTerrainEffect(Terrain.MISTY_TERRAIN)
                .execute();
    }

    public String PlayRough(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.ATK, -1, 10, false)
                .execute();
    }

    public String FairyWind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public String DazzlingGleam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public String BabyDollEyes(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.ATK, -1, 100, false)
                .execute();
    }

    public String SpiritBreak(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPATK, -1, 100, false)
                .execute();
    }

    public String MistyExplosion(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.terrain.equals(Terrain.MISTY_TERRAIN)) move.setPower(1.5);

        user.damage(user.getHealth());

        return user.getName() + " fainted! " + MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public String Decorate(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, 2, 100, true)
                        .add(Stat.SPATK, 2))
                .execute();
    }
}
