package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.PokemonSkin;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandSkins extends Command
{
    public CommandSkins(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        boolean equip = this.msg.length == 3 && this.msg[1].equals("equip") && this.isNumeric(2);
        boolean unequip = this.msg.length == 3 && this.msg[1].equals("unequip") && this.isNumeric(2);
        boolean preview = this.msg.length >= 3 && this.msg[1].equals("preview");

        if(equip || unequip)
        {
            int index = this.getInt(2);

            if(index < 1 || index > this.playerData.getOwnedSkins().size())
            {
                this.sendMsg("Invalid index!");
                return this;
            }

            PokemonSkin s = this.playerData.getOwnedSkins().get(index - 1);

            if(equip)
            {
                if(this.playerData.getEquippedSkins().contains(s))
                {
                    this.sendMsg("You already have this skin equipped!");
                }
                else
                {
                    if(this.playerData.getEquippedSkins().stream().anyMatch(skin -> skin.pokemon.equals(s.pokemon)))
                    {
                        this.playerData.unequipSkin(s.pokemon);
                        this.sendMsg("Unequipped your current skin for " + s.pokemon + "!");
                    }

                    this.playerData.equipSkin(s);

                    this.sendMsg("Equipped `" + s.skinName + "`" + " for " + s.pokemon + "!");
                }
            }
            else if(unequip)
            {
                if(!this.playerData.getEquippedSkins().contains(s))
                {
                    this.sendMsg("You do not have this skin equipped!");
                }
                else
                {
                    this.playerData.unequipSkin(s);

                    this.sendMsg("Unequipped `" + s.skinName + "` (for " + s.pokemon + ")!");
                }
            }
        }
        else if(preview)
        {
            PokemonSkin target;

            if(this.isNumeric(2))
            {
                if(this.getInt(2) < 1 || this.getInt(2) > this.playerData.getOwnedSkins().size())
                {
                    this.sendMsg("Invalid index!");
                    return this;
                }
                else target = this.playerData.getOwnedSkins().get(this.getInt(2) - 1);
            }
            else
            {
                target = PokemonSkin.cast(this.getMultiWordContent(2));

                if(target == null)
                {
                    this.sendMsg("Invalid Skin Name!");
                    return this;
                }
            }

            this.embed.setTitle("Skin Preview")
                    .setDescription("Previewing: " + target.skinName + " for " + target.pokemon)
                    .setImage(target.URL);
        }
        else
        {
            StringBuilder ownedSkinList = new StringBuilder();
            for(int i = 0; i < this.playerData.getOwnedSkins().size(); i++) ownedSkinList.append(i + 1).append(": ").append(this.playerData.getOwnedSkins().get(i).skinName).append(" (For ").append(this.playerData.getOwnedSkins().get(i).pokemon).append(")\n");
            if(ownedSkinList.isEmpty()) ownedSkinList.append("No skins owned.");
            else ownedSkinList.deleteCharAt(ownedSkinList.length() - 1);

            StringBuilder equippedSkinList = new StringBuilder();
            for(PokemonSkin s : this.playerData.getEquippedSkins()) equippedSkinList.append(s.pokemon).append(": ").append(s.skinName).append("\n");
            if(equippedSkinList.isEmpty()) equippedSkinList.append("No skins equipped.");
            else equippedSkinList.deleteCharAt(equippedSkinList.length() - 1);

            this.embed.setTitle(this.player.getName() + "'s Pokemon Skin Inventory");
            this.embed.setDescription("This is a list of all the Skins you own and have equipped. To equip a skin, type `p!skins equip <number>`. To unequip a skin, type `p!skins unequip <number>`. To preview what a skin looks like, type `p!skins preview <number>`");
            this.embed.addField("Owned", ownedSkinList.toString(), false);
            this.embed.addField("Equipped", equippedSkinList.toString(), false);
        }

        return this;
    }
}
