package ABPassNew.Model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import it.unisa.dia.gas.jpbc.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 41861 on 2017/6/27.
 */
public class User {

    private Element m;
    private String token;

    private HashMap<AttributeEnum, String> attributeHashMap = new HashMap<AttributeEnum, String>();

    private HashMap<AttributeEnum, Signature> signatureHashMap = new HashMap<AttributeEnum, Signature>();



    public User() {

    }

    public boolean hasValidSignature(AttributeEnum attributeEnum) {
        if (signatureHashMap.containsKey(attributeEnum)) {
            if (signatureHashMap.get(attributeEnum).isValid()) {
                return true;
            }
        }
        return false;
    }

    public HashMap<AttributeEnum, String> getAttributeHashMap() {
        return attributeHashMap;
    }

    public HashMap<AttributeEnum, Signature> getSignatureHashMap() {
        return signatureHashMap;
    }

    public String getAttributeHashMapJson() {
        Gson gson = new Gson();
        return gson.toJson(attributeHashMap);
    }

    public void setAttributeHashMapFromJson(String json) {
        Gson gson = new Gson();
        attributeHashMap = gson.fromJson(json, new TypeToken<HashMap<AttributeEnum, String>>() {
        }.getType());
    }

    public void print() {
        System.out.println("-----User----");
        for (Map.Entry<AttributeEnum, String> s : attributeHashMap.entrySet()) {
            System.out.println("attribute:" + s);
        }
        for (Map.Entry<AttributeEnum, Signature> s : signatureHashMap.entrySet()) {
            System.out.println("signature:" + s);
        }
    }

    public Element getM() {
        return m;
    }

    public void setM(Element m) {
        this.m = m;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
