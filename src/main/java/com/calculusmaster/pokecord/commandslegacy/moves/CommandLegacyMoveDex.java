package com.calculusmaster.pokecord.commandslegacy.moves;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CommandLegacyMoveDex extends CommandLegacy
{
    public CommandLegacyMoveDex(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        boolean learnInfo = this.msg.length > 2 && this.msg[1].equals("move");
        boolean moveDex = this.msg.length > 2 && (this.msg[1].equals("pokemon") || this.msg[1].equals("dex"));

        if(learnInfo)
        {
            String move = Global.normalize(this.getMultiWordContent(2));
            MoveEntity moveEntity = MoveEntity.cast(move);

            if(!Move.isMove(move)) this.response = "Invalid move name!";
            else
            {
                List<String> pokemon = Arrays.stream(PokemonEntity.values()).map(PokemonEntity::data).filter(d -> d.getLevelUpMoves().containsKey(moveEntity)).map(PokemonData::getName).toList();

                this.embed
                        .setTitle("Pokemon Able to Learn " + move)
                        .setDescription(String.join("\n", pokemon))
                        .setFooter("These are all the Pokemon that can learn " + move + " through leveling up!");
            }
        }
        else if(moveDex)
        {
            String pokemon = Global.normalize(this.getMultiWordContent(2));
            PokemonEntity pokemonEntity = PokemonEntity.cast(pokemon);

            if(pokemonEntity == null) this.response = "Invalid Pokemon name!";
            else
            {
                Map<MoveEntity, Integer> moveData = pokemonEntity.data().getLevelUpMoves();

                List<String> moveDexInfo = new ArrayList<>();
                moveData.forEach((move, level) -> moveDexInfo.add("**%s** | Level %s".formatted(move.data().getName(), level)));

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
