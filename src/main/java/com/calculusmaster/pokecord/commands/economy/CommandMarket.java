package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.commands.pokemon.CommandInfo;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.player.Settings;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.sort.MarketListSorter;
import com.calculusmaster.pokecord.game.pokemon.sort.MarketSorterFlag;
import com.calculusmaster.pokecord.game.pokemon.sort.PokemonListSorter;
import com.calculusmaster.pokecord.game.pokemon.sort.PokemonSorterFlag;
import com.calculusmaster.pokecord.game.trade.elements.MarketEntry;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandMarket extends Command
{
    public CommandMarket(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.ACCESS_MARKET)) return this.invalidMasteryLevel(Feature.ACCESS_MARKET);

        boolean list = this.msg.length >= 4 && (this.msg[1].equals("list") || this.msg[1].equals("sell")) && this.isNumeric(2) && this.isNumeric(3) && this.getInt(2) >= 1 && this.getInt(2) <= this.playerData.getPokemonList().size();
        boolean buy = this.msg.length == 3 && this.msg[1].equals("buy") && MarketEntry.isValidID(this.msg[2]);
        boolean collect = this.msg.length == 3 && this.msg[1].equals("collect") && MarketEntry.isValidID(this.msg[2]);
        boolean info = this.msg.length == 3 && this.msg[1].equals("info") && MarketEntry.isValidID(this.msg[2]);

        if(list)
        {
            MarketEntry newEntry = MarketEntry.create(this.player.getId(), this.player.getName(), this.playerData.getPokemonList().get(this.getInt(2) - 1), this.getInt(3));

            this.playerData.removePokemon(newEntry.pokemonID);

            this.response = "You successfully listed your Level " + newEntry.pokemon.getLevel() + " " + newEntry.pokemon.getName() + "` for " + newEntry.price + " credits!";
        }
        else if(buy || collect || info)
        {
            MarketEntry m = MarketEntry.build(this.msg[2]);

            if(buy && m.price <= this.playerData.getCredits())
            {
                this.playerData.changeCredits(-1 * m.price);
                this.playerData.addPokemon(m.pokemonID);

                if(!m.sellerID.equals("BOT"))
                {
                    PlayerDataQuery seller = PlayerDataQuery.of(m.sellerID);

                    seller.changeCredits(m.price);
                    seller.directMessage("Your `Level " + m.pokemon.getLevel() + " " + m.pokemon.getName() + "` was sold from your Market Listing to " + this.playerData.getUsername() + " for " + m.price + " credits!");

                    Achievements.grant(m.sellerID, Achievements.SOLD_FIRST_POKEMON_MARKET, this.event);
                    seller.getStatistics().incr(PlayerStatistic.POKEMON_SOLD_MARKET);
                }

                Achievements.grant(this.player.getId(), Achievements.BOUGHT_FIRST_POKEMON_MARKET, this.event);
                this.playerData.getStatistics().incr(PlayerStatistic.POKEMON_BOUGHT_MARKET);

                this.response = "You successfully bought `Level " + m.pokemon.getLevel() + " " + m.pokemon.getName() + "` for " + m.price + " credits!";
                MarketEntry.delete(m.marketID);
            }
            else if(collect && m.sellerID.equals(this.player.getId()))
            {
                this.playerData.addPokemon(m.pokemonID);

                this.response = "You successfully retrieved your `Level " + m.pokemon.getLevel() + " " + m.pokemon.getName() + "` from the market!";
                MarketEntry.delete(m.marketID);
            }
            else if(info)
            {
                MarketEntry entry = MarketEntry.build(this.msg[2]);
                Pokemon chosen = Pokemon.build(entry.pokemonID);

                String title = "**Level " + chosen.getLevel() + " " + chosen.getName() + (chosen.hasNickname() ? " (" + chosen.getNickname() + ")" : "") + "**" + (chosen.isShiny() ? " :star2:" : "");
                String market = "Market ID: " + entry.marketID + " | Price: " + entry.price + "\nSold by: " + entry.sellerName;
                String exp = chosen.getLevel() == 100 ? " Max Level " : chosen.getExp() + " / " + GrowthRate.getRequiredExp(chosen.getData().growthRate, chosen.getLevel()) + " XP";
                String gender = "Gender: " + Global.normalize(chosen.getGender().toString());
                String type = "Type: " + chosen.getType().stream().map(Type::getStyledName).collect(Collectors.joining(" | "));
                String nature = "Nature: " + Global.normalize(chosen.getNature().toString());
                String item = "Held Item: " + chosen.getItem().getStyledName();
                String stats = CommandInfo.getStatsFormatted(chosen, this.playerData.getSettings().get(Settings.CLIENT_DETAILED, Boolean.class));

                this.embed.setTitle(title);
                this.embed.setDescription(market + "\n" + exp + "\n" + gender + "\n" + type + "\n" + nature + "\n" + item + "\n\n" + stats);
                this.color = chosen.getType().get(0).getColor();
                this.embed.setImage(chosen.getImage());
                this.embed.setFooter("Buy this pokemon with `p!market buy " + entry.marketID + "`!");
            }
            else this.embed.setDescription(CommandInvalid.getShort());
        }
        //Standard Display of Market Entries
        else
        {
            List<MarketEntry> marketEntries = new ArrayList<>(List.copyOf(CacheHelper.MARKET_ENTRIES));

            if(this.msg.length == 1) Collections.shuffle(marketEntries);

            List<String> msg = new ArrayList<>(Arrays.asList(this.msg));

            //@see CommandPokemon - does the situation warrant the use of a parallel stream
            Stream<MarketEntry> display = marketEntries.size() > 500 && msg.size() > 4 ? marketEntries.parallelStream() : marketEntries.stream();

            //Market Sorting
            MarketListSorter marketSorter = new MarketListSorter(display, msg);

            marketSorter.sortGeneric(MarketSorterFlag.LISTINGS, m -> m.sellerID.equals(this.player.getId()));

            marketSorter.sortGeneric(MarketSorterFlag.BOT, m -> m.sellerID.equals("BOT"));

            marketSorter.sortNumeric(MarketSorterFlag.PRICE, m -> m.price);

            //Prepare for Pokemon Sorting
            PokemonListSorter pokemonSorter = marketSorter.convert();

            pokemonSorter.sortSearchName(PokemonSorterFlag.NAME, (p, s) -> p.getName().toLowerCase().contains(s));

            pokemonSorter.sortSearchName(PokemonSorterFlag.MOVE, (p, s) -> p.allMoves().contains(Global.normalize(s)));

            pokemonSorter.sortNumeric(PokemonSorterFlag.LEVEL, Pokemon::getLevel);

            pokemonSorter.sortNumeric(PokemonSorterFlag.LEVEL, Pokemon::getDynamaxLevel);

            pokemonSorter.sortNumeric(PokemonSorterFlag.IV, p -> (int)(p.getTotalIVRounded()));

            pokemonSorter.sortNumeric(PokemonSorterFlag.EV, Pokemon::getEVTotal);

            pokemonSorter.sortNumeric(PokemonSorterFlag.STAT, Pokemon::getTotalStat);

            pokemonSorter.sortEnum(PokemonSorterFlag.TYPE, Type::cast, Pokemon::isType);

            pokemonSorter.sortEnum(PokemonSorterFlag.MAIN_TYPE, Type::cast, (p, t) -> p.getType().get(0).equals(t));

            pokemonSorter.sortEnum(PokemonSorterFlag.GENDER, Gender::cast, (p, g) -> p.getGender().equals(g));

            pokemonSorter.sortEnum(PokemonSorterFlag.EGG_GROUP, EggGroup::cast, (p, e) -> p.getEggGroups().contains(e));

            pokemonSorter.sortGeneric(PokemonSorterFlag.SHINY, Pokemon::isShiny);

            pokemonSorter.sortGeneric(PokemonSorterFlag.MASTERED, Pokemon::isMastered);

            pokemonSorter.sortStats();

            pokemonSorter.sortNameCategories();

            //Convert Pokemon Stream back into a MarketEntry Stream
            display = marketSorter.rebuildStream();

            //Convert Stream back to List
            marketEntries = display.collect(Collectors.toList());

            if(msg.contains("--order") && msg.indexOf("--order") + 1 < msg.size())
            {
                String order = msg.get(msg.indexOf("--order") + 1);
                boolean asc = msg.indexOf("--order") + 2 < msg.size() && msg.get(msg.indexOf("--order") + 2).equals("a");
                OrderSort o = OrderSort.cast(order);
                if(o != null) this.sortOrder(marketEntries, o, !asc);
            }
            else this.sortOrder(marketEntries, OrderSort.RANDOM, false);

            //Finalizing
            if(marketEntries.isEmpty()) this.embed.setDescription("No market listings found with those parameters!");
            else this.embed.setDescription(this.getMarketPage(marketEntries));
        }

        return this;
    }

    private void sortOrder(List<MarketEntry> entries, OrderSort o, boolean desc)
    {
        switch(o)
        {
            case IV -> entries.sort(Comparator.comparingDouble(m -> m.pokemon.getTotalIVRounded()));
            case EV -> entries.sort(Comparator.comparingInt(m -> m.pokemon.getEVTotal()));
            case STAT -> entries.sort(Comparator.comparingInt(m -> m.pokemon.getTotalStat()));
            case LEVEL -> entries.sort(Comparator.comparingInt(m -> m.pokemon.getLevel()));
            case NAME -> entries.sort(Comparator.comparing(m -> m.pokemon.getName()));
            case PRICE -> entries.sort(Comparator.comparingInt(m -> m.price));
            case RANDOM -> Collections.shuffle(entries);
        }

        if(desc) Collections.reverse(entries);
    }

    enum OrderSort
    {
        IV,
        EV,
        STAT,
        LEVEL,
        NAME,
        PRICE,
        RANDOM;

        static OrderSort cast(String s)
        {
            for(OrderSort o : values()) if(o.toString().equals(s.toUpperCase())) return o;
            return null;
        }
    }

    private String getMarketPage(List<MarketEntry> marketEntries)
    {
        int startIndex = 0;
        if(this.msg.length >= 2 && this.isNumeric(1)) startIndex = (this.getInt(1) - 1) * 20;
        int endIndex = startIndex + 20;

        StringBuilder page = new StringBuilder();
        for(int i = startIndex; i < endIndex; i++) if(i < marketEntries.size()) page.append(marketEntries.get(i).getEntryLine(this.playerData.getSettings().get(Settings.CLIENT_DETAILED, Boolean.class))).append("\n");

        return page.toString();
    }
}
