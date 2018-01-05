package ABPassNew.Model;

import ABPassNew.Utils.ElementUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

/**
 * Created by 41861 on 2017/6/27.
 */
public class HasPublicParametersBase {
    protected Pairing pairing;
    protected Element publicKey;
    protected Element g1;
    protected Element g2;
    protected PublicParametersIO publicParametersIO;

    public HasPublicParametersBase()
    {
        pairing = PairingSingleton.getPairing();
        publicParametersIO = PublicParametersIO.getInstanceFromFile("publicConfig.json");
        g1 = ElementUtil.getElementByBase64(publicParametersIO.getG1Base64(), pairing.getG1()).getImmutable();
        g2 = ElementUtil.getElementByBase64(publicParametersIO.getG2Base64(), pairing.getG2()).getImmutable();
        publicKey = ElementUtil.getElementByBase64(publicParametersIO.getPublicKeyBase64Base64(), pairing.getG2());
    }

    public HasPublicParametersBase(PublicParametersIO publicParametersIO){
    	pairing = PairingSingleton.getPairing();
        g1 = ElementUtil.getElementByBase64(publicParametersIO.getG1Base64(), pairing.getG1()).getImmutable();
        g2 = ElementUtil.getElementByBase64(publicParametersIO.getG2Base64(), pairing.getG2()).getImmutable();
        publicKey = ElementUtil.getElementByBase64(publicParametersIO.getPublicKeyBase64Base64(), pairing.getG2());
        this.publicParametersIO=publicParametersIO;
    }

    public void printParameters() {
        System.out.println(g1);
        System.out.println(g2);
        System.out.println(publicKey);
    }
    
    protected  void loadParameters(PublicParametersIO publicParametersIO) {
        g1 = ElementUtil.getElementByBase64(publicParametersIO.getG1Base64(), pairing.getG1());
        g2 = ElementUtil.getElementByBase64(publicParametersIO.getG2Base64(), pairing.getG2());
        publicKey = ElementUtil.getElementByBase64(publicParametersIO.getPublicKeyBase64Base64(), pairing.getZr());
        this.publicParametersIO=publicParametersIO;
    }
    
    public PublicParametersIO getPublicParametersIO() {
        return publicParametersIO;
    }

}
