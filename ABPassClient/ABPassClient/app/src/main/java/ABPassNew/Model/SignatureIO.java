package ABPassNew.Model;

import com.google.gson.Gson;

import java.util.Date;

/**
 * Created by 41861 on 2017/6/27.
 */
public class SignatureIO extends IOBase {
    private String signatureBase64;

    private AttributeEnum attribute;

    private Date signDate;

    private Date invalidDate;

    public SignatureIO(){};


    public static SignatureIO getInstance(String json){
        Gson gson=new Gson();
        SignatureIO signatureIO=gson.fromJson(json,SignatureIO.class);
        return signatureIO;
    }

    public String getSignatureBase64() {
        return signatureBase64;
    }

    public void setSignatureBase64(String signatureBase64) {
        this.signatureBase64 = signatureBase64;
    }

    public AttributeEnum getAttribute() {
        return attribute;
    }

    public void setAttribute(AttributeEnum attribute) {
        this.attribute = attribute;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public Date getInvalidDate() {
        return invalidDate;
    }

    public void setInvalidDate(Date invalidDate) {
        this.invalidDate = invalidDate;
    }
}
