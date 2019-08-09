package com.example.flashcard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;

import java.util.ArrayList;
import java.util.List;

public class UserProgressFragment extends Fragment {

    private String[] settings = {
            "#FF0000", "#FFFF00", "#00CC00", "#0099FF"
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_progress, null);

        //adding chart
        Pie pie = AnyChart.pie();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Thuộc lòng", 10000));
        data.add(new ValueDataEntry("Sơ sơ", 12000));
        data.add(new ValueDataEntry("Đã quên", 18000));
        data.add(new ValueDataEntry("Chưa thuộc", 18000));

        pie.data(data);

        pie.palette(settings);

        AnyChartView anyChartView = rootView.findViewById(R.id.any_chart_view);

        pie.title("Progression Report");

        pie.labels().position("outside");

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);

        return rootView;
    }

}
