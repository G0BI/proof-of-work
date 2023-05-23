import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PoW extends Thread {
    // SHA-256 hash generator
    public static String getSHA(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String hashtext = no.toString(16);
        while (hashtext.length() < 64) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    public void mining(String hash) throws NoSuchAlgorithmException {
        String miner = "SBF";
        String nonce = "";
        String hashNonce = hash + miner + nonce;
        String maxFound = "";
        String currentHash = "";
        BigInteger attempts = BigInteger.ONE;
        BigInteger attemptCount = BigInteger.ONE;

        while (true) {
            nonce = generateNonce();
            hashNonce = hash + miner + nonce;
            currentHash = getSHA(hashNonce);

            // stores the hash with the most zeros
            if (getZeros(currentHash) > getZeros(maxFound)) {
                maxFound = currentHash;
                System.out.println("Hash: " + maxFound + " length: " + getZeros(maxFound) + " nonce: " + nonce
                        + " Thread: " + Thread.currentThread().getId() + " Attempts: " + attempts); // prints the highest hash found
            }

            // if difficulty 40 is reached, break the loop
            if (getZeros(maxFound) > 13) {
                System.out.println(getZeros(maxFound));
                break;
            }

            attempts = attempts.add(BigInteger.ONE); // counts the number of attempts
            attemptCount = attemptCount.add(BigInteger.ONE); 

            // loop count of a billion to print the time so I know the script is still running when it's looking for the last hash
            if (attemptCount.equals(BigInteger.valueOf(1000000000))) {
                System.out.println("Thread: " + Thread.currentThread().getId() + " Attempts: " + attempts + " Time: " + LocalTime.now());
                attemptCount = BigInteger.ZERO;
            }
        }
    }

    // nonce generator for integers
    public int randomNonce(int randomNonce) {
        Random random = new Random();
        randomNonce = random.nextInt(1000000000);

        return randomNonce;
    }

    // nonce generator for characters
    public String generateNonce() {
        Random random = new Random();
        String ch = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int nonceLength = random.nextInt(64);

        StringBuilder sb = new StringBuilder(nonceLength);

        for (int i = 0; i < nonceLength; i++) {
            sb.append(ch.charAt(random.nextInt(ch.length())));
        }
        return sb.toString();
    }

    // counts the number of zeros in the hash
    public int getZeros(String hash) {
        Pattern pattern = Pattern.compile("^00000+");
        Matcher matcher = pattern.matcher(hash);

        if (matcher.find()) {
            String str = matcher.group();
            return str.length();
        }
        return 0;

    }

    // runs the thread
    public void run() {
        try {
            mining("0000000001ba1bd52cd6315be65690f58f2edc624ac4590c93c9cfbdbbec6e75");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        int n = 8;
        for (int i = 0; i < n; i++) {
            PoW pow = new PoW();
            pow.start();
        }

    }
}
