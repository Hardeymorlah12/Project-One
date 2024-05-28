package Week4.ProjectOne;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class DeveloperImpl implements Week4.ProjectOne.Developer {
    private final Connection connection;

    public DeveloperImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public ResultSet loadDevelopers() throws SQLException {
        // Creating Connection to Database
        String url = "jdbc:mysql://localhost:3306/developer";
        String username = "root";
        String password = "Hardeymorlah12%";
        String file = "C:\\Users\\User\\IdeaProjects\\Ingryd January Cohort\\src\\Week4\\ProjectOne\\Project.txt";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {

            // Creating the 'developers table'
            String developersTableQuery = "CREATE TABLE IF NOT EXISTS developers(name Text, age Integer, location Text, skill Text )";
            statement.executeUpdate(developersTableQuery);

            // Fetching the contents of 'Project.txt'
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {

                    // Removing 'HashTag' from the contents
                    String removeHash = line.substring(0, line.lastIndexOf("#"));
                    String[] data = removeHash.split(",");

                    // Populating the fetched data into the database table called 'developers'
                    String insertDevsQuery = "INSERT INTO developers(name,age,location,skill) VALUES (?,?,?,?)";
                    PreparedStatement prStatement = connection.prepareStatement(insertDevsQuery);
                    prStatement.setString(1, data[0]);

                    // Trying to catch NumberFormatException
                    try {
                        prStatement.setInt(2, Integer.parseInt(data[1].trim()));
                    } catch (NumberFormatException n) {
                        System.out.println(n.getMessage());
                        continue;
                    }
                    prStatement.setString(3, data[2]);
                    prStatement.setString(4, data[3]);
                    prStatement.addBatch();
                    prStatement.executeBatch();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        // Fetch all loaded Contents
        String allDevs = "SELECT * FROM developers";
        //ResultSet resultSet;
        ResultSet resultSet;
        resultSet = connection.createStatement().executeQuery(allDevs);
        return resultSet;
    }
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/developer"; // developer
        String username = "root";
        String password = "Hardeymorlah12%";
       // String file = "C:\\Users\\User\\IdeaProjects\\Ingryd January Cohort\\src\\Week4\\ProjectOne\\Project.txt";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Developer dev = new DeveloperImpl(connection);
            ResultSet resultSet = dev.loadDevelopers();
            System.out.println("Name\t\tAge\tLocation\tSkill");
            while (resultSet.next()) {
                try {
                    String name = resultSet.getString("name");
                    int age = resultSet.getInt("age");
                    String location = resultSet.getString("location");
                    String skill = resultSet.getString("skill");
                    System.out.printf("%s\t%d\t%s\t%s\t\n", name, age, location, skill);
                } catch (Exception exception) {
                    System.out.println(exception.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}