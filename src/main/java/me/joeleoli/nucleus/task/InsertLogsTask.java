package me.joeleoli.nucleus.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.log.CommandLog;
import me.joeleoli.nucleus.log.ConnectionLog;
import me.joeleoli.nucleus.log.LogQueue;
import me.joeleoli.nucleus.log.PrivateMessageLog;
import me.joeleoli.nucleus.log.PublicMessageLog;
import org.bson.Document;

public class InsertLogsTask implements Runnable {

	@Override
	public void run() {
		final List<Document> commandLogDocuments = new ArrayList<>();
		final List<Document> connectionLogDocuments = new ArrayList<>();
		final List<Document> privateMessageLogDocuments = new ArrayList<>();
		final List<Document> publicMessageLogDocuments = new ArrayList<>();
		final Iterator<CommandLog> commandLogIterator = LogQueue.getCommandLogs().iterator();
		final Iterator<ConnectionLog> connectionLogIterator = LogQueue.getConnectionLogs().iterator();
		final Iterator<PrivateMessageLog> privateMessageLogIterator = LogQueue.getPrivateMessageLogs().iterator();
		final Iterator<PublicMessageLog> publicMessageLogIterator = LogQueue.getPublicMessageLogs().iterator();

		while (commandLogIterator.hasNext()) {
			final CommandLog commandLog = commandLogIterator.next();
			final Document document = new Document();

			document.put("uuid", commandLog.getUuid().toString());
			document.put("command", commandLog.getCommand());
			document.put("timestamp", commandLog.getTimestamp());

			commandLogDocuments.add(document);
			commandLogIterator.remove();
		}

		while (connectionLogIterator.hasNext()) {
			final ConnectionLog connectionLog = connectionLogIterator.next();
			final Document document = new Document();

			document.put("uuid", connectionLog.getUuid().toString());
			document.put("ip_address", connectionLog.getIpAddress());
			document.put("result", connectionLog.getResult().name());
			document.put("result_reason", connectionLog.getResultReason());
			document.put("timestamp", connectionLog.getTimestamp());

			connectionLogDocuments.add(document);
			connectionLogIterator.remove();
		}

		while (privateMessageLogIterator.hasNext()) {
			final PrivateMessageLog privateMessageLog = privateMessageLogIterator.next();
			final Document document = new Document();

			document.put("sender_uuid", privateMessageLog.getSender().toString());
			document.put("receiver_uuid", privateMessageLog.getReceiver().toString());
			document.put("message", privateMessageLog.getMessage());
			document.put("timestamp", privateMessageLog.getTimestamp());

			privateMessageLogDocuments.add(document);
			privateMessageLogIterator.remove();
		}

		while (publicMessageLogIterator.hasNext()) {
			final PublicMessageLog globalMessageLog = publicMessageLogIterator.next();
			final Document document = new Document();

			document.put("sender_uuid", globalMessageLog.getSender().toString());
			document.put("message", globalMessageLog.getMessage());
			document.put("timestamp", globalMessageLog.getTimestamp());

			publicMessageLogDocuments.add(document);
			publicMessageLogIterator.remove();
		}

		if (!commandLogDocuments.isEmpty()) {
			Nucleus.getInstance().getNucleusMongo().insertCommandLogs(commandLogDocuments);
		}

		if (!connectionLogDocuments.isEmpty()) {
			Nucleus.getInstance().getNucleusMongo().insertConnectionLogs(connectionLogDocuments);
		}

		if (!privateMessageLogDocuments.isEmpty()) {
			Nucleus.getInstance().getNucleusMongo().insertPrivateMessageLogs(privateMessageLogDocuments);
		}

		if (!publicMessageLogDocuments.isEmpty()) {
			Nucleus.getInstance().getNucleusMongo().insertPublicMessageLogs(publicMessageLogDocuments);
		}
	}

}
