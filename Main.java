package ampersand;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommand;

import java.util.ArrayList;

// if I try to compile this at the terminal, this is the command
// it doesn't recognize the discord stuff, though
// javac -classpath . *.java

public class Main
{
    public static PlayerCharacter getPC(MessageCreateEvent event, ArrayList<PlayerCharacter> party)
    {
        Server server = event.getServer().get();

        // checks to make sure it gets the server properly
        if(event.getServer().isEmpty())
            event.getChannel().sendMessage("uh oh, no server :(");

        // checks to make sure it gets the user properly
        if(event.getMessageAuthor().asUser().isEmpty())
            event.getChannel().sendMessage("uh oh, no user :(");

        // gets the first non-everyone role
        String pcName = event.getMessageAuthor().asUser().get().getRoles(server).get(1).getName();

        // makes new character reference to be returned
        PlayerCharacter currentPC = null;

        // enhanced for loop iterates through list of PCs
        for (PlayerCharacter character : party)
            if (pcName.equals(character.getName()))
                currentPC = character; // makes reference same as the character

        if (currentPC == null)
            currentPC = new PlayerCharacter("womp womp! character not found :(");

        return currentPC;
    }


    public static void main(String[] args)
    {
        // add the token to make things work obviously, but it's not gonna be on the github
        String token = "";

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        api.addListener(new MyListener()); // creates message listener

        SlashCommand command = SlashCommand.with("ping", "Checks the functionality of this command")
                .createGlobal(api)
                .join();

        SlashCommand commando = SlashCommand.with("pong", "Checks the functionality of this other command")
                .createGlobal(api)
                .join();

        // messing with Alexei through reactions
        api.addReactionAddListener(event -> {
            if(event.getEmoji().equalsEmoji("😭"))
            {
                System.out.println("reacting to a reaction, hooray!");
                event.getChannel().sendMessage("😭");
            }
        });

        Weapon.makeWeapons();
        Armor.makeArmor();

        ampersand.Spell.createAllSpells();

        // create your players, starting with Anashu
        PlayerCharacter fern = new PlayerCharacter(
                "Anashu", new int[] {13, 16, 14, 6, 13, 18}, 81, new String[] {"Sorcerer 11"},
                "insight, intimidation, persuasion, stealth", "draconic ancestry, shield",
                new String[]{"Common", "Elvish", "Draconic"}
        );

        // add first spell, acid splash, for testing purposes
        ((ampersand.Caster)fern.getClasses().get(10)).learnSpell("acidsplash");
        ((ampersand.Caster)fern.getClasses().get(10)).learnSpell("bladeward");

        PlayerCharacter olive = new PlayerCharacter(
                "Cassiopeia", new int[] {10, 12, 14, 16, 18, 8}, 38, new String[] {"Druid 5"},
                "arcana, history, nature, perception", "leather, shield, +1",
                new String[]{"Common", "Druidic", "Orc", "Celestial"}
        );

        PlayerCharacter mako = new PlayerCharacter(
                "3V-3172", new int[] {20, 14, 16, 9, 11, 12}, 60, new String[] {"Fighter 4", "Barbarian 1"},
                "acrobatics, athletics, insight, intimidation, stealth", "plate",
                new String[]{"Common", "Dwarvish"}
        );

        PlayerCharacter ben = new PlayerCharacter(
                "Algernon Wonwill", new int[] {6, 20, 14, 10, 18, 7}, 44, new String[] {"Ranger 4", "Rogue 1"},
                "athletics, insight, intimidation, perception, stealth, survival", "leather",
                new String[]{"Common", "Goblin", "Harengon", "Orc"}
        );

        ben.race = new ampersand.Harengon(ben);

        PlayerCharacter noah = new PlayerCharacter(
                "Micah", new int[] {8, 10, 10, 14, 16, 18}, 28, new String[] {"Warlock 5"},
                "arcana, deception, history, investigation, persuasion, religion", "leather",
                new String[]{"Common", "Dwarvish", "Elvish", "Goblin", "Draconic", "Abyssal", "Infernal"}
        );

//        PlayerCharacter fool = new PlayerCharacter(
//                "Abserd", new int[]{20, 20, 20, 20, 20, 20}, 2000000000, new String[]{"Artificer 1",
//                "Barbarian 1", "Bard 1", "Cleric 1", "Druid 1", "Fighter 1", "Monk 1", "Paladin 1", "Ranger 1",
//                "Rogue 1", "Sorcerer 1", "Warlock 1", "Wizard 1"},
//                "", "chain mail", new String[]{}
//        );

        PlayerCharacter sora = new PlayerCharacter(
                "Hedrick", new int[]{20, 14, 18, 11, 7, 12}, 148, new String[]{"Barbarian 13"},
                "acrobatics, athletics, intimidation, persuasion, survival", "none",
                new String[]{"Common", "Giant", "Dwarvish", "Orc"}
        );

//        PlayerCharacter mako = new PlayerCharacter(
//                "Hiti", new int[] {12, 14, 12, 15, 16, 12}, 84, "Druid 10",
//                "insight, medicine, perception, religion, survival", "leather"
//        );
//
//        PlayerCharacter noah = new PlayerCharacter(
//                "Urza", new int[] {9, 12, 12, 20, 12, 14}, 72, "Artificer 10",
//                "arcana, history, perception, persuasion", "chain mail"
//        );
//
//        PlayerCharacter ethan = new PlayerCharacter(
//                "Faer", new int[] {8, 14, 12, 11, 12, 18}, 65, "Bard 10",
//                "deception expertise, insight, medicine, performance, persuasion expertise, sleight of hand expertise, stealth expertise", "leather"
//        );
//
//        PlayerCharacter ben = new PlayerCharacter(
//                "Laedros", new int[] {17, 13, 17, 8, 13, 10}, 152, "Fighter 10",
//                "athletics, insight, perception, religion", "chain mail"
//        );
//
//        PlayerCharacter ollie = new PlayerCharacter(
//                "Andus", new int[] {12, 12, 14, 11, 14, 18}, 69, "Sorcerer 10",
//                "deception, persuasion, religion, stealth", "draconic ancestry"
//        );

//        // make Charles
//        int[] CharlesArr = {9, 18, 16, 10, 14, 12};
//        String CharlesSkills = "acrobatics, insight expertise, intimidation, perception expertise, performance, sleight of hand";
//        PlayerCharacter alexei = new PlayerCharacter("Charles", CharlesArr, "Rogue 1", CharlesSkills);


        // Print the invite url of your bot
        // System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }
}
