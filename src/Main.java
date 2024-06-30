import  java.util.Scanner;
import  java.sql.DriverManager;
import  java.sql.Connection;
import  java.sql.ResultSet;
import  java.sql.SQLException;
import  java.sql.Statement;

public class Main {

    private static String url = "jdbc:mysql://localhost:3306/hotel_db";

    private static String username = "root";

    private static String password = "password";

    public static void main(String[] args)throws SQLException, ClassNotFoundException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("Drivers Loaded Successfully!!!");
        }
        catch (ClassNotFoundException e)
        {
            e.getStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(url,username,password)){
            System.out.println("Inside try Block!!!");

            while(true)
            {
                System.out.println("Inside while Block!!!");
                System.out.println("*** Hotel Reservation System ***");
                System.out.println("1. Reserve Room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                Scanner sc = new Scanner(System.in);
                System.out.print("Enter your Choice : ");
                int choice = sc.nextInt();

                switch (choice){
                    case 1:
                        reserveRoom(connection, sc);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, sc);
                        break;
                    case 4:
                        updateReservation(connection, sc);
                        break;
                    case  5:
                        deleteReservation(connection, sc);
                        break;
                    case 0:
                        exiting();
                    default:
                        System.out.println("Enter a valid input");
                }
            }
        }
        catch (Exception e)
        {
            e.getStackTrace();
        }

    }
    private  static void reserveRoom(Connection connection, Scanner sc)
    {
        System.out.println("Enter Guest Name : ");
        String guest_name = sc.next();
        System.out.println("Enter Room Number : ");
        int room_number = sc.nextInt();
        System.out.println("Enter Contact Number : ");
        String contact_number = sc.next();


        try (Statement stmt = connection.createStatement()){
            String query = "insert into reservation(guest_name, room_number, contact_number)" +
                    "values( '" +guest_name+"', "+room_number+", '"+contact_number+"')";
            int affected_rows = stmt.executeUpdate(query);

            if(affected_rows > 0)
            {
                System.out.println("Reservation Successfull!!!");
            }
            else
            {
                System.out.println("Reservation unsuccessfull!!!");
            }

        }
        catch (SQLException e)
        {
            e.getStackTrace();
        }
    }
    private  static void viewReservation(Connection connection)
    {

        try{
            Statement stmt = connection.createStatement();

            String Query = "select * from reservation";
            ResultSet rs = stmt.executeQuery(Query);
            System.out.println("----------------------------------------------------------------------------------");
            System.out.println("Reservation_ID\t Guest_Name\t Room_Number\t Contact_Number\t Reservation_Date");
            System.out.println("----------------------------------------------------------------------------------");

            while(rs.next())
            {
                int reservID = rs.getInt("reservation_id");
                String name = rs.getString("guest_name");
                int roomno = rs.getInt("room_number");
                String contactNumber = rs.getString("contact_number");
                String reservDate = rs.getString("reservation_date");

                System.out.println("\t"+reservID+"\t\t\t\t"+name+"\t\t"+roomno+"\t\t\t"+contactNumber+"\t\t"+reservDate);
            }
        }
        catch (SQLException e)
        {
            e.getStackTrace();
        }
    }
    private static void getRoomNumber(Connection connection, Scanner sc)
    {

        try{
            System.out.println("Enter Reservation ID : ");
            int reservId = sc.nextInt();
            System.out.println("Enter Guest Name : ");
            String name = sc.next();
            String Query = "select * from reservation where reservation_id = " + reservId + " and guest_name = '" + name + "' ";
            try (Statement stmt = connection.createStatement()){
                ResultSet rs = stmt.executeQuery(Query);

                if (rs.next())
                {
                    int roomno = rs.getInt("room_number");

                    String cus_name = rs.getString("guest_name");

                    System.out.println("Room Number "+roomno+ "is reserved for "+ cus_name);
                }
                else
                {
                    System.out.println("No Registration for name "+name);
                }

            }

        }
        catch (SQLException e)
        {
            e.getStackTrace();
        }
    }
    private  static void updateReservation(Connection connection, Scanner sc)
    {
        try{
            System.out.println("Enter Reservation ID : ");
            int reservID = sc.nextInt();
            try (Statement stmt = connection.createStatement()){

                if(reservationExists(connection, reservID))
                {
                    System.out.println("Enter Guest Name : ");
                    String guest_name = sc.next();
                    System.out.println("Enter Room Number : ");
                    int room_number = sc.nextInt();
                    System.out.println("Enter Contact Number : ");
                    String contact_number = sc.next();

                    String Query = " Update reservation set guest_name = '"+guest_name+"', room_number = "+room_number+", contact_number = '"+contact_number+"' where reservation_id = "+reservID+" ";
                    int affectedrows = stmt.executeUpdate(Query);

                    if(affectedrows > 0)
                    {
                        System.out.println("Update Successfull!!!");
                    }
                }
                else
                {
                    System.out.println("Reservation Doesnot Exists for this ID ");
                }
            }
            catch (SQLException e)
            {
                e.getStackTrace();
            }
        }
        catch (Exception e)
        {
            e.getStackTrace();
        }
    }
    private static  void deleteReservation(Connection connection, Scanner sc)
    {
        try(Statement stmt = connection.createStatement()){
            System.out.println("Enter Room Reservation ID : ");
            int reservID = sc.nextInt();
            if(reservationExists(connection, reservID))
            {
                String Query = "DELETE from reservation where reservation_id = "+reservID+" ";
                int affectedRows = stmt.executeUpdate(Query);
                if (affectedRows > 0)
                {
                    System.out.println("Reservation Cancelled!!! ");
                }
            }
            else
            {
                System.out.println("No Reservation Found!!! ");
            }

        }
        catch (SQLException e)
        {
            e.getStackTrace();
        }
    }
    private static void exiting()
    {
        try {
            System.out.print("Exiting");
            for (int i = 0; i < 5; i++) {
                Thread.sleep(450);
                System.out.print(".");
            }
            System.exit(0);
        }
        catch (InterruptedException e)
        {
            e.getStackTrace();
        }
    }
    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            System.out.println("inside try of reservation Exists");
            Statement stmt = connection.createStatement();
            String Query = "select reservation_id from reservation where reservation_id = " + reservationId + " ";

            ResultSet rs = stmt.executeQuery(Query);
             return rs.next();

        } catch (SQLException e) {
            e.getStackTrace();
            return  false;
        }

    }
}
