package ABPassNew.Verifier;

import ABPassNew.Model.*;
import ABPassNew.Utils.ElementUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 41861 on 2017/6/27.
 */
public class Verifier extends HasPublicParametersBase{

public Verifier(PublicParametersIO publicParametersIO){
    super(publicParametersIO);
}


//    public boolean verify(String proofBase64, String aggregateSignatureBase64) {
//        Element proof = ElementUtil.getElementByBase64(proofBase64, pairing.getGT());
//        Element aggregateSignature = ElementUtil.getElementByBase64(aggregateSignatureBase64, pairing.getG1());
//        Element Vs = pairing.pairing(aggregateSignature, g2);
//        return Vs.equals(proof);
//    }

    public static String policiesToJson(ArrayList<Policy> policies){
        Gson gson=new Gson();
        return gson.toJson(policies);
    }

    public static String TAToJson(ArrayList<String> TA){
        Gson gson = new Gson();
        return gson.toJson(TA);
    }
    
    public static ArrayList<String> getStringListFromJson(String json){
        Gson gson = new Gson();
        return (ArrayList<String>)gson.fromJson(json, new TypeToken<ArrayList<String>>() {
        }.getType());
    }
    
    public boolean verify(String aggregateSignatureBase64, String commitmentBase64, String responseJson, String TAJson, Element c){
        Element aggregateSignature = ElementUtil.getElementByBase64(aggregateSignatureBase64, pairing.getG1()).getImmutable();
        //12.11添加获取系统时间
        long currentTimeStart = System.currentTimeMillis();

        Element Vs = pairing.pairing(aggregateSignature, g2).getImmutable();

        //12.11添加获取系统时间
        long currentTimeEnd = System.currentTimeMillis();
        long costTime = currentTimeEnd - currentTimeStart;

        Element commitment = ElementUtil.getElementByBase64(commitmentBase64, pairing.getGT()).getImmutable();
        Element left = commitment.mul(Vs.powZn(c));

        ArrayList<String> TA = getStringListFromJson(TAJson);
        ArrayList<String> response = getStringListFromJson(responseJson);
        Element right = pairing.getGT().newOneElement().getImmutable();

        //12.11添加获取系统时间
        long currentTimeStart2 = System.currentTimeMillis();

        for (int i = 0; i < TA.size(); i++){
            Element Vi = ElementUtil.getElementByBase64(TA.get(i), pairing.getGT()).getImmutable();
            Element Ui = ElementUtil.getElementByBase64(response.get(i), pairing.getZr()).getImmutable();
            right = right.mul(Vi.powZn(Ui));
        }
        Element Vc = pairing.pairing(g1, publicKey).getImmutable();
        Element Uc = ElementUtil.getElementByBase64(response.get(TA.size()), pairing.getZr()).getImmutable();
        right = right.mul(Vc.powZn(Uc));

        //12.11添加获取系统时间
        long currentTimeEnd2 = System.currentTimeMillis();
        long costTime2 = currentTimeEnd2 - currentTimeStart2;

        return left.equals(right);
    }
}
