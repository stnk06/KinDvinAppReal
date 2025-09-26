package com.example.kindvinapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kindvinapp.R;
import com.example.kindvinapp.adapters.TeamEditorAdapter;
import com.example.kindvinapp.models.Team;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;

public class TeamEditorActivity extends AppCompatActivity {

    private final ArrayList<Team> teamList = new ArrayList<>();
    private final int[] teamColors = {
            R.color.team_1_color, R.color.team_2_color, R.color.team_3_color, R.color.team_4_color,
            R.color.team_5_color
    };
    // --- ИЗМЕНЕНИЕ: Добавили массив иконок ---
    private final int[] pawnIcons = {
            R.drawable.ic_pawn_1, R.drawable.ic_pawn_2, R.drawable.ic_pawn_3,
            R.drawable.ic_pawn_4, R.drawable.ic_pawn_5
    };

    private TeamEditorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_editor);

        adapter = new TeamEditorAdapter(teamList);

        if (teamList.isEmpty()) {
            addDefaultTeams();
        }

        setupViewsAndAnimations();
    }

    private void setupViewsAndAnimations() {
        TextView mainTitle = findViewById(R.id.main_title);
        Button startGameButton = findViewById(R.id.start_game_button);
        Button setupTeamsButton = findViewById(R.id.setup_teams_button);
        Button editChallengesButton = findViewById(R.id.edit_challenges_button);

        Animation titleAnim = AnimationUtils.loadAnimation(this, R.anim.title_animation);
        Animation buttonsAnim = AnimationUtils.loadAnimation(this, R.anim.buttons_animation);

        mainTitle.startAnimation(titleAnim);
        startGameButton.startAnimation(buttonsAnim);
        setupTeamsButton.startAnimation(buttonsAnim);
        editChallengesButton.startAnimation(buttonsAnim);

        startGameButton.setOnClickListener(v -> {
            if (teamList.size() < 2) {
                Toast.makeText(this, "Нужно как минимум 2 команды для начала игры", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(TeamEditorActivity.this, CellEditorActivity.class);
            intent.putExtra("TEAMS_LIST", teamList);
            startActivity(intent);
        });

        setupTeamsButton.setOnClickListener(v -> showTeamSetupDialog());

        editChallengesButton.setOnClickListener(v -> {
            Intent intent = new Intent(TeamEditorActivity.this, ChallengeEditorActivity.class);
            startActivity(intent);
        });
    }


    private void showTeamSetupDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.CustomDialogTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_team_setup, null);
        builder.setView(dialogView);

        RecyclerView teamsRecyclerView = dialogView.findViewById(R.id.dialog_teams_recycler_view);
        Button addTeamButton = dialogView.findViewById(R.id.dialog_add_team_button);
        Button shuffleTeamsButton = dialogView.findViewById(R.id.dialog_shuffle_teams_button);

        teamsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamsRecyclerView.setAdapter(adapter);

        adapter.setOnRemoveClickListener(position -> {
            teamList.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, teamList.size() - position);
        });

        addTeamButton.setOnClickListener(v -> addTeam());

        shuffleTeamsButton.setOnClickListener(v -> {
            if (teamList.size() > 1) {
                Collections.shuffle(teamList);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Команды перемешаны!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Готово", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    private void addDefaultTeams() {
        // --- ИЗМЕНЕНИЕ: Используем новые иконки ---
        teamList.add(new Team("Команда 1", teamColors[0], pawnIcons[0]));
        teamList.add(new Team("Команда 2", teamColors[1], pawnIcons[1]));
        adapter.notifyDataSetChanged();
    }

    private void addTeam() {
        final int MAX_TEAMS = teamColors.length;

        if (teamList.size() >= MAX_TEAMS) {
            Toast.makeText(this, getString(R.string.max_teams_reached, MAX_TEAMS), Toast.LENGTH_SHORT).show();
            return;
        }

        int nextTeamNumber = teamList.size() + 1;
        int colorIndex = teamList.size();
        // --- ИЗМЕНЕНИЕ: Присваиваем уникальную иконку ---
        int iconRes = pawnIcons[teamList.size() % pawnIcons.length];
        teamList.add(new Team("Команда " + nextTeamNumber, teamColors[colorIndex], iconRes));
        adapter.notifyItemInserted(teamList.size() - 1);
    }
}
