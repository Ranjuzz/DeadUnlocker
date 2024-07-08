package App;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.*;

public class ApplicationFinal {

    private final static String path = "Files/Ticket.txt";

    public static void main(String[] args) throws InterruptedException, IOException, RuntimeException {

//        to store the finally modified time of the file if we use final File time we cant modify the time so we are going with the one element array here!!
        final FileTime[] lm = {FileTime.fromMillis(0)};
        ScheduledExecutorService es = Executors.newScheduledThreadPool(1);
        es.scheduleAtFixedRate(() -> {
            try {
                FileTime ft = Files.getLastModifiedTime(Path.of(path));
                if (ft.compareTo(lm[0]) > 0) {
                    System.out.println("The tickets are updated");
                    OperationsFinal.tickets();
                    lm[0] = ft;
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, 0, 1, TimeUnit.MILLISECONDS);
        Thread.sleep(10);

        OperationsFinal op = new OperationsFinal();
        Scanner sc = new Scanner(System.in);
        Thread.sleep(1000);
        System.out.println(" ---- Ticket Booking App ---- ");
        String ch = "";
        do {
            System.out.println("\t  Menu");
            System.out.println("\t1. Buy Ticket");
            System.out.println("\t2. Add Ticket");
            System.out.println("\t3. Show Ticket");
            System.out.println("\t4. Exit");
            System.out.println("Enter your choice: ");
            ch = sc.next();
            switch (ch) {
                case "1" ->
                    op.buyTicket();
                case "2" ->
                    op.returnTicket();
                case "3" ->
                    op.showTicket();
                case "4" -> {
                    System.exit(0);
                }
                default ->
                    System.out.println("Invalid choice");
            }
        } while (!ch.equalsIgnoreCase("4"));
    }

}

class OperationsFinal extends Thread {

    private final static Semaphore semaphore = new Semaphore(1);
    private final static String path = "Files/Ticket.txt";
    private final static File file = new File(path);
    private boolean fg = true;

    public void buyTicket() throws InterruptedException, IOException {

        Scanner sc1 = new Scanner(System.in);
        showTicket();
        if (!fg) {
            System.out.println("Sorry we ran out of tickets");
            return;
        }
        System.out.println("How many tickets do you need?: ");
        int need = Integer.parseInt(sc1.next());

//            Locking the file for modification!!
        semaphore.acquire();
        try (Scanner sc = new Scanner(file)) {
            int tickets = Integer.parseInt(sc.nextLine());
            if (tickets == 0) {
                System.out.println("Sorry! We ran out of Tickets!! :( ");
                return;
            }
            if (need <= tickets && need > 0) {
                int newTicketCount = tickets - need;
                System.out.printf("You bought %d tickets\n", need);
                try (FileWriter fw = new FileWriter(file)) {
                    fw.write(String.valueOf(newTicketCount));
//                    Thread.sleep(1);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else if (need == 0) {
                System.out.println("Please enter a ticket count of more than 0!");
                return;
            } else {
                System.out.printf("\nOnly %d tickets are available\n", tickets);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // Releasing the lock
        semaphore.release();
        Thread.sleep(30);
    }

    public void returnTicket() throws IOException, InterruptedException {
//        File file = new File(path);
        Scanner sc1 = new Scanner(System.in);
        System.out.print("No of tickets you need to return: ");
        int need = Integer.parseInt(sc1.next());
        semaphore.acquire();
        try (Scanner sc = new Scanner(file)) {
            int tickets = Integer.parseInt(sc.nextLine());
            int newTicketCount = tickets + need;
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(String.valueOf(newTicketCount));
                fg = true;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        semaphore.release();
        Thread.sleep(30);
    }

    public void showTicket() throws InterruptedException {
//        File file = new File(path);
        try (Scanner sc = new Scanner(file)) {
            semaphore.acquire();

            int tickets = Integer.parseInt(sc.nextLine());
            if (tickets == 0) {
                System.out.println("No tickets available");
                fg = false;
                return;
            }
            System.out.println("No of available Tickets: " + tickets);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            semaphore.release();
        }

    }

    public static synchronized void tickets() throws InterruptedException, IOException {
//        File file = new File(path);
        try (Scanner sc = new Scanner(file)) {
            semaphore.acquire();
            int tickets = Integer.parseInt(sc.nextLine());
            System.out.println("Updated Tickets: " + tickets);
        } finally {
            semaphore.release();
        }
    }
}
