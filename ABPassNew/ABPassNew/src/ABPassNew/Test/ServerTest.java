package ABPassNew.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ABPassNew.Client.Client;
import ABPassNew.Model.AttributeEnum;
import ABPassNew.Model.PairingSingleton;
import ABPassNew.Model.Policy;
import ABPassNew.Model.PrivateParametersIO;
import ABPassNew.Model.PublicParametersIO;
import ABPassNew.Model.User;
import ABPassNew.Movies.Movies;
import ABPassNew.Server.Server;
import ABPassNew.Utils.ElementUtil;
import ABPassNew.Utils.HttpUtil;
import ABPassNew.Verifier.Movie;
import ABPassNew.Verifier.Verifier;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

/**
 * Created by 41861 on 2017/6/27.
 */
public class ServerTest {

	public static Pairing pairing= PairingSingleton.getPairing();
	
	public static Element r = ElementUtil.getrByPassword("asdasdasd").getImmutable();
	
	public static PrivateParametersIO privateParametersIO = PrivateParametersIO.getInstanceFromFile("privateConfig.json");
	public static PublicParametersIO publicParametersIO = PublicParametersIO.getInstanceFromFile("publicConfig.json");
	public static Element g1 = ElementUtil.getElementByBase64(publicParametersIO.getG1Base64(), pairing.getG1()).getImmutable();
	public static Element g2 = ElementUtil.getElementByBase64(publicParametersIO.getG2Base64(), pairing.getG2()).getImmutable();
	public static Element publicKey = ElementUtil.getElementByBase64(publicParametersIO.getPublicKeyBase64Base64(), pairing.getG2()).getImmutable();
	public static Element M = g1.powZn(r).getImmutable();
	public static Element sk = ElementUtil.getElementByBase64(privateParametersIO.getPrivateKeyBase64(), pairing.getZr()).getImmutable();

	public static Element Vc = pairing.pairing(g1, publicKey).getImmutable();
	public static Element t = pairing.getZr().newRandomElement().getImmutable();
	
	public static int count = 0;
	public static ArrayList<BigInteger> Wn = new ArrayList<BigInteger>(); //用于记录Vi的指数;
    
	public static User user = new User();
	public static void main(String[] args) throws Exception {   
		int length = r.getLengthInBytes();
		System.err.println(length);
    	Element h = ElementUtil.getHashElement("1990").getImmutable();
    	Element sigElement = h.mul(M).powZn(sk).getImmutable();
    	int length2 = g2.getLengthInBytes();
    	System.err.println(length2);
    	String ret = HttpUtil.get("http://115.159.211.53/movies.jsp");
    	ArrayList<Movie> movies = Movies.getMovies(ret);
        ArrayList<Policy> policies = (ArrayList<Policy>) movies.get(0).getPolicies().clone();
        ArrayList<String> TA = (ArrayList<String>) movies.get(0).getTA().clone();
        
        Element challenge = pairing.getZr().newRandomElement().getImmutable();
        Element aggregateSignature = getAggregateSignatureBase64(policies, sigElement);
        Element commitment = getCommitmentBase64(TA);
        ArrayList<Element> responses = (ArrayList<Element>) getResponseBase64(challenge).clone();
        boolean result = verify(aggregateSignature, commitment, responses, TA, challenge);
        System.out.println(result);
	}
    
    public static Element getCommitmentBase64(ArrayList<String> TA){      
        //12.11添加获取系统时间
//        long currentTimeStart = System.currentTimeMillis();
        Element commitment = pairing.getGT().newOneElement();
        for (String Ti: TA){
            Element Vi = ElementUtil.getElementByBase64(Ti, pairing.getGT());
            commitment = commitment.mul(Vi.powZn(t));
        }
        commitment = commitment.mul(Vc.powZn(t));
        return commitment;
        //12.11添加获取系统时间
//        long currentTimeEnd = System.currentTimeMillis();
//        long costTime = currentTimeEnd - currentTimeStart;
//        Log.i("生成承诺时间花费：", " " + costTime);
    }
    
    public static ArrayList<Element> getResponseBase64(Element challenge){
        ArrayList<Element> responses = new ArrayList ();
        for (BigInteger integer: Wn){
            Element response = t.add(challenge.mul(integer));
            responses.add(response);
        }
        Element response = t.add(challenge.mul(r).mul(BigInteger.valueOf(count)));

        responses.add(response);
        return responses;
    }
    
    public static Element getAggregateSignatureBase64(ArrayList<Policy> policies, Element signature) {
        count = 0;
        Element aggregateSignature = PairingSingleton.getPairing().getG1().newOneElement();
        for (Policy entry : policies) {
            if ("1990".equals(entry.getRequiredValue())) {
                aggregateSignature = aggregateSignature.mul(signature).duplicate();
                Wn.add(BigInteger.valueOf(1));//11.29添加
                count++; //11.30添加
            }else {//11.29添加
                Wn.add(BigInteger.valueOf(0));//11.29添加
            }
        }

        return aggregateSignature;
    }
    
    public static boolean verify(Element aggregateSignature, Element commitment, ArrayList<Element> responses, ArrayList<String> TA, Element challenge){
    	//12.11添加获取系统时间
        long currentTimeStart = System.currentTimeMillis();

        Element Vs = pairing.pairing(aggregateSignature, g2).getImmutable();

        //12.11添加获取系统时间
        long currentTimeEnd = System.currentTimeMillis();
        long costTime = currentTimeEnd - currentTimeStart;
        System.out.println("生成Vs时间花费：" + costTime);

        Element left = commitment.mul(Vs.powZn(challenge));

        Element right = pairing.getGT().newOneElement().getImmutable();

        //12.11添加获取系统时间
        long currentTimeStart2 = System.currentTimeMillis();

        for (int i = 0; i < TA.size(); i++){
            Element Vi = ElementUtil.getElementByBase64(TA.get(i), pairing.getGT()).getImmutable();
            Element Ui = responses.get(i).getImmutable();
            right = right.mul(Vi.powZn(Ui));
        }
        Element Vc = pairing.pairing(g1, publicKey).getImmutable();
        Element Uc = responses.get(TA.size()).getImmutable();
        right = right.mul(Vc.powZn(Uc));

        //12.11添加获取系统时间
        long currentTimeEnd2 = System.currentTimeMillis();
        long costTime2 = currentTimeEnd2 - currentTimeStart2;
        System.out.println("生成V1*V2*VL时间花费：" + costTime2);

        return left.equals(right);
    }
}
