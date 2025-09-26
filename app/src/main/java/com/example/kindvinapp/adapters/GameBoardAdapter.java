package com.example.kindvinapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kindvinapp.R;
import com.example.kindvinapp.models.GameCell;
import com.example.kindvinapp.models.Team;

import java.util.Arrays;
import java.util.List;

public class GameBoardAdapter extends RecyclerView.Adapter<GameBoardAdapter.CellViewHolder> {

    private final List<GameCell> cellList;
    private final Context context;

    public GameBoardAdapter(List<GameCell> cellList, Context context) {
        this.cellList = cellList;
        this.context = context;
    }

    @NonNull
    @Override
    public CellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_cell_item, parent, false);
        return new CellViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CellViewHolder holder, int position) {
        GameCell cell = cellList.get(position);
        holder.bind(cell, context);
    }

    @Override
    public int getItemCount() {
        return cellList.size();
    }

    public static class CellViewHolder extends RecyclerView.ViewHolder {
        TextView cellNumberTextView;
        CardView cellCardView;
        ImageView specialCellIcon;
        // --- ИСПРАВЛЕНИЕ: Получаем ссылки на все 4 слота ---
        List<ImageView> pawnSlots;

        public CellViewHolder(@NonNull View itemView) {
            super(itemView);
            cellNumberTextView = itemView.findViewById(R.id.cell_number_text);
            cellCardView = itemView.findViewById(R.id.cell_card);
            specialCellIcon = itemView.findViewById(R.id.special_cell_icon);
            // --- ИСПРАВЛЕНИЕ: Инициализируем список слотов ---
            pawnSlots = Arrays.asList(
                    itemView.findViewById(R.id.pawn_slot_1),
                    itemView.findViewById(R.id.pawn_slot_2),
                    itemView.findViewById(R.id.pawn_slot_3),
                    itemView.findViewById(R.id.pawn_slot_4)
            );
        }

        public void bind(GameCell cell, Context context) {
            cellNumberTextView.setText(String.valueOf(cell.getNumber()));
            boolean hasTeams = cell.getTeamsOnCell() != null && !cell.getTeamsOnCell().isEmpty();

            // --- Оптимизация: Прячем номер, если на ячейке есть фишки ИЛИ спец. иконка ---
            int specialIconRes = 0;
            switch (cell.getType()) {
                case ATTACK:
                    specialIconRes = R.drawable.ic_attack_sword;
                    break;
                case CHALLENGE:
                    specialIconRes = R.drawable.ic_challenge_star;
                    break;
                case TELEPORT_FORWARD_5:
                case TELEPORT_FORWARD_7:
                    specialIconRes = R.drawable.ic_teleport_forward;
                    break;
                case TELEPORT_BACKWARD_5:
                case TELEPORT_BACKWARD_7:
                    specialIconRes = R.drawable.ic_teleport_backward;
                    break;
                case EMPTY:
                default:
                    break;
            }

            if (specialIconRes != 0) {
                specialCellIcon.setImageResource(specialIconRes);
                specialCellIcon.setVisibility(View.VISIBLE);
                cellNumberTextView.setVisibility(View.INVISIBLE); // Прячем номер, если есть иконка
            } else {
                specialCellIcon.setVisibility(View.GONE);
                cellNumberTextView.setVisibility(hasTeams ? View.INVISIBLE : View.VISIBLE); // Прячем, только если есть фишки
            }


            for(ImageView slot : pawnSlots){
                slot.setVisibility(View.GONE);
            }

            if (hasTeams) {
                List<Team> teamsOnCell = cell.getTeamsOnCell();
                for (int i = 0; i < teamsOnCell.size() && i < pawnSlots.size(); i++) {
                    Team team = teamsOnCell.get(i);
                    ImageView currentSlot = pawnSlots.get(i);

                    currentSlot.setImageResource(team.getPawnIconResId());
                    currentSlot.setColorFilter(ContextCompat.getColor(context, team.getColorResId()));
                    currentSlot.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}

