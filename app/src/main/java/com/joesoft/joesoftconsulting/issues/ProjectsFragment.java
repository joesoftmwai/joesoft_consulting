package com.joesoft.joesoftconsulting.issues;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.joesoft.joesoftconsulting.R;


public class ProjectsFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_projects, container, false);
        ImageView addNewProject = view.findViewById(R.id.add_new_project);
        ImageView searchProjects = view.findViewById(R.id.search_projects);
        addNewProject.setOnClickListener(this);
        searchProjects.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_new_project) {
            Intent intent = new Intent(getActivity(), NewProjectActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.search_projects) {
            // TODO: search implementation
        }
    }
}
