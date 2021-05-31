package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.duel.TrainerDuel;
import com.calculusmaster.pokecord.game.duel.elements.Trainer;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.PokemonRarity;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class CommandTrainerDuel extends Command
{
    public CommandTrainerDuel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 2 && this.isNumeric(1) && this.getInt(1) >= 1 && this.getInt(1) <= Trainer.DAILY_TRAINERS.size())
        {
            if(DuelHelper.isInDuel(this.player.getId()))
            {
                this.event.getMessage().getChannel().sendMessage(this.playerData.getMention() + ": You are already in a duel!").queue();
                this.embed = null;
                return this;
            }

            Trainer.TrainerInfo trainer = Trainer.DAILY_TRAINERS.get(this.getInt(1) - 1);

            if(this.isInvalidTeam(trainer.pokemon.size()))
            {
                this.event.getMessage().getChannel().sendMessage(this.playerData.getMention() + ": Your team is invalid! You can have a maximum of " + this.getLegendaryCap(trainer.pokemon.size()) + " legendaries and " + this.getMythicalUBCap(trainer.pokemon.size()) + " mythicals/ultra beasts!").queue();
                this.embed = null;
                return this;
            }

            Duel d = TrainerDuel.create(this.player.getId(), this.event, trainer);

            this.event.getMessage().getChannel().sendMessage(this.playerData.getMention() + ": You challenged " + trainer.name + " !").queue();
            this.embed = null;

            d.sendTurnEmbed();
        }
        else
        {
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < Trainer.DAILY_TRAINERS.size(); i++) sb.append(i + 1).append(": ").append(Trainer.DAILY_TRAINERS.get(i).name).append(Trainer.PLAYER_TRAINERS_DEFEATED.get(Trainer.DAILY_TRAINERS.get(i).name).contains(this.player.getId()) ? " (Defeated)" : "").append("\n");

            this.embed.setDescription(sb.toString());
            this.embed.setTitle("Daily Trainers");
        }
        return this;
    }

    private int getLegendaryCap(int size)
    {
        return 1 + (size - 1) / 4;
    }

    private int getMythicalUBCap(int size)
    {
        return 2 + (size - 1) / 4;
    }

    public boolean isInvalidTeam(int size)
    {
        if(size < 3) return false;

        int legendary = 0;
        int mythical = 0;
        int ub = 0;

        String name;
        List<String> team = this.playerData.getTeam();

        for(int i = 0; i < this.playerData.getTeam().size(); i++)
        {
            name = Mongo.PokemonData.find(Filters.eq("UUID", team.get(i))).first().getString("name");

            if(PokemonRarity.LEGENDARY.contains(name)) legendary++;
            if(PokemonRarity.MYTHICAL.contains(name)) mythical++;
            if(PokemonRarity.ULTRA_BEAST.contains(name)) ub++;
        }

        return legendary > this.getLegendaryCap(size) || (mythical + ub) > this.getMythicalUBCap(size);
    }
}
