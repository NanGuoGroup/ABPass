package ABPassNew.Model;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class PrivateParametersIO extends IOBase{
    private String privateKeyBase64;
    public PrivateParametersIO() {
    	
    }

    public static PrivateParametersIO getInstanceFromFile(String filePath) {
        Gson gson = new Gson();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        PrivateParametersIO privateParametersBase64 = gson.fromJson(reader, PrivateParametersIO.class);
        return privateParametersBase64;
    }

    public static PrivateParametersIO getInstance(String json){
        Gson gson=new Gson();
        PrivateParametersIO privateParametersBase64 = gson.fromJson(json,PrivateParametersIO.class);
        return privateParametersBase64;
    }

    public String getPrivateKeyBase64() {
        return privateKeyBase64;
    }

    public void setPrivateKeyBase64(String privateKeyBase64) {
        this.privateKeyBase64 = privateKeyBase64;
    }
}
