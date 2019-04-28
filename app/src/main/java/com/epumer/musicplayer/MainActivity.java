package com.epumer.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
    implements ListaCanciones.ListaCancionesListener {

    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storageRef = FirebaseStorage.getInstance().getReference();
        mostrarListaCanciones();
    }

    public void mostrarListaCanciones() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, ListaCanciones.newInstance(), "ListaCanciones")
                .commit();

        Log.i("HEY EPUMER", "1");
        FirebaseDatabase.getInstance().getReference().child("listaCanciones").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Cancion cancion = dataSnapshot.getValue(Cancion.class);
                Log.i("HEY EPUMER", cancion.getTitulo());

                ListaCanciones listaCanciones = (ListaCanciones)getSupportFragmentManager().findFragmentByTag("ListaCanciones");

                listaCanciones.addCancion(cancion);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void iniciarCancion(Cancion cancion) {

    }

    @Override
    public void ponerPortada(String urlPortada, ImageView imagen) {
        Log.i("HEY EPUMER", urlPortada);
        new HiloDescargaBitmap(imagen).execute(urlPortada);
    }

    public class HiloDescargaBitmap extends AsyncTask<String, Void, Bitmap> {

        ImageView imagen;
        File localFile;
        boolean success, end;

        public HiloDescargaBitmap(ImageView imagen) {
            this.imagen = imagen;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            end = false;
            for (String string : strings) {
                Log.i("HEY EPUMER", string);
            }
            StorageReference ref = storageRef.child(strings[0]);
            localFile = null;
            try {
                localFile = File.createTempFile(strings[0], "png");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("HEY EPUMER", "File fail");
            }

            Log.i("HEY EPUMER", "DESCARGANDO");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.i("HEY EPUMER", "Descarga de imagen success");
                    success = true;
                    end = true;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.i("HEY EPUMER", "Descarga de imagen fail");
                    success = false;
                    end = true;
                }
            });
            while (true) {
                if (end) {
                    if (success) {
                        Log.i("HEY EPUMER", localFile.getPath());
                        return BitmapFactory.decodeFile(localFile.getPath());
                    } else {
                        Log.i("HEY EPUMER", "WHAT");
                        return null;
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imagen.setImageBitmap(bitmap);
            }
        }
    }
}