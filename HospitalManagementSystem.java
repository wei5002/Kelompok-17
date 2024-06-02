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

        public void admitPatient(String room, String patientName, boolean limitFloors, int maxFloors) {
            if (limitFloors && !isValidFloor(room, maxFloors)) {
                System.out.println("Invalid floor. The building has a limit of " + maxFloors + " floors.");
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

        public void dischargePatient(String room, boolean limitFloors, int maxFloors) {
            if (limitFloors && !isValidFloor(room, maxFloors)) {
                System.out.println("Invalid floor. The building has a limit of " + maxFloors + " floors.");
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

    private HospitalTrie cardiologyBuilding;
    private HospitalTrie mentalInstituteBuilding;
    private HospitalTrie laborHospitalBuilding;
    private HospitalTrie thtBuilding;
    private HospitalTrie orthopedicBuilding; 

    public HospitalManagementSystem() {
        cardiologyBuilding = new HospitalTrie();
        mentalInstituteBuilding = new HospitalTrie();
        laborHospitalBuilding = new HospitalTrie();
        thtBuilding = new HospitalTrie();
        orthopedicBuilding = new HospitalTrie(); 
    }

    public void admitPatient(String building, String room, String patientName, Scanner scanner) {
        switch (building) {
            case "Cardiology":
            System.out.println("Jenis Ruangan pada Gedung Cardiology(Jantung):");
            System.out.println("1. ICU      = Rp 2.000.000,00");
            System.out.println("2. BPJS     = Rp 0");
            System.out.println("3. Normal   = Rp 1.500.000,00");
            System.out.println("4. VIP      = Rp 15.000.000,00");
            System.out.println("5. VVIP     = Rp 20.000.000,00");
            System.out.print("Silahkan Memilih Jenis Ruangan (1-5): ");
            int roomTypeCardio = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (roomTypeCardio) {
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
                default:
                    System.out.println("Invalid room type.");
                    return;
            }
                cardiologyBuilding.admitPatient(room, patientName, false, 0);
                break;
            case "Orthopedic":
            System.out.println("Jenis Ruangan pada Gedung Orthopedic(Tulang):");
            System.out.println("1. ICU      = Rp 2.000.000,00");
            System.out.println("2. BPJS     = Rp 0");
            System.out.println("3. Normal   = Rp 1.500.000,00");
            System.out.println("4. VIP      = Rp 15.000.000,00");
            System.out.println("5. VVIP     = Rp 20.000.000,00");
            System.out.print("Silahkan Memilih Jenis Ruangan (1-5): ");
            int roomTypeOrtho = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (roomTypeOrtho) {
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
                default:
                    System.out.println("Invalid room type.");
                    return;
            }
                orthopedicBuilding.admitPatient(room, patientName, false, 0); // New addition
                break;
            case "THT":
            System.out.println("Jenis Ruangan pada Gedung THT: ");
            System.out.println("1. ICU      = Rp 2.000.000,00");
            System.out.println("2. BPJS     = Rp 0");
            System.out.println("3. Normal   = Rp 1.500.000,00");
            System.out.println("4. VIP      = Rp 15.000.000,00");
            System.out.println("5. VVIP     = Rp 20.000.000,00");
            System.out.print("Silahkan Memilih Jenis Ruangan (1-5): ");
            int roomTypeTHT = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (roomTypeTHT) {
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
                default:
                    System.out.println("Invalid room type.");
                    return;
            }

            thtBuilding.admitPatient(room, patientName, false, 0); // No floor limit for THT
            break;
            case "Mental":
                System.out.print("Apakah pasien mengalami Halusinasi? (ya/tidak): ");
                String hallucinate = scanner.nextLine().trim().toLowerCase();
                System.out.print("Does the patient like to hurt themselves? (yes/no): ");
                String selfHarm = scanner.nextLine().trim().toLowerCase();

                if (hallucinate.equals("ya") && selfHarm.equals("ya")) {
                    room = "3 " + room.substring(1); // Admitting to 3rd floor
                } else if (hallucinate.equals("ya")) {
                    room = "2 " + room.substring(1); // Admitting to 2nd floor
                } else if (selfHarm.equals("ya")) {
                    room = "3 " + room.substring(1); // Admitting to 3rd floor
                } else {
                    room = "1" + room.substring(1); // Admitting to 1st floor
                }

                mentalInstituteBuilding.admitPatient(room, patientName, false, MAX_FLOORS_MENTAL);
                break;

            case "Labor":
            System.out.println("Jenis Ruangan pada Gedung Labor (Kandungan): ");
            System.out.println("1. ICU      = Rp 4.000.000,00");
            System.out.println("2. BPJS     = Rp 0");
            System.out.println("3. Normal   = Rp 5.000.000,00");
            System.out.println("4. VIP      = Rp 20.000.000,00");
            System.out.println("5. VVIP     = Rp 25.000.000,00");
            System.out.print("Silahkan Memilih Jenis Ruangan (1-5): ");
            int roomTypeLabor = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (roomTypeLabor) {
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
                default:
                    System.out.println("Invalid room type.");
                    return;
            }
                laborHospitalBuilding.admitPatient(room, patientName, false, MAX_FLOORS_LABOR);
                break;
            default:
                System.out.println("Invalid building.");
        }
    }

    public void dischargePatient(String building, String room) {
        switch (building) {
            case "Cardiology":
                cardiologyBuilding.dischargePatient(room, false, 0);
                break;
            case "THT":
                thtBuilding.dischargePatient(room, false, 0); 
                break;
            case "Orthopedic":
                orthopedicBuilding.dischargePatient(room, false, 0); 
                break;
            case "Mental":
                mentalInstituteBuilding.dischargePatient(room, true, MAX_FLOORS_MENTAL);
                break;
            case "Labor":
                laborHospitalBuilding.dischargePatient(room, true, MAX_FLOORS_LABOR);
                break;
            default:
                System.out.println("Invalid building.");
        }
    }

    public boolean isOccupied(String building, String room) {
        switch (building) {
            case "Cardiology":
                return cardiologyBuilding.isOccupied(room);
            case "THT":
                return thtBuilding.isOccupied(room); 
            case "Orthopedic":
                return orthopedicBuilding.isOccupied(room); 
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
            case "Cardiology":
                cardiologyBuilding.print();
                break;
            case "THT":
                thtBuilding.print(); // New addition
                break;
            case "Orthopedic":
                orthopedicBuilding.print();
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
                    System.out.print("Enter building (Cardiology, Orthopedic, THT, Mental, Labor): ");
                    String building = scanner.nextLine();
                    String room = "";
                    if (building.equalsIgnoreCase("Mental")) {
                        hospital.printBuilding(building);
                        System.out.println("\n1st floor for normal mentally ill patients");
                        System.out.println("2nd floor for Middle-level mentally ill patients");
                        System.out.println("3rd floor for Maximum level security mentally ill patients");
                        System.out.print("Enter room number (it will be reassigned based on the patient's condition): ");
                        room = scanner.nextLine();
                    } if (building.equalsIgnoreCase("THT")) {
                        System.out.print("Enter room number (it will be reassigned based on the customer's selection): ");
                        room = scanner.nextLine();
                        hospital.printBuilding(building);
                    } if (building.equalsIgnoreCase("Orthopedic")) {
                        System.out.print("Enter room number (it will be reassigned based on the customer's selection): ");
                        room = scanner.nextLine();
                        hospital.printBuilding(building);
                    } if (building.equalsIgnoreCase("Labor")) {
                        System.out.print("Enter room number (it will be reassigned based on the customer's selection): ");
                        room = scanner.nextLine();
                        hospital.printBuilding(building); 
                    } if (building.equalsIgnoreCase("Cardiology")) {
                        System.out.print("Enter room number (it will be reassigned based on the customer's selection): ");
                        room = scanner.nextLine();
                        hospital.printBuilding(building); 
                    }
                    hospital.admitPatient(building, room, patientName, scanner);
                    break;
                case 2:
                    System.out.print("Enter room number: ");
                    room = scanner.nextLine();
                    System.out.print("Enter building (Cardiology, Orthopedic, THT, Mental, Labor): ");
                    String building2 = scanner.nextLine();
                    hospital.dischargePatient(building2, room);
                    break;
                case 3:
                    System.out.print("Enter room number: ");
                    room = scanner.nextLine();
                    System.out.print("Enter building (Cardiology, Orthopedic, THT, Mental, Labor): ");
                    String building3 = scanner.nextLine();
                    boolean isOccupied = hospital.isOccupied(building3, room);
                    System.out.println("Room " + room + " in " + building3 + " building is " + (isOccupied ? "occupied." : "empty."));
                    break;
                case 4:
                    System.out.println("do you want to see all building or a spesific one? ");
                    System.out.println("1. All buildings");
                    System.out.println("2. A spesific building");
                    System.out.print("Input number :");
                    int choice2 = scanner.nextInt();
                    if (choice2 == 1) {
                        System.out.println("Cardiology");
                        hospital.printBuilding("Cardiology");
                        System.out.println("Orthopedic");
                        hospital.printBuilding("Orthopedic");
                        System.out.println("THT");
                        hospital.printBuilding("THT");
                        System.out.println("Mental");
                        hospital.printBuilding("Mental");
                        System.out.println("Labor");
                        hospital.printBuilding("Labor");
                    } if (choice2 == 2) {
                    System.out.print("Enter building (Cardiology, Orthopedic, THT, Mental, Labor): ");
                    String building4 = scanner.nextLine();
                    hospital.printBuilding(building4);
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        scanner.close();
    }
}