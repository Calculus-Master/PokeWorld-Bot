package com.calculusmaster.pokecord.game.duel.restrictions.types;

import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestriction;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.moves.MoveData;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.List;

public class StatusMovesCountRestriction extends TeamRestriction
{
    private final int count;

    public StatusMovesCountRestriction(int count)
    {
        super("STATUS_MOVES_COUNT_RESTRICTION_" + count);
        this.count = count;
    }

    @Override
    public boolean validate(List<Pokemon> team)
    {
        return team.stream().allMatch(p -> p.getMoves().stream().map(MoveData::get).filter(m -> m.category.equals(Category.STATUS)).count() == this.count);
    }

    @Override
    public String getDescription()
    {
        return "All Pokemon on the team must have **" + (this.count == 0 ? "no " : "exactly " + this.count) + " Status Moves equipped**.";
    }
}
