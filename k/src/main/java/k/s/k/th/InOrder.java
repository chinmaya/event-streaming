package k.s.k.th;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.Uninterruptibles;

public class InOrder {
	
	private CountDownLatch dit = new CountDownLatch(1);
	private CountDownLatch dot = new CountDownLatch(1);
	private CountDownLatch blip = new CountDownLatch(1);
	
	public void dit() {
		try {
			dit.await();
			System.out.println("dit");
			dot.countDown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void dot() {
		try {
			dot.await();
			System.out.println("dot");
			blip.countDown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void blip() {
		try {
			blip.await();
			System.out.println("blip");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		ExecutorService executors = Executors.newFixedThreadPool(3);
		executors.submit(this::blip);
		executors.submit(this::dit);
		executors.submit(this::dot);
		Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(1));
		dit.countDown();
	}

	public static void main(String[] args) throws InterruptedException {
		InOrder order = new InOrder();
		order.start();
		Thread.currentThread().join();
	}

}
