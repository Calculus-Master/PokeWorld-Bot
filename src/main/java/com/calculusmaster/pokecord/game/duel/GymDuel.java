package com.calculusmaster.pokecord.game.duel;

import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.game.duel.elements.GymLeader;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.calculusmaster.pokecord.game.duel.DuelHelper.DUELS;

public class GymDuel extends TrainerDuel
{
    private GymLeader.LeaderInfo info;

    public static Duel create(String playerID, MessageReceivedEvent event, GymLeader.LeaderInfo leader)
    {
        GymDuel duel = new GymDuel();

        duel.setStatus(DuelHelper.DuelStatus.WAITING);
        duel.setEvent(event);
        duel.setPlayers(playerID, leader.name, leader.slots.size());
        duel.setGymLeader(leader);
        duel.limitPlayerPokemon(leader.pokemonLevel);
        duel.setDefaults();
        duel.setDuelPokemonObjects(0);
        duel.setDuelPokemonObjects(1);

        DUELS.add(duel);
        return duel;
    }

    @Override
    public void sendWinEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();

        GymLeader leader = ((GymLeader)this.players[1]);

        //Player won
        if(this.getWinner().ID.equals(this.players[0].ID))
        {
            int c = 1000 * leader.info.gymLevel;
            this.players[0].data.changeCredits(c);

            this.uploadEVs(0);
            this.uploadExperience();

            embed.setDescription("You defeated " + leader.info.name + "! You earned " + c + " credits!");

            Achievements.grant(this.players[0].ID, Achievements.DEFEATED_FIRST_GYM_LEADER, this.event);

            Mongo.GymData.updateOne(Filters.eq("name", this.info.name), Updates.push("defeated", this.players[0].ID));
        }
        //Player lost
        else
        {
            this.uploadEVs(0);

            embed.setDescription("You were defeated by " + leader.info.name + "!");
        }

        this.event.getChannel().sendMessage(embed.build()).queue();
        DuelHelper.delete(this.players[0].ID);
    }

    private void setGymLeader(GymLeader.LeaderInfo info)
    {
        this.info = info;
        this.players[1] = GymLeader.create(info);
    }
}
