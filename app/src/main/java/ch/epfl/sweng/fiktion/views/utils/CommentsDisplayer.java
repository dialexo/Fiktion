package ch.epfl.sweng.fiktion.views.utils;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.Comment;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;

/**
 * Created by dialexo on 06.12.17.
 * A utility class to display reviews
 */

public class CommentsDisplayer {

    /**
     * A class describing a loadable linear layout of comments
     */
    public static class LoadableList {
        // comments data
        private List<Comment> data;
        // parent linear layout in which to display comments
        private LinearLayout display;
        // number of comments to load at a time
        private int chunkSize;
        // max current number of comments
        private int max;
        // number of comments already shown
        private int shown;
        // empty text view
        private TextView empty;
        // button to load more
        private Button loadMore;
        // activity context of creation
        private Context ctx;

        /**
         * LoadableList constructor
         *
         * @param ll        a linear layout
         * @param chunkSize the number of elements to laod at a time
         */
        public LoadableList(LinearLayout ll, int chunkSize, Context ctx) {
            this.display = ll;
            this.data = new ArrayList<>();
            this.chunkSize = chunkSize;
            this.max = chunkSize;
            this.shown = 0;
            this.ctx = ctx;
            this.empty = new TextView(ctx);
            this.empty.setText(R.string.no_reviews_yet);
            this.empty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            this.empty.setPadding(0, 20, 0, 10);
            this.display.addView(this.empty);
            this.loadMore = new Button(ctx);
            this.loadMore.setText(R.string.load_more);
            this.loadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadMore();
                }
            });
        }

        /**
         * Adds a comment to the LoadableList, may display if there is room left
         *
         * @param c
         */
        public void add(Comment c) {
            this.data.add(c);
            this.show();
        }

        /**
         * Load next comments and show them
         */
        public void loadMore() {
            this.max += this.chunkSize;
            this.show();
        }

        /**
         * displays the correct number of elements
         */
        private void show() {
            // use the collection iterator to know where to start
            Iterator<Comment> it = data.iterator();
            // skip already shown comments
            for (int i = 0; i < shown; i++) it.next();
            // display the remaining comments until max
            while (it.hasNext() && shown < max) {
                Comment next = it.next();
                display.addView(CommentsDisplayer.createCommentCard(next, ctx));
                shown++;
            }
            // hide empty text
            this.empty.setVisibility(View.GONE);
            // show load more button if needed
            if (data.size() > shown) {
                display.removeView(loadMore);
                display.addView(loadMore);
            } else {
                display.removeView(loadMore);
            }
        }
    }

    /**
     * Creates a comment card view from a comment
     *
     * @param c a comment to display
     * @return a comment card view to be added to a parent view
     */
    public static View createCommentCard(Comment c, Context ctx) {
        // create new view for the comment
        LinearLayout v = new LinearLayout(ctx);
        v.setOrientation(LinearLayout.VERTICAL);

        /* styles */

        // background color
        v.setBackgroundColor(ctx.getResources().getColor(R.color.white));
        // margin
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(2, 10, 2, 10);
        v.setLayoutParams(params);
        // padding
        v.setPadding(20, 20, 20, 20);
        // shadow
        v.setElevation(2);

        // author text
        final TextView author = new TextView(ctx);
        author.setTextSize(14);
        author.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
        DatabaseProvider.getInstance().getUserById(c.getAuthorId(), new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                author.setText(user.getName());
            }

            @Override
            public void onDoesntExist() { /* nothing */ }

            @Override
            public void onFailure() { /* nothing */ }
        });
        v.addView(author);

        // comment text
        TextView comment = new TextView(ctx);
        comment.setTextSize(16);
        comment.setText(c.getText());
        v.addView(comment);

        // return the whole view
        return v;
    }
}
