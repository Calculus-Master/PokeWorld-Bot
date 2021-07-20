package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class ZMoves
{
    public String SavageSpinOut(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String BlackHoleEclipse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String DevastatingDrake(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String GigavoltHavoc(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String TwinkleTackle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String AllOutPummeling(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String InfernoOverdrive(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SupersonicSkystrike(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String NeverEndingNightmare(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String BloomDoom(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String TectonicRage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SubzeroSlammer(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String BreakneckBlitz(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String AcidDownpour(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ShatteredPsyche(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ContinentalCrush(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String CorkscrewCrash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String HydroVortex(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String StokedSparksurfer(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.PARALYZED)
                .execute();
    }

    public String SinisterArrowRaid(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ExtremeEvoboost(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, 2, 100, true)
                                .add(Stat.DEF, 2)
                                .add(Stat.SPATK, 2)
                                .add(Stat.SPDEF, 2)
                                .add(Stat.SPD, 2))
                .execute();
    }

    public String MaliciousMoonsault(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ClangorousSoulblaze(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, 1, 100, true)
                                .add(Stat.DEF, 1)
                                .add(Stat.SPATK, 1)
                                .add(Stat.SPDEF, 1)
                                .add(Stat.SPD, 1))
                .execute();
    }

    public String MenacingMoonrazeMaelstrom(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SplinteredStormshards(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SoulStealing7StarStrike(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String GenesisSupernova(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String LetsSnuggleForever(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Catastropika(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String TenMillionVoltThunderbolt(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect(12)
                .execute();
    }

    public String OceanicOperetta(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String PulverizingPancake(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SearingSunrazeSmash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String GuardianOfAlola(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedDamageEffect(opponent.getHealth() * 3 / 4)
                .execute();
    }

    public String LightThatBurnsTheSky(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //Custom Addition
        if(opponent.isDynamaxed()) move.setPower(move.getPower() * 2);
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //Custom

    //Reshiram
    public String WhiteHotInferno(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.BURNED, 40);
    }

    //Zekrom
    public String SuperchargedStormSurge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.PARALYZED, 40);
    }

    //Kyurem
    public String EternalWinter(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPD, -2, 100, false)
                .addStatusEffect(StatusCondition.FROZEN, 20)
                .execute();
    }

    //Black Kyurem
    public String FreezingStormSurge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.FROZEN, 10)
                .addStatusEffect(StatusCondition.PARALYZED, 50)
                .execute();
    }

    //White Kyurem
    public String BlazingIceferno(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.FROZEN, 10)
                .addStatusEffect(StatusCondition.BURNED, 50)
                .execute();
    }

    //Xerneas
    public String TreeOfLife(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.SPATK, 2, 100, true)
                                .add(Stat.SPDEF, 2)
                                .add(Stat.SPD, 2))
                .addFractionHealEffect(3 / 4D)
                .execute();
    }

    //Yveltal
    public String CocoonOfDestruction(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addDamageHealEffect(1)
                .execute();
    }

    //Diancie and Mega Diancie
    public String DazzlingDiamondBarrage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.DEF, 3, 50, true);
    }

    //Arceus
    public String DecreeOfArceus(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        double healthRatio = user.getHealth() / user.getStat(Stat.HP);

        if(healthRatio <= 0.01) move.setPower(500);
        else if(healthRatio <= 0.25) move.setPower(280);
        else if(healthRatio <= 0.5) move.setPower(200);
        else if((int)(healthRatio * 100) == 69) move.setPower(690);
        else if(healthRatio <= 0.75) move.setPower(180);
        else move.setPower(150);

        return ((int)(healthRatio * 100) == 69 ? "Nice! " : "") + Move.simpleDamageMove(user, opponent, duel, move);
    }

    //Rayquaza and Mega Rayquaza
    public String DraconicOzoneAscent(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(user.getName().contains("Mega") && (opponent.getName().contains("Kyogre") || opponent.getName().contains("Groudon"))) move.setPower(move.getPower() * 1.25);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //Ultra Necrozma (Prismatic Laser)
    public String TheBlindingOne(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.getName().contains("Mega") || opponent.getName().contains("Primal")) move.setPower(move.getPower() * 2);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //Zygarde (Any Form)
    public String TectonicZWrath(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //Zygarde Complete
    public String TitanicZEnforcer(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.getName().contains("Zygarde")) move.setPower(move.getPower() * 2);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MillionArrowBarrage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(opponent.getUUID()).isRaised = false;

        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    public String MillionWaveTsunami(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(opponent.getUUID()).canSwap = false;

        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    //Volcanion
    public String VolcanicSteamGeyser(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setType(Type.FIRE);
        int fireDamage = move.getDamage(user, opponent);
        move.setType(Type.WATER);
        int waterDamage = move.getDamage(user, opponent);
        move.setType(Type.GROUND);
        int groundDamage = move.getDamage(user, opponent);

        move.setType(fireDamage > waterDamage && fireDamage > groundDamage ? Type.FIRE : (waterDamage > fireDamage && waterDamage > groundDamage ? Type.WATER : Type.GROUND));

        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.BURNED, 60);
    }

    //Kyogre and Primal Kyogre
    public String PrimordialTsunami(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //Groudon and Primal Groudon
    public String PrimordialLandslide(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //Genesect
    public String ElementalTechnoOverdrive(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //Melmetal
    public String QuadrupleSteelSmash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move, 4);
    }

    public String MetalLiquidation(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean steelRemoval = opponent.isType(Type.STEEL);

        if(steelRemoval)
        {
            if(opponent.getType()[0].equals(Type.STEEL)) opponent.setType(Type.NORMAL, 0);
            if(opponent.getType()[1].equals(Type.STEEL)) opponent.setType(Type.NORMAL, 1);
        }

        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.DEF, 2, 100, true)
                .addStatChangeEffect(Stat.DEF, -2, 100, false)
                .execute() + (steelRemoval ? " " + opponent.getName() + " is no longer a Steel type!" : "");
    }

    //Dialga
    public String TimelineShatter(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //Palkia
    public String UltraSpaceHypernova(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //Giratina and Origin Giratina
    public String DarkMatterExplosion(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addRecoilEffect(1 / 4D)
                .execute();
    }

    //Eternatus and Eternamax Eternatus
    public String TheDarkestDay(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.isDynamaxed()) move.setPower(move.getPower() * 2);
        if(user.getName().contains("Eternamax")) move.setPower(Math.pow(move.getPower(), 2));

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MaxParticleBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.isDynamaxed()) move.setPower(move.getPower() * 2);
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //Darkrai
    public String NightmareVoid(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.ASLEEP)
                .addStatusEffect(StatusCondition.NIGHTMARE)
                .addStatusEffect(StatusCondition.CURSED)
                .addFractionHealEffect(1 / 2D)
                .execute();
    }
}
