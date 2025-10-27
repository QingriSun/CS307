import java.io.*;

public class head{
    public static void main(String[] argv){
        try{
            File f = new File("sources/recipes_light.csv");
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);

            String line = reader.readLine();
            System.out.println(line);
            String info[] = new String[27];
            info = line.split(",");
            for (int i = 0; i < 27; i++){
                System.out.println(i + info[i]);
            }
        }catch(FileNotFoundException e){
            System.out.println("FileNotFoundException");
        }catch(IOException e1){
            System.err.println("IOException");
        }



    }
}