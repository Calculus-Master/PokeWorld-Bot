package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.TypeEffectiveness;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.mongo.MongoQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class Move extends MongoQuery
{
    private String name;
    private boolean isWIP;

    //TODO: Change this is the stat multipler used in moves
    //When a move request to lower or raise a stat by a stage, how many IVs does that lower or raise
    protected int stageIV = 8;

    public static final List<Move> MOVES = new ArrayList<>();

    public Move(String name)
    {
        super("name", name, Mongo.MoveInfo);
        this.name = name;
        this.isWIP = false;

        Move.MOVES.add(this);
    }

    public Move setWIP()
    {
        this.isWIP = true;
        return this;
    }

    public boolean isWIP()
    {
        return this.isWIP;
    }

    public abstract String logic(Pokemon user, Pokemon opponent);

    protected String getMoveResults(Pokemon user, Pokemon opponent, int damage)
    {
        return this.getMoveUseResults(user) + " It dealt **" + damage + "** damage to " + opponent.getName() + "!";
    }

    protected String getMoveUseResults(Pokemon user)
    {
        return user.getName() + " used **" + this.getName() + "**!";
    }

    public String getMoveResultsFail(Pokemon user)
    {
        return user.getName() + " missed using " + this.getName() + "!";
    }

    public boolean getIsAccurate()
    {
        return (new Random().nextInt(100) + 1) <= this.getAccuracy();
    }

    public int getDamage(Pokemon user, Pokemon opponent)
    {
        Random r = new Random();

        //Damage = [((2 * Level / 5 + 2) * Power * A / D) / 50 + 2] * Modifier
        int level = user.getLevel();
        int power = this.getPower();
        int atkStat = this.getCategory().equals(Category.PHYSICAL) ? user.getStat(Stat.ATK) : user.getStat(Stat.SPATK);
        int defStat = this.getCategory().equals(Category.PHYSICAL) ? opponent.getStat(Stat.DEF) : opponent.getStat(Stat.SPDEF);

        //Modifier = Targets * Weather * Badge * Critical * Random * STAB * Type * Burn * Other
        //Ignored Components: Targets, Weather, Badge, Other
        double critical = (r.nextInt(10000) < 625) ? 1.5 : 1.0;
        double random = (r.nextInt(16) + 85.0) / 100.0;
        double stab = user.getType()[0].equals(this.getType()) || user.getType()[1].equals(this.getType()) ? 1.5 : 1.0;
        double type = TypeEffectiveness.getCombinedMap(opponent.getType()[0], opponent.getType()[1]).get(this.getType());
        //TODO: double burned = user.isBurned() ? 0.5 : 1.0;

        double modifier = critical * random * stab * type /* * burned*/;
        double damage = (((2 * level / 5.0 + 2) * power * (double)atkStat / (double)defStat) / 50) + 2;
        double finalDMG = damage * modifier;
        return (int)finalDMG;
    }

    public Type getType()
    {
        return Type.cast(this.json().getString("type"));
    }

    public Category getCategory()
    {
        return Category.cast(this.json().getString("category"));
    }

    public int getPower()
    {
        return this.json().getInt("power");
    }

    public int getAccuracy()
    {
        return this.json().getInt("accuracy");
    }

    public String getInfo()
    {
        return this.json().getString("info");
    }

    public String getName()
    {
        return this.name;
    }

    public String getZMove()
    {
        return this.json().getString("zmove");
    }

    public static Move asMove(String move)
    {
        return Move.MOVES.stream().filter(m -> m.getName().equals(Global.normalCase(move))).collect(Collectors.toList()).get(0);
    }

    public static boolean isMove(String move)
    {
        return Move.MOVES.stream().map(Move::getName).collect(Collectors.toList()).contains(Global.normalCase(move));
    }
}
