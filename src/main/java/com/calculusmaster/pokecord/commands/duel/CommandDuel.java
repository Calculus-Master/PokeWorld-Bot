package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.commands.pokemon.CommandTeam;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.player.level.PlayerLevel;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.game.tournament.TournamentHelper;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CommandDuel extends Command
{
    private static final Map<String, ScheduledFuture<?>> REQUEST_COOLDOWNS = new HashMap<>();

    public CommandDuel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        boolean info = this.msg.length == 2 && Arrays.asList("info", "tutorial", "how").contains(this.msg[1]);

        if(this.msg.length == 1 || info)
        {
            this.embed.setTitle("Duel Info");
            this.embed.setDescription("This is a PvP Duel! Duel another player with a Pokemon team of any size, up to a maximum of " + CommandTeam.MAX_TEAM_SIZE + "!");
            this.embed
                    .addField("Limits", "Duel Teams are limited by default. This means, that depending on the size of the Duel, there is a certain maximum number of Legendary and Mythical/Ultra Beast Pokemon you can have on your team. You will be notified about this when you request a duel (if your team violates the limits), and it can be disabled by including the `--nolimit` argument when requesting a duel.", false)
                    .addField("1v1", "To initiate a simple 1v1 duel, simply use `p!duel <@player>`. This will start a duel using you and your opponent's selected Pokemon, and will not involve either of your Pokemon teams.", false)
                    .addField("Battling", "While in a duel, you are able to submit a Turn Action using the `p!use` command. A list of the possible Turn Actions can be found by using `p!help use`", false)
                    .addField("Targets", "In every Server, a player is randomly chosen as the Target. `p!target` will show who this player is. To see more information about the Target system, use `p!target info`.", false)
                    .addField("Other Duels", "There are other kinds of duels available (other than PvP). `p!wildduel` initiates a duel against a Wild Pokemon, `p!trainerduel` initiates a duel against an AI Trainer, and `p!gymduel` initiates a duel against a Gym Leader", false);
            this.embed.setFooter("Enjoy dueling!");
            return this;
        }
        else if(!this.serverData.getDuelChannels().isEmpty() && !this.serverData.getDuelChannels().contains(this.event.getChannel().getId()))
        {
            this.response = "Duels are not allowed in this channel!";
            return this;
        }
        else if(this.msg.length >= 3 && (!isNumeric(2) || this.getInt(2) > CommandTeam.MAX_TEAM_SIZE || this.getInt(2) > this.playerData.getTeam().size()))
        {
            this.response = "Error with size. Either it isn't a number, larger than the max of " + CommandTeam.MAX_TEAM_SIZE + ", or larger than your team's size!";
            return this;
        }

        boolean checkTeam = !Arrays.asList(this.msg).contains("--nolimit");

        boolean accept = this.msg[1].equals("accept");
        boolean deny = this.msg[1].equals("deny") || this.msg[1].equals("cancel");

        if(accept || deny)
        {
            if(!DuelHelper.isInDuel(this.player.getId())) this.response = "You are not in a duel!";
            else if(DuelHelper.instance(this.player.getId()).getStatus().equals(DuelHelper.DuelStatus.DUELING)) this.response = "You are already in a duel!";
            else if(accept)
            {
                Duel d = DuelHelper.instance(this.player.getId());

                if(d.getSize() != 1 && d.getSize() > this.playerData.getTeam().size())
                {
                    this.response = "Your team needs to contain at least " + d.getSize() + " Pokemon to participate! Deleting duel request!";

                    this.removeRequestExpiry();

                    DuelHelper.delete(this.player.getId());
                }
                else if(checkTeam && this.isInvalidTeam(d.getSize())) this.createInvalidTeamEmbed(d.getSize());
                else
                {
                    this.removeRequestExpiry();

                    d.sendTurnEmbed();
                    this.embed = null;
                }
            }
            else
            {
                this.removeRequestExpiry();

                DuelHelper.delete(this.player.getId());
                this.response = this.player.getName() + " denied the duel request!";
            }

            return this;
        }

        int size = 1;

        if(this.msg.length >= 3) size = this.getInt(2);

        if(TournamentHelper.isInTournament(this.player.getId())) size = TournamentHelper.instance(this.player.getId()).getSize();

        //Player wants to start a duel with the mention, check all necessary things

        if(DuelHelper.isInDuel(this.player.getId()))
        {
            this.response = CommandInvalid.ALREADY_IN_DUEL;
            return this;
        }

        if(this.mentions.size() == 0)
        {
            this.embed.setDescription("You need to mention someone to duel them!");
            return this;
        }

        String opponentID = this.mentions.get(0).getId();
        PlayerDataQuery other = new PlayerDataQuery(opponentID);
        Member opponent = this.getMember(opponentID);

        if(this.playerData.getTeam().size() < size) this.response = "Your team needs to have at least " + size + " Pokemon!";
        else if(!PlayerDataQuery.isRegistered(opponentID)) this.response = opponent.getEffectiveName() + " is not registered! Use p!start <starter> to begin!";
        else if(size != 1 && new PlayerDataQuery(opponentID).getTeam().isEmpty()) this.response = opponent.getEffectiveName() + " needs to create a Pokemon team!";
        else if(DuelHelper.isInDuel(opponentID)) this.response = opponent.getEffectiveName() + " is already in a Duel!";
        else if(this.player.getId().equals(opponentID)) this.response = "You cannot duel yourself!";
        else if(size >= 6 && (this.playerData.getLevel() < PlayerLevel.REQUIRED_LEVEL_UNLIMITED_DUEL_SIZE || other.getLevel() < PlayerLevel.REQUIRED_LEVEL_UNLIMITED_DUEL_SIZE))
            this.response = "Both you and " + opponent.getEffectiveName() + " must be Pokemon Mastery Level " + PlayerLevel.REQUIRED_LEVEL_UNLIMITED_DUEL_SIZE + " to create duels with teams of more than 6 Pokemon";
        else if(checkTeam && this.isInvalidTeam(size)) this.createInvalidTeamEmbed(size);
        else
        {
            Duel.create(this.player.getId(), opponentID, size, this.event);

            ScheduledFuture<?> request = Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                DuelHelper.delete(this.player.getId());
                this.response = "Duel Request expired!";
                REQUEST_COOLDOWNS.remove(opponentID);
            }, 3, TimeUnit.MINUTES);
            REQUEST_COOLDOWNS.put(opponentID, request);

            this.event.getChannel().sendMessage("<@" + opponentID + "> ! " + this.player.getName() + " has challenged you to a duel! Type `p!duel accept` to accept!").queue();
            this.embed = null;
        }

        return this;
    }

    private void removeRequestExpiry()
    {
        if(REQUEST_COOLDOWNS.containsKey(this.player.getId()))
        {
            REQUEST_COOLDOWNS.get(this.player.getId()).cancel(true);
            REQUEST_COOLDOWNS.remove(this.player.getId());
        }
    }

    private void createInvalidTeamEmbed(int size)
    {
        int legendaryCap = this.getLegendaryCap(size);
        int mythUBCap = this.getMythicalUBCap(size);

        this.embed = new EmbedBuilder();
        this.embed.setDescription("Your Team is Invalid! Check the limits below.");
        this.embed.setTitle("Invalid Team!");
        this.embed
                .addField("Legendary Limit", "Maximum: " + legendaryCap, false)
                .addField("Mythical and Ultra Beast Limit", "Maximum: " + mythUBCap, false);
        this.embed.setFooter("Check your team with `p!team` to see how many Pokemon of each kind listed above you have!");
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
