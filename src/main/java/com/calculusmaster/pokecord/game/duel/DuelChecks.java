package com.calculusmaster.pokecord.game.duel;

import com.calculusmaster.pokecord.game.duel.elements.Player;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.Arrays;
import java.util.List;

public class DuelChecks
{
    /**
     * ALL METHODS RETURN TRUE IF THE PLAYER PASSES THE CHECK, FALSE OTHERWISE
     * */

    private Duel d;
    private Player p;
    private PlayerDataQuery data;
    private ZCrystal z;
    private Move move;

    public DuelChecks(String ID)
    {
        this(ID, null);
    }

    public DuelChecks(String ID, Move move)
    {
        this.d = DuelHelper.instance(ID);
        this.p = this.d.getPlayers()[this.d.indexOf(ID)];
        this.data = new PlayerDataQuery(ID);
        this.z = ZCrystal.cast(this.data.getEquippedZCrystal());

        this.move = move;
    }

    public void setMove(Move move)
    {
        this.move = move;
    }

    //Other Useful Data Storage Variables
    static final List<String> dynamaxBanList = Arrays.asList("Zacian", "Zamazenta", "Eternatus", "Ultra Necrozma");

    //Main Method
    public boolean checkFailed(CheckType check)
    {
        return !switch (check) {
            //Normal
            case NORMAL_MOVESUBMITTED -> this.checkNormal_MoveSubmitted();
            case NORMAL_FAINTED -> this.checkNormal_Fainted();
            case NORMAL_WILDDUEL -> this.checkNormal_WildDuel();
            //Swap
            case SWAP_ISABLE -> this.checkSwap_IsAble();
            case SWAP_BOUND -> this.checkSwap_Bound();
            case SWAP_DYNAMAXED -> this.checkSwap_Dynamaxed();
            //Z Move
            case ZMOVE_CRYSTAL -> this.checkZMove_Crystal();
            case ZMOVE_USED -> this.checkZMove_Used();
            case ZMOVE_DYNAMAXED -> this.checkZMove_Dynamaxed();
            case ZMOVE_MOVE -> this.checkZMove_Move();
            //Dynamax
            case DYNAMAX_USED -> this.checkDynamax_Used();
            case DYNAMAX_MEGA -> this.checkDynamax_Mega();
            case DYNAMAX_BANLIST -> this.checkDynamax_BanList();
        };
    }

    //Normal
    private boolean checkNormal_MoveSubmitted()
    {
        //Has the player already submitted a move this turn?
        return !this.d.hasPlayerSubmittedMove(this.data.getID());
    }

    private boolean checkNormal_Fainted()
    {
        //Is the active Pokemon fainted?
        return !this.p.active.isFainted();
    }

    private boolean checkNormal_WildDuel()
    {
        //Is this a Wild Duel (where no swapping, z moves, or dynamax are allowed)
        return !(this.d instanceof WildDuel);
    }

    //Swap
    private boolean checkSwap_IsAble()
    {
        //Is the active Pokemon unable to swap?
        return this.d.data(this.d.indexOf(this.data.getID())).canSwap;
    }

    private boolean checkSwap_Bound()
    {
        //Is the active Pokemon bound?
        if(this.p.active.isFainted()) return true;
        else return !this.p.active.hasStatusCondition(StatusCondition.BOUND);
    }

    private boolean checkSwap_Dynamaxed()
    {
        //Is the active Pokemon Dynamaxed?
        if(this.p.active.isFainted()) return true;
        else return !this.p.active.isDynamaxed();
    }

    //Z Move
    private boolean checkZMove_Crystal()
    {
        //Does the player have an equipped Z Crystal?
        return this.z != null;
    }

    private boolean checkZMove_Used()
    {
        //Has the player already used a Z Move?
        return !this.p.usedZMove;
    }

    private boolean checkZMove_Dynamaxed()
    {
        //Is the active Pokemon Dynamaxed?
        return !this.p.active.isDynamaxed();
    }

    private boolean checkZMove_Move()
    {
        //Is the base move compatible with the Z Crystal?
        return ZCrystal.isValid(this.z, this.move, this.p.active.getName());
    }

    //Dynamax
    private boolean checkDynamax_Used()
    {
        //Has the player already Dynamaxed?
        return !this.p.usedDynamax;
    }

    private boolean checkDynamax_Mega()
    {
        //Is the active Pokemon a mega or primal?
        return !this.p.active.getName().contains("Mega") && !this.p.active.getName().contains("Primal");
    }

    private boolean checkDynamax_BanList()
    {
        //Is the active Pokemon on the Dynamax Ban List?
        return dynamaxBanList.stream().noneMatch(ban -> this.p.active.getName().contains(ban));
    }

    //Core
    public enum CheckType
    {
        //Normal
        NORMAL_MOVESUBMITTED("You already submitted a move!"),
        NORMAL_FAINTED("Your Pokemon is fainted and can no longer battle!"),
        NORMAL_WILDDUEL("You cannot swap, use a Z-Move, or Dynamax in a Wild Pokemon Duel!"),
        //Swap
        SWAP_ISABLE("You are unable to swap out right now!"),
        SWAP_BOUND("You are unable to swap out due to the binding!"),
        SWAP_DYNAMAXED("You cannot swap out a Dynamaxed Pokemon!"),
        //Z Move
        ZMOVE_CRYSTAL("You don't have an equipped Z Crystal!"),
        ZMOVE_USED("You have already used a Z-Move!"),
        ZMOVE_DYNAMAXED("Dynamaxed Pokemon cannot use Z-Moves!"),
        ZMOVE_MOVE("Your equipped Z Crystal does not work on that move!"),
        //Dynamax
        DYNAMAX_USED("You have already Dynamaxed!"),
        DYNAMAX_MEGA("Mega and Primal Pokemon cannot Dynamax!"),
        DYNAMAX_BANLIST("This Pokemon is banned from Dynamaxing! The banned Pokemon are: " + dynamaxBanList);

        private String invalidMessage;

        CheckType(String invalidMessage)
        {
            this.invalidMessage = invalidMessage;
        }

        public String getInvalidMessage()
        {
            return this.invalidMessage;
        }
    }
}
