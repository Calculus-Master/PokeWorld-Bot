package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;

import java.util.Random;

public class FightingMoves
{
    public String BulkUp(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.ATK, 1);
        user.changeStatMultiplier(Stat.DEF, 1);
        return "It raised " + user.getName() + "'s Attack and Defense by 1 stage!";
    }

    public String RockSmash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean lower = new Random().nextInt(100) < 50;

        if(lower) opponent.changeStatMultiplier(Stat.DEF, -1);

        return Move.simpleDamageMove(user, opponent, duel, move) + (lower ? " " + opponent.getName() + "'s Defense was lowered by 1 stage!" : "");
    }

    public String BrickBreak(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String QuickGuard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String ThunderousKick(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.changeStatMultiplier(Stat.DEF, -1);
        return Move.simpleDamageMove(user, opponent, duel, move) + " " + opponent.getName() + "'s Defense was lowered by 1 stage!";
    }

    public String Counter(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Detect(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return user.getName() + " is now protected!";
    }

    public String CloseCombat(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.DEF, -1);
        user.changeStatMultiplier(Stat.SPDEF, -1);

        return Move.simpleDamageMove(user, opponent, duel, move) + " " + user.getName() + "'s Defense and Special Defense were lowered by 1 stage!";
    }

    public String Reversal(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        double hpRatio = user.getHealth() / (double)user.getStat(Stat.HP);

        if(hpRatio > 0.7) move.setPower(20);
        else if(hpRatio > 0.35) move.setPower(40);
        else if(hpRatio > 0.2) move.setPower(80);
        else if(hpRatio > 0.1) move.setPower(100);
        else if(hpRatio > 0.04) move.setPower(150);
        else move.setPower(200);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String AuraSphere(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SecretSword(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }
}
