import java.util.ArrayList;
import ABPassNew.Model.*;
import ABPassNew.Verifier.*;
public class Test {
 public static void main (String[] args){
	 ArrayList<Policy> policies1 = new ArrayList<Policy> ();
	 policies1.add(new Policy(AttributeEnum.Identity, "DrivingLicense"));
     policies1.add(new Policy(AttributeEnum.Identity, "PassPort"));
     policies1.add(new Policy(AttributeEnum.Identity, "SocialSecurityNumber"));
     policies1.add(new Policy(AttributeEnum.MemberShip, "6_month"));
     policies1.add(new Policy(AttributeEnum.MemberShip, "1_year"));
    
     System.out.println(policies1);
     Movie movie1 = new Movie();
     movie1.setPolicies(policies1);	
     
     
 }
}
