package ABPassNew.Model;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

/**
 * Created by 41861 on 2017/6/20.
 */
public class PairingSingleton {
    private PairingSingleton(){
    }
    private static Pairing pairing=null;
    public static Pairing getPairing(){
        if(pairing==null){
            //PairingParametersGenerator a = new TypeACurveGenerator(160, 512);
            //PairingParametersGenerator f = new TypeFCurveGenerator(160);
            pairing= PairingFactory.getPairing("assets/f.properties");
        }
        return pairing;
    }
}
