import java.sql.*;
import java.util.Scanner;

public class Account {
    private Connection connection;
    private Scanner sc;
    public Account(Connection connection,Scanner sc){
        this.connection = connection;
        this.sc = sc;
    }

    // open the new account
    public void open_accouont(String email) throws SQLException{

        if(!exists_account(email)){
            sc.nextLine();
            System.out.print("Name-> ");
            String name = sc.nextLine();
            System.out.print("Initial Balance-> ");
            String balance = sc.next();
            System.out.print("Security Pin-> ");
            String securityPin = sc.next();
            Long accountNumber = generateAccountNumber();
            String query = "INSERT INTO account(account_number,full_name,email,balance,security_pin)" +
                    "VALUES(?,?,?,?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setLong(1,accountNumber);
                preparedStatement.setString(2,name);
                preparedStatement.setString(3,email);
                preparedStatement.setString(4,balance);
                preparedStatement.setString(5,securityPin);
                if (preparedStatement.executeUpdate()>0){
                    System.out.println("Account Open Successfully");
                    return;
                }
                System.out.println("Fialed to open account");
            }
        }
        throw new RuntimeException("Account already exixts");
    }

    //to get the account number
    public long getAccouontNumber(String email) throws SQLException{
        if(exists_account(email)){
            String query = "SELECT account_number FROM account WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1,email);
                ResultSet accout = preparedStatement.executeQuery();
                if (accout.next())
                    return accout.getLong("account_number");
                else
                    return 0;
            }
        }
        throw new RuntimeException("Account not exists for this email address");
    }
    //to generate the account number
    private long generateAccountNumber(){
        String query = "SELECT account_number FROM account order by account_number DESC LIMIT 1;";
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()){// it's not the first account
                return resultSet.getLong("account_number")+1;
            }else {//for the first account
                return 10000100;
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        throw new RuntimeException("fail to generate account number");
    }

    public boolean exists_account(String email) throws SQLException {
        String query = "SELECT email FROM account WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1,email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
            else {
                return false;
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }


}
