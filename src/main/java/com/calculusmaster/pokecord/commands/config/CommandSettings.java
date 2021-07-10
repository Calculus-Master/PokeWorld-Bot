package com.calculusmaster.pokecord.commands.config;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.util.helpers.SettingsHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.calculusmaster.pokecord.util.helpers.SettingsHelper.Setting;
import static com.calculusmaster.pokecord.util.helpers.SettingsHelper.Setting.*;

public class CommandSettings extends Command
{
    public CommandSettings(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        this.server.retrieveMemberById(this.player.getId()).queue();
        boolean isUserAdmin = this.server.getMemberById(this.player.getId()).hasPermission(Permission.ADMINISTRATOR);

        boolean client = this.msg.length >= 2 && (this.msg[1].equals("client") || this.msg[1].equals("c"));
        boolean server = this.msg.length >= 2 && (this.msg[1].equals("server") || this.msg[1].equals("s"));

        if(client)
        {
            if(this.msg.length == 2 || !Setting.isValid(this.msg[2]))
            {
                this.embed.setTitle("Client Settings");

                StringBuilder clientSettings = new StringBuilder();
                for(Setting s : Setting.values()) if(s.isClient()) clientSettings.append("`p!settings client ").append(s.getCommand()).append("` - ").append(s.getDesc()).append("\n");

                this.embed.setDescription(clientSettings.toString());
            }
            else
            {
                SettingsHelper settings = this.playerData.getSettings();

                if(CLIENT_DETAILED.matches(this.msg[2]))
                {
                    //Toggle if no input given
                    if(this.msg.length == 3)
                    {
                        boolean currentValue = settings.getSettingBoolean(CLIENT_DETAILED);

                        settings.updateSettingBoolean(CLIENT_DETAILED, !currentValue);

                        if(currentValue) this.sendMsg("Disabled viewing of detailed information!");
                        else this.sendMsg("Enabled viewing of detailed information!");
                    }
                    //Set value to specific input
                    else if(this.msg.length == 4)
                    {
                        if("true".contains(this.msg[3]) || "false".contains(this.msg[3]))
                        {
                            boolean newValue = "true".contains(this.msg[3]);

                            settings.updateSettingBoolean(CLIENT_DETAILED, newValue);

                            if(newValue) this.sendMsg("Enabled viewing of detailed information!");
                            else this.sendMsg("Disabled viewing of detailed information!");
                        }
                        else this.sendMsg("Valid arguments: `true` or `false`!");
                    }
                }
                else this.embed.setDescription(CommandInvalid.getShort());
            }
        }
        else if(server)
        {
            if(this.msg.length == 2 || (isUserAdmin && !Setting.isValid(this.msg[2])))
            {
                this.embed.setTitle("Server Settings");

                StringBuilder serverSettings = new StringBuilder();
                for(Setting s : Setting.values()) if(s.isServer()) serverSettings.append("`p!settings server ").append(s.getCommand()).append("` - ").append(s.getDesc()).append("\n");

                this.embed.setDescription(serverSettings.toString());
                this.embed.setFooter("Only Administrators can edit these settings!");
            }
            else if(!isUserAdmin)
            {
                this.sendMsg("You must be an Administrator to change server settings!");
            }
            else
            {
                if(SERVER_PREFIX.matches(this.msg[2]))
                {
                    //Reset prefix
                    if(this.msg.length == 3)
                    {
                        this.serverData.setPrefix("p!");

                        this.sendMsg("Server prefix has been reset to `p!`");
                    }
                    //Change prefix
                    else if(this.msg.length == 4)
                    {
                        String oldPrefix = this.serverData.getPrefix();

                        this.serverData.setPrefix(this.msg[3]);

                        this.sendMsg("Server prefix has been changed from `" + oldPrefix + "` to `" + this.msg[3] + "`");
                    }
                }
                else if(SERVER_SPAWNCHANNEL.matches(this.msg[2]))
                {
                    String channel = this.event.getMessage().getMentionedChannels().size() > 0 ? this.event.getMessage().getMentionedChannels().get(0).getId() : this.event.getMessage().getChannel().getId();
                    TextChannel channelName = this.event.getGuild().getTextChannelById(channel);

                    if(channelName != null && this.serverData.getSpawnChannels().contains(channel))
                    {
                        this.serverData.removeSpawnChannel(channel);

                        this.sendMsg(channelName.getAsMention() + " will no longer have Pokemon spawns!");
                    }
                    else if(channelName != null)
                    {
                        this.serverData.addSpawnChannel(channel);

                        this.sendMsg(channelName.getAsMention() + " will now have Pokemon spawns!");
                    }
                }
                else if(SERVER_ZCRYSTAL_DUEL_EQUIP.matches(this.msg[2]))
                {
                    if(this.msg.length == 3 || (this.msg.length == 4 && !this.msg[3].equals("true") && !this.msg[3].equals("false")))
                    {
                        boolean currentValue = this.serverData.canEquipZCrystalDuel();

                        this.serverData.setEquipZCrystalDuel(!currentValue);

                        this.sendMsg("Players can " + (this.serverData.canEquipZCrystalDuel() ? "now" : "no longer") + " equip Z Crystals while in a Duel!");
                    }
                    else if(this.msg.length == 4)
                    {
                        boolean newValue = this.msg[3].equals("true");

                        this.serverData.setEquipZCrystalDuel(newValue);

                        this.sendMsg("Players can " + (newValue ? "now" : "no longer") + " equip Z Crystals while in a Duel!");
                    }
                }
                else this.embed.setDescription(CommandInvalid.getShort());
            }
        }
        else
        {
            this.sendMsg("Type `p!settings client` or `p!settings server` to view all available settings!");
        }

        return this;
    }
}
