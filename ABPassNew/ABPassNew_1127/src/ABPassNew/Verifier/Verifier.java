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


    public boolean verify(String proofBase64, String aggregateSignatureBase64) {
        Element proof = ElementUtil.getElementByBase64(proofBase64, pairing.getGT());
        Element aggregateSignature = ElementUtil.getElementByBase64(aggregateSignatureBase64, pairing.getG1());
        Element Vs = pairing.pairing(aggregateSignature, g2);
        return Vs.equals(proof);
    }

    public String policiesToJson(ArrayList<Policy> policies){
        Gson gson=new Gson();
        return gson.toJson(policies);
    }



}
