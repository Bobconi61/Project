package com.mainPackage.randevuapp.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mainPackage.randevuapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymptomCheckerActivity extends AppCompatActivity {

    private ListView symptomsListView;
    private Map<String, String> symptomToDepartmentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_symptom_checker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        symptomsListView = findViewById(R.id.symptomsListView);
        populateSymptomData();

        List<String> symptoms = new ArrayList<>(symptomToDepartmentMap.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, symptoms);
        symptomsListView.setAdapter(adapter);

        symptomsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSymptom = symptoms.get(position);
            String recommendedDepartment = symptomToDepartmentMap.get(selectedSymptom);

            new AlertDialog.Builder(this)
                    .setTitle("Önerilen Bölüm")
                    .setMessage("\'" + selectedSymptom + "\' için önerilen bölüm: \n\n" + recommendedDepartment)
                    .setPositiveButton("OK", null)
                    .show();
        });
    }

    private void populateSymptomData() {
        symptomToDepartmentMap = new HashMap<>();
        symptomToDepartmentMap.put("Göğüs Ağrısı, Nefes Darlığı", "Kardiyoloji");
        symptomToDepartmentMap.put("Baş Ağrısı, Baş Dönmesi", "Nöroloji");
        symptomToDepartmentMap.put("Kemik Kırığı, Eklem Ağrısı", "Ortopedi");
        symptomToDepartmentMap.put("Mide Ağrısı, Karın Ağrısı", "Dahiliye");
        symptomToDepartmentMap.put("Öksürük, Ateş", "Dahiliye");
        symptomToDepartmentMap.put("Cilt Döküntüsü, Kaşıntı", "Dermatoloji (Cildiye)");
        symptomToDepartmentMap.put("Gözde Kızarıklık, Bulanık Görme", "Göz Hastalıkları");
        symptomToDepartmentMap.put("Boğaz Ağrısı, Yutkunma Güçlüğü", "Kulak Burun Boğaz (KBB)");
        symptomToDepartmentMap.put("Sık İdrara Çıkma, Yanma", "Üroloji");
        symptomToDepartmentMap.put("Diş Ağrısı, Diş Eti Kanaması", "Diş Hekimliği");
        symptomToDepartmentMap.put("Kadın Sağlığı Sorunları", "Kadın Hastalıkları ve Doğum");
        symptomToDepartmentMap.put("Çocuk Hastalıkları (0-18 Yaş)", "Pediatri (Çocuk Sağlığı ve Hastalıkları)");
        symptomToDepartmentMap.put("Unutkanlık, Davranış Değişikliği", "Psikiyatri");
    }
}
