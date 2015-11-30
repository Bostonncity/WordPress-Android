package org.wordpress.android.ui.prefs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.wordpress.android.R;

/**
 * ListView that supports multiple item selection and provides a delete button.
 */
public class MultiSelectListView extends ListView
        implements AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener,
        ActionMode.Callback {

    public interface OnEnterMultiSelect {
        void onEnterMultiSelect();
    }

    public interface OnExitMultiSelect {
        void onExitMultiSelect();
    }

    public interface OnDeleteRequested {
        /**
         * @return
         * true to exit Action Mode
         */
        boolean onDeleteRequested();
    }

    private OnEnterMultiSelect mEnterListener;
    private OnExitMultiSelect mExitListener;
    private OnDeleteRequested mDeleteListener;
    private ActionMode mActionMode;

    public MultiSelectListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnItemClickListener(this);
        setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mActionMode == null) return;

        if (getCheckedItemCount() <= 0) {
            mActionMode.finish();
        } else {
            mActionMode.invalidate();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mActionMode != null) return false;
        if (mEnterListener != null) mEnterListener.onEnterMultiSelect();

        setItemChecked(position, true);
        mActionMode = startActionMode(this);

        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.list_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        mode.setTitle(String.valueOf(getCheckedItemCount()));
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            if (mDeleteListener == null || mDeleteListener.onDeleteRequested()) {
                mActionMode.finish();
            }
            return true;
        }

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mActionMode = null;
        if (mExitListener != null) mExitListener.onExitMultiSelect();
    }

    public void setEnterMultiSelectListener(OnEnterMultiSelect listener) {
        mEnterListener = listener;
    }

    public void setExitMultiSelectListener(OnExitMultiSelect listener) {
        mExitListener = listener;
    }

    public void setDeleteRequestListener(OnDeleteRequested listener) {
        mDeleteListener = listener;
    }
}