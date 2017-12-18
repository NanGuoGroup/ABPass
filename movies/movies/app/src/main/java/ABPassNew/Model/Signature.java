package ABPassNew.Model;

import ABPassNew.Utils.ElementUtil;
import it.unisa.dia.gas.jpbc.Element;

import java.util.Date;

/**
 * Created by 41861 on 2017/6/21.
 */

public class Signature {

    private Element signature;

    private String signatureBase64;

    private AttributeEnum attribute;

    private Date signDate;

    private Date invalidDate;

    private Signature() {

    }


    public Signature(SignatureIO signatureIO){
        signature=ElementUtil.getElementByBase64(signatureIO.getSignatureBase64(),PairingSingleton.getPairing().getG1());
        signatureBase64=signatureIO.getSignatureBase64();
        attribute=signatureIO.getAttribute();
        signDate=signatureIO.getSignDate();
        invalidDate=signatureIO.getInvalidDate();
    }


    public Signature(Element sig, AttributeEnum attr, int validDay) {
        attribute = attr;
        signDate = new Date();
        long milliSecond = 1000 * 60 * 60 * 24 * validDay;
        invalidDate = new Date(signDate.getTime() + milliSecond);
        signature = sig;
        signatureBase64 = ElementUtil.getBase64OfElement(signature);
    }





    /**
     * 判断签名是否过期
     */
    public boolean isValid() {
        return getInvalidDate().after(new Date());
    }

    public Element getSignature() {
        return signature;
    }


    public String getSignatureBase64() {
        return signatureBase64;
    }


    public void setSignatureBase64(String signatureBase64){
        this.signatureBase64=signatureBase64;
        signature=ElementUtil.getElementByBase64(signatureBase64,PairingSingleton.getPairing().getG1());
    }

    public AttributeEnum getAttribute() {
        return attribute;
    }


    public Date getSignDate() {
        return signDate;
    }


    public Date getInvalidDate() {
        return invalidDate;
    }

    public SignatureIO toSignatureIO(){
        SignatureIO signatureIO=new SignatureIO();
        signatureIO.setSignatureBase64(this.signatureBase64);
        signatureIO.setAttribute(this.attribute);
        signatureIO.setInvalidDate(this.invalidDate);
        signatureIO.setSignDate(this.signDate);
        return signatureIO;
    }


}
