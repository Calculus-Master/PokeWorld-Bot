package com.calculusmaster.pokecord.game.duel.tournament;

import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

import static com.calculusmaster.pokecord.game.duel.tournament.TournamentHelper.*;

public class Tournament
{
    private TournamentStatus status;
    private MessageReceivedEvent event;
    private int teamSize;

    private String creator;
    private List<String> allPlayers;
    private Map<String, PlayerDataQuery> playerQueries;

    private List<String> playerPool;
    private List<String> eliminatedPlayers;
    private Map<Matchup, Boolean> matchups;
    private Map<String, Boolean> playerAccepts;

    private int round;

    public static Tournament create(String creator, List<String> IDs, MessageReceivedEvent event, int teamSize)
    {
        Tournament t = new Tournament();

        t.setStatus(TournamentStatus.WAITING_FOR_PLAYERS);
        t.setPlayers(creator, IDs);
        t.setEvent(event);
        t.setSize(teamSize);

        TOURNAMENTS.add(t);
        return t;
    }

    public void start()
    {
        this.setStatus(TournamentStatus.DUELING);

        this.playerPool = new ArrayList<>(List.copyOf(this.allPlayers));
        this.matchups = new HashMap<>();
        this.eliminatedPlayers = new ArrayList<>();

        this.round = 0;

        for(String s : this.allPlayers) if(!s.equals(this.creator)) this.sendStart(s);

        this.startNextRound();
    }

    private void startNextRound()
    {
        List<String> pool = new ArrayList<>(List.copyOf(this.playerPool));

        int matchupCount = pool.size() / 2;
        this.matchups.clear();

        Collections.shuffle(pool);

        List<String> players1 = new ArrayList<>(List.copyOf(pool.subList(0, matchupCount)));
        List<String> players2 = new ArrayList<>(List.copyOf(pool.subList(matchupCount, pool.size())));

        Collections.shuffle(players1);
        Collections.shuffle(players2);

        for(int i = 0; i < players1.size(); i++)
        {
            this.matchups.put(new Matchup(players1.get(i), players2.get(i)), false);
        }

        this.round++;

        this.sendStatusEmbed();
    }

    public void addDuelResults(String winner, String loser)
    {
        Matchup matchup = null;
        for(Matchup m : this.matchups.keySet()) if(m.has(winner)) matchup = m;

        this.matchups.put(matchup, true);

        this.eliminatedPlayers.add(loser);
        this.playerPool.remove(loser);

        if(this.isRoundComplete())
        {
            if(this.isTournamentComplete()) this.sendCompleteEmbed();
            else this.startNextRound();
        }
    }

    public void sendCompleteEmbed()
    {
        this.setStatus(TournamentStatus.COMPLETE);

        EmbedBuilder embed = new EmbedBuilder();

        int credits = 1000 * this.allPlayers.size();
        int xp = (int)(2000 * Math.pow(this.allPlayers.size(), 2) / 2);

        Achievements.grant(this.playerPool.get(0), Achievements.WON_FIRST_TOURNAMENT, this.event);
        for(String s : this.allPlayers) Achievements.grant(s, Achievements.PARTICIPATED_FIRST_TOURNAMENT, this.event);

        PlayerDataQuery winner = this.playerQueries.get(this.playerPool.get(0));

        winner.changeCredits(credits);

        embed.setDescription("The Tournament winner is " + this.playerQueries.get(this.playerPool.get(0)).getUsername() + "! They earned " + credits + " credits!");
        embed.setTitle("Tournament Complete");

        this.event.getChannel().sendMessageEmbeds(embed.build()).queue();

        TournamentHelper.delete(this.creator);
    }

    public void sendStatusEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Tournament Status");

        switch(this.status)
        {
            case WAITING_FOR_PLAYERS -> {
                embed.setDescription("Tournament is currently waiting for players to accept their invitation!");

                StringBuilder accepted = new StringBuilder();
                StringBuilder waiting = new StringBuilder();

                for(String s : this.playerAccepts.keySet())
                {
                    String name = this.playerQueries.get(s).getUsername();
                    if(this.playerAccepts.get(s)) accepted.append(name).append(" :white_check_mark:\n");
                    else waiting.append(name).append("\n");
                }

                embed.addField("Waiting for", waiting.toString(), false);
                embed.addField("Ready", accepted.toString(), false);
            }
            case WAITING_FOR_START -> embed.setDescription("Tournament is waiting to be started! It will begin once " + this.playerQueries.get(this.creator).getUsername() + " sends the start command!");
            case DUELING -> {
                embed.setDescription("Tournament duels are live!");

                StringBuilder matches = new StringBuilder();
                for(Matchup m : this.matchups.keySet()) matches.append(this.getMatchupOverview(m)).append("\n");

                embed.addField("Matchup Overview", matches.toString(), false);

                int playerCount = this.matchups.keySet().size() * 2;
                embed.setFooter(switch(playerCount) {
                    case 2 -> "Finals";
                    case 4 -> "Semifinals";
                    case 8 -> "Quarterfinals";
                    default -> "Round of " + playerCount;
                });
            }
        }

        this.event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    public void sendInvite(String ID)
    {
        this.playerQueries.get(ID).directMessage("You have been invited to a Tournament in " + this.event.getGuild().getName() + " <#" + this.event.getChannel().getName() + ">! Required Team Size is " + this.teamSize + ". To join, type `p!tournament accept` in the Server where the Tournament is being held.");
    }

    public void sendStart(String ID)
    {
        this.playerQueries.get(ID).directMessage("Tournament starting in " + this.event.getGuild().getName() + "! Check matchups with `p!tournament status`!");
    }

    private String getMatchupOverview(Matchup m)
    {
        boolean complete = this.matchups.get(m);

        String header = this.playerQueries.get(m.player1).getUsername() + " vs " + this.playerQueries.get(m.player2).getUsername();

        return "`" + header + "` - " + (complete ? ":white_check_mark:" : ":x:");
    }

    private boolean isRoundComplete()
    {
        return this.matchups.values().stream().allMatch(v -> v);
    }

    private boolean isTournamentComplete()
    {
        return this.isRoundComplete() && this.playerPool.size() == 1;
    }

    public boolean isReady()
    {
        return this.playerAccepts.values().stream().allMatch(v -> v);
    }

    public void setPlayers(String creator, List<String> players)
    {
        this.allPlayers = new ArrayList<>();
        this.allPlayers.add(creator);
        this.allPlayers.addAll(players);

        this.playerQueries = new HashMap<>();
        for(String s : this.allPlayers) this.playerQueries.put(s, PlayerDataQuery.of(s));

        this.creator = creator;

        this.playerAccepts = new HashMap<>();
        for(String s : this.allPlayers) this.playerAccepts.put(s, false);

        this.playerAccepts.put(this.creator, true);
    }

    public void setPlayerAccepted(String ID, boolean value)
    {
        this.playerAccepts.put(ID, value);
    }

    public void notifyCreator(String msg)
    {
        this.playerQueries.get(this.creator).directMessage(msg);
    }

    public void setSize(int size)
    {
        this.teamSize = size;
    }

    public int getSize()
    {
        return this.teamSize;
    }

    public Set<Matchup> getMatchups()
    {
        return this.matchups.keySet();
    }

    public boolean hasPlayer(String ID)
    {
        return this.allPlayers.contains(ID);
    }

    public boolean isPlayerEliminated(String ID)
    {
        return this.eliminatedPlayers.contains(ID);
    }

    public boolean hasPlayerAccepted(String ID)
    {
        return this.playerAccepts.get(ID);
    }

    public List<String> getPlayers()
    {
        return this.allPlayers;
    }

    public String getCreator()
    {
        return this.creator;
    }

    private void setEvent(MessageReceivedEvent event)
    {
        this.event = event;
    }

    public void setStatus(TournamentStatus status)
    {
        this.status = status;
    }

    public TournamentStatus getStatus()
    {
        return this.status;
    }
}
