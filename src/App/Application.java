package App;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Application {

    public static void main(String[] args) {

        int numberOfUsers = 15;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);

        for (int i = 0; i < numberOfUsers; i++) {
            executorService.submit(() -> {
                Operations.buyTicket((int) (Math.random() * 101));
                Operations.returnTicket((int) (Math.random() * 11));
            });
        }

        executorService.shutdown();
    }
}

class Operations {

    private final static ReentrantLock rl = new ReentrantLock();

    public static void buyTicket(int need) {
        // rl.lock();
        try {
            File f = new File("D:\\Ranjith\\Project\\Abluva\\Concurrency\\DeadUnlocker\\Files\\Ticket.txt");
            try (Scanner sc = new Scanner(f)) {
                int tickets = Integer.parseInt(sc.nextLine());
                Scanner sc1 = new Scanner(System.in);
                System.out.println("No of available Tickets      : " + tickets);
                if (need < tickets) {
                    int num = tickets - need;
                    System.out.printf("You Bought %d tickets\n", need);
                    try (FileWriter fw = new FileWriter(f)) {
                        fw.write(String.valueOf(num));
                        System.err.println("Ticket balance :" + num);
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                } else {
                    System.out.printf("\nOnly %d tickets are available\n", tickets);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } finally {
            // rl.unlock();
        }
    }

    public synchronized void showTicket() {
        File f = new File("D:\\Ranjith\\Project\\Abluva\\Concurrency\\DeadUnlocker\\Files\\Ticket.txt");
        try (Scanner sc = new Scanner(f)) {
            int tickets = Integer.parseInt(sc.nextLine());
            System.out.println("No of available Tickets        : " + tickets);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void returnTicket(int need) {
        // rl.lock();
        try {
            File f = new File("D:\\Ranjith\\Project\\Abluva\\Concurrency\\DeadUnlocker\\Files\\Ticket.txt");
            try (Scanner sc = new Scanner(f)) {
                Scanner sc1 = new Scanner(System.in);
                int tickets = Integer.parseInt(sc.nextLine());
                System.out.println("No of Tickets you need to return: " + need);
                try (FileWriter fw = new FileWriter(f)) {
                    fw.write(String.valueOf(tickets + need));
                } catch (IOException e) {
                    System.out.println(e);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } finally {
            // rl.unlock();
        }
    }

}
