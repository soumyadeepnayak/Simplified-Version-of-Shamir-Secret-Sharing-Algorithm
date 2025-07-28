import Shamir.ShamirAlgo;
import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        System.out.println("Shamir's Secret Sharing - Polynomial Root Analysis");
        System.out.println("==================================================");
        
        ShamirAlgo shamir = new ShamirAlgo();
        
        try {
            // Process test1.json
            System.out.println("Processing test1.json...");
            BigInteger secret1 = shamir.processTestCase("test1.json");
            
            // Process test2.json
            System.out.println("Processing test2.json...");
            BigInteger secret2 = shamir.processTestCase("test2.json");
            
            System.out.println("==================================================");
            System.out.println("Final Results:");
            System.out.println("Secret from test1.json: " + secret1);
            System.out.println("Secret from test2.json: " + secret2);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
