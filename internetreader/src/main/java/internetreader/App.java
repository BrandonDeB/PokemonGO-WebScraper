package internetreader;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup; 
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.NodeList; 
import org.jsoup.select.Elements;

public class App 
{
    public static void main( String[] args ) throws IOException {
        File pokemonMaster = new File("pokemonMaster.xml");
        System.out.println("Running");
        Document document = Jsoup.parse(pokemonMaster, "UTF-8", "");
        Elements allPokemon = document.getElementsByTag("species");
        System.out.println("allPokemon");
        for (int i = 0; i < allPokemon.size(); i++) {
            Element singlePoke = allPokemon.get(i);
            System.out.println(singlePoke.getElementsByTag("name").html());
            String url = "https://pokemongo.fandom.com/wiki/" + singlePoke.getElementsByTag("name").html();
            System.out.println(url);
            Document wikiArticle = Jsoup.connect(url).get();
            Elements attacks = wikiArticle.getElementsByClass("pogo-attack-item-title");
            Elements damage = wikiArticle.getElementsByClass("pogo-attack-item-damage");
            System.out.println(attacks.size());
            for (int j = 0; j < attacks.size(); j++) {
                System.out.println(attacks.get(j).firstElementChild().html() + damage.get(j).firstElementChild().html());
            }
        }
    }
}
