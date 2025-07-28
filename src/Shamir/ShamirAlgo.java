package Shamir;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

public class ShamirAlgo {
    
    // Data class to hold polynomial root information
    public static class PolynomialRoot {
        public int x;
        public BigInteger y;
        public String base;
        public String encodedValue;
        
        public PolynomialRoot(int x, BigInteger y, String base, String encodedValue) {
            this.x = x;
            this.y = y;
            this.base = base;
            this.encodedValue = encodedValue;
        }
        
        @Override
        public String toString() {
            return "(" + x + ", " + y + ") [base: " + base + ", encoded: " + encodedValue + "]";
        }
    }
    
    // Data class to hold test case configuration
    public static class TestCase {
        public int n; // number of roots provided
        public int k; // minimum number of roots required
        public int m; // degree of polynomial (m = k - 1)
        public Map<String, Map<String, String>> roots; // JSON structure for roots
        
        public TestCase() {
            this.roots = new HashMap<>();
        }
    }
    
    // Decode value from given base
    public BigInteger decodeFromBase(String value, String base) {
        int baseInt = Integer.parseInt(base);
        return new BigInteger(value, baseInt);
    }
    
    // Parse JSON input from file
    public TestCase parseJsonInput(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        return parseJsonString(content.toString());
    }
    
    // Parse JSON string to extract test case data
    public TestCase parseJsonString(String jsonString) {
        TestCase testCase = new TestCase();
        
        // Remove whitespace for easier parsing
        jsonString = jsonString.replaceAll("\\s+", "");
        
        // Extract n and k from the "keys" object
        Pattern nPattern = Pattern.compile("\"n\":(\\d+)");
        var nMatcher = nPattern.matcher(jsonString);
        if (nMatcher.find()) {
            testCase.n = Integer.parseInt(nMatcher.group(1));
        }
        
        Pattern kPattern = Pattern.compile("\"k\":(\\d+)");
        var kMatcher = kPattern.matcher(jsonString);
        if (kMatcher.find()) {
            testCase.k = Integer.parseInt(kMatcher.group(1));
            testCase.m = testCase.k - 1; // degree = k - 1
        }
        
        // Extract roots from the JSON structure
        // Pattern to match: "2": {"base": "2", "value": "111"}
        Pattern rootPattern = Pattern.compile("\"(\\d+)\":\\{\"base\":\"([^\"]+)\",\"value\":\"([^\"]+)\"\\}");
        var rootMatcher = rootPattern.matcher(jsonString);
        
        while (rootMatcher.find()) {
            String xStr = rootMatcher.group(1);
            String base = rootMatcher.group(2);
            String value = rootMatcher.group(3);
            
            int x = Integer.parseInt(xStr);
            BigInteger y = decodeFromBase(value, base);
            
            // Store in the roots map structure
            Map<String, String> rootData = new HashMap<>();
            rootData.put("base", base);
            rootData.put("value", value);
            testCase.roots.put(xStr, rootData);
        }
        
        return testCase;
    }
    
    // Convert test case to list of polynomial roots
    public List<PolynomialRoot> getPolynomialRoots(TestCase testCase) {
        List<PolynomialRoot> roots = new ArrayList<>();
        
        for (Map.Entry<String, Map<String, String>> entry : testCase.roots.entrySet()) {
            String xStr = entry.getKey();
            Map<String, String> rootData = entry.getValue();
            
            int x = Integer.parseInt(xStr);
            String base = rootData.get("base");
            String encodedValue = rootData.get("value");
            BigInteger y = decodeFromBase(encodedValue, base);
            
            roots.add(new PolynomialRoot(x, y, base, encodedValue));
        }
        
        // Sort by x value for consistent processing
        roots.sort((a, b) -> Integer.compare(a.x, b.x));
        return roots;
    }
    
    // Find the constant term (secret) using Lagrange interpolation
    public BigInteger findConstantTerm(List<PolynomialRoot> roots) {
        if (roots.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 roots to interpolate");
        }
        
        BigInteger constantTerm = BigInteger.ZERO;
        
        for (int i = 0; i < roots.size(); i++) {
            PolynomialRoot root = roots.get(i);
            BigInteger xi = BigInteger.valueOf(root.x);
            BigInteger yi = root.y;
            
            // Calculate Lagrange coefficient
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            
            for (int j = 0; j < roots.size(); j++) {
                if (i != j) {
                    BigInteger xj = BigInteger.valueOf(roots.get(j).x);
                    // numerator *= (-xj)
                    numerator = numerator.multiply(xj.negate());
                    // denominator *= (xi - xj)
                    denominator = denominator.multiply(xi.subtract(xj));
                }
            }
            
            // Calculate Lagrange coefficient: numerator / denominator
            // Note: We need to handle division properly for large numbers
            BigInteger lagrangeCoeff;
            if (denominator.equals(BigInteger.ZERO)) {
                lagrangeCoeff = BigInteger.ZERO;
            } else {
                lagrangeCoeff = numerator.divide(denominator);
            }
            
            constantTerm = constantTerm.add(yi.multiply(lagrangeCoeff));
        }
        
        return constantTerm;
    }
    
    // Process test case and find the secret
    public BigInteger processTestCase(String filePath) throws IOException {
        TestCase testCase = parseJsonInput(filePath);
        List<PolynomialRoot> roots = getPolynomialRoots(testCase);
        
        System.out.println("Test Case from: " + filePath);
        System.out.println("n = " + testCase.n + ", k = " + testCase.k + ", m = " + testCase.m);
        System.out.println("Polynomial roots:");
        for (PolynomialRoot root : roots) {
            System.out.println("  " + root);
        }
        
        BigInteger secret = findConstantTerm(roots);
        System.out.println("Secret (constant term): " + secret);
        System.out.println();
        
        return secret;
    }
    
    // Main method to process both test cases
    public static void main(String[] args) {
        ShamirAlgo shamir = new ShamirAlgo();
        
        try {
            System.out.println("Shamir's Secret Sharing - Polynomial Root Analysis");
            System.out.println("==================================================");
            
            // Process test1.json
            BigInteger secret1 = shamir.processTestCase("test1.json");
            
            // Process test2.json
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