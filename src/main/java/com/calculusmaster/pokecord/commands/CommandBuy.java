package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.Nature;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandBuy extends Command
{
    //Prices
    public static final int COST_MEGA = 2000;
    public static final int COST_FORM = 1500;
    public static final int COST_NATURE = 200;

    //TODO: WIP (buy)
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
            //TODO: Validate nature
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
