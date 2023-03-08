package internetreader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import org.jsoup.Jsoup; 
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.NodeList; 
import org.jsoup.select.Elements;


public class App 
{
    public static void main( String[] args ) throws IOException {
        System.out.println("Running Pokemon GO webscraper");
        System.out.println("The Porgram will now find each pokemon's move");
        File pokemonMaster = new File("internetreader\\pokemonMaster.xml");
        Document document = Jsoup.parse(pokemonMaster, "UTF-8", "");
        Elements allPokemon = document.getElementsByTag("species");

        Document output = new Document("");
        Element root = output.createElement("Pokemon");
        output.appendChild(root);

        System.out.println("allPokemon");
        for (int i = 0; i < allPokemon.size(); i++) {
            Element singlePoke = allPokemon.get(i);

            Element species = output.createElement("species");
            species.attr("id", Integer.toString(i + 1));
            root.appendChild(species);

            Element name = output.createElement("name");
            name.html(singlePoke.getElementsByTag("name").html());
            species.appendChild(name);

            Element stats = output.createElement("stats");
            stats.html(singlePoke.getElementsByTag("stats").html());
            species.appendChild(stats);

            Element typeone = output.createElement("typeone");
            typeone.html(singlePoke.getElementsByTag("typeone").html());
            species.appendChild(typeone);

            try {
                Element typetwo = output.createElement("typetwo");
                typetwo.html(singlePoke.getElementsByTag("typetwo").html());
                species.appendChild(typetwo);
            } catch (Exception e) {
                System.out.println("The pokemon is monotype");
            }

            String url = "https://pokemongo.fandom.com/wiki/" + singlePoke.getElementsByTag("name").html();
            Document wikiArticle = Jsoup.connect(url).get();


            Elements attacks = wikiArticle.getElementsByClass("pogo-attack-item-title");
            
            for (int j = 0; j < attacks.size(); j++) {
                Element attack = output.createElement("attack");
                attack.attr("name", attacks.get(j).firstElementChild().html());
                System.out.println(attacks.get(j).firstElementChild().html());
                Document moveArticle;
                if (!attacks.get(j).firstElementChild().html().equals("Psychic")) {
                    moveArticle = Jsoup.connect("https://pokemongo.fandom.com/wiki/" + attacks.get(j).firstElementChild().html()).get();
                } else {
                    moveArticle = Jsoup.connect("https://pokemongo.fandom.com/wiki/Psychic_(Attack)").get();
                }

                System.out.println(moveArticle.getElementsByAttributeValue("title", "Attacks").first().html());

                Elements damageElements = moveArticle.getElementsByAttributeValue("data-source", "damagep");
                Element damage = output.createElement("damage");
                damage.html(damageElements.get(1).html());
                attack.appendChild(damage);
                attack.attr("movespeed", moveArticle.getElementsByAttributeValue("title", "Attacks").first().html());

                Elements cooldownElements = moveArticle.getElementsByAttributeValue("data-source", "cooldown");
                Element cooldown = output.createElement("cooldown");
                cooldown.html(cooldownElements.get(1).html());
                attack.appendChild(cooldown);

                Elements energyElements = moveArticle.getElementsByAttributeValue("data-source", "energyp");
                Element energy = output.createElement("energy");
                energy.html(energyElements.get(1).html());
                attack.appendChild(energy);

                Elements typeElements = moveArticle.getElementsByAttributeValueEnding("alt", "-type attack");
                String fullString = typeElements.attr("alt");
                fullString = fullString.replaceAll("-type attack", "");
                Element type = output.createElement("movetype");
                type.html(fullString);
                attack.appendChild(type);

                species.appendChild(attack);

            }

        }

        Document.OutputSettings docSetting = new Document.OutputSettings();
        docSetting.prettyPrint(true);
        docSetting.indentAmount(4);
        docSetting.outline(true);
        docSetting.syntax(Document.OutputSettings.Syntax.xml);
        output = output.outputSettings(docSetting);
        output.parser();
        BufferedWriter fw = new BufferedWriter(new FileWriter("output.xml"));
        fw.write(output.toString());
        fw.close();

    }
    

}
