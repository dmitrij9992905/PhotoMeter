package com.pmeter.dmitrij999.photometer;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.GraphViewXML;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class Graph_Frag extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_graph, null);

        // Inflate the layout for this fragment
        GraphView graphview = (GraphView) v.findViewById(R.id.graphics);
        graphview.getViewport().setScalable(true);
        graphview.getViewport().setScrollable(true);
        graphview.getViewport().setYAxisBoundsManual(true);
        graphview.getViewport().setMinY(0.0);
        graphview.getViewport().setMaxY(1024.0);
        //int meas[] = new int[16];
        //meas[0] = Screen.getMeasure(0);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, Integer.parseInt(Screen.getMeasure(0))),
                new DataPoint(1, Integer.parseInt(Screen.getMeasure(1))),
                new DataPoint(2, Integer.parseInt(Screen.getMeasure(2))),
                new DataPoint(3, Integer.parseInt(Screen.getMeasure(3))),
                new DataPoint(4, Integer.parseInt(Screen.getMeasure(4))),
                new DataPoint(5, Integer.parseInt(Screen.getMeasure(5))),
                new DataPoint(6, Integer.parseInt(Screen.getMeasure(6))),
                new DataPoint(7, Integer.parseInt(Screen.getMeasure(7))),
                new DataPoint(8, Integer.parseInt(Screen.getMeasure(8))),
                new DataPoint(9, Integer.parseInt(Screen.getMeasure(9))),
                new DataPoint(10, Integer.parseInt(Screen.getMeasure(10))),
                new DataPoint(11, Integer.parseInt(Screen.getMeasure(11))),
                new DataPoint(12, Integer.parseInt(Screen.getMeasure(12))),
                new DataPoint(13, Integer.parseInt(Screen.getMeasure(13))),
                new DataPoint(14, Integer.parseInt(Screen.getMeasure(14))),
                new DataPoint(15, Integer.parseInt(Screen.getMeasure(15)))
        });
        graphview.addSeries(series);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //GraphView graphview = (GraphView) findViewById(R.id.graphics);
        Log.d("Graphs", "Fragment1 onAttach");
    }

}
