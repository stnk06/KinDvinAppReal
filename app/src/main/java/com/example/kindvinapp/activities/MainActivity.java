package com.example.kindvinapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kindvinapp.R;
import com.example.kindvinapp.adapters.PlayerStatusAdapter;
import com.example.kindvinapp.models.BoardConfig;
import com.example.kindvinapp.models.CellType;
import com.example.kindvinapp.models.Challenge;
import com.example.kindvinapp.models.GameCell;
import com.example.kindvinapp.models.Team;
import com.example.kindvinapp.utils.ChallengeRepository;
import com.example.kindvinapp.utils.SoundManager;
import com.example.kindvinapp.views.PathView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class MainActivity extends AppCompatActivity {

    private static final int BOARD_SIZE = 50;
    private static final long DICE_ANIMATION_INTERVAL = 50;
    private static final int DICE_ANIMATION_STEPS = 20;

    private ConstraintLayout gameContainer;
    private PathView pathView;
    private ImageView animatedPawn;
    private Button rollDiceButton;
    private ImageView diceResultImageView;
    private RecyclerView playersRecyclerView;
    private PlayerStatusAdapter playerStatusAdapter;
    private SeekBar zoomSlider;
    private View overviewButton;
    private KonfettiView konfettiView;

    private List<GameCell> cellList;
    private List<Team> teamList;
    private List<Challenge> challenges;
    private List<PointF> cellCoordinates;
    private List<FrameLayout> cellViews = new ArrayList<>();

    private int currentPlayerIndex = 0;
    private final Handler animationHandler = new Handler(Looper.getMainLooper());
    private boolean isAnimating = false;
    private boolean isGameOver = false;
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        initViews();
        loadDataFromIntent();

        if (teamList == null || teamList.isEmpty()) {
            Toast.makeText(this, "Ошибка загрузки данных игры.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setupUI();
        setupBoard();
    }

    private void initViews() {
        gameContainer = findViewById(R.id.game_container);
        pathView = findViewById(R.id.path_view);
        animatedPawn = findViewById(R.id.animated_pawn);
        rollDiceButton = findViewById(R.id.roll_dice_button);
        diceResultImageView = findViewById(R.id.dice_result_image);
        playersRecyclerView = findViewById(R.id.players_recycler_view);
        zoomSlider = findViewById(R.id.zoom_slider);
        overviewButton = findViewById(R.id.overview_button);
        konfettiView = findViewById(R.id.konfettiView);
    }

    private void loadDataFromIntent() {
        Serializable serializableExtra = getIntent().getSerializableExtra("TEAMS_LIST");
        if (serializableExtra instanceof ArrayList) {
            teamList = (ArrayList<Team>) serializableExtra;
        }

        BoardConfig boardConfig = (BoardConfig) getIntent().getSerializableExtra("BOARD_CONFIG");
        if (boardConfig == null) {
            finish();
            return;
        }

        challenges = new ArrayList<>(ChallengeRepository.getChallenges(this));
        Collections.shuffle(challenges);
        initializeBoard(boardConfig);
    }

    private void setupUI() {
        // --- ИСПРАВЛЕНИЕ: Передаем Context в конструктор адаптера ---
        playerStatusAdapter = new PlayerStatusAdapter(teamList, this);
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playersRecyclerView.setAdapter(playerStatusAdapter);

        updateCurrentPlayerUI();

        rollDiceButton.setOnClickListener(v -> {
            if (!isAnimating && !isGameOver) {
                rollDice();
            }
        });

        zoomSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float scale = 1.0f + (progress / 100.0f);
                gameContainer.setScaleX(scale);
                gameContainer.setScaleY(scale);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        overviewButton.setOnClickListener(v -> {
            zoomSlider.setProgress(0);
            gameContainer.animate().scaleX(1).scaleY(1).translationX(0).translationY(0).setDuration(300).start();
        });
    }

    private void initializeBoard(BoardConfig boardConfig) {
        cellList = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            cellList.add(new GameCell(i + 1));
        }

        ArrayList<CellType> specialCells = new ArrayList<>();
        for (int i = 0; i < boardConfig.getAttackCount(); i++) specialCells.add(CellType.ATTACK);
        for (int i = 0; i < boardConfig.getChallengeCount(); i++) specialCells.add(CellType.CHALLENGE);
        for (int i = 0; i < boardConfig.getTeleportForward5Count(); i++) specialCells.add(CellType.TELEPORT_FORWARD_5);
        for (int i = 0; i < boardConfig.getTeleportForward7Count(); i++) specialCells.add(CellType.TELEPORT_FORWARD_7);
        for (int i = 0; i < boardConfig.getTeleportBackward5Count(); i++) specialCells.add(CellType.TELEPORT_BACKWARD_5);
        for (int i = 0; i < boardConfig.getTeleportBackward7Count(); i++) specialCells.add(CellType.TELEPORT_BACKWARD_7);

        Collections.shuffle(specialCells);

        List<Integer> availableIndexes = new ArrayList<>();
        for (int i = 1; i < BOARD_SIZE - 1; i++) {
            availableIndexes.add(i);
        }
        Collections.shuffle(availableIndexes);

        for (int i = 0; i < Math.min(specialCells.size(), availableIndexes.size()); i++) {
            cellList.get(availableIndexes.get(i)).setType(specialCells.get(i));
        }
    }

    private void setupBoard() {
        gameContainer.post(() -> {
            cellCoordinates = generateCellCoordinates(gameContainer.getWidth(), gameContainer.getHeight());
            pathView.setPath(cellCoordinates);

            LayoutInflater inflater = LayoutInflater.from(this);
            for (int i = 0; i < BOARD_SIZE; i++) {
                PointF coords = cellCoordinates.get(i);
                GameCell cellData = cellList.get(i);

                FrameLayout cellView = (FrameLayout) inflater.inflate(R.layout.game_cell, gameContainer, false);
                cellView.setX(coords.x);
                cellView.setY(coords.y);

                TextView numberText = cellView.findViewById(R.id.cell_number);
                numberText.setText(String.valueOf(i + 1));

                CardView cellCard = cellView.findViewById(R.id.cell_card);
                ImageView specialIcon = cellView.findViewById(R.id.special_cell_icon);

                int cellColor = ContextCompat.getColor(this, R.color.cell_empty_background);
                int iconRes = 0;

                switch (cellData.getType()) {
                    case CHALLENGE:
                        cellColor = ContextCompat.getColor(this, R.color.cell_challenge_background);
                        iconRes = R.drawable.ic_challenge_star;
                        break;
                    case ATTACK:
                        cellColor = ContextCompat.getColor(this, R.color.cell_attack_background);
                        iconRes = R.drawable.ic_attack_sword;
                        break;
                    case TELEPORT_FORWARD_5:
                    case TELEPORT_FORWARD_7:
                        cellColor = ContextCompat.getColor(this, R.color.cell_teleport_forward_background);
                        iconRes = R.drawable.ic_teleport_forward;
                        break;
                    case TELEPORT_BACKWARD_5:
                    case TELEPORT_BACKWARD_7:
                        cellColor = ContextCompat.getColor(this, R.color.cell_teleport_backward_background);
                        iconRes = R.drawable.ic_teleport_backward;
                        break;
                }
                cellCard.setCardBackgroundColor(cellColor);

                if (iconRes != 0) {
                    specialIcon.setImageResource(iconRes);
                    specialIcon.setVisibility(View.VISIBLE);
                } else {
                    specialIcon.setVisibility(View.GONE);
                }


                gameContainer.addView(cellView);
                cellViews.add(cellView);
            }
            updateAllPawnPositions();
        });
    }

    private List<PointF> generateCellCoordinates(int width, int height) {
        List<PointF> coordinates = new ArrayList<>();
        float cellDiameter = getResources().getDimension(R.dimen.cell_diameter);
        int cols = 8;
        int rows = (BOARD_SIZE + cols - 1) / cols;

        float horizontalSpacing = (width - cols * cellDiameter) / (cols + 1);
        float verticalSpacing = (height - rows * cellDiameter) / (rows + 1);
        float effectiveSpacingX = cellDiameter + horizontalSpacing;
        float effectiveSpacingY = cellDiameter + verticalSpacing;

        for (int i = 0; i < BOARD_SIZE; i++) {
            int row = i / cols;
            int col = i % cols;

            if (row % 2 != 0) {
                col = cols - 1 - col;
            }

            float randomOffsetX = (random.nextFloat() - 0.5f) * horizontalSpacing * 0.4f;
            float randomOffsetY = (random.nextFloat() - 0.5f) * verticalSpacing * 0.4f;


            float x = horizontalSpacing + col * effectiveSpacingX + randomOffsetX;
            float y = verticalSpacing + row * effectiveSpacingY + randomOffsetY;

            coordinates.add(new PointF(x, y));
        }
        return coordinates;
    }


    private void updateAllPawnPositions() {
        for (FrameLayout cellView : cellViews) {
            FrameLayout pawnsContainer = cellView.findViewById(R.id.pawns_container);
            pawnsContainer.removeAllViews();
        }

        for (Team team : teamList) {
            int pos = team.getPosition();
            if (pos > 0 && pos <= cellViews.size()) {
                FrameLayout cellView = cellViews.get(pos - 1);
                addPawnToCell(team, cellView);
            }
        }
    }

    private void addPawnToCell(Team team, FrameLayout cellView) {
        FrameLayout pawnsContainer = cellView.findViewById(R.id.pawns_container);
        ImageView pawnView = new ImageView(this);
        pawnView.setImageResource(team.getPawnIconResId());
        pawnView.setColorFilter(ContextCompat.getColor(this, team.getColorResId()));
        int size = (int) getResources().getDimension(R.dimen.pawn_size_small);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        pawnsContainer.addView(pawnView, params);
    }


    private void rollDice() {
        isAnimating = true;
        rollDiceButton.setEnabled(false);
        SoundManager.playDiceRoll(this);

        final int diceRoll = random.nextInt(6) + 1;

        animateDiceRoll(DICE_ANIMATION_STEPS, () -> {
            int[] diceDrawables = {R.drawable.dice_1, R.drawable.dice_2, R.drawable.dice_3, R.drawable.dice_4, R.drawable.dice_5, R.drawable.dice_6};
            diceResultImageView.setImageResource(diceDrawables[diceRoll - 1]);

            Team currentTeam = teamList.get(currentPlayerIndex);
            int startPos = currentTeam.getPosition();
            int endPos = Math.min(startPos + diceRoll, BOARD_SIZE);

            animateMove(currentTeam, startPos, endPos, true);
        });
    }

    private void animateDiceRoll(int stepsLeft, Runnable onFinished) {
        if (stepsLeft <= 0) {
            onFinished.run();
            return;
        }
        int randomFace = random.nextInt(6);
        int[] diceDrawables = {R.drawable.dice_1, R.drawable.dice_2, R.drawable.dice_3, R.drawable.dice_4, R.drawable.dice_5, R.drawable.dice_6};
        diceResultImageView.setImageResource(diceDrawables[randomFace]);
        animationHandler.postDelayed(() -> animateDiceRoll(stepsLeft - 1, onFinished), DICE_ANIMATION_INTERVAL);
    }

    private void animateMove(Team team, int from, int to, boolean isForward) {
        if (cellCoordinates == null || from <= 0 || to <= 0 || from > cellCoordinates.size() || to > cellCoordinates.size()) {
            updateTeamPosition(team, to);
            handleMoveCompletion(team);
            return;
        }

        SoundManager.playPawnMove(this);

        PointF startPoint = cellCoordinates.get(from - 1);
        PointF endPoint = cellCoordinates.get(to - 1);
        float cellDiameter = getResources().getDimension(R.dimen.cell_diameter);

        animatedPawn.setImageResource(team.getPawnIconResId());
        animatedPawn.setColorFilter(ContextCompat.getColor(this, team.getColorResId()));
        animatedPawn.setVisibility(View.VISIBLE);
        animatedPawn.setX(startPoint.x + cellDiameter / 4);
        animatedPawn.setY(startPoint.y + cellDiameter / 4);

        team.setPosition(0);
        updateAllPawnPositions();

        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(animatedPawn, "x", endPoint.x + cellDiameter / 4);
        ObjectAnimator yAnimator = ObjectAnimator.ofFloat(animatedPawn, "y", endPoint.y + cellDiameter / 4);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(animatedPawn, "rotation", 0f, isForward ? 360f : -360f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(xAnimator, yAnimator, rotation);
        animatorSet.setDuration(800);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatedPawn.setVisibility(View.GONE);
                updateTeamPosition(team, to);
                handleMoveCompletion(team);
            }
        });
        animatorSet.start();
    }


    private void updateTeamPosition(Team team, int newPosition) {
        team.setPosition(newPosition);
        updateAllPawnPositions();
        playerStatusAdapter.notifyDataSetChanged();
    }

    private void handleMoveCompletion(Team team) {
        if (checkWinCondition(team)) return;
        handleSpecialCell(team);
    }

    private boolean checkWinCondition(Team team) {
        if (team.getPosition() >= BOARD_SIZE) {
            isGameOver = true;
            rollDiceButton.setEnabled(false);
            showWinDialog(team);
            return true;
        }
        return false;
    }

    private void handleSpecialCell(Team team) {
        int position = team.getPosition();
        if (position <= 0 || position > cellList.size()) {
            endTurn();
            return;
        }

        GameCell currentCell = cellList.get(position - 1);
        CellType type = currentCell.getType();
        int newPosition;

        switch (type) {
            case ATTACK:
                animationHandler.postDelayed(() -> showAttackDialog(team), 500);
                return;
            case CHALLENGE:
                animationHandler.postDelayed(() -> showChallengeDialog(team), 500);
                return;
            case TELEPORT_FORWARD_5:
            case TELEPORT_FORWARD_7:
            case TELEPORT_BACKWARD_5:
            case TELEPORT_BACKWARD_7:
                SoundManager.playTeleport(this);
                Toast.makeText(this, "Телепорт!", Toast.LENGTH_SHORT).show();
                if (type == CellType.TELEPORT_FORWARD_5) newPosition = Math.min(position + 5, BOARD_SIZE);
                else if (type == CellType.TELEPORT_FORWARD_7) newPosition = Math.min(position + 7, BOARD_SIZE);
                else if (type == CellType.TELEPORT_BACKWARD_5) newPosition = Math.max(position - 5, 1);
                else newPosition = Math.max(position - 7, 1);
                animateMove(team, position, newPosition, newPosition > position);
                break;
            default:
                endTurn();
        }
    }

    private void showAttackDialog(Team attacker) {
        List<Team> targets = new ArrayList<>();
        for (Team team : teamList) {
            if (team != attacker && team.getPosition() > 1) {
                targets.add(team);
            }
        }

        if (targets.isEmpty()) {
            Toast.makeText(this, "Некого атаковать!", Toast.LENGTH_SHORT).show();
            endTurn();
            return;
        }

        String[] targetNames = new String[targets.size()];
        for (int i = 0; i < targets.size(); i++) {
            targetNames[i] = targets.get(i).getName();
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Выберите цель для атаки")
                .setItems(targetNames, (dialog, which) -> {
                    Team target = targets.get(which);
                    int currentTargetPos = target.getPosition();
                    animateMove(target, currentTargetPos, 1, false);
                    Toast.makeText(MainActivity.this, target.getName() + " отправлена на старт!", Toast.LENGTH_SHORT).show();
                })
                .setCancelable(false)
                .setOnDismissListener(dialog -> endTurn())
                .show();
    }

    private void showChallengeDialog(Team currentTeam) {
        if (challenges.isEmpty()) {
            Toast.makeText(this, "Челленджи закончились!", Toast.LENGTH_SHORT).show();
            endTurn();
            return;
        }

        Challenge randomChallenge = challenges.remove(0);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_challenge_bet, null);
        TextView title = dialogView.findViewById(R.id.challenge_title);
        TextView description = dialogView.findViewById(R.id.challenge_description);
        NumberPicker betPicker = dialogView.findViewById(R.id.bet_picker);

        title.setText(randomChallenge.getTitle());
        description.setText(randomChallenge.getDescription());

        betPicker.setMinValue(1);
        betPicker.setMaxValue(6);
        betPicker.setValue(3);

        new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton("Выполнил", (dialog, which) -> movePlayerAfterChallenge(currentTeam, betPicker.getValue(), true))
                .setNegativeButton("Не выполнил", (dialog, which) -> movePlayerAfterChallenge(currentTeam, betPicker.getValue(), false))
                .setCancelable(false)
                .show();
    }

    private void movePlayerAfterChallenge(Team team, int steps, boolean success) {
        int startPos = team.getPosition();
        int endPos = success ? Math.min(startPos + steps, BOARD_SIZE) : Math.max(startPos - steps, 1);
        animateMove(team, startPos, endPos, success);
    }

    private void showWinDialog(Team winner) {
        isAnimating = true;
        isGameOver = true;
        rollDiceButton.setEnabled(false);
        SoundManager.playWinMusic(this);

        showConfetti(winner);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_win, null);
        TextView winMessage = dialogView.findViewById(R.id.win_message_text);
        winMessage.setText(getString(R.string.win_message, winner.getName()));

        new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton(R.string.play_again, (dialog, which) -> {
                    SoundManager.stopWinMusic();
                    Intent intent = new Intent(MainActivity.this, TeamEditorActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.exit_to_menu, (dialog, which) -> {
                    SoundManager.stopWinMusic();
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void showConfetti(Team winner) {
        EmitterConfig emitterConfig = new Emitter(5, TimeUnit.SECONDS).perSecond(100);
        Party party = new PartyFactory(emitterConfig)
                .angle(270)
                .spread(90)
                .setSpeedBetween(0f, 15f)
                .timeToLive(2000L)
                .shapes(new Shape.Rectangle(0.2f))
                .sizes(new Size(12, 5f, 0.2f))
                .position(0.5, 0.3, 1, 1)
                .colors(Arrays.asList(ContextCompat.getColor(this, winner.getColorResId()), 0xfffce18a, 0xffff726d, 0xffb48def))
                .build();
        konfettiView.start(party);
    }


    private void endTurn() {
        if (isGameOver) return;
        playerStatusAdapter.clearHighlight();
        currentPlayerIndex = (currentPlayerIndex + 1) % teamList.size();
        updateCurrentPlayerUI();
        isAnimating = false;
        rollDiceButton.setEnabled(true);
    }

    private void updateCurrentPlayerUI() {
        if (isGameOver) return;
        playerStatusAdapter.highlightPlayer(currentPlayerIndex);
    }
}

