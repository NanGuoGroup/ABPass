package ABPassNew.Server;


import ABPassNew.Model.AttributeEnum;
import ABPassNew.Model.HasPublicParametersBase;
import ABPassNew.Model.PrivateParametersIO;
import ABPassNew.Model.PublicParametersIO;
import ABPassNew.Model.Signature;
import ABPassNew.Model.User;
import ABPassNew.Utils.ElementUtil;
import it.unisa.dia.gas.jpbc.Element;


/**
 * Created by 41861 on 2017/6/27.
 */
public class Server extends HasPublicParametersBase {
    private Element privateKey;
    private Element c;
    private PrivateParametersIO privateParametersIO;
    private User user = new User();


    public Server() {
//      resetParameters();
    	super();	
    	privateParametersIO = PrivateParametersIO.getInstanceFromFile("privateConfig.json");
    	privateKey = ElementUtil.getElementByBase64(privateParametersIO.getPrivateKeyBase64(), pairing.getZr());
    	Element r = ElementUtil.getrByPassword("asdasdasd");
    	user.setM(g1.powZn(r).getImmutable());
        setUserTestAttributes();
    }

    public String getRandomCBase64() {
        c = pairing.getZr().newRandomElement();
        return ElementUtil.getBase64OfElement(c);
    }

    public boolean signUserAttribute(AttributeEnum attributeEnum) {
        String message = user.getAttributeHashMap().get(attributeEnum);
        Element h = ElementUtil.getHashElement(message);
        Element sigElement = h.mul(user.getM()).powZn(privateKey);
        Signature signature = new Signature(sigElement, attributeEnum, 1);
        user.getSignatureHashMap().put(attributeEnum, signature);
        return true;
    }
    public String getSignatureJson(AttributeEnum attributeEnum, String accessToken) {
        if (!accessToken .equals(user.getToken())) {
            return "InvalidAccessToken";
        }
        if (!user.hasValidSignature(attributeEnum)) {
            signUserAttribute(attributeEnum);
        }
        return user.getSignatureHashMap().get(attributeEnum).toSignatureIO().toJson();
    }


    public boolean verifyUser(String TBase64, String sBase64) {

        if (c == null) {
            System.out.println("server->验证用户失败:没有生成c");
            return false;
        }
        Element T = ElementUtil.getElementByBase64(TBase64, pairing.getG1()).getImmutable();
        Element s = ElementUtil.getElementByBase64(sBase64, pairing.getZr()).getImmutable();
        Element left = g1.powZn(s).getImmutable();
        Element right = user.getM().powZn(c).mul(T).getImmutable();
        return left.equals(right);
    }

    public String generateRandomToken() {
        user.setToken(java.util.UUID.randomUUID().toString());
        return user.getToken();
    }

    private void resetParameters() {
        g1 = pairing.getG1().newRandomElement().getImmutable();
        g2 = pairing.getG2().newRandomElement().getImmutable();
        privateKey = pairing.getZr().newRandomElement().getImmutable();
        publicKey = g2.powZn(privateKey).getImmutable();
        publicParametersIO.setG1Base64(ElementUtil.getBase64OfElement(g1));
        publicParametersIO.setG2Base64(ElementUtil.getBase64OfElement(g2));
        publicParametersIO.setPublicKeyBase64(ElementUtil.getBase64OfElement(publicKey));
        publicParametersIO.saveParameters("publicConfig.json");
        
        privateParametersIO.setPrivateKeyBase64(ElementUtil.getBase64OfElement(privateKey));
        privateParametersIO.saveParameters("privateConfig.json");
        
        Element r = ElementUtil.getrByPassword("asdasdasd");
        user.setM(g1.powZn(r).getImmutable());
    }

    public void setUserTestAttributes() {
        user.getAttributeHashMap().put(AttributeEnum.BirthYeah, "1990");
        user.getAttributeHashMap().put(AttributeEnum.Doctor, "Doctor");
        user.getAttributeHashMap().put(AttributeEnum.Professor, "Professor");
        user.getAttributeHashMap().put(AttributeEnum.Teacher, "Teacher");
        user.getAttributeHashMap().put(AttributeEnum.Identity, "DrivingLicense");
        user.getAttributeHashMap().put(AttributeEnum.Sex, "Female");
        user.getAttributeHashMap().put(AttributeEnum.Nationality, "Chinese");
        user.getAttributeHashMap().put(AttributeEnum.CardType, "EID");
        user.getAttributeHashMap().put(AttributeEnum.BirthPlace, "Beijing");
        user.getAttributeHashMap().put(AttributeEnum.IssuancePlace, "Tianjin");
        user.getAttributeHashMap().put(AttributeEnum.EyeColor, "Brown");
        user.getAttributeHashMap().put(AttributeEnum.HairColor, "Black");
        user.getAttributeHashMap().put(AttributeEnum.VisualStatus, "VisualHealthy");
        user.getAttributeHashMap().put(AttributeEnum.HearStatus, "HearHealthy");
        user.getAttributeHashMap().put(AttributeEnum.PhysicalStatus, "PhysicalHealthy");
        user.getAttributeHashMap().put(AttributeEnum.SocialBenefitExist, "SocialBenefitExist");
        user.getAttributeHashMap().put(AttributeEnum.SocialBenefitStatus, "Unemployed");
        user.getAttributeHashMap().put(AttributeEnum.MS, "MS");
        user.getAttributeHashMap().put(AttributeEnum.BA, "BA");
        user.getAttributeHashMap().put(AttributeEnum.MemberShip, "1_year");
    }



    public User getUser() {
        return user;
    }
}
