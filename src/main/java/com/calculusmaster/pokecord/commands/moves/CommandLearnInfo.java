package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class CommandLearnInfo extends Command
{
    public CommandLearnInfo(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length < 2) return this.invalid();

        String move = Global.normalize(this.getMultiWordContent(1));

        if(!Move.isMove(move)) this.response = "Invalid move name!";
        else
        {
            List<String> pokemon = PokemonData.POKEMON.stream().map(PokemonData::get).filter(d -> d.moves.containsKey(move)).map(d -> d.name).toList();

            this.embed
                    .setTitle("Pokemon Able to Learn " + move)
                    .setDescription(String.join("\n", pokemon))
                    .setFooter("These are all the Pokemon that can learn " + move + " through leveling up!");
        }

        return this;
    }
}
