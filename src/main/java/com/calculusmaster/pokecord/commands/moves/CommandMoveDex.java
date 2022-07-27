package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandMoveDex extends Command
{
    public CommandMoveDex(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        boolean learnInfo = this.msg.length > 2 && this.msg[1].equals("move");
        boolean moveDex = this.msg.length > 2 && (this.msg[1].equals("pokemon") || this.msg[1].equals("dex"));

        if(learnInfo)
        {
            String move = Global.normalize(this.getMultiWordContent(2));

            if(!Move.isMove(move)) this.response = "Invalid move name!";
            else
            {
                List<String> pokemon = PokemonData.POKEMON.stream().map(PokemonData::get).filter(d -> d.moves.containsKey(move)).map(d -> d.name).toList();

                this.embed
                        .setTitle("Pokemon Able to Learn " + move)
                        .setDescription(String.join("\n", pokemon))
                        .setFooter("These are all the Pokemon that can learn " + move + " through leveling up!");
            }
        }
        else if(moveDex)
        {
            String pokemon = Global.normalize(this.getMultiWordContent(2));

            if(PokemonData.POKEMON.stream().noneMatch(s -> s.equalsIgnoreCase(pokemon))) this.response = "Invalid Pokemon name!";
            else
            {
                Map<String, Integer> moveData = PokemonData.get(pokemon).moves;

                List<String> moveDexInfo = new ArrayList<>();
                moveData.forEach((move, level) -> moveDexInfo.add("**%s** | Level %s".formatted(move, level)));

                this.embed
                        .setTitle(pokemon + " Moves")
                        .setDescription(String.join("\n", moveDexInfo))
                        .setFooter("These are all the Moves that " + pokemon + " can learn through leveling up!");
            }
        }
        else return this.invalid();

        return this;
    }
}
