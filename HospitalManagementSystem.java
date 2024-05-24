import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HospitalManagementSystem {

    private static final int R = 256; // extended ASCII
    private static final int MAX_FLOORS_MENTAL = 6;

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

        public void admitPatient(String room, String patientName, boolean limitFloors) {
            if (limitFloors && !isValidFloor(room)) {
                System.out.println("Invalid floor. The Mental building has a limit of " + MAX_FLOORS_MENTAL + " floors.");
                return;
            }

            TrieNode current = root;
            for (int i = 0, L = room.length(); i < L; i++) {
                int id = room.charAt(i);
                if (current.children[id] == null) {
                    current.children[id] = new TrieNode();
                }
                current = current.children[id];
            }
            if (!current.isOccupied) {
                current.isOccupied = true;
                current.patientName = patientName;
                System.out.println(patientName + " has been admitted to room " + room + ".");
            } else {
                System.out.println("Room " + room + " is already occupied by " + current.patientName + ".");
            }
        }

        public void dischargePatient(String room, boolean limitFloors) {
            if (limitFloors && !isValidFloor(room)) {
                System.out.println("Invalid floor. The Mental building has a limit of " + MAX_FLOORS_MENTAL + " floors.");
                return;
            }

            TrieNode current = root;
            for (int i = 0, L = room.length(); i < L; i++) {
                int id = room.charAt(i);
                if (current.children[id] == null) {
                    System.out.println("Room " + room + " does not exist.");
                    return;
                }
                current = current.children[id];
            }
            if (current.isOccupied) {
                System.out.println(current.patientName + " has been discharged from room " + room + ".");
                current.isOccupied = false;
                current.patientName = "";
            } else {
                System.out.println("Room " + room + " is already empty.");
            }
        }

        public boolean isOccupied(String room) {
            TrieNode current = root;
            for (int i = 0, L = room.length(); i < L; i++) {
                int id = room.charAt(i);
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

        private boolean isValidFloor(String room) {
            if (room == null || room.isEmpty()) return false;
            char floorChar = room.charAt(0);
            return Character.isDigit(floorChar) && (floorChar - '0') <= MAX_FLOORS_MENTAL;
        }
    }

    private HospitalTrie generalSicknessBuilding;
    private HospitalTrie mentalInstituteBuilding;
    private HospitalTrie laborHospitalBuilding;

    public HospitalManagementSystem() {
        generalSicknessBuilding = new HospitalTrie();
        mentalInstituteBuilding = new HospitalTrie();
        laborHospitalBuilding = new HospitalTrie();
    }

    public void admitPatient(String building, String room, String patientName, Scanner scanner) {
        switch (building) {
            case "General":
                generalSicknessBuilding.admitPatient(room, patientName, false);
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
                
                mentalInstituteBuilding.admitPatient(room, patientName, true);
                break;
            case "Labor":
                laborHospitalBuilding.admitPatient(room, patientName, false);
                break;
            default:
                System.out.println("Invalid building.");
        }
    }

    public void dischargePatient(String building, String room) {
        switch (building) {
            case "General":
                generalSicknessBuilding.dischargePatient(room, false);
                break;
            case "Mental":
                mentalInstituteBuilding.dischargePatient(room, true);
                break;
            case "Labor":
                laborHospitalBuilding.dischargePatient(room, false);
                break;
            default:
                System.out.println("Invalid building.");
        }
    }

    public boolean isOccupied(String building, String room) {
        switch (building) {
            case "General":
                return generalSicknessBuilding.isOccupied(room);
            case "Mental":
                return mentalInstituteBuilding.isOccupied(room);
            case "Labor":
                return laborHospitalBuilding.isOccupied(room);
            default:
                System.out.println("Invalid building.");
                return false;
        }
    }

    public void printBuilding(String building) {
        switch (building) {
            case "General":
                generalSicknessBuilding.print();
                break;
            case "Mental":
                mentalInstituteBuilding.print();
                break;
            case "Labor":
                laborHospitalBuilding.print();
                break;
            default:
                System.out.println("Invalid building.");
        }
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
                    System.out.print("Enter building (General, Mental, Labor): ");
                    String building = scanner.nextLine();
                    String room = "";
                    if (building.equalsIgnoreCase("Mental")) {
                        hospital.printBuilding(building);
                        System.out.print("\n1st floor for normal mentally ill patient");
                        System.out.print("\n2st floor for Middle level mentally ill patient");
                        System.out.print("\n3st floor for Maximum level security mentally ill patient");
                        System.out.print("\nEnter room number (it will be reassign based on the patients condition): ");
                        room = scanner.nextLine();
                    }
                    System.out.print("Enter room number: ");
                    room = scanner.nextLine();
                    hospital.admitPatient(building, room, patientName, scanner);
                    break;
                case 2:
                    System.out.print("Enter room number: ");
                    room = scanner.nextLine();
                    System.out.print("Enter building (General, Mental, Labor): ");
                    String building2 = scanner.nextLine();
                    hospital.dischargePatient(building2, room);
                    break;
                case 3:
                    System.out.print("Enter room number: ");
                    room = scanner.nextLine();
                    System.out.print("Enter building (General, Mental, Labor): ");
                    String building3 = scanner.nextLine();
                    boolean isOccupied = hospital.isOccupied(building3, room);
                    System.out.println("Room " + room + " in " + building3 + " building is " + (isOccupied ? "occupied." : "empty."));
                    break;
                case 4:
                    System.out.print("Enter building (General, Mental, Labor): ");
                    String building4 = scanner.nextLine();
                    hospital.printBuilding(building4);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        scanner.close();
    }
}
