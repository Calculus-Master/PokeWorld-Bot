package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.elements.Weather;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
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
        //Augment Effects
        List<String> augment = new ArrayList<>();

        if(this.opponent.hasAugment(PokemonAugment.LIGHT_ABSORPTION) &&
                (this.duel.weather.get().equals(Weather.HARSH_SUNLIGHT) || this.duel.weather.get().equals(Weather.EXTREME_HARSH_SUNLIGHT)) &&
                this.move.is(Category.SPECIAL))
        {
            this.damage *= 0.85;
            augment.add(" Some damage was absorbed due to the %s Augment!".formatted(PokemonAugment.LIGHT_ABSORPTION.getAugmentName()));
        }

        if(this.user.hasAugment(PokemonAugment.SUPERCHARGED)) this.damage *= 1.25;
        if(this.user.hasAugment(PokemonAugment.SUPERFORTIFIED)) this.damage *= 0.75;

        if(this.opponent.hasAugment(PokemonAugment.SUPERCHARGED)) this.damage *= 1.25;
        if(this.opponent.hasAugment(PokemonAugment.SUPERFORTIFIED)) this.damage *= 0.75;

        if(this.duel.getSize() != 1 &&
                (this.user.hasAugment(PokemonAugment.DIFFRACTED_BEAMS) || this.user.hasAugment(PokemonAugment.RADIANT_DIFFRACTED_BEAMS)) &&
                List.of("Charge Beam", "Prismatic Laser", "Solar Beam", "Hyper Beam", "Flash Cannon").contains(this.move.getName()))
        {
            boolean radiant = this.user.hasAugment(PokemonAugment.RADIANT_DIFFRACTED_BEAMS);

            int totalTeamDamage = this.damage / 2;
            this.damage *= 0.5;

            List<Pokemon> targetTeam = this.duel.getPlayers()[this.duel.playerIndexFromUUID(this.opponent.getUUID())].team;
            long targets = targetTeam.stream().filter(p -> !p.isFainted()).count() - 1;

            int diffractionDamage = (int)(totalTeamDamage / targets);
            if(radiant) diffractionDamage *= 1.1;

            for(Pokemon p : targetTeam.stream().filter(p -> !p.isFainted()).filter(p -> !p.getUUID().equals(this.opponent.getUUID())).toList())
                p.damage(diffractionDamage);

            augment.add(" The beam diffracted due to the %s Augment! Each opposing team member took %s damage!".formatted(radiant ? PokemonAugment.DIFFRACTED_BEAMS.getAugmentName() : PokemonAugment.RADIANT_DIFFRACTED_BEAMS.getAugmentName(), diffractionDamage));
        }

        if(this.opponent.hasAugment(PokemonAugment.PRISMATIC_MOONLIT_SHIELD))
        {
            if(this.move.is(Type.DARK)) this.damage *= 0.0;
            else if(this.move.is(Type.GHOST)) this.damage *= 0.25;

            augment.add("The %s Augment protected %s from the damage!".formatted(PokemonAugment.PRISMATIC_MOONLIT_SHIELD.getAugmentName(), this.opponent.getName()));
        }

        if(this.user.hasAugment(PokemonAugment.SPECTRAL_AMPLIFICATION) && this.move.is(Type.GHOST))
        {
            this.damage *= 2.5;
            this.user.changes().change(Stat.ATK, -2);
            this.user.changes().change(Stat.DEF, -2);

            augment.add("The damaged was amplified greatly due to the %s Augment! %s's Attack and Defense were lowered by 2 stages!".formatted(PokemonAugment.SPECTRAL_AMPLIFICATION.getAugmentName(), this.user.getName()));
        }

        //Core Logic
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
