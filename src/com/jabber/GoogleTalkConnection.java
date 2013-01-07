package com.jabber;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;

public class GoogleTalkConnection extends XMPPConnection {
	static ConnectionConfiguration config = new ConnectionConfiguration(
			"talk.google.com", 443, "gmail.com");

	public GoogleTalkConnection() {
		super(config);
		// config.setSecurityMode(SecurityMode.required);
		config.setSecurityMode(SecurityMode.enabled);
		config.setSelfSignedCertificateEnabled(true);
		config.setSocketFactory(new DummySSLSocketFactory());

		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
	}
}
