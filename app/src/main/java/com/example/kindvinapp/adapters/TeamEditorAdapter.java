package com.example.kindvinapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kindvinapp.R;
import com.example.kindvinapp.models.Team;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class TeamEditorAdapter extends RecyclerView.Adapter<TeamEditorAdapter.ViewHolder> {

    private final ArrayList<Team> teamList;
    private OnRemoveClickListener removeClickListener;

    public interface OnRemoveClickListener {
        void onRemoveClick(int position);
    }

    public void setOnRemoveClickListener(OnRemoveClickListener listener) {
        this.removeClickListener = listener;
    }

    public TeamEditorAdapter(ArrayList<Team> teamList) {
        this.teamList = teamList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_editor_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Team team = teamList.get(position);
        holder.teamName.setText(team.getName());
        holder.teamName.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), team.getColorResId()));
        holder.removeButton.setOnClickListener(v -> {
            if (removeClickListener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                removeClickListener.onRemoveClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView teamName;
        MaterialButton removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            teamName = itemView.findViewById(R.id.team_name_text_view);
            removeButton = itemView.findViewById(R.id.remove_team_button);
        }
    }
}

