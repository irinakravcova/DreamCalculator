
import java.sql.*;
import java.util.List;
import java.util.Scanner;
import java.awt.geom.Arc2D;

public class finalMain {
    public static void main(String[] args) {
        //connecting to SQL database
        String dbURL = "jdbc:mysql://localhost:3306/java24";
        String user = "root";
        String password = "1234MySQL";

        FinData finData;
        Scanner scanner = new Scanner(System.in);

        //User is asked what he wants to do
        System.out.println("Welcome to DreamCalculator!");
        System.out.println("Do you want to Enter data (1) or Read data (2) : ");
        int choice = scanner.nextInt();

        //Enter household name
        finData = new FinData();
        System.out.println("Enter household name: ");
        finData.familyName = scanner.next().trim().toLowerCase();

        //READ or ENTER data
        if (choice == 2) {
            //read data
            try (Connection conn = DriverManager.getConnection(dbURL, user, password)) {
                System.out.println("read DB");
                finData = readData(conn, finData.familyName);
            } catch (SQLException e) {
                System.out.println("Err " + e);
            }
        } else {
            //enter data
            System.out.println("Enter how big is your dream in euros? ");
            finData.dreamSum = scanner.nextFloat();
            finData.billsTotal = 0.0f;
            finData.percent = 0.0f;

            //IF dreamSum is too small, the app asks for re-enter the sum as long as it will be more than 100
            while (finData.dreamSum < 100) {
                System.out.println("sum is too small. Try again.\n Enter how big is your dream in euros? ");
                finData.dreamSum = scanner.nextFloat();
                // break;
            }


            System.out.println("You want to save up " + finData.dreamSum + " Euro. Enter your monthly income: ");
            finData.income = scanner.nextFloat();

            System.out.println("So, your monthly earning is " + finData.income + " Euros. Do you have any expenses every month? y/n");
            scanner.nextLine();
            char answer = scanner.nextLine().charAt(0);

            //Array with the elements - questions inside
            //Depending on user answer we create two IFs: yes part includes for-loop to sum all the elements,
            //no -user enters "no", the app goes further to the calculations
            finData.billsTotal = 0;
            finData.percent = 0;
            String[] questions = new String[]{
                    "How much do you pay monthly for utilities (rent, gas, water, electricity)?",
                    "How much do you pay monthly for food?",
                    "What is your monthly expenses for clothing?",
                    "What is your monthly expenses on children?",
                    "How much is for any other expenses in a month?"};

            if (answer == 'y') {
                System.out.println("Enter your monthly expenses one by one (in euros). Type (0) if not applicable. ");
                //    System.out.println();

                for (int i = 0; i < questions.length; i++) {
                    String question = questions[i];
                    System.out.println(question);
                    finData.billsAnswer = scanner.nextFloat();
                    finData.billsTotal = finData.billsTotal + finData.billsAnswer;
                }
                System.out.printf("Total expenses sum for %s in a month: %.2f Euros. ", finData.familyName, finData.billsTotal);

                System.out.println("Enter how your expenses can change in % per month? ");
                finData.percent = scanner.nextInt();

            } else if (answer == 'n') {
                //   if the user enters "no", the app goes further to the calculations
            }
            //Variables to calculate the difference between income and expenditure and changing %
            float variablePart = finData.billsTotal * finData.percent / 100;
            float moneyLeft = finData.income - finData.billsTotal;
            float moneyLeftMax = finData.income - finData.billsTotal + variablePart;
            float moneyLeftMin = finData.income - finData.billsTotal - variablePart;

            //BEST, OPTIMAL and WORST options, system print out
            if (moneyLeftMax <= 0) {
                System.out.println("In the best option: Your expenses exceed your income. Your dream is unreachable. ");
                float resultMax = 0;
            }
            finData.resultMax = (int) Math.ceil(finData.dreamSum / moneyLeftMax);

            if (moneyLeft <= 0) {
                System.out.println("In the optimal option: Your expenses exceed your income. Your dream is unreachable. ");
                float resultOpt = 0;
                ;
            }
            finData.resultOpt = (int) Math.ceil(finData.dreamSum / moneyLeft);

            if (moneyLeftMin <= 0) {
                System.out.println("In the worst scenario: Your expenses exceed your income. Your dream is unreachable. ");
                float resultMin = 0;
            }
            finData.resultMin = (int) Math.ceil(finData.dreamSum / moneyLeftMin);


            //if everything was correct then the app inserts user to the database
            try (Connection conn = DriverManager.getConnection(dbURL, user, password)) {
                System.out.println("insert DB");
                insertData(conn, finData);

            } catch (SQLException e) {
                System.out.println("Err " + e);
            }
        }
        //finData showed on the screen when the user READS data

        System.out.println("Result: ");
        String output = "%s, Your dreamSum is: %.2f";
        System.out.println(String.format(output, finData.familyName, finData.dreamSum));

        System.out.println("In the best option: ");
        reportMonthsToDream(finData.resultMax);

        System.out.println("In the optimal option: ");
        reportMonthsToDream(finData.resultOpt);

        System.out.println("In the worst scenario: ");
        reportMonthsToDream(finData.resultMin);
    }

    // Method to calculate year un month
    public static String monthsToReadable(int monthsTotal) {
        int years = monthsTotal / 12;
        int months = monthsTotal % 12;
        return years + " year(s) and " + months + " month(s)";
    }

    //SQL part to insert data
    public static void insertData(Connection conn, FinData finData) throws SQLException { // String familyName, float dreamSum, float income, float billsTotal, float percent, int Result1, int Result2, int Result3)
        //delete old data about person
        String sql = "DELETE FROM project where familyName = ? ";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, finData.familyName);
        preparedStatement.executeUpdate();

        sql = "INSERT INTO project (familyName, dreamSum, income, billsTotal,percent, Result1, Result2, Result3) Values (?,?,?,?,?,?,?,?)";

        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, finData.familyName);
        preparedStatement.setFloat(2, finData.dreamSum);
        preparedStatement.setFloat(3, finData.income);
        preparedStatement.setFloat(4, finData.billsTotal);
        preparedStatement.setFloat(5, finData.percent);
        preparedStatement.setInt(7, finData.resultMax);
        preparedStatement.setInt(6, finData.resultOpt);
        preparedStatement.setInt(8, finData.resultMin);
        int rowInserted = preparedStatement.executeUpdate();

        if (rowInserted > 0) {
            System.out.println("A new user was inserted successfully");
        } else {
            System.out.println("Something went wrong");
        }
    }

    //“A ceil function converts a decimal number to the immediate largest integer.”
// If the number passed is already a whole number or an integer, then the same number is the ceiling value.
// However, if you pass a null value to the ceil function in mathematics you get a “zero”
//    private static void reportMonthsToDream(float dreamSum, float moneyLeft) {
//        int monthsToDream = (int) (0.5 + Math.ceil(dreamSum / moneyLeft));
    private static void reportMonthsToDream(int result) {
        int monthsToDream = result; //(int) (0.5 + Math.ceil(finData.dreamSum / finData.moneyLeft));

        // depending on the amount of months, the output will be following:
        if (monthsToDream < 12 && monthsToDream >= 0) {
            System.out.println("Your dream will come true in " + monthsToDream + " months");
        } else if (monthsToDream > 12 && monthsToDream % 12 != 0) {
            System.out.println("Your dream will come true in " + monthsToReadable(monthsToDream));
        } else if (monthsToDream % 12 == 0) {
            System.out.println("Your dream will come true in " + monthsToDream / 12 + " year(s)");
        } else {
            System.out.println();
        }
    }

    public static FinData readData(Connection conn, String familyName) throws SQLException {

        FinData finData = new FinData();

        String sql = "Select familyName, dreamSum, income, billsTotal,percent, Result1, Result2, Result3 From project where familyName = '" + familyName + "'";

        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.next()) {

            finData.familyName = resultSet.getString("familyName");
            finData.dreamSum = resultSet.getFloat("dreamSum");
            finData.income = resultSet.getFloat("income");
            finData.billsTotal = resultSet.getFloat("billsTotal");
            finData.percent = resultSet.getFloat("percent");
            finData.resultOpt = resultSet.getInt("Result1");
            finData.resultMax = resultSet.getInt("Result2");
            finData.resultMin = resultSet.getInt("Result3");

            String output = "Your dreamSum is: %.2f \n\t Time for the Dream (month(s)): \n\t Result1: %d \n\t result2: %d " +
                    "\n\t Result3: %d \n\t ";

            System.out.println(String.format(output, finData.dreamSum, finData.resultMax, finData.resultOpt, finData.resultMin));
        } else {
            System.out.println("Sorry, we have no data about you");
        }
        return finData;
    }
}
