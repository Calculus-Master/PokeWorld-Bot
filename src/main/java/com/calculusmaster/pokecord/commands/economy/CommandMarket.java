package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.commands.pokemon.CommandInfo;
import com.calculusmaster.pokecord.commands.pokemon.CommandPokemon;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.player.level.PlayerLevel;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.game.trade.elements.MarketEntry;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import com.calculusmaster.pokecord.util.helpers.SettingsHelper;
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
        boolean list = this.msg.length >= 4 && (this.msg[1].equals("list") || this.msg[1].equals("sell")) && this.isNumeric(2) && this.isNumeric(3) && this.getInt(2) >= 1 && this.getInt(2) <= this.playerData.getPokemonList().size();
        boolean buy = this.msg.length == 3 && this.msg[1].equals("buy") && MarketEntry.isValidID(this.msg[2]);
        boolean collect = this.msg.length == 3 && this.msg[1].equals("collect") && MarketEntry.isValidID(this.msg[2]);
        boolean info = this.msg.length == 3 && this.msg[1].equals("info") && MarketEntry.isValidID(this.msg[2]);

        if(list)
        {
            if(this.playerData.getLevel() < PlayerLevel.REQUIRED_LEVEL_MARKET_LIST) this.sendMsg("You need to be Pokemon Mastery Level " + PlayerLevel.REQUIRED_LEVEL_MARKET_LIST + " to list Pokemon on the market!");
            else
            {
                MarketEntry newEntry = MarketEntry.create(this.player.getId(), this.player.getName(), this.playerData.getPokemonList().get(this.getInt(2) - 1), this.getInt(3));

                this.playerData.removePokemon(newEntry.pokemonID);

                this.sendMsg("You successfully listed your Level " + newEntry.pokemon.getLevel() + " " + newEntry.pokemon.getName() + "` for " + newEntry.price + " credits!");
            }
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
                    PlayerDataQuery seller = new PlayerDataQuery(m.sellerID);

                    seller.changeCredits(m.price);
                    seller.directMessage("Your `Level " + m.pokemon.getLevel() + " " + m.pokemon.getName() + "` was sold from your Market Listing to " + this.playerData.getUsername() + " for " + m.price + " credits!");

                    Achievements.grant(m.sellerID, Achievements.SOLD_FIRST_POKEMON_MARKET, this.event);
                    seller.getStats().incr(PlayerStatistic.POKEMON_SOLD_MARKET);
                }

                Achievements.grant(this.player.getId(), Achievements.BOUGHT_FIRST_POKEMON_MARKET, this.event);
                this.playerData.getStats().incr(PlayerStatistic.POKEMON_BOUGHT_MARKET);

                this.sendMsg("You successfully bought `Level " + m.pokemon.getLevel() + " " + m.pokemon.getName() + "` for " + m.price + " credits!");
                MarketEntry.delete(m.marketID);
            }
            else if(collect && m.sellerID.equals(this.player.getId()))
            {
                this.playerData.addPokemon(m.pokemonID);

                this.sendMsg("You successfully retrieved your `Level " + m.pokemon.getLevel() + " " + m.pokemon.getName() + "` from the market!");
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
                String type = "Type: " + (chosen.getType()[0].equals(chosen.getType()[1]) ? Global.normalize(chosen.getType()[0].toString()) : Global.normalize(chosen.getType()[0].toString()) + " | " + Global.normalize(chosen.getType()[1].toString()));
                String nature = "Nature: " + Global.normalize(chosen.getNature().toString());
                String item = "Held Item: " + Item.asItem(chosen.getItem()).getStyledName();
                String stats = CommandInfo.getStatsFormatted(chosen, this.playerData.getSettings().getSettingBoolean(SettingsHelper.Setting.CLIENT_DETAILED));

                this.embed.setTitle(title);
                this.embed.setDescription(market + "\n" + exp + "\n" + gender + "\n" + type + "\n" + nature + "\n" + item + "\n\n" + stats);
                this.color = chosen.getType()[0].getColor();
                this.embed.setImage(chosen.getImage());
                this.embed.setFooter("Buy this pokemon with `p!market buy " + entry.marketID + "`!");
            }
            else this.embed.setDescription(CommandInvalid.getShort());
        }
        //Standard Display of Market Entries
        else
        {
            List<MarketEntry> marketEntries = new ArrayList<>(List.copyOf(CacheHelper.MARKET_ENTRIES));
            Stream<MarketEntry> display = marketEntries.stream();

            if(this.msg.length == 1) Collections.shuffle(marketEntries);

            List<String> msg = new ArrayList<>(Arrays.asList(this.msg));

            //Market Specific Sorting
            if(msg.contains("--listings"))
            {
                display = display.filter(m -> m.sellerID.equals(this.player.getId()));
            }

            if(msg.contains("--bot"))
            {
                display = display.filter(m -> m.sellerID.equals("BOT"));
            }

            if(msg.contains("--price") && msg.indexOf("--price") + 1 < msg.size())
            {
                int index = msg.indexOf("--price") + 1;
                String after = msg.get(index);
                boolean validIndex = index + 1 < msg.size();

                if (after.equals(">") && validIndex && isNumeric(index + 1)) display = display.filter(m -> m.price > getInt(index + 1));
                else if (after.equals("<") && validIndex && isNumeric(index + 1)) display = display.filter(m -> m.price < getInt(index + 1));
                else if (isNumeric(index)) display = display.filter(m -> m.price == getInt(index));
            }

            //General Sorting (Common with CommandPokemon)
            if(msg.contains("--name") && msg.indexOf("--name") + 1 < msg.size())
            {
                display = display.filter(m -> CommandPokemon.getSearchNames(msg, "--name").stream().anyMatch(s -> m.pokemon.getName().toLowerCase().contains(s)));
            }

            if(msg.contains("--level") && msg.indexOf("--level") + 1 < msg.size())
            {
                int index = msg.indexOf("--level") + 1;
                String after = msg.get(index);
                boolean validIndex = index + 1 < msg.size();

                if (after.equals(">") && validIndex && isNumeric(index + 1)) display = display.filter(m -> m.pokemon.getLevel() > getInt(index + 1));
                else if (after.equals("<") && validIndex && isNumeric(index + 1)) display = display.filter(m -> m.pokemon.getLevel() < getInt(index + 1));
                else if (isNumeric(index)) display = display.filter(m -> m.pokemon.getLevel() == getInt(index));
            }

            if(msg.contains("--dlevel") && msg.indexOf("--dlevel") + 1 < msg.size())
            {
                int index = msg.indexOf("--dlevel") + 1;
                String after = msg.get(index);
                boolean validIndex = index + 1 < msg.size();

                if (after.equals(">") && validIndex && isNumeric(index + 1)) display = display.filter(m -> m.pokemon.getDynamaxLevel() > getInt(index + 1));
                else if (after.equals("<") && validIndex && isNumeric(index + 1)) display = display.filter(m -> m.pokemon.getDynamaxLevel() < getInt(index + 1));
                else if (isNumeric(index)) display = display.filter(m -> m.pokemon.getDynamaxLevel() == getInt(index));
            }

            if(msg.contains("--iv") && msg.indexOf("--iv") + 1 < msg.size())
            {
                int index = msg.indexOf("--iv") + 1;
                String after = msg.get(index);
                boolean validIndex = index + 1 < msg.size();

                if (after.equals(">") && validIndex && isNumeric(index + 1)) display = display.filter(m -> m.pokemon.getTotalIVRounded() > getInt(index + 1));
                else if (after.equals("<") && validIndex && isNumeric(index + 1)) display = display.filter(m -> m.pokemon.getTotalIVRounded() < getInt(index + 1));
                else if (isNumeric(index)) display = display.filter(m -> (int)m.pokemon.getTotalIVRounded() == getInt(index));
            }

            if(msg.contains("--ev") && msg.indexOf("--ev") + 1 < msg.size())
            {
                int index = msg.indexOf("--ev") + 1;
                String after = msg.get(index);
                boolean validIndex = index + 1 < msg.size();

                if (after.equals(">") && validIndex && isNumeric(index + 1)) display = display.filter(m -> m.pokemon.getEVTotal() > getInt(index + 1));
                else if (after.equals("<") && validIndex && isNumeric(index + 1)) display = display.filter(m -> m.pokemon.getEVTotal() < getInt(index + 1));
                else if (isNumeric(index)) display = display.filter(m -> m.pokemon.getEVTotal() == getInt(index));
            }

            display = this.sortIVs(display, msg, "--hpiv", "--healthiv", Stat.HP);

            display = this.sortIVs(display, msg, "--atkiv", "--attackiv", Stat.ATK);

            display = this.sortIVs(display, msg, "--defiv", "--defenseiv", Stat.DEF);

            display = this.sortIVs(display, msg, "--spatkiv", "--specialattackiv", Stat.SPATK);

            display = this.sortIVs(display, msg, "--spdefiv", "--specialdefenseiv", Stat.SPDEF);

            display = this.sortIVs(display, msg, "--spdiv", "--speediv", Stat.SPD);

            display = this.sortEVs(display, msg, "--hpev", "--healthev", Stat.HP);

            display = this.sortEVs(display, msg, "--atkev", "--attackev", Stat.ATK);

            display = this.sortEVs(display, msg, "--defev", "--defenseev", Stat.DEF);

            display = this.sortEVs(display, msg, "--spatkev", "--specialattackev", Stat.SPATK);

            display = this.sortEVs(display, msg, "--spdefev", "--specialdefenseev", Stat.SPDEF);

            display = this.sortEVs(display, msg, "--spdev", "--speediv", Stat.SPD);

            if(msg.contains("--type") && msg.indexOf("--type") + 1 < msg.size() && Type.cast(msg.get(msg.indexOf("--type") + 1)) != null)
            {
                Type t = Type.cast(msg.get(msg.indexOf("--type") + 1));
                display = display.filter(m -> m.pokemon.isType(t));
            }

            if(msg.contains("--maintype") && msg.indexOf("--maintype") + 1 < msg.size() && Type.cast(msg.get(msg.indexOf("--maintype") + 1)) != null)
            {
                Type t = Type.cast(msg.get(msg.indexOf("--maintype") + 1));
                display = display.filter(m -> m.pokemon.getType()[0].equals(t));
            }

            if(msg.contains("--sidetype") && msg.indexOf("--sidetype") + 1 < msg.size() && Type.cast(msg.get(msg.indexOf("--sidetype") + 1)) != null)
            {
                Type t = Type.cast(msg.get(msg.indexOf("--sidetype") + 1));
                display = display.filter(m -> m.pokemon.getType()[1].equals(t));
            }

            if(msg.contains("--gender") && msg.indexOf("--gender") + 1 < msg.size() && Gender.cast(msg.get(msg.indexOf("--gender") + 1)) != null)
            {
                Gender g = Gender.cast(msg.get(msg.indexOf("--gender") + 1));
                display = display.filter(m -> m.pokemon.getGender().equals(g));
            }

            if(msg.contains("--egggroup") && msg.indexOf("--egggroup") + 1 < msg.size() && EggGroup.cast(msg.get(msg.indexOf("--egggroup") + 1)) != null)
            {
                EggGroup g = EggGroup.cast(msg.get(msg.indexOf("--egggroup") + 1));
                display = display.filter(m -> m.pokemon.getEggGroup().contains(g));
            }

            if(msg.contains("--shiny"))
            {
                display = display.filter(m -> m.pokemon.isShiny());
            }

            if(msg.contains("--legendary") || msg.contains("--leg"))
            {
                display = display.filter(m -> PokemonRarity.LEGENDARY.contains(m.pokemon.getName()));
            }

            if(msg.contains("--mythical"))
            {
                display = display.filter(m -> PokemonRarity.MYTHICAL.contains(m.pokemon.getName()));
            }

            if(msg.contains("--ub") || msg.contains("--ultrabeast"))
            {
                display = display.filter(m -> PokemonRarity.ULTRA_BEAST.contains(m.pokemon.getName()));
            }

            if(msg.contains("--mega"))
            {
                display = display.filter(m -> PokemonRarity.MEGA.contains(m.pokemon.getName()));
            }

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

    private Stream<MarketEntry> sortIVs(Stream<MarketEntry> display, List<String> msg, String tag1, String tag2, Stat iv)
    {
        boolean hasTag1 = msg.contains(tag1) && msg.indexOf(tag1) + 1 < msg.size();
        boolean hasTag2 = msg.contains(tag2) && msg.indexOf(tag2) + 1 < msg.size();

        if(hasTag1 || hasTag2)
        {
            int index = msg.indexOf(hasTag1 ? tag1 : tag2) + 1;
            String after = msg.get(index);
            boolean validIndex = index + 1 < msg.size();
            if(after.equals(">") && validIndex && isNumeric(index + 1)) return display.filter(m -> m.pokemon.getIVs().get(iv) > getInt(index + 1));
            else if(after.equals("<") && validIndex && isNumeric(index + 1)) return display.filter(m -> m.pokemon.getIVs().get(iv) < getInt(index + 1));
            else if(isNumeric(index)) return display.filter(m -> m.pokemon.getIVs().get(iv) == getInt(index));
        }

        return display;
    }

    private Stream<MarketEntry> sortEVs(Stream<MarketEntry> display, List<String> msg, String tag1, String tag2, Stat ev)
    {
        boolean hasTag1 = msg.contains(tag1) && msg.indexOf(tag1) + 1 < msg.size();
        boolean hasTag2 = msg.contains(tag2) && msg.indexOf(tag2) + 1 < msg.size();

        if(hasTag1 || hasTag2)
        {
            int index = msg.indexOf(hasTag1 ? tag1 : tag2) + 1;
            String after = msg.get(index);
            boolean validIndex = index + 1 < msg.size();
            if(after.equals(">") && validIndex && isNumeric(index + 1)) return display.filter(m -> m.pokemon.getEVs().get(ev) > getInt(index + 1));
            else if(after.equals("<") && validIndex && isNumeric(index + 1)) return display.filter(m -> m.pokemon.getEVs().get(ev) < getInt(index + 1));
            else if(isNumeric(index)) return display.filter(m -> m.pokemon.getEVs().get(ev) == getInt(index));
        }

        return display;
    }

    private void sortOrder(List<MarketEntry> entries, OrderSort o, boolean desc)
    {
        switch(o)
        {
            case IV -> entries.sort(Comparator.comparingDouble(m -> m.pokemon.getTotalIVRounded()));
            case EV -> entries.sort(Comparator.comparingInt(m -> m.pokemon.getEVTotal()));
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
        for(int i = startIndex; i < endIndex; i++) if(i < marketEntries.size()) page.append(marketEntries.get(i).getEntryLine(this.playerData.getSettings().getSettingBoolean(SettingsHelper.Setting.CLIENT_DETAILED))).append("\n");

        return page.toString();
    }

    public static void addBotEntry()
    {
        Pokemon p = Pokemon.create(PokemonData.POKEMON.get(new Random().nextInt(PokemonData.POKEMON.size())));
        p.setLevel(new Random().nextInt(100) + 1);

        Random r = new Random();
        boolean rare = r.nextInt(100) < 10;
        StringBuilder condensed = new StringBuilder();
        for(int i = 0; i < 6; i++) condensed.append(rare ? r.nextInt(100) : r.nextInt(30)).append("-");
        condensed.deleteCharAt(condensed.length() - 1);

        p.setEVs(condensed.toString());

        Pokemon.uploadPokemon(p);

        int val = p.getValue();

        val += new Random().nextInt(val / 4) * (new Random().nextInt(50) < 25 ? -1 : 1);

        MarketEntry.create("BOT", "BOT", p.getUUID(), val);
    }
}
