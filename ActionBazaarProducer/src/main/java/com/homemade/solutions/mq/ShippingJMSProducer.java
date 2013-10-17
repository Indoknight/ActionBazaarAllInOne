package com.homemade.solutions.mq;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ShippingJMSProducer {
final private String SHIPPING_QUEUE = "ActionBazaarShippingQueue";

	public static void main(String[] args) {

		try{
		ShippingJMSProducer sender = new ShippingJMSProducer();
		
		ShippingRequest request = new ShippingRequest();
		
		request.setItem("Canon EOS 70D DSLR");
		request.setOrderId(4529349);
		request.setAddress("12 Ants Valley, Columbus, Ohio");
		request.setBillingAddress(true);

		sender.sendMessage(request);
		
		System.out.println("Message successfully sent!");
		
		}catch(Throwable thr){
			thr.printStackTrace();
		}

	}

	private ConnectionFactory connectionFactory;

	private Destination destination;

	private void sendMessage(ShippingRequest request) throws JMSException, NamingException {
		
		
		Context ctx = new InitialContext();

		connectionFactory = (ConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
		
		Connection con = connectionFactory.createConnection();
		
		con.start();

		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		destination = session.createQueue(SHIPPING_QUEUE);

		MessageProducer producer = session.createProducer(destination);
		
		
		//ObjectMessage objMessage = session.createObjectMessage();
		
		
		TextMessage txtMsg = session.createTextMessage();
		
		txtMsg.setText("Good Evening Naren!");
		
		//objMessage.setObject(request);
		
		producer.send(txtMsg);
		
		
		producer.close();

		session.close();

		con.close();
	}
	
	
	

}



class ShippingRequest implements Serializable{
	
	private static final long serialVersionUID = -4742144588211757993L;
	
	private int orderId;
	
	private String item;
	
	private String address;
	
	private boolean billingAddress;

	/**
	 * @return the orderId
	 */
	public int getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the item
	 */
	public String getItem() {
		return item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(String item) {
		this.item = item;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the billingAddress
	 */
	public boolean isBillingAddress() {
		return billingAddress;
	}

	/**
	 * @param billingAddress the billingAddress to set
	 */
	public void setBillingAddress(boolean billingAddress) {
		this.billingAddress = billingAddress;
	}
	
	
}
