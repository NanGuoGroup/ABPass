package ABPassNew.Client;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ABPassNew.HttpUtil;
import ABPassNew.Model.AttributeEnum;
import ABPassNew.Model.HasPublicParametersBase;
import ABPassNew.Model.PairingSingleton;
import ABPassNew.Model.Policy;
import ABPassNew.Model.PublicParametersIO;
import ABPassNew.Model.Signature;
import ABPassNew.Model.SignatureIO;
import ABPassNew.Model.User;
import ABPassNew.Utils.ElementUtil;
import it.unisa.dia.gas.jpbc.Element;

/**
 * Created by 41861 on 2017/6/27.
 */
public class Client extends HasPublicParametersBase{

    private User user=new User();

    private String TBase64;
    private String sBase64;

    private String token;

    private boolean isSignin=false;

    //1031修改添加proof零知识证明部分,并将Vc改为成员变量,增加countVn变量作为Vn的下标
    private Element Vc;
    private ArrayList<BigInteger> Wn = new ArrayList<BigInteger>(); //用于记录Vi的指数;
    private int count;
    public static final int USEFUL = 1;
    public static final int UNUSEFUL = 0;
    private String tBase64; //11.30添加
    //end

    public Client(PublicParametersIO publicParametersIO){
        super(publicParametersIO);
    }

    //11.29修改 将getProofBase64方法修改为getCommitmentBase64方法
    public String getCommitmentBase64(ArrayList<String> TA){
        Vc = pairing.pairing(g1, publicKey).getImmutable();
        Element t = pairing.getZr().newRandomElement().getImmutable();
        tBase64 = ElementUtil.getBase64OfElement(t);

        //12.11添加获取系统时间
        long currentTimeStart = System.currentTimeMillis();

        Element commitment = pairing.getGT().newOneElement();
        for (String Ti: TA){
            Element Vi = ElementUtil.getElementByBase64(Ti, pairing.getGT());
            commitment = commitment.mul(Vi.powZn(t));
        }
        commitment = commitment.mul(Vc.powZn(t));

        //12.11添加获取系统时间
        long currentTimeEnd = System.currentTimeMillis();
        long costTime = currentTimeEnd - currentTimeStart;
        Log.i("生成承诺时间花费：", " " + costTime);

        return ElementUtil.getBase64OfElement(commitment);
    }

    //11.30修改，添加getResponseBase64方法
    public String getResponseBase64(String password, String challengeStr){
        Element challenge = ElementUtil.getElementByBase64(challengeStr, pairing.getZr()).getImmutable();
        Element t = ElementUtil.getElementByBase64(tBase64, pairing.getZr()).getImmutable();

        //12.11添加获取系统时间
        long currentTimeStart = System.currentTimeMillis();

        Element r = ElementUtil.getrByPassword(password).getImmutable();
        ArrayList<String> responses = new ArrayList ();
        for (BigInteger integer: Wn){
            Element response = t.add(challenge.mul(integer));
            String responseBase64 = ElementUtil.getBase64OfElement(response);
            responses.add(responseBase64);
        }
        Element response = t.add(challenge.mul(r).mul(BigInteger.valueOf(count)));

        //12.11添加获取系统时间
        long currentTimeEnd = System.currentTimeMillis();
        long costTime = currentTimeEnd - currentTimeStart;
        Log.i("生成应答时间花费：", " " + costTime);

        String responseBase64 = ElementUtil.getBase64OfElement(response);
        responses.add(responseBase64);
        return responseToJson(responses);
    }
    //11.30修改，添加responseToJson方法
    public String responseToJson(ArrayList<String> response){
        Gson gson = new Gson();
        return gson.toJson(response);
    }
    //11.30修改，注释掉
//    public String getProofBase64(ArrayList<Policy> policies, String userPassword)
//    {
//        //1031将Vc由局部变量赋值改为成员变量赋值
//        Vc= pairing.pairing(g1,publicKey).getImmutable();
//        //end
//
//        Element proof = pairing.getGT().newOneElement();
//
//        for (Policy policy : policies
//                ) {
//            System.out.println("policy: " + policy.getRequiredValue());
//            AttributeEnum attributeEnum = policy.getAttribute();
//            if (user.hasValidSignature(attributeEnum)
//                    && user.getAttributeHashMap().get(attributeEnum).equals(policy.getRequiredValue())
//                    ) {
//                Element Vi = pairing.pairing(ElementUtil.getHashElement(policy.getRequiredValue()),publicKey);
//                Element r = ElementUtil.getrByPassword(userPassword);
//                Element VcPowR = Vc.powZn(r);
//                proof = proof.mul(Vi).mul(VcPowR);
//            }
//        }
//        return ElementUtil.getBase64OfElement(proof);
//    }

    public String getAggregateSignatureBase64(ArrayList<Policy> policies) {
        //12.11添加获取系统时间
        long currentTimeStart = System.currentTimeMillis();
        count = 0;
        Element aggregateSignature = PairingSingleton.getPairing().getG1().newOneElement();
        for (Policy entry : policies) {
            AttributeEnum attributeEnum = entry.getAttribute();
            if (user.hasValidSignature(attributeEnum)
                    && user.getAttributeHashMap().get(attributeEnum).equals(entry.getRequiredValue())) {
                Element sig = user.getSignatureHashMap().get(attributeEnum).getSignature();
                aggregateSignature = aggregateSignature.mul(sig).duplicate();
                Wn.add(BigInteger.valueOf(USEFUL));//11.29添加
                count++; //11.30添加
            }else {//11.29添加
                Wn.add(BigInteger.valueOf(UNUSEFUL));//11.29添加
            }
        }
        //12.11添加获取系统时间
        long currentTimeEnd = System.currentTimeMillis();
        long costTime = currentTimeEnd - currentTimeStart;
        Log.i("聚合签名时间花费：", " " + costTime);

        return ElementUtil.getBase64OfElement(aggregateSignature);
    }

    public boolean requestSignature(AttributeEnum attributeEnum) {
        try {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("action", "getsignature");
            parameters.put("token", token);
            parameters.put("attribute", attributeEnum.toString());
            String ret = HttpUtil.post("http://115.159.211.53/server/server.jsp", parameters);

            if (ret.equals("false")||ret.isEmpty()) {
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

    private boolean generateTts(String cBase64, String userPassword) {
        Element r = ElementUtil.getrByPassword(userPassword);
        Element c = ElementUtil.getElementByBase64(cBase64, pairing.getZr());
        Element t = pairing.getZr().newRandomElement();
        Element T = g1.powZn(t);
        Element s = r.mul(c).add(t);
        Log.i("commitment：", " " + T);
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
            userAttributeJson = HttpUtil.post("http://115.159.211.53/server/server.jsp", parameters);
            if (userAttributeJson.equals("false")) {
                System.out.println("登陆失败");
                return false;
            }
            System.out.println("登陆成功");
            user.setAttributeHashMapFromJson(userAttributeJson);
            isSignin=true;
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
            json = HttpUtil.post("http://115.159.211.53/server/server.jsp", parameters);
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

    public static ArrayList<Policy> getPolicyListFromJson(String json){
        Gson gson=new Gson();
        return (ArrayList<Policy>)gson.fromJson(json, new TypeToken<ArrayList<Policy>>() {
        }.getType());
    }

    //11.29添加方法
    public static ArrayList<String> getTAListFromJson(String json){
        Gson gson = new Gson();
        return (ArrayList<String>)gson.fromJson(json, new TypeToken<ArrayList<String>>() {
        }.getType());
    }
    public boolean requestCAndgenerateTts(String userPassword) {
        try {
            String randomCBase64 = "";
            randomCBase64 = HttpUtil.get("http://115.159.211.53/server/server.jsp?action=getc");
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
            token = HttpUtil.post("http://115.159.211.53/server/server.jsp", parameters);
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

    //1031添加Vc的get方法
    public Element getVc() {
        return Vc;
    }

    //1031添加Vi数量的get方法
    public int getCountVn() {
        return count;
    }

    //1121添加Wn的get方法
    public ArrayList<BigInteger> getWn() {
        return Wn;
    }
    /**
     * 判断是否已登录
     * @return
     */
    public boolean isSignin() {
        return isSignin;
    }

    public void signOut(){
        isSignin=false;
        user.getAttributeHashMap().clear();
        user.getSignatureHashMap().clear();
    }
}
