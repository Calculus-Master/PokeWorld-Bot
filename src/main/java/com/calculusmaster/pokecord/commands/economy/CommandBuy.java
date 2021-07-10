package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Nature;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class CommandBuy extends Command
{
    //Prices
    public static final int COST_MEGA = 2000;
    public static final int COST_FORM = 1500;
    public static final int COST_NATURE = 200;
    public static final int COST_RARE_CANDY = 500;
    public static final int COST_MOVETUTOR = 10000;

    public CommandBuy(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 1)
        {
            this.embed.setDescription("You have to specify a category! Valid categories are: `nature`, `candy`, `item`, `form`, `mega`, `tm`, `tr`, `movetutor`, `zcrystal`");
            return this;
        }

        //p!buy nature <nature>
        boolean nature = this.msg.length == 3 && this.msg[1].equals("nature");
        //p!buy candy <amount>
        boolean candy = this.msg[1].equals("candy");
        //p!buy item <index> or p!buy item <index> <amount>
        boolean item = this.msg.length >= 3 && this.msg[1].equals("item");
        //p!buy form <name>
        boolean form = this.msg.length >= 3 && this.msg[1].equals("form");
        //p!buy mega or p!buy mega <x:y>
        boolean mega = this.msg[1].equals("mega");
        //p!buy tm <tm>
        boolean tm = this.msg[1].equals("tm");
        //p!buy tm <tr>
        boolean tr = this.msg[1].equals("tr");
        //p!buy movetutor <move>
        boolean movetutor = this.msg.length >= 3 && Arrays.asList("movetutor", "move", "tutor", "mt").contains(this.msg[1]);
        //p!buy zcrystal <zcrystal>
        boolean zcrystal = this.msg.length >= 3 && Arrays.asList("zcrystal", "z", "zc").contains(this.msg[1]);

        Pokemon selected = this.playerData.getSelectedPokemon();
        boolean success = true;

        if(nature)
        {
            Nature n = Nature.cast(this.msg[2]);

            if(n == null) this.sendMsg("Invalid nature!");
            else if(this.playerData.getCredits() < COST_NATURE) this.sendInvalidCredits(COST_NATURE);
            else
            {
                selected.setNature(n.toString());

                this.playerData.changeCredits(-1 * COST_NATURE);

                this.sendMsg(selected.getName() + "'s Nature was changed to " + Global.normalCase(n.toString()));
            }
        }
        else if(candy)
        {
            int requestedNum = 1;
            if(this.msg.length > 2 && this.isNumeric(2) && this.getInt(2) > 0) requestedNum = Math.min(100, this.getInt(2));

            int num = Math.min(requestedNum, 100 - selected.getLevel());
            int cost = num * COST_RARE_CANDY;

            if(num == 0) this.sendMsg(selected.getName() + " is already at the maximum level!");
            else if(this.playerData.getCredits() < cost) this.sendInvalidCredits(cost);
            else
            {
                this.playerData.changeCredits(-1 * cost);
                selected.setLevel(selected.getLevel() + num);

                this.sendMsg("Bought `" + num + "` Rare Candies for " + cost + "c! " + selected.getName() + " is now **Level " + selected.getLevel() + "**!");
            }
        }
        else if(item)
        {
            if(!this.isNumeric(2) || this.getInt(2) < 1 || this.getInt(2) > CommandShop.itemPrices.size())
            {
                this.sendMsg("Invalid item number!");
                return this;
            }

            int amount = 1;
            if(this.msg.length == 4 && this.isNumeric(3)) amount = this.getInt(3);

            int cost = CommandShop.itemPrices.get(this.getInt(2) - 1) * amount;

            if(this.playerData.getCredits() < cost) this.sendInvalidCredits(cost);
            else
            {
                PokeItem i = CommandShop.entriesItem.get(this.getInt(2) - 1);

                this.playerData.changeCredits(-1 * cost);
                this.playerData.addItem(i.toString());

                this.sendMsg("Bought " + (amount > 1 ? amount + "x" : "") + "`" + i.getStyledName() + "` for " + cost + "c!");
            }
        }
        else if(form)
        {
            String requestedForm = Global.normalCase(this.getMultiWordContent(2));

            if(!selected.hasForms()) this.sendMsg(selected.getName() + " does not have any forms!");
            else if(!Global.POKEMON.contains(requestedForm)) this.sendMsg("Invalid form name!");
            else if(Arrays.asList("Aegislash", "Aegislash Blade").contains(selected.getName())) this.sendMsg(selected.getName() + "'s forms cannot be purchased!");
            else if(this.playerData.getOwnedForms().contains(requestedForm)) this.sendMsg("You already own this form!");
            else if(this.playerData.getCredits() < COST_FORM) this.sendInvalidCredits(COST_FORM);
            else
            {
                this.sendMsg(selected.getName() + " transformed into `" + requestedForm + "`!");

                this.playerData.addOwnedForm(requestedForm);
                this.playerData.changeCredits(-1 * COST_FORM);

                selected.changeForm(requestedForm);

                Achievements.grant(this.player.getId(), Achievements.BOUGHT_FIRST_FORM, this.event);
            }
        }
        else if(mega)
        {
            if(this.msg.length != 2 && this.msg.length != 3) this.sendMsg(CommandInvalid.getShort());
            else if(!selected.hasMega()) this.sendMsg(selected.getName() + " cannot Mega Evolve!");
            else if(this.playerData.getCredits() < COST_MEGA) this.sendInvalidCredits(COST_MEGA);
            else if(selected.getMegaList().size() == 1 && this.msg.length == 3) this.sendMsg("Use `p!buy mega` instead!");
            else if(selected.getMegaList().size() == 2 && this.msg.length == 2) this.sendMsg("Use `p!buy mega x` or `p!buy mega y` instead!");
            else if(this.msg.length == 3 && (!this.msg[2].equals("x") && !this.msg[2].equals("y"))) this.sendMsg("Use either `p!buy mega x` or `p!buy mega y`!");
            else
            {
                String requestedMega = selected.getMegaList().get(this.msg.length == 2 ? 0 : (this.msg.length == 3 && this.msg[2].equals("x") ? 0 : 1));

                if(this.playerData.getOwnedMegas().contains(requestedMega)) this.sendMsg("You already own this Mega! Use `p!mega` to Mega Evolve your Pokemon!");
                else
                {
                    this.sendMsg(selected.getName() + " Mega Evolved!");

                    this.playerData.addOwnedMegas(requestedMega);
                    this.playerData.changeCredits(-1 * COST_MEGA);

                    selected.changeForm(requestedMega);

                    Achievements.grant(this.player.getId(), Achievements.BOUGHT_FIRST_MEGA, this.event);
                }
            }
        }
        else if(tm || tr)
        {
            this.msg[2] = this.msg[2].replaceAll("tm", "").replaceAll("tr", "").trim();

            boolean numberError = !this.isNumeric(2) || ((tm && TM.isOutOfBounds(this.getInt(2))) || (tr && TR.isOutOfBounds(this.getInt(2))));
            int cost = tm ? CommandShop.currentTMPrice : CommandShop.currentTRPrice;

            if(numberError) this.sendMsg("Invalid " + (tm ? "TM" : "TR") + " number!");
            else if(this.playerData.getCredits() < cost) this.sendInvalidCredits(cost);
            else if(tm)
            {
                TM request = TM.get(this.getInt(2));

                if(!CommandShop.entriesTM.contains(request)) this.sendMsg("`" + request + "` is not in the shop right now!");
                else
                {
                    this.playerData.addTM(request.toString());
                    this.playerData.changeCredits(-1 * cost);

                    this.sendMsg("Successfully bought `" + request.getShopEntry().replaceAll("`", "") + "`!");
                }
            }
            else if(tr)
            {
                TR request = TR.get(this.getInt(2));

                if(!CommandShop.entriesTR.contains(request)) this.sendMsg("`" + request + "` is not in the shop right now!");
                else
                {
                    this.playerData.addTR(request.toString());
                    this.playerData.changeCredits(-1 * cost);

                    this.sendMsg("Successfully bought `" + request.getShopEntry() + "`!");
                }
            }
        }
        else if(movetutor)
        {
            String move = Global.normalCase(this.getMultiWordContent(1));

            if(!Move.isMove(move)) this.sendMsg(Move.INCOMPLETE_MOVES.contains(move) ? "`" + move + "` has not been implemented yet!" : "Invalid move!");
            else if(!Move.MOVE_TUTOR_MOVES.containsKey(selected.getName()) || !Move.MOVE_TUTOR_MOVES.get(selected.getName()).test(selected)) this.sendMsg("Your Pokemon cannot learn that Move Tutor move!");
            else if(this.playerData.getCredits() < COST_MOVETUTOR) this.sendInvalidCredits(COST_MOVETUTOR);
            else
            {
                this.playerData.changeCredits(-1 * COST_MOVETUTOR);
                selected.learnMove(move, 1);

                this.sendMsg(selected.getName() + " learned `" + move + "` in its first slot!");
            }
        }
        else if(zcrystal)
        {
            if(this.msg.length != 3 && this.msg.length != 4) this.sendMsg(CommandInvalid.getShort());

            String requestedZCrystal = Global.normalCase(this.msg[2] + " Z");
            ZCrystal z = ZCrystal.cast(requestedZCrystal);

            if(z == null) this.sendMsg("Invalid Z Crystal!");
            else if(this.playerData.getZCrystalList().contains(z.getStyledName())) this.sendMsg("You already own this Z Crystal!");
            else if(this.playerData.getCredits() < CommandShop.priceZCrystal) this.sendInvalidCredits(CommandShop.priceZCrystal);
            else
            {
                this.playerData.addZCrystal(z.getStyledName());
                this.playerData.changeCredits(-1 * CommandShop.priceZCrystal);

                Achievements.grant(this.player.getId(), Achievements.BOUGHT_FIRST_UNIQUE_ZCRYSTAL, this.event);

                this.sendMsg("You acquired `" + z.getStyledName() + "`!");
            }
        }
        else
        {
            success = false;
            this.sendMsg("Invalid category!");
        }

        if(success)
        {
            Pokemon.uploadPokemon(selected);

            Achievements.grant(this.player.getId(), Achievements.BOUGHT_FIRST_ITEM_SHOP, this.event);
            this.playerData.getStats().incr(PlayerStatistic.SHOP_ITEMS_BOUGHT);

            this.playerData.addPokePassExp(200, this.event);
        }

        return this;
    }
}
