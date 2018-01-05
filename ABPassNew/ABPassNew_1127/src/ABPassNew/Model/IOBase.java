package ABPassNew.Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

/**
 * Created by 41861 on 2017/6/27.
 */
public class IOBase {
    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    
    public void saveParameters(String filepath){
        Gson gson = new Gson();
        String json = gson.toJson(this);
        try {  
            File file = new File(filepath);  
            // if file doesnt exists, then create it  
            if (!file.exists()) {  
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);  
            bw.write(json);  
            bw.flush();  
            bw.close();
  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }
}
