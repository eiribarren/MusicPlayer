package com.epumer.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
    implements ListaCanciones.ListaCancionesListener,
        Reproductor.ReproductorListener {

    StorageReference storageRef;
    MediaPlayer mediaPlayer;
    boolean cancionCargada;
    HashMap<String, String> cachedFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer = new MediaPlayer();
        cachedFiles = new HashMap<>();
        storageRef = FirebaseStorage.getInstance().getReference();
        mostrarListaCanciones();
    }

    public void mostrarListaCanciones() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, ListaCanciones.newInstance(), "ListaCanciones")
                .commit();

        FirebaseDatabase.getInstance().getReference().child("listaCanciones").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Cancion cancion = dataSnapshot.getValue(Cancion.class);

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
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, Reproductor.newInstance(cancion))
                .commit();
    }

    @Override
    public void ponerPortada(String urlPortada, ImageView imagen) {
        new HiloDescargaBitmap(imagen).execute(urlPortada);
    }

    @Override
    public void reproducirCancion(String urlCancion) {
        FirebaseStorage.getInstance()
                .getReference()
                .child(urlCancion)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mediaPlayer.reset();
                        try {
                            mediaPlayer.setDataSource(uri.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mediaPlayer.start();
                            }
                        });
                        mediaPlayer.prepareAsync();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        mostrarListaCanciones();
    }

    public class HiloDescargaBitmap extends AsyncTask<String, Void, Bitmap> {
        /* Este hilo se encarga de poner la imagen en el imageView */

        ImageView imagen;
        File localFile;
        boolean success, end;

        public HiloDescargaBitmap(ImageView imagen) {
            this.imagen = imagen;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            end = false;
            if (cachedFiles.containsKey(strings[0])) {
                return BitmapFactory.decodeFile(cachedFiles.get(strings[0]));
            }
            StorageReference ref = storageRef.child(strings[0]);
            localFile = null;
            try {
                localFile = File.createTempFile(strings[0], "png");
            } catch (IOException e) {
                e.printStackTrace();
            }

            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    success = true;
                    end = true;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    success = false;
                    end = true;
                }
            });

            /* Espero a que se descargue la imagen para ponerla en el ImageView */
            while (true) {
                if (end) {
                    if (success) {
                        /* Guardo la dirección del archivo temporal para no descargarlo más de una
                        vez */
                        cachedFiles.put(strings[0], localFile.getPath());
                        return BitmapFactory.decodeFile(localFile.getPath());
                    } else {
                        return null;
                    }
                } else {
                    try {
                        //Por alguna razón si no paro el hilo un poco no se carga nunca la imagen
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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
