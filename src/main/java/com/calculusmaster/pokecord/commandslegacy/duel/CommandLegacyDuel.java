package com.calculusmaster.pokecord.commandslegacy.duel;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.commandslegacy.CommandLegacyInvalid;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.extension.CasualMatchmadeDuel;
import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestrictionRegistry;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
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

import static com.calculusmaster.pokecord.game.player.PlayerTeam.MAX_TEAM_SIZE;

public class CommandLegacyDuel extends CommandLegacy
{
    private static final Map<String, ScheduledFuture<?>> REQUEST_COOLDOWNS = new HashMap<>();

    public CommandLegacyDuel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, true);
    }

    @Override
    public CommandLegacy runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.PVP_DUELS)) return this.invalidMasteryLevel(Feature.PVP_DUELS);

        boolean info = this.msg.length == 2 && Arrays.asList("info", "tutorial", "how").contains(this.msg[1]);

        if(this.msg.length == 1 || info)
        {
            this.embed.setTitle("Duel Info");
            this.embed.setDescription("This is a PvP Duel! Duel another player with a Pokemon team of any size, up to a maximum of " + MAX_TEAM_SIZE + "!");
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
        else if(this.msg[1].equals("queue"))
        {
            if(this.msg.length == 3 && this.msg[2].equals("leave"))
            {
                if(!CasualMatchmadeDuel.isQueueing(this.player.getId())) this.response = "You are not currently queuing!";
                else
                {
                    CasualMatchmadeDuel.dequeuePlayer(this.player.getId());

                    this.response = "You have left the queue!";
                }
            }
            else if(this.msg.length == 3 && this.isNumeric(2) && List.of(1, 3, 6).contains(this.getInt(2)))
            {
                int size = this.getInt(2);

                if(CasualMatchmadeDuel.isQueueing(this.player.getId())) this.response = "You are already queuing!";
                else if(this.playerData.getTeam().isEmpty()) this.response = "You must have a team to queue!";
                else if(size != 1 && this.playerData.getTeam().size() > size) this.response = "Your team must be have at most " + this.getInt(2) + " Pokemon!";
                else if(size != 1 && !TeamRestrictionRegistry.STANDARD.validate(this.playerData.getTeam().getActiveTeamPokemon())) this.response = "Your team does not meet the Standard Duel Team restrictions!";
                else
                {
                    if(size != 1 && this.playerData.getTeam().size() < size) this.playerData.directMessage("Warning! Your team is smaller than the size of the duel you are queuing for!");

                    CasualMatchmadeDuel.QueueType type = switch(this.getInt(2)) {
                        case 1 -> CasualMatchmadeDuel.QueueType.ONES;
                        case 3 -> CasualMatchmadeDuel.QueueType.THREES;
                        case 6 -> CasualMatchmadeDuel.QueueType.SIXES;
                        default -> throw new IllegalStateException("Invalid QueueType for Casual Matchmade Duel: " + this.getInt(2));
                    };

                    CasualMatchmadeDuel.queuePlayer(this.player.getId(), this.event.getGuildChannel().asTextChannel(), type);

                    this.response = "You've entered the queue for a " + size + "v" + size + " duel!";
                }
            }
        }
        else if(this.msg.length >= 3 && (!isNumeric(2) || this.getInt(2) > MAX_TEAM_SIZE || this.getInt(2) > this.playerData.getTeam().size()))
        {
            this.response = "Error with size. Either it isn't a number, larger than the max of " + MAX_TEAM_SIZE + ", or larger than your team's size!";
            return this;
        }

        boolean checkTeam = !Arrays.asList(this.msg).contains("--nolimit");

        boolean accept = this.msg[1].equals("accept");
        boolean deny = this.msg[1].equals("deny") || this.msg[1].equals("cancel");

        if(accept || deny)
        {
            if(!DuelHelper.isInDuel(this.player.getId())) this.response = "There are no pending requests to duel you!";
            else if(DuelHelper.instance(this.player.getId()).getStatus().equals(DuelHelper.DuelStatus.DUELING)) this.response = "You are already in a duel!";
            else if(accept)
            {
                Duel d = DuelHelper.instance(this.player.getId());

                if(checkTeam && this.isInvalidTeam(d.getSize())) this.createInvalidTeamEmbed(d.getSize());
                else
                {
                    if(d.getSize() > this.playerData.getTeam().size()) this.event.getChannel().sendMessage("Warning! Your team has less Pokemon than your opponent.").queue();

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

        //Player wants to start a duel with the mention, check all necessary things

        if(DuelHelper.isInDuel(this.player.getId()))
        {
            this.response = CommandLegacyInvalid.ALREADY_IN_DUEL;
            return this;
        }

        if(this.mentions.size() == 0)
        {
            this.embed.setDescription("You need to mention someone to duel them!");
            return this;
        }

        String opponentID = this.mentions.get(0).getId();
        Member opponent = this.getMember(opponentID);

        if(this.playerData.getTeam().size() < size) this.response = "Your team needs to have at least " + size + " Pokemon!";
        else if(!PlayerDataQuery.isRegistered(opponentID)) this.response = opponent.getEffectiveName() + " is not registered! They must use p!start <starter> to begin.";
        else if(size != 1 && PlayerDataQuery.ofNonNull(opponentID).getTeam().isEmpty()) this.response = opponent.getEffectiveName() + " needs to create a Pokemon team!";
        else if(DuelHelper.isInDuel(opponentID)) this.response = opponent.getEffectiveName() + " is already in a Duel!";
        else if(this.player.getId().equals(opponentID)) this.response = "You cannot duel yourself!";
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

        PokemonEntity entity;
        List<String> team = this.playerData.getTeam().getActiveTeam();

        for(int i = 0; i < this.playerData.getTeam().size(); i++)
        {
            entity = PokemonEntity.cast(Mongo.PokemonData.find(Filters.eq("UUID", team.get(i))).projection(Projections.include("entity")).first().getString("entity"));

            if(PokemonRarity.isLegendary(entity)) legendary++;
            if(PokemonRarity.isMythical(entity)) mythical++;
            if(PokemonRarity.isUltraBeast(entity)) ub++;
        }

        return legendary > this.getLegendaryCap(size) || (mythical + ub) > this.getMythicalUBCap(size);
    }
}
