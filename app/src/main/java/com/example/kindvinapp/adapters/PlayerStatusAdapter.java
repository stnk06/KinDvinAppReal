package com.example.kindvinapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kindvinapp.R;
import com.example.kindvinapp.models.Team;

import java.util.List;

public class PlayerStatusAdapter extends RecyclerView.Adapter<PlayerStatusAdapter.PlayerViewHolder> {

    private final List<Team> teams;
    private final Context context;
    private int highlightedPosition = -1;

    public PlayerStatusAdapter(List<Team> teams, Context context) {
        this.teams = teams;
        this.context = context;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_status_item, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Team team = teams.get(position);
        holder.teamName.setText(team.getName());
        holder.teamPosition.setText(context.getString(R.string.player_position_text, team.getPosition()));
        holder.pawnIcon.setImageResource(team.getPawnIconResId());
        holder.pawnIcon.setColorFilter(ContextCompat.getColor(context, team.getColorResId()));

        if (position == highlightedPosition) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            holder.cardView.setAlpha(1.0f);
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            holder.cardView.setAlpha(0.6f);
        }
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    public void highlightPlayer(int position) {
        int previouslyHighlighted = highlightedPosition;
        highlightedPosition = position;
        if (previouslyHighlighted != -1) {
            notifyItemChanged(previouslyHighlighted);
        }
        notifyItemChanged(highlightedPosition);
    }

    public void clearHighlight() {
        int previouslyHighlighted = highlightedPosition;
        highlightedPosition = -1;
        if (previouslyHighlighted != -1) {
            notifyItemChanged(previouslyHighlighted);
        }
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView teamName;
        TextView teamPosition;
        ImageView pawnIcon;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.player_card);
            teamName = itemView.findViewById(R.id.player_name);
            teamPosition = itemView.findViewById(R.id.player_position);
            pawnIcon = itemView.findViewById(R.id.player_pawn_icon);
        }
    }
}

