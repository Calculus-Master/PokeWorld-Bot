package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.commands.pokemon.CommandTeam;
import com.calculusmaster.pokecord.game.DuelHelper;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandUse extends Command
{
    public CommandUse(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 1 || !DuelHelper.isInDuel(this.player.getId()))
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        Duel d = DuelHelper.instance(this.player.getId());

        String mention = this.playerData.getMention() + ": ";

        if(d.hasPlayerSubmittedMove(this.player.getId()))
        {
            this.event.getChannel().sendMessage(mention + "You already used a move!").queue();
            this.embed = null;
            return this;
        }

        if(d.getPlayers()[d.indexOf(this.player.getId())].active.isFainted() && !this.msg[1].equals("swap") && !d.isComplete())
        {
            this.event.getChannel().sendMessage(mention + "Your pokemon has fainted! You have to swap to another!").queue();
            this.embed = null;
            return this;
        }

        //Swap
        if(this.msg.length == 3 && this.msg[1].equals("swap") && this.isNumeric(2) && this.getInt(2) > 0 && this.getInt(2) < CommandTeam.MAX_TEAM_SIZE + 1)
        {
            if(d.data(d.indexOf(this.player.getId()) == 0 ? 1 : 0).thousandWavesUsed)
            {
                this.event.getChannel().sendMessage(mention + "You are unable to swap out right now!").queue();
                this.embed = null;
                return this;
            }

            d.submitMove(this.player.getId(), this.getInt(2));
            this.event.getMessage().delete().queue();
        }

        //Z Move
        if(this.msg.length == 3 && this.msg[1].equals("z") && isNumeric(2) && this.getInt(2) > 0 && this.getInt(2) < 5)
        {
            if(this.playerData.getEquippedZCrystal() == null)
            {
                this.event.getChannel().sendMessage(mention + "You don't have an equipped Z-Crystal! Equip one with `p!equip`!").queue();
                this.embed = null;
                return this;
            }

            if(d.getPlayers()[d.indexOf(this.player.getId())].usedZMove)
            {
                this.event.getChannel().sendMessage(mention + "You have already used a Z-Move!").queue();
                this.embed = null;
                return this;
            }

            Pokemon s = d.getPlayers()[d.indexOf(this.player.getId())].active;
            Move move = new Move(s.getLearnedMoves().get(this.getInt(2) - 1));

            boolean valid = switch(ZCrystal.cast(this.playerData.getEquippedZCrystal()))
            {
                //Type-based
                case BUGINIUM_Z -> move.getType().equals(Type.BUG);
                case DARKINIUM_Z -> move.getType().equals(Type.DARK);
                case DRAGONIUM_Z -> move.getType().equals(Type.DRAGON);
                case ELECTRIUM_Z -> move.getType().equals(Type.ELECTRIC);
                case FAIRIUM_Z -> move.getType().equals(Type.FAIRY);
                case FIGHTINIUM_Z -> move.getType().equals(Type.FIGHTING);
                case FIRIUM_Z -> move.getType().equals(Type.FIRE);
                case FLYINIUM_Z -> move.getType().equals(Type.FLYING);
                case GHOSTIUM_Z -> move.getType().equals(Type.GHOST);
                case GRASSIUM_Z -> move.getType().equals(Type.GRASS);
                case GROUNDIUM_Z -> move.getType().equals(Type.GROUND);
                case ICIUM_Z -> move.getType().equals(Type.ICE);
                case NORMALIUM_Z -> move.getType().equals(Type.NORMAL);
                case POISONIUM_Z -> move.getType().equals(Type.POISON);
                case PSYCHIUM_Z -> move.getType().equals(Type.PSYCHIC);
                case ROCKIUM_Z -> move.getType().equals(Type.ROCK);
                case STEELIUM_Z -> move.getType().equals(Type.STEEL);
                case WATERIUM_Z -> move.getType().equals(Type.WATER);
                //Uniques
                case ALORAICHIUM_Z -> s.getName().equals("Alolan Raichu") && move.getName().equals("Thunderbolt");
                case DECIDIUM_Z -> s.getName().equals("Decidueye") && move.getName().equals("Spirit Shackle");
                case EEVIUM_Z -> s.getName().equals("Eevee") && move.getName().equals("Last Resort");
                case INCINIUM_Z -> s.getName().equals("Incineroar") && move.getName().equals("Darkest Lariat");
                case KOMMOIUM_Z -> s.getName().equals("Kommo-o") && move.getName().equals("Clanging Scales");
                case LUNALIUM_Z -> (s.getName().equals("Lunala") || s.getName().equals("Dawn Wings Necrozma")) && move.getName().equals("Moongeist Beam");
                case LYCANIUM_Z -> (s.getName().equals("Lycanroc") || s.getName().equals("Lycanroc Day") || s.getName().equals("Lycanroc Night")) && move.getName().equals("Stone Edge");
                case MARSHADIUM_Z -> s.getName().equals("Marshadow") && move.getName().equals("Spectral Thief");
                case MEWNIUM_Z -> s.getName().equals("Mew") && move.getName().equals("Psychic");
                case MIMIKIUM_Z -> s.getName().equals("Mimikyu") && move.getName().equals("Play Rough");
                case PIKANIUM_Z -> s.getName().equals("Pikachu") && move.getName().equals("Volt Tackle");
                case PIKASHUNIUM_Z -> s.getName().equals("Pikachu") && move.getName().equals("Thunderbolt");
                case PRIMARIUM_Z -> s.getName().equals("Primarina") && move.getName().equals("Sparkling Aria");
                case SNORLIUM_Z -> s.getName().equals("Snorlax") && move.getName().equals("Giga Impact");
                case SOLGANIUM_Z -> (s.getName().equals("Solgaleo") || s.getName().equals("Dusk Mane Necrozma")) && move.getName().equals("Sunsteel Strike");
                case TAPUNIUM_Z -> s.getName().contains("Tapu") && move.getName().equals("Nature's Madness");
                case ULTRANECROZIUM_Z -> s.getName().equals("Ultra Necrozma") && move.getName().equals("Photon Geyser");
            };

            if(move.getCategory().equals(Category.STATUS))
            {
                this.event.getChannel().sendMessage(mention + "Status Z-Moves are not implemented!").queue();
                this.embed = null;
                return this;
            }

            if(!valid)
            {
                this.event.getChannel().sendMessage(mention + this.playerData.getEquippedZCrystal() + " does not work on " + move.getName() + "!").queue();
                this.embed = null;
                return this;
            }

            d.submitMove(this.player.getId(), this.getInt(2), true);
            this.event.getMessage().delete().queue();
        }

        //Normal Move
        if(this.msg.length == 2 && this.isNumeric(1) && this.getInt(1) > 0 && this.getInt(1) < 5)
        {
            d.submitMove(this.player.getId(), this.getInt(1), false);
            this.event.getMessage().delete().queue();
        }

        d.checkReady();

        this.embed = null;
        return this;
    }
}
