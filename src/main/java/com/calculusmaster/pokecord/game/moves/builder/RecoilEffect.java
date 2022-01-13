package com.calculusmaster.pokecord.game.moves.builder;

public class RecoilEffect extends MoveEffect implements DamageDependentEffect
{
    private double fraction;
    private int damageDealt;

    public RecoilEffect(double fraction)
    {
        this.fraction = fraction;
    }

    @Override
    public void setDamageDealt(int damageDealt)
    {
        this.damageDealt = damageDealt;
    }

    @Override
    public String get()
    {
        int recoilDamage = (int)(this.fraction * this.damageDealt);

        this.user.damage(recoilDamage);

        return this.user.getName() + " took " + recoilDamage + " damage in recoil!";
    }
}
