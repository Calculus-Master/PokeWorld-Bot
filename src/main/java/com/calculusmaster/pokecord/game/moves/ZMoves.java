package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.TypeEffectiveness;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;

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
        opponent.addStatusCondition(StatusCondition.PARALYZED);

        return Move.simpleDamageMove(user, opponent, duel, move) + " " + opponent.getName() + " is paralyzed!";
    }

    public String SinisterArrowRaid(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ExtremeEvoboost(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.ATK, 2);
        user.changeStatMultiplier(Stat.DEF, 2);
        user.changeStatMultiplier(Stat.SPATK, 2);
        user.changeStatMultiplier(Stat.SPDEF, 2);
        user.changeStatMultiplier(Stat.SPD, 2);

        return user.getName() + "'s stats were all raised by 2 stages!";
    }

    public String MaliciousMoonsault(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ClangorousSoulblaze(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.ATK, 1);
        user.changeStatMultiplier(Stat.DEF, 1);
        user.changeStatMultiplier(Stat.SPATK, 1);
        user.changeStatMultiplier(Stat.SPDEF, 1);
        user.changeStatMultiplier(Stat.SPD, 1);

        return Move.simpleDamageMove(user, opponent, duel, move) + " " + user.getName() + "'s stats were all raised by 1 stage!";
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
        user.setCrit(12);

        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        user.setCrit(12);

        return move.getDamageResult(opponent, damage);
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
        int damage = opponent.getStat(Stat.HP) * 3 / 4;
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String LightThatBurnsTheSky(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
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
        opponent.changeStatMultiplier(Stat.SPD, -2);

        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FROZEN, 20) + " " + opponent.getName() + "'s Speed was lowered by 2 stages!";
    }

    //Xerneas
    public String TreeOfLife(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.SPATK, 2);
        user.changeStatMultiplier(Stat.SPDEF, 2);
        user.changeStatMultiplier(Stat.SPD, 2);

        int amount = user.getHealth() * 3 / 4;
        user.heal(amount);

        return user.getName() + "'s Special Attack, Special Defense and Speed rose by 2 stages! " + user.getName() + " healed for " + amount + " HP!";
    }

    //Yveltal
    public String CocoonOfDestruction(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);
        user.heal(damage);

        return move.getDamageResult(opponent, damage) + " " + user.getName() + " recovered " + damage + " HP!";
    }

    //Diancie and Mega Diancie
    public String DazzlingDiamondBarrage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.DEF, 3, 50, true);
    }

    //Rayquaza and Mega Rayquaza
    public String DraconicOzoneAscent(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(user.getName().contains("Mega") && (opponent.getName().contains("Kyogre") || opponent.getName().contains("Groudon"))) move.setPower(move.getPower() * 1.25);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //Ultra Necrozma (Prismatic Laser)
    public String PrismaticLightBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
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
        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    //Volcanion
    public String VolcanicSteamGeyser(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setType(Type.FIRE);
        int fireDamage = move.getDamage(user, opponent);
        move.setType(Type.WATER);
        int waterDamage = move.getDamage(user, opponent);

        move.setType(fireDamage > waterDamage ? Type.FIRE : Type.WATER);

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
}
