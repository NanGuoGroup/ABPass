package ABPassNew.Model;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by 41861 on 2017/6/22.
 */

public class Policy extends IOBase{
    private AttributeEnum attribute;
    private String requiredValue;
    private Policy() {
    }
    public Policy(AttributeEnum attribute, String requiredValue) {
        this.attribute = attribute;
        this.requiredValue = requiredValue;
    }



    public AttributeEnum getAttribute() {
        return attribute;
    }

    public void setAttribute(AttributeEnum attribute) {
        this.attribute = attribute;
    }

    public String getRequiredValue() {
        return requiredValue;
    }

    public void setRequiredValue(String requiredValue) {
        this.requiredValue = requiredValue;
    }


}
