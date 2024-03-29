package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.component.EntryHazardHandler;
import com.calculusmaster.pokecord.game.duel.component.FieldBarrierHandler;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.FieldEffect;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.List;

public class FlyingMoves
{
    public String AirSlash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.FLINCHED, 30)
                .execute();
    }

    public String Gust(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(opponent.getUUID()).flyUsed || duel.data(opponent.getUUID()).bounceUsed) move.setPower(2 * move.getPower());
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Tailwind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        for(Pokemon p : duel.getPlayers()[duel.playerIndexFromUUID(user.getUUID())].team) p.overrides().set(Stat.SPD, p.getStat(Stat.SPD) * 2);

        duel.fieldEffects[duel.playerIndexFromUUID(user.getUUID())].add(FieldEffect.TAILWIND, 4);

        return user.getName() + " boosted the speed of its team!";
    }

    public String Roost(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(1 / 2D)
                .execute();
    }

    public String Hurricane(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.CONFUSED, 30);
    }

    public String Peck(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String DrillPeck(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Pluck(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String WingAttack(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String SkyAttack(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .addStatusEffect(StatusCondition.FLINCHED, 30)
                .execute();
    }

    public String FeatherDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.ATK, -2, 100, false)
                .execute();
    }

    public String DragonAscent(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.DEF, -1, 100, true)
                                .add(Stat.SPDEF, -1))
                .execute();
    }

    public String AerialAce(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Fly(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(user.getUUID()).flyUsed)
        {
            duel.data(user.getUUID()).flyUsed = false;
            return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
        }
        else
        {
            duel.data(user.getUUID()).flyUsed = true;
            return user.getName() + " flew high up!";
        }
    }

    public String Bounce(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(user.getUUID()).bounceUsed)
        {
            duel.data(user.getUUID()).bounceUsed = false;
            return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.PARALYZED, 30);
        }
        else
        {
            duel.data(user.getUUID()).bounceUsed = true;
            return user.getName() + " sprung up!";
        }
    }

    public String BraveBird(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addRecoilEffect(1 / 3D)
                .execute();
    }

    public String Defog(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        for(int i = 0; i < duel.getPlayers().length; i++)
        {
            duel.entryHazards[i] = new EntryHazardHandler();
            duel.barriers[i] = new FieldBarrierHandler();

            duel.data(i).mistTurns = 0;
        }

        return "All Entry Hazards and Barriers were removed! " + MoveEffectBuilder.make(user, opponent, duel, move)
                .addEvasionChangeEffect(-1, 100, false)
                .execute();
    }

    public String MirrorMove(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.getLastUsedMove(user.getUUID()) == null || duel.getLastUsedMove(user.getUUID()).equals("Mirror Move")) return move.getNothingResult();
        else return new Move(duel.getLastUsedMove(user.getUUID())).logic(user, opponent, duel);
    }

    public String SkyDrop(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String OblivionWing(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addDamageHealEffect(3 / 4D)
                .execute();
    }

    public String Chatter(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.CONFUSED, 100);
    }

    public String Aeroblast(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String AirCutter(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String Acrobatics(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCustomRunnableEffect(() -> move.setPower(!user.hasItem() ? 2.0 : 1.0))
                .addDamageEffect()
                .execute();
    }

    public String DualWingbeat(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedMultiStrikeEffect(2)
                .execute();
    }

    public String BeakBlast(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCustomEffect(() -> {
                    if(duel.first.equals(opponent.getUUID()))
                    {
                        List<MoveEntity> moves = duel.getMovesUsed(opponent.getUUID());
                        if(moves.get(moves.size() - 1).data().getCategory().equals(Category.PHYSICAL))
                        {
                            opponent.addStatusCondition(StatusCondition.BURNED);
                            return opponent.getName() + " is burned!";
                        }
                        else return "";
                    }
                    else return "";
                })
                .addDamageEffect()
                .execute();
    }

    public String BleakwindStorm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPD, -1, 30, false)
                .execute();
    }
}
