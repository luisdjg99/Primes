# Primes Calculator

This is a java program to calculate how many primes exist from 1 to 100 000 000 , using 8 threads. This can be viewed as an introduction to the advantages of multiprocessing, as doing the same task using 1 thread will take hours, while in this case is done within seconds. This is where Atkin Sieve algorithm comes into play, as it preliminarily cancels out factors of primes before proceeding to finding new prime numbers and this significantly helps the computation time. In my approach, all threads start at their same index as they started and work their way until maxValue has been reached or passed. The use of 'synchronized' allows for mutual exclusion and each thread is able to modify the sieve array, before all other threads try to modify it. This way, when a thread finished the process, it jumps to the next entry in the array that is false and prevents overlap. The runtime for this version is roughly 500 - 600 ms.

Atkin Sieve algorithm was used from the following link:

https://www.geeksforgeeks.org/sieve-of-atkin


### Commands to run from terminal:
  
  - javac Primes.java  
  - java Primes  
