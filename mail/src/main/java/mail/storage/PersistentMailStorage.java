package mail.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import mail.Mail;
import mail.system.MailItem;

public class PersistentMailStorage {

    private static final String PERSISTENT_MAIL_BASE = "IRC\\Bot Store\\Mail\\"
	    + Mail.hostname + "\\" + Mail.channel + "\\";
    private static FileOutputStream currentOutput;

    public static void storeMail(String nick, MailItem item) {
	try {
	    formFileOutputStream(nick);
	    String[] mail = item.generateMessage();
	    currentOutput.write((mail[0] + "\n").getBytes());
	    currentOutput.write((mail[1] + "\n").getBytes());
	    currentOutput.write((mail[2] + "\n").getBytes());
	    currentOutput.flush();
	    currentOutput.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private static void formFileOutputStream(String nick) throws IOException {
	File file = null;
	boolean append = true;
	file = new File(PERSISTENT_MAIL_BASE + nick + ".txt");
	if (!file.getParentFile().exists())
	    file.getParentFile().mkdirs();
	if (!file.exists()) {
	    file.createNewFile();
	}
	currentOutput = new FileOutputStream(file, append);
    }
}
