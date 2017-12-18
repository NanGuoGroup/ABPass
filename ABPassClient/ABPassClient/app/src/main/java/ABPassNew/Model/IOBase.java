package ABPassNew.Model;

import com.google.gson.Gson;

/**
 * Created by 41861 on 2017/6/27.
 */
public class IOBase {
    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
