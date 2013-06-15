package cheng.app.nga.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import cheng.app.nga.R;
import cheng.app.nga.activity.MainActivity;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class AddBoardFrament extends SherlockDialogFragment implements OnClickListener{
    static final String TAG = "AddBoardFrament";
    EditText mBoardId;
    EditText mBoardTitle;
    EditText mBoardSummary;

    public static AddBoardFrament newInstance() {
        AddBoardFrament frag = new AddBoardFrament();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String boardId = mBoardId.getText().toString();
        String boardTitle = mBoardTitle.getText().toString();
        String boardSummary = mBoardSummary.getText().toString();
        if (!TextUtils.isEmpty(boardId) && !TextUtils.isEmpty(boardTitle)) {
            MainActivity activity = (MainActivity) getActivity();
            int id = -7;
            try {
                id = Integer.parseInt(boardId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            activity.addBoard(id, boardTitle, boardSummary);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context dialogContext = new ContextThemeWrapper(
                getActivity(), R.style.AppDarkTheme);
        LayoutInflater inflater = LayoutInflater.from(dialogContext);
        View view = inflater.inflate(R.layout.add_board_layout, null);
        mBoardId = (EditText) view.findViewById(R.id.board_id);
        mBoardTitle = (EditText) view.findViewById(R.id.board_title);
        mBoardSummary = (EditText) view.findViewById(R.id.board_summary);
        return new AlertDialog.Builder(dialogContext)
        .setTitle(R.string.menu_add_board)
        .setView(view)
        .setPositiveButton(android.R.string.ok, this)
        .setNegativeButton(android.R.string.cancel, null)
        .create();
    }
}
