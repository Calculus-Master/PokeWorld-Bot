package com.calculusmaster.pokecord.commandslegacy.misc;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.commandslegacy.CommandLegacyInvalid;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.player.Settings;
import com.calculusmaster.pokecord.game.pokemon.sort.PokemonListOrderType;
import com.calculusmaster.pokecord.mongo.PlayerSettingsQuery;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.calculusmaster.pokecord.game.player.Settings.*;

public class CommandLegacySettings extends CommandLegacy
{
    public CommandLegacySettings(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, true);
    }

    @Override
    public CommandLegacy runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.ACCESS_SETTINGS)) return this.invalidMasteryLevel(Feature.ACCESS_SETTINGS);

        boolean isUserAdmin = Global.userHasAdmin(this.server, this.player);

        boolean client = this.msg.length >= 2 && (this.msg[1].equals("client") || this.msg[1].equals("c"));
        boolean server = this.msg.length >= 2 && (this.msg[1].equals("server") || this.msg[1].equals("s"));

        if(client)
        {
            if(this.msg.length == 2 || !Settings.isValid(this.msg[2]))
            {
                this.embed.setTitle("Client Settings");

                StringBuilder clientSettings = new StringBuilder();
                for(Settings s : Settings.values()) if(s.isClient()) clientSettings.append("`p!settings client ").append(s.getCommand()).append("` - ").append(s.getDesc()).append("\n");

                this.embed.setDescription(clientSettings.toString());
            }
            else
            {
                PlayerSettingsQuery settings = this.playerData.getSettings();

                if(CLIENT_DETAILED.matches(this.msg[2]))
                {
                    //Toggle if no input given
                    if(this.msg.length == 3)
                    {
                        boolean currentValue = settings.get(CLIENT_DETAILED, Boolean.class);

                        settings.update(CLIENT_DETAILED, !currentValue);

                        if(currentValue) this.response = "Disabled viewing of detailed information!";
                        else this.response = "Enabled viewing of detailed information!";
                    }
                    //Set value to specific input
                    else if(this.msg.length == 4)
                    {
                        if("true".contains(this.msg[3]) || "false".contains(this.msg[3]))
                        {
                            boolean newValue = "true".contains(this.msg[3]);

                            settings.update(CLIENT_DETAILED, newValue);

                            if(newValue) this.response = "Enabled viewing of detailed information!";
                            else this.response = "Disabled viewing of detailed information!";
                        }
                        else this.response = "Valid arguments: `true` or `false`!";
                    }
                }
                else if(CLIENT_CATCH_AUTO_INFO.matches(this.msg[2]))
                {
                    //Toggle if no input given
                    if(this.msg.length == 3)
                    {
                        boolean currentValue = settings.get(CLIENT_CATCH_AUTO_INFO, Boolean.class);

                        settings.update(CLIENT_CATCH_AUTO_INFO, !currentValue);

                        if(currentValue) this.response = "Disabled automatic info viewing after catch!";
                        else this.response = "Enabled automatic info viewing after catch!";
                    }
                    //Set value to specific input
                    else if(this.msg.length == 4)
                    {
                        if("true".contains(this.msg[3]) || "false".contains(this.msg[3]))
                        {
                            boolean newValue = "true".contains(this.msg[3]);

                            settings.update(CLIENT_CATCH_AUTO_INFO, newValue);

                            if(newValue) this.response = "Enabled automatic info viewing after catch!";
                            else this.response = "Disabled automatic info viewing after catch!";
                        }
                        else this.response = "Valid arguments: `true` or `false`!";
                    }
                }
                else if(CLIENT_DEFAULT_ORDER.matches(this.msg[2]))
                {
                    if(this.msg.length == 4)
                    {
                        PokemonListOrderType order = PokemonListOrderType.cast(this.msg[3]);

                        if(order == null) this.response = "Invalid Order!";
                        else
                        {
                            settings.update(CLIENT_DEFAULT_ORDER, order);

                            this.response = "Your Pokemon List will now be ordered by `" + order.toString().toLowerCase() + "`!";
                        }
                    }
                }
                else if(CLIENT_POKEMON_LIST_FIELDS.matches(this.msg[2]))
                {
                    //Toggle if no input given
                    if (this.msg.length == 3)
                    {
                        boolean currentValue = settings.get(CLIENT_POKEMON_LIST_FIELDS, Boolean.class);

                        settings.update(CLIENT_POKEMON_LIST_FIELDS, !currentValue);

                        if (currentValue) this.response = "Pokemon List view is now Text-based!";
                        else this.response = "Pokemon List view is now Field-based!";
                    }
                    //Set value to specific input
                    else if (this.msg.length == 4)
                    {
                        if ("true".contains(this.msg[3]) || "false".contains(this.msg[3]))
                        {
                            boolean newValue = "true".contains(this.msg[3]);

                            settings.update(CLIENT_POKEMON_LIST_FIELDS, newValue);

                            if (newValue) this.response = "Pokemon List view is now Field-based!";
                            else this.response = "Pokemon List view is now Text-based!";
                        }
                        else this.response = "Valid arguments: `true` or `false`!";
                    }
                }
                else this.embed.setDescription(CommandLegacyInvalid.getShort());
            }
        }
        else if(server)
        {
            if(this.msg.length == 2 || (isUserAdmin && !Settings.isValid(this.msg[2])))
            {
                this.embed.setTitle("Server Settings");

                StringBuilder serverSettings = new StringBuilder();
                for(Settings s : Settings.values()) if(s.isServer()) serverSettings.append("`p!settings server ").append(s.getCommand()).append("` - ").append(s.getDesc()).append("\n");

                this.embed.setDescription(serverSettings.toString());
                this.embed.setFooter("Only Administrators can edit these settings!");
            }
            else if(!isUserAdmin)
            {
                this.response = "You must be an Administrator to change server settings!";
            }
            else
            {
                if(SERVER_PREFIX.matches(this.msg[2]))
                {
                    //Reset prefix
                    if(this.msg.length == 3)
                    {
                        this.serverData.setPrefix("p!");

                        this.response = "Server prefix has been reset to `p!`";
                    }
                    //Change prefix
                    else if(this.msg.length == 4)
                    {
                        String oldPrefix = this.serverData.getPrefix();

                        this.serverData.setPrefix(this.msg[3]);

                        this.response = "Server prefix has been changed from `" + oldPrefix + "` to `" + this.msg[3] + "`";
                    }
                }
                else if(SERVER_SPAWNCHANNEL.matches(this.msg[2]))
                {
                    String channel = !this.event.getMessage().getMentions().getChannels().isEmpty() ? this.event.getMessage().getMentions().getChannels().get(0).getId() : this.event.getMessage().getChannel().getId();
                    TextChannel channelName = this.event.getGuild().getTextChannelById(channel);

                    if(channelName != null && this.serverData.getSpawnChannels().contains(channel))
                    {
                        this.serverData.removeSpawnChannel(channel);

                        this.response = channelName.getAsMention() + " will no longer have Pokemon spawns!";
                    }
                    else if(channelName != null)
                    {
                        this.serverData.addSpawnChannel(channel);

                        this.response = channelName.getAsMention() + " will now have Pokemon spawns!";
                    }
                }
                else if(SERVER_ZCRYSTAL_DUEL_EQUIP.matches(this.msg[2]))
                {
                    if(this.msg.length == 3 || (this.msg.length == 4 && !this.msg[3].equals("true") && !this.msg[3].equals("false")))
                    {
                        boolean currentValue = this.serverData.canEquipZCrystalDuel();

                        this.serverData.setEquipZCrystalDuel(!currentValue);

                        this.response = "Players can " + (this.serverData.canEquipZCrystalDuel() ? "now" : "no longer") + " equip Z Crystals while in a Duel!";
                    }
                    else if(this.msg.length == 4)
                    {
                        boolean newValue = this.msg[3].equals("true");

                        this.serverData.setEquipZCrystalDuel(newValue);

                        this.response = "Players can " + (newValue ? "now" : "no longer") + " equip Z Crystals while in a Duel!";
                    }
                }
                else if(SERVER_DYNAMAX.matches(this.msg[2]))
                {
                    if(this.msg.length == 3 || (this.msg.length == 4 && !this.msg[3].equals("true") && !this.msg[3].equals("false")))
                    {
                        boolean currentValue = this.serverData.isDynamaxEnabled();

                        this.serverData.setDynamaxEnabled(!currentValue);

                        this.response = "Players can " + (this.serverData.isDynamaxEnabled() ? "now" : "no longer") + " Dynamax their Pokemon in a Duel!";
                    }
                    else if(this.msg.length == 4)
                    {
                        boolean newValue = this.msg[3].equals("true");

                        this.serverData.setDynamaxEnabled(newValue);

                        this.response = "Players can " + (newValue ? "now" : "no longer") + " Dynamax their Pokemon in a Duel!";
                    }
                }
                else if(SERVER_ZMOVE.matches(this.msg[2]))
                {
                    if(this.msg.length == 3 || (this.msg.length == 4 && !this.msg[3].equals("true") && !this.msg[3].equals("false")))
                    {
                        boolean currentValue = this.serverData.areZMovesEnabled();

                        this.serverData.setZMovesEnabled(!currentValue);

                        this.response = "Players can " + (this.serverData.areZMovesEnabled() ? "now" : "no longer") + " use Z-Moves in a Duel!";
                    }
                    else if(this.msg.length == 4)
                    {
                        boolean newValue = this.msg[3].equals("true");

                        this.serverData.setZMovesEnabled(newValue);

                        this.response = "Players can " + (newValue ? "now" : "no longer") + " use Z-Moves in a Duel!";
                    }
                }
                else if(SERVER_DUELCHANNEL.matches(this.msg[2]))
                {
                    String channel = !this.event.getMessage().getMentions().getChannels().isEmpty() ? this.event.getMessage().getMentions().getChannels().get(0).getId() : this.event.getMessage().getChannel().getId();
                    TextChannel channelName = this.event.getGuild().getTextChannelById(channel);

                    if(channelName != null && this.serverData.getDuelChannels().contains(channel))
                    {
                        this.serverData.removeDuelChannel(channel);

                        this.response = this.serverData.getDuelChannels().isEmpty() ? "Duels are now allowed anywhere!" : "Duels are no longer allowed in " + channelName.getAsMention() + "!";
                    }
                    else if(channelName != null)
                    {
                        this.serverData.addDuelChannel(channel);

                        this.response = "Duels are now allowed in " + channelName.getAsMention() + "!";
                    }
                    else if(this.msg.length == 4 && this.msg[3].equals("reset"))
                    {
                        this.serverData.clearDuelChannels();

                        this.response = "Duels are now allowed anywhere!";
                    }
                }
                else if(SERVER_BOTCHANNEL.matches(this.msg[2]))
                {
                    String channel = !this.event.getMessage().getMentions().getChannels().isEmpty() ? this.event.getMessage().getMentions().getChannels().get(0).getId() : this.event.getMessage().getChannel().getId();
                    TextChannel channelName = this.event.getGuild().getTextChannelById(channel);

                    if(channelName != null && this.serverData.getBotChannels().contains(channel))
                    {
                        this.serverData.removeBotChannel(channel);

                        this.response = this.serverData.getDuelChannels().isEmpty() ? "Bot commands are now allowed anywhere!" : "Bot commands are no longer allowed in " + channelName.getAsMention() + "!";
                    }
                    else if(channelName != null)
                    {
                        this.serverData.addBotChannel(channel);

                        this.response = "Bot commands are now allowed in " + channelName.getAsMention() + "!";
                    }
                    else if(this.msg.length == 4 && this.msg[3].equals("reset"))
                    {
                        this.serverData.clearBotChannels();

                        this.response = "Bot commands are now allowed anywhere!";
                    }
                }
                else this.embed.setDescription(CommandLegacyInvalid.getShort());
            }
        }
        else
        {
            this.response = "Type `p!settings client` or `p!settings server` to view all available settings!";
        }

        return this;
    }
}
