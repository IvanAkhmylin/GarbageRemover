package com.example.garbageremover.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.garbageremover.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileDialog extends AppCompatDialogFragment {
    private EditText name ;
    private EditText surname ;
    private EditText city;
    private FirebaseUser mUser;
    private String mName , mSurname , mCity;

    public void EditProfileDialog(FirebaseUser user){
        this.mUser = user;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_profile_layout,null);

        builder.setView(view).setTitle("Edit Profile").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = name.getText().toString();
                String newSurname = surname.getText().toString();
                String newCity = city.getText().toString();
                if (mUser != null){
                    FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("name").setValue(newName);
                    FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("surname").setValue(newSurname);
                    FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("city").setValue(newCity);
                }else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        name = view.findViewById(R.id.change_name);
        surname = view.findViewById(R.id.change_surname);
        city = view.findViewById(R.id.change_city);
        name.setText(mName);
        surname.setText(mSurname);
        city.setText(mCity);

        return builder.create();
    }

    public void Constructor(String name, String surname, String city){
        this.mName = name;
        this.mSurname = surname;
        this.mCity = city;



    }


}
