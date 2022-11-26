package com.example.milkyway;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ProgressBar;
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

    //private MenuItem logout;
    private Button logout;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private ProfileViewModel mViewModel;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        // Change fragment when favorites button is clicked
        Button btn_favorites = requireView().findViewById(R.id.btn_favorites);
        btn_favorites.setOnClickListener(nextView -> {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            Fragment favs = new FavoritesFragment();
            ft.replace(R.id.profileFragmentContainerView, favs);
            ft.setReorderingAllowed(true);
            ft.addToBackStack("name"); // name can be null
            ft.commit();
        });

        // Change fragment when history button is clicked
        Button btn_history = requireView().findViewById(R.id.btn_history);
        btn_history.setOnClickListener(nextView -> {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.profileFragmentContainerView, HistoryFragment.class, null);
            ft.setReorderingAllowed(true);
            ft.addToBackStack("name"); // name can be null
            ft.commit();
        });
        logout = requireView().findViewById(R.id.logout);
        logout.setOnClickListener(nextView -> {
            FirebaseAuth.getInstance().signOut();;
            startActivity(new Intent(requireActivity().getApplicationContext(),
                    LoginActivity.class));
        });

        // BELOW IS MAHAN

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        final TextView nameView = requireView().findViewById(R.id.userName);
//        final TextView emailView = getView().findViewById(R.id.userEmail);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String name = userProfile.name;
                    String email = userProfile.email;
                    String profileTitle = "Hello " + name;
                    nameView.setText(profileTitle);
//                    emailView.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity().getApplicationContext(),
                        "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }
}