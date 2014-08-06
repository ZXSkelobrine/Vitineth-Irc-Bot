package core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.pircbotx.Colors;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.TLSCapHandler;
import org.pircbotx.exception.IrcException;

import com.gituhub.zxskelobrine.plugins.manager.api.central.logging.LoggingManager;
import com.gituhub.zxskelobrine.plugins.manager.api.central.plugins.utilities.PluginLoader;
import com.gituhub.zxskelobrine.plugins.manager.api.central.plugins.utilities.PluginManager;
import com.gituhub.zxskelobrine.plugins.manager.api.central.plugins.variables.PluginConfiguration;
import com.gituhub.zxskelobrine.plugins.manager.api.central.plugins.variables.PluginInstance;

import core.managers.OperatorManager;
import core.processors.CommandProcessor;

public class Launcher {

    /**
     * The folder that stores the plugin - relative to the jar location.
     * <ul>
     * <li>Public</li>
     * <li>Static</li>
     * <li>Final</li>
     * <li>String</li>
     * </ul>
     */
    public static final String PLUGIN_FOLDER = "Plugins\\IRC\\";

    /**
     * This is the bots name when it connects to the server.
     * <ul>
     * <li>Public</li>
     * <li>Static</li>
     * <li>Final</li>
     * <li>String</li>
     * </ul>
     */
    public static final String BOT_NAME = "VitCore";

    /**
     * This is the mask the bot uses to hide the ip address.
     * <ul>
     * <li>Public</li>
     * <li>Static</li>
     * <li>String</li>
     * </ul>
     */
    public static String BOT_MASK = "VitCore";

    /**
     * This is the hostname of the server the bot is connecting to.
     * <ul>
     * <li>Public</li>
     * <li>Static</li>
     * <li>String</li>
     * </ul>
     */
    public static String HOSTNAME;

    /**
     * This is the channel the bot will try to auto connect to.
     * <ul>
     * <li>Public</li>
     * <li>Static</li>
     * <li>String</li>
     * </ul>
     */
    public static String CHANNEL;

    /**
     * This is the OperatorManager that control the operators on the server.
     * <ul>
     * <li>Public</li>
     * <li>Static</li>
     * <li>OperatorManager</li>
     * </ul>
     */
    public static OperatorManager manager;

    /**
     * This is the main bot listener that will broadcast events to the plugins.
     * <ul>
     * <li>Public</li>
     * <li>Static</li>
     * <li>BotListener</li>
     * </ul>
     */
    public static BotListener botListener;

    /**
     * This is the list of loaded plugins.
     * <ul>
     * <li>Public</li>
     * <li>Static</li>
     * <li>List</li>
     * </ul>
     */
    public static List<PluginInstance> plugins;

    /**
     * This is the thread that the bot runs in.
     * <ul>
     * <li>Public</li>
     * <li>Static</li>
     * <li>Thread</li>
     * </ul>
     */
    public static Thread botThread;

    public static void main(String[] args) {
	// Set the Op manager for use all around the system.
	manager = new OperatorManager();
	// Set hostname to connect to
	HOSTNAME = args[0];
	// Set the channel to connect to
	CHANNEL = args[1];
	// Create the bot mask based of the plugins that are currently
	// installed.
	generateBotMask();
	// Create the bot listener.
	botListener = new BotListener();
	// Form the configuration of the bot.
	Configuration<PircBotX> configuration;
	if (args.length == 3) {
	    configuration = new Configuration.Builder<PircBotX>()
		    .setName(BOT_NAME)
		    .setLogin(BOT_MASK)
		    .setAutoNickChange(false)
		    .setCapEnabled(true)
		    .addCapHandler(
			    new TLSCapHandler(new UtilSSLSocketFactory()
				    .trustAllCertificates(), true))
		    .addListener(botListener).setServerHostname(HOSTNAME)
		    .addAutoJoinChannel(CHANNEL)
		    .setServerPassword("08348P!uiXJbeCHQ@hQj")
		    .buildConfiguration();
	} else {
	    configuration = new Configuration.Builder<PircBotX>()
		    .setName(BOT_NAME)
		    .setLogin(BOT_MASK)
		    .setAutoNickChange(false)
		    .setCapEnabled(true)
		    .addCapHandler(
			    new TLSCapHandler(new UtilSSLSocketFactory()
				    .trustAllCertificates(), true))
		    .addListener(botListener).setServerHostname(HOSTNAME)
		    .addAutoJoinChannel(CHANNEL).buildConfiguration();
	}
	// Create the bot off the configuration above.
	PircBotX bot = new PircBotX(configuration);
	// Start the bot in its own thread.
	enableBot(bot);
	// Set the bot in the bot listener.
	botListener.setBot(bot);
	// Then enable the plugins.
	enablePlugins(bot, HOSTNAME, CHANNEL, botListener, false);
    }

    private static void enablePlugins(PircBotX bot, String hostname,
	    String channel, BotListener botListener, boolean enableLogging) {
	// Enable logging if the param says to.
	if (enableLogging)
	    LoggingManager.enableLogging();
	else
	    LoggingManager.disableLogging();
	// Create a plugin config.
	PluginConfiguration configuration = new PluginConfiguration(0,
		PLUGIN_FOLDER, "pluginInit", new Object[] { bot, hostname,
			channel }, PircBotX.class, String.class, String.class,
		PluginManager.class);
	// Then create the plugin manager to control the system
	PluginManager manager = PluginManager
		.enablePluginSupport(configuration);
	// Then set the passed object (Again) so that the client can register
	// itself.
	configuration.setPassedObjects(new Object[] { bot, hostname, channel,
		manager });
	// Set the configuration (again) becuase of the update above.
	manager.setConfig(configuration);
	// Set the manager on the bot listener.
	botListener.setManager(manager);
	// Form the plugin loader.
	PluginLoader loader = manager.generatePluginLoader();
	// Then read all of the plugins into a List
	plugins = loader.readAllPlugins();
	// Register them
	loader.registerAllPlugins(plugins);
	try {
	    // And finally load in all of the help messages.
	    processHelp(plugins);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private static void processHelp(List<PluginInstance> plugins)
	    throws IOException {
	// For every plugin
	for (PluginInstance instance : plugins) {
	    // For every line from the configs contents
	    for (String line : instance.getFileContents()) {
		// If it starts with help:
		if (line.startsWith("help: ")) {
		    // Then removed the help at the start.
		    String help = line.split("help:")[1].substring(1);
		    // Finally add the help string to the processor.
		    CommandProcessor.addHelpString(formatHelpText(help));
		}
	    }
	}
    }

    /**
     * This will replace all formatting tags in the help text into the correct
     * formatting.
     * <ul>
     * <li>%UN% -> Underline</li>
     * <li>%BO% -> Bold</li>
     * <li>%IT% -> Italics (Reverse)</li>
     * </ul>
     * 
     * @param text
     *            - The text to format.
     * @return String - The formatted text.
     */
    private static String formatHelpText(String text) {
	return text.replace("%UN%", Colors.UNDERLINE)
		.replace("%BO%", Colors.BOLD).replace("%IT%", Colors.REVERSE);
    }

    /**
     * This will simply enable the bot in its own thread.
     * 
     * @param bot
     *            - The bot to enable.
     */
    private static void enableBot(final PircBotX bot) {
	// Set the bot thread
	botThread = new Thread() {
	    @Override
	    public void run() {
		try {
		    // Just start the bot.
		    bot.startBot();
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (IrcException e) {
		    e.printStackTrace();
		}
	    }
	};
	// Then start the thread.
	botThread.start();
    }

    /**
     * This will generate the ip mask based off the plugins in the folder.<br>
     * E.g.<br>
     * Folder contains:
     * <ul>
     * <li>Mail-0.1</li>
     * <li>Norway-1.3</li>
     * <li>Lyric-0.4</li>
     * </ul>
     * Ip mask becomes <b><code>~LMN</code></b>
     */
    private static void generateBotMask() {
	// Create a string builder to append all chars to.
	StringBuilder sb = new StringBuilder();
	// List all the files in the plugin folder.
	File[] files = new File(PLUGIN_FOLDER).listFiles();
	// If this list is not null
	if (files != null) {
	    // For every file in the list
	    for (File file : files) {
		// Get the file name
		String fileName = file.getName();
		// Get the extension from the end
		String extension = fileName
			.substring(fileName.lastIndexOf(".") + 1);
		// If it is a jar file.
		if (extension.equalsIgnoreCase("jar")) {
		    // Append the first character as an upper case letter.
		    sb.append(Character.toUpperCase(file.getName().charAt(0)));
		}
	    }
	}
	// Then set the bot mask to the produced string.
	BOT_MASK = "+" + sb.toString();
    }

}
