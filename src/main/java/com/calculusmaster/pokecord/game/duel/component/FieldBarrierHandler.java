package com.calculusmaster.pokecord.game.duel.component;

import com.calculusmaster.pokecord.game.enums.elements.FieldBarrier;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class FieldBarrierHandler
{
    private EnumSet<FieldBarrier> barriers;
    private Map<FieldBarrier, Integer> barrierTurns;

    public FieldBarrierHandler()
    {
        this.barriers = EnumSet.noneOf(FieldBarrier.class);
        this.barrierTurns = new HashMap<>();
    }

    public void addBarrier(FieldBarrier barrier, boolean lightClay)
    {
        if(this.has(barrier)) return;

        this.barriers.add(barrier);
        this.barrierTurns.put(barrier, lightClay ? 8 : 5);
    }

    public void removeBarrier(FieldBarrier barrier)
    {
        this.barriers.remove(barrier);
        this.barrierTurns.remove(barrier);
    }

    public boolean has(FieldBarrier barrier)
    {
        return this.barriers.contains(barrier);
    }

    public void updateTurns()
    {
        this.barrierTurns.replaceAll((barrier, currentTurns) -> currentTurns--);

        for(FieldBarrier b : FieldBarrier.values())
            if(this.has(b) && this.barrierTurns.get(b) <= 0) this.removeBarrier(b);
    }
}
