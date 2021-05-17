package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.*;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MoveNew
{
    public static Map<String, MoveData> MOVES = new HashMap<>();

    private String name;
    private MoveData moveData;
    private Type type;
    private Category category;
    private int power;
    private int accuracy;

    public static void init()
    {
        Mongo.MoveInfo.find(Filters.exists("name")).forEach(d -> MOVES.put(d.getString("name"), new MoveData(d.getString("name"), d.getString("type"), d.getString("category"), d.getInteger("power"), d.getInteger("accuracy"), d.getString("info"), d.getString("zmove"))));
    }

    //TODO: Do this!
    public MoveNew(String name)
    {
        this.moveData = MOVES.get(name);
        this.name = this.moveData.name;
        this.setDefaultValues();
    }

    public String logic(Pokemon user, Pokemon opponent, Duel duel) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
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

        return (String)(typeClass.getMethod(this.name.replaceAll("\\s", ""), Pokemon.class, Pokemon.class, Duel.class).invoke(typeClass.getDeclaredConstructor().newInstance(), user, opponent, duel));
    }

    public static boolean isMove(String move)
    {
        return MOVES.containsKey(Global.normalCase(move));
    }

    public boolean isAccurate()
    {
        return (new Random().nextInt(100) + 1) <= this.accuracy;
    }

    public int getDamage(Pokemon user, Pokemon opponent)
    {
        Random r = new Random();

        //Damage = [((2 * Level / 5 + 2) * Power * A / D) / 50 + 2] * Modifier
        int level = user.getLevel();
        int power = this.power;
        int atkStat = this.category.equals(Category.PHYSICAL) ? user.getStat(Stat.ATK) : user.getStat(Stat.SPATK);
        int defStat = this.category.equals(Category.PHYSICAL) ? opponent.getStat(Stat.DEF) : opponent.getStat(Stat.SPDEF);

        //Modifier = Targets * Weather * Badge * Critical * Random * STAB * Type * Burn * Other
        //Ignored Components: Targets, Weather, Badge, Other
        //TODO: Weather
        double critical = (r.nextInt(10000) < 625) ? 1.5 : 1.0;
        double random = (r.nextInt(16) + 85.0) / 100.0;
        double stab = user.getType()[0].equals(this.type) || user.getType()[1].equals(this.type) ? 1.5 : 1.0;
        double type = TypeEffectiveness.getCombinedMap(opponent.getType()[0], opponent.getType()[1]).get(this.type);
        double burned = this.category.equals(Category.PHYSICAL) && user.getStatusCondition().equals(StatusCondition.BURNED) ? 0.5 : 1.0;

        double modifier = critical * random * stab * type * burned;
        double damage = (((2 * level / 5.0 + 2) * power * (double)atkStat / (double)defStat) / 50) + 2;
        double finalDMG = damage * modifier;
        return (int)(finalDMG + 0.5);
    }

    //Setters

    public void setDefaultValues()
    {
        this.type = this.moveData.type;
        this.category = this.moveData.category;
        this.power = this.moveData.power;
        this.accuracy = this.moveData.accuracy;
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

    public void setAccuracy(int a)
    {
        this.accuracy = a;
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

    public String getZMove()
    {
        return this.moveData.zmove;
    }

    public static class MoveData
    {
        public String name;
        public Type type;
        public Category category;
        public int power;
        public int accuracy;
        public String info;
        public String zmove;

        MoveData(String name, String type, String category, int power, int accuracy, String info, String zmove)
        {
            this.name = name;
            this.type = Type.cast(type);
            this.category = Category.cast(category);
            this.power = power;
            this.accuracy = accuracy;
            this.info = info;
            this.zmove = zmove;
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
                    ", zmove='" + zmove + '\'' +
                    '}';
        }
    }
}
