package org.example;

import java.sql.*;
import java.util.Scanner;

public class App {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/Apartments?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "";

    static Connection conn;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            try {

                conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
                initDB();

                while (true) {
                    System.out.println("1: add apartment");
                    System.out.println("2: change apartment");
                    System.out.println("3: delete apartment");
                    System.out.println("4: view apartments");
                    System.out.println("5: filter by neighbourhood");
                    System.out.println("6: filter by area");
                    System.out.println("7: filter by number of rooms");
                    System.out.println("8: filter by price");
                    System.out.print("-> ");

                    String s = sc.nextLine();

                    switch (s) {
                        case "1":
                            addApt(sc);
                            break;
                        case "2":
                            changeApt(sc);
                            break;
                        case "3":
                            deleteApt(sc);
                            break;
                        case "4":
                            viewApartments();
                            break;
                        case "5":
                            filterByNeighbourhood(sc);
                            break;
                        case "6":
                            filterByArea(sc);
                            break;
                        case "7":
                            filterByRooms(sc);
                            break;
                        case "8":
                            filterByPrice(sc);
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                if (conn != null) conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void initDB() throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute("DROP TABLE IF EXISTS Properties");
            st.execute("CREATE TABLE Properties (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    " neighbourhood VARCHAR(128) NOT NULL, " +
                    " adress VARCHAR(128) NOT NULL, " +
                    " area FLOAT NOT NULL, " +
                    " rooms INT NOT NULL, " +
                    "price INT NOT NULL )");
        } finally {
            st.close();
        }

    }

    private static void addApt(Scanner sc) throws SQLException {
        System.out.print("Please choose your neighbourhood: ");
        String neighbourhood = sc.nextLine();
        System.out.print("Please enter your address: ");
        String addr = sc.nextLine();
        System.out.print("Please input the area: ");
        double area = Double.parseDouble(sc.nextLine());
        System.out.print("Please enter the number of rooms: ");
        int rooms = Integer.parseInt(sc.nextLine());
        System.out.print("Please choose the price: ");
        int price = Integer.parseInt(sc.nextLine());


        PreparedStatement ps = conn.prepareStatement("INSERT INTO Properties (neighbourhood, adress, area, rooms, price) VALUES(?, ?, ?, ?, ?)");
        try {
            ps.setString(1, neighbourhood);
            ps.setString(2, addr);
            ps.setDouble(3, area);
            ps.setInt(4, rooms);
            ps.setInt(5, price);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private static void deleteApt(Scanner sc) throws SQLException {
        System.out.print("Enter your address: ");
        String addr = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("DELETE FROM Properties WHERE adress = ?");
        try {
            ps.setString(1, addr);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private static void changeApt(Scanner sc) throws SQLException {
        System.out.print("Enter your address: ");
        String addr = sc.nextLine();
        System.out.print("Enter the price: ");
        int price = Integer.parseInt(sc.nextLine());

        PreparedStatement ps = conn.prepareStatement("UPDATE Properties SET adress = ? WHERE price = ?");
        try {
            ps.setString(1, addr);
            ps.setInt(2, price);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }
    private static void viewApartments() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Properties");
        try {
            ResultSet result = ps.executeQuery();
            printResult(result);
        } finally {
            ps.close();
        }
    }

    private static void filterByNeighbourhood(Scanner sc) throws SQLException {
        System.out.print("Please choose a desired neighbourhood: ");
        String neighbourhood = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Properties WHERE neighbourhood = ?");

        try {
            ps.setString(1, neighbourhood);
            ResultSet result = ps.executeQuery();

            printResult(result);
        } finally {
            ps.close();
        }
    }

    private static void filterByArea(Scanner sc) throws SQLException {
        System.out.print("Please enter a minimum desired area: ");
        double areaMin = Double.parseDouble(sc.nextLine());
        System.out.print("Please enter a minimum desired area: ");
        double areaMax = Double.parseDouble(sc.nextLine());

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Properties WHERE area BETWEEN ? AND ?");

        try {
            ps.setDouble(1, areaMin);
            ps.setDouble(2, areaMax);
            ResultSet result = ps.executeQuery();

            printResult(result);
        } finally {
            ps.close();
        }
    }

    private static void filterByRooms(Scanner sc) throws SQLException {
        System.out.print("Please enter a minimum desired number of rooms: ");
        int roomsMin = Integer.parseInt(sc.nextLine());
        System.out.print("Please enter a maximum desired number of rooms: ");
        int roomsMax = Integer.parseInt(sc.nextLine());

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Properties WHERE rooms BETWEEN ? AND ?");

        try {
            ps.setInt(1, roomsMin);
            ps.setInt(2, roomsMax);
            ResultSet result = ps.executeQuery();

            printResult(result);
        } finally {
            ps.close();
        }
    }

    private static void filterByPrice(Scanner sc) throws SQLException {
        System.out.print("Please enter a minimum price: ");
        int minPrice = Integer.parseInt(sc.nextLine());
        System.out.print("Please enter a maximum price: ");
        int maxPrice = Integer.parseInt(sc.nextLine());

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Properties WHERE price BETWEEN ? AND ?");

        try {
            ps.setInt(1, minPrice);
            ps.setInt(2, maxPrice);
            ResultSet result = ps.executeQuery();

            printResult(result);
        } finally {
            ps.close();
        }
    }

    private static void printResult(ResultSet rs) throws SQLException {
            try {
                ResultSetMetaData md = rs.getMetaData();

                for (int i = 1; i <= md.getColumnCount(); i++)
                    System.out.print(md.getColumnName(i) + "\t\t");
                System.out.println();

                while (rs.next()) {
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        System.out.print(rs.getString(i) + "\t\t");
                    }
                    System.out.println();
                }
            } finally {
                rs.close();
            }
        }
    }

