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
        File pokemonMaster = new File("pokemonMaster.xml");
        Document document = Jsoup.parse(pokemonMaster, "UTF-8", "");
        Elements allPokemon = document.getElementsByTag("species");

        Document pokemonXML = new Document("");
        Element pokemonRoot = pokemonXML.createElement("Pokemon");
        pokemonXML.appendChild(pokemonRoot);

        Document moveXML = new Document("");
        Element moveRoot = moveXML.createElement("Moves");
        moveXML.appendChild(moveRoot);

        System.out.println("allPokemon");
        for (int i = 0; i < allPokemon.size(); i++) {
            Element singlePoke = allPokemon.get(i);

            Element species = pokemonXML.createElement("species");
            species.attr("id", Integer.toString(i + 1));
            pokemonRoot.appendChild(species);

            Element name = pokemonXML.createElement("name");
            name.html(singlePoke.getElementsByTag("name").html());
            species.appendChild(name);

            Element stats = pokemonXML.createElement("stats");
            stats.html(singlePoke.getElementsByTag("stats").html());
            species.appendChild(stats);

            Element typeone = pokemonXML.createElement("typeone");
            typeone.html(singlePoke.getElementsByTag("typeone").html());
            species.appendChild(typeone);

            try {
                Element typetwo = pokemonXML.createElement("typetwo");
                typetwo.html(singlePoke.getElementsByTag("typetwo").html());
                species.appendChild(typetwo);
            } catch (Exception e) {
                System.out.println("The pokemon is monotype");
            }

            String url = "https://pokemongo.fandom.com/wiki/" + singlePoke.getElementsByTag("name").html();
            if (singlePoke.getElementsByTag("name").html().equals("Nidoran ♀")) {
                url = "https://pokemongo.fandom.com/wiki/Nidoran%E2%99%80";
            } else if(singlePoke.getElementsByTag("name").html().equals("Mr. Mime")){
                url = "https://pokemongo.fandom.com/wiki/Mr._Mime";
            } else if (singlePoke.getElementsByTag("name").html().equals("Nidoran ♂")) {
                url = "https://pokemongo.fandom.com/wiki/Nidoran%E2%99%82";
            }
            Document wikiArticle = Jsoup.connect(url).get();


            Elements attacks = wikiArticle.getElementsByClass("pogo-attack-item-title");
            
            for (int j = 0; j < attacks.size(); j++) {
                Element pokemonAttack = pokemonXML.createElement("attack");
                Element moveAttack = moveXML.createElement("attack");
                pokemonAttack.attr("id", attacks.get(j).firstElementChild().html()); //sets the attack name inside attack param
                moveAttack.attr("id", attacks.get(j).firstElementChild().html());

                System.out.println(attacks.get(j).firstElementChild().html());
                Document moveArticle;

                if(moveXML.getElementsByAttributeValue("id", attacks.get(j).firstElementChild().html()).size() < 1) {


                    if (!attacks.get(j).firstElementChild().html().equals("Psychic")) {
                        moveArticle = Jsoup.connect("https://pokemongo.fandom.com/wiki/" + attacks.get(j).firstElementChild().html()).get();
                    } else {
                        moveArticle = Jsoup.connect("https://pokemongo.fandom.com/wiki/Psychic_(Attack)").get();
                    }

                    try {
                    System.out.println(moveArticle.getElementsByAttributeValue("title", "Attacks").first().html());
                    pokemonAttack.html(moveArticle.getElementsByAttributeValue("title", "Attacks").first().html());

                    Elements damageElements = moveArticle.getElementsByAttributeValue("data-source", "damagep");
                    Element damage = pokemonXML.createElement("damage");
                    damage.html(damageElements.get(1).html());
                    moveAttack.appendChild(damage);

                    Elements cooldownElements = moveArticle.getElementsByAttributeValue("data-source", "cooldown");
                    Element cooldown = moveXML.createElement("cooldown");
                    cooldown.html(cooldownElements.get(1).html());
                    moveAttack.appendChild(cooldown);

                    Elements energyElements = moveArticle.getElementsByAttributeValue("data-source", "energyp");
                    Element energy = moveXML.createElement("energy");
                    energy.html(energyElements.get(1).html());
                    moveAttack.appendChild(energy);

                    Elements typeElements = moveArticle.getElementsByAttributeValueEnding("alt", "-type attack");
                    String fullString = typeElements.attr("alt");
                    fullString = fullString.replaceAll("-type attack", "");
                    Element type = moveXML.createElement("movetype");
                    type.html(fullString);
                    moveAttack.appendChild(type);
                    } catch (Exception e) {
                        System.out.println("this move is broken");
                    }
                    moveRoot.appendChild(moveAttack);
                }
                else {
                    System.out.println("Repeated move: " + attacks.get(j).firstElementChild().html());
                    pokemonAttack.html(pokemonXML.getElementsByAttributeValue("id", attacks.get(j).firstElementChild().html()).first().html());
                }

                species.appendChild(pokemonAttack);


            }

        }

        Document.OutputSettings docSetting = new Document.OutputSettings();
        docSetting.prettyPrint(true);
        docSetting.indentAmount(4);
        docSetting.outline(true);
        docSetting.syntax(Document.OutputSettings.Syntax.xml);
        pokemonXML = pokemonXML.outputSettings(docSetting);
        pokemonXML.parser();
        moveXML = moveXML.outputSettings(docSetting);
        moveXML.parser();
        BufferedWriter fw = new BufferedWriter(new FileWriter("pokemonXML.xml"));
        fw.write(pokemonXML.toString());
        BufferedWriter dw = new BufferedWriter(new FileWriter("moveXML.xml"));
        dw.write(moveXML.toString());
        fw.close();
        dw.close();

    }
    

}
