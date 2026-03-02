package com.example.tl01e11822;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Spinner spPais;
    EditText etNombre, etTelefono, etNota;
    Button btnGuardar, btnVer;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        spPais = findViewById(R.id.spPais);
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etTelefono.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(8)
        });
        etNota = findViewById(R.id.etNota);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnVer = findViewById(R.id.btnVer);

        dbHelper = new DBHelper(this);
        String[] paises = {"Honduras (+504)", "Guatemala (+502)", "El Salvador (+503)", "Costa Rica (+506)"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, paises);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPais.setAdapter(adapter);
        btnGuardar.setOnClickListener(v -> {

            String pais = spPais.getSelectedItem().toString();
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String nota = etNota.getText().toString().trim();


            if(nombre.isEmpty()){
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Alerta")
                        .setMessage("Debe escribir un nombre")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            if(!nombre.matches("[a-zA-Z ]+")){
                etNombre.setError("Solo letras");
                return;
            }

            if(!telefono.matches("[0-9]{8}")){
                etTelefono.setError("Debe ingresar 8 números");
                return;
            }

            if(nota.isEmpty()){
                etNota.setError("Debe escribir una nota");
                return;
            }


            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("pais", pais);
            values.put("nombre", nombre);
            values.put("telefono", telefono);
            values.put("nota", nota);

            db.insert("contactos", null, values);
            byte[] imagenVacia = new byte[0];
            values.put("imagen", imagenVacia);


            db.insert("contactos", null, values);
            db.close();

            Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show();
            db.close();

            Toast.makeText(this,"Contacto guardado",Toast.LENGTH_SHORT).show();

            etNombre.setText("");
            etTelefono.setText("");
            etNota.setText("");
        });
        btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ListaActivity.class);
                startActivity(intent);
            }
        });
        }
    }
