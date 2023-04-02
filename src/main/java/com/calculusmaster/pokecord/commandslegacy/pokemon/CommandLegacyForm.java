package com.calculusmaster.pokecord.commandslegacy.pokemon;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandLegacyForm extends CommandLegacy
{
    public CommandLegacyForm(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.ACQUIRE_POKEMON_FORMS)) return this.invalidMasteryLevel(Feature.ACQUIRE_POKEMON_FORMS);

        if(this.msg.length == 1)
        {
            this.embed.setDescription("You need to include the form name!");
            return this;
        }

        String form = this.getForm();
        Pokemon s = this.playerData.getSelectedPokemon();

        this.response = "Switching forms is currently disabled.";
//        if(!this.isPokemon(form)) this.response = "`" + form + "` is not a valid form!";
//        else if(!this.playerData.getOwnedForms().contains(form)) this.response = "You don't own `" + form + "`! (Buy forms in the shop!)";
//        else if(!s.getFormsList().contains(form)) this.response = s.getRealName() + " can't transform into `" + form + "`!";
//        else
//        {
//            this.response = s.getRealName() + " transformed into `" + form + "`!";
//
//            s.changeForm(form);
//            s.updateName();
//
//            s.resetAugments();
//        }

        return this;
    }

    private String getForm()
    {
        StringBuilder form = new StringBuilder();
        for(int i = 1; i < this.msg.length; i++) form.append(this.msg[i]).append(" ");

        return Global.normalize(form.toString().trim());
    }
}
