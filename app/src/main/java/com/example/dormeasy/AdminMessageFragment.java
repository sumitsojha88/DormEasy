package com.example.dormeasy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminMessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdminMessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminMessageFragment extends Fragment {

    RecyclerView rv;
    int REQUEST_PHONE_CALL = 100;


    List<AdminInfo> adminList  = new ArrayList<>();

    class RecyclerViewAdapter extends RecyclerView.Adapter<AdminMessageFragment.RecyclerViewAdapter.viewHolder>{

        List<AdminInfo> data;
        String [] authority = getActivity().getResources().getStringArray(R.array.departments);

        public RecyclerViewAdapter(List<AdminInfo> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public AdminMessageFragment.RecyclerViewAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_contact_rv,parent,false);
            return new AdminMessageFragment.RecyclerViewAdapter.viewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull AdminMessageFragment.RecyclerViewAdapter.viewHolder holder, final int position) {

            String auth_name = authority[Integer.valueOf(data.get(position).auth)];

            holder.name.setText(data.get(position).name);
            holder.auth.setText(auth_name);

            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+data.get(position).phone));
                    startActivity(i);
                }
            });

            holder.mail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mailIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:"+data.get(position).email));
                    startActivity(Intent.createChooser(mailIntent,"Select An Application"));
                }
            });

            holder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://api.whatsapp.com/send?phone="+data.get(position).phone;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class viewHolder extends RecyclerView.ViewHolder{

            TextView name,auth;
            ImageButton call,message,mail;

            public viewHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.student_m_rv_name);
                auth= itemView.findViewById(R.id.student_m_rv_auth);
                call = itemView.findViewById(R.id.student_m_rv_call);
                message = itemView.findViewById(R.id.student_m_rv_message);
                mail = itemView.findViewById(R.id.student_m_rv_mail);
            }
        }
    }




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AdminMessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminMessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminMessageFragment newInstance(String param1, String param2) {
        AdminMessageFragment fragment = new AdminMessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_admin_message, container, false);

        rv = rootview.findViewById(R.id.admin_contacts_rv);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    adminList.add(snap.getValue(AdminInfo.class));
                }

                RecyclerViewAdapter adapter = new AdminMessageFragment.RecyclerViewAdapter(adminList);
                rv.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);


        //Asking calling permission

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
        }


        return rootview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }  */

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
