package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.PokemonRarity;
import com.calculusmaster.pokecord.util.TableBuilder;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.collections4.list.TreeList;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommandPokemon extends Command
{
    public static void init()
    {
        long i = System.currentTimeMillis();
        List<String> IDs = new ArrayList<>();
        Mongo.PlayerData.find(Filters.exists("username")).forEach(d -> IDs.add(d.getString("playerID")));

        ExecutorService pool = Executors.newFixedThreadPool(IDs.size() / 2);

        for(String s : IDs)
        {
            try {Thread.sleep(100);}
            catch (Exception e) {System.out.println("Can't Sleep Thread!");}

            pool.execute(() -> Global.updatePokemonList(s));
        }

        pool.shutdown();

        try { pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); }
        catch (Exception e) { System.out.println("CommandPokemon Init failed!"); }

        long f = System.currentTimeMillis();
        System.out.println((f - i) + "ms");
    }

    List<Pokemon> pokemon;
    public CommandPokemon(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
        //this.buildList();
        this.pokemon = new TreeList<>(Global.POKEMON_LISTS.get(this.player.getId()));
    }

    @Override
    public Command runCommand()
    {
        List<String> msg = Arrays.asList(this.msg);

        if(msg.contains("--name") && msg.indexOf("--name") + 1 < msg.size())
        {
            StringBuilder name = new StringBuilder();

            for(int i = msg.indexOf("--name") + 1; i < msg.size(); i++)
            {
                if(!msg.get(i).contains("--")) name.append(msg.get(i)).append(" ");
                else i = msg.size();
            }

            name = new StringBuilder(Global.normalCase(name.toString().trim()));

            String searchName = name.toString();

            //String name = msg.get(msg.indexOf("--name") + 1);
            if(isPokemon(searchName)) this.pokemon = this.pokemon.stream().filter(p -> p.getName().equals(searchName)).collect(Collectors.toList());
        }

        if(msg.contains("--level") && msg.indexOf("--level") + 1 < msg.size())
        {
            int index = msg.indexOf("--level") + 1;
            String after = msg.get(index);
            boolean validIndex = index + 1 < msg.size();
            if(after.equals(">") && validIndex && isNumeric(index + 1)) this.pokemon = this.pokemon.stream().filter(p -> p.getLevel() > getInt(index + 1)).collect(Collectors.toList());
            else if(after.equals("<") && validIndex && isNumeric(index + 1)) this.pokemon = this.pokemon.stream().filter(p -> p.getLevel() < getInt(index + 1)).collect(Collectors.toList());
            else if(isNumeric(index)) this.pokemon = this.pokemon.stream().filter(p -> p.getLevel() == getInt(index)).collect(Collectors.toList());
        }

        if(msg.contains("--iv") && msg.indexOf("--iv") + 1 < msg.size())
        {
            int index = msg.indexOf("--iv") + 1;
            String after = msg.get(index);
            boolean validIndex = index + 1 < msg.size();
            if(after.equals(">") && validIndex && isNumeric(index + 1)) this.pokemon = this.pokemon.stream().filter(p -> p.getTotalIVRounded() > getInt(index + 1)).collect(Collectors.toList());
            else if(after.equals("<") && validIndex && isNumeric(index + 1)) this.pokemon = this.pokemon.stream().filter(p -> p.getTotalIVRounded() < getInt(index + 1)).collect(Collectors.toList());
            else if(isNumeric(index)) this.pokemon = this.pokemon.stream().filter(p -> (int)p.getTotalIVRounded() == getInt(index)).collect(Collectors.toList());
        }

        this.sortIVs(msg, "--hpiv", "--healthiv", Stat.HP);

        this.sortIVs(msg, "--atkiv", "--attackiv", Stat.ATK);

        this.sortIVs(msg, "--defiv", "--defenseiv", Stat.DEF);

        this.sortIVs(msg, "--spatkiv", "--specialattackiv", Stat.SPATK);

        this.sortIVs(msg, "--spdefiv", "--specialdefenseiv", Stat.SPDEF);

        this.sortIVs(msg, "--spdiv", "--speediv", Stat.SPD);

        if(msg.contains("--legendary") || msg.contains("--leg"))
        {
            this.pokemon = this.pokemon.stream().filter(p -> PokemonRarity.LEGENDARY.contains(p.getName())).collect(Collectors.toList());
        }

        if(msg.contains("--mythical"))
        {
            this.pokemon = this.pokemon.stream().filter(p -> PokemonRarity.MYTHICAL.contains(p.getName())).collect(Collectors.toList());
        }

        if(msg.contains("--ub") || msg.contains("--ultrabeast"))
        {
            this.pokemon = this.pokemon.stream().filter(p -> PokemonRarity.ULTRA_BEAST.contains(p.getName())).collect(Collectors.toList());
        }

        if(msg.contains("--mega"))
        {
            this.pokemon = this.pokemon.stream().filter(p -> PokemonRarity.MEGA.contains(p.getName())).collect(Collectors.toList());
        }

        if(msg.contains("--order") && msg.indexOf("--order") + 1 < msg.size())
        {
            String order = msg.get(msg.indexOf("--order") + 1);
            boolean desc = msg.indexOf("--order") + 2 < msg.size() && msg.get(msg.indexOf("--order") + 2).equals("d");
            OrderSort o = OrderSort.cast(order);
            if(o != null) this.sortOrder(o, desc);
        }
        else this.sortOrder(OrderSort.NUMBER, false);

        if(!this.pokemon.isEmpty()) this.createListEmbed();
        else this.embed.setDescription("You have no Pokemon with those characteristics!");

        return this;
    }

    private void sortIVs(List<String> msg, String tag1, String tag2, Stat iv)
    {
        boolean hasTag1 = msg.contains(tag1) && msg.indexOf(tag1) + 1 < msg.size();
        boolean hasTag2 = msg.contains(tag2) && msg.indexOf(tag2) + 1 < msg.size();

        if(hasTag1 || hasTag2)
        {
            int index = msg.indexOf(hasTag1 ? tag1 : tag2) + 1;
            String after = msg.get(index);
            boolean validIndex = index + 1 < msg.size();
            if(after.equals(">") && validIndex && isNumeric(index + 1)) this.pokemon = this.pokemon.stream().filter(p -> p.getIVs().get(iv) > getInt(index + 1)).collect(Collectors.toList());
            else if(after.equals("<") && validIndex && isNumeric(index + 1)) this.pokemon = this.pokemon.stream().filter(p -> p.getIVs().get(iv) < getInt(index + 1)).collect(Collectors.toList());
            else if(isNumeric(index)) this.pokemon = this.pokemon.stream().filter(p -> p.getIVs().get(iv) == getInt(index)).collect(Collectors.toList());
        }
    }

    private void sortOrder(OrderSort o, boolean descending)
    {
        switch (o)
        {
            case NUMBER -> this.pokemon.sort(Comparator.comparingInt(Pokemon::getNumber));
            case IV -> this.pokemon.sort(Comparator.comparingDouble(Pokemon::getTotalIVRounded));
            case LEVEL -> this.pokemon.sort(Comparator.comparingInt(Pokemon::getLevel));
            case NAME -> this.pokemon.sort(Comparator.comparing(Pokemon::getName));
        }

        if(descending) Collections.reverse(this.pokemon);
    }

    enum OrderSort
    {
        NUMBER,
        IV,
        LEVEL,
        NAME;

        static OrderSort cast(String s)
        {
            for(OrderSort o : values()) if(o.toString().equals(s.toUpperCase())) return o;
            return null;
        }
    }

    //Do sorting before this
    private void createListEmbed()
    {
        StringBuilder sb = new StringBuilder();
        boolean hasPage = this.msg.length >= 2 && this.isNumeric(1);
        int perPage = 20;
        int startIndex = hasPage ? ((getInt(1) - 1) * perPage > this.pokemon.size() ? 0 : getInt(1)) : 0;
        if(startIndex != 0) startIndex--;

        startIndex *= perPage;
        int endIndex = Math.min(startIndex + perPage, this.pokemon.size());
        for(int i = startIndex; i < endIndex; i++)
        {
            if(i > this.pokemon.size() - 1) break;
            sb.append(this.getLine(this.pokemon.get(i)));
        }

        this.embed.setDescription(sb.toString());
        this.embed.setTitle(this.player.getName() + "'s Pokemon");
        this.embed.setFooter("Showing Numbers " + (startIndex + 1) + " to " + (endIndex) + " out of " + this.pokemon.size() + " Pokemon");
    }

    private void sendListEmbed()
    {
        boolean hasPage = this.msg.length >= 2 && this.isNumeric(1);
        int perPage = 10;
        int startIndex = hasPage ? ((getInt(1) - 1) * perPage > this.pokemon.size() ? 0 : getInt(1)) : 0;
        if(startIndex != 0) startIndex--;

        startIndex *= perPage;
        int endIndex = Math.min(startIndex + perPage, this.pokemon.size());

        String[] pokemonNames = new String[endIndex - startIndex];
        String[][] pokemonData = new String[endIndex - startIndex][3];

        for(int i = startIndex; i < endIndex; i++)
        {
            pokemonNames[i - startIndex] = this.pokemon.get(i).getName();

            pokemonData[i - startIndex][0] = "Number: " + this.pokemon.get(i).getNumber();
            pokemonData[i - startIndex][1] = "Level: " + this.pokemon.get(i).getLevel();
            pokemonData[i - startIndex][2] = "Total IV: " + this.pokemon.get(i).getTotalIV();
        }

        TableBuilder pokemonTable = new TableBuilder()
                .setAlignment(TableBuilder.Alignment.LEFT)
                .addHeaders("Number", "Level", "Total IV")
                .addRowNames(pokemonNames)
                .setValues(pokemonData)
                .setBorders(TableBuilder.Borders.HEADER_ROW_FRAME)
                .frame(false);

        this.embed.setDescription(pokemonTable.build());
        this.embed.setTitle(this.player.getName() + "'s Pokemon");
        this.embed.setFooter("Showing Numbers " + (startIndex + 1) + " to " + (endIndex) + " out of " + this.pokemon.size() + " Pokemon");

        this.embed = null;
        this.event.getChannel().sendMessage("```\n" + pokemonTable.build() + "\n```").queue();
    }

//    @Deprecated
//    private void buildList()
//    {
//        //long l = System.currentTimeMillis();
//        for(int i = 0; i < this.playerData.getPokemonList().length(); i++) this.pokemon.add(Pokemon.buildCore(this.playerData.getPokemonList().getString(i), i));
//        //System.out.println("Creating the full list took " + (System.currentTimeMillis() - l) + "ms");
//    }

    private String getLine(Pokemon p)
    {
        return "**" + p.getName() + "** " + (this.playerData.isInTeam(p.getUUID()) ? "(T) " : "") + "| Number: " + p.getNumber() + " | Level " + p.getLevel() + " | Total IV: " + p.getTotalIV() + "\n";
    }
}
