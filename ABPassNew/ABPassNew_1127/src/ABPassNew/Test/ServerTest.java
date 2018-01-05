package ABPassNew.Test;

import java.util.ArrayList;
import java.util.Arrays;

import ABPassNew.Client.Client;
import ABPassNew.Model.AttributeEnum;
import ABPassNew.Model.PairingSingleton;
import ABPassNew.Model.Policy;
import ABPassNew.Model.PublicParametersIO;
import ABPassNew.Model.User;
import ABPassNew.Movies.Movies;
import ABPassNew.Server.Server;
import ABPassNew.Utils.ElementUtil;
import ABPassNew.Utils.HttpUtil;
import ABPassNew.Verifier.Movie;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

/**
 * Created by 41861 on 2017/6/27.
 */
public class ServerTest {

    public static void main(String[] args) throws Exception {

    	String publicParametersJson = Client.requestPublicParameters();
    	PublicParametersIO publicParametersIO = PublicParametersIO.getInstance(publicParametersJson);
    	Pairing pairing = PairingSingleton.getPairing();
    	Element publicKey = ElementUtil.getElementByBase64(publicParametersIO.getPublicKeyBase64Base64(),PairingSingleton.getPairing().getG2());
//    	System.out.println("g1:" + ElementUtil.getElementByBase64(publicParametersIO.getG1Base64(),PairingSingleton.getPairing().getG1()));
//    	System.out.println("g2:" + ElementUtil.getElementByBase64(publicParametersIO.getG2Base64(),PairingSingleton.getPairing().getG2()));
//    	System.out.println("v:" + ElementUtil.getElementByBase64(publicParametersIO.getPublicKeyBase64Base64(),PairingSingleton.getPairing().getG2()));
//    	
    	String ret = HttpUtil.get("http://ouy.hyyas.com/movies.jsp");
    	ArrayList<Movie> movies = Movies.getMovies(ret);
    	ArrayList<Policy> policies = movies.get(0).getPolicies();
    	for (Policy policy: policies){
    		System.out.print(policy.getRequiredValue() + ":");
    		Element Vi = pairing.pairing(ElementUtil.getHashElement(policy.getRequiredValue()), publicKey);
    		System.out.println(Vi);
    	}
    }
}
