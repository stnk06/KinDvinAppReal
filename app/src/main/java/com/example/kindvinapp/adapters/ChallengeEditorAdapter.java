package com.example.kindvinapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kindvinapp.R;
import com.example.kindvinapp.models.Challenge;

import java.util.List;

public class ChallengeEditorAdapter extends RecyclerView.Adapter<ChallengeEditorAdapter.ChallengeViewHolder> {

    private final List<Challenge> challenges;
    private final OnChallengeEditListener editListener;
    private final OnChallengeDeleteListener deleteListener;

    public interface OnChallengeEditListener {
        void onEditClick(Challenge challenge, int position);
    }

    public interface OnChallengeDeleteListener {
        void onDeleteClick(int position);
    }

    public ChallengeEditorAdapter(List<Challenge> challenges, OnChallengeEditListener editListener, OnChallengeDeleteListener deleteListener) {
        this.challenges = challenges;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.challenge_editor_item, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        Challenge challenge = challenges.get(position);
        holder.bind(challenge, position, editListener, deleteListener);
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        Button editButton;
        Button deleteButton;

        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.challenge_item_title);
            editButton = itemView.findViewById(R.id.edit_challenge_button);
            deleteButton = itemView.findViewById(R.id.delete_challenge_button);
        }

        public void bind(final Challenge challenge, final int position, final OnChallengeEditListener editListener, final OnChallengeDeleteListener deleteListener) {
            titleTextView.setText(challenge.getTitle());

            editButton.setOnClickListener(v -> {
                if (editListener != null) {
                    editListener.onEditClick(challenge, position);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(position);
                }
            });
        }
    }
}

