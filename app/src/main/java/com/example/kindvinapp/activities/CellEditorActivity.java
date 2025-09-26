package com.example.kindvinapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kindvinapp.R;
import com.example.kindvinapp.models.BoardConfig;
import com.example.kindvinapp.models.Team;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class CellEditorActivity extends AppCompatActivity {

    private ArrayList<Team> teamList;

    private TextView totalCountTextView;
    private final int MAX_SPECIAL_CELLS = 48;

    private CellTypeController challengeController;
    private CellTypeController attackController;
    private CellTypeController teleF5Controller;
    private CellTypeController teleF7Controller;
    private CellTypeController teleB5Controller;
    private CellTypeController teleB7Controller;

    private final ArrayList<CellTypeController> controllers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_editor);

        Serializable serializableExtra = getIntent().getSerializableExtra("TEAMS_LIST");
        if (serializableExtra instanceof ArrayList) {
            teamList = (ArrayList<Team>) serializableExtra;
        } else {
            Toast.makeText(this, "Ошибка загрузки команд", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupControllers();
        updateTotalCount();
    }

    private void initViews() {
        totalCountTextView = findViewById(R.id.total_cells_count_text);
        findViewById(R.id.start_game_button_cell_editor).setOnClickListener(v -> startGame());
        findViewById(R.id.randomize_button).setOnClickListener(v -> randomizeCellCounts());
    }

    private void setupControllers() {
        challengeController = new CellTypeController(
                findViewById(R.id.challenge_count),
                findViewById(R.id.challenge_minus),
                findViewById(R.id.challenge_plus)
        );
        attackController = new CellTypeController(
                findViewById(R.id.attack_count),
                findViewById(R.id.attack_minus),
                findViewById(R.id.attack_plus)
        );
        teleF5Controller = new CellTypeController(
                findViewById(R.id.tele_f5_count),
                findViewById(R.id.tele_f5_minus),
                findViewById(R.id.tele_f5_plus)
        );
        teleF7Controller = new CellTypeController(
                findViewById(R.id.tele_f7_count),
                findViewById(R.id.tele_f7_minus),
                findViewById(R.id.tele_f7_plus)
        );
        teleB5Controller = new CellTypeController(
                findViewById(R.id.tele_b5_count),
                findViewById(R.id.tele_b5_minus),
                findViewById(R.id.tele_b5_plus)
        );
        teleB7Controller = new CellTypeController(
                findViewById(R.id.tele_b7_count),
                findViewById(R.id.tele_b7_minus),
                findViewById(R.id.tele_b7_plus)
        );

        controllers.add(challengeController);
        controllers.add(attackController);
        controllers.add(teleF5Controller);
        controllers.add(teleF7Controller);
        controllers.add(teleB5Controller);
        controllers.add(teleB7Controller);
    }

    private void updateTotalCount() {
        int total = 0;
        for (CellTypeController controller : controllers) {
            total += controller.getCount();
        }
        totalCountTextView.setText(getString(R.string.total_special_cells, total, MAX_SPECIAL_CELLS));
    }

    private void randomizeCellCounts() {
        for (CellTypeController controller : controllers) {
            controller.setCount(0);
        }

        Random random = new Random();
        int totalCellsToPlace = 20 + random.nextInt(16); // от 20 до 35 ячеек

        int[] weights = {4, 2, 1, 1, 1, 1}; // challenge, attack, f5, f7, b5, b7

        for (int i = 0; i < totalCellsToPlace; i++) {
            int controllerIndex = getWeightedRandom(weights, random);
            controllers.get(controllerIndex).increment();
        }

        updateTotalCount();
        Toast.makeText(this, "Сгенерирована случайная карта!", Toast.LENGTH_SHORT).show();
    }

    private int getWeightedRandom(int[] weights, Random random) {
        int totalWeight = 0;
        for (int weight : weights) {
            totalWeight += weight;
        }
        int r = random.nextInt(totalWeight);
        for (int i = 0; i < weights.length; i++) {
            r -= weights[i];
            if (r < 0) {
                return i;
            }
        }
        return 0; // fallback
    }

    private void startGame() {
        BoardConfig config = new BoardConfig();
        config.setChallengeCount(challengeController.getCount());
        config.setAttackCount(attackController.getCount());
        config.setTeleportForward5Count(teleF5Controller.getCount());
        config.setTeleportForward7Count(teleF7Controller.getCount());
        config.setTeleportBackward5Count(teleB5Controller.getCount());
        config.setTeleportBackward7Count(teleB7Controller.getCount());

        // --- ИЗМЕНЕНИЕ: Запускаем SplashActivity вместо MainActivity ---
        Intent intent = new Intent(CellEditorActivity.this, SplashActivity.class);
        intent.putExtra("TEAMS_LIST", teamList);
        intent.putExtra("BOARD_CONFIG", config);
        startActivity(intent);
    }

    private class CellTypeController {
        private final TextView countTextView;
        private int count = 0;

        CellTypeController(TextView countTextView, View minusButton, View plusButton) {
            this.countTextView = countTextView;
            updateText();

            minusButton.setOnClickListener(v -> {
                if (count > 0) {
                    count--;
                    updateText();
                    updateTotalCount();
                }
            });

            plusButton.setOnClickListener(v -> {
                int total = 0;
                for (CellTypeController c : controllers) total += c.getCount();
                if (total < MAX_SPECIAL_CELLS) {
                    count++;
                    updateText();
                    updateTotalCount();
                } else {
                    Toast.makeText(CellEditorActivity.this, "Достигнут максимум ячеек", Toast.LENGTH_SHORT).show();
                }
            });
        }

        void increment() {
            count++;
            updateText();
        }

        int getCount() {
            return count;
        }

        void setCount(int newCount) {
            count = newCount;
            updateText();
        }

        private void updateText() {
            countTextView.setText(String.valueOf(count));
        }
    }
}

