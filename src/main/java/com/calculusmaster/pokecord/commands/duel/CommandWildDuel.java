package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.duel.WildDuel;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandWildDuel extends Command
{
    public static final List<String> HP = new ArrayList<>();
    public static final List<String> ATK = new ArrayList<>();
    public static final List<String> DEF = new ArrayList<>();
    public static final List<String> SPATK = new ArrayList<>();
    public static final List<String> SPDEF = new ArrayList<>();
    public static final List<String> SPD = new ArrayList<>();
    public static final List<List<String>> EV_LISTS = new ArrayList<>();

    //Initialize the Stat List
    public static void init()
    {
        EV_LISTS.add(HP); EV_LISTS.add(ATK); EV_LISTS.add(DEF); EV_LISTS.add(SPATK); EV_LISTS.add(SPDEF); EV_LISTS.add(SPD);

        Mongo.PokemonInfo.find(Filters.exists("ev")).forEach(d -> {
            List<Integer> j = d.getList("ev", Integer.class);
            for(int i = 0; i < 6; i++) if(j.get(i) > 0) EV_LISTS.get(i).add(d.getString("name"));
        });
    }

    public CommandWildDuel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        //Possible options: p!wildduel (random pokemon), p!wildduel STAT (random pokemon with evs in STAT), p!wildduel PKMN (specifically battle PKMN)
        boolean random = this.msg.length == 1;
        boolean randomSTAT = this.msg.length == 2 && Stat.cast(this.msg[1]) != null;
        boolean specific = this.msg.length >= 2 && Global.POKEMON.contains(this.getPokemon());

        if(DuelHelper.isInDuel(this.player.getId()))
        {
            this.event.getMessage().getChannel().sendMessage(this.playerData.getMention() + ": You are already in a duel!").queue();
            this.embed = null;
        }
        else if(specific)
        {
            Duel d = WildDuel.create(this.player.getId(), this.event, this.getPokemon());
            this.event.getMessage().getChannel().sendMessage(this.playerData.getMention() + ": A wild Pokemon appeared, and it wants to challenge you!").queue();
            this.embed = null;

            d.sendTurnEmbed();
        }
        else if(randomSTAT)
        {
            String pokemon = EV_LISTS.get(Stat.cast(this.msg[1]).ordinal()).get(new Random().nextInt(EV_LISTS.get(Stat.cast(this.msg[1]).ordinal()).size()));
            Duel d = WildDuel.create(this.player.getId(), this.event, pokemon);
            this.event.getMessage().getChannel().sendMessage(this.playerData.getMention() + ": A wild Pokemon appeared, and it wants to challenge you!").queue();
            this.embed = null;

            d.sendTurnEmbed();
        }
        else if(random)
        {
            Duel d = WildDuel.create(this.player.getId(), this.event, "");
            this.event.getMessage().getChannel().sendMessage(this.playerData.getMention() + ": A wild Pokemon appeared, and it wants to challenge you!").queue();
            this.embed = null;

            d.sendTurnEmbed();
        }
        else
        {
            this.embed.setDescription(CommandInvalid.getShort());
        }

        return this;
    }

    private String getPokemon()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < this.msg.length; i++) sb.append(this.msg[i]).append(" ");

        return Global.normalCase(sb.toString().trim());
    }
}
