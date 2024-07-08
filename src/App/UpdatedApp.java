package App;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// This code will run the threads indefinelty until you stop the program i tried ti simulate the real world booking system where ther ewill users 24/7
public class UpdatedApp {

    private static final String FILE_PATH = "Files/Ticket.txt";
    private static final int threads = 5;
    private static final int ticket = 100;

    public static void main(String[] args) {
        File ticketFile = new File(FILE_PATH);
        initializeTicketFile(ticketFile);

// this is used to run the threads in parallel 
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executor.execute(new TicketProcessor(ticketFile));
        }
    }

//  for keeping the ticket value at a maximm number while running the program
    static void initializeTicketFile(File file) {
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(String.valueOf(ticket));
        } catch (IOException e) {
            System.err.println("Error initializing ticket file: " + e.getMessage());
        }
    }

    static class TicketProcessor implements Runnable {

        private final File ticketFile;
        Random random = new Random();

        public TicketProcessor(File ticketFile) {
            this.ticketFile = ticketFile;
        }

// i have implemented multi threadin using the runnable interface
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(random.nextInt(2000) + 100);
                    int action = random.nextInt(2);
                    int ticketsToProcess = random.nextInt(5) + 1;
                    if (action == 0) {
                        buyTickets(ticketsToProcess);
                    } else {
                        returnTickets(ticketsToProcess);
                    }
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }
        }

        private synchronized void buyTickets(int ticket) {
            try {
                int current = ticketCount();
                if (current > 0) {
                    System.out.println(Thread.currentThread().getName() + " is buying " + ticket + " tickets.");
                    if (ticket <= current) {
                        int newCount = current - ticket;
                        writeTicketCount(newCount);
                    } else {
                        System.out.println("Not enough tickets available");
                    }
                } else {
                    System.out.println("No tickets available for " + Thread.currentThread().getName());
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }

        private synchronized void returnTickets(int ticketsToReturn) {
            try {
                int curr = ticketCount();
                int newCount = curr + ticketsToReturn;
                writeTicketCount(newCount);
                System.out.println(Thread.currentThread().getName() + " is returning " + ticketsToReturn + " tickets.");
            } catch (IOException e) {
                System.err.println(e);
            }
        }

        private int ticketCount() throws IOException {
            synchronized (ticketFile) {
                try (Scanner sc = new Scanner(ticketFile)) {
                    return Integer.parseInt(sc.nextLine().trim());
                }
            }
        }

        private void writeTicketCount(int count) throws IOException {
            synchronized (ticketFile) {
                try (FileWriter fw = new FileWriter(ticketFile)) {
                    fw.write(String.valueOf(count));
                }
            }
        }
    }
}
