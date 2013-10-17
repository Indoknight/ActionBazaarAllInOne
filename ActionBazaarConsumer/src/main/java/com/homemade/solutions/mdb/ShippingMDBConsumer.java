package com.homemade.solutions.mdb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.annotation.ejb.ResourceAdapter;


@ResourceAdapter("activemq-rar-5.8.0.rar")
@MessageDriven(
		name="ShippingMDB",
		activationConfig={
			@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
			@ActivationConfigProperty(propertyName="destination", propertyValue="hornetq/queue/ActionBazaarShippingQueue"),
			@ActivationConfigProperty(propertyName="acknowledgeMode", propertyValue="Auto-acknowledge")
		}	
)
public class ShippingMDBConsumer implements MessageListener {
	
	
	@PostConstruct
	public void initialize(){
		System.out.println("Initializing************************");
	}
	
	
	public void onMessage(Message message){
		try{
			TextMessage txtMsg = (TextMessage) message;
			
			System.out.println("Got a message \n"+ txtMsg.getText());
		}catch(Throwable thr){
			thr.printStackTrace();
		}
	}
	
	
	@PreDestroy
	public void cleanUp(){
		System.out.println("Cleaning up************************");
	}
	
}
