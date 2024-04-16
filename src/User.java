import java.sql.*;
import java.util.Scanner;
import java.util.logging.Handler;

public class User {
    private Scanner sc;
    private Connection connection;
    public User(Connection connection,Scanner sc) {
        this.connection = connection;
        this.sc = sc;
    }

    //Registeration
    public String register() throws SQLException {
        System.out.println("--Fill the details--");
        sc.nextLine();
        System.out.print("Full Name-> ");
        String name = sc.nextLine().toUpperCase();
        System.out.print("Email-> ");
        String email = sc.next();
        System.out.print("Password-> ");
        String password = sc.next();
        if(userExists(email)){//user exists
            System.out.println("User already exists for this email address");
            return null;
        }
        String query = "INSERT INTO user(full_name,email,password) VALUES(?,?,?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,password);
            if(preparedStatement.executeUpdate()>0){
                System.out.println(name+" REGISTER SUCCESSFULL");
                return email;
            }else {
                System.out.println("FAILL TO REGISTER");
                return null;
            }
        }catch (SQLException e){
            System.out.println("failled to register");
            System.out.println(e.getMessage());
        }
        return null;
    }

    //Login
    public String login() throws SQLException{
        sc.nextLine();
        System.out.print("Email-> ");
        String email = sc.next();
        System.out.print("Password-> ");
        String password = sc.next();
        String query = "SELECT email FROM user WHERE email = ? AND password = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,password);
            if (preparedStatement.executeQuery().next()){
                System.out.println("Login Successfull");
                return email;
            }else {
                return null;
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    private boolean userExists(String email) throws SQLException {
        String query = "SELECT email FROM user WHERE email = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1,email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return true;//condition for the exixting user
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return false;//NOT EXIXTS
    }
}
