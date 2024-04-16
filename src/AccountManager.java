import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    private Connection connection;
    private Scanner sc;
    Double current_balance;
    public AccountManager(Connection connection,Scanner sc){
        this.connection = connection;
        this.sc = sc;
    }

    //credit maney
    public void creditManey(long account_number) throws SQLException{
        sc.nextLine();
        System.out.print("Amount-> ");
        Double balance = sc.nextDouble();
        System.out.print("Pin-> ");
        String pin = sc.next();
        if (account_number!=0){//valid accounts
            String query = "SELECT balance FROM account WHERE account_number = ? AND security_pin = ?";
            try {
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1,account_number);
                preparedStatement.setString(2,pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){//valid pin
                    try {
                        String creditQuery = "UPDATE account SET balance = balance + ? WHERE account_number = ?";
                        PreparedStatement creditStatement = connection.prepareStatement(creditQuery);
                        creditStatement.setDouble(1,balance);
                        creditStatement.setLong(2,account_number);
                        int rowAffected = creditStatement.executeUpdate();
                        if (rowAffected>0){
                            connection.commit();
                            System.out.println(balance+" credit successfully");

                        }else {
                            connection.rollback();
                            System.out.println("failed to credit maney");

                        }

                    }catch (SQLException e){
                        System.out.println(e.getMessage());
                    }
                }else {
                    System.out.println("Invalid Pin");
                }
                connection.setAutoCommit(true);
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("invalid account");
        }
    }

    //debit maney

    public void debitManey(long account_number) throws SQLException{
        sc.nextLine();
        System.out.print("Amount-> ");
        Double amount = sc.nextDouble();
        System.out.print("Pin-> ");
        String pin = sc.next();
        if(account_number!=0) {
            try {
                String query = "SELECT balance FROM account WHERE account_number = ? AND security_pin = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setDouble(1, account_number);
                preparedStatement.setString(2, pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String fetchQuery = "SELECT balance FROM account WHERE account_number = ?";
                    PreparedStatement fetchStatement = connection.prepareStatement(fetchQuery);
                    fetchStatement.setDouble(1, account_number);

                    //get cuurent balance
                    connection.setAutoCommit(false);
                    ResultSet resultSet1 = fetchStatement.executeQuery();

                    if (resultSet1.next())
                         current_balance = resultSet1.getDouble("balance");
                    if (current_balance >= amount) {
                        String debitQuery = "UPDATE account SET balance = balance - ? WHERE account_number = ?";
                        PreparedStatement debitPrepateStatemnt = connection.prepareStatement(debitQuery);
                        debitPrepateStatemnt.setDouble(1, amount);
                        debitPrepateStatemnt.setLong(2, account_number);
                        if (debitPrepateStatemnt.executeUpdate() > 0) {
                            System.out.println(amount + " debit successfully");
                            connection.commit();
                        } else {
                            System.out.println("failed to debit maney");
                            connection.rollback();
                        }
                    } else {
                        System.out.println("insufficient balance..");
                    }
                    connection.setAutoCommit(true);
                } else {
                    System.out.println("Invalid Pin");
                }
            } catch (SQLException e) {
                System.out.println("error in debit function");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }else {
            System.out.println("invalid account number");
        }
    }

    //transfer maney

    public void transferManey(long sender_account) throws SQLException{

        sc.nextLine();
        System.out.println("account details.....");
        System.out.println("From-> "+sender_account);
        System.out.print("To-> ");
        long reciever_account = sc.nextLong();
        System.out.print("Amount-> ");
        Double amount = sc.nextDouble();
        System.out.print("Pin-> ");
        String pin = sc.next();
        if(sender_account!=0 && reciever_account!=0) {
            try {
                String query = "SELECT balance FROM account WHERE account_number = ? AND security_pin = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setDouble(1, sender_account);
                preparedStatement.setString(2, pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String fetchQuery = "SELECT balance FROM account WHERE account_number = ?";
                    PreparedStatement fetchStatement = connection.prepareStatement(fetchQuery);
                    fetchStatement.setDouble(1, sender_account);

                    //get cuurent balance
                    connection.setAutoCommit(false);
                    ResultSet resultSet1 = fetchStatement.executeQuery();
                    if (resultSet1.next())
                        current_balance = resultSet1.getDouble("balance");
                    if (current_balance >= amount) {//transaction done here
                        //debit maney
                        String debitQuery = "UPDATE account SET balance = balance - ? WHERE account_number = ?";
                        PreparedStatement debitPrepateStatemnt = connection.prepareStatement(debitQuery);
                        debitPrepateStatemnt.setDouble(1, amount);
                        debitPrepateStatemnt.setLong(2, sender_account);
                        int debitEffect = debitPrepateStatemnt.executeUpdate();

                        // credit maney
                        String creditQuery = "UPDATE account SET balance = balance + ? WHERE account_number = ?";
                        PreparedStatement creditPrepateStatemnt = connection.prepareStatement(creditQuery);
                        creditPrepateStatemnt.setDouble(1, amount);
                        creditPrepateStatemnt.setLong(2, reciever_account);
                        int creditEffect = creditPrepateStatemnt.executeUpdate();

                        if (debitEffect > 0 && creditEffect>0) {
                            System.out.println(amount + " transfer successfully");
                            connection.commit();
                        } else {
                            System.out.println("failed to transfer maney");
                            connection.rollback();
                        }
                    } else {
                        System.out.println("insufficient balance..");
                    }
                    connection.setAutoCommit(true);
                } else {
                    System.out.println("Invalid Pin");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }else {
            System.out.println("invalid account details.");
        }
    }

    //check balance
    public void checkBalance(long account_nubmer)throws SQLException{
        sc.nextLine();
        System.out.print("Pin-> ");
        String pin = sc.next();
        try {
            String checkQuery = "SELECT balance FROM account WHERE account_number = ? AND security_pin = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setLong(1,account_nubmer);
            checkStatement.setString(2,pin);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()){
                String fetchQuery = "SELECT balance FROM account WHERE account_number = ?";
                PreparedStatement fetchStatement = connection.prepareStatement(fetchQuery);
                fetchStatement.setLong(1,account_nubmer);
                ResultSet result = fetchStatement.executeQuery();
                if (result.next())
                    System.out.println("Balance-> "+result.getString("balance"));
            }else {
                System.out.println("Invalid Pin");
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

}
