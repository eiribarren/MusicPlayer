package com.epumer.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReproductorListener} interface
 * to handle interaction events.
 * Use the {@link Reproductor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Reproductor extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private ReproductorListener mListener;
    private String titulo, urlCancion, urlPortada;

    public Reproductor() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Reproductor.
     */
    // TODO: Rename and change types and number of parameters
    public static Reproductor newInstance(Cancion cancion) {
        Reproductor fragment = new Reproductor();
        Bundle args = new Bundle();
        args.putString("titulo", cancion.getTitulo());
        args.putString("urlCancion", cancion.getUrlCancion());
        args.putString("urlPortada", cancion.getUrlPortada());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            titulo = getArguments().getString("titulo");
            urlCancion = getArguments().getString("urlCancion");
            urlPortada = getArguments().getString("urlPortada");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reproductor, container, false);

        TextView tituloView = v.findViewById(R.id.titulo);
        final ImageView portadaView = v.findViewById(R.id.portada);
        final Button playButton = v.findViewById(R.id.play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.reproducirCancion(urlCancion);
                playButton.animate().scaleX(0).scaleY(0).alpha(0).setDuration(1000).start();
                float scaleBy = 1/3f;
                portadaView.animate().scaleXBy(scaleBy).scaleYBy(scaleBy).setDuration(1000).start();
            }
        });

        tituloView.setText(titulo);
        mListener.ponerPortada(urlPortada, portadaView);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ReproductorListener) {
            mListener = (ReproductorListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ReproductorListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface ReproductorListener {
        // TODO: Update argument type and name
        void ponerPortada(String urlPortada, ImageView imagen);
        void reproducirCancion(String urlCancion);
    }
}
