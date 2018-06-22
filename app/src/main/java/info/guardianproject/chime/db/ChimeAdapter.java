package info.guardianproject.chime.db;


import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import info.guardianproject.chime.model.Chime;
import info.guardianproject.chime.R;

public class ChimeAdapter extends RecyclerView.Adapter<ChimeAdapter.MyViewHolder> {

    private Context mContext;
    private List<Chime> chimeList;
    private int chimeLayout;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.card_title);
        }
    }


    public ChimeAdapter(Context mContext, List<Chime> chimeList, int chimeLayout) {
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
        Chime chime = chimeList.get(position);
        holder.title.setText(chime.name);

        if (chimeLayout == R.layout.layout_card_mixed) {
            // Set a random height for TextView
            holder.title.getLayoutParams().height = getRandomIntInRange(300, 100);
        }

        //  holder.count.setText(album.getNumOfSongs() + " songs");

        // loading album cover using Glide library
      //  Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

        /**
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });**/
    }

    private Random mRandom = new Random();

    // Custom method to get a random number between a range
    protected int getRandomIntInRange(int max, int min){
        return mRandom.nextInt((max-min)+min)+min;
    }


    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        /**
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
         **/
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            /**
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }**/
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return chimeList.size();
    }
}