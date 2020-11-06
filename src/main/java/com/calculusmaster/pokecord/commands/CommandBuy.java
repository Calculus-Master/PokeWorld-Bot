package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Nature;
import com.calculusmaster.pokecord.game.enums.items.XPBooster;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandBuy extends Command
{
    //Prices
    public static final int COST_MEGA = 2000;
    public static final int COST_FORM = 1500;
    public static final int COST_NATURE = 200;

    public CommandBuy(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "buy <item>");
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 1)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        Pokemon selected = this.playerData.getSelectedPokemon();

        if(this.msg[1].equals("nature") && this.msg.length == 3)
        {
            if(this.playerData.getCredits() >= CommandBuy.COST_NATURE && Nature.cast(this.msg[2]) != null)
            {
                this.embed.setDescription(selected.getName() + "'s Nature was changed from " + Global.normalCase(selected.getNature().toString()) + " to " + Global.normalCase(this.msg[2]));
                selected.setNature(this.msg[2]);
                this.playerData.changeCredits(-1 * CommandBuy.COST_NATURE);
            }
            else this.embed.setDescription("You do not have enough money!");
        }
        else if(this.msg[1].equals("form") && this.msg.length == 3)
        {
            if(selected.hasForms() && this.playerData.getCredits() >= CommandBuy.COST_FORM && selected.getFormsList().contains(Global.normalCase(this.msg[2])))
            {
                this.embed.setDescription(selected.getName() + " transformed into " + Global.normalCase(this.msg[2]));
                selected.changeForm(this.msg[2]);
                this.playerData.changeCredits(-1 * CommandBuy.COST_FORM);
            }
            else if(this.playerData.getCredits() < CommandBuy.COST_FORM)
            {
                this.embed.setDescription("You do not have enough money! You need " + (CommandBuy.COST_FORM - this.playerData.getCredits()) + " more credits!");
            }
            else this.embed.setDescription(selected.getName() + " cannot transform into " + this.msg[2]);
        }
        else if(this.msg[1].equals("mega"))
        {
            boolean hasMoney = this.playerData.getCredits() >= CommandBuy.COST_MEGA;

            if(this.msg.length == 2 && selected.hasMega() && hasMoney)
            {
                if(selected.getMegaList().size() == 1)
                {
                    this.embed.setDescription(selected.getName() + " mega evolved into " + selected.getMegaList().get(0) + "!");
                    selected.changeForm(selected.getMegaList().get(0));
                    this.playerData.changeCredits(-1 * CommandBuy.COST_MEGA);
                }
                else this.embed.setDescription("Use p!buy mega x or p!buy mega y to buy the specific evolution!");
            }
            else if(this.msg.length == 3 && selected.hasMega() && hasMoney)
            {
                if(selected.getMegaList().size() == 2)
                {
                    String chosen = Global.normalCase(selected.getMegaList().get(this.msg[2].contains("x") ? 0 : 1));
                    this.embed.setDescription(selected.getName() + " mega evolved into " + chosen + "!");
                    selected.changeForm(selected.getMegaList().get(this.msg[2].contains("x") ? 0 : 1));
                    this.playerData.changeCredits(-1 * CommandBuy.COST_MEGA);
                }
                else this.embed.setDescription("Use p!buy mega to mega evolve!");
            }
        }
        else if(this.msg[1].equals("tm"))
        {
            boolean isInTMList = CommandShop.entriesTM.stream().anyMatch(tm -> tm.toUpperCase().contains(this.msg[2].toUpperCase()));
            System.out.println(CommandShop.entriesTM + ", checking for " + this.msg[2]);
            if(isInTMList && this.playerData.getCredits() >= CommandShop.currentTMPrice)
            {
                this.playerData.addTM(this.msg[2].toUpperCase());
                this.playerData.changeCredits(-1 * CommandShop.currentTMPrice);
                this.embed.setDescription("Successfully bought " + this.msg[2].toUpperCase());
            }
            else if(!isInTMList)
            {
                this.embed.setDescription("Invalid TM!");
            }
            else this.embed.setDescription("You don't have enough money!");
        }
        else if(this.msg[1].equals("tr"))
        {
            boolean isInTRList = CommandShop.entriesTR.stream().anyMatch(tr -> tr.toUpperCase().contains(this.msg[2].toUpperCase()));
            System.out.println(CommandShop.entriesTR + ", checking for " + this.msg[2]);
            if(isInTRList && this.playerData.getCredits() >= CommandShop.currentTRPrice)
            {
                this.playerData.addTR(this.msg[2].toUpperCase());
                this.playerData.changeCredits(-1 * CommandShop.currentTRPrice);
                this.embed.setDescription("Successfully bought " + this.msg[2].toUpperCase());
            }
            else if(!isInTRList)
            {
                this.embed.setDescription("Invalid TR!");
            }
            else this.embed.setDescription("You don't have enough money!");
        }
        else if(this.msg[1].equals("xp") && this.msg.length == 3 && this.msg[2].chars().allMatch(Character::isDigit))
        {
            int val = Integer.parseInt(this.msg[2]) - 1;
            if(val >= 0 && val < XPBooster.values().length)
            {
                XPBooster xp = XPBooster.values()[val];
                if(xp.price <= this.playerData.getCredits())
                {
                    if(this.playerData.hasXPBooster()) this.embed.setDescription("You already have an active xp booster!");
                    else
                    {
                        this.playerData.addXPBooster(xp, this.event);
                        this.playerData.changeCredits(-1 * xp.price);
                        this.embed.setDescription("Successfully started `" + xp.timeForShop() + " " + xp.boost + "x ` Booster!");
                    }
                }
                else this.embed.setDescription("You don't have enough money!");
            }
            else this.embed.setDescription(CommandInvalid.getShort());
        }
        else
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        Pokemon.uploadPokemon(selected);
        this.embed.setTitle(this.player.getName());
        this.color = selected.getType()[0].getColor();
        return this;
    }
}
