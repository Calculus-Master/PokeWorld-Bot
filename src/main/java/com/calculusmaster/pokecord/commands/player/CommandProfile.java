package com.calculusmaster.pokecord.commands.player;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.mongo.PlayerStatisticsQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.format.DateTimeFormatter;

public class CommandProfile extends Command
{
    public CommandProfile(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.VIEW_PROFILE)) return this.invalidMasteryLevel(Feature.VIEW_PROFILE);

        String targetID = this.player.getId();
        if(this.mentions.size() > 0 && PlayerDataQuery.isRegistered(this.mentions.get(0).getId())) targetID = this.mentions.get(0).getId();

        Member player = this.getMember(targetID);
        PlayerDataQuery data = targetID.equals(this.player.getId()) ? this.playerData : new PlayerDataQuery(targetID);
        PlayerStatisticsQuery stats = data.getStats();

        this.embed
                .addField("Level", "`" + data.getLevel() + "`", true)
                .addField("Joined Discord", player.getUser().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                .addField("Joined Server", player.getTimeJoined().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                .addField("Pokemon Collected", "`" + data.getPokemonList().size() + "`", true)
                .addField("Pokemon Caught", "`" + stats.get(PlayerStatistic.POKEMON_CAUGHT) + "`", true)
                .addField("Duel Wins", "PvP: " + stats.get(PlayerStatistic.PVP_DUELS_WON) + "\nWild: " + stats.get(PlayerStatistic.WILD_DUELS_WON) + "\nTrainer: " + stats.get(PlayerStatistic.TRAINER_DUELS_WON) + "\nElite: " + stats.get(PlayerStatistic.ELITE_TRAINER_DUELS_WON), true)
                .addField("Credits", "Earned: " + stats.get(PlayerStatistic.CREDITS_EARNED) + "\nSpent: " + stats.get(PlayerStatistic.CREDITS_SPENT) + "\nBalance: " + data.getCredits(), true)
                .addField("Redeems", "Earned: " + stats.get(PlayerStatistic.NATURAL_REDEEMS_EARNED) + "\nBalance: " + data.getRedeems(), true)
                .addField("Market", "Bought: " + stats.get(PlayerStatistic.POKEMON_BOUGHT_MARKET) + "\nSold: " + stats.get(PlayerStatistic.POKEMON_SOLD_MARKET), true)
                .addField("Trades Completed", "`" + stats.get(PlayerStatistic.TRADES_COMPLETED) + "`", true)
                .addField("Shop Purchases", "`" + stats.get(PlayerStatistic.SHOP_ITEMS_BOUGHT) + "`", true)
                .addField("Bounties Completed", "`" + stats.get(PlayerStatistic.BOUNTIES_COMPLETED) + "`", true);

        this.embed.setTitle(player.getUser().getName() + "'s Profile (ID: " + player.getId() + ")");
        this.embed.setThumbnail(player.getUser().getEffectiveAvatarUrl());

        if(targetID.equals(this.player.getId())) this.embed.setDescription("This is your profile! To see your pokemon, type `p!pokemon`. To see your balance, type `p!balance`. To see your items, TMs, and TRs, type `p!inventory`. To view the market, type `p!market`. To start a duel, type `p!duel @player`.");
        return this;
    }
}
