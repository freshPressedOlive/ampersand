package ampersand;

import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class MyListener implements MessageCreateListener
{
    public static boolean attackSent;

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        //testers
        if (event.getMessageContent().equalsIgnoreCase("hi")
                || event.getMessageContent().equalsIgnoreCase("hey")
                || event.getMessageContent().contains("hello")
                || event.getMessageContent().equalsIgnoreCase("heyo")
                || event.getMessageContent().equalsIgnoreCase("hiya")
                || event.getMessageContent().equalsIgnoreCase("hola")
                || event.getMessageContent().equalsIgnoreCase("greetings")
                || event.getMessageContent().equalsIgnoreCase("salutations")
                || event.getMessageContent().equalsIgnoreCase("yo")) {
            if (!event.getMessageAuthor().isBotUser())
            {
                event.getChannel().sendMessage("hello");
            }
        }
        if (event.getMessageContent().contains("szum") || event.getMessageContent().contains("Szum")) {
            event.getChannel().sendMessage("😭");
        }

        if (event.getMessageContent().contains("😭") || event.getMessageContent().contains(":SobFluent:") || event.getMessageContent().contains(":pinksob:")){
            if (!event.getMessageAuthor().isBotUser()){
                event.getChannel().sendMessage("😭");
            }
        }

        // print party
        if (event.getMessageContent().contains("&print party")){ //would move to printers, but that'd be more code
            for (PlayerCharacter character : PlayerCharacter.party) { //iterates through party array
                event.getChannel().sendMessage(character.sendName()); //sends character names
            }
        }

        if (event.getMessageContent().contains("&print all weapons"))
            for (Weapon thisWeapon : Weapon.allWeapons)
                event.getChannel().sendMessage(String.valueOf(thisWeapon));

        // print stats, mods, and other character features, add something for DM role asking whose features
        if (event.getMessageContent().contains("&print name"))
            event.getChannel().sendMessage(Printers.printName(event));
        else if (event.getMessageContent().contains("&print stats"))
            event.getChannel().sendMessage(Printers.printStats(event));
        else if (event.getMessageContent().contains("&print mods"))
            event.getChannel().sendMessage(Printers.printMods(event));
        else if (event.getMessageContent().contains("&print class") && !event.getMessageContent().contains("level"))
            event.getChannel().sendMessage(Printers.printClass(event));
        else if (event.getMessageContent().contains("&print class and level"))
            event.getChannel().sendMessage(Printers.printClassNLevel(event));
        else if (event.getMessageContent().contains("&print total level"))
            event.getChannel().sendMessage(Printers.printTotalLevel(event));
        else if (event.getMessageContent().contains("&print skills"))
            event.getChannel().sendMessage(Printers.printSkills(event));
        else if (event.getMessageContent().contains("&print weapon prof"))
            event.getChannel().sendMessage(Printers.printWeaponProfs(event));
        else if (event.getMessageContent().contains("&print AC")
                || event.getMessageContent().contains("&print armor class")
                || event.getMessageContent().contains("&print Armor Class"))
            event.getChannel().sendMessage(Printers.printArmorClass(event));
        else if (event.getMessageContent().contains("&print armor"))
            event.getChannel().sendMessage(Printers.printArmor(event));
        else if (event.getMessageContent().contains("&print hit dice"))
            event.getChannel().sendMessage(Printers.printHitDice(event));
        else if (event.getMessageContent().contains("&print health")
                || event.getMessageContent().contains("&print hp")
                || event.getMessageContent().contains("&print HP"))
        {
            if (event.getMessageContent().contains("max"))
                event.getChannel().sendMessage(Printers.printMaxHealth(event));
            else
                event.getChannel().sendMessage(Printers.printHealth(event));
        }
        else if (event.getMessageContent().contains("&print max spell slots"))
            event.getChannel().sendMessage(Printers.printMaxSpellSlots(event));
        else if (event.getMessageContent().contains("&print languages"))
            event.getChannel().sendMessage(Printers.printLanguages(event));
        else if (event.getMessageContent().contains("&print save prof"))
            event.getChannel().sendMessage(Printers.printSaveProfs(event));
        else if (event.getMessageContent().contains("&custom randchar") || event.getMessageContent().contains("&randchar custom"))
            event.getChannel().sendMessage(Dice.rollCustomCharacter());
        else if (event.getMessageContent().contains("&randchar"))
            event.getChannel().sendMessage(Dice.rollCharacter(false));
        else if (event.getMessageContent().contains("&r")
                || event.getMessageContent().contains("&save")
                || event.getMessageContent().contains("&attack"))
            event.getChannel().sendMessage(Dice.rollCheck(event));
        else if (event.getMessageContent().contains("&cast"))
        {
            // this one gets the character directly, not a printer
            // goal is to figure out how to get a non-PC character later
                // maybe in getPC?
            Character currentPC = Main.getPC(event, PlayerCharacter.party);
            event.getChannel().sendMessage(currentPC.castSpell(event.getMessageContent()));
        }
        else if (event.getMessageContent().contains("&"))
            event.getChannel().sendMessage(Dice.rollCheck(event));
    }

    /*public void onButtonInteraction(ButtonClickEvent event){
        if (event.getButtonInteraction().getIdAsString().equals("success"))
        {
            event.getButtonInteraction().getChannel().get().sendMessage("success button");
        }*/
}
