package com.calculusmaster.pokecord.game.enums.elements;

public enum Region
{
    KANTO(1),
    JOHTO(2),
    SINNOH(3),
    HOENN(4),
    UNOVA(5),
    KALOS(6),
    ALOLA(7),
    GALAR(8),
    HISUI(8),
    PALDEA(9);

    private final int generation;

    Region(int generation)
    {
        this.generation = generation;
    }

    public int getGeneration()
    {
        return this.generation;
    }
}
