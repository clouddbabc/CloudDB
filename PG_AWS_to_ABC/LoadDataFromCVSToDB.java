
import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;  

import java.text.ParseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Timestamp;


import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.math.BigDecimal;

import org.postgresql.util.PSQLException;

public class LoadDataFromCVSToDB {
     

    private static Connection connectDB() {
       Connection conn=null;
        
       String myDBurl="jdbc:postgresql://"+System.getenv("DBHOST")+":"+System.getenv("DBPORT")+"/"+System.getenv("DBNAME");
       Properties myDBprops = new Properties();
       myDBprops.setProperty("user",System.getenv("DBUSER"));
       myDBprops.setProperty("password", System.getenv("DBUSERPW"));
        
       try {
            conn = DriverManager.getConnection(myDBurl, myDBprops);
            if (conn != null) {
                Statement stmt = conn.createStatement(); 
                ResultSet rs = stmt.executeQuery("select version()");
                rs.next();
                System.out.println("connect to : " + rs.getString(1));
            } else {
                System.out.println("Failed to make connection!");
            }

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static void insertSQL(Connection conn, String fileName, int timeout) {
        String line = "";  
        String splitBy = ",";
        try {
            conn.setAutoCommit(false);
        
            String insertOrder = "INSERT INTO onlineretail " 
                + " (invoice, stockcode, description , quantity "
                + ", invoiceDate, price, customerid, country ) "
                + "VALUES(?,?,?,?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(insertOrder);

          
            Date date = null;

            Statement stmt = conn.createStatement();
            ResultSet rs = null;

            //parsing a CSV file into BufferedReader class constructor  
            BufferedReader br = new BufferedReader(new FileReader(fileName));  
            line = br.readLine(); 
            String[] columns = line.split(splitBy); // get the first row as column names
            int colNum = columns.length;
            int count = 0; 
            while ((line = br.readLine()) != null)   //returns a Boolean value  
            {  
                String[] values = line.split(splitBy);    // use comma as separator  
                if  (values.length != colNum) 
                    continue;  //mismatch? skip the iteration
                try {
                /* 
                 invoice char(8), 
                 stockcode varchar(18), 
                 description varchar(64), 
                 quantity int, 
                 invoiceDate timestamp, 
                 price numeric(10,2),
                 customerid int, 
                 country varchar(32);
                */
                    pstmt.setString(1, values[0]);
                    pstmt.setString(2, values[1]);
                    pstmt.setString(3, values[2]);
                    pstmt.setInt(4, Integer.parseInt(values[3]));
                    date = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(values[4]);
                    pstmt.setTimestamp(5, new Timestamp(date.getTime()));
                    pstmt.setBigDecimal(6, new BigDecimal(values[5]));
                    pstmt.setInt(7, Integer.parseInt(values[6]));
                    pstmt.setString(8, values[7]);
                    pstmt.execute();
                    conn.commit(); // commit on every insert
                    count++;                 
                }  catch (NumberFormatException | PSQLException ignored) 
                { 
                    //mismatch? skip the iteration
                }

                if (count % 100 == 0) {
                 rs = stmt.executeQuery("select count(*) from onlineretail");
                 rs.next();
                 System.out.println("nidm.onlineretail has " + rs.getString(1) + " rows");
                 Thread.sleep(timeout);  // timeout for n sec
                } else System.out.print(".");
            } 
        } catch (IOException | SQLException | ParseException | InterruptedException e)
        {
            System.out.println("error line: " + line); 
            e.printStackTrace();  
        }
    }

    public static void main(String[] args) { 
        if (args.length < 1 || args.length > 2)
            { System.out.println("1st arg filename(required); 2nd arg timeout(ms, optional)"); return ; } 

        String fileName = args[0];
        int timeout = 0; //ms
        if (args.length == 2) timeout = Integer.parseInt(args[1]); 

        Connection conn = connectDB(); 
        insertSQL(conn, fileName, timeout);
         
     } 
}     
 
