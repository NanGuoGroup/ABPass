package ABPassNew.Verifier;

import ABPassNew.Client.Client;
import ABPassNew.Model.AttributeEnum;
import ABPassNew.Model.PairingSingleton;
import ABPassNew.Model.Policy;
import ABPassNew.Model.PublicParametersIO;
import ABPassNew.Utils.ElementUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by 41861 on 2017/6/27.
 */
public class Movie {


    private String name;
    private String url;
    private String imgUrl;
    private String summary;
    private String warning;
    //20170707增加 给每个电影增加策略字符串
    private String policyStr;

    ArrayList<Policy> policies=new ArrayList<Policy>();  //20171128修改，将初始化方法放在setPolicies方法中
    //20171128增加TA，用于存储Vi
    ArrayList<String> TA;
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public ArrayList<Policy> getPolicies() {
        return policies;
    }

    //20171128删除
//    public void setPolicies(ArrayList<Policy> policies) {
//        this.policies = policies;
//    }
    
    //20171128修改
    @SuppressWarnings("unchecked")
	public void setPolicies(ArrayList<Policy> policies) {
        this.policies = (ArrayList<Policy>) policies.clone();
        
        setTA(policies);
    }
    //20171128增加
    public void setTA(ArrayList<Policy> policies){
    	Pairing pairing = PairingSingleton.getPairing();
    	PublicParametersIO publicParametersIO = PublicParametersIO.getInstanceFromFile("publicConfig.json");
    	Element publicKey = ElementUtil.getElementByBase64(publicParametersIO.getPublicKeyBase64Base64(), 
    													   			 PairingSingleton.getPairing().getG2());
    	this.TA = new ArrayList<String> ();
    	for (Policy policy: policies){
    		Element Vi = pairing.pairing(ElementUtil.getHashElement(policy.getRequiredValue()), publicKey);
    		String ViStr = ElementUtil.getBase64OfElement(Vi);
    		this.TA.add(ViStr);
    	}
    	
    }
    
    public void setTAByTA(ArrayList<String> TA){
    	this.TA = new ArrayList<String> (TA);
    }
    
    //20171128增加
    public ArrayList<String> getTA(){
    	return this.TA;
    }
    
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getWarning() {
        return warning;
    }

    public static ArrayList<Movie> getMovieListFromJson(String json){
        Gson gson=new Gson();
        return (ArrayList<Movie>)gson.fromJson(json, new TypeToken<ArrayList<Movie>>() {
        }.getType());
    }
    
    //20170707增加
    public String getPolicyStr() {
        return policyStr;
    }
    //20170707增加
    public void setPolicyStr(String policyStr) {
        this.policyStr = policyStr;
    }

    public static Movie getTestMovie() {

        Movie movie = new Movie();
        movie.name = "摔跤吧!爸爸";
        for (int year = 1940; year < 2001; year++) {
            String yearStr = Integer.toString(year);
            movie.getPolicies().add(new Policy(AttributeEnum.BirthYeah, yearStr));
        }
        movie.getPolicies().add(new Policy(AttributeEnum.Identity, "DrivingLicense"));
        movie.getPolicies().add(new Policy(AttributeEnum.Identity, "PassPort"));
        movie.getPolicies().add(new Policy(AttributeEnum.Identity, "SocialSecurityNumber"));
        movie.getPolicies().add(new Policy(AttributeEnum.MemberShip, "6_month"));
        movie.getPolicies().add(new Policy(AttributeEnum.MemberShip, "1_year"));

        return movie;
    }
}
