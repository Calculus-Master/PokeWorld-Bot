package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Weather;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugment;

import java.util.ArrayList;
import java.util.List;

public class FixedDamageEffect extends MoveEffect
{
    private int damage;

    public FixedDamageEffect(int damage)
    {
        this.damage = damage;
    }

    public FixedDamageEffect()
    {
        this(0);
    }

    public void set(int damage)
    {
        this.damage = damage;
    }

    public int getDamage()
    {
        return this.damage;
    }

    @Override
    public String get()
    {
        List<String> augment = new ArrayList<>();
        if(this.opponent.hasAugment(PokemonAugment.LIGHT_ABSORPTION) &&
                (this.duel.weather.get().equals(Weather.HARSH_SUNLIGHT) || this.duel.weather.get().equals(Weather.EXTREME_HARSH_SUNLIGHT)) &&
                this.move.is(Category.SPECIAL))
        {
            this.damage *= 0.85;
            augment.add(" Some damage was absorbed due to the Light Absorption Augment!");
        }

        // TODO: 7/17/22 Add results text for these?
        if(this.user.hasAugment(PokemonAugment.SUPERCHARGED)) this.damage *= 1.25;
        if(this.user.hasAugment(PokemonAugment.SUPERFORTIFIED)) this.damage *= 0.75;

        if(this.opponent.hasAugment(PokemonAugment.SUPERCHARGED)) this.damage *= 1.25;
        if(this.opponent.hasAugment(PokemonAugment.SUPERFORTIFIED)) this.damage *= 0.75;

        this.opponent.damage(this.damage);

        if(this.opponent.getHealth() <= 0 && this.duel.data(this.opponent.getUUID()).endureUsed)
        {
            this.opponent.setHealth(1);
            this.duel.data(this.opponent.getUUID()).endureUsed = false;
        }

        if(this.opponent.getHealth() <= 0 && this.move.getName().equals("Hold Back")) this.opponent.setHealth(1);

        this.duel.data(this.opponent.getUUID()).lastDamageTaken = this.damage;

        return this.move.getDamageResult(this.opponent, this.damage) + String.join(" ", augment);
    }
}
