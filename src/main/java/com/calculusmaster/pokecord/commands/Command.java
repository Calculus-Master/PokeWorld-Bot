package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.commands.misc.CommandHelp;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.calculusmaster.pokecord.commands.misc.CommandHelp.CommandCategory.*;

public abstract class Command
{
    public static final CommandHelp.HelpEntry START = new CommandHelp.HelpEntry("start");
    public static final CommandHelp.HelpEntry BALANCE = new CommandHelp.HelpEntry("balance");
    public static final CommandHelp.HelpEntry SELECT = new CommandHelp.HelpEntry("select");
    public static final CommandHelp.HelpEntry DEX = new CommandHelp.HelpEntry("dex");
    public static final CommandHelp.HelpEntry INFO = new CommandHelp.HelpEntry("info");
    public static final CommandHelp.HelpEntry CATCH = new CommandHelp.HelpEntry("catch");
    public static final CommandHelp.HelpEntry POKEMON = new CommandHelp.HelpEntry("pokemon");
    public static final CommandHelp.HelpEntry MOVES = new CommandHelp.HelpEntry("moves");
    public static final CommandHelp.HelpEntry MOVEINFO = new CommandHelp.HelpEntry("moveinfo");
    public static final CommandHelp.HelpEntry LEARN = new CommandHelp.HelpEntry("learn");
    public static final CommandHelp.HelpEntry REPLACE = new CommandHelp.HelpEntry("replace");
    public static final CommandHelp.HelpEntry DUEL = new CommandHelp.HelpEntry("duel");
    public static final CommandHelp.HelpEntry USE = new CommandHelp.HelpEntry("use");
    public static final CommandHelp.HelpEntry SHOP = new CommandHelp.HelpEntry("shop");
    public static final CommandHelp.HelpEntry BUY = new CommandHelp.HelpEntry("buy");
    public static final CommandHelp.HelpEntry RELEASE = new CommandHelp.HelpEntry("release");
    public static final CommandHelp.HelpEntry REPORT = new CommandHelp.HelpEntry("bugreport");
    public static final CommandHelp.HelpEntry TEACH = new CommandHelp.HelpEntry("teach");
    public static final CommandHelp.HelpEntry INVENTORY = new CommandHelp.HelpEntry("inventory");
    public static final CommandHelp.HelpEntry HELP = new CommandHelp.HelpEntry("help");
    public static final CommandHelp.HelpEntry TRADE = new CommandHelp.HelpEntry("trade");
    public static final CommandHelp.HelpEntry GIVE = new CommandHelp.HelpEntry("give");
    public static final CommandHelp.HelpEntry MARKET = new CommandHelp.HelpEntry("market");
    public static final CommandHelp.HelpEntry EVOLVE = new CommandHelp.HelpEntry("evolve");
    public static final CommandHelp.HelpEntry EQUIP = new CommandHelp.HelpEntry("equip");
    public static final CommandHelp.HelpEntry TEAM = new CommandHelp.HelpEntry("team");
    public static final CommandHelp.HelpEntry MEGA = new CommandHelp.HelpEntry("mega");
    public static final CommandHelp.HelpEntry WILDDUEL = new CommandHelp.HelpEntry("wildduel");
    public static final CommandHelp.HelpEntry REDEEM = new CommandHelp.HelpEntry("redeem");
    public static final CommandHelp.HelpEntry TRAINERDUEL = new CommandHelp.HelpEntry("trainerduel");
    public static final CommandHelp.HelpEntry GYMDUEL = new CommandHelp.HelpEntry("gymduel");
    public static final CommandHelp.HelpEntry ABILITYINFO = new CommandHelp.HelpEntry("abilityinfo");
    public static final CommandHelp.HelpEntry ACTIVATE = new CommandHelp.HelpEntry("activate");
    public static final CommandHelp.HelpEntry POKEPASS = new CommandHelp.HelpEntry("pokepass");
    public static final CommandHelp.HelpEntry FAVORITES = new CommandHelp.HelpEntry("favorites");
    public static final CommandHelp.HelpEntry FORM = new CommandHelp.HelpEntry("form");
    public static final CommandHelp.HelpEntry FLEE = new CommandHelp.HelpEntry("flee");
    public static final CommandHelp.HelpEntry NICKNAME = new CommandHelp.HelpEntry("nickname");
    public static final CommandHelp.HelpEntry SETTINGS = new CommandHelp.HelpEntry("settings");

    public static final CommandHelp.HelpEntry DEV = new CommandHelp.HelpEntry("dev");

    public static void init()
    {
        START.setCategory(MISC)
                .addShortDescription("Start your journey!")
                .addAliases()
                .addArgs("starter")
                .addArgDesc("starter", "The starter Pokemon you want to begin with.");

        BALANCE.setCategory(ECONOMY)
                .addShortDescription("Check how much credits you have.")
                .addAliases("credits", "bal", "c");

        SELECT.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addShortDescription("Select a pokemon to be your main one!")
                .addAliases()
                .addArgs("index")
                .addArgDesc("index", "The number of the Pokemon you want to select.");

        DEX.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addShortDescription("View generic info about a Pokemon by name.")
                .addAliases()
                .addArgs("name")
                .addArgDesc("name", "The name of the Pokemon you want info about.");

        INFO.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addShortDescription("View specific info about a Pokemon you have.")
                .addAliases()
                .addArgs(" ", "number")
                .addArgDesc(" ", "If you don't specify an argument, you will view info about your selected Pokemon.")
                .addArgDesc("number", "Number of the Pokemon you want to see info about.");

        CATCH.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addShortDescription("Catch a spawned Pokemon!")
                .addAliases()
                .addArgs("name")
                .addArgDesc("name", "Your guess for the name of the spawned Pokemon.");

        POKEMON.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addShortDescription("View your Pokemon!")
                .addAliases("p")
                .addArgs("page")
                .addArgDesc("page", "The page of Pokemon you want to view (if you have more than 20).");

        MOVES.setCategory(CommandHelp.CommandCategory.MOVES)
                .addShortDescription("View your Pokemon's moves.")
                .addAliases("m");

        MOVEINFO.setCategory(CommandHelp.CommandCategory.MOVES)
                .addShortDescription("View info about a specific move.")
                .addAliases("mi")
                .addArgs("name")
                .addArgDesc("name", "The name of the move you want to view info about.");

        LEARN.setCategory(CommandHelp.CommandCategory.MOVES)
                .addShortDescription("Teach your Pokemon a move!")
                .addAliases()
                .addArgs("name")
                .addArgDesc("name", "The move you want your Pokemon to learn.");

        REPLACE.setCategory(CommandHelp.CommandCategory.MOVES)
                .addShortDescription("Replace a learned move with another move!")
                .addAliases()
                .addArgs("index")
                .addArgDesc("index", "The move number you want to replace with the new move.");

        DUEL.setCategory(CommandHelp.CommandCategory.DUEL)
                .addShortDescription("Duel another player!")
                .addAliases()
                .addArgs("@player", "accept", "deny")
                .addArgDesc("@player", "Replace this with the mention of the user you want to duel.")
                .addArgDesc("accept", "If someone has requested to duel you, accept their challenge.")
                .addArgDesc("deny", "If someone has requested to due you, deny their challenge like a coward.");

        USE.setCategory(CommandHelp.CommandCategory.DUEL)
                .addShortDescription("Use a move during a duel!")
                .addAliases()
                .addArgs("index")
                .addArgDesc("index", "The number of the move you want to use (can be found by using the move command).");

        SHOP.setCategory(ECONOMY)
                .addShortDescription("Shop!")
                .addAliases("store")
                .addArgs("page")
                .addArgDesc("page", "The page you want to view. To see available pages, use this command without arguments.");

        BUY.setCategory(ECONOMY)
                .addShortDescription("Buy something from the shop!")
                .addAliases()
                .addArgs("item")
                .addArgs("natureName", "formName", "x", "y", "tmNum", "trNum")
                .addArgDesc("item", "The item type you want to buy. Item types are listed on the shop page (Ex. 'nature' or 'form')")
                .addArgDesc("natureName", "The name of the nature you want to replace your selected Pokemon's nature with.")
                .addArgDesc("formName", "The name of the form you want to transform your selected Pokemon into.")
                .addArgDesc("x", "If your selected Pokemon has an X evolution, type 'x' to buy it.")
                .addArgDesc("y", "If your selected Pokemon has a Y evolution, type 'y' to buy it.")
                .addArgDesc("tmName", "The name of the TM you want to give your selected Pokemon (between 1 and 100, including the letters 'TM').")
                .addArgDesc("trName", "The name of the TR you want to give your selected Pokemon (between 0 and 99, including the letters 'TR').");

        RELEASE.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addShortDescription("Release a caught Pokemon into the wild.")
                .addAliases()
                .addArgs("index", "confirm", "deny")
                .addArgDesc("index", "The number of the Pokemon you want to release.")
                .addArgDesc("confirm", "If you want to release a Pokemon, confirm the release of it.")
                .addArgDesc("deny", "If you want to release a Pokemon, deny the release of it.");

        REPORT.setCategory(MISC)
                .addShortDescription("Report a bug or problem, or suggest a feature or change!")
                .addAliases("suggest", "report")
                .addArgs("command")
                .addArgs("report")
                .addArgDesc("command", "The command you were using or the command you want to suggest something for")
                .addArgDesc("report", "Description of the problem or suggestion");

        TEACH.setCategory(CommandHelp.CommandCategory.MOVES)
                .addShortDescription("Teach your Pokemon a TM or TR!")
                .addAliases()
                .addArgs("tm", "tr")
                .addArgs("number")
                .addArgDesc("tm", "If you are teaching your selected Pokemon a TM.")
                .addArgDesc("tr", "If you are teaching your selected Pokemon a TR.")
                .addArgDesc("number", "The number of the TM or TR (TM between 1 and 100, TR between 0 and 99).");

        INVENTORY.setCategory(ECONOMY)
                .addShortDescription("View your inventory!")
                .addAliases("inv", "items", "tms", "trs");

        HELP.setCategory(MISC)
                .addShortDescription("View all possible commands and what they do!")
                .addAliases()
                .addArgs("command")
                .addArgDesc("command", "Optional. Specify the command you want help on.");

        TRADE.setCategory(MISC)
                .addShortDescription("Trade with other players!")
                .addAliases()
                .addArgs("@player", "accept", "deny", "confirm", "credits", "pokemon")
                .addArgs("add", "remove")
                .addArgs("creditAmount", "pokemonNumber")
                .addArgDesc("@player", "Mention the player you want to start a trade with.")
                .addArgDesc("accept", "Accept a trade request.")
                .addArgDesc("deny", "Deny a trade request.")
                .addArgDesc("confirm", "Confirm a trade. After both players confirm, the trade will be complete.")
                .addArgDesc("credits", "Alias: c. Offer credits to a trade.")
                .addArgDesc("pokemon", "Alias: p. Offer pokemon to a trade.")
                .addArgDesc("add", "Add credits or pokemon to a trade offer")
                .addArgDesc("remove", "Remove credits or pokemon from a trade offer")
                .addArgDesc("creditAmount", "Number of credits you offer")
                .addArgDesc("pokemonNumber", "Number of the Pokemon you want to offer");

        GIVE.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addShortDescription("Give your selected Pokemon an item!")
                .addAliases("giveitem")
                .addArgs("item")
                .addArgDesc("item", "The item to give to your selected pokemon.");

        MARKET.setCategory(ECONOMY)
                .addShortDescription("A market where you can buy and sell other players' pokemon!")
                .addAliases()
                .addArgs("list")
                .addArgs("number")
                .addArgs("price")
                .addArgDesc("list", "List a pokemon for sale on the market")
                .addArgDesc("number", "The number of the pokemon you want to sell")
                .addArgDesc("price", "The price you want to sell your pokemon for");

        EVOLVE.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addShortDescription("Evolve your selected Pokemon, if it meets the necessary requirements!");

        EQUIP.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addShortDescription("Equip a Z-Crystal to use in battle!")
                .addAliases()
                .addArgs("number")
                .addArgDesc("number", "The number of the Z-Crystal, shown in your inventory.");

        TEAM.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addAliases();

        MEGA.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addAliases();

        WILDDUEL.setCategory(CommandHelp.CommandCategory.DUEL)
                .addAliases("wild");

        REDEEM.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addAliases("r");

        TRAINERDUEL.setCategory(CommandHelp.CommandCategory.DUEL)
                .addAliases("fight", "trainer");

        GYMDUEL.setCategory(CommandHelp.CommandCategory.DUEL)
                .addAliases("challenge", "gym", "leader");

        ABILITYINFO.setCategory(CommandHelp.CommandCategory.MOVES)
                .addAliases("ai");

        ACTIVATE.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addAliases();

        POKEPASS.setCategory(MISC)
                .addAliases("pass", "pp", "bp");

        FAVORITES.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addAliases("fav");

        FORM.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addAliases();

        FLEE.setCategory(CommandHelp.CommandCategory.DUEL)
                .addAliases("concede", "surrender");

        NICKNAME.setCategory(CommandHelp.CommandCategory.POKEMON)
                .addAliases("nick");

        SETTINGS.setCategory(CONFIG)
                .addAliases("config");

        DEV.setCategory(MISC)
                .addAliases("run");
    }

    protected MessageReceivedEvent event;
    protected String[] msg;
    protected List<Member> mentions;

    protected User player;
    protected Guild server;

    protected ServerDataQuery serverData;
    protected PlayerDataQuery playerData;

    protected EmbedBuilder embed;
    protected Color color;

    private long timeI, timeF;

    public Command(MessageReceivedEvent event, String[] msg)
    {
        this.event = event;
        this.msg = msg;
        this.mentions = event.getMessage().getMentionedMembers();

        this.player = event.getAuthor();
        this.server = event.getGuild();

        this.serverData = new ServerDataQuery(this.server.getId());
        this.playerData = new PlayerDataQuery(this.player.getId());

        this.embed = new EmbedBuilder();
        this.color = null;

        this.timeI = System.currentTimeMillis();
    }

    public abstract Command runCommand() throws IOException;

    //Useful Methods for other Commands
    //TODO: Roll these out to all of the commands
    protected boolean isLength(int len)
    {
        return this.msg.length >= len;
    }

    protected boolean isNumeric(int index)
    {
        return this.msg[index].chars().allMatch(Character::isDigit);
    }

    protected boolean isPokemon(String pokemon)
    {
        return Global.POKEMON.contains(Global.normalCase(pokemon));
    }

    protected int getInt(int index)
    {
        return Integer.parseInt(this.msg[index]);
    }

    protected void sendMsg(String msg)
    {
        this.embed = null;
        this.event.getChannel().sendMessage(this.playerData.getMention() + ": " + msg).queue();
    }

    protected void sendInvalidCredits(int req)
    {
        this.sendMsg("Insufficient Credits! Needed: `" + req + "`, you have `" + this.playerData.getCredits() + "`!");
    }

    protected String getMultiWordContent(int start)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = start; i < this.msg.length; i++) sb.append(this.msg[i]).append(" ");

        return sb.toString().trim();
    }

    //Embed-Related

    protected void setAuthor()
    {
        List<String> professors = Arrays.asList("Pokecord", "Oak", "Juniper", "Elm", "Birch", "Rowan", "Sycamore", "Kukui", "Magnolia", "Sonia");
        this.embed.setAuthor("Professor " + professors.get(new Random().nextInt(professors.size())));
    }

    private void setColor()
    {
        this.embed.setColor(this.color == null ? this.getRandomColor() : this.color);
    }

    public MessageEmbed getResponseEmbed()
    {
        this.timeF = System.currentTimeMillis();
        LoggerHelper.time(this.getClass(), this.msg[0], this.timeI, this.timeF);

        this.setAuthor();
        this.setColor();
        return this.embed.build();
    }

    public boolean isNull()
    {
        return this.embed == null;
    }

    private Color getRandomColor()
    {
        Random r = new Random();
        return new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
    }
}
