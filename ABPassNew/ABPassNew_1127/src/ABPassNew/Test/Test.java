package ABPassNew.Test;

import ABPassNew.Client.Client;
import ABPassNew.Model.*;
import ABPassNew.Server.Server;
import ABPassNew.Utils.ElementUtil;
import ABPassNew.Verifier.Movie;
import ABPassNew.Verifier.Verifier;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by 41861 on 2017/6/27.
 */
public class Test {
    public static void main(String[] args) {
    	Pairing pairing = PairingSingleton.getPairing();
    	
        Element privateKey = pairing.getZr().newRandomElement().getImmutable(); 	
    	Element r = pairing.getZr().newRandomElement().getImmutable();
        Element g1 = pairing.getG1().newRandomElement().getImmutable();
        Element M = g1.powZn(r).getImmutable();
    	
    	double[] feature = new double[]{-0.120712, 0.202998, 0.128704, -0.120796,
    			-0.0879383, 0.0124032, -0.095876, -0.0473592, 0.187803, -0.0369501, 
    			 0.270662, -0.0032912, -0.220344, -0.0735786, 0.0295736, 0.177021, 
    			-0.213383, -0.119534,  -0.0503222,-0.0279117, 0.0469026, -0.0277145, 
    			 0.0779783, 0.0680332, -0.136235, -0.265637, -0.0755324, -0.140073, 
    			 0.0306728, -0.119969, -0.0554971,-0.13337,  -0.214156, -0.091157, 
    			 0.00153446, 0.0656872,-0.0297139,-0.0860404, 0.187859, -0.0366233, 
    			-0.0366233, -0.171423,  0.0143982,-0.0278889, 0.189925, 0.1674, 
    			 0.0476937,  0.144078, -0.0585164, 0.0658821, -0.175722, 0.103686, 
    			 0.131773, 0.0906367, 0.0338614, 0.0249025, -0.174803, -0.0293371, 
    			 0.257405, -0.141037, 0.0825213, 0.105506, -0.0693805, -0.0555286, 
    			-0.0333683, 0.28, 0.172897, -0.147932, -0.219472, 0.106573, -0.0422057,
    			-0.0668778, 0.0421475, -0.168111, -0.120643, -0.299988, 0.0355726, 0.330169,
    			0.0631577, -0.251872, -0.0444571, -0.148759, 0.0948411, -0.0100398,
    			0.0861672, -0.0759868, 0.0442143, -0.120158, 0.00845036, 0.151873,
    			-0.005978, -0.0300295, 0.149843, -0.0288533, 0.0316538, 0.00247569,
    			0.0499104, -0.12228, -0.0261523, -0.0722644, -0.0286182, 0.0310265,
    			-0.0231644, -0.0487185, 0.124507, -0.2273, 0.126952, -0.0622857,
    			-0.0135036, -0.0641059, 0.0265501, -0.0313924, -0.0263191, 0.179586,
    			-0.245026, 0.239454, 0.257873, 0.00451807, 0.134975, 0.0813824,
    			0.0596384, -0.0151435, 0.0269027, -0.177885, -0.0582218, 0.0272956,
    			-0.029679, 0.139955,};
    	StringBuffer featureSB = new StringBuffer();
    	for (int i = 0; i < feature.length; i++){
    		featureSB.append(feature[i]);
    		if ((feature.length - 1) == i){
    			break;
    		}else{
    			featureSB.append(',');
    		}
    	}
    	String featureStr = new String(featureSB);
    	System.out.println(featureStr);
    	
        Element h = ElementUtil.getHashElement(featureStr);
        Element sigElement = h.mul(M).powZn(privateKey);
        System.out.println(sigElement);
    }
}
