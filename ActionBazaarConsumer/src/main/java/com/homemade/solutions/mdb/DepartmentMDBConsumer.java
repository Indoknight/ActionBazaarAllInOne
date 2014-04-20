package com.homemade.solutions.mdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.sql.DataSource;

import com.homemade.solutions.db.model.Department;

@MessageDriven(name = "DepartmentMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/DepartmentQueue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") }
		//,messageListenerInterface = MessageListener.class
		)
public class DepartmentMDBConsumer implements MessageListener {
	
	private Connection connection;

	@Resource
	private MessageDrivenContext mdc;
	
	
	@Resource(lookup="java:jboss/datasources/OjdbcDS")
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

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void onMessage(Message message) {
		if (mdc != null) {

			System.out.println("Caller Principal :" + mdc.getCallerPrincipal());
			System.out.println("Rollback only :" + mdc.getRollbackOnly());
			// You get IllegalStateException when used with default Container Managed Transactions
			//System.out.println("User Transaction " + mdc.getUserTransaction());
		}
		try {
			System.out.println("Got a message \n");
			
			ObjectMessage objMsg = (ObjectMessage) message;
			
			Department dept = (Department) objMsg.getObject();
			
			processDepartmentRequest(dept);
			
			
			//mdc.setRollbackOnly();
			//cleanUp();

			
		} catch (SQLException se) {
			throw new EJBException(se);
		} catch(JMSException je){
			throw new EJBException(je);
		}
	}
	
	
	private void processDepartmentRequest(Department dept) throws SQLException{
		PreparedStatement ps = connection.prepareStatement("INSERT INTO DEPARTMENTS VALUES (?, ?, ?, ?)");
		ps.setLong(1, dept.getDepartmentId());
		ps.setString(2, dept.getDepartmentName());
		ps.setLong(3, dept.getManagerId());
		ps.setLong(4, dept.getLocationId());		
		ps.executeUpdate();		
	}
	

	@PreDestroy
	public void cleanUp() {
		System.out.println("Cleaning up************************");
		if(connection!=null){
			try{
				connection.close();
				connection = null;
			}catch(SQLException se){
				se.printStackTrace();
			}
		}
	}

}
