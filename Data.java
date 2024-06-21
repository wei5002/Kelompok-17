import java.util.ArrayList;
import java.util.List;

public class Data {
    private List<String> patients;

    public Data() {
        patients = new ArrayList<>();
    }

    public void addPatient(String patientName) {
        patients.add(patientName);
    }

    public List<String> getPatients() {
        return patients;
    }

    public void initializeData() {
        addPatient("John Doe");
        addPatient("Jane Smith");
        addPatient("Alice Brown");
        addPatient("Bob Johnson");
        addPatient("Charlie Davis");
    }
}
