package com.homemade.solutions.mdb;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.sql.DataSource;

//@ResourceAdapter("activemq-rar-5.8.0.rar")
@MessageDriven(name = "ShippingMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/ActionBazaarShippingQueue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") }
		//,messageListenerInterface = MessageListener.class
		)
public class ShippingMDBConsumer implements MessageListener {
	
	private Connection connection;

	@Resource
	private MessageDrivenContext mdc;
	
	
	@Resource(name="java:jboss/datasources/OjdbcDS")
	//@Resource
	private DataSource ds;

	@PostConstruct
	public void initialize() {
		System.out.println("Initializing************************");
		System.out.println("Message Driven Context: " + mdc);
		System.out.println("DataSource: " + ds);
		try{
			connection = ds.getConnection();
		}catch(SQLException se){
			se.printStackTrace();
		}
		
	}

	public void onMessage(Message message) {
		if (mdc != null) {

			System.out.println("Caller Principal :" + mdc.getCallerPrincipal());
			System.out.println("Rollback only :" + mdc.getRollbackOnly());
			// You get IllegalStateException when used with default Container Managed Transactions
			//System.out.println("User Transaction " + mdc.getUserTransaction());
		}
		try {
			TextMessage txtMsg = (TextMessage) message;

			System.out.println("Got a message \n" + txtMsg.getText());
		} catch (Throwable thr) {
			thr.printStackTrace();
		}
	}

	@PreDestroy
	public void cleanUp() {
		System.out.println("Cleaning up************************");
		try{
			connection.close();
			connection = null;
		}catch(SQLException se){
			se.printStackTrace();
		}
	}

}
