package com.jabber;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

/**
 * @author rustam
 * 
 */
public class AntBot extends Task {
	/**
	 * 
	 */
	private static Message botResponse;
	private Set<String> contacts;
	// private int contactCount = 0;
	private int wait = 10;
	private String contactList = "agzamovr@gmail.com";
	private Date start;
	private boolean stopProcess = false;
	private String resignContact;
	private String message;
	private String text;
	private Set<String> agreedContacts;
	private String login = "ecc.ant.bot@gmail.com";
	private String password = "dct[jhjij";

	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static void main(String[] args) {
		AntBot antBot = new AntBot();
		antBot.addText("yes/no");
		antBot.setMessage("crp2");
		antBot.execute();
		antBot.getProject().getProperty("");
	}

	/**
	 * @param messageText
	 * @param c
	 */
	public void sendIM(String messageText, Chat c) {

		try {
			botResponse = new Message();
			botResponse.setBody(messageText);
			c.sendMessage(botResponse);
		} catch (Exception e) {
			System.out.println("Error sending message.");
			e.printStackTrace();
		}
	}

	@Override
	public void execute() throws BuildException {
		final GoogleTalkConnection connection = new GoogleTalkConnection();
		// ConnectionConfiguration config = new ConnectionConfiguration(
		// "192.168.110.209", 5222);
		// config.setSecurityMode(SecurityMode.disabled);
		// System.out.println("server is: " + config.getHost());
		// final XMPPConnection connection = new XMPPConnection(config);

		try {
			contacts = new HashSet<String>();
			agreedContacts = new HashSet<String>();
			// Connect to the server
			connection.connect();
			// Log into the server
			connection.login(login, password);
			// Assume we've created an XMPPConnection name "connection".
			// See if you are authenticated
			Presence p = new Presence(Presence.Type.available);
			p.setStatus("Crazy bot");
			connection.sendPacket(p);
			connection.getChatManager().addChatListener(
					new ChatManagerListener() {
						public void chatCreated(Chat c, boolean l) {
							c.addMessageListener(new MessageListener() {
								public void processMessage(Chat c, Message m) {
									boolean isNumber = m.getBody().matches(
											"\\d+");
									if (isNumber)
										wait += Integer.parseInt(m.getBody());
									System.out.println(c.getParticipant()
											+ ":> " + m.getBody());
									stopProcess = m.getBody().equalsIgnoreCase(
											"no");
									if (stopProcess) {
										resignContact = c.getParticipant();

									} else if (isNumber)
										sendIM("Операция завершится через "
												+ (wait - Math.round((new Date()
														.getTime() - start
														.getTime()) / 1000))
												+ " сек.", c);
									else if (m.getBody().equalsIgnoreCase("ok"))
										agreedContacts.add(c.getParticipant());
								}
							});
						}
					});
			sendToAll(connection, String.format(message + text, wait));
			start = new Date();
			while (connection.isConnected()) {
				Thread.sleep(100);
				if (stopProcess) {
					sendToAll(connection, "Операция прервана по просьбе "
							+ resignContact);
					throw new BuildException("Operation has been canceled by "
							+ resignContact);
				} else if (new Date().getTime() - start.getTime() > wait * 1000
						|| agreedContacts.containsAll(contacts)) {
					System.out.println(String.format(
							"No rejection. Waited %d seconds.", Math
									.round((new Date().getTime() - start
											.getTime())) / 1000));
					break;
				}
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			connection.disconnect();
		}
	}

	private void sendToAll(XMPPConnection connection, String message) {
		for (String contact : contactList.split(",")) {
			Chat chat = connection.getChatManager().createChat(contact.trim(),
					null);
			System.out.println("send message to: " + chat.getParticipant());
			sendIM(message, chat);
			contacts.add(chat.getParticipant());
		}
	}

	public void setContactList(String contactList) {
		this.contactList = contactList;
	}

	public String getContactList() {
		return contactList;
	}

	public void setWait(int wait) {
		this.wait = wait;
	}

	public int getWait() {
		return wait;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void addText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}