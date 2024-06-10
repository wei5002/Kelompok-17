import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HospitalManagementSystem {

    private static final int R = 256; // extended ASCII
    private static final int MAX_FLOORS_MENTAL = 4;
    private static final int MAX_FLOORS_LABOR = 5;

    private class TrieNode {
        boolean isOccupied;
        String patientName;
        TrieNode[] children;

        public TrieNode() {
            isOccupied = false;
            patientName = "";
            children = new TrieNode[R];
        }
    }

    private class HospitalTrie {
        private TrieNode root;

        public HospitalTrie() {
            root = new TrieNode();
        }

        public void admitPatient(String building, String room, String patientName, boolean limitFloors, int maxFloors) {
            String fullRoom = building + room;
            if (limitFloors && !isValidFloor(room, maxFloors)) {
                System.out.println("Invalid floor. The building has a limit of " + maxFloors + " floors.");
                return;
            }

            TrieNode current = root;
            for (int i = 0, L = fullRoom.length(); i < L; i++) {
                int id = fullRoom.charAt(i);
                if (current.children[id] == null) {
                    current.children[id] = new TrieNode();
                }
                current = current.children[id];
            }
            if (!current.isOccupied) {
                current.isOccupied = true;
                current.patientName = patientName;
                System.out.println(patientName + " has been admitted to room " + room + " in building " + building + ".");
            } else {
                System.out.println("Room " + room + " in " + building + " building is already occupied by " + current.patientName + ".");
            }
        }

        public void dischargePatient(String building, String room, boolean limitFloors, int maxFloors) {
            String fullRoom = building + room;
            if (limitFloors && !isValidFloor(room, maxFloors)) {
                System.out.println("Invalid floor. The building has a limit of " + maxFloors + " floors.");
                return;
            }

            TrieNode current = root;
            for (int i = 0, L = fullRoom.length(); i < L; i++) {
                int id = fullRoom.charAt(i);
                if (current.children[id] == null) {
                    System.out.println("Room " + room + " in " + building + " building does not exist.");
                    return;
                }
                current = current.children[id];
            }
            if (current.isOccupied) {
                System.out.println(current.patientName + " has been discharged from room " + room + " in " + building + " building.");
                current.isOccupied = false;
                current.patientName = "";
            } else {
                System.out.println("Room " + room + " in " + building + " building is already empty.");
            }
        }

        public boolean isOccupied(String building, String room) {
            String fullRoom = building + room;
            TrieNode current = root;
            for (int i = 0, L = fullRoom.length(); i < L; i++) {
                int id = fullRoom.charAt(i);
                if (current.children[id] == null) {
                    return false;
                }
                current = current.children[id];
            }
            return current.isOccupied;
        }

        public List<String> listOccupiedRooms() {
            List<String> list = new ArrayList<>();
            listRooms(root, "", list);
            return list;
        }

        private void listRooms(TrieNode current, String prefix, List<String> list) {
            if (current == null) return;
            if (current.isOccupied) {
                list.add(prefix + " (occupied by " + current.patientName + ")");
            }
            for (int i = 0; i < R; i++) {
                if (current.children[i] != null) {
                    listRooms(current.children[i], prefix + (char) i, list);
                }
            }
        }

        public void print() {
            print("", root, 0, true, true);
        }

        private void print(String prefix, TrieNode root, int id, boolean isTail, boolean isRoot) {
            if (!isRoot) {
                System.out.println(prefix + (isTail ? "└── " : "├── ") + (char) id + (root.isOccupied ? " *** (occupied by " + root.patientName + ")" : ""));
            }

            TrieNode lastChild = null;
            int lastChildId = 0;
            boolean isLastChild = true;
            for (int i = R - 1; i >= 0; i--) {
                if (root.children[i] != null) {
                    if (isLastChild) {
                        isLastChild = false;
                        lastChild = root.children[i];
                        lastChildId = i;
                    } else {
                        print(prefix + (isRoot ? "" : (isTail ? "    " : "│   ")), root.children[i], i, false, false);
                    }
                }
            }
            if (lastChild != null) {
                print(prefix + (isRoot ? "" : (isTail ? "    " : "│   ")), lastChild, lastChildId, true, false);
            }
        }

        private boolean isValidFloor(String room, int maxFloors) {
            if (room == null || room.isEmpty()) return false;
            int floorNumber = 0;
            int i = 0;
            while (i < room.length() && Character.isDigit(room.charAt(i))) {
                floorNumber = floorNumber * 10 + (room.charAt(i) - '0');
                i++;
            }
            return floorNumber > 0 && floorNumber <= maxFloors;
        }
    }

    private HospitalTrie hospitalTrie;

    public HospitalManagementSystem() {
        hospitalTrie = new HospitalTrie();
    }

    public void admitPatient(String building, String room, String patientName, Scanner scanner) {
        int roomType = -1;
        boolean validInput = false;

        System.out.println("Room types for " + building + " building:");
        System.out.println("1. ICU");
        System.out.println("2. BPJS");
        System.out.println("3. Normal");
        System.out.println("4. VIP");
        System.out.println("5. VVIP");
        while (!validInput) {
            System.out.print("Choose room type (1-5): ");
            if (scanner.hasNextInt()) {
                roomType = scanner.nextInt();
                if (roomType >= 1 && roomType <= 5) {
                    validInput = true;
                } else {
                    System.out.println("Invalid room type. Please enter a number between 1 and 5.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();  // Consume invalid input
            }
        }
        scanner.nextLine();  // Consume newline
        System.out.print("Enter room number: ");
        room = scanner.nextLine();
        switch (roomType) {
            case 1:
                room = "1" + room.substring(1); // ICU on the 1st floor
                break;
            case 2:
                room = "2" + room.substring(1); // BPJS on the 2nd floor
                break;
            case 3:
                room = "3" + room.substring(1); // Normal on the 3rd floor
                break;
            case 4:
                room = "4" + room.substring(1); // VIP on the 4th floor
                break;
            case 5:
                room = "5" + room.substring(1); // VVIP on the 5th floor
                break;
        }

        switch (building) {
            case "THT":
                hospitalTrie.admitPatient("T", room, patientName, false, 0);
                break;

            case "Cardiology":
                hospitalTrie.admitPatient("C", room, patientName, false, 0);
                break;

            case "Orthopedi":
                hospitalTrie.admitPatient("O", room, patientName, false, 0);
                break;

            case "Mental":
                hospitalTrie.admitPatient("M", room, patientName, false, MAX_FLOORS_MENTAL);
                break;

            case "Labor":
                hospitalTrie.admitPatient("L", room, patientName, false, MAX_FLOORS_LABOR);
                break;

            default:
                System.out.println("Invalid building.");
                break;
        }
    }

    public void dischargePatient(String building, String room) {
        switch (building) {
            case "THT":
            case "Cardiology":
            case "Orthopedi":
                hospitalTrie.dischargePatient(building.substring(0, 1), room, false, 0);
                break;
            case "Mental":
                hospitalTrie.dischargePatient("M", room, false, MAX_FLOORS_MENTAL);
                break;
            case "Labor":
                hospitalTrie.dischargePatient("L", room, false, MAX_FLOORS_LABOR);
                break;
            default:
                System.out.println("Invalid building.");
                break;
        }
    }

    public boolean isOccupied(String building, String room) {
        return hospitalTrie.isOccupied(building.substring(0, 1), room);
    }

    public List<String> listOccupiedRooms() {
        return hospitalTrie.listOccupiedRooms();
    }

    public void printTrie() {
        hospitalTrie.print();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HospitalManagementSystem system = new HospitalManagementSystem();

        while (true) {
            System.out.println("\n1. Admit Patient");
            System.out.println("2. Discharge Patient");
            System.out.println("3. Check Room Occupancy");
            System.out.println("4. List Occupied Rooms");
            System.out.println("5. Print Trie");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (option) {
                case 1:
                    System.out.print("Enter patient name: ");
                    String patientName = scanner.nextLine();
                    System.out.print("Enter building (THT, Cardiology, Orthopedi, Mental, Labor): ");
                    String building = scanner.nextLine();
                    String room = "";  // room will be entered based on building type
                    system.admitPatient(building, room, patientName, scanner);
                    break;
                case 2:
                    System.out.print("Enter building (THT, Cardiology, Orthopedi, Mental, Labor): ");
                    building = scanner.nextLine();
                    System.out.print("Enter room number: ");
                    room = scanner.nextLine();
                    system.dischargePatient(building, room);
                    break;
                case 3:
                    System.out.print("Enter building (THT, Cardiology, Orthopedi, Mental, Labor): ");
                    building = scanner.nextLine();
                    System.out.print("Enter room number: ");
                    room = scanner.nextLine();
                    if (system.isOccupied(building, room)) {
                        System.out.println("The room is occupied.");
                    } else {
                        System.out.println("The room is available.");
                    }
                    break;
                case 4:
                    List<String> occupiedRooms = system.listOccupiedRooms();
                    if (occupiedRooms.isEmpty()) {
                        System.out.println("No rooms are occupied.");
                    } else {
                        System.out.println("Occupied rooms:");
                        for (String occupiedRoom : occupiedRooms) {
                            System.out.println(occupiedRoom);
                        }
                    }
                    break;
                case 5:
                    system.printTrie();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }
}
