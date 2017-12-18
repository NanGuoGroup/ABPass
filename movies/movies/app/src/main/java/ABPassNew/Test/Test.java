package ABPassNew.Test;

import ABPassNew.Client.Client;
import ABPassNew.Model.Policy;
import ABPassNew.Model.PublicParametersIO;
import ABPassNew.Model.Signature;
import ABPassNew.Model.SignatureIO;
import ABPassNew.Server.Server;
import ABPassNew.Verifier.Movie;
import ABPassNew.Verifier.Verifier;

/**
 * Created by 41861 on 2017/6/27.
 */
public class Test {
    public static void main(String[] args) {
        Server server = new Server();


        String parametersJson=server.getPublicParametersIO().toJson();

        //模拟登陆
        Client client = new Client(PublicParametersIO.getInstance(parametersJson));
        client.getUser().setAttributeHashMapFromJson(server.getUser().getAttributeHashMapJson());

        Movie movie = Movie.getTestMovie();

        if (!client.meetPolicy(movie.getPolicies())) {
            System.out.println("用户不满足该策略.");
            return;
        }
        client.generateTts(server.getRandomCBase64(), "asdasdasd");

        if (server.verifyUser(client.getTBase64(), client.getsBase64())) {
            System.out.println("验证成功.");
            client.getUser().setToken(server.generateRandomToken());
        }
        ;


        for (Policy entry : movie.getPolicies()
                ) {

            if (!client.getUser().hasValidSignature(entry.getAttribute())) {
                String signatureJson = server.getSignatureJson(entry.getAttribute(), client.getUser().getToken());
                Signature signature = new Signature(SignatureIO.getInstance(signatureJson));
                client.getUser().getSignatureHashMap().put(signature.getAttribute(), signature);
            }
        }
        Verifier verifier = new Verifier(server.getPublicParametersIO());

        String proofBase64 = client.generateProofBase64(verifier.policiesToJson(movie.getPolicies()), "asdasdasd");
        String aggSig = client.getAggregateSignatureBase64(movie.getPolicies());

//        System.out.println(verifier.verify(proofBase64,aggSig));


    }
}
