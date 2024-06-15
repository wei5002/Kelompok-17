// dibawah ini mengimport data data yang di perlukan dalam program ini 
import java.util.ArrayList; 
import java.util.List;
import java.util.Scanner;

// class HospitalManagementSystem ini merupakan deklarasi class utama pada program ini, dan class ini menyimpan semua codenya
public class HospitalManagementSystem {

    // di bawah ini merupakan statis (hanya dapat digunakan oleh class itu saja) yang di deklarasikan di dalam class HospitalManagementSystem
    private static final int R = 256;                   // R = ukuran alphabeth yang digunakan untuk TrieNode 
    private static final int MAX_FLOORS_RUMAHSAKIT = 5; // MAX_FLOORS_RUMAHSAKIT = konstanta yang menentukan maksimum lantai di rumah sakit
    private static final int ROOMS_PER_FLOOR = 10;      // ROOMS_PER_FLOOR = konstanta yang menentukan maksumum jumlah kamar per lantai 

    // ini adalah class TrieNode yang digunakan untuk menginisialisasi anak dalam trie
    private class TrieNode {
        // atribut yang digunakan dari kelas TrieNode
        boolean isOccupied;     // menunjukan apakah tempat ini di tempati atau tidak
        String patientName;     // menyimpan nama pasien jika tempat ini di tempati
        TrieNode[] children;    // meninisialisasi array node dengan ukuran R (ASCII) 

        public TrieNode() {     //Konstruktor
            isOccupied = false; //Pada pembuatan objek TrieNode pertama, nilai awal isOccupied = false
            patientName = "";   //Default patientName adalah string kosong
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

        // dischargePatient merupakan method untuk mengeluarkan atau melepaskan pasien dari kamar di rumah sakit yang mereka tempati sebelumnya
        public void dischargePatient(String building, String room, boolean limitFloors, int maxFloors) {
            String fullRoom = building + room; // menggabungkan nama gedung dan nomor kamar 
            if (limitFloors && !isValidRoom(room, maxFloors)) { // untuk memeriksa kamarnya valid ga (ada jelasin di code atas sebelumnya)
                System.out.println("Nomor ruangan tidak valid. Gedung memiliki batas " + maxFloors + " lantai dengan 10 kamar per lantai (000-009).");
                return;
            }

            TrieNode current = root; // current melacak inisial trie 
            for (int i = 0, L = fullRoom.length(); i < L; i++) {
                // code di bawah digunakan untuk mengonversi karakter di posisi tertentu dari nomor kamar lengkap menjadi nilai ASCII
                // dan digunakan sebagai indeks untuk array children pada inisial saat ini
                int id = fullRoom.charAt(i);
                if (current.children[id] == null) { // memeriksa inisial children yang di hasilkan dari karakter saat ini telah dibuat atau belum. jika tidak, maka membuat inisial baru 
                    System.out.println("Kamar " + room + " di gedung " + building + " tidak ada.");
                    return;
                }
                current = current.children[id];
            }

            if (current.isOccupied) { // memeriksa apakah kamar saat ini sedang ditempati atau tidak
                System.out.println(current.patientName + " telah keluar dari kamar " + room + " di gedung " + building + ".");
                // jika kamar sedang ditempati maka akan keluar output yang di code atas
                // setelah outputnya keluar maka atribut isOccupied akan diatur menjadi false, dan nama pasiennya di hapus dari trie 
                current.isOccupied = false;
                current.patientName = "";
            } else {
                // jika kamar tidak di tempati maka, kamar akan di beri tahu bahwa kamar tersebut sudah kosong 
                System.out.println("Kamar " + room + " di gedung " + building + " sudah kosong.");
            }
        }

        // metod ini digunakan untuk memeriksa apakah kamar tersebut sudah di tempati atau tidak
        public boolean isOccupied(String building, String room) {
            String fullRoom = building + room; // menggabungkan nama gedung dan nomor kamar 
            TrieNode current = root; // current melacak inisial trie 
            for (int i = 0, L = fullRoom.length(); i < L; i++) {
                // code di bawah digunakan untuk mengonversi karakter di posisi tertentu dari nomor kamar lengkap menjadi nilai ASCII
                // dan digunakan sebagai indeks untuk array children pada inisial saat ini
                int id = fullRoom.charAt(i);
                if (current.children[id] == null) {  // memeriksa inisial children yang di hasilkan dari karakter saat ini telah dibuat atau belum. jika tidak, maka membuat inisial baru 
                    return false;
                }
                current = current.children[id]; // digunakan untuk memeriksa kamar tertrntu di gedung tertentu sudah di tempati atau belum
            }
            return current.isOccupied; // mengembalikan nilai atribut isOccupied dari inisial yang mewakili kamar yang sedang dipertimbangkan
        }

        // metod bantu rekursif untuk membuat daftar kamar
        public List<String> listOccupiedRooms() {
            List<String> list = new ArrayList<>(); //deklrasi variabel list dengan tipe List yang berisi elemen bertipe string
            listRooms(root, "", list);
            return list;
        }

        // metode ini secara rekursif mengunjungi semua simpul trie untuk membangun daftar kamar yangg sedang di tempati
        private void listRooms(TrieNode current, String prefix, List<String> list) {
            if (current == null) return; // jika inisial trienya sudah tidak ada atau null, maka dia akan berhenti 
            if (current.isOccupied) { // memeriksa apakah inisial saat ini mewakili kamar yang sedang ditempati, jika iya maka akan menambahkan list kamar yang ditempati 
                list.add(prefix + " (ditempati oleh " + current.patientName + ")"); // jika memenuhi syarat yang di atas, maka akan menambahkan ke dalam list 
            }
            for (int i = 0; i < R; i++) { // menjalankan ke semua inisial trie 
                if (current.children[i] != null) { // memeriksa apakah ada children dengan indeks tertentu. jika ada, artinya ada kamar yang terhubung di bawah inisial saat ini
                    listRooms(current.children[i], prefix + (char) i, list); // jika di atas true, rekursi dilakukan ke children inisial tersebut untuk melanjutkan pencarian kamar yang ditempati di bawahnya
                }
            }
        }

        // metode ini digunakan untuk mencetak semua kamar yang ditempati dalam trie 
        public void print() {
            print("", root, 0, true, true); // panggilan metode rekursif untuk mencari trie 
        }

        // metod inimencetak trie secara rekursif, memberikan prefix(bagian awal dari suatu kata) yang sesuai untuk setiap tingkat kedalaman.
        private void print(String prefix, TrieNode root, int id, boolean isTail, boolean isRoot) {
            if (!isRoot) { // memeriksa apakah inisial yang sedang dipilih bukan merupakan akar trie
                // code di bawah ini untuk mencetak informasi tentang inisial saat ini, termasuk karakternya, dan jika ditempati, dia juga mencetak nama pasien yang ditempati
                System.out.println(prefix + (isTail ? "└── " : "├── ") + (char) id + (root.isOccupied ? " *** (ditempati oleh " + root.patientName + ")" : ""));
            }

            TrieNode lastChild = null;          // lastChild  menyimpan refrensi ke  children terakhir dari inisial saat ini
            int lastChildId = 0;                // lastChildId adalah indeks dari children terakhir dari inisial saat ini
            boolean isLastChild = true;         // isLastChild menandakan apakah inisial saat ini adalah children terakhir atau bukan
            for (int i = R - 1; i >= 0; i--) {  // loop ini berjalan mundur melalui semua kemungkinan inisial children dari inisial saat ini dalam trie 
                if (root.children[i] != null) { // memeriksa apakah ada children dengan indeks tertentu. jika ada, maka itu adalah children terakhir 
                    if (isLastChild) {          // memeriksa apakah inisial saat ini adalah children terakhir atau bukan
                        isLastChild = false;    // jika anak terakhir belum di temuka, maka nilai isLastChild di atur ke false, karena inisial saat ini bukan children terakhir.
                        lastChild = root.children[i]; // jika children terakhir belum di temukan, maka variabel 'lastChild' diatur untuk menuyimpan refrensi ke children terakhir yang ditemukan
                        lastChildId = i;        // jika anak terakhir belum ditemukan, maka indeks children terakhir di perbarui
                    } else {                    // jika children terakhir sudah ditemukan, maka angkah selanjutnya adalah mencetak informasi tentang childrens yang bukan anak terakhir
                        // di bawah ini adalah pemanggilan rekursif dalam mencetak informasi tentang childrenterakhir dari inisial saat ini
                        print(prefix + (isRoot ? "" : (isTail ? "    " : "│   ")), root.children[i], i, false, false);
                    }
                }
            }
            if (lastChild != null) { // memeriksa apakah children terakhir dari inisial saat ini ada 
                // jika ada, maka informasi tentang children terakhir tersebut di cetak dengan menandai bahwa itu adalah anak terakhir
                print(prefix + (isRoot ? "" : (isTail ? "    " : "│   ")), lastChild, lastChildId, true, false);
            }
        }

        // metode di bawah ini untuk memeriksa nomor kamar yang di masukan oleh pengguna valid tidak
        private boolean isValidRoom(String room, int maxFloors) {
            if (room == null || room.length() != 4) return false; // jika nomor kamar null atau panjang tidak sama dengan 4yang di harapkan, maka akan mengembalikan metode false
            try { // metode di bawah ini digunakan untuk mengonversi bagian nomor lantai dann nomor kamar dari string ke integer.  
                // dengan memotong string nomor kamar dan menggunakan Integer.parseInt() untuk mengonversi ke integer
                int floorNumber = Integer.parseInt(room.substring(0, 1));
                int roomNumber = Integer.parseInt(room.substring(1));
                // jika nomor lantai berada di antara 1 dan maxFloors, dan nomor kamarnya sesuai, maka nomor kamar dianggap valid dan mengembalikan true pada methodnya 
                return floorNumber > 0 && floorNumber <= maxFloors && roomNumber >= 0 && roomNumber < ROOMS_PER_FLOOR;
            } catch (NumberFormatException e) { // jika terdapat kesalahan maka metode ini akan mengembalikan methodenya sebagai false, atau nomor kamr tidak valid
                return false; 
            }
        }

        // metode ini digunakan untuk mencari dan menampilkan daftar kamar yang tersedia dalam gedung yang di pilih
        public List<String> listAvailableRooms(String building, int maxFloors) {
            List<String> availableRooms = new ArrayList<>();    // ArrayList digunakan untuk menyimpan kamar yang tersedia 
            for (int floor = 1; floor <= maxFloors; floor++) {  // looping dilakukan untuk setiap lantai dalam gedung, mulai dari lantai 1 hingga 'maxFloors'
                for (int room = 0; room < ROOMS_PER_FLOOR; room++) { // untuk setiap nomor kamar dalam lantai, mulai dari 0 hingga ROOMS_PER_FLOOR -1
                    // di bawah = nomor kamar dibentuk dengan menggunakan String.format() untuk memastikan bahwa nomor kamar memiliki panjang 4 digit dengan leading 0 jika perlu
                    String roomString = String.format("%d%03d", floor, room);
                    if (!isOccupied(building, roomString)) { // jika kamar tidak di tempati, maka kamar tersebut ditambahkan ke daftar kamar yang tersedia
                        availableRooms.add(building + roomString); // nomor gedung dan nomor kamar digabungkan dan ditambahkan ke daftar kamar yang tersedia
                    }
                }
            } 
            return availableRooms; // daftar kamar yang tersedia dikembalikan untuk digunakan atau di teampilkan kepada pengguna
        }
    }

    // deklarasi variabel hospitalTrie yang merupakan instance dari kelas HospitalTrie
    // digunakan untuk mengelola informasi tentang kamar di rumah sakit 
    private HospitalTrie hospitalTrie;

    // konstruktor untuk kelas HospitalManagementSystem yang menginisialisasi variabel 'hospitalTrie' dengan membuat instance baru dari kelas 'HospitalTrie'
    public HospitalManagementSystem() {
        hospitalTrie = new HospitalTrie();
    }

    // metode ini untuk mendaftarkan pasien ke dalam rumah sakit
    // mengambil input dari pengguna seperti nama pasien, gedung, dan nomor kamar
    public void admitPatient(String building, String room, String patientName, Scanner scanner) {
        int roomType = -1;
        boolean validInput = false; // inisialisasi variabel untuk menyimpan tipe kamar dan status validasi input dari pengguna

        System.out.println("Tipe kamar untuk gedung " + building + ":");
        System.out.println("1. ICU\t\t\t\t\tRp 2.000.000,00\t\t/malam"); //list harga kamar. \t untuk tab agar tampilan rapi
        System.out.println("2. BPJS\t\t\t\t\tRp 0,00\t\t\t/malam");
        System.out.println("3. Normal\t\t\t\tRp 1.500.000,00\t\t/malam");
        System.out.println("4. VIP\t\t\t\t\tRp 15.000.000,00\t/malam");
        System.out.println("5. VVIP\t\t\t\t\tRp 20.000.000,00\t/malam");
        // mengambil input dari pengguna untuk memilih tipe kamar. melakukan validasi input untuk memastikan input adalah angka 1-5
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
        room = scanner.nextLine(); // meminta pengguna untuk memasukkan nomor kamar setelah memilih tipe kamar
        if (!isValidRoomNumber(room)) { // memeriksa apakah nomor kamar yang dimasukkan oleh pengguna valid, yaitu antara 000-009
            System.out.println("Nomor kamar tidak valid. Silakan masukkan angka antara 000 dan 009.");
            return; 
        }
        switch (roomType) { // menggabungkan nomor lantai dengan nomor kamar berdasarkan tipe kamar yang dipilih oleh pengguna
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

        switch (building) { // memilih jenis gedung yang di inginkan 
            case "1":
                // memanggil metode admitPatient dari objek hospitalTrie untuk mrndaftarkan pasien ke dalam rumah sakit.
                // yang diberikan mencakupi jenis gedung (dalam format yang di harapkan), nomor kamar, nama pasien, serta mengikuti aturan (valid)  
                hospitalTrie.admitPatient("T", room, patientName, true, MAX_FLOORS_RUMAHSAKIT);
                break; 

            case "2":
                hospitalTrie.admitPatient("C", room, patientName, true, MAX_FLOORS_RUMAHSAKIT);
                break;

            case "3":
                hospitalTrie.admitPatient("O", room, patientName, true, MAX_FLOORS_RUMAHSAKIT);
                break;

            case "4":
                hospitalTrie.admitPatient("M", room, patientName, true, MAX_FLOORS_RUMAHSAKIT);
                break;

            case "5":
                hospitalTrie.admitPatient("L", room, patientName, true, MAX_FLOORS_RUMAHSAKIT);
                break;

            default: // jika nama gedung tidak ada atau tidak valid
                System.out.println("Gedung tidak valid.");
                break;
        }
    }

    // metode ini untuk memghapus atau membuang kamar yang sudah di tempati dan dapat di tempati lagi oleh pasien baru 
    public void dischargePatient(String building, String room) {
        switch (building) {
            case "1": // memanggil metode 'dischargePatient' dari objek 'hospitalTrie' untuk membuang pasien dri kamar di gedung yang sesuai 
                hospitalTrie.dischargePatient("T", room, false, MAX_FLOORS_RUMAHSAKIT);
                break;

            case "2":
                hospitalTrie.dischargePatient("C", room, false, MAX_FLOORS_RUMAHSAKIT);
                break;

            case "3":
                hospitalTrie.dischargePatient("O", room, false, MAX_FLOORS_RUMAHSAKIT);
                break;

            case "4":
                hospitalTrie.dischargePatient("M", room, true, MAX_FLOORS_RUMAHSAKIT);
                break;

            case "5":
                hospitalTrie.dischargePatient("L", room, true, MAX_FLOORS_RUMAHSAKIT);
                break;

            default: // jika nama gedung yang di masukan tidak valid atau tidak sesuai petunjuk
                System.out.println("Gedung tidak valid.");
                break;
        }
    }

    // metode ini untuk memeriksa apakah sebuah kamar digedung rumah sakit tertentu sudah  di tempati atau belulm
    // memeriksa status kamar menggunakan isOccupied dari objek hospitalTrie
    public boolean isOccupied(String building, String room) {
        return hospitalTrie.isOccupied(building.substring(0, 1), room);
    }

    // metode ini untuk mendapatkan daftar kamar yang sudah di tempati di seluruh gedung rumah sakit
    // menggunakann metode listOccupiedRooms dari objek hospitalTrie 
    public List<String> listOccupiedRooms() {
        return hospitalTrie.listOccupiedRooms();
    }

    // metode ini untuk mencetak struktur trie yang menyimpan informasi kamar di rumah sakit.
    // dengan menggunakan metode print dari objek hospitalTrie
    public void printTrie() {
        hospitalTrie.print();
    }

    // metode ini untuk mencetak daftar kamar yang tersedia di sebuah gedung rumah sakit.
    // mengambil nomor gedung sebagai parameter
    public void listAvailableRooms(String building) {
        int maxFloors = 0; //dimula dari 0, dan selanjutnya akan dipakai untuk menyimpan maksimum lamtai untuk gedung yang dipilih
        switch (building) { // mengatur jumlah maksimum lantai sesuai dnegan nomor gedung yang dimasukkan. 
            case "1":
                maxFloors = MAX_FLOORS_RUMAHSAKIT;
                break;

            case "2":
                maxFloors = MAX_FLOORS_RUMAHSAKIT;
                break;

            case "3":
                maxFloors = MAX_FLOORS_RUMAHSAKIT;
                break;

            case "4":
                maxFloors = MAX_FLOORS_RUMAHSAKIT;
                break;

            case "5":
                maxFloors = MAX_FLOORS_RUMAHSAKIT;
                break;

            default: // jika nomor gedung tidak valid (bukan 1-5)
                System.out.println("Gedung tidak valid.");
                return;
        }
        // mendapatkan daftar kamar yang tersedia di gedung tersebut menggunakan metode listOccupiedRooms dari objek hospitalTrie
        // nama gedung diubah menjadi format yang di harapkan di metode tersebut 
        List<String> availableRooms = hospitalTrie.listAvailableRooms(building.substring(0, 1), maxFloors);
        if (availableRooms.isEmpty()) { //memeriksa apakah ada kamar yang tersedia di gedung tersebut.
            System.out.println("Tidak ada kamar yang tersedia.");
        } else {
            System.out.println("Kamar yang tersedia:");
            for (String availableRoom : availableRooms) { //pada bagian ini adalah loop yang berjalan melalui setiap elemen pada availableRooms
                System.out.println(availableRoom);
            }
        }
    }


    public static void main(String[] args) { // metode utama atau main pada program
        Scanner scanner = new Scanner(System.in); // scanner untuk menerima input 
        HospitalManagementSystem system = new HospitalManagementSystem(); // untuk menjalankan operasi pada sistem manajemen rumah sakit

        while (true) { // melakukan loop tak terbatas untuk menjalankan program secara terus menerus hingga pengguna memilih keluar
            System.out.println("\n1. Daftarkan Pasien");
            System.out.println("2. Keluarkan Pasien");
            System.out.println("3. Periksa Ketersediaan Ruangan");
            System.out.println("4. Daftar Ruangan yang Ditempati");
            System.out.println("5. Cetak Trie");
            System.out.println("6. Daftar Ruangan yang Tersedia");
            System.out.println("7. Keluar");
            System.out.print("Pilih opsi: ");
            int option = scanner.nextInt();
            scanner.nextLine();  // meminta pengguna memasukan opsi apa yang mereka inginkan

            switch (option) { // proses pilihan yang di pilih oleh pengguna 
                case 1: // sebagai pendaftaran pada pasien
                    System.out.print("Masukkan nama pasien: ");
                    String patientName = scanner.nextLine();
                    System.out.println("Masukkan gedung ! ");
                    System.out.println("1. THT");
                    System.out.println("2. Cardiology");
                    System.out.println("3. Orthopedi");
                    System.out.println("4. Mental");
                    System.out.println("5. Labor");
                    System.out.println("Pilih nomor jenis gedung yang diinginkan (1-5): ");
                    String building = scanner.nextLine();
                    String room = "";  // room will be entered based on building type
                    system.admitPatient(building, room, patientName, scanner);
                    break;
                case 2: // jika ingin mengeluarkan pasien 
                    System.out.println("Masukkan gedung ! ");
                    System.out.println("1. THT");
                    System.out.println("2. Cardiology");
                    System.out.println("3. Orthopedi");
                    System.out.println("4. Mental");
                    System.out.println("5. Labor");
                    System.out.println("Pilih nomor jenis gedung yang diinginkan (1-5): ");
                    building = scanner.nextLine();
                    System.out.print("Masukkan nomor kamar: ");
                    room = scanner.nextLine();
                    system.dischargePatient(building, room);
                    break;
                case 3: // jika ingin mengkonfirmasi kepada pasien, kamar yang mereka pilih apakah sudah di tempati
                    System.out.println("Masukkan gedung ! ");
                    System.out.println("1. THT");
                    System.out.println("2. Cardiology");
                    System.out.println("3. Orthopedi");
                    System.out.println("4. Mental");
                    System.out.println("5. Labor");
                    System.out.println("Pilih nomor jenis gedung yang diinginkan (1-5): ");
                    building = scanner.nextLine();
                    System.out.print("Masukkan nomor kamar: ");
                    room = scanner.nextLine();
                    if (system.isOccupied(building, room)) {
                        System.out.println("Kamar sudah ditempati.");
                    } else {
                        System.out.println("Kamar tersedia.");
                    }
                    break;
                case 4: // jika ingin melihat list kamar yang sudah di tempati seluruhnya dalam rumah sakit 
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
                case 5: // jika ingin melihat tampilan trie
                    system.printTrie();
                    break;
                case 6: // jika ingin melihat ruangan mana saja yang masih kosong dengan memasukkan nama gedung maka akan menampilakn semua kamar yang belum di tempati
                    System.out.println("Masukkan gedung ! ");
                    System.out.println("1. THT");
                    System.out.println("2. Cardiology");
                    System.out.println("3. Orthopedi");
                    System.out.println("4. Mental");
                    System.out.println("5. Labor");
                    System.out.println("Pilih nomor jenis gedung yang diinginkan (1-5): ");
                    building = scanner.nextLine();
                    system.listAvailableRooms(building);
                    break;
                case 7: // keluar dari program 
                    System.out.println("Keluar...");
                    scanner.close();
                    return;
                default: // tidak valid
                    System.out.println("Opsi tidak valid. Silakan coba lagi.");
                    break;
            }
        }
    }

    // metode yang memeriksa apakah nomor kamar yang di berikan sesuai dengan pola yang diinginkan yaitu 000-009
    private static boolean isValidRoomNumber(String room) {
        return room.matches("00[0-9]"); // jika karakter pertama bukan 00 maka akan invalid, dan karakter terakhir bukan angka maka akan invalid juga 
    }
}
