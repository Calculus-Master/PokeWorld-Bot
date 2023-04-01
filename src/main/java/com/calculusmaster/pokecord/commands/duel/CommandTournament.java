package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.commands.pokemon.CommandTeam;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.tournament.Tournament;
import com.calculusmaster.pokecord.game.duel.tournament.TournamentHelper;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandTournament extends Command
{
    public CommandTournament(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.PVP_DUELS_TOURNAMENT)) return this.invalidMasteryLevel(Feature.PVP_DUELS_TOURNAMENT);

        boolean info = this.msg.length == 2 && this.msg[1].equals("info");
        boolean start = this.msg.length >= 2 && this.msg[1].equals("start");
        boolean create = this.msg.length >= 2 && this.msg[1].equals("create");
        boolean accept = this.msg.length == 2 && this.msg[1].equals("accept");
        boolean cancel = this.msg.length == 2 && this.msg[1].equals("cancel");
        boolean deny = this.msg.length == 2 && this.msg[1].equals("deny");
        boolean status = this.msg.length == 2 && this.msg[1].equals("status");
        boolean notify = this.msg.length == 2 && this.msg[1].equals("notify");

        if(info || this.msg.length == 1)
        {
            this.embed.setTitle("Tournament Info");
            this.embed.setDescription("Tournaments are large competitions between many players.");
            this.embed.addField("Creation", "To create a Tournament, type `p!tournament create --size <num>`. <num> is the team size you want players to use during Tournament duels, and in the same message you have to mention all the players you want to invite. Tournaments must have some multiple of 4 players (4, 8, 16, etc).", false)
                    .addField("Before Starting", "Tournaments cannot be started unless all invited players accept their invitations with `p!tournament accept`. To see which players still need to accept their invites, type `p!tournament status.` To notify all the players that they have not accepted their invites, type `p!tournament notify`.", false)
                    .addField("Starting", "Once all invited players have accepted their invites, the Tournament can be started with `p!tournament start`.", false)
                    .addField("Dueling", "Matchps for the current round can be found at anytime by using `p!tournament status`. To start a Tournament duel, simply send a duel request to the other player. The size will be automatically set to the Tournament's size setting. Each round, if all the duels are complete, the Tournament will automatically progress to the next round, or if the finals are complete, reward players correctly.", false);
        }
        else if(status)
        {
            if(!TournamentHelper.isInTournament(this.player.getId())) this.response = "You are not in a Tournament!";
            else
            {
                this.embed = null;
                TournamentHelper.instance(this.player.getId()).sendStatusEmbed();
            }
        }
        else if(notify)
        {
            if(!TournamentHelper.isInTournament(this.player.getId())) this.response = "You are not in a Tournament!";
            else
            {
                Tournament t = TournamentHelper.instance(this.player.getId());

                if(!t.getCreator().equals(this.player.getId())) this.response = "You are not the creator of this Tournament!";
                else
                {
                    List<String> players = t.getPlayers().stream().filter(p -> !t.hasPlayerAccepted(p)).collect(Collectors.toList());
                    for(String s : players) t.sendInvite(s);

                    this.response = "Reminded players!";
                }
            }
        }
        else if(accept || cancel || deny)
        {
            if(!TournamentHelper.isInTournament(this.player.getId())) this.response = "You are not in a Tournament!";
            else
            {
                Tournament t = TournamentHelper.instance(this.player.getId());

                if(accept)
                {
                    t.setPlayerAccepted(this.player.getId(), true);

                    t.notifyCreator(this.player.getName() + " joined your Tournament!");

                    if(t.isReady())
                    {
                        t.setStatus(TournamentHelper.TournamentStatus.WAITING_FOR_START);
                        t.notifyCreator("Tournament ready to start! Use `p!tournament start` to begin!");
                    }

                    this.response = "You entered this Tournament! It will begin once all players have accepted their invites and the creator starts the Tournament.";
                }
                else if(deny)
                {
                    t.setPlayerAccepted(this.player.getId(), false);

                    t.notifyCreator(this.player.getName() + " left your Tournament!");

                    this.response = "You left this Tournament! You can still join back when ready with `p!tournament accept`!";
                }
                else if(cancel)
                {
                    if(this.player.getId().equals(t.getCreator()))
                    {
                        TournamentHelper.delete(this.player.getId());

                        this.response = "Your Tournament was deleted!";
                    }
                    else this.response = "You are not the creator of this Tournament!";
                }
            }
        }
        else if(create)
        {
            List<String> msg = Arrays.asList(this.msg);

            this.mentions = this.mentions.stream().distinct().collect(Collectors.toList());

            if(this.mentions.size() < 3 || this.mentions.size() % 4 != 3) this.response = "You must mention at least 3 members, and the number of total players (including you) must be a multiple of 4!";
            else if(this.mentions.stream().anyMatch(m -> !PlayerDataQuery.isRegistered(m.getId()))) this.response = "At least one of the mentioned players is not registered!";
            else if(this.mentions.stream().anyMatch(m -> m.getId().equals(this.player.getId()))) this.response = "You do not need to mention yourself!";
            else if(this.mentions.stream().anyMatch(m -> DuelHelper.isInDuel(m.getId()))) this.response = "At least one mentioned player is currently in a duel!";
            else
            {
                List<String> players = this.mentions.stream().map(ISnowflake::getId).collect(Collectors.toList());

                int teamSize = 3;

                if(msg.contains("--size") && msg.indexOf("--size") + 1 < msg.size())
                {
                    int index = msg.indexOf("--size") + 1;
                    if(this.isNumeric(index) && this.getInt(index) < CommandTeam.MAX_TEAM_SIZE) teamSize = this.getInt(msg.indexOf("--size") + 1);
                }

                Tournament t = Tournament.create(this.player.getId(), players, this.event, teamSize);

                this.response = "Tournament started! Invitations will be sent to the respective players.";

                for(String s : t.getPlayers()) if(!s.equals(t.getCreator())) t.sendInvite(s);
            }
        }
        else if(start)
        {
            if(!TournamentHelper.isInTournament(this.player.getId())) this.response = "You are not in a Tournament!";
            else
            {
                Tournament t = TournamentHelper.instance(this.player.getId());

                if(!t.getCreator().equals(this.player.getId())) this.response = "You are not the manager of this tournament!";
                else switch(t.getStatus())
                {
                    case DUELING -> this.response = "This Tournament is already in progress!";
                    case WAITING_FOR_PLAYERS -> this.response = "Not all players have accepted the Tournament Request!";
                    case WAITING_FOR_START -> {
                        t.start();
                        this.response = "The Tournament has started!";
                    }
                }
            }
        }
        else this.response = CommandInvalid.getShort();

        return this;
    }
}
