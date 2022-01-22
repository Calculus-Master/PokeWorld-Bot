package com.calculusmaster.pokecord.game.duel.component;

import com.calculusmaster.pokecord.game.enums.elements.EntryHazard;

import java.util.HashMap;
import java.util.Map;

public class EntryHazardHandler
{
    private Map<EntryHazard, Integer> entryHazards;

    public EntryHazardHandler()
    {
        this.entryHazards = new HashMap<>();

        this.clearHazards();
    }

    public void addHazard(EntryHazard hazard)
    {
        int current = this.entryHazards.get(hazard);
        int hazardLimit = hazard.equals(EntryHazard.SPIKES) ? 3 : (hazard.equals(EntryHazard.TOXIC_SPIKES) ? 2 : 1);

        this.entryHazards.put(hazard, Math.min(hazardLimit, current + 1));
    }

    public boolean hasHazard(EntryHazard hazard)
    {
        return this.entryHazards.get(hazard) > 0;
    }

    public int getHazard(EntryHazard hazard)
    {
        return this.entryHazards.get(hazard);
    }

    public void removeHazard(EntryHazard hazard)
    {
        this.entryHazards.put(hazard, 0);
    }

    public void clearHazards()
    {
        this.removeHazard(EntryHazard.SPIKES);
        this.removeHazard(EntryHazard.STEALTH_ROCK);
        this.removeHazard(EntryHazard.STICKY_WEB);
        this.removeHazard(EntryHazard.TOXIC_SPIKES);
    }
}
