package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.types.*;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Move
{
    public static Map<String, MoveData> MOVES = new HashMap<>();
    //TODO: Keep checking the custom moves and see if they can function as close to the original as possible
    public static final List<String> WIP_MOVES = Arrays.asList("Roar", "Safeguard", "Whirlwind", "Rage Powder", "Tailwind", "Light Screen", "Frustration", "Return", "Mind Reader", "Counter", "Magnetic Flux", "After You", "Disable", "Miracle Eye", "Guard Swap", "Power Swap", "Me First", "Yawn", "Gravity", "Spite", "Mean Look", "Foresight", "Wide Guard", "Ingrain", "Forests Curse", "Natural Gift", "Last Resort", "Sand Attack", "Teleport", "Odor Sleuth", "Helping Hand", "Mirror Move", "Stuff Cheeks", "Copycat", "Entrainment", "Block", "Follow Me", "Sky Drop", "Simple Beam", "Fling", "Telekinesis", "Quash", "No Retreat", "Encore", "Substitute", "Magic Coat", "Embargo", "Ally Switch");
    public static final List<String> CUSTOM_MOVES = Arrays.asList("Leech Seed", "Rapid Spin", "Mirror Shot", "Stockpile", "Worry Seed", "Aromatic Mist");
    public static List<String> INCOMPLETE_MOVES = new ArrayList<>();
    public static Map<String, Predicate<Pokemon>> MOVE_TUTOR_MOVES = new HashMap<>();

    private String name;
    private MoveData moveData;
    private Type type;
    private Category category;
    private int power;
    private int accuracy;
    public boolean isZMove;
    public boolean isMaxMove;
    private int priority;
    private double damageMultiplier;
    public double accuracyMultiplier;

    public int critChance;
    private boolean hitCrit;

    public static void init()
    {
        MOVES.clear();
        Mongo.MoveInfo.find().forEach(d -> MOVES.put(d.getString("name"), new MoveData(d.getString("name"), d.getString("type"), d.getString("category"), d.getInteger("power"), d.getInteger("accuracy"), d.getString("info"))));

        //Incomplete Moves
        INCOMPLETE_MOVES.clear();
        Mongo.PokemonInfo.find().forEach(d -> {
            for(String s : d.getList("moves", String.class)) if(!Move.isMove(s) && !INCOMPLETE_MOVES.contains(s)) INCOMPLETE_MOVES.add(s);
        });

        for(TM tm : TM.values()) if(!Move.isMove(tm.getMoveName())) INCOMPLETE_MOVES.add(tm.getMoveName());
        for(TR tr : TR.values()) if(!Move.isMove(tr.getMoveName())) INCOMPLETE_MOVES.add(tr.getMoveName());

        INCOMPLETE_MOVES = INCOMPLETE_MOVES.stream().distinct().collect(Collectors.toList());

        //Move Tutor Moves
        Move.registerMoveTutorMove("Blast Burn", "Charizard", "Typhlosion", "Blaziken", "Infernape", "Emboar", "Delphox", "Incineroar");
        Move.registerMoveTutorMove("Hydro Cannon", "Blastoise", "Feraligatr", "Swampert", "Empoleon", "Samurott", "Greninja", "Primarina");
        Move.registerMoveTutorMove("Frenzy Plant", "Venusaur", "Meganium", "Sceptile", "Torterra", "Serperior", "Chesnaught", "Decidueye");
        Move.registerMoveTutorMove("Draco Meteor", p -> p.getType()[0].equals(Type.DRAGON));
        Move.registerMoveTutorMove("Steel Beam", p -> p.getType()[0].equals(Type.STEEL));
        Move.registerMoveTutorMove("Volt Tackle", p -> p.getName().contains("Pikachu"));
        Move.registerMoveTutorMove("Dragon Ascent", p -> p.getName().contains("Rayquaza"));
        Move.registerMoveTutorMove("Secret Sword", p -> p.getName().contains("Keldeo"));
        Move.registerMoveTutorMove("Relic Song", p -> p.getName().contains("Meloetta"));
    }

    private static void registerMoveTutorMove(String name, Predicate<Pokemon> rule)
    {
        MOVE_TUTOR_MOVES.put(name, rule);
    }

    private static void registerMoveTutorMove(String name, String... pokemon)
    {
        registerMoveTutorMove(name, p -> Arrays.stream(pokemon).anyMatch(s -> p.getName().contains(s)));
    }

    public Move(String name)
    {
        if(!MOVES.containsKey(name) || WIP_MOVES.contains(name)) name = "Tackle";

        this.moveData = MOVES.get(name);
        this.name = this.moveData.name;
        this.setDefaultValues();
    }

    //Z-Move Constructor
    public Move(String name, Type type, Category category, int power)
    {
        this.name = name;
        this.type = type;
        this.category = category;
        this.power = power;
        this.accuracy = 100;
        this.isZMove = true;
        this.isMaxMove = false;
        this.setPriority();
        this.damageMultiplier = 1.0;
        this.critChance = 1;
    }

    public String logic(Pokemon user, Pokemon opponent, Duel duel)
    {
        //Call specific move method
        Class<?> typeClass = switch(this.type) {
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

        if(this.isZMove) typeClass = ZMoves.class;
        if(this.isMaxMove) typeClass = MaxMoves.class;

        String results = this.getMoveUsedResult(user);
        String moveName = this.name;

        if(moveName.equals("10,000,000 Volt Thunderbolt")) moveName = "TenMillionVoltThunderbolt";

        moveName = moveName.replaceAll("\\s", "").replaceAll("'", "");

        //Special cases

        try
        {
            results += (String)(typeClass.getMethod(moveName, Pokemon.class, Pokemon.class, Duel.class, Move.class).invoke(typeClass.getDeclaredConstructor().newInstance(), user, opponent, duel, this));
        }
        catch (Exception e)
        {
            System.out.println("Move failed! " + this.getName());
            results += "MOVE FAILED";
            e.printStackTrace();
        }

        return results;
    }

    //Move logic
    public static String simpleDamageMove(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public static String statusDamageMove(Pokemon user, Pokemon opponent, Duel duel, Move move, StatusCondition status, int percent)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(status, percent)
                .execute();
    }

    public static String statChangeDamageMove(Pokemon user, Pokemon opponent, Duel duel, Move move, Stat s, int stage, int percent, boolean userChange)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(s, stage, percent, userChange)
                .execute();
    }

    public static String multihitDamageMove(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addVariableMultiStrikeEffect()
                .execute();
    }

    public static String multihitDamageMove(Pokemon user, Pokemon opponent, Duel duel, Move move, int times)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedMultiStrikeEffect(times)
                .execute();
    }

    //Different Move Results

    public String getMoveUsedResult(Pokemon user)
    {
        return user.getName() + " used **" + this.name + "**! ";
    }

    public String getDamageResult(Pokemon opponent, int dmg)
    {
        String effective;
        double e = TypeEffectiveness.getCombinedMap(opponent.getType()[0], opponent.getType()[1]).get(this.type);

        //Freeze Dry
        if(this.name.equals("Freeze Dry")) e = opponent.isType(Type.WATER) || opponent.isType(Type.GRASS) || opponent.isType(Type.GROUND) || opponent.isType(Type.FLYING) || opponent.isType(Type.DRAGON) ? 2.0 : (opponent.isType(Type.FIRE) || opponent.isType(Type.ICE) || opponent.isType(Type.STEEL) ? 0.5 : 1.0);

        if(e == 4.0) effective = "It's **extremely** effective (4x)!";
        else if(e == 2.0) effective = "It's **super** effective (2x)!";
        else if(e == 1.0) effective = "";
        else if(e == 0.5) effective = "It's **not very** effective (0.5x)!";
        else if(e == 0.25) effective = "It's **extremely** ineffective (0.25x)!";
        else if(e == 0.0) effective = this.getNoEffectResult(opponent);
        else throw new IllegalStateException("Effectiveness multiplier is a strange value: " + e);


        return "It dealt **" + dmg + "** damage to " + opponent.getName() + "! " + (dmg > 0 ? effective + (this.hitCrit ? " It was a critical hit!" : "") : "");
    }

    public String getEffectiveness(Pokemon opponent)
    {
        double e = TypeEffectiveness.getCombinedMap(opponent.getType()[0], opponent.getType()[1]).get(this.type);

        //Freeze Dry
        if(this.name.equals("Freeze Dry")) e = opponent.isType(Type.WATER) || opponent.isType(Type.GRASS) || opponent.isType(Type.GROUND) || opponent.isType(Type.FLYING) || opponent.isType(Type.DRAGON) ? 2.0 : (opponent.isType(Type.FIRE) || opponent.isType(Type.ICE) || opponent.isType(Type.STEEL) ? 0.5 : 1.0);

        if(e == 4.0) return "**Extremely Effective**";
        else if(e == 2.0) return "**Super Effective**";
        else if(e == 1.0) return "**Effective**";
        else if(e == 0.5) return "**Not Very Effective**";
        else if(e == 0.25) return "**Extremely Ineffective**";
        else if(e == 0.0) return "**No Effect**";
        else throw new IllegalStateException("Effectiveness multiplier is a strange value: " + e);
    }

    public String getRecoilDamageResult(Pokemon user, int dmg)
    {
        return user.getName() + " took **" + dmg + "** damage in recoil!";
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

    public static boolean isMove(String move)
    {
        return MOVES.containsKey(Global.normalCase(move));
    }

    public boolean isAccurate(Pokemon user, Pokemon opponent)
    {
        int combined = user.getAccuracyStage() - opponent.getEvasionStage();

        double numerator = 3.0 + combined > 0 ? combined : 0;
        double denominator = 3.0 + combined < 0 ? Math.abs(combined) : 0;

        double stageMultiplier = combined == 0 ? 1.0 : numerator / denominator;

        int threshold = (int)(this.getAccuracy() * stageMultiplier * this.accuracyMultiplier);

        return (new Random().nextInt(100) + 1) <= threshold;
    }

    public int getDamage(Pokemon user, Pokemon opponent)
    {
        Random r = new Random();

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
        double type = TypeEffectiveness.getCombinedMap(opponent.getType()[0], opponent.getType()[1]).get(this.type);
        double burned = this.category.equals(Category.PHYSICAL) && user.hasStatusCondition(StatusCondition.BURNED) ? 0.5 : 1.0;

        if(critical > 1.0) hitCrit = true;

        //Any nuances go here

        //Psyshock, Psystrike, Secret Sword
        if(this.name.equals("Psyshock") || this.name.equals("Psystrike") || this.name.equals("Secret Sword")) defStat = opponent.getStat(Stat.DEF);

        //Photon Geyser, Light That Burns The Sky
        if(this.name.equals("Photon Geyser") || this.name.equals("Light That Burns The Sky")) atkStat = Math.max(user.getStat(Stat.ATK), user.getStat(Stat.SPATK));

        //Freeze Dry
        if(this.name.equals("Freeze Dry")) type = opponent.isType(Type.WATER) || opponent.isType(Type.GRASS) || opponent.isType(Type.GROUND) || opponent.isType(Type.FLYING) || opponent.isType(Type.DRAGON) ? 2.0 : (opponent.isType(Type.FIRE) || opponent.isType(Type.ICE) || opponent.isType(Type.STEEL) ? 0.5 : 1.0);

        //Gensect Z-Move: Elemental Techno Overdrive
        if(this.name.equals("Elemental Techno Overdrive")) stab = Math.random() + 1;

        //Body Press
        if(this.name.equals("Body Press")) atkStat = user.getStat(Stat.DEF);

        //Ability: Adaptability
        if(stab == 1.5 && user.getAbilities().contains("Adaptability")) stab = 2.0;

        //Ability: Technician
        if(user.getAbilities().contains("Technician") && power <= 60) power = (int)(power * 1.5);

        double modifier = critical * random * stab * type * burned;
        double damage = (((2 * level / 5.0 + 2) * power * (double)atkStat / (double)defStat) / 50) + 2;
        double finalDMG = damage * modifier;

        finalDMG *= this.damageMultiplier;
        return (int)(finalDMG + 0.5);
    }

    //Setters

    public void setDefaultValues()
    {
        this.type = this.moveData.type;
        this.category = this.moveData.category;
        this.power = this.moveData.power;
        this.accuracy = this.moveData.accuracy;
        this.hitCrit = false;
        this.critChance = 1;
        this.isZMove = false;
        this.isMaxMove = false;
        this.damageMultiplier = 1.0;
        this.accuracyMultiplier = 1.0;
        this.setPriority();
    }

    public void setType(Type t)
    {
        this.type = t;
    }

    public void setCategory(Category c)
    {
        this.category = c;
    }

    public void setPower(int p)
    {
        this.power = p;
    }

    public void setPower(double p)
    {
        this.power = (int)(p);
    }

    public void setAccuracy(int a)
    {
        this.accuracy = a;
    }

    public void setPriority(int p)
    {
        this.priority = p;
    }

    public void setPriority()
    {
        this.priority = switch(this.getName())
                {
                    case "Helping Hand" -> 5;
                    case "Baneful Bunker", "Detect", "Endure", "Kings Shield", "Magic Coat", "Protect", "Spiky Shield", "Snatch" -> 4;
                    case "Crafty Shield", "Fake Out", "Quick Guard", "Wide Guard", "Spotlight" -> 3;
                    case "Ally Switch", "Extreme Speed", "Feint", "First Impression", "Follow Me", "Rage Powder" -> 2;
                    case "Accelerock", "Aqua Jet", "Baby Doll Eyes", "Bide", "Bullet Punch", "Ice Shard", "Iron Deluge", "Mach Punch", "Powder", "Quick Attack", "Shadow Sneak", "Sucker Punch", "Vacuum Wave", "Water Shuriken" -> 1;
                    case "Vital Throw" -> -1;
                    case "Beak Blast", "Focus Punch", "Shell Trap" -> -3;
                    case "Avalanche", "Revenge" -> -4;
                    case "Counter", "Mirror Coat" -> -5;
                    case "Circle Throw", "Dragon Tail", "Roar", "Whirlwind" -> -6;
                    case "Trick Room" -> -7;
                    default -> 0;
                };
    }

    public void setDamageMultiplier(double d)
    {
        this.damageMultiplier = d;
    }

    //Getters

    public String getName()
    {
        return this.name;
    }

    public Type getType()
    {
        return this.type;
    }

    public Category getCategory()
    {
        return this.category;
    }

    public int getPower()
    {
        return this.power;
    }

    public int getAccuracy()
    {
        return this.accuracy;
    }

    public String getInfo()
    {
        return this.moveData.info;
    }

    public int getPriority()
    {
        return this.priority;
    }

    public static String getRandomMove()
    {
        List<String> pool = new ArrayList<>(MOVES.keySet());
        return pool.get(new Random().nextInt(pool.size()));
    }

    @Override
    public String toString() {
        return "MoveNew{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", category=" + category +
                ", power=" + power +
                ", accuracy=" + accuracy +
                ", " + moveData.toString() +
                '}';
    }

    public static class MoveData
    {
        public String name;
        public Type type;
        public Category category;
        public int power;
        public int accuracy;
        public String info;

        MoveData(String name, String type, String category, int power, int accuracy, String info)
        {
            this.name = name;
            this.type = Type.cast(type);
            this.category = Category.cast(category);
            this.power = power;
            this.accuracy = accuracy;
            this.info = info;
        }

        @Override
        public String toString() {
            return "MoveData{" +
                    "name='" + name + '\'' +
                    ", type=" + type +
                    ", category=" + category +
                    ", power=" + power +
                    ", accuracy=" + accuracy +
                    ", info='" + info + '\'' +
                    '}';
        }
    }
}