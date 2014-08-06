package core.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import com.gituhub.zxskelobrine.plugins.manager.api.central.plugins.variables.PluginInstance;

import core.BotListener;
import core.Launcher;
import core.managers.OperatorManager;

public class CommandProcessor {

    private static List<String> helpMessages = new ArrayList<String>();

    /**
     * This will take the command and issue itself to its private handlers with
     * an instance of the bot.
     * 
     * @param message
     *            - The message that was sent
     * @param nick
     *            - The person that sent it
     * @param bot
     *            - The bot.
     */
    public static void processCommand(String message, String nick, PircBotX bot) {
	// If it starts with [sc]core help
	if (message.toLowerCase().startsWith(
		BotListener.commandStartCharacter + "core help")) {
	    // Then call the help method.
	    help(nick, bot);
	}
	// If it starts with [sc]core list users
	if (message.toLowerCase().startsWith(
		BotListener.commandStartCharacter + "core list users")) {
	    // Then call the users method.
	    users(nick, bot);
	}
	// If it starts with [sc]core list plugins
	if (message.toLowerCase().startsWith(
		BotListener.commandStartCharacter + "core list plugins")) {
	    // Then call the plugins method.
	    plugins(nick, bot);
	}
	// If it starts with [sc]core terminate
	if (message.toLowerCase().startsWith(
		BotListener.commandStartCharacter + "core terminate")) {
	    // Then call the terminate method.
	    terminate(nick, bot);
	}
    }

    /**
     * This will terminate the bot by doing the following:
     * <ol>
     * <li>Send a message saying the bot was disabled and who disabled it</li>
     * <li>Stop the bot from reconnecting</li>
     * <li>Stop the program *DNW*</li>
     * </ol>
     * 
     * @param nick
     *            - The person who shut it down
     * @param bot
     *            - The bot.
     */
    private static void terminate(String nick, PircBotX bot) {
	// Send the message
	bot.sendIRC().message(Launcher.CHANNEL,
		nick + " has disabled VitCore. Shutting down.");
	// Stop the reconnects
	bot.stopBotReconnect();
	try {
	    // Stop the bot thread
	    Launcher.botThread.join();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	// Stop the program *DNW*
	System.exit(1);
    }

    /**
     * This will send a list of plugins and their description to the given user.
     * 
     * @param nick
     *            - The user to whom to send the list.
     * @param bot
     *            - The bot.
     */
    private static void plugins(String nick, PircBotX bot) {
	// Get a copy of the list of plugins
	List<PluginInstance> plugins = new CopyOnWriteArrayList<>(
		Launcher.plugins);
	// Then for every plugin
	for (PluginInstance instance : plugins) {
	    // Send the message with the plugins name and description.
	    bot.sendIRC().message(
		    nick,
		    "Plugin: "
			    + instance
				    .getJarFile()
				    .getName()
				    .substring(
					    0,
					    instance.getJarFile().getName()
						    .lastIndexOf("."))
			    + ": "
			    + instance.getFileContents().get(1)
				    .split("description:")[1].substring(1));
	}
    }

    /**
     * This will send a list of users and their uuids to the given user.
     * 
     * @param nick
     *            - The user to whom to send the list.
     * @param bot
     *            - The bot.
     */
    private static void users(String nick, PircBotX bot) {
	// Get a copy of the users list
	List<User> users = Launcher.botListener.getUserList();
	// And then for every user in that list
	for (User user : users) {
	    // Send their name and uuid if they are op if not just send their
	    // name.
	    bot.sendIRC()
		    .message(
			    nick,
			    Launcher.manager.hasOpAccount(BotListener
				    .getUserUUID(nick)) ? "User: "
				    + user.getNick() + " \tUUID: "
				    + user.getUserId().toString() : "User: "
				    + user.getNick());
	}
    }

    /**
     * This will send a list of commands and their help strings to the given
     * user.
     * 
     * @param nick
     *            - The user to whom to send the list.
     * @param bot
     *            - The bot.
     */
    private static void help(String nick, PircBotX bot) {
	// Create a copy of the help messaged
	List<String> helps = new CopyOnWriteArrayList<>(helpMessages);
	// For every help message
	for (String help : helps) {
	    // Send it to the user
	    bot.sendIRC().message(nick, help);
	}
    }

    /**
     * This will add the given help string to the list. If the list it is empty
     * it will add the core messages to it first.
     * 
     * @param help
     *            - The help string.
     */
    public static void addHelpString(String help) {
	if (helpMessages.size() == 0) {
	    helpMessages.add(Colors.BOLD + "VitCore IRC Plugin System Help: ");
	    helpMessages.add("");
	    helpMessages.add(Colors.UNDERLINE + "VitCore:");
	    helpMessages.add(BotListener.commandStartCharacter
		    + "core help: Displays this message.");
	    helpMessages.add(Colors.UNDERLINE + "Op Commands:");
	    helpMessages
		    .add(BotListener.commandStartCharacter
			    + "core set sc <Character>: Sets the command start character to <Character>.");// TODO
	    helpMessages.add(BotListener.commandStartCharacter
		    + "core list plugins: Lists all currently active plugins.");// TODO
	    helpMessages.add(BotListener.commandStartCharacter
		    + "core list users: Lists all online users with UUIDs.");// TODO
	    helpMessages.add(" ");
	}
	helpMessages.add(help
		.replace("%SC%", BotListener.commandStartCharacter));
    }

    /**
     * This will replace all of the old start character in the help messages
     * with the new character
     * 
     * @param oldStart
     *            - The old start character.
     * @param newStart
     *            - The new start character.
     */
    public static void updateHelpMessages(String oldStart, String newStart) {
	for (int i = 0; i < helpMessages.size(); i++) {
	    helpMessages
		    .set(i, helpMessages.get(i).replace(oldStart, newStart));
	}
    }

}
