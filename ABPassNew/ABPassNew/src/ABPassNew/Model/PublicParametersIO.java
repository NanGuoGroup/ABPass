package ABPassNew.Model;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by 41861 on 2017/6/27.
 */
public class PublicParametersIO extends IOBase {
    private String publicKeyBase64;
    private String g1Base64;
    private String g2Base64;
    public PublicParametersIO() {
    	
    }

    public static PublicParametersIO getInstanceFromFile(String filePath) {
        Gson gson = new Gson();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        PublicParametersIO publicParametersBase64 = gson.fromJson(reader, PublicParametersIO.class);
        return publicParametersBase64;
    }

    public static PublicParametersIO getInstance(String json){
        Gson gson=new Gson();
        PublicParametersIO publicParametersBase64=gson.fromJson(json,PublicParametersIO.class);
        return publicParametersBase64;
    }

    public String getPublicKeyBase64Base64() {
        return publicKeyBase64;
    }

    public void setPublicKeyBase64(String publicKeyBase64) {
        this.publicKeyBase64 = publicKeyBase64;
    }

    public String getG1Base64() {
        return g1Base64;
    }

    public void setG1Base64(String g1Base64) {
        this.g1Base64 = g1Base64;
    }

    public String getG2Base64() {
        return g2Base64;
    }

    public void setG2Base64(String g2Base64) {
        this.g2Base64 = g2Base64;
    }
}
