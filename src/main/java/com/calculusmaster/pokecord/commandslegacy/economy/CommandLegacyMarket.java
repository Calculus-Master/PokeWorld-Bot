package com.calculusmaster.pokecord.commandslegacy.economy;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.commandslegacy.CommandLegacyInvalid;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.GrowthRate;
import com.calculusmaster.pokecord.game.enums.elements.Type;
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
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandLegacyMarket extends CommandLegacy
{
    public CommandLegacyMarket(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
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

            if(buy && m.sellerID.equals(this.player.getId()))
            {
                this.response = "You can't buy your own Pokemon! If you'd like to rescind a listing, use p!market collect <marketID>.";
                return this;
            }
            else if(buy && m.price <= this.playerData.getCredits())
            {
                this.playerData.changeCredits(-1 * m.price);
                this.playerData.addPokemon(m.pokemonID);

                m.pokemon.resetAugments();

                if(!m.sellerID.equals("BOT"))
                {
                    PlayerDataQuery seller = PlayerDataQuery.of(m.sellerID);

                    seller.changeCredits(m.price);
                    seller.directMessage("Your `Level " + m.pokemon.getLevel() + " " + m.pokemon.getName() + "` was sold from your Market Listing to " + this.playerData.getUsername() + " for " + m.price + " credits!");

                    seller.getStatistics().incr(PlayerStatistic.POKEMON_SOLD_MARKET);
                }

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
                String exp = chosen.getLevel() == 100 ? " Max Level " : chosen.getExp() + " / " + GrowthRate.getRequiredExp(chosen.getData().getGrowthRate(), chosen.getLevel()) + " XP";
                String gender = "Gender: " + Global.normalize(chosen.getGender().toString());
                String type = "Type: " + chosen.getType().stream().map(Type::getStyledName).collect(Collectors.joining(" | "));
                String nature = "Nature: " + Global.normalize(chosen.getNature().toString());
                String item = "Held Item: " + chosen.getItem().getStyledName();
//                String stats = CommandLegacyInfo.getStatsFormatted(chosen, this.playerData.getSettings().get(Settings.CLIENT_DETAILED, Boolean.class));

                this.embed.setTitle(title);
                this.embed.setDescription(market + "\n" + exp + "\n" + gender + "\n" + type + "\n" + nature + "\n" + item + "\n\n" + "stats");
                this.color = chosen.getType().get(0).getColor();
                this.embed.setFooter("Buy this pokemon with `p!market buy " + entry.marketID + "`!");

                String image = Pokemon.getImage(chosen.getEntity(), chosen.isShiny(), chosen, null);
                String imageAttachmentName = "info_" + chosen.getUUID() + ".png";
                this.embed.setImage("attachment://" + imageAttachmentName);
                this.event.getChannel().sendFiles(FileUpload.fromData(Pokeworld.class.getResourceAsStream(image), imageAttachmentName)).setEmbeds(this.embed.build()).queue();

                this.embed = null;
            }
            else this.embed.setDescription(CommandLegacyInvalid.getShort());
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
            MarketListSorter marketSorter = new MarketListSorter(marketEntries, display, msg);

            marketSorter.sortGeneric(MarketSorterFlag.LISTINGS, m -> m.sellerID.equals(this.player.getId()));

            marketSorter.sortGeneric(MarketSorterFlag.BOT, m -> m.sellerID.equals("BOT"));

            marketSorter.sortNumeric(MarketSorterFlag.PRICE, m -> m.price);

            //Prepare for Pokemon Sorting
            PokemonListSorter pokemonSorter = marketSorter.convert();

            pokemonSorter.sortSearchName(PokemonSorterFlag.NAME, (p, s) -> p.getName().toLowerCase().contains(s));

            pokemonSorter.sortSearchName(PokemonSorterFlag.MOVE, (p, s) -> p.getLevelUpMoves().contains(Global.normalize(s)));

            //Standards
            pokemonSorter.sortStandardNumeric();
            pokemonSorter.sortStandardEnum();
            pokemonSorter.sortStandardBoolean();
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
            else this.embed
                    .setDescription(this.getMarketPage(marketEntries))
                    .setFooter("View more information about a specific listing using the p!market info <ID> command. To purchase a Pokemon, use the p!market buy <ID> command. If you want to recollect one of your listings, use the p!market collect <ID> command.");
        }

        return this;
    }

    private void sortOrder(List<MarketEntry> entries, OrderSort o, boolean desc)
    {
        switch(o)
        {
            case IV -> entries.sort(Comparator.comparingDouble(m -> m.pokemon.getTotalIVRounded()));
            case EV -> entries.sort(Comparator.comparingInt(m -> m.pokemon.getTotalEV()));
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
