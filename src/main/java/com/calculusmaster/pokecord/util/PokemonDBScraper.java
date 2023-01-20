package com.calculusmaster.pokecord.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.List;

public class PokemonDBScraper
{
    //https://oxylabs.io/blog/web-scraping-with-java

    public static void main(String[] args) throws IOException
    {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);

        List<String> sites = List.of("https://pokemondb.net/pokedex/kleavor", "https://pokemondb.net/pokedex/floragato", "https://pokemondb.net/pokedex/wyrdeer");

        sites.forEach(url -> {
            HtmlPage page;
            try { page = webClient.getPage(url); } catch (IOException e) { throw new IllegalStateException(e); }

            System.out.println(url.substring(url.lastIndexOf("/") + 1));

            //Vitals Table
            HtmlElement vitalsTable = page.getFirstByXPath("/html/body/main/div[2]/div[2]/div/div[1]/div[3]/div/div[2]/table");
            vitalsTable.getFirstElementChild().getChildElements().forEach(e -> System.out.println(e.getVisibleText()));

            webClient.close();
        });
    }
}
