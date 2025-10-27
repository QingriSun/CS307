public class InsertRecipes{
    public static void main(String[] argv){
        DatabaseManipulation dm = new DatabaseManipulation(
            "localhost", "project1", "postgres", "postgres616123", "5432", "public"
        );
        dm.recipesCsvUsage("sources/recipes_light.csv");
    }
}