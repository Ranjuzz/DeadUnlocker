package App;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

    public static void main(String[] args) {

        int numberOfUsers = 15;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);

        for (int i = 0; i < numberOfUsers; i++) {
            executorService.submit(() -> {
                Operations.buyTicket((int) (Math.random() * 101));
                Operations.returnTicket((int) (Math.random() * 10));
            });
        }

        executorService.shutdown();
    }
}

class Operations {

    private final static String path = "Files/Ticket.txt";

    public static void buyTicket(int need) {
        // rl.lock();
        try {
            File f = new File(path);
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
        File f = new File(path);
        try (Scanner sc = new Scanner(f)) {
            int tickets = Integer.parseInt(sc.nextLine());
            System.out.println("No of available Tickets        : " + tickets);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void returnTicket(int need) {
        // rl.lock();
        if (need == 0) {
            need = 10;
        }
        try {
            File f = new File(path);
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
