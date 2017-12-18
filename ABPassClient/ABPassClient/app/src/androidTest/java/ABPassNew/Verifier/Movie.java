package ABPassNew.Verifier;

import ABPassNew.Model.AttributeEnum;
import ABPassNew.Model.Policy;

import java.util.ArrayList;

/**
 * Created by 41861 on 2017/6/27.
 */
public class Movie {

    private String name;
    private String url;
    private String imgUrl;
    private String summary;

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
