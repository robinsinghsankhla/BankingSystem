import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingApp {
    private static final String url = "jdbc:mysql://localhost:3306/banking_system";
    private static final String user = "root";
    private static final String password = "root";
    public static void main(String args[]){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        try {
            Connection connection = DriverManager.getConnection(url,user,password);
            Scanner sc = new Scanner(System.in);
            User user = new User(connection,sc);
            Account account = new Account(connection,sc);
            AccountManager accountManager = new AccountManager(connection,sc);

            String email;
            long account_number;
            while (true) {
                System.out.println("** WELCOME TO BANKING SYSTEM **");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter choice-> ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        email = user.register();
                        break;
                    case 2:
                        email = user.login();
                        if (email!=null){
                            if(!account.exists_account(email)){
                                System.out.println("1. Open Account");
                                System.out.println("2. LogOut");
                                System.out.print("Choice-> ");
                                int choiceOpenAccount = sc.nextInt();
                                if (choiceOpenAccount == 1) {
                                    account.open_accouont(email);
                                }
                            }
                            //get account number
                            account_number = account.getAccouontNumber(email);
                            int choiceAccManager = 0;
                            while (choiceAccManager!=5) {
                                System.out.println("1. Credit Maney");
                                System.out.println("2. Debit Maney");
                                System.out.println("3. Transfer Maney");
                                System.out.println("4. Check Balance");
                                System.out.println("5. Exit");
                                System.out.print("Choice-> ");
                                choiceAccManager = sc.nextInt();
                                if (choiceAccManager == 1){
                                    accountManager.creditManey(account_number);
                                }else if (choiceAccManager == 2){
                                    accountManager.debitManey(account_number);
                                }else if (choiceAccManager == 3){
                                    accountManager.transferManey(account_number);
                                }else if (choiceAccManager == 4){
                                    accountManager.checkBalance(account_number);
                                }else if (choiceAccManager == 5){
                                    choiceAccManager = 5;
                                }else {
                                    System.out.println("Enter Valid Choice");
                                }
                            }
                        }else {
                            System.out.println("Invalid email or password");
                            break;
                        }
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid Entery");
                }
            }


        }catch (SQLException e){

            System.out.println("error in BakingApp");
            System.out.println(e.getMessage());
        }
    }
}
