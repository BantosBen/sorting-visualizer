package com.sanj.sortingvisualizer.algorithm;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.slider.Slider;
import com.sanj.sortingvisualizer.R;
import com.sanj.sortingvisualizer.model.BarchartModel;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class SelectionSort {

    private final BarChart barChart;
    private final ArrayList<BarEntry> barEntryArrayList;
    private final ArrayList<String> label_names;
    private final ArrayList<BarchartModel> barChartModelArrayList;
    private final Slider slider;
    private final Context mContext;
    private final Handler selectionHandler;

    Runnable selectionRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                initializeThread();
                ArrayList<Integer> data = converterToIntegers();
                int n = data.size();
                for (int i = 0; i < n - 1; i++) {
                    int min_index = i;
                    for (int j = i + 1; j < n; j++) {
                        if (data.get(j) < data.get(min_index)) {
                            min_index = j;
                        }
                    }
                    int temp = data.get(min_index);
                    data.set(min_index, data.get(i));
                    data.set(i, temp);
                    selectionHandler.post(() -> converterToModel(data));
                    sleep(ThreadState.delayTime);
                }
            } catch (InterruptedException e) {
                ThreadState.threadAlive = false;
                selectionHandler.post(() -> Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                finalizeThread();
            }
        }
    };

    public SelectionSort(BarChart barChart, ArrayList<BarEntry> barEntryArrayList, ArrayList<String> label_names, ArrayList<BarchartModel> barChartModelArrayList, Slider slider, Context mContext) {
        this.barChart = barChart;
        this.barEntryArrayList = barEntryArrayList;
        this.label_names = label_names;
        this.barChartModelArrayList = barChartModelArrayList;
        this.slider = slider;
        this.mContext = mContext;
        selectionHandler = new Handler();
        new Thread(selectionRunnable).start();
    }

    private ArrayList<Integer> converterToIntegers() {
        ArrayList<Integer> data = new ArrayList<>();
        int k = barChartModelArrayList.size();
        for (int m = 0; m < k; m++) {
            data.add(barChartModelArrayList.get(m).getValue());
        }
        return data;
    }

    private void displayGraph() {
        barEntryArrayList.clear();
        label_names.clear();

        for (int i = 0; i < barChartModelArrayList.size(); i++) {
            int val = barChartModelArrayList.get(i).getValue();
            barEntryArrayList.add(new BarEntry(i, val));
            label_names.add(" ");
        }

        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, " ");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        Description description = new Description();
        description.setText(" ");
        barChart.setDescription(description);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(label_names));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(label_names.size());
        barChart.invalidate();
    }

    private void converterToModel(ArrayList<Integer> data) {
        int l = data.size();
        barChartModelArrayList.clear();
        for (int p = 0; p < l; p++) {
            int randomValue = data.get(p);
            barChartModelArrayList.add(new BarchartModel(String.valueOf(p), randomValue));
        }
        displayGraph();
    }

    private void finalizeThread() {
        selectionHandler.post(() -> {
            ThreadState.threadAlive = false;
            displayGraph();
            slider.setEnabled(true);
            barChart.setEnabled(true);
            Toast.makeText(mContext,"Sorting Process Completed",Toast.LENGTH_LONG).show();
//        view_algorithm.setVisibility(View.VISIBLE);
//        sliderSpeed.setVisibility(View.GONE);
        });
    }
    private void initializeThread() {
        selectionHandler.post(() -> {
            ThreadState.threadAlive = true;
            Toast.makeText(mContext, "Sorting Process Initiated", Toast.LENGTH_LONG).show();
//        view_algorithm.setVisibility(View.VISIBLE);
//        sliderSpeed.setVisibility(View.GONE);
        });
    }
}
