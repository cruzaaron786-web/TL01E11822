package com.example.tl01e11822;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ListaActivity extends AppCompatActivity {

    ListView listView;
    DBHelper dbHelper;
    ArrayList<String> lista;
    ArrayList<Integer> listaIds;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista);

        listView = findViewById(R.id.listView);
        dbHelper = new DBHelper(this);


        lista = new ArrayList<>();
        listaIds = new ArrayList<>();

        cargarContactos();


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        listView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long idItem) {

                final String contactoSeleccionado = adapter.getItem(position);
                

                int index = lista.indexOf(contactoSeleccionado);
                if (index == -1) return;
                final int idSeleccionado = listaIds.get(index);

                String[] opciones = {"Llamar", "Compartir", "Eliminar", "Actualizar"};

                new AlertDialog.Builder(ListaActivity.this)
                        .setTitle("Opciones")
                        .setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    confirmarLlamada(contactoSeleccionado);
                                } else if (which == 1) {
                                    compartirContacto(contactoSeleccionado);
                                } else if (which == 2) {
                                    confirmarEliminacion(idSeleccionado);
                                } else if (which == 3) {
                                    LinearLayout layout = new LinearLayout(ListaActivity.this);
                                    layout.setOrientation(LinearLayout.VERTICAL);
                                    layout.setPadding(50, 20, 50, 20);


                                    final EditText etNombre = new EditText(ListaActivity.this);
                                    etNombre.setHint("Nombre");
                                    etNombre.setText(contactoSeleccionado.split("\\|")[0].trim());
                                    layout.addView(etNombre);

                                    final EditText etTelefono = new EditText(ListaActivity.this);
                                    etTelefono.setHint("Teléfono");
                                    etTelefono.setText(contactoSeleccionado.split("\\|")[1].trim());
                                    layout.addView(etTelefono);

                                    final EditText etNota = new EditText(ListaActivity.this);
                                    etNota.setHint("Nota (Opcional)");

                                    layout.addView(etNota);


                                    new AlertDialog.Builder(ListaActivity.this)
                                            .setTitle("Actualizar Contacto completo")
                                            .setView(layout)
                                            .setPositiveButton("Guardar Cambios", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int whichBtn) {
                                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                                    ContentValues values = new ContentValues();

                                                    values.put("nombre", etNombre.getText().toString());
                                                    values.put("telefono", etTelefono.getText().toString());
                                                    values.put("nota", etNota.getText().toString());


                                                    int res = db.update("contactos", values, "id=?",
                                                            new String[]{String.valueOf(idSeleccionado)});

                                                    if (res > 0) {
                                                        Toast.makeText(ListaActivity.this, "Datos actualizados", Toast.LENGTH_SHORT).show();
                                                        cargarContactos(); // Refresca la lista
                                                    }
                                                    db.close();
                                                }
                                            })
                                            .setNegativeButton("Cancelar", null)
                                            .show();
                                }
                            }
                        }).show();
            }
        });
    }

    private void cargarContactos() {
        if (lista == null) lista = new ArrayList<>();
        if (listaIds == null) listaIds = new ArrayList<>();

        lista.clear();
        listaIds.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, nombre, telefono FROM contactos", null);

        if (cursor.moveToFirst()) {
            do {
                listaIds.add(cursor.getInt(0)); // ID
                lista.add(cursor.getString(1) + " | " + cursor.getString(2)); // Nombre | Telefono
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void confirmarLlamada(String contacto) {
        String[] partes = contacto.split("\\|");
        String nombre = partes[0].trim();
        String telefono = partes[1].trim();

        new AlertDialog.Builder(this)
                .setTitle("Acción")
                .setMessage("¿Desea llamar a " + nombre + "?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + telefono));

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                        return;
                    }
                    startActivity(callIntent);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void compartirContacto(String contacto) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Contacto: " + contacto);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void confirmarEliminacion(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Estás seguro de que deseas eliminar este contacto?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete(DBHelper.TABLE_CONTACTOS, DBHelper.ID + "=?", new String[]{String.valueOf(id)});
                    db.close();
                    cargarContactos(); // Refrescar la lista
                    Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
