package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Terrain;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class GrassMoves
{
    public String RazorLeaf(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String VineWhip(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String LeechSeed(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(!opponent.isType(Type.GRASS) && !opponent.hasStatusCondition(StatusCondition.SEEDED))
        {
            return MoveEffectBuilder.make(user, opponent, duel, move)
                    .addStatusEffect(StatusCondition.SEEDED)
                    .execute();
        }
        else return move.getNoEffectResult(opponent);
    }

    public String SleepPowder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.ASLEEP)
                .execute();
    }

    public String SeedBomb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Synthesis(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(switch(duel.weather.get()) {
                    case CLEAR -> 1 / 2D;
                    case HARSH_SUNLIGHT, EXTREME_HARSH_SUNLIGHT -> 2 / 3D;
                    default -> 1 / 4D;
                })
                .execute();
    }

    public String WorrySeed(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(user.hasStatusCondition(StatusCondition.ASLEEP))
        {
            user.removeStatusCondition(StatusCondition.ASLEEP);
            return user.getName() + " is now awake!";
        }
        else return move.getNothingResult();
    }

    public String SolarBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(user.getItem().equals(Item.POWER_HERB) || duel.data(user.getUUID()).solarBeamUsed)
        {
            duel.data(user.getUUID()).solarBeamUsed = false;
            return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
        }
        else
        {
            duel.data(user.getUUID()).solarBeamUsed = true;
            return user.getName() + " is absorbing light!";
        }
    }

    public String PetalBlizzard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String PetalDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.CONFUSED, 100, true)
                .execute();
    }

    public String StunSpore(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.PARALYZED)
                .execute();
    }

    public String FrenzyPlant(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Leafage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String LeafBlade(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String LeafStorm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPATK, -2, 100, true);
    }

    public String ForestsCurse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.isType(Type.GRASS)) return move.getNoEffectResult(opponent);
        else
        {
            opponent.addType(Type.GRASS);
            return opponent.getName() + " is now partially a Grass Type!";
        }
    }

    public String WoodHammer(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addRecoilEffect(1 / 3D)
                .execute();
    }

    public String CottonGuard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.DEF, 3, 100, true)
                .execute();
    }

    public String HornLeech(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addDamageHealEffect(1 / 2D)
                .execute();
    }

    public String SolarBlade(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String GravApple(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.DEF, -1, 100, false);
    }

    public String BulletSeed(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.multiDamage(user, opponent, duel, move);
    }

    public String Spore(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.ASLEEP)
                .execute();
    }

    public String GrassKnot(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.getWeight() < 10) move.setPower(20);
        else if(opponent.getWeight() < 25) move.setPower(40);
        else if(opponent.getWeight() < 50) move.setPower(60);
        else if(opponent.getWeight() < 100) move.setPower(80);
        else if(opponent.getWeight() < 200) move.setPower(100);
        else move.setPower(120);

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String PowerWhip(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String SpikyShield(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).spikyShieldUsed = true;

        return user.getName() + " defended itself with its Shield!";
    }

    public String GrassyTerrain(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addTerrainEffect(Terrain.GRASSY_TERRAIN)
                .execute();
    }

    public String Aromatherapy(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        for(Pokemon p : duel.getPlayers()[duel.playerIndexFromUUID(user.getUUID())].team) p.clearStatusConditions();

        return user.getName() + "'s party's Status Conditions were cleared!";
    }

    public String NeedleArm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.FLINCHED, 30)
                .execute();
    }

    public String GigaDrain(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addDamageHealEffect(1 / 2D)
                .execute();
    }

    public String TropKick(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.ATK, -1, 100, false)
                .execute();
    }

    public String Absorb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addDamageHealEffect(0.5)
                .execute();
    }

    public String AppleAcid(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPDEF, -1, 100, false)
                .execute();
    }

    public String EnergyBall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPDEF, -1, 10, false)
                .execute();
    }

    public String MegaDrain(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addDamageHealEffect(0.5)
                .execute();
    }

    public String MagicalLeaf(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String StrengthSap(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedHealEffect(opponent.getStat(Stat.ATK))
                .addStatChangeEffect(Stat.ATK, -1, 100, false)
                .execute();
    }

    public String SnapTrap(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BOUND)
                .execute();
    }

    public String GrassWhistle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.ASLEEP)
                .execute();
    }

    public String BranchPoke(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String SeedFlare(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPDEF, -2, 40, false)
                .execute();
    }

    public String CottonSpore(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPD, -2, 100, false)
                .execute();
    }

    public String GrassPledge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: Pledge moves (See Fire Pledge)
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public String LeafTornado(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addAccuracyChangeEffect(-1, 30, false)
                .execute();
    }

    public String DrumBeating(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPD, -1, 100, false)
                .execute();
    }

    public String GrassyGlide(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public String JungleHealing(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.clearStatusConditions(StatusCondition.BURNED, StatusCondition.FROZEN, StatusCondition.PARALYZED, StatusCondition.POISONED);

        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(1 / 4D)
                .execute();
    }

    public String Ingrain(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).canSwap = false;
        duel.data(user.getUUID()).ingrainUsed = true;

        return user.getName() + " extends its roots!";
    }

    public String FlowerTrick(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect(24)
                .execute();
    }

    public String Chloroblast(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addFractionSelfDamageEffect(1 / 2.)
                .execute();
    }
}
