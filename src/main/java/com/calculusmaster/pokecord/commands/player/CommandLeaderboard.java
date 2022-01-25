package com.calculusmaster.pokecord.commands.player;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.calculusmaster.pokecord.util.interfaces.IScoreComponent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.*;
import java.util.stream.Collectors;

public class CommandLeaderboard extends Command
{
    private static final List<PlayerDataQuery> PLAYER_QUERIES = new ArrayList<>();
    private static final Map<String, Double> FINAL_SCORES = new HashMap<>();
    private static final List<Map.Entry<String, Double>> SORTED_FINAL_SCORES = new LinkedList<>();

    public CommandLeaderboard(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.ACCESS_LEADERBOARD)) return this.invalidMasteryLevel(Feature.ACCESS_LEADERBOARD);

        boolean server = this.msg.length == 2 && this.msg[1].equals("server");
        boolean specific = this.msg.length == 2;

        this.event.getChannel().sendMessage("Calculating Player Scores...").queue();

        this.reset();
        this.generatePlayerQueries(server);
        this.generateZScores();
        this.generateFinalScores();
        this.sortFinalScores();

        this.embed = new EmbedBuilder();

        final String standardizedNote = "*Note: Score values are Standardized. Negative values indicate a score below average, while Positive values indicate above average. The farther a value is from 0, the more extreme that value is compared to the player population!*";

        if(specific && !server)
        {
            if(this.mentions.size() > 0 || this.msg[1].equals("self") || this.msg[1].equals("me"))
            {
                String targetID = this.mentions.size() > 0 ? this.mentions.get(0).getId() : this.player.getId();

                if(PLAYER_QUERIES.stream().noneMatch(p -> p.getID().equals(targetID)))
                {
                    this.response = this.mentions.get(0).getEffectiveName() + " is not registered!";
                    return this;
                }

                for(ScoreComponent sc : ScoreComponent.values()) this.embed.addField(sc.name, "Value: " + sc.raw.get(targetID) + "\nZ Score: `" + this.format(sc.zscore.get(targetID)) + "`\nWeight: `" + sc.weight + "`", true);
                this.embed.addField("Total Score", "`" + this.format(FINAL_SCORES.get(targetID)) + "`", false);
                this.embed.addField("Position", "*" + this.getPosition(targetID) + "*", true);

                this.embed.setDescription(standardizedNote);
                this.embed.setTitle("Score Calculation for " + PLAYER_QUERIES.stream().filter(p -> p.getID().equals(targetID)).findFirst().orElseThrow().getUsername());
                this.embed.setFooter("Higher weight values mean that the component has a larger impact on your overall score value!");
            }
            else this.response = "Invalid Arguments!";
        }
        else
        {
            if(!server)
            {
                String currentID = this.player.getId();

                if(SORTED_FINAL_SCORES.get(0).getKey().equals(currentID)) Achievements.grant(currentID, Achievements.REACHED_TOP_1_LEADERBOARD, this.event);

                for(int i = 0; i < Math.min(10, SORTED_FINAL_SCORES.size()); i++) if(SORTED_FINAL_SCORES.get(i).getKey().equals(currentID)) Achievements.grant(currentID, Achievements.REACHED_TOP_10_LEADERBOARD, this.event);
            }

            StringBuilder leaderboard = new StringBuilder();

            PlayerDataQuery p;
            for(int i = 0; i < 10; i++)
            {
                if(i < SORTED_FINAL_SCORES.size())
                {
                    final Map.Entry<String, Double> entry = SORTED_FINAL_SCORES.get(i);
                    p = PLAYER_QUERIES.stream().filter(q -> q.getID().equals(entry.getKey())).collect(Collectors.toList()).get(0);

                    leaderboard.append(i + 1).append(": ***").append(p.getUsername()).append("*** (Score: ").append(this.format(entry.getValue())).append(")\n");
                }
            }

            this.embed.setTitle((server ? this.server.getName() : "Pokecord2 Global") + " Leaderboard");
            this.embed.setDescription(leaderboard + "\n\n" + standardizedNote);
            this.embed.setFooter("Your Position: " + this.getPosition(this.player.getId()));
        }

        return this;
    }

    private String getPosition(String ID)
    {
        int index = -1;
        for(int i = 0; i < SORTED_FINAL_SCORES.size(); i++) if(SORTED_FINAL_SCORES.get(i).getKey().equals(ID)) index = i;

        return (index + 1) + " / " + SORTED_FINAL_SCORES.size();
    }

    private String format(double d)
    {
        return String.format("%.3f", d);
    }

    private void sortFinalScores()
    {
        SORTED_FINAL_SCORES.addAll(FINAL_SCORES.entrySet());
        SORTED_FINAL_SCORES.sort(Comparator.comparingDouble(Map.Entry::getValue));
        Collections.reverse(SORTED_FINAL_SCORES);
    }

    private void generateFinalScores()
    {
        double sum;
        for(PlayerDataQuery p : PLAYER_QUERIES)
        {
            sum = Arrays.stream(ScoreComponent.values()).mapToDouble(sc -> sc.zscore.get(p.getID())).sum();

            FINAL_SCORES.put(p.getID(), sum);
        }
    }

    private void generateZScores()
    {
        //Create Raw Count Maps
        PLAYER_QUERIES.forEach(p -> {
            for(ScoreComponent sc : ScoreComponent.values()) sc.raw.put(p.getID(), sc.value.get(p));
        });

        //Initialize DescriptiveStatistics Variables
        PLAYER_QUERIES.forEach(p -> {
            for(ScoreComponent sc : ScoreComponent.values()) sc.stats.addValue((double)sc.raw.get(p.getID()));
        });

        //Generate Initial Z Scores
        PLAYER_QUERIES.forEach(p -> {
            for(ScoreComponent sc : ScoreComponent.values()) sc.zscore.put(p.getID(), sc.calculateZScore(p.getID()));
        });

        //Weight Z Scores
        PLAYER_QUERIES.forEach(p -> {
            for(ScoreComponent sc : ScoreComponent.values()) sc.zscore.put(p.getID(), sc.zscore.get(p.getID()) * sc.weight);
        });
    }

    private void generatePlayerQueries(boolean server)
    {
        final List<String> IDs_Lambda = new ArrayList<>();
        Mongo.PlayerData.find().forEach(d -> IDs_Lambda.add(d.getString("playerID")));

        List<String> IDs = new ArrayList<>(IDs_Lambda);

        if(server)
        {
            List<String> serverMembers = DataHelper.SERVER_PLAYERS.get(this.server.getId());

            IDs = IDs.stream().filter(serverMembers::contains).collect(Collectors.toList());
        }

        IDs.stream().map(PlayerDataQuery::of).forEach(PLAYER_QUERIES::add);
    }

    private void reset()
    {
        PLAYER_QUERIES.clear();
        ScoreComponent.clearLists();
        FINAL_SCORES.clear();
        SORTED_FINAL_SCORES.clear();
    }

    enum ScoreComponent
    {
        POKEMON_LISTS(2.5, p -> p.getPokemonList().size(), "Pokemon"),
        ACHIEVEMENTS(2.5, p -> p.getAchievementsList().size(), "Achievements"),
        MASTERY_LEVEL(1.9, p -> p.getLevel(), "Pokemon Mastery Level"),
        POKEMON_PRESTIGED(2.5, p -> p.getPokemon().stream().filter(Pokemon::hasPrestiged).mapToInt(Pokemon::getPrestigeLevel).sum(), "Prestiged Pokemon"),
        CREDITS(1.25, p -> p.getCredits(), "Credits"),
        REDEEMS(1.85, p -> p.getRedeems(), "Redeems"),
        TMS(1.5, p -> p.getTMList().size(), "TMs"),
        TRS(1.5, p -> p.getTRList().size(), "TRs"),
        ITEMS(0.75, p -> p.getItemList().size(), "Items"),
        FORMS_OWNED(1.5, p -> p.getOwnedForms().size(), "Owned Forms"),
        MEGA_OWNED(1.5, p -> p.getOwnedMegas().size(), "Owned Mega Evolutions"),
        ZCRYSTALS(1.8, p -> p.getZCrystalList().size(), "Z Crystals"),
        DUELS_PVP_WON(1.75, p -> p.getStatistics().get(PlayerStatistic.PVP_DUELS_WON), "PvP Duels Won"),
        DUELS_WILD_WON(0.5, p -> p.getStatistics().get(PlayerStatistic.WILD_DUELS_WON), "Wild Duels Won"),
        DUELS_TRAINER_WON(0.75, p -> p.getStatistics().get(PlayerStatistic.TRAINER_DUELS_WON), "Trainer Duels Won"),
        DUELS_TRAINER_ELITE_WON(1.25, p -> p.getStatistics().get(PlayerStatistic.ELITE_DUELS_WON), "Elite Duels Won");

        double weight;
        IScoreComponent value;
        Map<String, Integer> raw;
        DescriptiveStatistics stats;
        Map<String, Double> zscore;
        String name;

        ScoreComponent(double weight, IScoreComponent value, String name)
        {
            this.weight = weight;
            this.value = value;
            this.raw = new HashMap<>();
            this.stats = new DescriptiveStatistics();
            this.zscore = new HashMap<>();
            this.name = name;
        }

        double calculateZScore(String ID)
        {
            try
            {
                double z = (this.raw.get(ID) - this.stats.getMean()) / this.stats.getStandardDeviation();
                return Double.isNaN(z) ? 0 : z;
            }
            catch (ArithmeticException e)
            {
                LoggerHelper.reportError(CommandLeaderboard.class, "Divide by zero when calculating Z-Score (StdDev is 0)", e);
                return 0;
            }
        }

        public static void clearLists()
        {
            for(ScoreComponent sc : values())
            {
                sc.raw.clear();
                sc.stats = new DescriptiveStatistics();
                sc.zscore.clear();
            }
        }
    }
}
