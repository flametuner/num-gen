import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class Main {

    private static Random rand = new MWC(); // Aqui é definido qual random será utilizado, new Xorshift(), new MWC(), new Random(), new SecureRandom(), etc...

    public static void main(String[] args) {
        int k = 10;
        long start = System.currentTimeMillis();
        for(int i = 0; i < k; i++)
            System.out.println(generatePrimeNumber(4096));
        long end = System.currentTimeMillis();
        long avarage = (end - start) / k;
        System.out.println(avarage + " ms");
    }

    public static BigInteger generatePrimeNumber(int bits) {
        boolean probablyPrime = false;
        BigInteger integer = null;
        while (!probablyPrime) {
            integer = new BigInteger(bits, rand);
            integer = integer.setBit(0);
            integer = integer.setBit((int) ((bits + 7) / 8));
            probablyPrime = isPrime(integer);
        }
        return integer;
    }


    public static boolean isPrime(BigInteger n) {
        return millerRabin(n, 3) && fermatTest(n, 20);
    }


    public static boolean millerRabin(BigInteger n, int iteration) {
        if (n.equals(BigInteger.ZERO) || n.equals(BigInteger.ONE))
            return false;
        if (n.equals(BigInteger.TWO))
            return true;
        if (n.remainder(BigInteger.TWO).equals(BigInteger.ZERO))
            return false;
        BigInteger s = BigInteger.ZERO;
        BigInteger r = n.subtract(BigInteger.ONE);
        while (r.and(BigInteger.ONE).equals(BigInteger.ZERO)) {
            s = s.add(BigInteger.ONE);
            r = r.divide(BigInteger.TWO);
        }
        for (int i = 0; i < iteration; i++) {
            BigInteger a = BigInteger.TWO.add(new BigInteger(n.bitLength(), rand).remainder(n.subtract(BigInteger.ONE.add(BigInteger.TWO))));
            BigInteger x = a.modPow(r, n);
            if (!x.equals(BigInteger.ONE) && !x.equals(n.subtract(BigInteger.ONE))) {
                BigInteger j = BigInteger.ONE;
                while (j.compareTo(s) < 0 && !x.equals(n.subtract(BigInteger.ONE))) {
                    x = x.modPow(BigInteger.TWO, n);
                    if (x.equals(BigInteger.ONE))
                        return false;
                    j = j.add(BigInteger.ONE);
                }
                if (!x.equals(n.subtract(BigInteger.ONE)))
                    return false;
            }
        }
        return true;
    }

    public static boolean fermatTest(BigInteger n, int iteration) {
        if (n.equals(BigInteger.ZERO) || n.equals(BigInteger.ONE))
            return false;
        if (n.equals(BigInteger.TWO))
            return true;
        if (n.remainder(BigInteger.TWO).equals(BigInteger.ZERO))
            return false;

        for (int i = 0; i < iteration; i++) {
            BigInteger a = getRandomA(n);
            if (!a.modPow(n.subtract(BigInteger.ONE), n).equals(BigInteger.ONE))
                return false;
        }
        return true;
    }

    private static BigInteger getRandomA(BigInteger n) {
        while (true) {
            BigInteger a = new BigInteger(n.bitLength(), rand);
            // Deve ser entre 1 <= a < n
            if (a.compareTo(BigInteger.ONE) >= 0 && a.compareTo(n) < 0) {
                return a;
            }
        }
    }

    public static class MWC extends Random {
        final long a = 0xffffda61L;
        long x = System.nanoTime() & 0xffffffffL;


        @Override
        public int nextInt() {
            x = (a * (x & 0xffffffffL)) + (x >>> 32);
            return (int) x;
        }

    }


    public static class Xorshift extends Random {

        int x = (int) System.nanoTime();

        @Override
        public int nextInt() {
            x ^= x << 13;
            x ^= x >> 7;
            x ^= x << 17;
            return x;
        }
    }

}
