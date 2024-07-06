import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.LocalDate;

class Reservation {
    private int id;
    private String name;
    private LocalDateTime date;
    private int numberOfGuests;
    

    public Reservation(int id, String name, LocalDateTime date, int numberOfGuests) {
        this.id = id;
        this.name = name;
        this.date = date;        
        this.numberOfGuests = numberOfGuests;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
}

class ReservationSystem {
    private List<Reservation> reservations = new ArrayList<>();
    private int nextId = 1;

    public Reservation makeReservation(String name, LocalDateTime date, int numberOfGuests) {
        Reservation reservation = new Reservation(nextId++, name, date, numberOfGuests);
        reservations.add(reservation);
        return reservation;
    }

    public LocalDateTime combineDateTime(LocalDate date, LocalTime time){
        return date.atTime(time);
    }
    public boolean timeValidator(String timeToValidate){
        String timePattern = "^([01][0-9]|2[0-3]):[0-5][0-9]$"; // matches 0-23:00-59

        Pattern pattern = Pattern.compile(timePattern);
        Matcher matcher = pattern.matcher(timeToValidate);

        if(matcher.matches()){
            return true;
        }
        return false;
    }
    public boolean dateValidator(String dateToValidate) {
        String datePattern = "^(\\d{4})-(\\d{2})-(\\d{2})$";

        Pattern pattern = Pattern.compile(datePattern);
        Matcher matcher = pattern.matcher(dateToValidate);

        if(matcher.matches()){
            return true;
        }
        return false;
    }
    public String timeCounter(int id) {        
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = null;
        for (Reservation reservation : reservations) {
            if (reservation.getId() == id) {
                endDate = reservation.getDate();
            }                               
        }
        if (endDate == null) {
            return "Reservation not found.";
        }
        
        long durationInSeconds = Duration.between(startDate, endDate).getSeconds();
        
        // Variables to calculate time from seconds into days, hours, minutes.
        long days = durationInSeconds / (24 * 3600);
        long hours = (durationInSeconds % (24 * 3600)) / 3600;
        long minutes = (durationInSeconds % 3600) / 60;
        
        String formattedTime = String.format("%02d days, %02d:%02d", days, hours, minutes);
        
        return formattedTime;
    }
    

    public List<Reservation> getReservations() {
        return reservations;
    }

    public Reservation getReservationById(int id) {
        for (Reservation reservation : reservations) {
            if (reservation.getId() == id) {
                return reservation;
            }
        }
        return null;
    }

    public boolean cancelReservation(int id) {
        Reservation reservation = getReservationById(id);
        if (reservation != null) {
            reservations.remove(reservation);
            return true;
        }
        return false;
    }
}

public class ReservationSystemUI {
    private ReservationSystem reservationSystem = new ReservationSystem();
    private Scanner scanner = new Scanner(System.in);
    private DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public void start() {
        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    makeReservation();
                    break;
                case 2:
                    viewAllReservations();
                    break;
                case 3:
                    cancelReservation();
                    break;
                case 4:
                    checkTimeLeft();
                    break;
                case 5:
                    updateReservation();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice");
            }

            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("1. Make a reservation");
        System.out.println("2. View all reservations");
        System.out.println("3. Cancel a reservation");
        System.out.println("4. Check time left until reservation");
        System.out.println("5. Update reservation");
        System.out.println("6. Exit");
    }

    private void makeReservation() {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Date(yyyy-mm-dd): ");
        String dateToConvert = scanner.nextLine();
        System.out.print("Time(HH:MM): ");
        String timeToConvert = scanner.nextLine();
        System.out.print("Number of guests: ");
        int numberOfGuests = scanner.nextInt();
        scanner.nextLine(); // consume newline

        LocalDateTime date = LocalDateTime.parse(dateToConvert + "T" + timeToConvert);
        Reservation reservation = reservationSystem.makeReservation(name, date, numberOfGuests);
        System.out.println("Reservation made with ID " + reservation.getId());
    }

    private void viewAllReservations() {
        System.out.println("Reservations:");
        for (Reservation rr : reservationSystem.getReservations()) {
            String formattedDate = rr.getDate().format(myFormat);
            System.out.println(rr.getId() + " - " + rr.getName() + " - " + formattedDate + " - " + rr.getNumberOfGuests() + " people.");
        }
    }

    private void cancelReservation() {
        System.out.print("Reservation ID to cancel: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (reservationSystem.cancelReservation(id)) {
            System.out.println("Reservation canceled");
        } else {
            System.out.println("Reservation not found");
        }
    }

    private void checkTimeLeft() {
        System.out.print("Reservation ID to check time left until reservation: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Reservation r = reservationSystem.getReservationById(id);
        if (r != null) {
            String timeLeft = reservationSystem.timeCounter(id);
            String formattedDate = r.getDate().format(myFormat);
            System.out.println(timeLeft + " until reservation for " + r.getName() + " on " + formattedDate);
        } else {
            System.out.println("Reservation not found");
        }
    }

    private void updateReservation() {
        System.out.print("Reservation ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Reservation r = reservationSystem.getReservationById(id);
        if (r != null) {
            UpdateReservationHandler handler = new UpdateReservationHandler(r, scanner, reservationSystem);
            handler.handleUpdate();
        } else {
            System.out.println("Reservation not found");
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello, Welcome to Reservation Application");

        ReservationSystemUI obj = new ReservationSystemUI();
        obj.start();
    }
}

class UpdateReservationHandler {
    private Reservation reservation;
    private Scanner scanner;
    private ReservationSystem reservationSystem;

    public UpdateReservationHandler(Reservation reservation, Scanner scanner, ReservationSystem reservationSystem) {
        this.reservation = reservation;
        this.scanner = scanner;
        this.reservationSystem = reservationSystem;
    }

    public void handleUpdate() {
        String newName = reservation.getName();
        LocalDate newDate = reservation.getDate().toLocalDate();
        LocalTime newTime = reservation.getDate().toLocalTime();
        int newNumberOfGuests = reservation.getNumberOfGuests();
        String spaces = "             |";

        while (true) {
            System.out.println("a. Update name" + spaces + newName);
            System.out.println("b. Update Date" + spaces + newDate);
            System.out.println("c. Update Time" + spaces + newTime);
            System.out.println("d. Update number of guests |" + newNumberOfGuests);
            System.out.println("e. Main menu  " + spaces);

            String choice = scanner.nextLine();
            switch (choice) {
                case "a":
                    System.out.print("Update Name (" + newName + "): ");
                    newName = scanner.nextLine();
                    reservation.setName(newName);
                    break;
                case "b":
                    System.out.print("Update Date (yyyy-mm-dd): ");
                    String dateToParse = scanner.nextLine();
                    if (reservationSystem.dateValidator(dateToParse)) {
                        newDate = LocalDate.parse(dateToParse);
                        reservation.setDate(reservationSystem.combineDateTime(newDate, newTime));
                        System.out.println("New date set to " + dateToParse);
                    } else {
                        System.out.println("Invalid date format");
                    }
                    break;
                case "c":
                    System.out.print("Update Time (HH:MM): ");
                    String timeToParse = scanner.nextLine();
                    if (reservationSystem.timeValidator(timeToParse)) {
                        newTime = LocalTime.parse(timeToParse);
                        reservation.setDate(reservationSystem.combineDateTime(newDate, newTime));
                        System.out.println("Time updated successfully");
                    } else {
                        System.out.println("Invalid time format");
                    }
                    break;
                case "d":
                    System.out.print("Update number of guests (" + newNumberOfGuests + "): ");
                    newNumberOfGuests = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    reservation.setNumberOfGuests(newNumberOfGuests);
                    break;
                case "e":
                    return; // exit update loop
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}

