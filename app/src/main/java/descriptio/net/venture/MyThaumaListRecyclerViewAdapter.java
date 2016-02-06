package descriptio.net.venture;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import descriptio.net.venture.models.Astu;
import descriptio.net.venture.models.Thauma;

/**
 * {@link RecyclerView.Adapter} that can display an item and makes a call to the
 * specified {OnThaumaFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyThaumaListRecyclerViewAdapter extends RecyclerView.Adapter<MyThaumaListRecyclerViewAdapter.ViewHolder> {

    private final Astu mAstu;
    private final ThaumaListFragment.OnThaumaFragmentInteractionListener mListener;

    public MyThaumaListRecyclerViewAdapter(Astu astu, ThaumaListFragment.OnThaumaFragmentInteractionListener listener) {
        mAstu = astu;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_thaumalist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mAstu.getThaumata().get(position);
        holder.mIdView.setText(holder.mItem.getName());
        holder.mContentView.setText(holder.mItem.getDescription());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onThaumaFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mAstu != null) {
            if (mAstu.getThaumata() != null) {
                return mAstu.getThaumata().size();
            }
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Thauma mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.thauma_name);
            mContentView = (TextView) view.findViewById(R.id.thauma_content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
