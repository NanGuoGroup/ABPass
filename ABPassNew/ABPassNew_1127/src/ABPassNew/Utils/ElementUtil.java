package ABPassNew.Utils;

import ABPassNew.Model.PairingSingleton;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.util.io.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 41861 on 2017/6/23.
 */
public class ElementUtil {

    private static Pairing pairing= PairingSingleton.getPairing();
    public static String getBase64OfElement(Element element){
        try {
            return Base64.encodeBytes(element.toBytes(),Base64.URL_SAFE);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Element getElementByBase64(String elementBase64, Field field){
        try {
            return field.newElementFromBytes(Base64.decode(elementBase64,Base64.URL_SAFE));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Element getHashElement(String message){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] messageBytes = new byte[0];
        try {
            messageBytes = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        md.reset();
        md.update(messageBytes);
        byte[] messageHash = md.digest();
        Element h = pairing.getG1().newElementFromHash(messageHash, 0, messageHash.length);
        return h;

    }

    public static Element getrByPassword(String userPassword){
        Element r = null;//通过密码生成r
        try {
            r = pairing.getZr().newElementFromBytes(userPassword.getBytes("UTF-8")).getImmutable();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return r;
    }
}
