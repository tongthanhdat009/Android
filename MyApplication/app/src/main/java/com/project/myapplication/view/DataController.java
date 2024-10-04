package com.project.myapplication.view;

import com.project.myapplication.model.DataModel;
import com.project.myapplication.view.DataView;

import java.util.List;

public class DataController {
    private DataModel model;
    private DataView view;

    public DataController(DataModel model, DataView view) {
        this.model = model;
        this.view = view;
    }

    // Lấy dữ liệu từ Model và cập nhật View
    public void loadData() {
        model.getData(new DataModel.OnDataFetchedListener() {
            @Override
            public void onDataFetched(List<String> data) {
                view.displayData(data);
            }

            @Override
            public void onError(Exception e) {
                view.showError(e.getMessage());
            }
        });
    }
}