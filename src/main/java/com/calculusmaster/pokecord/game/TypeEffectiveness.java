package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.enums.Type;

import java.util.HashMap;
import java.util.Map;

public class TypeEffectiveness
{
    private enum EffectType
    {
        NORMAL("", "Fighting", "Ghost"),
        FIRE("Fire-Grass-Ice-Bug-Steel-Fairy", "Water-Ground-Rock"),
        WATER("Fire-Water-Ice-Steel", "Electric-Grass"),
        ELECTRIC("Electric-Flying-Steel", "Ground"),
        GRASS("Water-Electric-Grass-Ground", "Fire-Ice-Poison-Flying-Bug"),
        ICE("Ice", "Fire-Fighting-Rock-Steel"),
        FIGHTING("Bug-Rock-Dark", "Flying-Psychic-Fairy"),
        POISON("Grass-Fighting-Poison-Bug-Fairy", "Ground-Psychic"),
        GROUND("Poison-Rock", "Water-Grass-Ice", "Electric"),
        FLYING("Grass-Fighting-Bug", "Electric-Ice-Rock", "Ground"),
        PSYCHIC("Fighting-Psychic", "Bug-Ghost-Dark"),
        BUG("Grass-Fighting-Ground", "Fire-Flying-Rock"),
        ROCK("Normal-Fire-Poison-Flying", "Water-Grass-Fighting-Ground-Steel"),
        GHOST("Poison-Bug", "Ghost-Dark", "Normal-Fighting"),
        DRAGON("Fire-Water-Electric-Grass", "Ice-Dragon-Fairy"),
        DARK("Ghost-Dark", "Fighting-Bug-Fairy", "Psychic"),
        STEEL("Normal-Grass-Ice-Flying-Psychic-Bug-Rock-Dragon-Steel-Fairy", "Fire-Fighting-Ground", "Poison"),
        FAIRY("Fighting-Bug-Dark", "Poison-Steel", "Dragon");

        private Map<Type, Double> effectMap;
        EffectType(String notEffect, String superEffect, String zeroEffect)
        {
            this.effectMap = new HashMap<>();

            for(Type t : this.getStringAsTypeArray(notEffect)) this.effectMap.put(t, 0.5);
            for(Type t : this.getStringAsTypeArray(superEffect)) this.effectMap.put(t, 2.0);
            for(Type t : this.getStringAsTypeArray(zeroEffect)) this.effectMap.put(t, 0.0);

            this.assignRemainingTypes();
        }

        EffectType(String notEffect, String superEffect)
        {
            this(notEffect, superEffect, "");
        }

        public Map<Type, Double> getMap()
        {
            return this.effectMap;
        }

        private Type[] getStringAsTypeArray(String str)
        {
            String[] split = str.split("-");
            Type[] out = new Type[split.length];
            for(int i = 0; i < split.length; i++) out[i] = Type.cast(split[i]);

            //return (Type[]) Arrays.stream(split).map(Type::cast).toArray();
            return out;
        }

        private void assignRemainingTypes()
        {
            for(Type t : Type.values()) if(!this.effectMap.containsKey(t)) this.effectMap.put(t, 1.0);
        }

        public static EffectType getEffectForType(Type t)
        {
            for(EffectType eT : values()) if(t.toString().equals(eT.toString())) return eT;
            throw new IllegalStateException("Something went wrong: TypeEffectiveness.EffectType.class");
        }
    }

    public static Map<Type, Double> getCombinedMap(Type t1, Type t2)
    {
        Map<Type, Double> mapT1 = EffectType.getEffectForType(t1).getMap();
        Map<Type, Double> mapT2 = EffectType.getEffectForType(t2).getMap();

        Map<Type, Double> out = new HashMap<>();
        for(Type t : Type.values()) out.put(t, mapT1.get(t) * (t1.equals(t2) ? 1 : mapT2.get(t)));
        return out;
    }
}
