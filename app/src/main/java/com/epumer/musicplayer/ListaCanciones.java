package com.epumer.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListaCancionesListener} interface
 * to handle interaction events.
 * Use the {@link ListaCanciones#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaCanciones extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private ListaCancionesListener mListener;
    private CancionesAdapter adapter;
    private ArrayList<Cancion> canciones;

    public ListaCanciones() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListaCanciones.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaCanciones newInstance() {
        ListaCanciones fragment = new ListaCanciones();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        canciones = new ArrayList<Cancion>();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lista_canciones, container, false);

        RecyclerView rv = v.findViewById(R.id.listaCanciones);

        rv.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayout.VERTICAL));
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        adapter = new CancionesAdapter(canciones);

        rv.setAdapter(adapter);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListaCancionesListener) {
            mListener = (ListaCancionesListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ListaCancionesListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void addCancion(Cancion cancion) {
        canciones.add(cancion);
        adapter.notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface ListaCancionesListener {
        // TODO: Update argument type and name
        void iniciarCancion(Cancion cancion);
        void ponerPortada(String urlPortada, ImageView imagen);
    }

    public class CancionesAdapter extends RecyclerView.Adapter<CancionesAdapter.CancionViewHolder> {

        ArrayList<Cancion> canciones;

        public CancionesAdapter(ArrayList<Cancion> canciones) {
            this.canciones = canciones;
        }

        @NonNull
        @Override
        public CancionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cancion_holder, parent, false);

            CancionViewHolder cvh = new CancionViewHolder(v);

            v.setOnClickListener(cvh);

            return cvh;
        }

        @Override
        public void onBindViewHolder(@NonNull CancionViewHolder viewHolder, int i) {
            viewHolder.setCancion(canciones.get(i));
            mListener.ponerPortada(canciones.get(i).urlPortada, viewHolder.imagen);
            viewHolder.titulo.setText(canciones.get(i).titulo);
        }

        @Override
        public int getItemCount() {
            return canciones.size();
        }

        public class CancionViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

            ImageView imagen;
            TextView titulo;
            Cancion cancion;

            public CancionViewHolder(@NonNull View itemView) {
                super(itemView);
                imagen = itemView.findViewById(R.id.imagenHolder);
                titulo = itemView.findViewById(R.id.textoHolder);
            }

            public void setCancion(Cancion cancion) {
                this.cancion = cancion;
            }

            @Override
            public void onClick(View v) {
                mListener.iniciarCancion(this.cancion);
            }
        }
    }
}
