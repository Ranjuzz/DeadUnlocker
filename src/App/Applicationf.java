package App;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Applicationf {

    private final static String path = "D:\\Ranjith\\Project\\Abluva\\Concurrency\\DeadUnlocker\\Files\\Ticket.txt";

    public static void main(String[] args) throws InterruptedException, IOException {

        Thread fileWatcherThread = new Thread(new FileWatcher(Paths.get(path).getParent()));
        fileWatcherThread.start();

        Operationsf op = new Operationsf();
        Scanner sc = new Scanner(System.in);
        System.out.println("Ticketing App.Application");
        String ch = "";
        do {
            System.out.println("\tMenu");
            System.out.println("\t1. Buy Ticket");
            System.out.println("\t2. Return Ticket");
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

    static class FileWatcher implements Runnable {

        private final Path path;

        public FileWatcher(Path path) {
            this.path = path;
        }

        @Override
        public void run() {
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                while (true) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                            Path changed = (Path) event.context();
                            if (changed.endsWith("Ticket.txt")) {
                                Opera
                            }
                        }
                    }
                    key.reset();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Operationsf {

    private final static Semaphore semaphore = new Semaphore(1);
    private final static String path = "D:\\Ranjith\\Project\\Abluva\\Concurrency\\DeadUnlocker\\Files\\Ticket.txt";

    public void buyTicket() throws InterruptedException, IOException {

        File file = new File(path);
        try (Scanner sc = new Scanner(file)) {
            Scanner sc1 = new Scanner(System.in);
            showTicket();
            System.out.println("How many tickets do you need?: ");
            int need = Integer.parseInt(sc1.next());
            semaphore.acquire();
            int tickets = Integer.parseInt(sc.nextLine());
            if (need <= tickets) {
                int newTicketCount = tickets - need;
                System.out.printf("You bought %d tickets\n", need);
                try (FileWriter fw = new FileWriter(file)) {
                    fw.write(String.valueOf(newTicketCount));
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.printf("\nOnly %d tickets are available\n", tickets);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            semaphore.release();
        }
    }

    public void returnTicket() throws IOException, InterruptedException {

        File file = new File(path);
        try (Scanner sc = new Scanner(file)) {
            Scanner sc1 = new Scanner(System.in);
            System.out.print("No of tickets you need to return: ");
            int need = Integer.parseInt(sc1.next());
            semaphore.acquire();
            int tickets = Integer.parseInt(sc.nextLine());
            int newTicketCount = tickets + need;
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(String.valueOf(newTicketCount));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            semaphore.release();
        }
    }

    public static void showTicket() throws InterruptedException, IOException {

        File file = new File(path);
        try (Scanner sc = new Scanner(file)) {
            semaphore.acquire();
            int tickets = Integer.parseInt(sc.nextLine());
            System.out.println("No of available Tickets        : " + tickets);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            semaphore.release();
        }
    }

}
