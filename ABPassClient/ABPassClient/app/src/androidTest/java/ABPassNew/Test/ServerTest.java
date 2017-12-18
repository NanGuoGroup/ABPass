package ABPassNew.Test;

import ABPassNew.Client.Client;
import ABPassNew.Model.AttributeEnum;
import ABPassNew.Model.PublicParametersIO;
import ABPassNew.Model.User;
import ABPassNew.Server.Server;
import ABPassNew.Verifier.Movie;

/**
 * Created by 41861 on 2017/6/27.
 */
public class ServerTest {

    public static void main(String[] args) {
        String publicParametersJson = Client.requestPublicParameters();
        PublicParametersIO publicParametersIO = PublicParametersIO.getInstance(publicParametersJson);
        Client client = new Client(publicParametersIO);
        client.requestSignIn("ouyang", "asdasd");
        Movie movie = Movie.getTestMovie();
        client.requestCAndgenerateTts("asdasdasd");
        client.requestVerify();
        client.requestSignature(AttributeEnum.BirthYeah);
        client.requestSignature(AttributeEnum.BirthPlace);

    }
}
