package com.project.myapplication.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.myapplication.DTO.ShortComment;
import com.project.myapplication.R;
import com.project.myapplication.model.ShortModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentBottomSheetFragment extends BottomSheetDialogFragment {

    private String userID;
    private String shortID;
    private CommentAdapter commentAdapter;
    private List<ShortComment> commentList = new ArrayList<>();
    private FirebaseFirestore db;
    private ShortModel shortModel=new ShortModel();

    public static CommentBottomSheetFragment newInstance(String shortID, String userID) {
        CommentBottomSheetFragment fragment = new CommentBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("shortID", shortID);
        args.putString("userID", userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shortID = getArguments().getString("shortID");
            userID = getArguments().getString("userID");
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null && getDialog() != null) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = (int) (requireContext().getResources().getDisplayMetrics().heightPixels * 0.65);
            view.setLayoutParams(layoutParams);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.setBackgroundResource(android.R.color.transparent);
            }
        });

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_comment, container, false);
        EditText commentEditText = view.findViewById(R.id.commentInput);
        ImageView sendCommentButton = view.findViewById(R.id.sendCommentBtn);
        TextView commentCountTextView = view.findViewById(R.id.commentCountTextView);

        RecyclerView recyclerView = view.findViewById(R.id.commentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        commentAdapter = new CommentAdapter(commentList);
        recyclerView.setAdapter(commentAdapter);

        shortModel.getAllCommentsForShort(shortID, comments -> {
            int commentCount = comments.size();

            if (commentCountTextView != null) {
                commentCountTextView.setText(formatCount(commentCount));
            }

            commentList.clear();
            commentList.addAll(comments);
            commentAdapter.notifyDataSetChanged();
        });

        sendCommentButton.setOnClickListener(v -> {
            String content = commentEditText.getText().toString().trim();
            if (!content.isEmpty()) {
                ShortComment comment = new ShortComment(userID, content, new Date());
                shortModel.addCommentToShort(shortID, comment);
                commentEditText.setText("");
            }
        });

        ImageView closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }

    public static String formatCount(int count) {
        if (count >= 1_000_000) {
            return String.format("%.1fM bình luận", count / 1_000_000.0);
        } else if (count >= 1_000) {
            return String.format("%.1fk bình luận", count / 1_000.0);
        } else {
            return count + " bình luận";
        }
    }

    private void loadCommentsFromFirestore() {
        if (shortID == null) return;

        db.collection("short")
                .document(shortID)
                .collection("comment")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    commentList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String userID = doc.getString("userID");
                        String content = doc.getString("content");
                        Timestamp timestamp = doc.getTimestamp("timestamp");
                        Date date = timestamp != null ? timestamp.toDate() : null;

                        commentList.add(new ShortComment(userID, content, date));
                    }
                    commentAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("CommentBottomSheet", "Error loading comments", e));
    }

    static class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

        private final List<ShortComment> comments;

        public CommentAdapter(List<ShortComment> comments) {
            this.comments = comments;
        }

        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
            ShortComment comment = comments.get(position);
            holder.bind(comment);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        static class CommentViewHolder extends RecyclerView.ViewHolder {

            ShortModel shortModel= new ShortModel();
            TextView userNameTextView, commentTextView;
            ImageView userAvatar;

            public CommentViewHolder(@NonNull View itemView) {
                super(itemView);
                userAvatar = itemView.findViewById(R.id.avatarImageView);
                userNameTextView = itemView.findViewById(R.id.userNameTextView);
                commentTextView = itemView.findViewById(R.id.commentTextView);
            }

            public void bind(ShortComment comment) {
                shortModel.getUserInfo(comment.getUserID(), user -> {
                    userNameTextView.setText(user.getName());
                    Glide.with(itemView.getContext())
                            .load(user.getAvatar())
                            .placeholder(R.drawable.unknow_avatar)
                            .circleCrop()
                            .into(userAvatar);
                });
                commentTextView.setText(comment.getContent());
            }
        }
    }
}
