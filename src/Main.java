import java.math.BigInteger;
import java.util.Random;

import static java.math.BigInteger.*; // Importa estaticamente ZERO, ONE, TWO, etc

public class Main {

    private static Random rand = new MWC(); // Aqui é definido qual random será utilizado, new Xorshift(), new MWC(), new Random(), new SecureRandom(), etc...

    public static void main(String[] args) {

        BigInteger a = new BigInteger("4294957665");
        BigInteger b = new BigInteger("4294967296");
        // Testando valores de MWC
        System.out.println(isPrime(a.multiply(b).subtract(ONE)));
        System.out.println(isPrime(a.multiply(b).subtract(ONE).divide(TWO)));

        int k = 10;
        int bits = 40;
        averageTiming(k, bits);

        bits = 56;
        averageTiming(k, bits);

        bits = 80;
        averageTiming(k, bits);

        bits = 128;
        averageTiming(k, bits);

        bits = 168;
        averageTiming(k, bits);

        bits = 224;
        averageTiming(k, bits);

        bits = 256;
        averageTiming(k, bits);

        bits = 512;
        averageTiming(k, bits);

        bits = 1024;
        averageTiming(k, bits);

        bits = 2048;
        averageTiming(k, bits);

        bits = 4096;
        averageTiming(k, bits);
    }

    private static void averageTiming(int k, int bits) {
        long start;
        long end;
        long average;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        long local = 0;
        long sum = 0;
        BigInteger prime = null;
        for(int i = 0; i < k; i++) {
            start = System.currentTimeMillis();
            prime = generatePrimeNumber(bits);
            end = System.currentTimeMillis();
            local = end - start;
            if(local < min)
                min = local;
            if (local > max)
                max = local;
            sum += local;
            System.out.println(prime);
        }
        average = sum / k;
        System.out.println("Min/Average/Max (" + bits + " bits): " + min + "/" + average + "/" + max + " ms");
    }

    public static BigInteger generatePrimeNumber(int bits) {
        boolean probablyPrime = false;
        BigInteger integer = null;
        while (!probablyPrime) {
            integer = new BigInteger(bits, rand); // geração de um numero aleatório com a quantidade de bits desejada.
            integer = integer.setBit(0); // deve ser impar
            integer = integer.setBit((int) ((bits + 7) / 8)); // deve ter n bits
            probablyPrime = isPrime(integer);
        }
        return integer;
    }


    public static boolean isPrime(BigInteger n) {
        // Checkagem com 3 iterações miller-rabin
        // Checkagem com 20 iterações do teste de fermat
        return millerRabin(n, 3) && fermatTest(n, 20);
    }


    public static boolean millerRabin(BigInteger n, int iteration) {
        // algumas checkagens padrões antes de começar o método
        if (n.equals(ZERO) || n.equals(ONE))
            return false;
        // se for 2, é primo
        if (n.equals(TWO))
            return true;
        // se for divisivel por 2, não é primo
        if (n.remainder(TWO).equals(ZERO))
            return false;

        BigInteger nMinus1 = n.subtract(ONE);

        BigInteger s = ZERO; // s = 0
        BigInteger r = nMinus1; // r = n -1


        while (r.and(ONE).equals(ZERO)) { // enquanto r for par
            s = s.add(ONE);
            r = r.divide(TWO);
        }
        for (int i = 0; i < iteration; i++) {
            BigInteger a = new BigInteger(n.bitLength(), rand) // a deve ser numero aleatório [2, n-1]
                    .remainder(nMinus1.subtract(TWO)).add(TWO);
            BigInteger x = a.modPow(r, n);
            if (!x.equals(ONE) && !x.equals(nMinus1)) { // if (x != 1 && x != n-1)
                BigInteger j = ONE;
                while (j.compareTo(s) < 0 && !x.equals(nMinus1)) { // while (j < s && x != n-1)
                    x = x.modPow(TWO, n);
                    if (x.equals(ONE)) // if x == 1
                        return false;
                    j = j.add(ONE);
                }
                if (!x.equals(nMinus1))
                    return false;
            }
        }
        return true;
    }

    public static boolean fermatTest(BigInteger n, int iteration) {
        // algumas checkagens padrões antes de começar o método
        if (n.equals(ZERO) || n.equals(ONE))
            return false;
        // se for 2, é primo
        if (n.equals(TWO))
            return true;
        // se for divisivel por 2, não é primo
        if (n.remainder(TWO).equals(ZERO))
            return false;

        for (int i = 0; i < iteration; i++) {
            BigInteger a = getRandomA(n);
            if (!a.modPow(n.subtract(ONE), n).equals(ONE))
                return false;
        }
        return true;
    }


    /**
     *  Pega um numero aleatório de 1 <= a < n
     * @param n
     * @return a
     */

    private static BigInteger getRandomA(BigInteger n) {
        while (true) {
            BigInteger a = new BigInteger(n.bitLength(), rand);
            // Deve ser entre 1 <= a < n
            if (a.compareTo(ONE) >= 0 && a.compareTo(n) < 0) {
                return a;
            }
        }
    }

    public static class MWC extends Random {

        // valor escolhido deve ser que a*b - 1 e (a*b-1)/2 sejam primos

        // valor de b = 2^32 = 4294967296
        // valor de a em decimal: 4294957665
        final long a = 0xffffda61L; // primeiro "bom numero" sugerido pelo livro 'Numerical Recipes'
        long x = System.nanoTime() & 0xffffffffL; // Usando os primeiros 32 bits do nanoTime() para seed inicial

        @Override
        public int nextInt() {


            x = (a * (x & 0xffffffffL)) + (x >>> 32);
            return (int) x;
        }

    }


    public static class Xorshift extends Random {

        int x = (int) System.nanoTime();  // Usando os primeiros 32 bits do nanoTime() para seed inicial

        /**
         *  numeros considerados padrões para um bom Xorshift 13, 7, 17
         * @return
         */

        @Override
        public int nextInt() {
            x ^= x << 13;
            x ^= x >> 7;
            x ^= x << 17;
            return x;
        }
    }

}
