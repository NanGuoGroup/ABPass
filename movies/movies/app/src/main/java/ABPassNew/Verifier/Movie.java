package ABPassNew.Verifier;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import ABPassNew.Model.AttributeEnum;
import ABPassNew.Model.Policy;

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
    //20171128增加TA，用于存储Vi
    ArrayList<String> TA = new ArrayList<String> ();

    ArrayList<Policy> policies=new ArrayList<Policy>();

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

    public void setPolicies(ArrayList<Policy> policies) {
        this.policies = policies;
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
    //20171128增加
    public ArrayList<String> getTA(){
        return this.TA;
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
