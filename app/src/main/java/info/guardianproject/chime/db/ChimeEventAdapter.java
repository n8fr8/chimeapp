package info.guardianproject.chime.db;


import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;
import java.util.Random;

import info.guardianproject.chime.model.Chime;
import info.guardianproject.chime.model.ChimeEvent;
import info.guardianproject.chime.R;

public class ChimeEventAdapter extends RecyclerView.Adapter<ChimeEventAdapter.MyViewHolder> {

    private Context mContext;
    private List<ChimeEvent> chimeList;
    private int chimeLayout;
    private PrettyTime pTime = new PrettyTime();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description, when;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.card_title);
            description = (TextView) view.findViewById(R.id.card_description);
            when = (TextView) view.findViewById(R.id.card_when);

        }
    }


    public ChimeEventAdapter(Context mContext, List<ChimeEvent> chimeList, int chimeLayout) {
        this.mContext = mContext;
        this.chimeList = chimeList;
        this.chimeLayout = chimeLayout;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(chimeLayout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        ChimeEvent chimeEvent = chimeList.get(position);
        Chime chime = Chime.findById(Chime.class,Long.parseLong(chimeEvent.chimeId));

        if (chimeEvent.type == ChimeEvent.TYPE_HEARD_KNOWN_CHIME)
        {
            holder.title.setText(R.string.action_heard_known_chime);
        }
        else if (chimeEvent.type == ChimeEvent.TYPE_FOUND_NEW_CHIME)
        {
            holder.title.setText(R.string.action_heard_new_chime);
        }
        else if (chimeEvent.type == ChimeEvent.TYPE_ADDED_CHIME)
        {
            holder.title.setText(R.string.action_added_chime);
        }


        holder.description.setText(chime.name);
        holder.when.setText(pTime.format(chimeEvent.happened));


    }

    @Override
    public int getItemCount() {
        return chimeList.size();
    }
}