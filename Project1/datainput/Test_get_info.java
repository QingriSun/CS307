import java.io.*;

public class Test_get_info{
    public static void main(String[] argv){
        try {
            File f = new File("sources/recipes_light.csv");
            FileInputStream inputStream = new FileInputStream(f);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            reader.readLine();

            String str = "";
            do { 
                str += reader.readLine().toString();
            } while (str.charAt(str.length() - 2) == ',');
            
            String info[] = get_info(str);

            for (int i = 0; i < info.length; i++){
                System.out.println(info[i]);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    private static String[] get_info(String line){
        String[] info = new String[27]; // there are 27 element in recipes.csv

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
        info[index] = sb.toString();

        return info;
    }
}