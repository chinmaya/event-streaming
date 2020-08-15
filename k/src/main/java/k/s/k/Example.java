package k.s.k;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Example {

	public static void main(String[] args) {
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
		List<Integer> result = numbers
			.stream()
			.filter((n) -> n%2 == 0)
			.map((n) -> n*n)
			.limit(2)
			.collect(Collectors.toList());
		System.out.println(result);
			
		Integer sum = numbers
				.stream()
				.filter((n) -> n%2 == 0)
				.map((n) -> n*n)
				.reduce(0, (s, n) -> {
					System.out.println("s,n: " + s+","+n);
					return s+n;
				});
		System.out.println(sum);
		
		sum = numbers
				.stream()
				.filter((n) -> n%2 == 0)
				.mapToInt((n) -> n*n)
				.sum();
		System.out.println(sum);
	}

}
