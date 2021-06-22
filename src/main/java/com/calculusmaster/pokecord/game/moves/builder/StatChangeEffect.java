package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.enums.elements.Stat;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StatChangeEffect extends MoveEffect
{
    private Map<Stat, Integer> statChanges;
    private int percent;
    private boolean userChange;

    public StatChangeEffect(Stat s, int stage, int percent, boolean userChange)
    {
        this.statChanges = new HashMap<>();
        this.statChanges.put(s, stage);

        this.percent = percent;
        this.userChange = userChange;
    }

    public StatChangeEffect add(Stat s, int stage)
    {
        this.statChanges.put(s, stage);
        return this;
    }

    @Override
    public String get()
    {
        //TODO: Combine multiple stat changes with "and"
        StringBuilder result = new StringBuilder();

        if(new Random().nextInt(100) < this.percent)
        {
            for(Stat s : this.statChanges.keySet())
            {
                if(this.statChanges.containsKey(s))
                {
                    (this.userChange ? this.user : this.opponent).changeStatMultiplier(s, this.statChanges.get(s));
                    result.append((this.userChange ? this.user : this.opponent).getName()).append("'s ").append(switch (s) {
                        case HP -> "HP";
                        case ATK -> "Attack";
                        case DEF -> "Defense";
                        case SPATK -> "Special Attack";
                        case SPDEF -> "Special Defense";
                        case SPD -> "Speed";
                    }).append(this.statChanges.get(s) > 0 ? " rose " : " was lowered ").append("by ").append(Math.abs(this.statChanges.get(s))).append(" stage").append(this.statChanges.get(s) > 1 ? "s" : "").append("! ");
                }
            }
        }

        return result.toString().trim();
    }
}
