public class TestDriver {
    public static void main(String[] args) {
        DatabaseManipulation dm = new DatabaseManipulation(
             "localhost", "project1", "postgres", "postgres616123", "5432", "public"
        );
        System.out.println(dm.elementInDatabase());
        
    }
}
