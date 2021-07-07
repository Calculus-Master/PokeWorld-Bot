package com.calculusmaster.pokecord.util.listener;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.commands.config.CommandSettings;
import com.calculusmaster.pokecord.commands.duel.*;
import com.calculusmaster.pokecord.commands.economy.*;
import com.calculusmaster.pokecord.commands.misc.*;
import com.calculusmaster.pokecord.commands.moves.*;
import com.calculusmaster.pokecord.commands.pokemon.*;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Listener extends ListenerAdapter
{
    private final Map<String, Long> cooldowns = new HashMap<>();
    int cooldown = 1; //Seconds

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        //Check if the message was sent by a bot, and skip the listener if true
        if(event.getAuthor().isBot()) return;

        //If a link is sent, skip the listener
        if(event.getMessage().getContentRaw().toLowerCase().startsWith("http")) return;

        //If any attachment is sent, skip the listener
        if(event.getMessage().getAttachments().size() > 0) return;

        User player = event.getAuthor();
        Guild server = event.getGuild();
        String[] msg = event.getMessage().getContentRaw().toLowerCase().trim().split("\\s+");
        ServerDataQuery serverQuery = new ServerDataQuery(server.getId());
        Command c;
        Random r = new Random(System.currentTimeMillis());

        //If bot is mentioned, send the server prefix
        if(event.getMessage().getMentionedMembers().stream().anyMatch(m -> m.getId().equals("718169293904281610"))) event.getChannel().sendMessage("<@" + player.getId() + ">: My prefix is `" + serverQuery.getPrefix() + "`!").queue();

        //If the message starts with the right prefix, continue, otherwise skip the listener
        if(msg[0].startsWith(serverQuery.getPrefix()))
        {
            //Check cooldown
            if(this.cooldowns.containsKey(player.getId()) && !msg[0].contains("catch") && !msg[0].contains("use"))
            {
                if(System.currentTimeMillis() - this.cooldowns.get(player.getId()) <= this.cooldown * 1000L)
                {
                    event.getMessage().getChannel().sendMessage("<@" + player.getId() + ">: You're sending commands too quickly!").queue();
                    return;
                }
                else this.cooldowns.put(player.getId(), System.currentTimeMillis());
            }
            else this.cooldowns.put(player.getId(), System.currentTimeMillis());

            LoggerHelper.info(Listener.class, "Parsing: " + Arrays.toString(msg));

            //Remove prefix from the message array, msg[0] is the raw command name
            msg[0] = msg[0].substring(serverQuery.getPrefix().length());

            //Check for a valid command, and if there is none reply with the invalid message
            if(Command.START.contains(msg[0]) || !PlayerDataQuery.isRegistered(player.getId()))
            {
                c = new CommandStart(event, msg).runCommand();
            }
            else if(Command.BALANCE.contains(msg[0]))
            {
                c = new CommandBalance(event, msg).runCommand();
            }
            else if(Command.SELECT.contains(msg[0]))
            {
                c = new CommandSelect(event, msg).runCommand();
            }
            else if(Command.DEX.contains(msg[0]))
            {
                c = new CommandDex(event, msg).runCommand();
            }
            else if(Command.INFO.contains(msg[0]))
            {
                c = new CommandInfo(event, msg).runCommand();
            }
            else if(Command.CATCH.contains(msg[0]))
            {
                c = new CommandCatch(event, msg).runCommand();
            }
            else if(Command.POKEMON.contains(msg[0]))
            {
                c = new CommandPokemon(event, msg).runCommand();
            }
            else if(Command.MOVES.contains(msg[0]))
            {
                c = new CommandMoves(event, msg).runCommand();
            }
            else if(Command.LEARN.contains(msg[0]))
            {
                c = new CommandLearn(event, msg).runCommand();
            }
            else if(Command.REPLACE.contains(msg[0]))
            {
                c = new CommandReplace(event, msg).runCommand();
            }
            else if(Command.MOVEINFO.contains(msg[0]))
            {
                c = new CommandMoveInfo(event, msg).runCommand();
            }
            else if(Command.DUEL.contains(msg[0]))
            {
                c = new CommandDuel(event, msg).runCommand();
            }
            else if(Command.USE.contains(msg[0]))
            {
                c = new CommandUse(event, msg).runCommand();
            }
            else if(Command.SHOP.contains(msg[0]))
            {
                c = new CommandShop(event, msg).runCommand();
            }
            else if(Command.BUY.contains(msg[0]))
            {
                c = new CommandBuy(event, msg).runCommand();
            }
            else if(Command.RELEASE.contains(msg[0]))
            {
                c = new CommandRelease(event, msg).runCommand();
            }
            else if(Command.REPORT.contains(msg[0]))
            {
                c = new CommandReport(event, msg).runCommand();
            }
            else if(Command.TEACH.contains(msg[0]))
            {
                c = new CommandTeach(event, msg).runCommand();
            }
            else if(Command.INVENTORY.contains(msg[0]))
            {
                c = new CommandInventory(event, msg).runCommand();
            }
            else if(Command.HELP.contains(msg[0]))
            {
                c = new CommandHelp(event, msg).runCommand();
            }
            else if(Command.TRADE.contains(msg[0]))
            {
                c = new CommandTrade(event, msg).runCommand();
            }
            else if(Command.GIVE.contains(msg[0]))
            {
                c = new CommandGive(event, msg).runCommand();
            }
            else if(Command.MARKET.contains(msg[0]))
            {
                c = new CommandMarket(event, msg).runCommand();
            }
            else if(Command.EVOLVE.contains(msg[0]))
            {
                c = new CommandEvolve(event, msg).runCommand();
            }
            else if(Command.EQUIP.contains(msg[0]))
            {
                c = new CommandEquip(event, msg).runCommand();
            }
            else if(Command.TEAM.contains(msg[0]))
            {
                c = new CommandTeam(event, msg).runCommand();
            }
            else if(Command.MEGA.contains(msg[0]))
            {
                c = new CommandMega(event, msg).runCommand();
            }
            else if(Command.WILDDUEL.contains(msg[0]))
            {
                c = new CommandWildDuel(event, msg).runCommand();
            }
            else if(Command.REDEEM.contains(msg[0]))
            {
                c = new CommandRedeem(event, msg).runCommand();
            }
            else if(Command.TRAINERDUEL.contains(msg[0]))
            {
                c = new CommandTrainerDuel(event, msg).runCommand();
            }
            else if(Command.GYMDUEL.contains(msg[0]))
            {
                c = new CommandGymDuel(event, msg).runCommand();
            }
            else if(Command.ABILITYINFO.contains(msg[0]))
            {
                c = new CommandAbilityInfo(event, msg).runCommand();
            }
            else if(Command.ACTIVATE.contains(msg[0]))
            {
                c = new CommandActivate(event, msg).runCommand();
            }
            else if(Command.POKEPASS.contains(msg[0]))
            {
                c = new CommandPokePass(event, msg).runCommand();
            }
            else if(Command.FAVORITES.contains(msg[0]))
            {
                c = new CommandFavorites(event, msg).runCommand();
            }
            else if(Command.FORM.contains(msg[0]))
            {
                c = new CommandForm(event, msg).runCommand();
            }
            else if(Command.FLEE.contains(msg[0]))
            {
                c = new CommandFlee(event, msg).runCommand();
            }
            else if(Command.NICKNAME.contains(msg[0]))
            {
                c = new CommandNickname(event, msg).runCommand();
            }
            else if(Command.SETTINGS.contains(msg[0]))
            {
                c = new CommandSettings(event, msg).runCommand();
            }
            else if(Command.DEV.contains(msg[0]))
            {
                c = new CommandDev(event, msg).runCommand();
            }
            else c = new CommandInvalid(event, msg).runCommand();

            if(!(c instanceof CommandInvalid) && r.nextInt(5000) < 1) redeemEvent(event);

            if(!c.isNull()) event.getChannel().sendMessageEmbeds(c.getResponseEmbed()).queue();
        }

        if(r.nextInt(10) <= 3) Listener.expEvent(event);
    }

    private static void redeemEvent(MessageReceivedEvent event)
    {
        PlayerDataQuery p = new PlayerDataQuery(event.getAuthor().getId());
        p.changeRedeems(1);

        event.getChannel().sendMessage(p.getMention() + ": You earned a Redeem!").queue();
    }

    private static void expEvent(MessageReceivedEvent event)
    {
        PlayerDataQuery data = new PlayerDataQuery(event.getAuthor().getId());
        String mention = "<@" + event.getAuthor().getId() + ">";
        Pokemon p = data.getSelectedPokemon();

        int initL = p.getLevel();

        p.addExp((int)(new Random().nextInt(200) * (1 + Math.random())));

        if(p.getLevel() != initL)
        {
            event.getChannel().sendMessage(mention + ": Your " + p.getName() + " is now Level " + p.getLevel() + "!").queue();
        }

        Pokemon.updateExperience(p);
    }
}
