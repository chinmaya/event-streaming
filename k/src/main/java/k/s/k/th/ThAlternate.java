package k.s.k.th;

import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThAlternate {
	static class FooBar {
		private int n;
		
		private volatile boolean fooStarted = false;
		
		private Lock lock = new ReentrantLock();
		private Condition cond = lock.newCondition();

		public FooBar(int n) {
			this.n = n;
		}
		
		public void foo() throws InterruptedException {
			// wait till start signal
			for (int i = 0; i < n; i++) {
				System.out.print("Foo");
			}
		}

		public void bar() throws InterruptedException {
			for (int i = 0; i < n; i++) {
				System.out.print("Bar");
			}
		}
	}

	public static void main (String[] args) {
		FooBar f = new FooBar(3);
		Executors.newSingleThreadExecutor().submit(() -> {
			try {
				f.foo();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		Executors.newSingleThreadExecutor().submit(() -> {
			try {
				f.bar();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
