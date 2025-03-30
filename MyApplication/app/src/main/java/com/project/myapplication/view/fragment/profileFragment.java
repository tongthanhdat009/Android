package com.project.myapplication.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.project.myapplication.DTO.Followers;
import com.project.myapplication.DTO.Following;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.DTO.User;
import com.project.myapplication.MainActivity;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.model.UserModel;
import com.project.myapplication.view.activity.FullscreenImageActivity;
import com.project.myapplication.view.activity.accountCenterActivity;
import com.project.myapplication.view.activity.userShowFollowActivity;
import com.project.myapplication.view.adapter.userPostShowAdapter;
import com.project.myapplication.view.components.GridSpacingItemDecoration;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class profileFragment extends Fragment {
    public String userID;
    private static final int PICK_IMAGE_REQUEST = 1;
    private final PostModel postModel = new PostModel();
    private final UserModel userModel = new UserModel();
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.userID = getArguments().getString("userID");
            assert userID != null;
            Log.d("Profile",userID);
        }
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        // Xử lý Uri của ảnh đã chọn
                        postModel.getUserInfor(userID, new PostModel.OnUserRetrievedCallback() {
                            @Override
                            public void onUserRetrievedCallback(User user) {
                                userModel.uploadAvatar(selectedImageUri, userID, new UserModel.OnUpdateAvatarCallback() {
                                    @Override
                                    public void updateAvatar(String avatar, String noti) {
                                        if (noti.equals("Thay đổi avatar thành công")) {
                                            Toast.makeText(getContext(), noti, Toast.LENGTH_SHORT).show();
                                            assert selectedImageUri != null;
                                            user.setAvatar(avatar);
                                            userModel.userUpdate(user);
                                        } else {
                                            Toast.makeText(getContext(), noti, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
        );
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        // Thiết lập hành động khi kéo để làm mới
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadInfor(view, swipeRefreshLayout);
        });
        loadInfor(view, swipeRefreshLayout);
        return view;

    }

    public void loadInfor(View view, SwipeRefreshLayout swipeRefreshLayout){
        ImageView avatar = view.findViewById(R.id.avatar);
        TextView profileTitle = view.findViewById(R.id.profile_title);
        TextView biography = view.findViewById(R.id.biography);
        LinearLayout totalFollowerContainer = view.findViewById(R.id.total_followers_container);
        LinearLayout totalFollowingContainer = view.findViewById(R.id.total_following_container);
        ImageButton menu = view.findViewById(R.id.menu);

        // Ví dụ giả lập tải dữ liệu từ một nguồn
        new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
        }, 2000);  // Giả lập thời gian tải 2 giây

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), menu);
                popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.logout){
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(view.getContext(),"Đăng xuất",Toast.LENGTH_SHORT).show();
                            postModel.getUserInfor(userID, new PostModel.OnUserRetrievedCallback() {
                                @Override
                                public void onUserRetrievedCallback(User user) {
                                    user.setLogged("");
                                    userModel.userUpdate(user);
                                    requireActivity().finish();
                                    Intent intent = new Intent(requireContext(), MainActivity.class);
                                    requireContext().startActivity(intent);
                                }
                            });
                            return true;
                        }
                        else if(menuItem.getItemId() == R.id.account_management){
                            Intent intent = new Intent(view.getContext(), accountCenterActivity.class);
                            intent.putExtra("userID",userID);
                            startActivity(intent);
                        }
                        else if(menuItem.getItemId() == R.id.edit_avatar){
                            Toast.makeText(view.getContext(),"Chọn sửa ảnh đại diện",Toast.LENGTH_SHORT).show();
                            openImagePicker();
                            return true;
                        }
                        else if(menuItem.getItemId() == R.id.edit_biography){
                            Toast.makeText(view.getContext(),"Chọn sửa tiểu sử",Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Sửa tiểu sử:");

                            // Tạo EditText để nhập thông tin
                            final EditText input = new EditText(getContext());
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            builder.setView(input);

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String newBiography = input.getText().toString();
                                    // Xử lý thông tin nhập vào
                                    if(newBiography.isEmpty()){
                                      Toast.makeText(getContext(), "Vui lòng nhập tiểu sử để sửa!", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
//                                Toast.makeText(context, "Caption đã nhập: " + newCaption, Toast.LENGTH_SHORT).show();
                                        postModel.getUserInfor(userID, new PostModel.OnUserRetrievedCallback() {
                                            @Override
                                            public void onUserRetrievedCallback(User user) {
                                                user.setBiography(newBiography);
                                                userModel.userUpdate(user);
                                                Toast.makeText(getContext(), " Sửa tiểu sử thành công! ", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }
                            });

                            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();
                            return true;
                        }
                        return false;
                    }
                });
                try {
                    @SuppressLint("DiscouragedPrivateApi") Field popup = PopupMenu.class.getDeclaredField("mPopup");
                    popup.setAccessible(true);
                    Object menuPopupHelper = popup.get(popupMenu);
                    assert menuPopupHelper != null;
                    menuPopupHelper.getClass()
                            .getDeclaredMethod("setForceShowIcon", boolean.class)
                            .invoke(menuPopupHelper, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                popupMenu.show();
            }
        });

        postModel.getUserInfor(userID, new PostModel.OnUserRetrievedCallback() {
            @Override
            public void onUserRetrievedCallback(User user) {
                Picasso.get().load(user.getAvatar()).into(avatar);
                profileTitle.setText(user.getName());
                biography.setText(user.getBiography());
                biography.setEllipsize(TextUtils.TruncateAt.END);

                biography.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(biography.getEllipsize() != null) {
                            biography.setEllipsize(null);
                            biography.setMaxLines(Integer.MAX_VALUE);
                            biography.setText(user.getBiography());
                        } else {
                            biography.setEllipsize(TextUtils.TruncateAt.END);
                            biography.setMaxLines(2);
                        }
                        biography.requestLayout();
                    }
                });

                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), FullscreenImageActivity.class);
                        intent.putExtra("imageUri", Uri.parse(user.getAvatar()));
                        startActivity(intent);
                    }
                });
            }
        });

        postModel.getUserPost(userID, new PostModel.OnUserPostListRetrievedCallback(){
            final TextView totalPosts = view.findViewById(R.id.total_posts);
            @Override
            public void getUserPost(ArrayList<Post> postsList) {
                totalPosts.setText(String.valueOf(postsList.size()));
            }
        });

        postModel.getAllFollower(userID, new PostModel.OnFollowerListRetrievedCallback() {
            final TextView totalFollowers = view.findViewById(R.id.total_followers);
            @Override
            public void getAllFollower(ArrayList<Followers> followerList) {
                totalFollowers.setText(String.valueOf(followerList.size()));
            }
        });

        postModel.getAllFollowing(userID, new PostModel.OnFollowingListRetrievedCallback() {
            final TextView totalFollowing = view.findViewById(R.id.total_following);
            @Override
            public void getAllFollowing(ArrayList<Following> followingList) {
                totalFollowing.setText(String.valueOf(followingList.size()));
            }
        });


        postModel.getUserPost(userID, new PostModel.OnUserPostListRetrievedCallback() {
            @Override
            public void getUserPost(ArrayList<Post> postsList) {
                RecyclerView recyclerView = view.findViewById(R.id.post_show_recycler_view);
                int spanCount = calculateOptimalSpanCount();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(),spanCount);
                recyclerView.setLayoutManager(gridLayoutManager);
                int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
                recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));

                userPostShowAdapter postShowAdapter = new userPostShowAdapter(view.getContext(), postsList, postModel, userID);

                recyclerView.setAdapter(postShowAdapter);
            }
        });

        totalFollowerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), userShowFollowActivity.class);
                intent.putExtra("CurrentUser", userID);
                startActivity(intent);
            }
        });

        totalFollowingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), userShowFollowActivity.class);
                intent.putExtra("CurrentUser", userID);
                startActivity(intent);
            }
        });
    }
    private int calculateOptimalSpanCount() {
        // Lấy kích thước màn hình
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;

        // Kích thước tối thiểu cho mỗi item (dp)
        float minItemWidthDp = getResources().getDimension(R.dimen.grid_item_min_size) / displayMetrics.density;

        // Tính toán số cột tối ưu

        return Math.max(3, (int) (screenWidthDp / minItemWidthDp));
    }

    public void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Chọn ảnh đại diện"));
    }
}

