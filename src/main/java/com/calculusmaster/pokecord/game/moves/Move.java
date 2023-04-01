package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.moves.data.MoveData;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.moves.types.*;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugment;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class Move
{
    //TODO: Keep checking the custom moves and see if they can function as close to the original as possible
    public static final EnumSet<MoveEntity> WIP_MOVES = EnumSet.of(MoveEntity.ROAR, MoveEntity.WHIRLWIND, MoveEntity.RAGE_POWDER, MoveEntity.FRUSTRATION, MoveEntity.RETURN, MoveEntity.MAGNETIC_FLUX, MoveEntity.AFTER_YOU, MoveEntity.DISABLE, MoveEntity.MIRACLE_EYE, MoveEntity.ME_FIRST, MoveEntity.GRAVITY, MoveEntity.SPITE, MoveEntity.MEAN_LOOK, MoveEntity.FORESIGHT, MoveEntity.WIDE_GUARD, MoveEntity.TELEPORT, MoveEntity.ODOR_SLEUTH, MoveEntity.HELPING_HAND, MoveEntity.STUFF_CHEEKS, MoveEntity.COPYCAT, MoveEntity.FOLLOW_ME, MoveEntity.SKY_DROP, MoveEntity.SIMPLE_BEAM, MoveEntity.FLING, MoveEntity.TELEKINESIS, MoveEntity.QUASH, MoveEntity.NO_RETREAT, MoveEntity.ENCORE, MoveEntity.SUBSTITUTE, MoveEntity.MAGIC_COAT, MoveEntity.EMBARGO, MoveEntity.ALLY_SWITCH, MoveEntity.SLEEP_TALK, MoveEntity.GEAR_UP, MoveEntity.STRUGGLE);
    public static final EnumSet<MoveEntity> CUSTOM_MOVES = EnumSet.of(MoveEntity.LEECH_SEED, MoveEntity.RAPID_SPIN, MoveEntity.MIRROR_SHOT, MoveEntity.WORRY_SEED, MoveEntity.AROMATIC_MIST, MoveEntity.PAY_DAY, MoveEntity.POLLEN_PUFF);
    public static EnumSet<MoveEntity> INCOMPLETE_MOVES = EnumSet.noneOf(MoveEntity.class);

    //Other Data Storage
    public static final EnumSet<MoveEntity> SOUND_BASED_MOVES = EnumSet.of(MoveEntity.BOOMBURST, MoveEntity.BUG_BUZZ, MoveEntity.CHATTER, MoveEntity.CLANGING_SCALES, MoveEntity.CLANGOROUS_SOUL, MoveEntity.CLANGOROUS_SOULBLAZE, MoveEntity.CONFIDE, MoveEntity.DISARMING_VOICE, MoveEntity.ECHOED_VOICE, MoveEntity.EERIE_SPELL, MoveEntity.GRASS_WHISTLE, MoveEntity.GROWL, MoveEntity.HEAL_BELL, MoveEntity.HOWL, MoveEntity.HYPER_VOICE, MoveEntity.METAL_SOUND, MoveEntity.NOBLE_ROAR, MoveEntity.OVERDRIVE, MoveEntity.PARTING_SHOT, MoveEntity.PERISH_SONG, MoveEntity.RELIC_SONG, MoveEntity.ROAR, MoveEntity.ROUND, MoveEntity.SCREECH, MoveEntity.SING, MoveEntity.SNARL, MoveEntity.SNORE, MoveEntity.SPARKLING_ARIA, MoveEntity.SUPERSONIC, MoveEntity.UPROAR);
    public static final EnumSet<MoveEntity> BITING_MOVES = EnumSet.of(MoveEntity.BITE, MoveEntity.CRUNCH, MoveEntity.FIRE_FANG, MoveEntity.FISHIOUS_REND, MoveEntity.HYPER_FANG, MoveEntity.ICE_FANG, MoveEntity.JAW_LOCK, MoveEntity.POISON_FANG, MoveEntity.PSYCHIC_FANGS, MoveEntity.THUNDER_FANG);
    public static final EnumSet<MoveEntity> PULSE_MOVES = EnumSet.of(MoveEntity.AURA_SPHERE, MoveEntity.DARK_PULSE, MoveEntity.DRAGON_PULSE, MoveEntity.HEAL_PULSE, MoveEntity.ORIGIN_PULSE, MoveEntity.TERRAIN_PULSE, MoveEntity.WATER_PULSE);
    public static final EnumSet<MoveEntity> BALL_AND_BOMB_MOVES = EnumSet.of(MoveEntity.ACID_SPRAY, MoveEntity.AURA_SPHERE, MoveEntity.BARRAGE, MoveEntity.BEAK_BLAST, MoveEntity.BULLET_SEED, MoveEntity.EGG_BOMB, MoveEntity.ELECTRO_BALL, MoveEntity.ENERGY_BALL, MoveEntity.FOCUS_BLAST, MoveEntity.GYRO_BALL, MoveEntity.ICE_BALL, MoveEntity.MAGNET_BOMB, MoveEntity.MIST_BALL, MoveEntity.MUD_BOMB, MoveEntity.OCTAZOOKA, MoveEntity.POLLEN_PUFF, MoveEntity.PYRO_BALL, MoveEntity.ROCK_BLAST, MoveEntity.ROCK_WRECKER, MoveEntity.SEARING_SHOT, MoveEntity.SEED_BOMB, MoveEntity.SHADOW_BALL, MoveEntity.SLUDGE_BOMB, MoveEntity.WEATHER_BALL, MoveEntity.ZAP_CANNON);
    public static final EnumSet<MoveEntity> OHKO_MOVES = EnumSet.of(MoveEntity.FISSURE, MoveEntity.GUILLOTINE, MoveEntity.HORN_DRILL, MoveEntity.SHEER_COLD);
    public static final EnumSet<MoveEntity> DIRECT_DAMAGE_MOVES = EnumSet.of(MoveEntity.BIDE, MoveEntity.COMEUPPANCE, MoveEntity.COUNTER, MoveEntity.DRAGON_RAGE, MoveEntity.ENDEAVOR, MoveEntity.FINAL_GAMBIT, MoveEntity.GUARDIAN_OF_ALOLA, MoveEntity.METAL_BURST, MoveEntity.MIRROR_COAT, MoveEntity.NATURES_MADNESS, MoveEntity.NIGHT_SHADE, MoveEntity.PSYWAVE, MoveEntity.RUINATION, MoveEntity.SEISMIC_TOSS, MoveEntity.SONIC_BOOM, MoveEntity.SUPER_FANG);
    public static final EnumSet<MoveEntity> PUNCH_MOVES = EnumSet.of(MoveEntity.BULLET_PUNCH, MoveEntity.DIZZY_PUNCH, MoveEntity.DRAIN_PUNCH, MoveEntity.DYNAMIC_PUNCH, MoveEntity.FIRE_PUNCH, MoveEntity.FOCUS_PUNCH, MoveEntity.ICE_PUNCH, MoveEntity.MACH_PUNCH, MoveEntity.MEGA_PUNCH, MoveEntity.METEOR_MASH, MoveEntity.POWER_UP_PUNCH, MoveEntity.SHADOW_PUNCH, MoveEntity.SKULL_BASH, MoveEntity.THUNDER_PUNCH);

    private final MoveEntity entity;
    private final MoveData data;
    private Type type;
    private Category category;
    private int power;
    private int accuracy;
    private int priority;

    private double damageMultiplier;
    private double accuracyMultiplier;

    public int critChance;
    public boolean hitCrit;

    public static void init()
    {
        //Incomplete Moves TODO - Eventually remove
        INCOMPLETE_MOVES.clear();

        for(MoveEntity m : MoveEntity.values()) if(!WIP_MOVES.contains(m) && !Move.isImplemented(m) && !INCOMPLETE_MOVES.contains(m)) INCOMPLETE_MOVES.add(m);
    }

    public Move(MoveEntity entity)
    {
        this.entity = entity;
        this.data = entity.data();

        this.setDefaultValues();
    }

    public void setDefaultValues()
    {
        this.type = this.data.getType();
        this.category = this.data.getCategory();
        this.power = this.data.getBasePower();
        this.accuracy = this.data.getBaseAccuracy();
        this.priority = this.data.getPriority();

        this.hitCrit = false;
        this.critChance = 1;

        this.damageMultiplier = 1.0;
        this.accuracyMultiplier = 1.0;
    }

    public String logic(Pokemon user, Pokemon opponent, Duel duel)
    {
        //Call specific move method
        Class<?> typeClass = this.getHostClass();

        String results = this.getMoveUsedResult(user);
        String moveName = this.getMethodName();

        try
        {
            results += (String)(typeClass.getMethod(moveName, Pokemon.class, Pokemon.class, Duel.class, Move.class).invoke(typeClass.getDeclaredConstructor().newInstance(), user, opponent, duel, this));
        }
        catch (Exception e)
        {
            LoggerHelper.reportError(Move.class, "Could not find Move Method for %s (Looking for: %s)".formatted(this.entity.toString(), moveName), e);

            results = "An error occurred while using %s. Defaulting to Tackle...\n".formatted(this.entity.toString()) + new Move(MoveEntity.TACKLE).logic(user, opponent, duel);
        }

        return results;
    }

    public static boolean isImplemented(MoveEntity entity)
    {
        boolean implemented;

        if(WIP_MOVES.contains(entity)) implemented = false;
        else
        {
            Move m = new Move(entity);

            try { m.getHostClass().getMethod(m.getMethodName(), Pokemon.class, Pokemon.class, Duel.class, Move.class); implemented = true; }
            catch (NoSuchMethodException e) { implemented = false; }
        }

        return implemented;
    }

    public String getMethodName()
    {
        String out = Global.normalize(this.entity.toString().replaceAll("_", " ")).replaceAll(" ", "");

        if(this.is(MoveEntity.TEN_MILLION_VOLT_THUNDERBOLT)) out = "TenMillionVoltThunderbolt";

        return out;
    }

    public Class<?> getHostClass()
    {
        if(this.isZMove()) return ZMoves.class;
        else if(this.isMaxMove()) return MaxMoves.class;
        else return switch(this.data.getType()) {
            case BUG -> BugMoves.class;
            case DARK -> DarkMoves.class;
            case DRAGON -> DragonMoves.class;
            case ELECTRIC -> ElectricMoves.class;
            case FAIRY -> FairyMoves.class;
            case FIGHTING -> FightingMoves.class;
            case FIRE -> FireMoves.class;
            case FLYING -> FlyingMoves.class;
            case GHOST -> GhostMoves.class;
            case GRASS -> GrassMoves.class;
            case GROUND -> GroundMoves.class;
            case ICE -> IceMoves.class;
            case NORMAL -> NormalMoves.class;
            case POISON -> PoisonMoves.class;
            case PSYCHIC -> PsychicMoves.class;
            case ROCK -> RockMoves.class;
            case STEEL -> SteelMoves.class;
            case WATER -> WaterMoves.class;
        };
    }

    public String getMoveUsedResult(Pokemon user)
    {
        return user.getName() + " used **" + this.getName() + "**! ";
    }

    public String getDamageResult(Pokemon opponent, int dmg)
    {
        return "It dealt **" + dmg + "** damage to " + opponent.getName() + "! " + (dmg > 0 ? this.getTypeEffectivenessText(opponent, false) + (this.hitCrit ? " It was a critical hit!" : "") : "");
    }

    public String getEffectivenessOverview(Pokemon opponent)
    {
        return this.getTypeEffectivenessText(opponent, true);
    }

    private String getTypeEffectivenessText(Pokemon other, boolean forBattle)
    {
        double e = this.getTypeEffectiveness(other);

        if(e == 4.0) return forBattle ? "**Extremely Effective**" : "It's **extremely** effective (4x)!";
        else if(e == 2.0) return forBattle ? "**Super Effective**" : "It's **super** effective (2x)!";
        else if(e == 1.0) return forBattle ? "**Effective**" : "";
        else if(e == 0.5) return forBattle ? "**Not Very Effective**" : "It's **not very** effective (0.5x)!";
        else if(e == 0.25) return forBattle ? "**Extremely Ineffective**" : "It's **extremely** ineffective (0.25x)!";
        else if(e == 0.0) return forBattle ? "**No Effect**" : this.getNoEffectResult(other);
        else throw new IllegalStateException("Effectiveness multiplier is a strange value: " + e);
    }

    public String getNoEffectResult(Pokemon opponent)
    {
        return "It **doesn't affect** " + opponent.getName() + "...";
    }

    public String getMissedResult(Pokemon user)
    {
        return user.getName() + " **missed** " + this.getName() + "!";
    }

    public String getNothingResult()
    {
        return "**Nothing** happened!";
    }

    public String getNotImplementedResult()
    {
        return "It did **nothing**! (Move has not been implemented yet)";
    }

    //Other Methods

    public boolean is(EnumSet<MoveEntity> entities)
    {
        return entities.contains(this.entity);
    }

    public boolean is(MoveEntity... entities)
    {
        return Stream.of(entities).anyMatch(e -> e == this.entity);
    }

    public boolean is(Category... categories)
    {
        return List.of(categories).contains(this.getCategory());
    }

    public boolean is(Type... types)
    {
        return List.of(types).contains(this.getType());
    }

    public boolean isZMove()
    {
        return this.entity.isZMove();
    }

    public boolean isMaxMove()
    {
        return this.entity.isMaxMove();
    }

    public boolean isContact()
    {
        return this.is(Category.PHYSICAL) || this.is(MoveEntity.PETAL_DANCE, MoveEntity.TRUMP_CARD, MoveEntity.WRING_OUT, MoveEntity.GRASS_KNOT, MoveEntity.DRAINING_KISS, MoveEntity.INFESTATION);
    }

    public static boolean isMove(String move)
    {
        return MoveEntity.cast(move) != null;
    }

    public boolean isAccurate(Pokemon user, Pokemon opponent)
    {
        int combined = user.changes().getAccuracy() - opponent.changes().getEvasion();

        double numerator = 3.0 + combined > 0 ? combined : 0;
        double denominator = 3.0 + combined < 0 ? Math.abs(combined) : 0;

        double stageMultiplier = combined == 0 ? 1.0 : numerator / denominator;

        int threshold = (int)(this.getAccuracy() * stageMultiplier * this.accuracyMultiplier);

        return (new Random().nextInt(100) + 1) <= threshold;
    }

    public int getDamage(Pokemon user, Pokemon opponent)
    {
        Random r = new Random();

        //Augment: Precision Strikes
        if(user.hasAugment(PokemonAugment.PRECISION_STRIKES)) this.critChance += 2;

        //Damage = [((2 * Level / 5 + 2) * Power * A / D) / 50 + 2] * Modifier
        int level = user.getLevel();
        int power = this.power;
        int atkStat = user.getStat(this.category.equals(Category.PHYSICAL) ? Stat.ATK : Stat.SPATK);
        int defStat = opponent.getStat(this.category.equals(Category.PHYSICAL) ? Stat.DEF : Stat.SPDEF);

        //Modifier = Targets * Weather * Badge * Critical * Random * STAB * Type * Burn * Other
        //Ignored Components: Targets, Badge, Other
        //Weather component is done in the Harsh Sunlight section in Duel
        double critical = (r.nextInt(24) < this.critChance) ? 1.5 : 1.0;
        double random = (r.nextInt(16) + 85.0) / 100.0;
        double stab = user.isType(this.type) ? 1.5 : 1.0;
        double type = this.getTypeEffectiveness(opponent);
        double burned = this.category.equals(Category.PHYSICAL) && user.hasStatusCondition(StatusCondition.BURNED) ? 0.5 : 1.0;

        if(critical > 1.0) hitCrit = true;

        //Any nuances go here

        //Psyshock, Psystrike, Secret Sword
        if(this.is(MoveEntity.PSYSHOCK, MoveEntity.PSYSTRIKE, MoveEntity.SECRET_SWORD)) defStat = opponent.getStat(Stat.DEF);

        //Photon Geyser, Light That Burns The Sky
        if(this.is(MoveEntity.PHOTON_GEYSER, MoveEntity.LIGHT_THAT_BURNS_THE_SKY)) atkStat = Math.max(user.getStat(Stat.ATK), user.getStat(Stat.SPATK));

        //Gensect Z-Move: Elemental Techno Overdrive
        if(this.is(MoveEntity.ELEMENTAL_TECHNO_OVERDRIVE)) stab = Math.random() + 1;

        //Body Press
        if(this.is(MoveEntity.BODY_PRESS)) atkStat = user.getStat(Stat.DEF);

        //Foul Play
        if(this.is(MoveEntity.FOUL_PLAY)) atkStat = opponent.getStat(Stat.ATK);

        //Ability: Adaptability
        if(stab > 1.0 && user.hasAbility(Ability.ADAPTABILITY)) stab *= 2.0;

        //Ability: Technician
        if(user.hasAbility(Ability.TECHNICIAN) && power <= 60) power = (int)(power * 1.5);

        //Ability: Wonder Guard
        if(user.hasAbility(Ability.WONDER_GUARD) && type < 2) type = 0.0;

        //Ability: Battle Armor
        if(opponent.hasAbility(Ability.BATTLE_ARMOR)) critical = 1.0;

        //Ability: Prism Armor
        if(opponent.hasAbility(Ability.PRISM_ARMOR) && type > 1) power = (int)(power * 0.75);

        //Ability: Neuroforce
        if(user.hasAbility(Ability.NEUROFORCE) && type > 1) power = (int)(power * 1.25);

        //Ability: Hustle
        if(user.hasAbility(Ability.HUSTLE)) atkStat *= 1.5;

        //Ability: Defeatist
        if(user.hasAbility(Ability.DEFEATIST) && user.getHealth() <= user.getMaxHealth(0.5)) atkStat *= 0.5;

        //Augment: Phantom Targeting
        if(user.hasAugment(PokemonAugment.PHANTOM_TARGETING) && type > 1.0) type *= 1.5;

        //Augment: Harmony
        if(user.hasAugment(PokemonAugment.HARMONY) && stab > 1.0) stab *= 1.75;

        //Augment: Precision Burst
        if(user.hasAugment(PokemonAugment.PRECISION_BURST) && critical > 1.0) critical = 2.0;

        //Augment: Raw Force
        if(user.hasAugment(PokemonAugment.RAW_FORCE))
        {
            critical = 1.0;
            stab = 1.0;
            type = 1.0;

            power *= 1.5;
        }

        //Augment: Modifying Force
        if(user.hasAugment(PokemonAugment.MODIFYING_FORCE))
        {
            if(critical > 1.0) critical *= 1.15;
            if(stab > 1.0) stab *= 1.2;
            type *= 1.25;

            power *= 0.6;
        }

        //Augment: Meteor Shower
        if(user.hasAugment(PokemonAugment.METEOR_SHOWER) && this.is(MoveEntity.METEOR_MASH))
        {
            power -= 20;
        }

        //Augment: Magical Sure Shot
        if(user.hasAugment(PokemonAugment.SURE_SHOT) && this.is(Type.PSYCHIC))
        {
            if(type < 1.0) type = (type + 1.0) / 2;
            else if(type > 1.0) type *= 1.5;
        }

        double modifier = critical * random * stab * type * burned;
        double damage = (((2 * level / 5.0 + 2) * power * (double)atkStat / (double)defStat) / 50) + 2;
        double finalDMG = damage * modifier;

        finalDMG *= this.damageMultiplier;
        return (int)(finalDMG + 0.5);
    }

    //Handles Moves with Custom Type Effectiveness Charts (ie, Freeze Dry, Flying Press)
    private double getTypeEffectiveness(Pokemon other)
    {
        return switch(this.entity) {
            case FREEZE_DRY -> Stream.of(Type.WATER, Type.GRASS, Type.GROUND, Type.FLYING, Type.DRAGON).anyMatch(other::isType) ? 2.0 : (Stream.of(Type.FIRE, Type.ICE, Type.STEEL).anyMatch(other::isType) ? 0.5 : 1.0);
            case FLYING_PRESS -> Stream.of(Type.NORMAL, Type.GRASS, Type.ICE, Type.FIGHTING, Type.DARK).anyMatch(other::isType) ? 2.0 : (Stream.of(Type.ELECTRIC, Type.POISON, Type.FLYING, Type.PSYCHIC, Type.FAIRY).anyMatch(other::isType) ? 0.5 : (other.isType(Type.GHOST) ? 0.0 : 1.0));
            default -> TypeEffectiveness.getEffectiveness(other.getType()).get(this.type);
        };
    }

    //Other
    public void setDamageMultiplier(double damageMultiplier)
    {
        this.damageMultiplier *= damageMultiplier;
    }

    public void setAccuracyMultiplier(double accuracyMultiplier)
    {
        this.accuracyMultiplier = accuracyMultiplier;
    }

    //Core
    public int getPriority()
    {
        return this.priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public MoveEntity getEntity()
    {
        return this.entity;
    }

    public String getName()
    {
        return this.data.getName();
    }

    public Type getType()
    {
        return this.type;
    }

    public void setType(Type t)
    {
        this.type = t;
    }

    public Category getCategory()
    {
        return this.category;
    }

    public void setCategory(Category c)
    {
        this.category = c;
    }

    public int getPower()
    {
        return this.power;
    }

    public void setPower(int p)
    {
        this.power = p;
    }

    public void setPower(double p)
    {
        this.power = (int)(p * this.power);
    }

    public int getAccuracy()
    {
        return this.accuracy;
    }

    public void setAccuracy(int a)
    {
        this.accuracy = a;
    }
}
