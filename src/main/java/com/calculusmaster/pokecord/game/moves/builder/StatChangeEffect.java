package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.enums.elements.Ability;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.*;

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

    public StatChangeEffect(int stage, int percent, boolean userChange)
    {
        this(Stat.HP, stage, percent, userChange);
        Arrays.stream(Stat.values()).filter(s -> !s.equals(Stat.HP)).forEach(s -> this.add(s, stage));
        this.statChanges.remove(Stat.HP);
    }

    public StatChangeEffect add(Stat s, int stage)
    {
        this.statChanges.put(s, stage);
        return this;
    }

    @Override
    public String get()
    {
        StringBuilder result = new StringBuilder();

        Pokemon target = this.userChange ? this.user : this.opponent;

        Map<Integer, List<Stat>> changesMap = new HashMap<>();
        List<Stat> mistImmuneStats = new ArrayList<>();

        if(new Random().nextInt(100) < this.percent)
        {
            for(Stat s : this.statChanges.keySet())
            {
                if(this.statChanges.containsKey(s))
                {
                    if(this.statChanges.get(s) < 0 && target.hasAbility(Ability.BIG_PECKS) && s.equals(Stat.DEF))
                        result.append(Ability.BIG_PECKS.formatActivation(target.getName(), target.getName() + "'s defense was not lowered!"));
                    else if(this.statChanges.get(s) < 0 && this.duel.data(target.getUUID()).mistTurns > 0) mistImmuneStats.add(s);
                    else
                    {
                        int change = this.statChanges.get(s);

                        target.changes().change(s, change);

                        if(!changesMap.containsKey(change)) changesMap.put(change, new ArrayList<>());
                        changesMap.get(change).add(s);
                    }
                }
            }
        }

        //Compile generic changes
        for(Map.Entry<Integer, List<Stat>> e : changesMap.entrySet())
        {
            StringJoiner joiner = new StringJoiner(" ");
            joiner.add(target.getName() + "'s");
            joiner.add(this.compileSeries(e.getValue()));
            joiner.add(e.getKey() > 0 ? "rose by" : "fell by");
            joiner.add(Math.abs(e.getKey()) + (Math.abs(e.getKey()) == 1 ? " stage" : " stages"));

            result.append(joiner).append("!");
        }

        //Compile mist messages
        if(!mistImmuneStats.isEmpty())
            result.append(target.getName()).append(" is immune to the change in ").append(this.compileSeries(mistImmuneStats)).append("!");

        return result.toString().trim();
    }

    private String compileSeries(List<Stat> stat)
    {
        List<String> names = stat.stream().map(s -> s.name).toList();

        if(names.size() == 1) return names.get(0);
        else if(names.size() == 2) return names.get(0) + " and " + names.get(1);
        else
        {
            return String.join(", ", names.subList(0, names.size() - 1)) + ", and " + names.get(names.size() - 1);
        }
    }
}
