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
                System.out.println(patientName + " has been admitted to room " + room + " in " + " building " +building + "." );
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
                System.out.print("Is the patient hallucinating? (yes/no): ");
                String hallucinate = scanner.nextLine().trim().toLowerCase();
                System.out.print("Does the patient like to hurt themselves? (yes/no): ");
                String selfHarm = scanner.nextLine().trim().toLowerCase();
                
                if (hallucinate.equals("yes") && selfHarm.equals("yes")) {
                    room = "3" + room.substring(1); // Admitting to 3rd floor
                } else if (hallucinate.equals("yes")) {
                    room = "2" + room.substring(1); // Admitting to 2nd floor
                } else if (selfHarm.equals("yes")) {
                    room = "3" + room.substring(1); // Admitting to 3rd floor
                } else {
                    room = "1" + room.substring(1); // Admitting to 1st floor
                }
                
                hospitalTrie.admitPatient("M", room, patientName, false, MAX_FLOORS_MENTAL);
                break;
            case "Labor":
                hospitalTrie.admitPatient("L", room, patientName, true, MAX_FLOORS_LABOR);
                break;
            default:
                System.out.println("Invalid building.");
        }
    }

    public void dischargePatient(String building, String room) {
        switch (building) {
            case "Orthopedi":
                hospitalTrie.dischargePatient("O", room, false, 0);
                break;
            case "Mental":
                hospitalTrie.dischargePatient("M", room, true, MAX_FLOORS_MENTAL);
                break;
            case "Labor":
                hospitalTrie.dischargePatient("L", room, true, MAX_FLOORS_LABOR);
                break;
            case "Cardiology":
                hospitalTrie.dischargePatient("C", room, false, 0);
                break;
            case "THT":
                hospitalTrie.dischargePatient("T", room, false, 0);
                break;
                default:
                System.out.println("Invalid building.");
        }
    }

    public boolean isOccupied(String building, String room) {
        switch (building) {
            case "Orthopedi":
                return hospitalTrie.isOccupied("O", room);
            case "Mental":
                return hospitalTrie.isOccupied("M", room);
            case "Labor":
                return hospitalTrie.isOccupied("L", room);
            case "Cardiology":
                return hospitalTrie.isOccupied("C", room);
            case "THT":
                return hospitalTrie.isOccupied("T", room);
            default:
                System.out.println("Invalid building.");
                return false;
        }
    }

    public void printBuilding() {
        hospitalTrie.print();
    }

    public static void main(String[] args) {
        HospitalManagementSystem hospital = new HospitalManagementSystem();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nHospital Management System");
            System.out.println("1. Admit Patient");
            System.out.println("2. Discharge Patient");
            System.out.println("3. Check Room Occupancy");
            System.out.println("4. Print Building Status");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            if (choice == 5) {
                break;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter patient name: ");
                    String patientName = scanner.nextLine();
                    System.out.print("Enter building (Orthopedi, Mental, Labor, THT, Cardiology): ");
                    String building = scanner.nextLine();
                    String room = "";
                    if (building.equalsIgnoreCase("Mental")) {
                        hospital.printBuilding();
                        System.out.println("\n1st floor for normal mentally ill patients");
                        System.out.println("2nd floor for Middle-level mentally ill patients");
                        System.out.println("3rd floor for Maximum level security mentally ill patients");
                        System.out.print("Enter room number (it will be reassigned based on the patient's condition): ");
                        room = scanner.nextLine();
                    } else {
                        System.out.print("Enter room number: ");
                        room = scanner.nextLine();
                    }
                    hospital.admitPatient(building, room, patientName, scanner);
                    break;
                case 2:
                    System.out.print("Enter building (Orthopedi, Mental, Labor, THT, Cardiology): ");
                    String building2 = scanner.nextLine();
                    System.out.print("Enter room number: ");
                    room = scanner.nextLine();
                    hospital.dischargePatient(building2, room);
                    break;
                case 3:
                    System.out.print("Enter building (Orthopedi, Mental, Labor,THT,Cardiology): ");
                    String building3 = scanner.nextLine();
                    System.out.print("Enter room number: ");
                    room = scanner.nextLine();
                    boolean isOccupied = hospital.isOccupied(building3, room);
                    System.out.println("Room " + room + " in " + building3 + " building is " + (isOccupied ? "occupied." : "empty."));
                    break;
                case 4:
                    hospital.printBuilding();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        scanner.close();
    }
}
