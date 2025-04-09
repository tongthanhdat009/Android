package com.project.myapplication.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class CommentBottomSheetFragment extends BottomSheetDialogFragment {

    private String shortID;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private FirebaseFirestore db;

    public static CommentBottomSheetFragment newInstance(String shortID) {
        CommentBottomSheetFragment fragment = new CommentBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("shortID", shortID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shortID = getArguments().getString("shortID");
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

        RecyclerView recyclerView = view.findViewById(R.id.commentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        commentAdapter = new CommentAdapter(commentList);
        recyclerView.setAdapter(commentAdapter);

        ImageView closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dismiss());

        loadCommentsFromFirestore();

        return view;
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
                        String username = doc.getString("userID");
                        String content = doc.getString("content");
                        commentList.add(new Comment(username, content));
                    }
                    commentAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("CommentBottomSheet", "Error loading comments", e));
    }

    static class Comment {
        String username;
        String content;

        public Comment(String username, String content) {
            this.username = username;
            this.content = content;
        }
    }

    static class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

        private final List<Comment> comments;

        public CommentAdapter(List<Comment> comments) {
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
            Comment comment = comments.get(position);
            holder.bind(comment);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        static class CommentViewHolder extends RecyclerView.ViewHolder {

            TextView userNameTextView, commentTextView;

            public CommentViewHolder(@NonNull View itemView) {
                super(itemView);
                userNameTextView = itemView.findViewById(R.id.userNameTextView);
                commentTextView = itemView.findViewById(R.id.commentTextView);
            }

            public void bind(Comment comment) {
                userNameTextView.setText(comment.username);
                commentTextView.setText(comment.content);
            }
        }
    }
}
