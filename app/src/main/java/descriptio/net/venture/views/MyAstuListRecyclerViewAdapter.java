package descriptio.net.venture.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import descriptio.net.venture.R;
import descriptio.net.venture.models.Astu;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a list of {@link Astu} and makes a call to the
 * specified OnListFragmentInteractionListener.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAstuListRecyclerViewAdapter extends RecyclerView.Adapter<MyAstuListRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private final List<Astu> astea;
    private final AstuListFragment.OnListFragmentInteractionListener mListener;

    public MyAstuListRecyclerViewAdapter(Context context, List<Astu> items, AstuListFragment.OnListFragmentInteractionListener listener) {
        this.context = context;
        this.astea = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_astulist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = astea.get(position);
        holder.mCityNameView.setText(astea.get(position).getName());
        holder.mCitySummaryView.setText(astea.get(position).getOverview());
        Picasso.with(context)
                .load(astea.get(position)
                .getImageUrl())
                .fit()
                .centerCrop()
                .placeholder(R.color.colorAccent)
                .into(holder.mImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListItemClicked(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return astea.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final CardView mCardView;
        public final ImageView mImageView;
        public final TextView mCityNameView;
        public final TextView mCitySummaryView;
        public Astu mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCardView = (CardView) view.findViewById(R.id.astu_card);
            mImageView = (ImageView) view.findViewById(R.id.astu_photo);
            mCityNameView = (TextView) view.findViewById(R.id.city_name);
            mCitySummaryView = (TextView) view.findViewById(R.id.city_summary);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCityNameView.getText() + "'";
        }
    }
}
