
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManipulation{
    private Connection con = null;
    private ResultSet resultSet;

    private String host = "localhost";
    private String dbname = "lab"; // change database name
    private String user = "postgres"; // change user name
    private String pwd = "postgres616123"; // change password
    private String port = "5432";
    private String schema = "public";

    // constructor
    public DatabaseManipulation(String host, String dbname, String user, String pwd, String port, String schema){
        this.host = host;
        this.dbname = dbname;
        this.user = user;
        this.pwd = pwd;
        this.port = port;
        this.schema = schema;
    }

    public DatabaseManipulation(){}

    private void getConnection() {
        try {
            // load the Driver class. the compiled .class file must in the same directory with the jar file
            Class.forName("org.postgresql.Driver"); // package.package.class

        } catch (Exception e) {
            System.err.println("Cannot find the PostgreSQL driver. Check CLASSPATH.");
            //
            System.out.println("Cannot find driver");
            //
            System.exit(1);
        }

        try { // jdbc: java database connectivity
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbname + "?currentSchema=" + schema;
            con = DriverManager.getConnection(url, user, pwd);

        } catch (SQLException e) {
            System.err.println("Database connection failed hh");
            // System.err.println(e.getMessage());
            System.exit(1);
        }
    }


    private void closeConnection() {
        if (con != null) {
            try {
                con.close();
                con = null;
            } catch (Exception e) {
                // e.printStackTrace();
                System.err.println("Cannot close connection.");
            }
        }
    }

    private int recipesInsertion(int ri, String name, int ai, String date, String ds, 
        String rc, float ar, int rct, int rs){
            int result = 0; // insert 0 line
            String sql = "insert into Recipes (recipeid, name, authorid, datepublished, " +
                "description, recipecategory, aggregaterating, reviewcount, recipeserving) " +
                "values (?, ?, ?, ?::timestamptz, ?, ?, ?, ?, ?);";
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setInt(1, ri);
                preparedStatement.setString(2, name);
                preparedStatement.setInt(3,ai);
                preparedStatement.setString(4, date);
                preparedStatement.setString(5, ds);
                preparedStatement.setString(6, rc);
                preparedStatement.setFloat(7, ar);
                preparedStatement.setInt(8, rct);
                preparedStatement.setInt(9, rs);
                result = preparedStatement.executeUpdate();
            } catch (Exception e) {
                // e.getStackTrace();
                System.err.println(e.getMessage());
                System.err.println("Error in recipesInsertion.");
            }
            return result;
    }

    private int usersInsertion(int id, String name, String gender, int age, int followers, int following){
        gender = (gender.equals("Male")) ? "M" : "F";
        int change = 0;
        String sql = "Insert into users (id, name, gender, age, followers, following) values (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, gender);
            preparedStatement.setInt(4, age);
            preparedStatement.setInt(5, followers);
            preparedStatement.setInt(6, following);
            change = preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Exception in usersInsertion");
        }

        return change;
    }

    public void recipesCsvUsage(String file){
        // delete after I make up the Usage function of the three files
        getConnection();
        //
        try {
            File f = new File(file);
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            reader.readLine(); // consume the header

            String line = "";
            String[] info = new String[27]; // 27 columns in Recipes.csv
            while (reader.ready()){
                do { 
                    line += reader.readLine().toString();
                } while (line.charAt(line.length() - 2) == ',');
                info = get_info(line, 27);
                recipesInsertion(Integer.parseInt(info[0]), info[1], Integer.parseInt(info[2]), info[7], info[8], info[9], Float.parseFloat(info[12]), (int) Float.parseFloat(info[13]), (int) Float.parseFloat(info[23]));
            }
            reader.close();
        } catch (Exception e) {
            //
            closeConnection();
            //
            System.err.println(e.getMessage());
            System.err.println("Exception in manipulating recipes.csv");
        }

        //
        closeConnection();
        //
    }

    public void usersCsvUsage(){
        //
        getConnection();
        //
        try {
            File f = new File("sources/users_light.csv");
            FileInputStream fileInputStream = new FileInputStream(f);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            reader.readLine(); 
            while(reader.ready()){
                String line = reader.readLine();
                String info[] = get_info_users(line, 7);
                usersInsertion(Integer.parseInt(info[0]), info[1], info[2], Integer.parseInt(info[3]), Integer.parseInt(info[4]), Integer.parseInt(info[5]));
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("File not found in userCsvUsage function");
        }


    }

        private static String[] get_info(String line, int len){
        String[] info = new String[len]; // there are 27 element in recipes.csv

        StringBuffer sb = new StringBuffer();
        int index = 0;

        int count = 0; // 1. a character, 2. a back quotation mark ", 3. a comma ,
        boolean have_com = false; // whether there is comma inside an element
        boolean in_process = false;
        for (int i = 0; i < line.length(); i++){
            char c = line.charAt(i);

            if (!have_com && c == '"'){ // handle the first " in "have_com element"
                in_process = true;
                sb.append(c);
                have_com = true;
                continue;
            }

            if (!have_com && c != ','){ // an empty element
                in_process = true;
            }

            if (!have_com && !in_process){
                info[index++] = "";
                in_process = false;
                continue;
            }

            if (!have_com){
                if (c != ','){
                    sb.append(c);
                }else{
                    info[index++] = sb.toString();
                    sb.setLength(0);
                    in_process = false;
                    continue;
                }
            }

            if (have_com){
                if (count == 0){
                    sb.append(c);
                    if (c != '"' && c != ','){
                        count = 1;
                    }
                }else if (count == 1){
                    sb.append(c);
                    if (c == '"'){
                        count = 2;
                    }
                }else if (count == 2 && c == ','){
                    info[index++] = sb.toString();
                    sb.setLength(0);
                    count = 0;
                    have_com = false;
                    in_process = false;
                    continue;
                }else if(count == 2 && c != ','){
                    count = 1;
                    sb.append(c);
                }
            }

        }
        info[index] = sb.toString(); // the last element does not end with comma

        return info;
    }


    public String elementInDatabase() {
        // add your code here...
        getConnection();
        String sql = "select * from users;";
        StringBuffer result = new StringBuffer();
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                result.append(resultSet.getString("id"));
                result.append(resultSet.getString("name"));
                result.append(resultSet.getString("gender"));
                result.append(resultSet.getString("age"));
                result.append(System.lineSeparator());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public void findUsersCsv(){
        try {
            File f = new File("sources/users_light.csv");
            FileInputStream inputStream = new FileInputStream(f);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            reader.readLine();

            String str = "";
            do { 
                str += reader.readLine().toString();
            } while (str.charAt(str.length() - 2) == ',');
            
            String info[] = get_info_users(str, 7);

            for (int i = 0; i < info.length; i++){
                System.out.println(info[i]);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    private static String[] get_info_users(String line, int len){
        String[] info = new String[len]; // there are 27 element in recipes.csv

        StringBuffer sb = new StringBuffer();
        int index = 0;

        int count = 0; // 1. a character, 2. a back ", 3. a ,
        boolean have_com = false; // whether there is comma inside an element
        boolean in_process = false;
        for (int i = 0; i < line.length(); i++){
            char c = line.charAt(i);

            if (!have_com && c == '"'){ // handle the first " in "have_com element"
                in_process = true;
                sb.append(c);
                have_com = true;
                continue;
            }

            if (!have_com && c != ','){ // an empty element
                in_process = true;
            }

            if (!have_com && !in_process){
                info[index++] = "";
                in_process = false;
                continue;
            }

            if (!have_com){
                if (c != ','){
                    sb.append(c);
                }else{
                    info[index++] = sb.toString();
                    sb.setLength(0);
                    in_process = false;
                    continue;
                }
            }

            if (have_com){
                if (count == 0){
                    sb.append(c);
                    if (c != '"' && c != ','){
                        count = 1;
                    }
                }else if (count == 1){
                    sb.append(c);
                    if (c == '"'){
                        count = 2;
                    }
                }else if (count == 2 && c == ','){
                    info[index++] = sb.toString();
                    sb.setLength(0);
                    count = 0;
                    have_com = false;
                    in_process = false;
                    continue;
                }else if(count == 2 && c != ','){
                    count = 1;
                    sb.append(c);
                }
            }

        }

        return info;
    }


}


