package it.polito.mad1819.group17.deliveryapp.restaurateur.reviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.mad1819.group17.deliveryapp.restaurateur.R;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {

    private ArrayList<Review> reviews;
    private Context context;
    private RecyclerView recyclerView;
    private int animationFlag = 0;

    public ReviewsAdapter(ArrayList<Review> reviews, Context context, RecyclerView recyclerView) {
        this.reviews = new ArrayList<Review>(reviews);
        this.context = context;
        this.recyclerView = recyclerView;
    }

    /* ------------------------------------------------------------------------------------------------------------- */
    public class ReviewHolder extends RecyclerView.ViewHolder {
        RatingBar rb_reviews_restaurant;
        RatingBar rb_reviews_service;
        TextView tv_reviews_comment;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            rb_reviews_restaurant = itemView.findViewById(R.id.rb_reviews_restaurant);
            rb_reviews_service = itemView.findViewById(R.id.rb_reviews_service);
            tv_reviews_comment = itemView.findViewById(R.id.tv_reviews_comment);
        }
    }
    /* ------------------------------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_review, viewGroup, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder reviewHolder, int i) {
        Review review = reviews.get(i);
        if (review.getRestaurant_rate() != null)
            reviewHolder.rb_reviews_restaurant.setRating(review.getRestaurant_rate());
        if (review.getService_rate() != null)
            reviewHolder.rb_reviews_service.setRating(review.getService_rate());
        if (!TextUtils.isEmpty(review.getComment()))
            reviewHolder.tv_reviews_comment.setText(review.getComment());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    private void runLayoutAnimation(final RecyclerView recyclerView, int type) {
        final Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;

        if (type == 0)
            controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
        animationFlag = 1;
    }
}

