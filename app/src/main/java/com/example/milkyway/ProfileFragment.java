package com.example.milkyway;

import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //setHasOptionsMenu(true);

        // Change fragment when favorites button is clicked
        Button btn_favorites = requireView().findViewById(R.id.btn_favorites);
        btn_favorites.setOnClickListener(nextView -> {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            Fragment favorites = new FavoritesFragment();
            ft.replace(R.id.profileFragmentContainerView, favorites);
            ft.setReorderingAllowed(true);
            ft.addToBackStack("name"); // name can be null
            ft.commit();
        });

        // Log out button
        Button logout = requireView().findViewById(R.id.logout);
        logout.setOnClickListener(nextView -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(requireActivity().getApplicationContext(),
                    LoginActivity.class));
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        if (user == null) {
            Toast.makeText(requireActivity().getApplicationContext(),
                "Failed to retrieve user data!", Toast.LENGTH_SHORT).show();
        } else {
            String userID = user.getUid();

            final TextView nameView = requireView().findViewById(R.id.userName);

            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    if (userProfile != null) {
                        String name = userProfile.name;
                        String profileTitle = "Hello " + name;
                        nameView.setText(profileTitle);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireActivity().getApplicationContext(),
                            "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}