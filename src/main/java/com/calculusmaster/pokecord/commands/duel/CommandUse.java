package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

public class CommandUse extends Command
{
    public CommandUse(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "use <movenumber>");
    }

    @Override
    public Command runCommand() throws IOException
    {
        if(!Duel.isInDuel(this.player.getId()) || !Duel.getInstance(this.player.getId()).getStatus().equals(Duel.DuelStatus.DUELING) || (this.msg.length != 2 && this.msg.length != 3))
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }
        else if(this.isNumeric(1) || (this.msg[1].equals("z") && this.isNumeric(2)))
        {
            Duel duel = Duel.getInstance(this.player.getId());
            System.out.println(duel.getTurnID());

            if(!duel.getTurnID().equals(this.player.getId()))
            {
                this.embed.setDescription("It's not your turn!");
                return this;
            }

            if(!duel.isComplete())
            {
                //Delete the p!use message sent
                this.event.getChannel().deleteMessageById(this.event.getMessageId()).queue();

                boolean zmove = this.msg[1].equals("z");
                int moveNum = zmove ? this.getInt(2) : this.getInt(1);

                if(moveNum <= 0 || moveNum > 4)
                {
                    this.event.getChannel().sendMessage(this.playerData.getMention() + ": Invalid Move Selection!").queue();
                    this.embed = null;
                    return this;
                }

                if(zmove && this.playerData.getEquippedZCrystal() == null)
                {
                    this.event.getChannel().sendMessage(this.playerData.getMention() + ": You don't have an equipped Z-Crystal! Equip one with `p!equip`!").queue();
                    this.embed = null;
                    return this;
                }

                if(zmove && duel.hasUsedZMove(duel.turn))
                {
                    this.event.getChannel().sendMessage(this.playerData.getMention() + ": You have already used a Z-Move!").queue();
                    this.embed = null;
                    return this;
                }

                if(zmove)
                {
                    Pokemon s = this.playerData.getSelectedPokemon();
                    Move move = new Move(s.getLearnedMoves().get(moveNum - 1));

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
                        this.event.getChannel().sendMessage(this.playerData.getMention() + ": Status Z-Moves are not implemented!").queue();
                        this.embed = null;
                        return this;
                    }

                    if(!valid)
                    {
                        this.event.getChannel().sendMessage(this.playerData.getMention() + ": " + this.playerData.getEquippedZCrystal() + " does not work on " + move.getName() + "!").queue();
                        this.embed = null;
                        return this;
                    }
                }

                String results = duel.doTurn(moveNum, zmove);
                if(duel.getTurnID().equals(this.player.getId())) duel.swapTurns();
                duel.sendGenericTurnEmbed(this.event, results);
                this.embed = null;
            }
            if(duel.isComplete())
            {
                duel.onWin();
                duel.giveWinExp();
                duel.giveWinCredits();
                duel.sendWinEmbed(this.event);
                Duel.remove(this.player.getId());
                this.embed = null;
            }
        }
        return this;
    }
}
