package ampersand;

import java.util.Arrays;

public class StringMethods
{
    // removes &roll and spaces
    public static String removeCommand(String message)
    {
        if (message.contains("roll") || (message.contains("&save")) || message.contains("saving throw"))
        {
            if (message.contains("save") || message.contains("saving throw"))
                message += "save";
            message = message.substring(5);
        }
        else if (message.contains("cast"))
            message = message.substring(6);
        else if (message.contains("attack"))
        {
            message = message.substring(message.indexOf("k") + 1);
            message += "attack";
        }
        else if (message.contains("r"))
        {
            message = message.substring(2);
        }
        else
            message = message.substring(1);

        if (message.contains("disadv"))
        {
            String newMessage = message.substring(0, message.indexOf("adv") - 3);
            if (message.contains("advantage"))
                newMessage += message.substring(message.indexOf("adv") + 8);
            else
                newMessage += message.substring(message.indexOf("v"));
            message = newMessage;
            message += "disadv";
        }
        else if (message.contains("adv"))
        {
            String newMessage = message.substring(0, message.indexOf("adv"));
            if (message.contains("advantage"))
                newMessage += message.substring(message.indexOf("adv") + 8);
            else
                newMessage += message.substring(message.indexOf("v"));
            message = newMessage;
            message += "adv";
        }

        return message;
    }

    public static String removePunctuation(String message)
    {
        for (String s : Arrays.asList("'", ",", ":", "-", "/")) {
            message = message.replaceAll(s, "");
        }

        return message;
    }

    // removes all the spaces from a string
    public static String removeSpaces(String message)
    {
        while(message.contains(" "))
            message = message.substring(0, message.indexOf(" ")) + message.substring(message.indexOf(" ") + 1);

        return message;
    }

    //finds the next +/-
    public static int findNextSign(String command)
    {
        // sets default to the length of the string in case there's no signs
        int index = command.length();
        boolean startWithSign = false;

        if (command.charAt(0) == '+' || command.charAt(0) == '-')
        {
            command = command.substring(1);
            startWithSign = true;
        }

        if (command.contains("+"))
        {
            // set to first plus index
            index = command.indexOf("+");

            // if it also has a minus that comes before the plus, still dependent on other signs existing
            if (command.contains("-") && command.indexOf("-") < index)
                index = command.indexOf("-"); // set to first minus index

            if (startWithSign) // corrects for it in case it starts with a sign
                index += 1;
        }
        // else if it has a plus and other signs
        else if (command.contains("-"))
        {
            index = command.indexOf("-");
            if (startWithSign)
                index += 1;
        }

        return index;
    }

    // counts the number of +/-
    public static int countSigns(String str)
    {
        int count = 0;

        for (int i=0; i < str.length(); i++)
            if (str.charAt(i) == '+')
                count++;

        for (int i=0; i < str.length(); i++)
            if (str.charAt(i) == '-')
                count++;

        return count;
    }

    public static int getLevel(String str)
    {
        return Integer.parseInt(StringMethods.removeSpaces(str.substring(str.length() - 2)));
    }
}
