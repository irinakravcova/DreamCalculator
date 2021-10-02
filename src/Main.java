//import java.awt.geom.Arc2D;
//import java.sql.*;
//import java.util.Scanner;
//Guntas kods
//public class Main {
//    public static void main(String[] args) {
//        String dbURL = "jdbc:mysql://localhost:3306/java24";
//        String user = "root";
//        String password = "1234MySQL";
//
//        FinData finData; // = new FinData();
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("Read data (1) enter data (2): ");
//        int choise = scanner.nextInt();
//
//        //jaievada household name
//        finData = new FinData();
//        System.out.println("Enter household name: ");
//        finData.familyName = scanner.next(); //.nextLine();
//
//
//        if (choise == 1) {
//            //read data
//            try (Connection conn = DriverManager.getConnection(dbURL, user, password)) {
//                System.out.println("read DB");
//
//                finData = readData(conn, finData.familyName);
//
//            } catch (SQLException e) {
//                System.out.println("Err " + e);
//            }
//        } else {
//            //enter data
//            System.out.println("Enter how big is your dream in euros? ");
//            finData.dreamSum = scanner.nextFloat();
//            finData.billsTotal = 0.0f;
//            finData.percent = 0.0f;
//
//            if (finData.dreamSum > 0) {
//                System.out.println("You have to save up " + finData.dreamSum + " Euro. Enter your monthly income: ");
//            } else {
//                System.out.println("sum is not valid. Try again. ");
//            }
//            finData.income = scanner.nextFloat();
//
//            System.out.println("So, your monthly earning is " + finData.income + " Euros. " + "Do you have any expenses every month? y/n");
//            scanner.nextLine();
//            char answer = scanner.nextLine().charAt(0);
//            //char answer = 'y';
//            //float billsTotal = 0;
//            // float moneyLeft = salary - billsTotal;
//
//
//            if (answer == 'y') {
//                System.out.println("Enter your monthly expenses one by one (in euros). Type (0) if not applicable");
//                System.out.println();
//                System.out.println("How much do you pay for communal utilities (gas, water, electricity?)");
//                float bill1 = scanner.nextFloat();
//                System.out.println("How much do you pay for food?");
//                float bill2 = scanner.nextFloat();
//                System.out.println("What is your monthly expenses for clothing?");
//                float bill3 = scanner.nextFloat();
//                System.out.println("What is your monthly expenses on children?");
//                float bill4 = scanner.nextFloat();
//                System.out.println("How much is for unexpected expenses?");
//                float bill5 = scanner.nextFloat();
//                System.out.println("Enter how your expenses can change in percentage per month? ");
//                finData.percent = scanner.nextFloat();
//                finData.billsTotal = bill1 + bill2 + bill3 + bill4 + bill5;
//                System.out.println("Total expenses sum in a month: " + finData.billsTotal + " Euros");
//
//            } else {
//                System.out.println();
//            }
//
//            /*String[] questions = new String[]{
//                    "How much do you pay monthly for communal utilities (gas, water, electricity?)",
//                    "How much do you pay monthly for food?",
//                    "What is your monthly expenses for clothing?",
//                    "What is your monthly expenses on children?",
//                    "How much is for unexpected expenses in a month?"};
//
//            if (answer == 'y') {
//                System.out.println("Enter your monthly expenses one by one (in euros). Type (0) if not applicable");
//                System.out.println();
//                //for loop - atbilde summējas
//                for (int i = 0; i < questions.length; i++) {
//                    String question = questions[i];
//                    System.out.println(question);
//                    float billsAnswer = scanner.nextFloat();
//                    billsTotal = billsTotal + billsAnswer;
//                }*/
//
//            float moneyLeft = finData.income - finData.billsTotal;
//            if (moneyLeft < 0) {
//                System.out.println("In the optimal option, Your expenses exceed your income. Your dream is unreachable. ");
//            }
//            finData.result1 = (int) Math.ceil(finData.dreamSum / moneyLeft);
//
//
//            float moneyLeftMax = finData.income - (finData.billsTotal - finData.billsTotal * finData.percent / 100);
//            if (moneyLeftMax < 0) {
//                System.out.println("Unfortunately, Your expenses exceed your income. Your dream is unreachable. ");
//            }
//            finData.resultMax = (int) Math.ceil(finData.dreamSum / moneyLeftMax);
//
//
//            float moneyLeftMin = finData.income - (finData.billsTotal + finData.billsTotal * finData.percent / 100);
//            if (moneyLeftMin < 0) {
//                System.out.println("Unfortunately, in the worst scenario Your expenses exceed your income. Your dream is unreachable. ");
//            }
//            finData.resultMin = (int) Math.ceil(finData.dreamSum / moneyLeftMin);
//
//            try (Connection conn = DriverManager.getConnection(dbURL, user, password)) {
//                System.out.println("insert DB");
//
//                insertData(conn, finData);
//
//            } catch (SQLException e) {
//                System.out.println("Err " + e);
//
//            }
//        }
//
//        //druk'at finData uzekr'ana
//
//        System.out.println("Result:");
//
//        String output = "%s your amount for dream: dreamsum: %.2f";
//
//        System.out.println(String.format(output, finData.familyName, finData.dreamSum));
//
//        System.out.println("In the best option: ");
//        reportMonthsToDream(finData.resultMax);
//
//        System.out.println("In the optimal option: ");
//        reportMonthsToDream(finData.result1);
//
//        System.out.println("In the worst scenario: ");
//        reportMonthsToDream(finData.resultMin);
//    }
//
//    public static String monthsToReadable(int monthsTotal) {
//        int years = monthsTotal / 12;
//        int months = monthsTotal % 12;
//        return years + " year(s) and " + months + " month(s)";
//    }
//
//    public static void insertData(Connection conn, FinData finData) throws SQLException { // String familyName, float dreamSum, float income, float billsTotal, float percent, int Result1, int Result2, int Result3)
//        //nodzesh vecos datus par personu
//        String sql = "DELETE FROM project where familyName = ? ";
//        PreparedStatement preparedStatement = conn.prepareStatement(sql);
//        preparedStatement.setString(1, finData.familyName);
//        preparedStatement.executeUpdate();
//
//
//        sql = "INSERT INTO project (familyName, dreamSum, income, billsTotal,percent, Result1, Result2, Result3) Values (?,?,?,?,?,?,?,?)";
//
//        preparedStatement = conn.prepareStatement(sql);
//        preparedStatement.setString(1, finData.familyName);
//        preparedStatement.setFloat(2, finData.dreamSum);
//        preparedStatement.setFloat(3, finData.income);
//        preparedStatement.setFloat(4, finData.billsTotal);
//        preparedStatement.setFloat(5, finData.percent);
//        preparedStatement.setInt(6, finData.result1);
//        preparedStatement.setInt(7, finData.resultMax);
//        preparedStatement.setInt(8, finData.resultMin);
//        int rowInserted = preparedStatement.executeUpdate();
//
//        if (rowInserted > 0) {
//            System.out.println("A new user was inserted successfully");
//        } else {
//            System.out.println("Something went wrong");
//        }
//    }
//
//    private static void reportMonthsToDream(int result) {
//        int monthsToDream = result; //(int) Math.ceil(dreamSum / moneyLeft);
//
//// depending on the amount of months, the output will be following:
//        if (monthsToDream < 12) {
//            System.out.println("Your dream will come true in " + monthsToDream + " months");
//        } else if (monthsToDream > 12 && monthsToDream % 12 != 0) {
//            System.out.println("Your dream will come true in " + monthsToReadable(monthsToDream));
//        } else {
//            System.out.println("Your dream will come true in " + monthsToDream / 12 + "years");
//        }
//    }
//
//    public static FinData readData(Connection conn, String familyName) throws SQLException {
//
//        FinData finData = new FinData();
//
//        String sql = "Select familyName, dreamSum, income, billsTotal,percent, Result1, Result2, Result3 From project where familyName = '" + familyName + "'";
//
//        Statement statement = conn.createStatement();
//        ResultSet resultSet = statement.executeQuery(sql);
//
//        if (resultSet.next()) {
//            //System.out.println(resultSet.getString("familyName"));
//
//            finData.familyName = resultSet.getString("familyName");
//            finData.dreamSum = resultSet.getFloat("dreamSum");
//            finData.income = resultSet.getFloat("income");
//            finData.billsTotal = resultSet.getFloat("billsTotal");
//            finData.percent = resultSet.getFloat("percent");
//            finData.result1 = resultSet.getInt("Result1");
//            finData.resultMax = resultSet.getInt("Result2");
//            finData.resultMin = resultSet.getInt("Result3");
//
//            String output = "Your amount for dream: \n\t dreamsum: %f \n\t Result1: %d \n\t result2: %d " +
//                    "\n\t Result3: %d \n\t ";
//
//            System.out.println(String.format(output, finData.dreamSum, finData.result1, finData.resultMax, finData.resultMin));
//        } else {
//            System.out.println("Sorry we have not data about you");
//        }
//        return finData;
//    }
//}