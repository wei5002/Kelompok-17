// dibawah ini mengimport data data yang di perlukan dalam program ini 
import java.util.ArrayList; 
import java.util.List;
import java.util.Scanner;

// class HospitalManagementSystem ini merupakan deklarasi class utama pada program ini, dan class ini menyimpan semua codenya
public class HospitalManagementSystem {

    // di bawah ini merupakan statis (hanya dapat digunakan oleh class itu saja) yang di deklarasikan di dalam class HospitalManagementSystem
    private static final int R = 256;                   // R = ukuran alfabed yang digunakan untuk TrieNode 
    private static final int MAX_FLOORS_RUMAHSAKIT = 5; // MAX_FLOORS_RUMAHSAKIT = konstanta yang menentukan maksimum lantai di rumah sakit
    private static final int ROOMS_PER_FLOOR = 10;      // ROOMS_PER_FLOOR = konstanta yang menentukan maksumum jumlah kamar per lantai 

    // ini adalah class TrieNode yang digunakan untuk menginisialisasi anak dalam trie
    private class TrieNode {
        // atribut yang digunakan dari kelas TrieNode
        boolean isOccupied;     // menunjukan apakah tempat ini di tempati atau tidak
        String patientName;     // menyimpan nama pasien jika tempat ini di tempati
        TrieNode[] children;    // meninisialisasi array node dengan ukuran R (ASCII) 

        public TrieNode() {
            isOccupied = false;
            patientName = "";
            children = new TrieNode[R];
        }
    }

     private class HospitalTrie {
        private TrieNode root;

        // di bawah ini merupakan konstruktor untuk class HospitalTrie. 
        // ketika sebuah objek HospitalTrie dibuat, akan menginisialkan root baru untuk trie 
        public HospitalTrie() {
            root = new TrieNode();
        }

        // di bawah ini merupakan method yang digunakan untuk mendaftarkan pasien ke dalam kamar di dalam rumah sakit
        // dia mengambil beberapa parameter seperti gedung, nomor kamar, nama psien, limitlantai(apakah melewati batas lantai, dan maksimal lantai)  
        public void admitPatient(String building, String room, String patientName, boolean limitFloors, int maxFloors) {
            // code fullRoom digunakan untuk menggabungkan nama gedung dengan nomor kamar untuk membentuk nomor kamar pasien secara lengkap atau full
            String fullRoom = building + room;
            if (limitFloors && !isValidRoom(room, maxFloors)) { // line ini digunakan untuk memeriksa apakah pembatasan jumlah lantai dan jika nomor kamar yang dimasukan sesuai dengan jumlah maksimum lantai
                System.out.println("Nomor ruangan tidak valid. Gedung memiliki batas " + maxFloors + " lantai dengan 10 kamar per lantai (000-009).");
                return;
            }

            // currett digunakan untuk melacak inisial trie 
            TrieNode current = root;
            // for code ini untuk menjalankan loop sebanyak panjang nomor kamar lengkap untuk memasukkan setiap karakter ke dalam trie 
            for (int i = 0, L = fullRoom.length(); i < L; i++) {
                // code di bawah digunakan untuk mengonversi karakter di posisi tertentu dari nomor kamar lengkap menjadi nilai ASCII
                // dan digunakan sebagai indeks untuk array children pada inisial saat ini
                int id = fullRoom.charAt(i);
                if (current.children[id] == null) { // memeriksa inisial children yang di hasilkan dari karakter saat ini telah dibuat atau belum. jika tidak, maka membuat inisial baru 
                    current.children[id] = new TrieNode();
                }
                current = current.children[id];
            }
            if (!current.isOccupied) { // memeriksa kamar yang di pilih apakah sudah di tempati belum. jika belum, maka pasien dapat ditempatkan di ruangan yang ia pilih
                current.isOccupied = true;
                current.patientName = patientName;
                System.out.println(patientName + " telah diterima di kamar " + room + " di gedung " + building + ".");
            } else {
                System.out.println("Kamar " + room + " di gedung " + building + " sudah ditempati oleh " + current.patientName + ".");
            }
        }

        //
        public void dischargePatient(String building, String room, boolean limitFloors, int maxFloors) {
            String fullRoom = building + room;
            if (limitFloors && !isValidRoom(room, maxFloors)) {
                System.out.println("Nomor ruangan tidak valid. Gedung memiliki batas " + maxFloors + " lantai dengan 10 kamar per lantai (000-009).");
                return;
            }

            TrieNode current = root;
            for (int i = 0, L = fullRoom.length(); i < L; i++) {
                int id = fullRoom.charAt(i);
                if (current.children[id] == null) {
                    System.out.println("Kamar " + room + " di gedung " + building + " tidak ada.");
                    return;
                }
                current = current.children[id];
            }
            if (current.isOccupied) {
                System.out.println(current.patientName + " telah keluar dari kamar " + room + " di gedung " + building + ".");
                current.isOccupied = false;
                current.patientName = "";
            } else {
                System.out.println("Kamar " + room + " di gedung " + building + " sudah kosong.");
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
                list.add(prefix + " (ditempati oleh " + current.patientName + ")");
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
                System.out.println(prefix + (isTail ? "└── " : "├── ") + (char) id + (root.isOccupied ? " *** (ditempati oleh " + root.patientName + ")" : ""));
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

        private boolean isValidRoom(String room, int maxFloors) {
            if (room == null || room.length() != 4) return false;
            try {
                int floorNumber = Integer.parseInt(room.substring(0, 1));
                int roomNumber = Integer.parseInt(room.substring(1));
                return floorNumber > 0 && floorNumber <= maxFloors && roomNumber >= 0 && roomNumber < ROOMS_PER_FLOOR;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        public List<String> listAvailableRooms(String building, int maxFloors) {
            List<String> availableRooms = new ArrayList<>();
            for (int floor = 1; floor <= maxFloors; floor++) {
                for (int room = 0; room < ROOMS_PER_FLOOR; room++) {
                    String roomString = String.format("%d%03d", floor, room);
                    if (!isOccupied(building, roomString)) {
                        availableRooms.add(building + roomString);
                    }
                }
            }
            return availableRooms;
        }
    }

    private HospitalTrie hospitalTrie;

    public HospitalManagementSystem() {
        hospitalTrie = new HospitalTrie();
    }

    public void admitPatient(String building, String room, String patientName, Scanner scanner) {
        int roomType = -1;
        boolean validInput = false;

        System.out.println("Tipe kamar untuk gedung " + building + ":");
        System.out.println("1. ICU\t\t\t\t\tRp 2.000.000,00\t\t/malam");
        System.out.println("2. BPJS\t\t\t\t\tRp 0,00\t\t\t/malam");
        System.out.println("3. Normal\t\t\t\tRp 1.500.000,00\t\t/malam");
        System.out.println("4. VIP\t\t\t\t\tRp 15.000.000,00\t/malam");
        System.out.println("5. VVIP\t\t\t\t\tRp 20.000.000,00\t/malam");
        while (!validInput) {
            System.out.print("Pilih tipe kamar (1-5): ");
            if (scanner.hasNextInt()) {
                roomType = scanner.nextInt();
                if (roomType >= 1 && roomType <= 5) {
                    validInput = true;
                } else {
                    System.out.println("Tipe kamar tidak valid. Silakan masukkan angka antara 1 dan 5.");
                }
            } else {
                System.out.println("Input tidak valid. Silakan masukkan angka.");
                scanner.next();  // Consume invalid input
            }
        }
        scanner.nextLine();  // Consume newline
        System.out.print("Masukkan nomor kamar (000-009): ");
        room = scanner.nextLine();
        if (!isValidRoomNumber(room)) {
            System.out.println("Nomor kamar tidak valid. Silakan masukkan angka antara 000 dan 009.");
            return;
        }
        switch (roomType) {
            case 1:
                room = "1" + room; // ICU on the 1st floor
                break;
            case 2:
                room = "2" + room; // BPJS on the 2nd floor
                break;
            case 3:
                room = "3" + room; // Normal on the 3rd floor
                break;
            case 4:
                room = "4" + room; // VIP on the 4th floor
                break;
            case 5:
                room = "5" + room; // VVIP on the 5th floor
                break;
        }

        switch (building) {
            case "THT":
                hospitalTrie.admitPatient("T", room, patientName, true, MAX_FLOORS_RUMAHSAKIT);
                break;

            case "Cardiology":
                hospitalTrie.admitPatient("C", room, patientName, true, MAX_FLOORS_RUMAHSAKIT);
                break;

            case "Orthopedi":
                hospitalTrie.admitPatient("O", room, patientName, true, MAX_FLOORS_RUMAHSAKIT);
                break;

            case "Mental":
                hospitalTrie.admitPatient("M", room, patientName, true, MAX_FLOORS_RUMAHSAKIT);
                break;

            case "Labor":
                hospitalTrie.admitPatient("L", room, patientName, true, MAX_FLOORS_RUMAHSAKIT);
                break;

            default:
                System.out.println("Gedung tidak valid.");
                break;
        }
    }

    public void dischargePatient(String building, String room) {
        switch (building) {
            case "THT":
                hospitalTrie.dischargePatient("T", room, false, MAX_FLOORS_RUMAHSAKIT);
                break;

            case "Cardiology":
                hospitalTrie.dischargePatient("C", room, false, MAX_FLOORS_RUMAHSAKIT);
                break;

            case "Orthopedi":
                hospitalTrie.dischargePatient("O", room, false, MAX_FLOORS_RUMAHSAKIT);
                break;

            case "Mental":
                hospitalTrie.dischargePatient("M", room, true, MAX_FLOORS_RUMAHSAKIT);
                break;

            case "Labor":
                hospitalTrie.dischargePatient("L", room, true, MAX_FLOORS_RUMAHSAKIT);
                break;

            default:
                System.out.println("Gedung tidak valid.");
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

    public void listAvailableRooms(String building) {
        int maxFloors = 0;
        switch (building) {
            case "THT":
                maxFloors = MAX_FLOORS_RUMAHSAKIT;
                break;

            case "Cardiology":
                maxFloors = MAX_FLOORS_RUMAHSAKIT;
                break;

            case "Orthopedi":
                maxFloors = MAX_FLOORS_RUMAHSAKIT;
                break;

            case "Mental":
                maxFloors = MAX_FLOORS_RUMAHSAKIT;
                break;

            case "Labor":
                maxFloors = MAX_FLOORS_RUMAHSAKIT;
                break;

            default:
                System.out.println("Gedung tidak valid.");
                return;
        }
        List<String> availableRooms = hospitalTrie.listAvailableRooms(building.substring(0, 1), maxFloors);
        if (availableRooms.isEmpty()) {
            System.out.println("Tidak ada kamar yang tersedia.");
        } else {
            System.out.println("Kamar yang tersedia:");
            for (String availableRoom : availableRooms) {
                System.out.println(availableRoom);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HospitalManagementSystem system = new HospitalManagementSystem();

        while (true) {
            System.out.println("\n1. Daftarkan Pasien");
            System.out.println("2. Keluarkan Pasien");
            System.out.println("3. Periksa Ketersediaan Ruangan");
            System.out.println("4. Daftar Ruangan yang Ditempati");
            System.out.println("5. Cetak Trie");
            System.out.println("6. Daftar Ruangan yang Tersedia");
            System.out.println("7. Keluar");
            System.out.print("Pilih opsi: ");
            int option = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (option) {
                case 1:
                    System.out.print("Masukkan nama pasien: ");
                    String patientName = scanner.nextLine();
                    System.out.print("Masukkan gedung (THT, Cardiology, Orthopedi, Mental, Labor): ");
                    String building = scanner.nextLine();
                    String room = "";  // room will be entered based on building type
                    system.admitPatient(building, room, patientName, scanner);
                    break;
                case 2:
                    System.out.print("Masukkan gedung (THT, Cardiology, Orthopedi, Mental, Labor): ");
                    building = scanner.nextLine();
                    System.out.print("Masukkan nomor kamar: ");
                    room = scanner.nextLine();
                    system.dischargePatient(building, room);
                    break;
                case 3:
                    System.out.print("Masukkan gedung (THT, Cardiology, Orthopedi, Mental, Labor): ");
                    building = scanner.nextLine();
                    System.out.print("Masukkan nomor kamar: ");
                    room = scanner.nextLine();
                    if (system.isOccupied(building, room)) {
                        System.out.println("Kamar sudah ditempati.");
                    } else {
                        System.out.println("Kamar tersedia.");
                    }
                    break;
                case 4:
                    List<String> occupiedRooms = system.listOccupiedRooms();
                    if (occupiedRooms.isEmpty()) {
                        System.out.println("Tidak ada ruangan yang ditempati.");
                    } else {
                        System.out.println("Ruangan yang ditempati:");
                        for (String occupiedRoom : occupiedRooms) {
                            System.out.println(occupiedRoom);
                        }
                    }
                    break;
                case 5:
                    system.printTrie();
                    break;
                case 6:
                    System.out.print("Masukkan gedung (THT, Cardiology, Orthopedi, Mental, Labor): ");
                    building = scanner.nextLine();
                    system.listAvailableRooms(building);
                    break;
                case 7:
                    System.out.println("Keluar...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opsi tidak valid. Silakan coba lagi.");
                    break;
            }
        }
    }

    private static boolean isValidRoomNumber(String room) {
        return room.matches("00[0-9]");
    }
}
