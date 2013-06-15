package cheng.app.nga.loader;

import android.app.Activity;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;

import cheng.app.nga.R;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.content.NgaBoard;
import cheng.app.nga.content.NgaCategory;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomePageLoader extends CustomLoader<NgaCategory> {
    private static final String NGA_BOARDS = "nga-boards";
    private static final String NGA_CATEGORY = "nga-category";
    private static final String NGA_BOARD_ITEM = "nga-board-item";
    NGAApp mApp;

    public HomePageLoader(Activity context) {
        super(context);
        mApp = (NGAApp) context.getApplication();
    }

    @Override
    public List<NgaCategory> loadInBackground() {
        List<NgaCategory> mScetions = new ArrayList<NgaCategory>();
        loadBoardsFromResource(R.xml.nga_boards, mScetions);
        List<NgaBoard> boards = mApp.loadExtraBoards();
        if (mScetions != null && !mScetions.isEmpty() && boards != null && !boards.isEmpty()) {
            mScetions.get(mScetions.size() - 1).boards.addAll(boards);
        }
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mScetions;
    }

    private void loadBoardsFromResource(int resid, List<NgaCategory> target) {
        XmlResourceParser parser = null;
        try {
            parser = getContext().getResources().getXml(resid);
            AttributeSet attrs = Xml.asAttributeSet(parser);

            int type;
            while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                    && type != XmlPullParser.START_TAG) {
                // Parse next until start tag is found
            }

            String nodeName = parser.getName();
            if (!NGA_BOARDS.equals(nodeName)) {
                throw new RuntimeException("XML document must start with <" + NGA_BOARDS
                        + "> tag; found" + nodeName + " at " + parser.getPositionDescription());
            }

            final int outerDepth = parser.getDepth();
            while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                    && (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
                if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                    continue;
                }

                nodeName = parser.getName();
                if (NGA_CATEGORY.equals(nodeName)) {
                    NgaCategory cate = new NgaCategory();

                    TypedArray sa = getContext().getResources().obtainAttributes(attrs,
                            R.styleable.NgaBoard);
                    cate.title = sa.getString(R.styleable.NgaBoard_category_title);
                    sa.recycle();
                    final int innerDepth = parser.getDepth();
                    List<NgaBoard> boards = new ArrayList<NgaBoard>();
                    while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                            && (type != XmlPullParser.END_TAG || parser.getDepth() > innerDepth)) {
                        if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                            continue;
                        }

                        String innerNodeName = parser.getName();
                        if (innerNodeName.equals(NGA_BOARD_ITEM)) {
                            NgaBoard board = new NgaBoard();

                            sa = getContext().getResources().obtainAttributes(attrs,
                                    R.styleable.NgaBoard);
                            board.id = Integer.valueOf(sa
                                    .getString(R.styleable.NgaBoard_board_id));
                            board.icon = sa.getResourceId(R.styleable.NgaBoard_board_icon, 0);
                            board.title = sa.getString(R.styleable.NgaBoard_board_title);
                            board.summary = sa.getString(R.styleable.NgaBoard_board_summary);
                            sa.recycle();
                            boards.add(board);
                        } else {
                            skipCurrentTag(parser);
                        }
                    }
                    cate.boards = boards;
                    target.add(cate);
                } else {
                    skipCurrentTag(parser);
                }
            }

        } catch (XmlPullParserException e) {
            throw new RuntimeException("Error parsing", e);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing", e);
        } finally {
            if (parser != null)
                parser.close();
        }

    }

    private static void skipCurrentTag(XmlPullParser parser) throws XmlPullParserException,
            IOException {
        int outerDepth = parser.getDepth();
        int type;
        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                && (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
        }
    }

}

