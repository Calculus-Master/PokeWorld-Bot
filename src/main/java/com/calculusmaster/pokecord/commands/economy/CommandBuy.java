package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.Nature;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.registry.MoveTutorRegistry;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.enums.Prices;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class CommandBuy extends Command
{
    public CommandBuy(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.ACCESS_BUY_SHOP)) return this.invalidMasteryLevel(Feature.ACCESS_BUY_SHOP);

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

            if(n == null) this.response = "Invalid nature!";
            else if(this.playerData.getCredits() < Prices.SHOP_NATURE.get()) this.invalidCredits(Prices.SHOP_NATURE.get());
            else
            {
                selected.setNature(n.toString());

                this.playerData.changeCredits(-1 * Prices.SHOP_NATURE.get());

                this.response = selected.getName() + "'s Nature was changed to " + Global.normalize(n.toString());
            }
        }
        else if(candy)
        {
            int requestedNum = 1;
            if(this.msg.length > 2 && this.isNumeric(2) && this.getInt(2) > 0) requestedNum = Math.min(100, this.getInt(2));

            int num = Math.min(requestedNum, 100 - selected.getLevel());
            int cost = num * Prices.SHOP_CANDY.get();

            if(num == 0) this.response = selected.getName() + " is already at the maximum level!";
            else if(this.playerData.getCredits() < cost) this.invalidCredits(cost);
            else
            {
                this.playerData.changeCredits(-1 * cost);
                selected.setLevel(selected.getLevel() + num);

                this.response = "Bought `" + num + "` Rare Candies for " + cost + "c! " + selected.getName() + " is now **Level " + selected.getLevel() + "**!";
            }
        }
        else if(item)
        {
            if(!this.isNumeric(2) || this.getInt(2) < 1 || this.getInt(2) > CommandShop.ITEM_PRICES.size())
            {
                this.response = "Invalid item number!";
                return this;
            }

            int amount = 1;
            if(this.msg.length == 4 && this.isNumeric(3)) amount = this.getInt(3);

            int cost = CommandShop.ITEM_PRICES.get(this.getInt(2) - 1) * amount;

            if(this.playerData.getCredits() < cost) this.invalidCredits(cost);
            else
            {
                Item i = CommandShop.ITEM_ENTRIES.get(this.getInt(2) - 1);

                this.playerData.changeCredits(-1 * cost);
                this.playerData.addItem(i.toString());

                final int amt = amount;
                this.playerData.updateBountyProgression(ObjectiveType.BUY_ITEMS, b -> b.update(amt));

                this.response = "Bought " + (amount > 1 ? amount + "x" : "") + "`" + i.getStyledName() + "` for " + cost + "c!";
            }
        }
        else if(form)
        {
            if(this.insufficientMasteryLevel(Feature.ACQUIRE_POKEMON_FORMS)) return this.invalidMasteryLevel(Feature.ACQUIRE_POKEMON_FORMS);

            String requestedForm = Global.normalize(this.getMultiWordContent(2));

            if(!selected.hasForms()) this.response = selected.getName() + " does not have any forms!";
            else if(!this.isPokemon(requestedForm)) this.response = "Invalid form name!";
            else if(Arrays.asList("Aegislash", "Aegislash Blade").contains(selected.getName())) this.response = selected.getName() + "'s forms cannot be purchased!";
            else if(this.playerData.getOwnedForms().contains(requestedForm)) this.response = "You already own this form!";
            else if(this.playerData.getCredits() < Prices.SHOP_FORM.get()) this.invalidCredits(Prices.SHOP_FORM.get());
            else
            {
                this.response = selected.getName() + " transformed into `" + requestedForm + "`!";

                this.playerData.addOwnedForm(requestedForm);
                this.playerData.changeCredits(-1 * Prices.SHOP_FORM.get());

                selected.changeForm(requestedForm);

                Achievements.grant(this.player.getId(), Achievements.BOUGHT_FIRST_FORM, this.event);
            }
        }
        else if(mega)
        {
            if(this.insufficientMasteryLevel(Feature.ACQUIRE_POKEMON_MEGA_EVOLUTIONS)) return this.invalidMasteryLevel(Feature.ACQUIRE_POKEMON_MEGA_EVOLUTIONS);
            else if(this.msg.length != 2 && this.msg.length != 3) this.response = CommandInvalid.getShort();
            else if(!selected.hasMega()) this.response = selected.getName() + " cannot Mega Evolve!";
            else if(this.playerData.getCredits() < Prices.SHOP_MEGA.get()) this.invalidCredits(Prices.SHOP_MEGA.get());
            else if(selected.getMegaList().size() == 1 && this.msg.length == 3) this.response = "Use `p!buy mega` instead!";
            else if(selected.getMegaList().size() == 2 && this.msg.length == 2) this.response = "Use `p!buy mega x` or `p!buy mega y` instead!";
            else if(this.msg.length == 3 && (!this.msg[2].equals("x") && !this.msg[2].equals("y"))) this.response = "Use either `p!buy mega x` or `p!buy mega y`!";
            else
            {
                String requestedMega = selected.getMegaList().get(this.msg.length == 2 ? 0 : (this.msg.length == 3 && this.msg[2].equals("x") ? 0 : 1));

                if(this.playerData.getOwnedMegas().contains(requestedMega)) this.response = "You already own this Mega! Use `p!mega` to Mega Evolve your Pokemon!";
                else
                {
                    this.response = selected.getName() + " Mega Evolved!";

                    this.playerData.addOwnedMegas(requestedMega);
                    this.playerData.changeCredits(-1 * Prices.SHOP_MEGA.get());

                    selected.changeForm(requestedMega);

                    Achievements.grant(this.player.getId(), Achievements.BOUGHT_FIRST_MEGA, this.event);
                }
            }
        }
        else if(tm || tr)
        {
            if(this.insufficientMasteryLevel(Feature.ACCESS_TMS) || this.insufficientMasteryLevel(Feature.ACCESS_TRS))
            {
                this.invalidMasteryLevel(Feature.ACCESS_TMS);
                return this.invalidMasteryLevel(Feature.ACCESS_TRS);
            }

            this.msg[2] = this.msg[2].replaceAll("tm", "").replaceAll("tr", "").trim();

            boolean numberError = !this.isNumeric(2) || ((tm && TM.isOutOfBounds(this.getInt(2))) || (tr && TR.isOutOfBounds(this.getInt(2))));
            int cost = tm ? CommandShop.TM_PRICE : CommandShop.TR_PRICE;

            if(numberError) this.response = "Invalid " + (tm ? "TM" : "TR") + " number!";
            else if(this.playerData.getCredits() < cost) this.invalidCredits(cost);
            else if(tm)
            {
                TM request = TM.get(this.getInt(2));

                if(!CommandShop.TM_ENTRIES.contains(request)) this.response = "`" + request + "` is not in the shop right now!";
                else
                {
                    this.playerData.addTM(request.toString());
                    this.playerData.changeCredits(-1 * cost);

                    this.response = "Successfully bought `" + request.getShopEntry().replaceAll("`", "") + "`!";
                }
            }
            else if(tr)
            {
                TR request = TR.get(this.getInt(2));

                if(!CommandShop.TR_ENTRIES.contains(request)) this.response = "`" + request + "` is not in the shop right now!";
                else
                {
                    this.playerData.addTR(request.toString());
                    this.playerData.changeCredits(-1 * cost);

                    this.response = "Successfully bought `" + request.getShopEntry() + "`!";
                }
            }
        }
        else if(movetutor)
        {
            if(this.insufficientMasteryLevel(Feature.PURCHASE_MOVE_TUTOR_MOVES)) return this.invalidMasteryLevel(Feature.PURCHASE_MOVE_TUTOR_MOVES);

            String move = Global.normalize(this.getMultiWordContent(1));

            if(!Move.isMove(move)) this.response = "Invalid move name!";
            else if(!Move.isImplemented(move)) this.response = move + " has not been implemented yet!";
            else if(!MoveTutorRegistry.MOVE_TUTOR_MOVES.contains(move)) this.response = move + " cannot be learned from a Move Tutor!";
            else if(!MoveTutorRegistry.PREDICATES.get(move).test(selected)) this.response = "Your Pokemon cannot learn `" + move + "` from a Move Tutor!";
            else if(!MoveTutorRegistry.MOVE_TUTOR_MOVES.contains(move) || !MoveTutorRegistry.PREDICATES.get(selected.getName()).test(selected)) this.response = "Your Pokemon cannot learn that Move Tutor move!";
            else if(this.playerData.getCredits() < Prices.SHOP_MOVETUTOR.get()) this.invalidCredits(Prices.SHOP_MOVETUTOR.get());
            else
            {
                this.playerData.changeCredits(-1 * Prices.SHOP_MOVETUTOR.get());
                selected.learnMove(move, 1);

                this.response = selected.getName() + " learned `" + move + "` in its first slot!";
            }
        }
        else if(zcrystal)
        {
            if(this.insufficientMasteryLevel(Feature.PURCHASE_Z_CRYSTALS)) return this.invalidMasteryLevel(Feature.PURCHASE_Z_CRYSTALS);

            if(this.msg.length != 3 && this.msg.length != 4) this.response = CommandInvalid.getShort();

            String requestedZCrystal = Global.normalize(this.msg[2] + " Z");
            ZCrystal z = ZCrystal.cast(requestedZCrystal);

            if(z == null) this.response = "Invalid Z Crystal!";
            else if(!CommandShop.ZCRYSTAL_ENTRIES.contains(z)) this.response = "This Z Crystal is not in the shop right now!";
            else if(this.playerData.getZCrystalList().contains(z.getStyledName())) this.response = "You already own this Z Crystal!";
            else if(this.playerData.getCredits() < Prices.SHOP_ZCRYSTAL.get()) this.invalidCredits(Prices.SHOP_ZCRYSTAL.get());
            else
            {
                this.playerData.addZCrystal(z.getStyledName());
                this.playerData.changeCredits(-1 * Prices.SHOP_ZCRYSTAL.get());

                Achievements.grant(this.player.getId(), Achievements.BOUGHT_FIRST_UNIQUE_ZCRYSTAL, this.event);

                this.response = "You acquired `" + z.getStyledName() + "`!";
            }
        }
        else
        {
            success = false;
            this.response = "Invalid category!";
        }

        if(success)
        {
            Pokemon.uploadPokemon(selected);

            Achievements.grant(this.player.getId(), Achievements.BOUGHT_FIRST_ITEM_SHOP, this.event);
            this.playerData.getStats().incr(PlayerStatistic.SHOP_ITEMS_BOUGHT);
        }

        return this;
    }
}
