import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

	public static final String URL = "jdbc:oracle:thin:@fling.seas.upenn.edu:1521:cis";
	
	public static void main(String[] args) throws Exception {
		
		String username = args[0];
        String password = args[1];
        float thisUserLat;
        float thisUserLon;

    	try { 
    		try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

            /*String thisUserDataQuery = 
            	"SELECT lat, lon, personality " +
            	"FROM YELP_USER " +
            	"WHERE userid='"+thisUserID+"'";
            rs = st.executeQuery(thisUserDataQuery);
            rs.next();
            thisUserLat = rs.getFloat(1);
            thisUserLon = rs.getFloat(2);
            thisUserPersonality = rs.getInt(3);
            friendFinder(st, thisUserLat, thisUserLon, thisUserPersonality, thisUserCategory, partySize);
            businessFinder(st, thisUserLat, thisUserLon, thisUserCategory);*/
          
    	
	        File file = new File("Amazon.csv");
	        FileInputStream fis = new FileInputStream(file);
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        BufferedReader d= new BufferedReader(new InputStreamReader(bis));
	    	
	    	File writeFile = new File("AmazonCoords.csv");
			// if file doesnt exists, then create it
			if (!writeFile.exists()) {
				writeFile.createNewFile();
			}
			FileWriter fw = new FileWriter(writeFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			//Skip the headers
			String headers = d.readLine();
			bw.write(headers + ",Latitude,Longitude\n");
            Statement st = makeConnectionWithDatabase(args);
			
	        while (d.ready()) {
	        	ResultSet rs = null;
	        	String line = d.readLine();
	        	String[] splitLine = line.split(",");
	        	String zip = splitLine[splitLine.length - 2];
	        	if (zip.length() == 0) {
	        		bw.write(line + ",MISSING,MISSING\n");
	        		continue;
	        	}
	        	String zipQuery = "SELECT latitude, longitude FROM zcta WHERE zip = " + zip;
	        	System.out.println(zipQuery);
	        	rs = st.executeQuery(zipQuery);
	         	if (rs.next()) {
	                thisUserLat = rs.getFloat(1);
	                thisUserLon = rs.getFloat(2);
	        	
		 		    bw.write(line + "," + thisUserLat + "," + thisUserLon + "\n");
	        	}
				rs.close();
	        	//System.out.print(st1.execute(zipQuery));
	        }
			bw.close();
			System.out.println("Done");
	        //st1.close();
		
    	} catch (Exception e) {e.printStackTrace();}
	}
	
	public static Statement makeConnectionWithDatabase(String[] args) throws Exception
	{
		try {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection conn = null;

		// either pass command line arguments or give your username and password for oracle below
		//url if you use a local instance is likely to be something like what you see below
		//url for fling is jdbc:oracle:thin:@fling.seas.upenn.edu:1521:cis
		if(args.length>=2)
			conn = DriverManager.getConnection ("jdbc:oracle:thin:@fling.seas.upenn.edu:1521:cis",args[0], args[1]);
		else
			conn = DriverManager.getConnection ("jdbc:oracle:thin:@fling.seas.upenn.edu:1521:cis","username", "password");

		Statement st = conn.createStatement();
		System.out.println("SQL connection established...");
		return st;
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		System.out.println("SQL connection failed...");
		return null;
	}
}
