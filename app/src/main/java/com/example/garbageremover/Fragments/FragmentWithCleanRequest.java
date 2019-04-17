package com.example.garbageremover.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbageremover.Adapter.RecyclerViewAdapter;
import com.example.garbageremover.CreateRequestForClean;
import com.example.garbageremover.Model.RequestModel;

import com.example.garbageremover.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentWithCleanRequest extends Fragment {
    private final int ADD_CLEAN_REQUEST = 6;
    FloatingActionButton fab ;
    DatabaseReference reference ;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ArrayList<RequestModel> requests;
    RecyclerViewAdapter adapter;
    TextView listIsEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_with_clean_request,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateRequestForClean.class);
                getActivity().startActivityForResult(intent, ADD_CLEAN_REQUEST);
            }
        });
        recyclerView = v.findViewById(R.id.recyclerView);
        listIsEmpty = v.findViewById(R.id.listIsEmpty);
        linearLayoutManager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        requests = new ArrayList<RequestModel>();


    }



    @Override
    public void onResume() {
        super.onResume();
        updateRecyclerView();
    }

    @Override
    public void onPause() {
        super.onPause();
        requests.clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        requests.clear();
    }

    private void updateRecyclerView(){
        reference = FirebaseDatabase.getInstance().getReference().child("CleanRequests");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    listIsEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    for (DataSnapshot data: dataSnapshot.getChildren()) {
                        RequestModel model = data.getValue(RequestModel.class);
                        requests.add(model);
                        adapter = new RecyclerViewAdapter(getActivity(),requests);
                    }
                    recyclerView.setAdapter(adapter);
                }else{
                    listIsEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Something wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


