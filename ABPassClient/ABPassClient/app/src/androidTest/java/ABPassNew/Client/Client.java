package ABPassNew.Client;

import ABPassNew.HttpUtil;
import ABPassNew.Model.*;
import ABPassNew.Utils.ElementUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 41861 on 2017/6/27.
 */
public class Client extends HasPublicParametersBase{

    private User user=new User();

    private String TBase64;
    private String sBase64;

    private String token;

    public Client(PublicParametersIO publicParametersIO){
        super(publicParametersIO);
    }

    public String generateProofBase64(String policiesJson,String userPassword)
    {
        Gson gson=new Gson();
        ArrayList<Policy> policies=gson.fromJson(policiesJson,new TypeToken<ArrayList<Policy>>(){}.getType());
        Element Vc= pairing.pairing(g1,publicKey).getImmutable();
        Element proof = pairing.getGT().newOneElement();
        for (Policy policy : policies
                ) {
            AttributeEnum attributeEnum = policy.getAttribute();
            if (user.hasValidSignature(attributeEnum)
                    && user.getAttributeHashMap().get(attributeEnum).equals(policy.getRequiredValue())
                    ) {
                Element Vi=pairing.pairing(ElementUtil.getHashElement(policy.getRequiredValue()),publicKey);
                Element r = ElementUtil.getrByPassword(userPassword);
                Element VcPowR = Vc.powZn(r);
                proof = proof.mul(Vi).mul(VcPowR);
            }
        }
        return ElementUtil.getBase64OfElement(proof);
    }
    public String getAggregateSignatureBase64(ArrayList<Policy> policies) {
        Element aggregateSignature = PairingSingleton.getPairing().getG1().newOneElement();
        for (Policy entry : policies
                ) {
            AttributeEnum attributeEnum = entry.getAttribute();
            if (user.hasValidSignature(attributeEnum)
                    && user.getAttributeHashMap().get(attributeEnum).equals(entry.getRequiredValue())
                    ) {
                Element sig = user.getSignatureHashMap().get(attributeEnum).getSignature();
                aggregateSignature = aggregateSignature.mul(sig).duplicate();
            }
        }
        return ElementUtil.getBase64OfElement(aggregateSignature);
    }

    public boolean requestSignature(AttributeEnum attributeEnum) {
        try {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("action", "getsignature");
            parameters.put("token", token);
            parameters.put("attribute", attributeEnum.toString());
            String ret = HttpUtil.post("http://ouy.hyyas.com/server/server.jsp", parameters);
            if (ret.equals("false")) {
                System.out.println("获取签名失败");
                token = "";
                return false;
            } else {
                Signature signature = new Signature(SignatureIO.getInstance(ret));
                user.getSignatureHashMap().put(signature.getAttribute(), signature);
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean generateTts(String cBase64, String userPassword) {
        Element r = ElementUtil.getrByPassword(userPassword);
        Element c = ElementUtil.getElementByBase64(cBase64, pairing.getZr());
        Element t = pairing.getZr().newRandomElement();
        Element T = g1.powZn(t);
        Element s = r.mul(c).add(t);
        this.sBase64=ElementUtil.getBase64OfElement(s);
        this.TBase64=ElementUtil.getBase64OfElement(T);
        return true;
    }

    public boolean requestSignIn(String username,String password){
        try {
            String userAttributeJson = "";
            //模拟登陆
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("action", "signin");
            parameters.put("username", username);
            parameters.put("password", password);
            userAttributeJson = HttpUtil.post("http://ouy.hyyas.com/server/server.jsp", parameters);
            if (userAttributeJson.equals("false")) {
                System.out.println("登陆失败");
                return false;
            }
            System.out.println("登陆成功");
            user.setAttributeHashMapFromJson(userAttributeJson);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static String requestPublicParameters(){
        try {
            String json = "";
            //模拟登陆
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("action", "getparameters");
            json = HttpUtil.post("http://ouy.hyyas.com/server/server.jsp", parameters);
            if (json.equals("false")) {
                System.out.println("获取公共参数失败");
                return "";
            }
            return json;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    public boolean requestCAndgenerateTts(String userPassword) {
        try {
            String randomCBase64 = "";
            randomCBase64 = HttpUtil.get("http://ouy.hyyas.com/server/server.jsp?action=getc");
            generateTts(randomCBase64, userPassword);
            return true;
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            return false;
        }
    }

    public boolean meetPolicy(ArrayList<Policy> policies) {
        HashMap<AttributeEnum, Boolean> meetHashMap = new HashMap<AttributeEnum, Boolean>();
        for (Policy entry : policies
                ) {
            AttributeEnum attributeEnum = entry.getAttribute();
            boolean meet = user.getAttributeHashMap().get(attributeEnum).equals(entry.getRequiredValue());//判断当前用户是否满足该条策略
            if (!meetHashMap.containsKey(attributeEnum) || meetHashMap.get(attributeEnum).equals(false)) {//
                meetHashMap.put(attributeEnum, meet);
            }
        }

        System.out.println("----判断用户的属性是否满足策略----");
        for (Map.Entry<AttributeEnum, Boolean> entry : meetHashMap.entrySet()
                ) {
            System.out.println(entry);
        }
        boolean ret=!meetHashMap.containsValue(false);
        System.out.println("用户是否满足策略:"+ret);
        return ret;
    }

    public boolean requestVerify() {
        try {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("action", "verify");
            parameters.put("TBase64",TBase64);
            parameters.put("sBase64", sBase64);
            System.out.println("----client->requestVerify----");
            token = HttpUtil.post("http://ouy.hyyas.com/server/server.jsp", parameters);
            if (token.equals("false")) {
                System.out.println("验证失败");
                token = "";
                return false;
            }
            System.out.println("验证成功");
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public User getUser() {
        return user;
    }

    public String getTBase64() {
        return TBase64;
    }

    public String getsBase64() {
        return sBase64;
    }
}
