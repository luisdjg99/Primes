//Luis D Jimenez

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Primes
{
    
    public static final int maxValue = 100000000; 
    public static final int threadsAmount = 8; 
    

    
    public static boolean sieveArray[] = new boolean[maxValue];
    public static boolean finishedFirstSection[] = new boolean[threadsAmount];
    public static long totalPrimes = 0;
    public static long sumOfPrimes = 0;

    
    private List<findPrimes> primeObject;
    private List<Thread> primeThreads;

    
    public static void main(String args[])
    {
        Primes superThread = new Primes();
        

        //System.out.println("Initializing sieve array ...");
        for (int i = 0; i < maxValue; i++)
        {
            // add 2 and 3 to primes
            if(i == 2 || i == 3){
                superThread.sieveArray[i] = true;
            }
            //initialize everything to false
            else{
                superThread.sieveArray[i] = false;
            }
        }


        
        superThread.primeObject = new ArrayList<>();
        superThread.primeThreads = new ArrayList<>();
        for(int i = 0; i < threadsAmount; i++)
        {
            //System.out.println("Creating Thread: " + i + "...");
            findPrimes primeCalculator = new findPrimes(superThread, i, maxValue);
            superThread.primeObject.add(primeCalculator);
            Thread th = new Thread(primeCalculator);
            superThread.primeThreads.add(th);
        }
        
        long startClock = System.nanoTime();
        
        for (Thread i : superThread.primeThreads)
        {
            i.start();
            //System.out.println(i.getName() + " started");
        }
        
        
        for (Thread i : superThread.primeThreads)
        {
            try
            {
                i.join();
            }
            catch(Exception e)
            {
                System.out.println("[Exception]: " + e);
            }
        }

        //System.out.println("All threads have finished !");

        
        long endClock = System.nanoTime();
        
        
        for (Thread i : superThread.primeThreads)
        {
            i.interrupt();
        }

        //System.out.println("All threads have been successfully terminated !");


        long executionTime = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(endClock - startClock);


        for (int i = 0; i < maxValue; i++)
        {
            if (sieveArray[i] == true)
            {
                totalPrimes++;
                sumOfPrimes += i;
            }
        }

        // writing data
        //System.out.println("Writing data ...");
        try
        {
            File fp = new File("primes.txt");
            fp.createNewFile();
            FileWriter w = new FileWriter("primes.txt");
            w.write(executionTime + "ms " + totalPrimes + " " + sumOfPrimes + "\n");


            int printedPrimes = 0;
            int[] topTenPrimes = new int[10];
            for (int i = maxValue - 1; i > 0 ; i--)
            {
                if (sieveArray[i] == true)
                {
                    // Add the primes to an array
                    topTenPrimes[printedPrimes] = i;
                    printedPrimes++;
                }
                if(printedPrimes == 10){
                    break;
                }
            }
            for (int j = 9; j >= 0; j--)
            {

                w.write(topTenPrimes[j] + " ");
            }

            w.close();
        }
        catch (IOException e)
        {
            System.out.println("[IOException]: ");
            e.printStackTrace();
        }
    }
}

class findPrimes implements Runnable
{
    
    private Primes superThread;
    private int threadIndex;
    private int maxValue;

    // constructor
    public findPrimes(Primes superThread, int threadIndex, int inputMaxValue)
    {
        this.superThread = superThread;
        this.threadIndex = threadIndex;
        this.maxValue = inputMaxValue;
    }

    
    @Override
    public void run()
    {
        try
        {
            //Start calculating Primes
            atkinSieve(superThread, threadIndex, maxValue);
        }
        catch (Exception e)
        {
            System.out.println("Exception: " + e);
        }
    }

    
    public void atkinSieve(Primes superThread, int threadIndex, int maxValue)
    {
        
        for (int x = 1 + threadIndex; x * x < maxValue; x += superThread.threadsAmount)
        {
            for (int y = 1; y * y < maxValue; y++)
            {

                int n = (4 * x * x) + (y * y);
                if (n <= maxValue && (n % 12 == 1 || n % 12 == 5))
                {

                    synchronized(this){
                        this.superThread.sieveArray[n] ^= true;
                    }


                }

                n = (3 * x * x) + (y * y);
                if (n <= maxValue && n % 12 == 7)
                {
                    synchronized(this){
                        this.superThread.sieveArray[n] ^= true;
                    }

                }

                n = (3 * x * x) - (y * y);
                if (x > y && n <= maxValue && n % 12 == 11)
                {
                    synchronized(this){
                        this.superThread.sieveArray[n] ^= true;
                    }
                }
            }
        }

        
        superThread.finishedFirstSection[threadIndex] = true;

        // Wait until all threads are finished with the first part
        boolean firstSectionFinished = false;
        
        while(firstSectionFinished == false)
        {
            try
            {
                Thread.sleep(150);
            }
            catch (Exception e)
            {

            }
            firstSectionFinished = true;
            for (int i = 0; i < superThread.threadsAmount; i++)
            {
                if (superThread.finishedFirstSection[i] == false)
                {
                    firstSectionFinished = false;
                    break;
                }
            }
        }
        

        // Flag all multiples of squares as non-prime
        for (int r = 5 + threadIndex; r * r < maxValue; r += superThread.threadsAmount)
        {
            synchronized(this){
                if (this.superThread.sieveArray[r])
                {
                    for (int i = r * r; i < maxValue; i += r * r)
                    {
                        if (this.superThread.sieveArray[i] == true)
                        {
                            this.superThread.sieveArray[i] = false;
                        }
                    }
                }
            }
        }
    }
}
