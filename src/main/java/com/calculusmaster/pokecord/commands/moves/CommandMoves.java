package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.extension.RaidDuel;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.stream.Collectors;

public class CommandMoves extends Command
{
    public CommandMoves(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.VIEW_MOVES)) return this.invalidMasteryLevel(Feature.VIEW_MOVES);

        boolean analysis = this.msg.length == 2 && Arrays.asList("analysis", "overview", "info", "help").contains(this.msg[1]);

        Pokemon selected = this.playerData.getSelectedPokemon();

        if(analysis)
        {
            this.embed.setDescription("Note: Not all moves that can be learned by your Pokemon are listed here, because moves that have not been implemented yet are excluded from this. Also, some moves listed here might not be able to be learned by your Pokemon right now due to your Pokemon not being high enough level. Use `p!moves` to see what moves your Pokemon can learn at its current level!");
            this.embed.setTitle("Move Info for " + selected.getName());

            List<Move> moves = selected.allMoves().stream().filter(Move::isImplemented).map(Move::new).toList();

            List<Move> topDamage = moves.stream().sorted(Comparator.comparingInt(Move::getPower)).collect(Collectors.toList());
            Collections.reverse(topDamage);

            StringBuilder damage = new StringBuilder();
            for(int i = 0; i < Math.min(5, topDamage.size()); i++) damage.append(topDamage.get(i).getName()).append(" - ").append(topDamage.get(i).getPower()).append("\n");
            damage.deleteCharAt(damage.length() - 1);

            Map<Category, List<Move>> categoryMoves = new HashMap<>();
            for(Category c : Category.values()) if(moves.stream().anyMatch(m -> m.getCategory().equals(c))) categoryMoves.put(c, moves.stream().filter(m -> m.getCategory().equals(c)).collect(Collectors.toList()));

            StringBuilder status = new StringBuilder();
            if(!categoryMoves.containsKey(Category.STATUS)) status.append("None");
            else for(Move m : categoryMoves.get(Category.STATUS)) status.append(m.getName()).append("\n");

            Map<Type, List<Move>> typeMoves = new HashMap<>();
            for(Type t : Type.values()) if(moves.stream().anyMatch(m -> m.getType().equals(t))) typeMoves.put(t, moves.stream().filter(m -> m.getType().equals(t)).collect(Collectors.toList()));

            StringBuilder type = new StringBuilder();
            for(Type t : typeMoves.keySet())
            {
                String mL = typeMoves.get(t).stream().map(Move::getName).collect(Collectors.toList()).toString();
                type.append(Global.normalize(t.toString())).append(" - ").append(mL, 1, mL.length() - 1).append("\n");
            }
            type.deleteCharAt(type.length() - 1);

            List<Move> priorityMoves = moves.stream().filter(m -> m.getPriority() != 0).sorted(Comparator.comparingInt(Move::getPriority)).collect(Collectors.toList());
            Collections.reverse(priorityMoves);

            StringBuilder priority = new StringBuilder();
            if(priorityMoves.isEmpty()) priority.append("None");
            else
            {
                for(Move m : priorityMoves) priority.append(m.getName()).append(" - ").append(m.getPriority()).append("\n");
                priority.deleteCharAt(priority.length() - 1);
            }

            List<Move> accuracyMoves = moves.stream().filter(m -> m.getAccuracy() < 100).sorted(Comparator.comparingInt(Move::getAccuracy)).collect(Collectors.toList());

            StringBuilder accuracy = new StringBuilder();
            if(accuracyMoves.isEmpty()) accuracy.append("None");
            else
            {
                for(Move m : accuracyMoves) accuracy.append(m.getName()).append(" - ").append(m.getAccuracy()).append("%\n");
                accuracy.deleteCharAt(accuracy.length() - 1);
            }

            this.embed.addField("Top 5 Highest Base Damage Moves", damage.toString(), true)
                    .addField("Status Moves", status.toString(), true)
                    .addField("Move Types", type.toString(), false)
                    .addField("Priority Moves", priority.toString(), true)
                    .addField("Low Accuracy Moves", accuracy.toString(), true);
        }
        else
        {
            boolean inDuel = DuelHelper.isInDuel(this.player.getId());
            StringBuilder movesList = new StringBuilder();

            if(inDuel)
            {
                Duel d = DuelHelper.instance(this.player.getId());
                int current = d.indexOf(this.player.getId());
                int other = d instanceof RaidDuel ? d.getPlayers().length - 1 : current == 0 ? 1 : 0;
                selected = d.getPlayers()[current].active;

                movesList.append("**Learned Moves: **\n");
                Move m;
                for(int i = 0; i < 4; i++)
                {
                    movesList.append(i + 1).append(": ");

                    m = selected.getMove(i);
                    if(selected.isDynamaxed()) m = DuelHelper.getMaxMove(selected, m);

                    movesList.append(m.getName()).append(" (").append(m.getEffectiveness(d.getPlayers()[other].active)).append(")");

                    movesList.append("\n");
                }
            }

            if(!inDuel)
            {
                movesList.append("**Learned Moves: **\n");
                for(int i = 0; i < 4; i++) movesList.append("Move ").append(i + 1).append(": ").append(selected.getMove(i).getName()).append("\n");

                movesList.append("\n**All Moves: **\n");
                String emote;
                for (String s : selected.allMoves())
                {
                    if(selected.availableMoves().contains(s) && Move.isImplemented(s)) emote = " ";
                    else if(!selected.availableMoves().contains(s) && Move.isImplemented(s)) emote = " :lock:";
                    else emote = " :no_entry_sign:";

                    movesList.append(s).append(emote).append("\n");
                }
            }

            this.embed.setDescription(movesList.toString());
            this.embed.setTitle("Level " + selected.getLevel() + " " + selected.getName());
            if(!inDuel) this.embed.setFooter(":lock: signifies moves that are either not unlocked or not implemented! Unlock them by leveling up this Pokemon!");
        }

        return this;
    }
}
