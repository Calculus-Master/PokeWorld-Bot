package com.calculusmaster.pokecord.game.objectives;

import com.calculusmaster.pokecord.game.common.CreditRewards;
import com.calculusmaster.pokecord.game.objectives.types.AbstractObjective;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Bounty
{
    public static int MAX_BOUNTIES_HELD;
    public static int BOUNTY_REWARD_MAX;
    public static int BOUNTY_REWARD_MIN;

    private List<AbstractObjective> objectives;
    private int creditReward;

    public static Bounty create()
    {
        Random r = new Random();

        int tier;
        if(r.nextFloat() < 0.5) tier = 1;
        else if(r.nextFloat() < 0.8) tier = 2;
        else tier = 3;

        return Bounty.create(tier);
    }

    public static Bounty create(int tier)
    {
        Bounty b = new Bounty();

        b.setReward(tier);
        b.setObjectives(tier);

        return b;
    }

    private Bounty() {}

    public Bounty(Document data)
    {
        this.creditReward = data.getInteger("credits");
        this.objectives = data.getList("objectives", Document.class).stream().map(d -> ObjectiveType.valueOf(d.getString("objective_type")).build(d)).collect(Collectors.toList());
    }

    public Document serialize()
    {
        return new Document()
                .append("credits", this.creditReward)
                .append("objectives", this.objectives.stream().map(AbstractObjective::serialize).toList());
    }

    public void reroll()
    {
        this.objectives.replaceAll(objective -> !objective.isComplete() ? objective.getObjectiveType().create() : objective);
        this.creditReward = new Random().nextInt((int)(this.creditReward * 0.5), (int)(this.creditReward * 0.8));
    }

    //Core
    public boolean isComplete()
    {
        return this.objectives.stream().allMatch(AbstractObjective::isComplete);
    }

    public void setObjectives(int tier)
    {
        Random r = new Random();
        this.objectives = new ArrayList<>();
        IntStream.range(0, tier).forEach(i -> this.objectives.add(ObjectiveType.values()[r.nextInt(ObjectiveType.values().length)].create()));
    }

    public void setReward(int tier)
    {
        Random r = new Random();

        int[] source = switch(tier) {
            case 1 -> CreditRewards.BOUNTY_TIER_1;
            case 2 -> CreditRewards.BOUNTY_TIER_2;
            case 3 -> CreditRewards.BOUNTY_TIER_3;
            default -> new int[]{tier * 80, tier * 120};
        };

        this.creditReward = r.nextInt(source[0], source[1] + 1);
    }

    public int getReward()
    {
        return this.creditReward;
    }

    public List<AbstractObjective> getObjectives()
    {
        return this.objectives;
    }
}
