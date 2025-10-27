
public class TestUsersInsertion {
    public static void main(String[] argv){

        DatabaseManipulation dm = new DatabaseManipulation(
             "localhost", "project1", "postgres", "postgres616123", "5432", "public"
        );

        System.out.println(dm.elementInDatabase());

        dm.usersCsvUsage();
    }
    
}
