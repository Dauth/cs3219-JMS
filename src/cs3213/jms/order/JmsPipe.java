package cs3213.jms.order;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Matric 1:
 * Name   1:
 * 
 * Matric 2:
 * Name   2:
 *
 * This file implements a pipe that transfer messages using JMS.
 */

public class JmsPipe implements IPipe {

	private QueueConnectionFactory qconFactory;
	private QueueConnection qcon;
	private QueueSession qsession;
	private QueueSender qsender;
	private Queue queue;
	private TextMessage msg;
	private QueueReceiver qreceiver;

	// your code here
	public JmsPipe(String factoryName, String queueName) throws JMSException, NamingException{
		InitialContext ic = getInitialContext();
		qconFactory = (QueueConnectionFactory) ic.lookup(factoryName);
		qcon = qconFactory.createQueueConnection();
		qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		queue = (Queue) ic.lookup(queueName);
		qsender = qsession.createSender(queue);
		//qreceiver = qsession.createReceiver(queue);
		msg = qsession.createTextMessage();
		qcon.start();
	}

	private static InitialContext getInitialContext()
			throws NamingException {
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		props.put(Context.PROVIDER_URL, "jnp://localhost:1099");
		props.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
		return new InitialContext(props);
	}

	@Override
	public void write(Order s){
		try {
			msg.setText(s.toString());
	        qsender.send(msg);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	};
	@Override
	public Order read(){
		Order order = null;
		try {
			if(qreceiver == null){
				qreceiver = qsession.createReceiver(queue);
			}
			TextMessage msg = (TextMessage) qreceiver.receive();
			return Order.fromString(msg.getText());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return order;

	};  


	@Override
	public void close(){
		try {
			qsender.close();
			qreceiver.close();
			qcon.close();
			qsession.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
