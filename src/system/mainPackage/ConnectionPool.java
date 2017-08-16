package system.mainPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;

/**
 * 
 * @author Liron
 * 
 * the Class {@link ConnectionPool} creates ONE connection pool instance object
 * 
 * the NUMBER_OF_CONNECTIONS refers to the number of connections each ConnctionPool instance
 * can create in this case just 5
 * 
 * then Creates 5 connection references to our DB (jdbc:derby://localhost:1527/MainCouponDB)
 * 
 * @method getConnection 		sends 1 connection reference from e connections pool
 * 								
 * @method returnConnection 	adds the connection back to the pool
 * 
 * @method closeAllConnections  closes all the connections and removes them from the pool
 * 
 */



public class ConnectionPool {

public static final int CONNECTIONS_COUNT  = 5;
	
	private static ConnectionPool instance = null;
	private HashSet<Connection> connections = new HashSet<Connection>();
	public static synchronized ConnectionPool getInstance() throws SQLException, ClassNotFoundException {
		if(instance == null) {
			instance = new ConnectionPool();
		}
		
		return instance;
	}
	
	
	private ConnectionPool() throws SQLException, ClassNotFoundException {
			Class.forName("org.apache.derby.jdbc.ClientDriver");
			System.out.println("driver -- loaded");
	
			//fills the the HashSet connections with Connections to the DB
			
	for(int i = 0; i < CONNECTIONS_COUNT; ++i) {
	Connection connection = DriverManager.getConnection("jdbc:derby://localhost:1527/CouponSystemDB");
	connections.add(connection);
			
		}
	}
	
	public Connection getConnection()  {
		Connection connection = null;
		
		//in case there is no connections in the pool the method 
		//will wait until a connection will be returned
		
			while(connection == null) {
				if(connections.size() == 0) {
					try {
						wait();
					} catch (InterruptedException e) {
						
					}
				}
				
				synchronized(this) {
					if(connections.size() > 0) {
						connection = connections.iterator().next();
						connections.remove(connection);
					}
				}
			}
			
			return connection;
		
		
	}
	
	public synchronized void returnConnection(Connection connection) {
		connections.add(connection);
		notify();
	}
	
	public synchronized void closeAllConnections() throws SQLException {
		boolean full = false;
		do{
		if (connections.size() == 5)
		{
		full = true;
		}
		}while(full == false);
		
		for(Connection connection : connections) {
			connection.close();
			connections.remove(connection);}
		}
	}


	

