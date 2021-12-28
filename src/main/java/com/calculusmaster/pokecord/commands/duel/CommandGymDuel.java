package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.extension.GymDuel;
import com.calculusmaster.pokecord.game.duel.players.GymLeader;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandGymDuel extends Command
{
    public CommandGymDuel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.PVE_DUELS_GYM)) return this.invalidMasteryLevel(Feature.PVE_DUELS_GYM);

        int level = this.playerData.getGymLevel();

        if(level - 1 >= GymLeader.GYM_LEADERS.size())
        {
            this.embed.setDescription("There are no Gym Leaders at this level. It is still a WIP!");
            this.embed.setTitle("Level " + level + " Gym Leaders");
            return this;
        }
        else if(this.msg.length == 2 && GymLeader.GYM_LEADERS.get(level - 1).stream().anyMatch(l -> l.name.equals(Global.normalize(this.msg[1]))))
        {
            GymLeader.LeaderInfo info = GymLeader.GYM_LEADERS.get(level - 1).stream().filter(l -> l.name.equals(Global.normalize(this.msg[1]))).collect(Collectors.toList()).get(0);

            if(GymLeader.getPlayersDefeated(info.name).contains(this.player.getId()))
            {
                this.event.getChannel().sendMessage(this.playerData.getMention() + ": You have already defeated this gym leader!").queue();
                this.embed = null;
                return this;
            }
            else if(this.isInvalidTeam(info.slots.size()))
            {
                this.event.getMessage().getChannel().sendMessage(this.playerData.getMention() + ": Your team is invalid! You can have a maximum of " + this.getLegendaryCap(info.slots.size()) + " legendaries and " + this.getMythicalUBCap(info.slots.size()) + " mythicals/ultra beasts!").queue();
                this.embed = null;
                return this;
            }

            Duel d = GymDuel.create(this.player.getId(), this.event, info);

            this.event.getMessage().getChannel().sendMessage(this.playerData.getMention() + ": You challenged Leader " + info.name + " !").queue();
            this.embed = null;

            d.sendTurnEmbed();
        }
        else
        {
            if(level - 1 >= GymLeader.GYM_LEADERS.size())
            {
                this.embed.setDescription("There are no Gym Leaders at this level. It is still a WIP!");
                this.embed.setTitle("Level " + level + " Gym Leaders");
                return this;
            }

            List<String> leadersAtLevel = GymLeader.GYM_LEADERS.get(level - 1).stream().map(info -> info.name).collect(Collectors.toList());
            List<Boolean> progress = new ArrayList<>();

            for(String s : leadersAtLevel) progress.add(GymLeader.getPlayersDefeated(s).contains(this.player.getId()));

            if(progress.stream().allMatch(b -> b))
            {
                level++;
                this.playerData.increaseGymLevel();
                this.event.getChannel().sendMessage(this.playerData.getMention() + ": You defeated all Gym Leaders at Level " + (level - 1) + "! You can now progress to Level " + level + "!").queue();
            }

            StringBuilder leaderList = new StringBuilder();

            if(level - 1 >= GymLeader.GYM_LEADERS.size()) leaderList.append("There are no Gym Leaders at this level. It is still a WIP!");
            else for(int i = 0; i < leadersAtLevel.size(); i++) leaderList.append(i + 1).append(". ").append(progress.get(i) ? "~~" : "").append(leadersAtLevel.get(i)).append(progress.get(i) ? "~~" : "").append("\n");

            this.embed.setDescription(leaderList.toString());
            this.embed.setTitle("Level " + level + " Gym Leaders");
        }
        return this;
    }

    private int getLegendaryCap(int size)
    {
        return 1 + (size - 1) / 6;
    }

    private int getMythicalUBCap(int size)
    {
        return 2 + (size - 1) / 6;
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
