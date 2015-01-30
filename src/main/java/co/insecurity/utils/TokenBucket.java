package co.insecurity.utils;

import java.util.Scanner;

public class TokenBucket {

	private double capacity;
	private double fillRate;
	private double tokens;
	private long lastCheck;
	
	public TokenBucket(double capacity, double fillRate) {
		this.capacity = capacity;
		this.fillRate = fillRate;
		this.tokens = capacity;
		this.lastCheck = System.currentTimeMillis();
	}
	
	private double getTokens() {
		long now = System.currentTimeMillis();
		if (tokens < capacity) {
			tokens += (fillRate * (now - lastCheck)) / 1000;
			tokens = Math.min(tokens, capacity);
		}
		lastCheck = now;
		return tokens;
	}
	
	public boolean removeTokens(double numTokens) {
		double availableTokens = this.getTokens();
		if (numTokens <= availableTokens) {
			tokens = availableTokens - numTokens;
			return true;
		} else
			return false;
	}
	
	@Override
	public String toString() {
		return String.format(
				"TokenBucket[capacity=%.2f \tfill_rate=%.2f \ttokens=%.2f]", 
				capacity, fillRate, this.getTokens());
	}
	
	public static void main(String[] args) {
		TokenBucket tb = new TokenBucket(2, 0.5);
		Scanner scanner = new Scanner(System.in);
		double startTokens;
		double endTokens;
		while (true) {
			int option = scanner.nextInt();
			switch (option) {
				case 0:
					System.exit(0);
					break;
				case 1:
					System.out.println("Enter capacity: ");
					double newCapacity = scanner.nextDouble();
					System.out.println("Enter fillRate: ");
					double newFillRate = scanner.nextDouble();
					tb = new TokenBucket(newCapacity, newFillRate);
					System.out.println("OK!");
					break;
				case 2:
					startTokens = tb.getTokens();
					System.out.print(String.format("Trying to remove 1 token (have %f)...", startTokens));
					if (tb.removeTokens(1))
						System.out.println(" success!");
					else
						System.out.println(" failed!");
					endTokens = tb.getTokens();
					System.out.println(String.format("Start: %f \tEnd: %f", startTokens, endTokens));
					break;
				case 3:
					System.out.println("Enter num tokens to remove: ");
					double numTokens = scanner.nextDouble();
					startTokens = tb.getTokens();
					System.out.print(String.format("Trying to remove %f token (have %f)...", numTokens, startTokens));
					if (tb.removeTokens(numTokens))
						System.out.println(" success!");
					else
						System.out.println(" failed!");
					endTokens = tb.getTokens();
					System.out.println(String.format("Start: %f \tEnd: %f", startTokens, endTokens));
					break;
				case 4:
					System.out.println("Enter a duration for the test: ");
					long duration = scanner.nextLong();
					long start = (System.currentTimeMillis() / 1000);
					long timestamp = start;
					double successes = 0;
					long attempts = 0;
					while ((timestamp - start) < duration) {
						if (tb.removeTokens(1.0))
							successes += 1.0;
						timestamp = (System.currentTimeMillis() / 1000);
						attempts++;
						System.out.println(String.format("%f", tb.tokens));
					}
					double actualDuration  = ((double)(timestamp - start));
					double rate = successes / actualDuration;
					System.out.println(String.format(
							"Capacity: %.2f \tFill Rate: %.2f \tAttempts/Successes: %d/%.0f \tDuration: %f \tRate: %f", 
							tb.capacity, tb.fillRate, attempts, successes, actualDuration, rate));
					break;
				default:
					break;
			}
		}
	}
}