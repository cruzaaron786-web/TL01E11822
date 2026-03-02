package com.example.tl01e11822;

public class Contacto {
    private int id;
    private String pais;
    private String nombre;
    private String telefono;
    private String nota;
    private byte[] imagen;

    public Contacto() {
    }

    public Contacto(String pais, String nombre, String telefono, String nota, byte[] imagen) {
        this.pais = pais;
        this.nombre = nombre;
        this.telefono = telefono;
        this.nota = nota;
        this.imagen = imagen;
    }
}
