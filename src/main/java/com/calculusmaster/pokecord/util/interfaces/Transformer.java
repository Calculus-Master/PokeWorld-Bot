package com.calculusmaster.pokecord.util.interfaces;

public interface Transformer<INPUT, OUTPUT>
{
    OUTPUT transform(INPUT f);
}
