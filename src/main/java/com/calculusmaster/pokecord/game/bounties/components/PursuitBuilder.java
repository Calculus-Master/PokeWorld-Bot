package com.calculusmaster.pokecord.game.bounties.components;

import com.calculusmaster.pokecord.game.bounties.enums.PursuitSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PursuitBuilder
{
    public static final int PER_BOUNTY_REWARD = 150;

    private List<Bounty> bounties;

    public static PursuitBuilder create(PursuitSize s)
    {
        PursuitBuilder p = new PursuitBuilder();

        p.bounties = new ArrayList<>();

        int size = s.generateSize();
        for(int i = 0; i < size; i++)
        {
            Bounty b = Bounty.create().setReward(PER_BOUNTY_REWARD);

            if(i == size - 1) b.getObjective().setTarget(1.75);
            else if(i == size - 2) b.getObjective().setTarget(1.25);

            p.bounties.add(b);
        }

        p.bounties.get(new Random().nextInt(p.bounties.size())).getObjective().setTarget(2.0);

        return p;
    }

    public List<String> getIDs()
    {
        return this.bounties.stream().map(Bounty::getBountyID).collect(Collectors.toList());
    }

    public void build()
    {
        for(Bounty b : this.bounties) b.upload();
    }

}
