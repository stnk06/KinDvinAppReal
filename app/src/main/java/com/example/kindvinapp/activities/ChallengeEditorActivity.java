package com.example.kindvinapp.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kindvinapp.R;
import com.example.kindvinapp.adapters.ChallengeEditorAdapter;
import com.example.kindvinapp.models.Challenge;
import com.example.kindvinapp.utils.ChallengeRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class ChallengeEditorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button addChallengeButton;
    private ChallengeEditorAdapter adapter;
    private List<Challenge> challenges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_editor);

        recyclerView = findViewById(R.id.challenges_recycler_view);
        addChallengeButton = findViewById(R.id.add_challenge_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addChallengeButton.setOnClickListener(v -> showEditChallengeDialog(null, -1));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChallenges();
    }

    private void loadChallenges() {
        challenges = new ArrayList<>(ChallengeRepository.getChallenges(this));
        adapter = new ChallengeEditorAdapter(challenges, this::showEditChallengeDialog, this::deleteChallenge);
        recyclerView.setAdapter(adapter);
    }

    private void deleteChallenge(int position) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Удалить челлендж?")
                .setMessage("Вы уверены, что хотите удалить этот челлендж?")
                .setPositiveButton("Да", (dialog, which) -> {
                    ChallengeRepository.removeChallenge(this, position);
                    loadChallenges();
                    Toast.makeText(this, "Челлендж удален", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    private void showEditChallengeDialog(Challenge challenge, int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_challenge, null);
        builder.setView(dialogView);

        final EditText titleEditText = dialogView.findViewById(R.id.edit_challenge_title);
        final EditText descriptionEditText = dialogView.findViewById(R.id.edit_challenge_description);

        boolean isNew = (challenge == null);
        builder.setTitle(isNew ? "Добавить челлендж" : "Редактировать челлендж");

        if (!isNew) {
            titleEditText.setText(challenge.getTitle());
            descriptionEditText.setText(challenge.getDescription());
        }

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Заголовок и содержание не могут быть пустыми", Toast.LENGTH_SHORT).show();
                return;
            }

            Challenge newChallenge = new Challenge(title, description);
            if (isNew) {
                ChallengeRepository.addChallenge(this, newChallenge);
                Toast.makeText(this, "Челлендж добавлен", Toast.LENGTH_SHORT).show();
            } else {
                ChallengeRepository.updateChallenge(this, position, newChallenge);
                Toast.makeText(this, "Челлендж обновлен", Toast.LENGTH_SHORT).show();
            }
            loadChallenges();
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
