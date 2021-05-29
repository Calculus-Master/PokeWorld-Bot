package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.pokemon.CommandInfo;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.GrowthRate;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.PokemonRarity;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommandMarket extends Command
{
    public static List<MarketEntry> marketEntries = new ArrayList<>();

    public static void init()
    {
        long i = System.currentTimeMillis();
        List<String> IDs = new ArrayList<>();
        Mongo.MarketData.find(Filters.exists("marketID")).forEach(d -> IDs.add(d.getString("marketID")));

        int split = 20;
        List<List<String>> totalList = new ArrayList<>();

        for (int j = 0; j < IDs.size(); j += split)
        {
            totalList.add(IDs.subList(j, j + split >= IDs.size() ? IDs.size() - 1 : j + split));
        }


        ExecutorService pool = Executors.newFixedThreadPool(IDs.size() / split);

        for(List<String> l : totalList)
        {
            try {Thread.sleep(100);}
            catch (Exception e) {System.out.println("Can't Sleep Thread!");}

            pool.execute(() -> { for(String s : l) marketEntries.add(MarketEntry.build(s)); });
        }

        pool.shutdown();

        try { pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); }
        catch (Exception e) { System.out.println("CommandMarket Init failed!"); }

        long f = System.currentTimeMillis();
        System.out.println((f - i) + "ms");
    }

    public CommandMarket(MessageReceivedEvent event, String[] msg) {
        super(event, msg);
    }

    //TODO: Add invalid messages for each if block
    @Override
    public Command runCommand()
    {
        if (this.msg.length >= 4 && (this.msg[1].equals("list") || this.msg[1].equals("sell")) && isNumeric(2) && isNumeric(3) && Integer.parseInt(this.msg[2]) <= this.playerData.getPokemonList().length() && Integer.parseInt(this.msg[3]) > 0)
        {
            //Add pokemon to the market
            MarketEntry newMarketEntry = MarketEntry.create(this.player.getId(), this.player.getName(), this.playerData.getPokemonList().getString(Integer.parseInt(this.msg[2]) - 1), Integer.parseInt(this.msg[3]));
            marketEntries.add(newMarketEntry);
            this.playerData.removePokemon(newMarketEntry.pokemonID);

            this.embed.setDescription("Listed your " + newMarketEntry.pokemon.getName() + " for " + newMarketEntry.price + "c!");
        }
        else if (this.msg.length >= 3 && this.msg[1].equals("buy") && isNumeric(2) && MarketEntry.isIDValid(this.msg[2]))
        {
            //Buy pokemon from market
            MarketEntry entry = marketEntries.stream().filter(m -> m.marketID.equals(this.msg[2])).collect(Collectors.toList()).get(0);
            if (!this.player.getId().equals(entry.sellerID) && this.playerData.getCredits() >= entry.price)
            {
                marketEntries.remove(entry);
                Mongo.MarketData.deleteOne(Filters.eq("marketID", entry.marketID));

                this.playerData.changeCredits(-1 * entry.price);
                this.playerData.addPokemon(entry.pokemonID);
                if(!entry.sellerID.equals("BOT")) new PlayerDataQuery(entry.sellerID).changeCredits(entry.price);

                this.embed.setDescription("Purchased a Level " + entry.pokemon.getLevel() + " " + entry.pokemon.getName() + " for " + entry.price + "c!");
            }
        }
        else if(this.msg.length >= 3 && this.msg[1].equals("collect") && isNumeric(2) && MarketEntry.isIDValid(this.msg[2]))
        {
            //Collect a listing from market
            MarketEntry entry = marketEntries.stream().filter(m -> m.marketID.equals(this.msg[2])).collect(Collectors.toList()).get(0);
            if(this.player.getId().equals(entry.sellerID))
            {
                marketEntries.remove(entry);
                Mongo.MarketData.deleteOne(Filters.eq("marketID", entry.marketID));

                this.playerData.addPokemon(entry.pokemonID);
                this.embed.setDescription("Collected your Level " + entry.pokemon.getLevel() + " " + entry.pokemon.getName() + " from the market!");
            }
        }
        else if (this.msg.length >= 3 && this.msg[1].equals("info") && isNumeric(2) && MarketEntry.isIDValid(this.msg[2]))
        {
            MarketEntry entry = marketEntries.stream().filter(m -> m.marketID.equals(this.msg[2])).collect(Collectors.toList()).get(0);
            Pokemon chosen = Pokemon.build(entry.pokemonID);

            String title = "**Level " + chosen.getLevel() + " " + chosen.getName() + "**" + (chosen.isShiny() ? " :star2:" : "");
            String market = "Market ID: " + entry.marketID + " | Price: " + entry.price + "\nSold by: " + entry.sellerName;
            String exp = chosen.getLevel() == 100 ? " Max Level " : chosen.getExp() + " / " + GrowthRate.getRequiredExp(chosen.getGenericJSON().getString("growthrate"), chosen.getLevel()) + " XP";
            String type = "Type: " + (chosen.getType()[0].equals(chosen.getType()[1]) ? Global.normalCase(chosen.getType()[0].toString()) : Global.normalCase(chosen.getType()[0].toString()) + " | " + Global.normalCase(chosen.getType()[1].toString()));
            String nature = "Nature: " + Global.normalCase(chosen.getNature().toString());
            String item = "Held Item: " + PokeItem.asItem(chosen.getItem()).getStyledName();
            String stats = CommandInfo.getStatsFormatted(chosen);

            this.embed.setTitle(title);
            this.embed.setDescription(market + "\n" + exp + "\n" + type + "\n" + nature + "\n" + item + "\n\n" + stats);
            this.color = chosen.getType()[0].getColor();
            this.embed.setImage(chosen.getImage());
            this.embed.setFooter("Buy this pokemon with `p!market buy " + entry.marketID + "`!");
        }
        else
        {
            List<MarketEntry> display = new ArrayList<>(List.copyOf(marketEntries));

            //p!market listings - Shows only the player's listings
            if(this.msg.length == 1) Collections.shuffle(display);
            else if (this.msg[1].equals("listings")) display = display.stream().filter(m -> m.sellerID.equals(this.player.getId())).collect(Collectors.toList());
            else if (this.msg[1].equals("search"))
            {
                List<String> args = new ArrayList<>(Arrays.asList(this.msg));

                if (args.contains("--name") && args.indexOf("--name") + 1 < args.size())
                {
                    String name = args.get(args.indexOf("--name") + 1);
                    if (isPokemon(name)) display = display.stream().filter(m -> m.pokemon.getName().equals(Global.normalCase(name))).collect(Collectors.toList());
                }

                if (args.contains("--level") && args.indexOf("--level") + 1 < args.size())
                {
                    int index = args.indexOf("--level") + 1;
                    String after = args.get(index);
                    boolean validIndex = index + 1 < args.size();
                    if (after.equals(">") && validIndex && isNumeric(index + 1)) display = display.stream().filter(m -> m.pokemon.getLevel() > getInt(index + 1)).collect(Collectors.toList());
                    else if (after.equals("<") && validIndex && isNumeric(index + 1)) display = display.stream().filter(m -> m.pokemon.getLevel() < getInt(index + 1)).collect(Collectors.toList());
                    else if (isNumeric(index)) display = display.stream().filter(m -> m.pokemon.getLevel() == getInt(index)).collect(Collectors.toList());
                }

                if (args.contains("--iv") && args.indexOf("--iv") + 1 < args.size())
                {
                    int index = args.indexOf("--iv") + 1;
                    String after = args.get(index);
                    boolean validIndex = index + 1 < args.size();
                    if (after.equals(">") && validIndex && isNumeric(index + 1)) display = display.stream().filter(m -> m.pokemon.getTotalIVRounded() > getInt(index + 1)).collect(Collectors.toList());
                    else if (after.equals("<") && validIndex && isNumeric(index + 1)) display = display.stream().filter(m -> m.pokemon.getTotalIVRounded() < getInt(index + 1)).collect(Collectors.toList());
                    else if (isNumeric(index)) display = display.stream().filter(m -> (int) m.pokemon.getTotalIVRounded() == getInt(index)).collect(Collectors.toList());
                }

                if (args.contains("--price") && args.indexOf("--price") + 1 < args.size())
                {
                    int index = args.indexOf("--price") + 1;
                    String after = args.get(index);
                    boolean validIndex = index + 1 < args.size();
                    if (after.equals(">") && validIndex && isNumeric(index + 1)) display = display.stream().filter(m -> m.price > getInt(index + 1)).collect(Collectors.toList());
                    else if (after.equals("<") && validIndex && isNumeric(index + 1)) display = display.stream().filter(m -> m.price < getInt(index + 1)).collect(Collectors.toList());
                    else if (isNumeric(index)) display = display.stream().filter(m -> m.price == getInt(index)).collect(Collectors.toList());
                }

                if(args.contains("--legendary") || args.contains("--leg"))
                {
                    display = display.stream().filter(m -> PokemonRarity.LEGENDARY.contains(m.pokemon.getName())).collect(Collectors.toList());
                }

                if(args.contains("--mythical"))
                {
                    display = display.stream().filter(m -> PokemonRarity.MYTHICAL.contains(m.pokemon.getName())).collect(Collectors.toList());
                }

                if(args.contains("--ub") || args.contains("--ultrabeast"))
                {
                    display = display.stream().filter(m -> PokemonRarity.ULTRA_BEAST.contains(m.pokemon.getName())).collect(Collectors.toList());
                }

                if(args.contains("--mega"))
                {
                    display = display.stream().filter(m -> PokemonRarity.MEGA.contains(m.pokemon.getName())).collect(Collectors.toList());
                }

                if (args.contains("--order") && (args.indexOf("--order") + 1) < args.size())
                {
                    switch (args.get(args.indexOf("--order") + 1))
                    {
                        case "number" -> display.sort(Comparator.comparingInt(m -> m.pokemon.getNumber()));
                        case "iv" -> display.sort((m1, m2) -> (int) (Double.parseDouble(m1.pokemon.getTotalIV().substring(0, 5)) - Double.parseDouble(m2.pokemon.getTotalIV().substring(0, 5))));
                        case "level" -> display.sort(Comparator.comparingInt(m -> m.pokemon.getLevel()));
                        case "price" -> display.sort(Comparator.comparingInt(m -> m.price));
                        default -> display.sort(Comparator.comparing(m -> m.pokemon.getName()));
                    }
                }
                else display.sort(Comparator.comparing(m -> m.pokemon.getName()));
            }

            this.embed.setTitle("Market Listings");

            if (display.isEmpty())
                this.embed.setDescription("There are no market listings. Add one using p!market list <pokemon> <price>!");
            else
                this.embed.setDescription(getMarketPage(display, this.msg.length > 2 && isNumeric(2) ? Integer.parseInt(this.msg[2]) : 0));
        }

        return this;
    }

    public static void addBotEntry()
    {
        Pokemon p = Pokemon.create(Global.POKEMON.get(new Random().nextInt(Global.POKEMON.size())));
        p.setLevel(new Random().nextInt(100) + 1);

        Random r = new Random();
        boolean rare = r.nextInt(100) < 10;
        StringBuilder condensed = new StringBuilder();
        for(int i = 0; i < 6; i++) condensed.append(rare ? r.nextInt(252) : r.nextInt(100)).append("-");
        condensed.deleteCharAt(condensed.length() - 1);

        p.setEVs(condensed.toString());

        Pokemon.uploadPokemon(p);

        int val = p.getValue();

        val += new Random().nextInt(val / 4) * (new Random().nextInt(50) < 25 ? -1 : 1);

        MarketEntry m = MarketEntry.create("BOT", "BOT", p.getUUID(), val);

        marketEntries.add(m);
    }

    private static String getMarketPage(List<MarketEntry> list, int start)
    {
        StringBuilder mList = new StringBuilder();

        for(int i = start * 20; i < start * 20 + 20; i++) if(i < list.size()) mList.append(list.get(i).getEntryLine()).append("\n");

        return mList.toString();
    }

    public static class MarketEntry
    {
        public String marketID;
        public String sellerID;
        public String sellerName;
        public String pokemonID;
        public int price;
        public Pokemon pokemon;

        public static MarketEntry create(String sellerID, String sellerName, String pokemonID, int price)
        {
            String marketID = generateUUID();
            Document marketData = new Document("marketID", marketID).append("sellerID", sellerID).append("sellerName", sellerName).append("pokemonID", pokemonID).append("price", price);
            Mongo.MarketData.insertOne(marketData);

            return build(marketID);
        }

        public static MarketEntry build(String marketID)
        {
            MarketEntry m = new MarketEntry();
            m.marketID = marketID;

            JSONObject marketJSON = getMarketJSON(marketID);
            m.sellerID = marketJSON.getString("sellerID");
            m.sellerName = marketJSON.getString("sellerName");
            m.pokemonID = marketJSON.getString("pokemonID");
            m.price = marketJSON.getInt("price");
            m.pokemon = Pokemon.buildCore(m.pokemonID, -1);

            return m;
        }

        private String getEntryLine()
        {
            return "ID: " + this.marketID + " | Level " + this.pokemon.getLevel() + " " + this.pokemon.getName() + " | Price: " + this.price + " | Total IV: " + this.pokemon.getTotalIV();
        }

        public static boolean isIDValid(String marketID)
        {
            return marketEntries.stream().anyMatch(m -> m.marketID.equals(marketID));
        }

        public static JSONObject getMarketJSON(String marketID)
        {
            return new JSONObject(Mongo.MarketData.find(Filters.eq("marketID", marketID)).first().toJson());
        }

        private static String generateUUID()
        {
            String digits = "0123456789";
            StringBuilder s = new StringBuilder();
            for(int i = 0; i < 8; i++) s.append(digits.charAt(new Random().nextInt(digits.length())));
            return s.toString();
        }

    }
}
